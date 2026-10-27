package uk.co.yottr.form;

import org.junit.jupiter.api.Test;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.SailingPurpose;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        search.setPurpose(SailingPurpose.CRUISING);
        search.setWhen(FrequencyEnum.WEEKENDS);
        search.setCosts(FinancialArrangementEnum.SHARED_FIXED);
        search.setMyLevel(300);

        assertEquals("text=Solent&purpose=CRUISING&when=WEEKENDS&costs=SHARED_FIXED&myLevel=300", search.toQueryString());
    }

    @Test
    public void theTextIsEncoded() {
        final BoatSearch search = new BoatSearch();
        search.setText("Sigma 33 & crew");

        assertEquals("text=Sigma%2033%20%26%20crew", search.toQueryString());
    }
}
