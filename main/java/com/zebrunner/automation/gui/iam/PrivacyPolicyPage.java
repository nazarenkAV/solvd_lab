package com.zebrunner.automation.gui.iam;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class PrivacyPolicyPage extends AbstractPage {

    public static final String PAGE_NAME = "ZEBRUNNER PRIVACY POLICY";

    @FindBy(xpath = "//h1[@id='zebrunner-privacy-policy']")
    private ExtendedWebElement title;

    public PrivacyPolicyPage(WebDriver driver) {
        super(driver);
        setPageURL("/documentation/legal/privacy-policy/");
        setUiLoadedMarker(title);
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }

}
