package uk.co.yottr.form;

import org.springframework.web.util.UriComponentsBuilder;
import uk.co.yottr.model.FinancialArrangement.FinancialArrangementEnum;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.SailingPurpose;

/**
 * What a member is looking for in the list of boats. Every criterion is optional; an empty search finds every
 * visible listing.
 */
public class BoatSearch {

    // matched against the title, make and model and description, or the whole reference
    private String text;

    private SailingPurpose purpose;

    private FrequencyEnum when;

    private FinancialArrangementEnum costs;

    // the rank of the searcher's own RYA level: listings that need more are left out
    private Integer myLevel;

    /**
     * The criteria as a query string, without the leading ?, so that page links keep the search.
     */
    public String toQueryString() {
        final UriComponentsBuilder query = UriComponentsBuilder.newInstance();
        if (text != null && !text.isBlank()) {
            query.queryParam("text", text.trim());
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
        final String queryString = query.encode().build().getQuery();
        return queryString == null ? "" : queryString;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
