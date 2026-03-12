package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.reporting.launch.CardUpdateModalR;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//div[@class='page-header test-details-header']
@Getter
public class TestDetailsPageHeader extends AbstractUIObject {

    @FindBy(xpath = ".//h1")
    private Element testTitle;

    @FindBy(xpath = ".//div[@class='environment-label']")
    private Element env;

    @FindBy(xpath = "//*[@class = 'test-details__nav-and-menu']//button[contains(@class, 'MuiButtonBase-root')]")
    private Element headerSettings;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown dropdown;

    @FindBy(xpath = TestNavigation.ROOT_XPATH)
    private TestNavigation testNavigation;

    public TestDetailsPageHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CardUpdateModalR clickMarkAsFiled() {
        headerSettings.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.MARK_AS_FAILED.getItemValue()).click();
        return new CardUpdateModalR(driver);
    }

    public void clickDownloadArtifacts() {
        headerSettings.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.DOWNLOAD_ARTIFACT.getItemValue()).click();
    }

    public CardUpdateModalR clickMarkAsPassed() {
        headerSettings.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.MARK_AS_PASSED.getItemValue()).click();
        return new CardUpdateModalR(driver);
    }

    public String getEnv() {
        return env.getText();
    }

    public String getTestTitleText() {
        return testTitle.getText();
    }
}