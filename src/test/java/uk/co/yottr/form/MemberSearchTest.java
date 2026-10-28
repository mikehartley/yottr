package uk.co.yottr.form;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberSearchTest {

    @Test
    public void anEmptySearchHasNoQuery() {
        assertEquals("", new MemberSearch().toQueryString());
    }

    @Test
    public void blankTextIsLeftOut() {
        final MemberSearch search = new MemberSearch();
        search.setText("   ");

        assertEquals("", search.toQueryString());
    }

    @Test
    public void theQueryHasEveryCriterionGiven() {
        final MemberSearch search = new MemberSearch();
        search.setText(" Ann Smith ");
        search.setJoinedFrom(LocalDate.of(2026, 1, 1));
        search.setJoinedTo(LocalDate.of(2026, 6, 30));

        assertEquals("text=Ann%20Smith&joinedFrom=2026-01-01&joinedTo=2026-06-30", search.toQueryString());
    }
}
