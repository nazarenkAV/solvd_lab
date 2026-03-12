package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class GettingStartedPage extends TenantProjectBasePage {

    @FindBy(xpath = "//h1[text() = 'Getting started']")
    private ExtendedWebElement pageTitle;

    @FindBy(xpath = "//a[text() = 'reporting API guide.']")
    private ExtendedWebElement reportingApiGuideLink;

    @FindBy(xpath = "//a[text() = 'docs']")
    private ExtendedWebElement reportingConceptsLink;

    public GettingStartedPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(pageTitle);
    }

    public void clickReportingApiGuideLink() {
        reportingApiGuideLink.click();
    }

    public void clickReportingConceptsLink() {
        reportingConceptsLink.click();
    }
}