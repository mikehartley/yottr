package uk.co.yottr.form;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.SailingPurpose;
import uk.co.yottr.model.SailingRegion;

import java.time.LocalDate;
import java.util.List;

/**
 * What a member is looking for in the list of boats. Every criterion is optional; an empty search finds every
 * visible listing.
 */
public class BoatSearch {

    /**
     * The choices for how recently a listing was posted, in days, as the search form offers them.
     */
    public static final List<Integer> POSTED_WITHIN_DAYS = List.of(7, 30, 90);

    // matched against the title, make and model, description and places, or the whole reference
    private String text;

    // a listing sailing from or to it
    private SailingRegion region;

    private SailingPurpose purpose;

    private FrequencyEnum when;

    private FinancialArrangementEnum costs;

    // the rank of the searcher's own RYA level: listings that need more are left out
    private Integer myLevel;

    // listings first posted this many days ago or since
    private Integer postedWithin;

    // the days the searcher could sail; a listing matches if its dates overlap them, and no dates means any
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate sailingFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate sailingTo;

    // in feet, whichever units the listing gives its length in
    private Integer minLength;

    private Integer maxLength;

    /**
     * The criteria as a query string, without the leading ?, so that page links keep the search.
     */
    public String toQueryString() {
        final UriComponentsBuilder query = UriComponentsBuilder.newInstance();
        if (text != null && !text.isBlank()) {
            query.queryParam("text", text.trim());
        }
        if (region != null) {
            query.queryParam("region", region.name());
        }
        if (purpose != null) {
            query.queryParam("purpose", purpose.name());
        }
        if (when != null) {
            query.queryParam("when", when.name());
        }
        if (costs != null) {
            query.queryParam("costs", costs.name());
        }
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

    public SailingRegion getRegion() {
        return region;
    }

    public void setRegion(SailingRegion region) {
        this.region = region;
    }

    public SailingPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(SailingPurpose purpose) {
        this.purpose = purpose;
    }

    public FrequencyEnum getWhen() {
        return when;
    }

    public void setWhen(FrequencyEnum when) {
        this.when = when;
    }

    public FinancialArrangementEnum getCosts() {
        return costs;
    }

    public void setCosts(FinancialArrangementEnum costs) {
        this.costs = costs;
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
