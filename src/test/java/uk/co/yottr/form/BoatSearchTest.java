package uk.co.yottr.form;

import org.junit.jupiter.api.Test;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.SailingRegion;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoatSearchTest {

    @Test
    public void anEmptySearchHasNoQuery() {
        assertEquals("", new BoatSearch().toQueryString());
    }

    @Test
    public void blankTextIsLeftOut() {
        final BoatSearch search = new BoatSearch();
        search.setText("   ");

        assertEquals("", search.toQueryString());
    }

    @Test
    public void theQueryHasEveryCriterionGiven() {
        final BoatSearch search = new BoatSearch();
        search.setText(" Solent ");
        search.setRegion(List.of(SailingRegion.WEST_COUNTRY));
        search.setPurpose(List.of(SailingPurpose.CRUISING));
        search.setWhen(List.of(FrequencyEnum.WEEKENDS));
        search.setCosts(List.of(FinancialArrangementEnum.SHARED_FIXED));
        search.setMyLevel(300);

        assertEquals("text=Solent&region=WEST_COUNTRY&purpose=CRUISING&when=WEEKENDS&costs=SHARED_FIXED&myLevel=300", search.toQueryString());
    }

    @Test
    public void theQueryHasTheDatesAndLengths() {
        final BoatSearch search = new BoatSearch();
        search.setPostedWithin(30);
        search.setSailingFrom(LocalDate.of(2027, 6, 1));
        search.setSailingTo(LocalDate.of(2027, 6, 14));
        search.setMinLength(30);
        search.setMaxLength(40);

        assertEquals("postedWithin=30&sailingFrom=2027-06-01&sailingTo=2027-06-14&minLength=30&maxLength=40", search.toQueryString());
    }

    @Test
    public void theTextIsEncoded() {
        final BoatSearch search = new BoatSearch();
        search.setText("Sigma 33 & crew");

        assertEquals("text=Sigma%2033%20%26%20crew", search.toQueryString());
    }

    @Test
    public void severalChoicesAreEachInTheQuery() {
        final BoatSearch search = new BoatSearch();
        search.setPurpose(List.of(SailingPurpose.CRUISING, SailingPurpose.RACING));
        search.setWhen(List.of(FrequencyEnum.WEEKENDS, FrequencyEnum.HOLIDAYS));

        assertEquals("purpose=CRUISING&purpose=RACING&when=WEEKENDS&when=HOLIDAYS", search.toQueryString());
    }

    @Test
    public void aWholeGroupOfRegionsTakesInItsOwnRegions() {
        final BoatSearch search = new BoatSearch();
        search.setRegionGroup(List.of(SailingRegion.Group.UK_AND_IRELAND));
        search.setRegion(List.of(SailingRegion.WEST_COUNTRY, SailingRegion.CARIBBEAN));

        assertEquals("regionGroup=UK_AND_IRELAND&region=CARIBBEAN", search.toQueryString(), "West Country is in the group already");
        assertEquals(List.of("UK and Ireland", "Caribbean"), search.getWhereChosen());
        assertTrue(search.getRegionsWanted().containsAll(SailingRegion.Group.UK_AND_IRELAND.getRegions()));
        assertTrue(search.getRegionsWanted().contains(SailingRegion.CARIBBEAN));
        assertEquals(SailingRegion.Group.UK_AND_IRELAND.getRegions().size() + 1, search.getRegionsWanted().size());
    }

    @Test
    public void lengthsAndDatesMustMakeSense() {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final BoatSearch search = new BoatSearch();
        search.setMinLength(-5);
        assertEquals(Set.of("Length from can't be less than nothing."), messages(validator, search));

        search.setMinLength(40);
        search.setMaxLength(30);
        assertEquals(Set.of("Length from is more than length to."), messages(validator, search));

        search.setMaxLength(40);
        search.setSailingFrom(LocalDate.of(2027, 6, 14));
        search.setSailingTo(LocalDate.of(2027, 6, 1));
        assertEquals(Set.of("Sailing from is after sailing to."), messages(validator, search));

        search.setSailingTo(LocalDate.of(2027, 6, 14));
        assertEquals(Set.of(), messages(validator, search), "the same length or day at both ends is fine");
    }

    private static Set<String> messages(Validator validator, BoatSearch search) {
        return validator.validate(search).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }
}
