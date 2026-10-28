package uk.co.yottr.journey;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import uk.co.yottr.model.Country;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Someone using the site, with a browser of their own. HtmlUnit submits the real forms over HTTP, so sessions,
 * sign-in and the security rules all apply. JavaScript is off: the pages work without it.
 * <p>
 * The actions go through the pages the way a person would, and find things by what the person sees: a listing by
 * its make and model, a member by their username.
 */
class Person {

    static final String PASSWORD = "correct-horse-battery";

    private final String name;
    private final String username;
    private final String site;
    private final WebClient browser;
    private String password = PASSWORD;
    private HtmlPage page;

    Person(String name, String username, String site) {
        this.name = name;
        this.username = username;
        this.site = site;
        this.browser = new WebClient();
        browser.getOptions().setJavaScriptEnabled(false);
        browser.getOptions().setCssEnabled(false);
        // the journeys check refusals and errors themselves
        browser.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    String username() {
        return username;
    }

    // --- what they see

    /**
     * The text of the page they're looking at.
     */
    String sees() {
        return page.asNormalizedText();
    }

    String currentMenuItem() {
        final List<String> current = page.<HtmlElement>getByXPath("//nav[@aria-label='Main']//a[@aria-current='page']").stream()
                .map(HtmlElement::asNormalizedText)
                .toList();
        assertThat(current).as("the menu items marked current").hasSize(1);
        return current.get(0);
    }

    boolean isSignedIn() {
        return page.getElementById("logout-link") != null;
    }

    // --- joining and signing in

    void registers() {
        open("/signup");
        final HtmlForm form = form("signup-form");
        form.getInputByName("username").setValue(username);
        form.getInputByName("password").setValue(password);
        form.getInputByName("firstName").setValue(name);
        form.getInputByName("lastName").setValue("Sailor");
        form.getInputByName("email").setValue(username + "@example.test");
        form.getInputByName("mobile").setValue("07700 900123");
        form.getSelectByName("country").setSelectedAttribute(Country.UK.name(), true);
        form.getInputByName("postcode").setValue("PO1 1AA");
        submit(form);
    }

    void signsIn() {
        signsInWith(password);
    }

    void signsInWith(String password) {
        open("/");
        final HtmlForm form = form("main-sign-in-form");
        form.getInputByName("username").setValue(username);
        form.getInputByName("password").setValue(password);
        show(click(form.getInputByName("submit")));
    }

    void signsOut() {
        show(click(page.getHtmlElementById("logout-link")));
    }

    /**
     * Loads the page they're on again.
     */
    void refreshes() {
        open(page.getUrl().getPath() + (page.getUrl().getQuery() == null ? "" : "?" + page.getUrl().getQuery()));
    }

    void goesTo(String menuItem) {
        open("/");
        final HtmlAnchor link = page.getFirstByXPath("//nav[@aria-label='Main']//a[normalize-space()='" + menuItem + "']");
        assertThat(link).as("the " + menuItem + " menu item").isNotNull();
        show(click(link));
    }

    // --- their account

    void changesTheirPasswordTo(String newPassword) {
        goesTo("My Details");
        final HtmlForm form = form("signup-form");
        form.getInputByName("password").setValue(newPassword);
        submit(form);
        assertThat(sees()).contains("Your details are saved.");
        password = newPassword;
    }

    void closesTheirAccount() {
        goesTo("My Details");
        show(click(page.getFirstByXPath("//button[@form='delete-me-form']")));
    }

    // --- their listings

    void startsANewListing() {
        open("/s/listings/new");
    }

    void postsAListingFor(String makeAndModel) {
        startsANewListing();
        final HtmlForm form = form("listing-form");
        form.getInputByName("title").setValue("Crew wanted for a " + makeAndModel);
        form.getInputByName("makeAndModel").setValue(makeAndModel);
        form.getInputByName("length").setValue("33");
        form.getSelectByName("vesselType").setSelectedAttribute("SAIL", true);
        form.getInputByName("yearBuilt").setValue("1987");
        form.getTextAreaByName("description").setText("Relaxed weekend cruising.");
        form.getSelectByName("sailingPurpose").setSelectedAttribute("CRUISING", true);
        form.getSelectByName("roleRequired").setSelectedAttribute("CREW", true);
        form.<HtmlCheckBoxInput>getFirstByXPath(".//input[@name='frequency'][@value='WEEKENDS']").setChecked(true);
        form.getInputByName("dateRelevantTo").setValue("14/06/2027");
        form.getSelectByName("financialArrangement").setSelectedAttribute("SHARED_FIXED", true);
        submit(form);
    }

    /**
     * Opens the edit page for their listing, and changes its make and model.
     */
    void changesTheMakeOfTheirListing(String makeAndModel, String newMakeAndModel) {
        show(click(onMyListing(makeAndModel, "//a[normalize-space()='Edit']")));
        final HtmlForm form = form("listing-form");
        form.getInputByName("makeAndModel").setValue(newMakeAndModel);
        submit(form);
    }

    void suspendsTheirListing(String makeAndModel) {
        show(click(onMyListing(makeAndModel, "//button[normalize-space()='Suspend']")));
    }

    void reEnablesTheirListing(String makeAndModel) {
        show(click(onMyListing(makeAndModel, "//button[normalize-space()='Re-enable']")));
    }

    void deletesTheirListing(String makeAndModel) {
        show(click(onMyListing(makeAndModel, "//form[contains(@action, '/delete')]//button")));
    }

    /**
     * The make and model of each of their listings, on My Listings.
     */
    List<String> myListings() {
        goesTo("My Listings");
        return makesOnThePage();
    }

    /**
     * What My Listings shows of one of their listings.
     */
    String myListing(String makeAndModel) {
        goesTo("My Listings");
        return listingCard(makeAndModel).asNormalizedText();
    }

    private HtmlElement onMyListing(String makeAndModel, String xpath) {
        goesTo("My Listings");
        final HtmlElement element = listingCard(makeAndModel).getFirstByXPath("." + xpath);
        assertThat(element).as(xpath + " on the " + makeAndModel + " listing").isNotNull();
        return element;
    }

    // --- finding a boat

    /**
     * Searches from the box on the home page, and returns the make and model of each boat found.
     */
    List<String> searchesTheBoatsFor(String text) {
        open("/");
        final HtmlForm form = form("home-search-form");
        form.getInputByName("text").setValue(text);
        submit(form);
        return makesOnThePage();
    }

    /**
     * Opens a listing from All Boats, which shows the newest first.
     */
    void opensTheListingFor(String makeAndModel) {
        goesTo("Boats");
        final HtmlAnchor title = listingCard(makeAndModel).getFirstByXPath(".//h2/a");
        show(click(title));
    }

    // --- looking after members

    /**
     * Searches the members for their usernames, and returns the usernames found.
     */
    List<String> findsMembers(String text) {
        open("/admin/users?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8));
        return page.<HtmlElement>getByXPath("//div[@id='results']//tbody/tr/td[1]").stream()
                .map(HtmlElement::asNormalizedText)
                .toList();
    }

    void changesTheLastNameOf(Person member, String lastName) {
        editsTheDetailsOf(member);
        final HtmlForm form = form("member-details-form");
        form.getInputByName("lastName").setValue(lastName);
        submit(form);
    }

    void changesTheListingLimitOf(Person member, int maxListings) {
        editsTheDetailsOf(member);
        final HtmlForm form = form("member-details-form");
        form.getInputByName("maxListings").setValue(String.valueOf(maxListings));
        submit(form);
    }

    void disables(Person member) {
        show(click(onMembersRow(member, "//button[normalize-space()='Disable']")));
    }

    void enables(Person member) {
        show(click(onMembersRow(member, "//button[normalize-space()='Enable']")));
    }

    private void editsTheDetailsOf(Person member) {
        show(click(onMembersRow(member, "//a[normalize-space()='Edit']")));
    }

    private HtmlElement onMembersRow(Person member, String xpath) {
        assertThat(findsMembers(member.username())).containsExactly(member.username());
        final HtmlElement element = page.getFirstByXPath("//div[@id='results']//tbody/tr[1]" + xpath);
        assertThat(element).as(xpath + " for " + member.username()).isNotNull();
        return element;
    }

    // --- moderating listings

    /**
     * Opens one of a member's listings from their details, which works whether or not other members can see it.
     */
    void opensTheListingOf(Person owner, String makeAndModel) {
        editsTheDetailsOf(owner);
        show(click(page.getFirstByXPath("//dd//a[normalize-space()='Crew wanted for a " + makeAndModel + "']")));
    }

    void withdrawsTheListing() {
        moderates("Withdraw");
    }

    void putsTheListingBack() {
        moderates("Put back");
    }

    void deletesTheListing() {
        moderates("Delete");
    }

    private void moderates(String action) {
        final HtmlButton button = page.getFirstByXPath("//aside[@id='moderation']//button[normalize-space()='" + action + "']");
        assertThat(button).as("the " + action + " button").isNotNull();
        show(click(button));
    }

    // --- running the site

    void setsTheLogLevel(String logger, String level) {
        goesTo("Logging");
        final HtmlForm form = form("set-level-form");
        form.getInputByName("name").setValue(logger);
        form.getSelectByName("level").setSelectedAttribute(level, true);
        show(click(page.getHtmlElementById("set-level-button")));
    }

    /**
     * The level set for the logger, and the level it has in effect, or "-" where there is none.
     */
    List<String> logLevelOf(String logger) {
        open("/admin/logging?filter=" + URLEncoder.encode(logger, StandardCharsets.UTF_8));
        final HtmlElement row = page.getHtmlElementById("logger-" + logger);
        return List.of(cell(row, "configured-level"), cell(row, "effective-level"));
    }

    private static String cell(HtmlElement row, String cssClass) {
        return row.<HtmlElement>getFirstByXPath(".//td[@class='" + cssClass + "']").asNormalizedText();
    }

    // --- the browser

    private void open(String path) {
        try {
            page = browser.getPage(site + path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private HtmlForm form(String id) {
        return page.getHtmlElementById(id);
    }

    private void submit(HtmlForm form) {
        show(click(form.getFirstByXPath(".//button[@type='submit']")));
    }

    private static HtmlPage click(HtmlElement element) {
        try {
            return element.click();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void show(HtmlPage page) {
        this.page = page;
    }

    private HtmlElement listingCard(String makeAndModel) {
        final HtmlElement card = page.getFirstByXPath(
                "//article[.//span[@class='yottr-listing-make'][normalize-space()='" + makeAndModel + "']]");
        assertThat(card).as("the " + makeAndModel + " listing").isNotNull();
        return card;
    }

    private List<String> makesOnThePage() {
        return page.<HtmlElement>getByXPath("//span[@class='yottr-listing-make']").stream()
                .map(HtmlElement::asNormalizedText)
                .toList();
    }

    void closeBrowser() {
        browser.close();
    }
}
