package uk.co.yottr.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;

/**
 * Whether the signed-in member is an administrator, for the pages members and administrators share.
 */
public final class Administrators {

    private Administrators() {
    }

    public static boolean isAdministrator(Principal principal) {
        return principal instanceof Authentication authentication
                && authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(Role.ADMIN.name()::equals);
    }
}
