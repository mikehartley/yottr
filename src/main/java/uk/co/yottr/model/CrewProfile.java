package uk.co.yottr.model;

import jakarta.persistence.*;
import uk.co.yottr.model.Frequency.FrequencyEnum;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * What a member looking for a boat tells owners about themselves (#49), so that owners can find crew as well as
 * crew finding boats. Having one is opting in to being found; the owners who find it see the member's name, email
 * and mobile, as crew see an owner's on a listing.
 */
@Entity
@Table(name = "crew_profiles")
public class CrewProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User member;

    // the rank of their RYA level (RyaSailCruisingLevel.Level)
    @Column(name = "level_rank", nullable = false)
    private int levelRank;

    // where they'd like to sail
    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    private SailingRegion region;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crew_profile_availability", joinColumns = @JoinColumn(name = "crew_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private Set<FrequencyEnum> availability = EnumSet.noneOf(FrequencyEnum.class);

    @Column(name = "experience", nullable = false, length = 1000)
    private String experience;

    @Column(name = "last_updated", nullable = false)
    private LocalDate lastUpdated;

    /**
     * For Hibernate.
     */
    protected CrewProfile() {
    }

    public CrewProfile(User member) {
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public User getMember() {
        return member;
    }

    public int getLevelRank() {
        return levelRank;
    }

    public void setLevelRank(int levelRank) {
        this.levelRank = levelRank;
    }

    /**
     * The level's name, such as Day Skipper.
     */
    public String getLevelName() {
        return Arrays.stream(RyaSailCruisingLevel.Level.values()).filter(level -> level.rank == levelRank)
                .map(level -> new RyaSailCruisingLevel(level).getDisplayName()).findFirst().orElse("");
    }

    public SailingRegion getRegion() {
        return region;
    }

    public void setRegion(SailingRegion region) {
        this.region = region;
    }

    public Set<FrequencyEnum> getAvailability() {
        return availability;
    }

    public void setAvailability(Set<FrequencyEnum> availability) {
        this.availability.clear();
        this.availability.addAll(availability);
    }

    /**
     * When they can sail, as it's shown, in the usual order: "Weekends, Holidays".
     */
    public String getWhen() {
        return availability.stream().sorted().map(FrequencyEnum::getDisplayName).collect(Collectors.joining(", "));
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
