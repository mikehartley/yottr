package uk.co.yottr.acceptance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class SignInSignOutIT {

    private WebDriver driver;

    final String urlBase = Constants.URL_BASE;

    @Before
    public void beforeEveryTest() {
        driver = new FirefoxDriver();
    }

    @After
    public void afterEveryTest() {
        driver.quit();
    }

    @Test
    public void errorMessageAfterFailedLoginAttempt() {
        driver.navigate().to(urlBase + "/yottr");

        final WebElement usernameField = driver.findElement(By.id("username"));
        final WebElement passwordField = driver.findElement(By.id("password"));
        final WebElement signInButton = driver.findElement(By.id("sign-in-button"));

        usernameField.sendKeys("mike");
        passwordField.sendKeys("wrongpassword");

        signInButton.click();

        final WebElement errorMessage = driver.findElement(By.id("error-msg"));

        assertEquals("Invalid username and password.", errorMessage.getText());
    }

    @Test
    public void signInSignOut() {
        driver.navigate().to(urlBase + "/yottr");

        final WebElement usernameField = driver.findElement(By.id("username"));
        final WebElement passwordField = driver.findElement(By.id("password"));
        final WebElement signInButton = driver.findElement(By.id("sign-in-button"));

        usernameField.sendKeys("mike");
        passwordField.sendKeys("aph3xtwIn");

        signInButton.click();

        final WebElement logoutLink = driver.findElement(By.id("logout-link"));
        assertEquals("Logout", logoutLink.getText());

        logoutLink.click();

        final WebElement loggedOutMessage = driver.findElement(By.id("logged-out-msg"));
        assertEquals("You have been logged out.", loggedOutMessage.getText());
    }
}