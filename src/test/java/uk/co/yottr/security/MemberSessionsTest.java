package uk.co.yottr.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberSessionsTest {

    private SessionRegistry sessionRegistry;
    private MemberSessions memberSessions;

    @BeforeEach
    public void init() {
        sessionRegistry = new SessionRegistryImpl();
        memberSessions = new MemberSessions(sessionRegistry);
    }

    @Test
    public void expiresEverySessionOfTheMember() {
        sessionRegistry.registerNewSession("alice-laptop", member("alice"));
        sessionRegistry.registerNewSession("alice-phone", member("alice"));

        memberSessions.expire("alice");

        assertTrue(session("alice-laptop").isExpired(), "laptop session expired");
        assertTrue(session("alice-phone").isExpired(), "phone session expired");
    }

    @Test
    public void leavesOtherMembersSignedIn() {
        sessionRegistry.registerNewSession("alice-laptop", member("alice"));
        sessionRegistry.registerNewSession("bob-laptop", member("bob"));

        memberSessions.expire("alice");

        assertFalse(session("bob-laptop").isExpired(), "bob's session");
    }

    @Test
    public void aMemberWithNoSessionsIsFine() {
        memberSessions.expire("nobody");
    }

    private SessionInformation session(String sessionId) {
        return sessionRegistry.getSessionInformation(sessionId);
    }

    private static User member(String username) {
        return new User(username, "password", List.of());
    }
}
