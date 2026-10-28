package uk.co.yottr.repository;

import java.util.Locale;

/**
 * LIKE patterns for the searches, which match text anywhere in a column, ignoring case.
 */
final class Like {

    static final char ESCAPE = '\\';

    private Like() {
    }

    /**
     * A pattern that finds the text anywhere in a lower-cased column.
     */
    static String containing(String text) {
        return "%" + escape(text.toLowerCase(Locale.ROOT)) + "%";
    }

    /**
     * So that a % or _ the member typed is matched as itself.
     */
    static String escape(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
