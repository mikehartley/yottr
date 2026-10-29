package uk.co.yottr.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.SailingRegion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * What a member is looking for in the list of boats. Every criterion is optional; an empty search finds every
 * visible listing. Where several choices of one criterion are given, a listing matching any of them will do.
 */
public class BoatSearch {

    /**
     * The choices for how recently a listing was posted, in days, as the search form offers them.
     */
    public static final List<Integer> POSTED_WITHIN_DAYS = List.of(7, 30, 90);

    // matched against the title, make and model, description, places and region names, or the whole reference
    private String text;

    // Where: whole groups of regions, and regions; a listing matches if it sails from or to any of them. A group
    // and some of its own regions together is just the group (getRegionsWanted).
    private List<SailingRegion.Group> regionGroup = new ArrayList<>();

    private List<SailingRegion> region = new ArrayList<>();

    // any of each
    private List<SailingPurpose> purpose = new ArrayList<>();

    private List<FrequencyEnum> when = new ArrayList<>();

    private List<FinancialArrangementEnum> costs = new ArrayList<>();

    // the rank of the searcher's own highest RYA level: listings that need it or less match, and those that need
    // more are left out
    private Integer myLevel;

    // listings first posted this many days ago or since
    private Integer postedWithin;

    // the days the searcher could sail; a listing matches if its dates overlap them, and no dates means any
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate sailingFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate sailingTo;

    // in feet, whichever units the listing gives its length in
    @Min(value = 0, message = "Length from can't be less than nothing.")
    private Integer minLength;

    @Min(value = 0, message = "Length to can't be less than nothing.")
    private Integer maxLength;

    @AssertTrue(message = "Length from is more than length to.")
    public boolean isLengthsInOrder() {
        return minLength == null || maxLength == null || minLength <= maxLength;
    }

    @AssertTrue(message = "Sailing from is after sailing to.")
    public boolean isDatesInOrder() {
        return sailingFrom == null || sailingTo == null || !sailingFrom.isAfter(sailingTo);
    }

    /**
     * What Where shows: the names of the whole groups asked for, then of the other regions.
     */
    public List<String> getWhereChosen() {
        final List<String> chosen = new ArrayList<>(regionGroup.stream().map(SailingRegion.Group::getDisplayName).toList());
        region.stream().filter(one -> !regionGroup.contains(one.getGroup())).map(SailingRegion::getDisplayName).forEach(chosen::add);
        return chosen;
    }

    /**
     * Every region asked for, by itself or as part of a whole group, or none if it's anywhere.
     */
    public Set<SailingRegion> getRegionsWanted() {
        final Set<SailingRegion> wanted = EnumSet.noneOf(SailingRegion.class);
        regionGroup.forEach(group -> wanted.addAll(group.getRegions()));
        wanted.addAll(region);
        return wanted;
    }

    /**
     * The criteria as a query string, without the leading ?, so that page links keep the search.
     */
    public String toQueryString() {
        final UriComponentsBuilder query = UriComponentsBuilder.newInstance();
        if (text != null && !text.isBlank()) {
            query.queryParam("text", text.trim());
        }
        regionGroup.forEach(group -> query.queryParam("regionGroup", group.name()));
        // a region in a whole group asked for is left out, being the group already
        region.stream().filter(one -> !regionGroup.contains(one.getGroup())).forEach(one -> query.queryParam("region", one.name()));
        purpose.forEach(one -> query.queryParam("purpose", one.name()));
        when.forEach(one -> query.queryParam("when", one.name()));
        costs.forEach(one -> query.queryParam("costs", one.name()));
        if (myLevel != null) {
            query.queryParam("myLevel", myLevel);
        }
        if (postedWithin != null) {
            query.queryParam("postedWithin", postedWithin);
        }
        if (sailingFrom != null) {
            query.queryParam("sailingFrom", sailingFrom);
        }
        if (sailingTo != null) {
            query.queryParam("sailingTo", sailingTo);
        }
        if (minLength != null) {
            query.queryParam("minLength", minLength);
        }
        if (maxLength != null) {
            query.queryParam("maxLength", maxLength);
        }
        final String queryString = query.encode().build().getQuery();
        return queryString == null ? "" : queryString;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SailingRegion.Group> getRegionGroup() {
        return regionGroup;
    }

    public void setRegionGroup(List<SailingRegion.Group> regionGroup) {
        this.regionGroup = regionGroup == null ? new ArrayList<>() : regionGroup;
    }

    public List<SailingRegion> getRegion() {
        return region;
    }

    public void setRegion(List<SailingRegion> region) {
        this.region = region == null ? new ArrayList<>() : region;
    }

    public List<SailingPurpose> getPurpose() {
        return purpose;
    }

    public void setPurpose(List<SailingPurpose> purpose) {
        this.purpose = purpose == null ? new ArrayList<>() : purpose;
    }

    public List<FrequencyEnum> getWhen() {
        return when;
    }

    public void setWhen(List<FrequencyEnum> when) {
        this.when = when == null ? new ArrayList<>() : when;
    }

    public List<FinancialArrangementEnum> getCosts() {
        return costs;
    }

    public void setCosts(List<FinancialArrangementEnum> costs) {
        this.costs = costs == null ? new ArrayList<>() : costs;
    }

    public Integer getMyLevel() {
        return myLevel;
    }

    public void setMyLevel(Integer myLevel) {
        this.myLevel = myLevel;
    }

    public Integer getPostedWithin() {
        return postedWithin;
    }

    public void setPostedWithin(Integer postedWithin) {
        this.postedWithin = postedWithin;
    }

    public LocalDate getSailingFrom() {
        return sailingFrom;
    }

    public void setSailingFrom(LocalDate sailingFrom) {
        this.sailingFrom = sailingFrom;
    }

    public LocalDate getSailingTo() {
        return sailingTo;
    }

    public void setSailingTo(LocalDate sailingTo) {
        this.sailingTo = sailingTo;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
