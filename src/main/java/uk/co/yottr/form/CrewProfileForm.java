package uk.co.yottr.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.co.yottr.model.CrewProfile;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingRegion;
import uk.co.yottr.validator.Inoffensive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * A member's crew profile, as they fill it in.
 */
public class CrewProfileForm {

    private static final String REQUIRED_ERROR_MSG = "required";

    private int levelRank = RyaSailCruisingLevel.Level.NONE.rank;

    @NotNull(message = REQUIRED_ERROR_MSG)
    private SailingRegion region;

    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min = 1, message = "must have at least one")
    private List<FrequencyEnum> availability = new ArrayList<>();

    @NotBlank(message = REQUIRED_ERROR_MSG)
    @Size(max = 1000)
    @Inoffensive
    private String experience;

    public static CrewProfileForm from(CrewProfile profile) {
        final CrewProfileForm form = new CrewProfileForm();
        form.levelRank = profile.getLevelRank();
        form.region = profile.getRegion();
        form.availability = new ArrayList<>(profile.getAvailability());
        form.experience = profile.getExperience();
        return form;
    }

    public void copyTo(CrewProfile profile) {
        profile.setLevelRank(levelRank);
        profile.setRegion(region);
        profile.setAvailability(availability.isEmpty() ? EnumSet.noneOf(FrequencyEnum.class) : EnumSet.copyOf(availability));
        profile.setExperience(experience.strip());
    }

    @AssertTrue(message = "unknown level")
    public boolean isLevelKnown() {
        return Arrays.stream(RyaSailCruisingLevel.Level.values()).anyMatch(level -> level.rank == levelRank);
    }

    public int getLevelRank() {
        return levelRank;
    }

    public void setLevelRank(int levelRank) {
        this.levelRank = levelRank;
    }

    public SailingRegion getRegion() {
        return region;
    }

    public void setRegion(SailingRegion region) {
        this.region = region;
    }

    public List<FrequencyEnum> getAvailability() {
        return availability;
    }

    public void setAvailability(List<FrequencyEnum> availability) {
        this.availability = availability;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
