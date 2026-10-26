package uk.co.yottr.acceptance;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggingSteps {

    @Autowired
    private Browser browser;

    @When("I set the log level of {string} to {string}")
    public void iSetTheLogLevel(String logger, String level) throws IOException {
        setLevel(logger, level);
    }

    @When("I reset the log level of {string}")
    public void iResetTheLogLevel(String logger) throws IOException {
        setLevel(logger, "");
    }

    private void setLevel(String logger, String level) throws IOException {
        browser.open("/admin/logging");
        final HtmlForm form = browser.form("set-level-form");
        form.getInputByName("name").setValue(logger);
        form.getSelectByName("level").setSelectedAttribute(level, true);
        final HtmlButton set = browser.page().getHtmlElementById("set-level-button");
        browser.showing(set.click());
    }

    @Then("the log level of {string} is {string}")
    public void theLogLevelIs(String logger, String level) throws IOException {
        final HtmlElement row = loggerRow(logger);
        assertThat(cell(row, "configured-level")).isEqualTo(level);
        assertThat(cell(row, "effective-level")).isEqualTo(level);
    }

    @Then("{string} has no log level of its own")
    public void hasNoLogLevelOfItsOwn(String logger) throws IOException {
        assertThat(cell(loggerRow(logger), "configured-level")).isEqualTo("-");
    }

    private HtmlElement loggerRow(String logger) throws IOException {
        browser.open("/admin/logging?filter=" + URLEncoder.encode(logger, StandardCharsets.UTF_8));
        return browser.page().getHtmlElementById("logger-" + logger);
    }

    private static String cell(HtmlElement row, String cssClass) {
        return row.<HtmlElement>getFirstByXPath(".//td[@class='" + cssClass + "']").asNormalizedText();
    }
}
