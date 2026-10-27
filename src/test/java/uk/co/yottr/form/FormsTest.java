package uk.co.yottr.form;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.yottr.model.*;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.testconfig.UnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static uk.co.yottr.builder.BoatBuilder.aBoat;
import static uk.co.yottr.builder.UserBuilder.aUser;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UnitConfig.class})
public class FormsTest {

    @Autowired
    private Validator validator;

    @Autowired
    private UserRepository mockUserRepository;

    @BeforeEach
    public void resetMocks() {
        reset(mockUserRepository);
    }

    private static SignupForm aSignupForm() {
        final SignupForm form = new SignupForm();
        form.setUsername("newcomer");
        form.setPassword("a-long-password");
        form.setTitle("Ms");
        form.setFirstName("New");
        form.setLastName("Comer");
        form.setEmail("new@example.test");
        form.setMobile("07700 900123");
        form.setCountry(Country.UK);
        form.setPostcode("PO1 1AA");
        form.setAboutMe("Keen.");
        return form;
    }

    @Test
    public void aCompleteSignupFormIsValid() {
        assertEquals(Set.of(), validator.validate(aSignupForm()));
    }

    @Test
    public void signupRejectsAUsernameThatIsTaken() {
        when(mockUserRepository.findByUsername("newcomer")).thenReturn(new User());

        final Set<ConstraintViolation<SignupForm>> violations = validator.validate(aSignupForm());

        assertEquals(1, violations.size(), "should be one violation: " + violations);
        final ConstraintViolation<SignupForm> violation = violations.iterator().next();
        assertEquals("username", violation.getPropertyPath().toString());
        assertEquals("Username not available", violation.getMessage());
    }

    @Test
    public void signupMakesANewUserWithOnlyTheFormsDetails() {
        final User user = aSignupForm().toUser();

        assertNull(user.getId(), "a new user has no id");
        assertEquals("newcomer", user.getUsername());
        assertEquals("a-long-password", user.getPassword());
        assertEquals("Ms", user.getTitle());
        assertEquals("New", user.getFirstName());
        assertEquals("Comer", user.getLastName());
        assertEquals("new@example.test", user.getEmail());
        assertEquals("07700 900123", user.getMobile());
        assertEquals(Country.UK, user.getCountry());
        assertEquals("PO1 1AA", user.getPostcode());
        assertEquals("Keen.", user.getAboutMe());
        assertTrue(user.getUserRoles().isEmpty(), "roles are the controller's to give");
        assertEquals(Integer.valueOf(2), user.getMaxListings(), "the default listing limit");
    }

    @Test
    public void myDetailsStartsWithTheUsersDetailsButNoPassword() {
        final User user = aUser().build();

        final MyDetailsForm form = MyDetailsForm.from(user);

        assertEquals(user.getFirstName(), form.getFirstName());
        assertEquals(user.getEmail(), form.getEmail());
        assertNull(form.getPassword());
        assertFalse(form.isChangingPassword());
    }

    @Test
    public void myDetailsTreatsABlankPasswordAsKeepingTheCurrentOne() {
        final MyDetailsForm form = MyDetailsForm.from(aUser().build());
        form.setPassword("");

        assertFalse(form.isChangingPassword());
        assertEquals(Set.of(), validator.validate(form), "a blank password is not an error");
    }

    @Test
    public void myDetailsChecksTheLengthOfANewPassword() {
        final MyDetailsForm form = MyDetailsForm.from(aUser().build());
        form.setPassword("short");

        assertTrue(form.isChangingPassword());
        final Set<ConstraintViolation<MyDetailsForm>> violations = validator.validate(form);
        assertEquals(1, violations.size(), "should be one violation: " + violations);
        assertEquals("password", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    public void listingFormRoundTripsABoat() {
        final Boat boat = aBoat().withOwner(aUser().build()).build();
        boat.setDateRelevantTo(LocalDate.of(2027, 6, 14));

        final ListingForm form = ListingForm.from(boat);

        assertEquals(boat.getTitle(), form.getTitle());
        assertEquals(boat.getMakeAndModel(), form.getMakeAndModel());
        assertEquals(boat.getVesselType(), form.getVesselType());
        assertEquals(boat.getYearBuilt(), form.getYearBuilt());
        assertEquals(boat.getRoleRequired(), form.getRoleRequired());
        assertEquals(List.of(Frequency.FrequencyEnum.HOLIDAYS), form.getFrequency());
        assertEquals(FinancialArrangement.FinancialArrangementEnum.NO_CONTRIBUTION, form.getFinancialArrangement());
        assertEquals(boat.getMinimumRequiredLevelByRank(), form.getMinimumRequiredLevelByRank());
        assertEquals(LocalDate.of(2027, 6, 14), form.getDateRelevantTo());
        assertEquals(Set.of(), validator.validate(form), "a stored boat makes a valid form");
    }

    @Test
    public void listingFormRejectsAnUnknownLevel() {
        final ListingForm form = ListingForm.from(aBoat().withOwner(aUser().build()).build());
        form.setMinimumRequiredLevelByRank(150);

        final Set<ConstraintViolation<ListingForm>> violations = validator.validate(form);

        assertEquals(1, violations.size(), "should be one violation: " + violations);
        assertEquals("minimumRequiredLevelKnown", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    public void listingFormRoundTripsTheTermsOfATrip() {
        final Boat boat = aBoat().withOwner(aUser().build()).build();
        boat.setSmoking(Smoking.YES_OUTSIDE);
        boat.setTravelExpenses(TravelExpenses.NEGOTIABLE);
        boat.setNumberOfCrewWanted(3);
        boat.setWhenFrom(LocalDate.of(2027, 6, 14));
        boat.setWhenTo(LocalDate.of(2027, 6, 28));

        final ListingForm form = ListingForm.from(boat);
        final Boat copy = aBoat().withOwner(aUser().build()).build();
        form.copyTo(copy);

        assertEquals(Smoking.YES_OUTSIDE, copy.getSmoking());
        assertEquals(TravelExpenses.NEGOTIABLE, copy.getTravelExpenses());
        assertEquals(Integer.valueOf(3), copy.getNumberOfCrewWanted());
        assertEquals(LocalDate.of(2027, 6, 14), copy.getWhenFrom());
        assertEquals(LocalDate.of(2027, 6, 28), copy.getWhenTo());
        assertEquals(Set.of(), validator.validate(form));
    }

    @Test
    public void listingFormNeedsTheLevelOfACourse() {
        final ListingForm form = ListingForm.from(aBoat().withOwner(aUser().build()).build());
        form.setSailingPurpose(SailingPurpose.COURSE);

        final Set<ConstraintViolation<ListingForm>> violations = validator.validate(form);

        assertEquals(1, violations.size(), "should be one violation: " + violations);
        assertEquals("courseLevelGivenForACourse", violations.iterator().next().getPropertyPath().toString());

        form.setCourseLevel(CourseLevel.DAY_SKIPPER);
        assertEquals(Set.of(), validator.validate(form), "with its level");

        final Boat boat = aBoat().withOwner(aUser().build()).build();
        form.copyTo(boat);
        assertEquals(CourseLevel.DAY_SKIPPER, boat.getCourseLevel());
    }

    @Test
    public void listingFormDropsACourseLevelWhenItIsNotACourse() {
        final ListingForm form = ListingForm.from(aBoat().withOwner(aUser().build()).build());
        form.setSailingPurpose(SailingPurpose.CRUISING);
        form.setCourseLevel(CourseLevel.DAY_SKIPPER);

        final Boat boat = aBoat().withOwner(aUser().build()).build();
        form.copyTo(boat);

        assertNull(boat.getCourseLevel(), "only a course has a level");
    }

    @Test
    public void listingFormRefusesAStartAfterTheEnd() {
        final ListingForm form = ListingForm.from(aBoat().withOwner(aUser().build()).build());
        form.setWhenFrom(LocalDate.of(2027, 6, 28));
        form.setWhenTo(LocalDate.of(2027, 6, 14));

        final Set<ConstraintViolation<ListingForm>> violations = validator.validate(form);

        assertEquals(1, violations.size(), "should be one violation: " + violations);
        assertEquals("whenFromNotAfterWhenTo", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    public void listingFormAllowsADaySailAndOpenEndedDates() {
        final ListingForm form = ListingForm.from(aBoat().withOwner(aUser().build()).build());

        form.setWhenFrom(LocalDate.of(2027, 6, 14));
        form.setWhenTo(LocalDate.of(2027, 6, 14));
        assertEquals(Set.of(), validator.validate(form), "a day sail");

        form.setWhenTo(null);
        assertEquals(Set.of(), validator.validate(form), "no end date");

        form.setWhenFrom(null);
        form.setWhenTo(LocalDate.of(2027, 6, 14));
        assertEquals(Set.of(), validator.validate(form), "no start date");
    }

    @Test
    public void listingFormLimitsTheCrewWanted() {
        final ListingForm form = ListingForm.from(aBoat().withOwner(aUser().build()).build());

        form.setNumberOfCrewWanted(0);
        assertEquals(1, validator.validate(form).size(), "none");

        form.setNumberOfCrewWanted(100);
        assertEquals(1, validator.validate(form).size(), "too many");
    }
}
