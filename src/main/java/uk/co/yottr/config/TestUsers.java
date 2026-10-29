package uk.co.yottr.config;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Members to sign in as with one click, for trying the site out locally. They come from test-users.properties in
 * the project root, which isn't committed and is read only with the dev profile (application-dev.properties), so
 * anywhere else there are none and the home page shows nothing of them.
 */
@Component
public class TestUsers {

    public record TestUser(String username, String password, String description) {
    }

    private final List<TestUser> users;

    public TestUsers(Environment environment) {
        this.users = Binder.get(environment).bind("yottr.test-users", Bindable.listOf(TestUser.class)).orElse(List.of());
    }

    public List<TestUser> all() {
        return users;
    }
}
