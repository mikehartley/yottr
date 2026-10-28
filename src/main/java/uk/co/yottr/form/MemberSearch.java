package uk.co.yottr.form;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

/**
 * What an administrator is looking for in the list of members. Every criterion is optional; an empty search finds
 * every member.
 */
public class MemberSearch {

    // matched against the username, name or email
    private String text;

    // the days are included; members with no joined date are left out when either is given
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joinedFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joinedTo;

    /**
     * The criteria as a query string, without the leading ?, so that page links keep the search.
     */
    public String toQueryString() {
        final UriComponentsBuilder query = UriComponentsBuilder.newInstance();
        if (text != null && !text.isBlank()) {
            query.queryParam("text", text.trim());
        }
        if (joinedFrom != null) {
            query.queryParam("joinedFrom", joinedFrom);
        }
        if (joinedTo != null) {
            query.queryParam("joinedTo", joinedTo);
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

    public LocalDate getJoinedFrom() {
        return joinedFrom;
    }

    public void setJoinedFrom(LocalDate joinedFrom) {
        this.joinedFrom = joinedFrom;
    }

    public LocalDate getJoinedTo() {
        return joinedTo;
    }

    public void setJoinedTo(LocalDate joinedTo) {
        this.joinedTo = joinedTo;
    }
}
