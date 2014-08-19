package uk.co.yottr.tempDatastore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.User;

import java.util.*;

/**
 * For in-memory testing when a database isn't required.
 */
public class Database {
    public static final String ROLE_ADMIN = "Admin";

    private static Map<String, User> users = new HashMap<>();
    private static Collection<Boat> boats = new ArrayList<>();

    static {
        initialiseUsers();
        initialiseBoats();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Collection<Boat> getBoats() {
        return boats;
    }

    private static void initialiseUsers() {
        addUser("mike");
    }

    private static void initialiseBoats() {
        boats.add(createBoat("Y0001", "Halberg Rassy", "HR42", 42, Boat.HullType.MONO, "Looking for some crew to do some sailing."));
        boats.add(createBoat("Y0002", "Jaguar", "SeaCat", 55, Boat.HullType.MULTI, "I just like to motor about then have some lunch."));
    }

    private static Boat createBoat(String reference, String manufacturer, String model, int length,
                            Boat.HullType hullType, String description) {
        Boat boat = new Boat();
        boat.setReference(reference);
        boat.setManufacturer(manufacturer);
        boat.setModel(model);
        boat.setLength(length);
        boat.setHullType(hullType);
        boat.setDesc(description);
        return boat;
    }

    private static void addUser(final String usernamePassword) {
        User newUser = new User() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public String getUsername() {
                return usernamePassword;
            }

            @Override
            public String getPassword() {
                return usernamePassword;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
                return authorities;
            }
        };

        users.put(usernamePassword, newUser);
    }
}
