package uk.co.yottr.acceptance;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.security.Role;
import uk.co.yottr.service.UserService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.yottr.builder.UserBuilder.aUser;

public class MemberSteps {

    private static final String PASSWORD = "correct-horse-battery";

    @Autowired
    private Browser browser;

    @Autowired
    private UserService userService;

    // Features use short names such as "alice"; each gets a unique username, as every scenario shares the database.
    private final Map<String, String> usernames = new HashMap<>();

    String username(String name) {
        return usernames.computeIfAbsent(name, n -> n + "-" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Given("a member called {string}")
    public void aMemberCalled(String name) {
        createMember(name, 2, Role.FREE);
    }

    @Given("a member called {string} who may have {int} listings")
    public void aMemberWhoMayHaveListings(String name, int maxListings) {
        createMember(name, maxListings, Role.FREE);
    }

    @Given("an administrator called {string}")
    public void anAdministratorCalled(String name) {
        createMember(name, 2, Role.FREE, Role.ADMIN);
    }

    private void createMember(String name, int maxListings, Role... roles) {
        final User user = aUser().withUsername(username(name)).withMaxListings(maxListings).build();
        user.setPassword(PASSWORD);
        user.setEmail(name + "@example.test");
        for (Role role : roles) {
            user.addRole(role);
        }
        userService.save(user, true);
    }

    @When("I open the home page")
    public void iOpenTheHomePage() throws IOException {
        browser.open("/");
    }

    @When("I open {string}")
    public void iOpen(String path) throws IOException {
        browser.open(path);
    }

    @Given("{string} has signed in")
    @When("{string} signs in")
    public void signsIn(String name) throws IOException {
        signIn(username(name), PASSWORD);
    }

    @When("{string} signs in with the password {string}")
    public void signsInWithThePassword(String name, String password) throws IOException {
        signIn(username(name), password);
    }

    private void signIn(String username, String password) throws IOException {
        browser.open("/");
        final HtmlForm form = browser.form("main-sign-in-form");
        form.getInputByName("username").setValue(username);
        form.getInputByName("password").setValue(password);
        browser.showing(form.getInputByName("submit").click());
    }

    @When("I sign out")
    public void iSignOut() throws IOException {
        final HtmlElement logout = browser.page().getHtmlElementById("logout-link");
        browser.showing(logout.click());
    }

    @When("I delete my account")
    public void iDeleteMyAccount() throws IOException {
        browser.open("/s/myDetails");
        final HtmlButton deleteMe = browser.page().getFirstByXPath("//button[@form='delete-me-form']");
        browser.showing(deleteMe.click());
    }

    @When("I register as {string}")
    public void iRegisterAs(String name) throws IOException {
        browser.open("/signup");
        final HtmlForm form = browser.form("signup-form");
        form.getInputByName("username").setValue(username(name));
        form.getInputByName("password").setValue(PASSWORD);
        form.getInputByName("firstName").setValue(name);
        form.getInputByName("lastName").setValue("Sailor");
        form.getInputByName("email").setValue(name + "@example.test");
        form.getInputByName("mobile").setValue("07700 900123");
        form.getSelectByName("country").setSelectedAttribute(Country.UK.name(), true);
        form.getInputByName("postcode").setValue("PO1 1AA");
        final HtmlButton signUp = form.getFirstByXPath(".//button[@type='submit']");
        browser.showing(signUp.click());
    }

    @When("I search the members for {string}'s username")
    public void iSearchTheMembersFor(String name) throws IOException {
        // what the search form sends without JavaScript; the username is unique, so the member is on the first page
        browser.open("/admin/users?text=" + URLEncoder.encode(username(name), StandardCharsets.UTF_8));
    }

    @Then("the members found are {string}")
    public void theMembersFoundAre(String name) {
        final List<String> found = browser.page().<HtmlElement>getByXPath("//div[@id='results']//tbody/tr/td[1]").stream()
                .map(HtmlElement::asNormalizedText)
                .toList();
        assertThat(found).containsExactly(username(name));
    }

    @Then("the menu shows I am in {string}")
    public void theMenuShowsIAmIn(String item) {
        final List<String> current = browser.page().<HtmlElement>getByXPath("//nav[@aria-label='Main']//a[@aria-current='page']").stream()
                .map(HtmlElement::asNormalizedText)
                .toList();
        assertThat(current).containsExactly(item);
    }

    @Then("I see {string}")
    public void iSee(String text) {
        assertThat(browser.page().asNormalizedText()).contains(text);
    }

    @Then("I am on the sign-in page")
    public void iAmOnTheSignInPage() {
        assertThat(browser.path()).isEqualTo("/index");
        assertThat(browser.page().getElementById("main-sign-in-form")).isNotNull();
    }

    @Then("access is refused")
    public void accessIsRefused() {
        assertThat(browser.statusCode()).isEqualTo(403);
    }
}
