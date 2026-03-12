package com.zebrunner.automation.gui.iam;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TermsOfServicePage extends AbstractPage {

    public static final String PAGE_NAME = "ZEBRUNNER CLOUD TERMS OF SERVICE";

    @FindBy(xpath = "//h1[@id='zebrunner-cloud-terms-of-service']")
    private ExtendedWebElement title;

    public TermsOfServicePage(WebDriver driver) {
        super(driver);
        setPageURL("/documentation/legal/terms-of-service/");
        setUiLoadedMarker(title);
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }

}