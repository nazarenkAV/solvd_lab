package com.zebrunner.automation.gui.landing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.gui.Element;

public class AboutUsPage extends LandingBasePage {

    public static final String PAGE_NAME = "About us";

    @FindBy(xpath = "//div[@class='tamp-try-zebrunner-wrapper']")
    private Element btnStartTrial;

    public AboutUsPage(WebDriver driver) {
        super(driver);
        setPageAbsoluteURL(APIContextManager.LANDING_URL + "/about-us");
    }

    public RegisterPage clickStartTrial() {
        btnStartTrial.click();
        return new RegisterPage(getDriver());
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }
}
