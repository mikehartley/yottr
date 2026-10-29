package uk.co.yottr.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.CommercialOffering;
import uk.co.yottr.model.CourseLevel;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.MoneyPeriod;
import uk.co.yottr.model.RoleRequired;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.SailingRegion;
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

    // needed unless there's no cost to crew, and ignored then (isMoneyGivenUnlessNoCost)
    @Min(value = 1, message = "must be more than zero")
    @Max(99999)
    private Integer moneyAmount;

    private MoneyPeriod moneyPeriod;

    // needed when crew pay a business, and ignored otherwise (isCommercialOfferingGivenWhenCommercial)
    private CommercialOffering commercialOffering;

    // needed for a course or for tuition, and ignored for anything else (isCourseLevelGivenWhenNeeded)
    private CourseLevel courseLevel;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private SailingRegion fromRegion;

    @Size(max = 60)
    private String fromPlace;

    // needed for a trip or a delivery, or with a place (isWhereToGivenWhenNeeded)
    private SailingRegion toRegion;

    @Size(max = 60)
    private String toPlace;

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
        form.moneyAmount = boat.getMoneyAmount();
        form.moneyPeriod = boat.getMoneyPeriod();
        form.commercialOffering = boat.getCommercialOffering();
        form.courseLevel = boat.getCourseLevel();
        form.fromRegion = boat.getFromRegion();
        form.fromPlace = boat.getFromPlace();
        form.toRegion = boat.getToRegion();
        form.toPlace = boat.getToPlace();
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
        final boolean money = financialArrangement != FinancialArrangementEnum.NO_CONTRIBUTION;
        boat.setMoneyAmount(money ? moneyAmount : null);
        boat.setMoneyPeriod(money ? moneyPeriod : null);
        boat.setCommercialOffering(isCommercial() ? commercialOffering : null);
        boat.setCourseLevel(isCourseLevelNeeded() ? courseLevel : null);
        boat.setFromRegion(fromRegion);
        boat.setFromPlace(placeOrNull(fromPlace));
        boat.setToRegion(toRegion);
        boat.setToPlace(toRegion == null ? null : placeOrNull(toPlace));
        boat.setWhenFrom(whenFrom);
        boat.setWhenTo(whenTo);
    }

    private static String placeOrNull(String place) {
        return place == null || place.isBlank() ? null : place.strip();
    }

    @AssertTrue(message = "unknown level")
    public boolean isMinimumRequiredLevelKnown() {
        return Arrays.stream(RyaSailCruisingLevel.Level.values()).anyMatch(level -> level.rank == minimumRequiredLevelByRank);
    }

    @AssertTrue(message = "a course or tuition needs its level")
    public boolean isCourseLevelGivenWhenNeeded() {
        return !isCourseLevelNeeded() || courseLevel != null;
    }

    @AssertTrue(message = "say what's offered")
    public boolean isCommercialOfferingGivenWhenCommercial() {
        return !isCommercial() || commercialOffering != null;
    }

    private boolean isCommercial() {
        return financialArrangement == FinancialArrangementEnum.COMMERCIAL;
    }

    /**
     * A course has a level, and so does tuition, which is a course that crew pay for.
     */
    private boolean isCourseLevelNeeded() {
        return sailingPurpose == SailingPurpose.COURSE || (isCommercial() && commercialOffering == CommercialOffering.TUITION);
    }

    @AssertTrue(message = "say how much, and what it's for")
    public boolean isMoneyGivenUnlessNoCost() {
        return financialArrangement == FinancialArrangementEnum.NO_CONTRIBUTION || (moneyAmount != null && moneyPeriod != null);
    }

    /**
     * A trip or a delivery goes somewhere, so it says where; and a place needs its region, which is what's searched.
     */
    @AssertTrue(message = "needed for a trip or a delivery, or with a place")
    public boolean isWhereToGivenWhenNeeded() {
        final boolean needed = (frequency != null && frequency.contains(FrequencyEnum.TRIP)) || sailingPurpose == SailingPurpose.DELIVERY
                || placeOrNull(toPlace) != null;
        return !needed || toRegion != null;
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

    public SailingRegion getFromRegion() {
        return fromRegion;
    }

    public void setFromRegion(SailingRegion fromRegion) {
        this.fromRegion = fromRegion;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public SailingRegion getToRegion() {
        return toRegion;
    }

    public void setToRegion(SailingRegion toRegion) {
        this.toRegion = toRegion;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
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

    public Integer getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(Integer moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public MoneyPeriod getMoneyPeriod() {
        return moneyPeriod;
    }

    public void setMoneyPeriod(MoneyPeriod moneyPeriod) {
        this.moneyPeriod = moneyPeriod;
    }

    public CommercialOffering getCommercialOffering() {
        return commercialOffering;
    }

    public void setCommercialOffering(CommercialOffering commercialOffering) {
        this.commercialOffering = commercialOffering;
    }
}
