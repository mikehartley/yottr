package uk.co.yottr.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.CourseLevel;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.RoleRequired;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.Smoking;
import uk.co.yottr.model.TravelExpenses;
import uk.co.yottr.model.VesselType;
import uk.co.yottr.validator.YearBuilt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The new and edit listing forms. They bind here rather than to {@link Boat}, so a request can only set
 * the fields on the form. Frequencies, the financial arrangement and the minimum level are held by name
 * and rank; BoatService looks up the stored ones.
 */
public class ListingForm {

    private static final String REQUIRED_ERROR_MSG = "required";

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 2, max = 64)
    private String title;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, max = 60)
    private String makeAndModel;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Min(3)
    @Max(999)
    private Integer length;

    private boolean unitsImperial = true;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private Boat.HullType hullType = Boat.HullType.MONO;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private VesselType vesselType;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @YearBuilt
    private Integer yearBuilt;

    @NotEmpty(message = REQUIRED_ERROR_MSG)
    @Size(max = 2000)
    private String description;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private SailingPurpose sailingPurpose;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private RoleRequired roleRequired;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, message = "must have at least one")
    private List<FrequencyEnum> frequency = new ArrayList<>();

    @DateTimeFormat(pattern = Boat.DATE_PATTERN)
    private LocalDate dateRelevantTo;

    private int minimumRequiredLevelByRank = RyaSailCruisingLevel.Level.NONE.rank;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private FinancialArrangementEnum financialArrangement;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private Smoking smoking = Smoking.NO;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private TravelExpenses travelExpenses = TravelExpenses.PAID_BY_CREW;

    @Min(1)
    @Max(99)
    private Integer numberOfCrewWanted;

    // needed for a course, and ignored for anything else (isCourseLevelGivenForACourse)
    private CourseLevel courseLevel;

    // either may be left out, and no end means open-ended (isWhenFromNotAfterWhenTo)
    @DateTimeFormat(pattern = Boat.DATE_PATTERN)
    private LocalDate whenFrom;

    @DateTimeFormat(pattern = Boat.DATE_PATTERN)
    private LocalDate whenTo;

    public static ListingForm from(Boat boat) {
        final ListingForm form = new ListingForm();
        form.title = boat.getTitle();
        form.makeAndModel = boat.getMakeAndModel();
        form.length = boat.getLength();
        form.unitsImperial = boat.isUnitsImperial();
        form.hullType = boat.getHullType();
        form.vesselType = boat.getVesselType();
        form.yearBuilt = boat.getYearBuilt();
        form.description = boat.getDescription();
        form.sailingPurpose = boat.getSailingPurpose();
        form.roleRequired = boat.getRoleRequired();
        form.frequency = boat.getFrequency().stream().map(Frequency::getName).map(FrequencyEnum::valueOf).toList();
        form.dateRelevantTo = boat.getDateRelevantTo();
        form.minimumRequiredLevelByRank = boat.getMinimumRequiredLevelByRank();
        form.financialArrangement = boat.getFinancialArrangement() == null
                ? null : FinancialArrangementEnum.valueOf(boat.getFinancialArrangement().getName());
        form.smoking = boat.getSmoking();
        form.travelExpenses = boat.getTravelExpenses();
        form.numberOfCrewWanted = boat.getNumberOfCrewWanted();
        form.courseLevel = boat.getCourseLevel();
        form.whenFrom = boat.getWhenFrom();
        form.whenTo = boat.getWhenTo();
        return form;
    }

    /**
     * Copies the plain fields onto the boat. The reference data (frequencies, financial arrangement and
     * minimum level) is BoatService's to look up and set.
     */
    public void copyTo(Boat boat) {
        boat.setTitle(title);
        boat.setMakeAndModel(makeAndModel);
        boat.setLength(length);
        boat.setUnitsImperial(unitsImperial);
        boat.setHullType(hullType);
        boat.setVesselType(vesselType);
        boat.setYearBuilt(yearBuilt);
        boat.setDescription(description);
        boat.setSailingPurpose(sailingPurpose);
        boat.setRoleRequired(roleRequired);
        boat.setDateRelevantTo(dateRelevantTo);
        boat.setSmoking(smoking);
        boat.setTravelExpenses(travelExpenses);
        boat.setNumberOfCrewWanted(numberOfCrewWanted);
        boat.setCourseLevel(sailingPurpose == SailingPurpose.COURSE ? courseLevel : null);
        boat.setWhenFrom(whenFrom);
        boat.setWhenTo(whenTo);
    }

    @AssertTrue(message = "unknown level")
    public boolean isMinimumRequiredLevelKnown() {
        return Arrays.stream(RyaSailCruisingLevel.Level.values()).anyMatch(level -> level.rank == minimumRequiredLevelByRank);
    }

    @AssertTrue(message = "a course needs its level")
    public boolean isCourseLevelGivenForACourse() {
        return sailingPurpose != SailingPurpose.COURSE || courseLevel != null;
    }

    /**
     * The start may be the same day as the end, for a day sail.
     */
    @AssertTrue(message = "must not be after the end date")
    public boolean isWhenFromNotAfterWhenTo() {
        return whenFrom == null || whenTo == null || !whenFrom.isAfter(whenTo);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.makeAndModel = makeAndModel;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isUnitsImperial() {
        return unitsImperial;
    }

    public void setUnitsImperial(boolean unitsImperial) {
        this.unitsImperial = unitsImperial;
    }

    public Boat.HullType getHullType() {
        return hullType;
    }

    public void setHullType(Boat.HullType hullType) {
        this.hullType = hullType;
    }

    public VesselType getVesselType() {
        return vesselType;
    }

    public void setVesselType(VesselType vesselType) {
        this.vesselType = vesselType;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SailingPurpose getSailingPurpose() {
        return sailingPurpose;
    }

    public void setSailingPurpose(SailingPurpose sailingPurpose) {
        this.sailingPurpose = sailingPurpose;
    }

    public RoleRequired getRoleRequired() {
        return roleRequired;
    }

    public void setRoleRequired(RoleRequired roleRequired) {
        this.roleRequired = roleRequired;
    }

    public List<FrequencyEnum> getFrequency() {
        return frequency;
    }

    public void setFrequency(List<FrequencyEnum> frequency) {
        this.frequency = frequency;
    }

    public LocalDate getDateRelevantTo() {
        return dateRelevantTo;
    }

    public void setDateRelevantTo(LocalDate dateRelevantTo) {
        this.dateRelevantTo = dateRelevantTo;
    }

    public int getMinimumRequiredLevelByRank() {
        return minimumRequiredLevelByRank;
    }

    public void setMinimumRequiredLevelByRank(int minimumRequiredLevelByRank) {
        this.minimumRequiredLevelByRank = minimumRequiredLevelByRank;
    }

    public FinancialArrangementEnum getFinancialArrangement() {
        return financialArrangement;
    }

    public void setFinancialArrangement(FinancialArrangementEnum financialArrangement) {
        this.financialArrangement = financialArrangement;
    }

    public Smoking getSmoking() {
        return smoking;
    }

    public void setSmoking(Smoking smoking) {
        this.smoking = smoking;
    }

    public TravelExpenses getTravelExpenses() {
        return travelExpenses;
    }

    public void setTravelExpenses(TravelExpenses travelExpenses) {
        this.travelExpenses = travelExpenses;
    }

    public Integer getNumberOfCrewWanted() {
        return numberOfCrewWanted;
    }

    public void setNumberOfCrewWanted(Integer numberOfCrewWanted) {
        this.numberOfCrewWanted = numberOfCrewWanted;
    }

    public CourseLevel getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(CourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }

    public LocalDate getWhenFrom() {
        return whenFrom;
    }

    public void setWhenFrom(LocalDate whenFrom) {
        this.whenFrom = whenFrom;
    }

    public LocalDate getWhenTo() {
        return whenTo;
    }

    public void setWhenTo(LocalDate whenTo) {
        this.whenTo = whenTo;
    }
}
