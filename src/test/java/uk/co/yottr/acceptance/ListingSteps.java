package uk.co.yottr.acceptance;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListingSteps {

    // the listing forms reuse the sign-up form's id
    private static final String LISTING_FORM_ID = "listing-form";

    @Autowired
    private Browser browser;

    @Given("I have posted a listing for a {string}")
    @When("I post a listing for a {string}")
    public void iPostAListingFor(String makeAndModel) throws IOException {
        browser.open("/s/listings/new");
        final HtmlForm form = browser.form(LISTING_FORM_ID);
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
        save(form);
    }

    @When("I change the manufacturer of my {string} listing to {string}")
    public void iChangeTheManufacturer(String makeAndModel, String newMakeAndModel) throws IOException {
        browser.open("/s/listings/mine");
        final HtmlAnchor edit = browser.page().getFirstByXPath(
                "//article[.//span[@class='yottr-listing-make'][normalize-space()='" + makeAndModel + "']]//a[normalize-space()='Edit']");
        assertThat(edit).as("Edit link for the " + makeAndModel + " listing").isNotNull();
        browser.showing(edit.click());

        final HtmlForm form = browser.form(LISTING_FORM_ID);
        form.getInputByName("makeAndModel").setValue(newMakeAndModel);
        save(form);
    }

    @When("I delete my {string} listing")
    public void iDeleteMyListing(String makeAndModel) throws IOException {
        browser.open("/s/listings/mine");
        final HtmlButton delete = browser.page().getFirstByXPath(
                "//article[.//span[@class='yottr-listing-make'][normalize-space()='" + makeAndModel + "']]//form[contains(@action, '/delete')]//button");
        assertThat(delete).as("Delete button for the " + makeAndModel + " listing").isNotNull();
        browser.showing(delete.click());
    }

    private void save(HtmlForm form) throws IOException {
        final HtmlButton save = form.getFirstByXPath(".//button[@type='submit']");
        browser.showing(save.click());
    }

    @Then("my listings include a {string}")
    public void myListingsInclude(String makeAndModel) throws IOException {
        assertThat(myListingsMakes()).contains(makeAndModel);
    }

    @Then("my listings do not include a {string}")
    public void myListingsDoNotInclude(String makeAndModel) throws IOException {
        assertThat(myListingsMakes()).doesNotContain(makeAndModel);
    }

    /**
     * The make and model of each listing on My Listings.
     */
    private List<String> myListingsMakes() throws IOException {
        return browser.open("/s/listings/mine").<HtmlElement>getByXPath("//span[@class='yottr-listing-make']").stream()
                .map(HtmlElement::asNormalizedText)
                .toList();
    }
}
