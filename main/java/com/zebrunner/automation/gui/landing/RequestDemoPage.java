package com.zebrunner.automation.gui.landing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.gui.Element;

public class RequestDemoPage extends LandingBasePage {

    public static final String PAGE_NAME = "Request A Demo";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @FindBy(xpath = "//input[@name='name']")
    private Element nameInput;

    @FindBy(xpath = "//input[@name='email']")
    private Element emailInput;

    @FindBy(xpath = "//input[@type='submit']")
    private Element btnSubmit;

    public RequestDemoPage(WebDriver driver) {

        super(driver);
        setPageAbsoluteURL(APIContextManager.LANDING_URL + "/request-a-demo");
    }

    public static RequestDemoPage openPage(WebDriver driver) {
        LOGGER.info("Attempt to go to the page '{}'", PAGE_NAME);
        RequestDemoPage mainLandingPage = new RequestDemoPage(driver);
        mainLandingPage.pause(2);
        return mainLandingPage;
    }

    public void requestDemo(String name, String email) {
        LOGGER.info("Request a Demo");
        nameInput.sendKeys(name);
        emailInput.sendKeys(email);
        btnSubmit.click();
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }
}
