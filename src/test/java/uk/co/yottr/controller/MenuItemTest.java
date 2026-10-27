package uk.co.yottr.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MenuItemTest {

    @Test
    public void aPageInTheMenuIsItsOwnItem() {
        assertEquals("/s/listings/all", MenuItem.forPath("/s/listings/all"));
        assertEquals("/s/listings/mine", MenuItem.forPath("/s/listings/mine"));
        assertEquals("/s/listings/new", MenuItem.forPath("/s/listings/new"));
        assertEquals("/s/myDetails", MenuItem.forPath("/s/myDetails"));
        assertEquals("/admin/users", MenuItem.forPath("/admin/users"));
    }

    @Test
    public void aListingsOwnPageComesUnderBoats() {
        assertEquals("/s/listings/all", MenuItem.forPath("/s/listings/1a2b3c4d-5e6f"));
    }

    @Test
    public void editingAListingComesUnderMyListings() {
        assertEquals("/s/listings/mine", MenuItem.forPath("/s/listings/1a2b3c4d-5e6f/edit"));
    }

    @Test
    public void aPageOutsideTheMenuMatchesNoItem() {
        assertEquals("/index", MenuItem.forPath("/index"));
    }

    @Test
    public void thePathIsTakenWithinTheApplication() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/yottr/s/listings/abc");
        request.setContextPath("/yottr");

        assertTrue(new MenuItem().preHandle(request, new MockHttpServletResponse(), new Object()), "carries on");
        assertEquals("/s/listings/all", request.getAttribute(MenuItem.REQUEST_ATTRIBUTE));
    }
}
