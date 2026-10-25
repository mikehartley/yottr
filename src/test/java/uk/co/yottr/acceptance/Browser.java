package uk.co.yottr.acceptance;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * A headless browser for one scenario. HtmlUnit submits the real forms over HTTP, so sessions, sign-in
 * and the security rules all apply. JavaScript is off: the pages work without it.
 */
public class Browser {

    @Value("${local.server.port}")
    private int port;

    private WebClient webClient;
    private HtmlPage page;

    @Before
    public void open() {
        webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        // let the steps check refusals and errors themselves
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    @After
    public void close() {
        webClient.close();
    }

    public HtmlPage open(String path) throws IOException {
        page = webClient.getPage("http://localhost:" + port + path);
        return page;
    }

    public HtmlPage page() {
        return page;
    }

    public void showing(HtmlPage page) {
        this.page = page;
    }

    public int statusCode() {
        return page.getWebResponse().getStatusCode();
    }

    public String path() {
        return page.getUrl().getPath();
    }

    public HtmlForm form(String id) {
        return page.getHtmlElementById(id);
    }
}
