package uk.co.yottr.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UrlPathHelper;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Which menu item a page comes under, so the menu can show where the member is. It goes to the pages as the
 * request attribute "currentMenuItem", the path of that item's link, which mainTemplate.html compares with each
 * link's path. As a request attribute rather than a model attribute, it leaves the controllers' models alone.
 */
@Component
public class MenuItem implements HandlerInterceptor {

    static final String REQUEST_ATTRIBUTE = "currentMenuItem";

    static final String BOATS = "/s/listings/all";
    static final String MY_LISTINGS = "/s/listings/mine";
    static final String MEMBERS = "/admin/users";

    // the fixed paths under /s/listings/; anything else there is a listing reference
    private static final Set<String> LISTINGS_PAGES = Set.of("all", "mine", "new");
    private static final Pattern LISTING_PAGE = Pattern.compile("/s/listings/([^/]+)");
    private static final Pattern EDIT_LISTING_PAGE = Pattern.compile("/s/listings/[^/]+/edit");
    private static final Pattern EDIT_MEMBER_PAGE = Pattern.compile("/admin/user/[^/]+/edit");

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(REQUEST_ATTRIBUTE, forPath(urlPathHelper.getPathWithinApplication(request)));
        return true;
    }

    /**
     * The path of the menu item for a page: a listing's own page comes under Boats, editing a listing under My
     * Listings, editing a member under Members, and any other page under the item with its own path, if there is one.
     */
    static String forPath(String path) {
        if (EDIT_LISTING_PAGE.matcher(path).matches()) {
            return MY_LISTINGS;
        }
        if (EDIT_MEMBER_PAGE.matcher(path).matches()) {
            return MEMBERS;
        }
        final var listingPage = LISTING_PAGE.matcher(path);
        if (listingPage.matches() && !LISTINGS_PAGES.contains(listingPage.group(1))) {
            return BOATS;
        }
        return path;
    }
}
