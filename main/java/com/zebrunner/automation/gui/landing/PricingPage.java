package com.zebrunner.automation.gui.landing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.gui.Element;

public class PricingPage extends LandingBasePage {

    public static final String PAGE_NAME = "Pricing | Zebrunner";

    @FindBy(xpath = "//div[text()='Chat with us']")
    private Element btnChatWithUs;

    public PricingPage(WebDriver driver) {
        super(driver);
        setPageAbsoluteURL(APIContextManager.LANDING_URL + "/pricing");
    }

    public RequestDemoPage clickChatWithUs() {
        btnChatWithUs.click();
        return new RequestDemoPage(getDriver());
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }
}
