package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

//v-accordion[@class='test-sessions__accordion ng-scope ng-isolate-scope']
public class TestSessionR extends AbstractUIObject {
    @FindBy(xpath = ".//div[@class='test-sessions__header-title ng-binding']")
    private ExtendedWebElement tittle;

    @FindBy(xpath = ".//div[@class='test-sessions__header-metadata']//div[contains(@ng-if,'platformName')]")
    private ExtendedWebElement titleOsPlatformIcon;

    @FindBy(xpath = ".//div[@class='test-sessions__header-metadata']//div[contains(@ng-if,'browserIcon')]//span[contains(@class,'platform')]")
    private ExtendedWebElement titleBrowserPlatformIcon;

    @FindBy(xpath = ".//div[@class='test-sessions__header-metadata']//div[contains(@ng-if,'browserIcon')]//span[contains(@ng-if,'browserVersion')]")
    private ExtendedWebElement titleBrowserVersion;

    @FindBy(xpath = ".//i[@class='test-sessions__header-icon material-icons']")
    private ExtendedWebElement expandButton;

    @FindBy(xpath = ".//div[@class='test-session-info__video ng-scope']")
    private ExtendedWebElement video;

    @FindBy(xpath = ".//span[text()='Platform']")
    private ExtendedWebElement platformInfoField;

    @FindBy(xpath = ".//span[text()='Browser']")
    private ExtendedWebElement browserInfoField;

    @FindBy(xpath = ".//span[text()='Started']")
    private ExtendedWebElement startedInfoField;

    @FindBy(xpath = ".//span[text()='Duration']")
    private ExtendedWebElement durationInfoField;

    @FindBy(xpath = "//div[text()='Artifacts']")
    private ExtendedWebElement artifactsField;

    @FindBy(xpath = "//div[@class='test-session-info__meta-section _left']//span[contains(@ng-if,'platformName')]")
    private ExtendedWebElement osPlatformIcon;

    @FindBy(xpath = "//div[@class='test-session-info__meta-section _left']//span[contains(@ng-if,'browserIcon')]")
    private ExtendedWebElement browserPlatformIcon;

    @FindBy(xpath = "//div[@class='test-session-info__meta-section _left']//span[contains(@ng-if,'browserVersion')]")
    private ExtendedWebElement BrowserPlatformVersion;

    @FindBy(xpath = "//div[@class='test-session-info__meta-section _right']//span[contains(@class,'test-session-info__meta-item-value-text ng-binding')]")
    private ExtendedWebElement startedAgo;

    @FindBy(xpath = "//div[@class='test-session-info__meta-section _right']//duration[contains(@name,'sessionDuration')]")
    private ExtendedWebElement duration;

    @FindBy(xpath = "//ul[@class='test-session-info__artifacts-list']//li[contains(@class,'test-session-info__artifacts-item ng-scope')]")
    private List<ExtendedWebElement> artifacts;

    public TestSessionR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }
}
