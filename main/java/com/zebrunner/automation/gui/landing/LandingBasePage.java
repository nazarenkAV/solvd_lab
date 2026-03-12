package com.zebrunner.automation.gui.landing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.carina.webdriver.gui.AbstractPage;

@Getter
public abstract class LandingBasePage extends AbstractPage {

    @FindBy(xpath = "//div[@class='header-wrapper']")
    private LandingHeader landingHeader;

    public LandingBasePage(WebDriver driver) {
        super(driver);
        setPageAbsoluteURL(APIContextManager.LANDING_URL);
    }

}
