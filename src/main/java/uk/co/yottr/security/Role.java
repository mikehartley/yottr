package uk.co.yottr.security;

/**
 * Created by mike on 20/08/14.
 */
public enum Role {
    FREE,
    CREW,
    ADMIN,
    // an account made for trying the site out, which the one-click sign-in may use (see TestUsers); it grants nothing
    TESTER
}
