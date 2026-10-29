package uk.co.yottr.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.co.yottr.model.Sides;
import uk.co.yottr.repository.UserRepository;

import java.security.Principal;

/**
 * What the signed-in member is on Yottr as, so the pages show them only what's relevant: owners' things (My
 * Listings, Add a boat, Find crew) to owners, and crew's (Saved, Crewing, asking to join a crew) to crew. It goes to
 * the pages as the request attribute "sides", a Sides, absent when no one is signed in; as a request attribute, like
 * MenuItem's, it leaves the controllers' models alone. It's looked up on each page, so a change in My Details
 * shows at once.
 */
@Component
public class MemberSides implements HandlerInterceptor {

    static final String REQUEST_ATTRIBUTE = "sides";

    private final UserRepository userRepository;

    public MemberSides(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Principal principal = request.getUserPrincipal();
        if (principal != null) {
            final Sides sides = userRepository.findSides(principal.getName());
            if (sides != null) {
                request.setAttribute(REQUEST_ATTRIBUTE, sides);
            }
        }
        return true;
    }
}
