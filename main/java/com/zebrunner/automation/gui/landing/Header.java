package com.zebrunner.automation.gui.landing;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class Header extends AbstractUIObject {

    @FindBy(xpath = ".//a[title='Documentation']")
    private Element logo;

    public Header(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public MainLandingPage toLandingHomePage() {
        logo.click();
        return MainLandingPage.openPage(getDriver());
    }
}
