package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class EditTestRunPage extends CreateTestRunPage {
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/test-runs/%s/edit";

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[contains(text(),'SAVE')]")
    private ExtendedWebElement saveButton;

    public EditTestRunPage(WebDriver driver) {
        super(driver);
    }

    public TestRunPage clickSaveButton() {
        saveButton.click();
        return TestRunPage.getPageInstance(getDriver());
    }
}
