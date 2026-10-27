package uk.co.yottr.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * For pages that htmx updates in place: htmx asks for the same URL as the whole page, and gets back only the part
 * it swaps in.
 */
final class Htmx {

    private Htmx() {
    }

    /**
     * Whether to answer with only the part of the page htmx swaps in. When htmx restores a page from its history
     * and has no copy, it asks for the whole page again, so that gets the whole page.
     * <p>
     * The answer differs by request header, so the response says so, and a browser won't serve the part from its
     * cache for the whole page, or the other way round.
     */
    static boolean wantsPartOfPage(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Vary", "HX-Request");
        return "true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"));
    }
}
