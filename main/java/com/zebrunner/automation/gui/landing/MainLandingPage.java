package com.zebrunner.automation.gui.landing;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class MainLandingPage extends LandingBasePage {

    public static final String PAGE_NAME = "Test automation management | Zebrunner";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public MainLandingPage(WebDriver driver) {
        super(driver);
    }

    public static MainLandingPage openPage(WebDriver driver) {
        LOGGER.info("Attempt to go to the page '{}'", PAGE_NAME);
        MainLandingPage mainLandingPage = new MainLandingPage(driver);
        mainLandingPage.pause(2);
        return mainLandingPage;
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }

}
