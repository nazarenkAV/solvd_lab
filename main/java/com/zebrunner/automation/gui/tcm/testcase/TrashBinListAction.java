package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.ZbrCheckbox;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class TrashBinListAction extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[@class = 'trash-bin-list__actions']";

    @FindBy(xpath = ".//span[text() = 'Restore']/parent::button")
    private ExtendedWebElement restoreButton;

    @FindBy(xpath = ".//span[text() = 'Purge']/parent::button")
    private ExtendedWebElement purgeButton;

    @FindBy(xpath = ZbrCheckbox.ROOT_XPATH)
    private ZbrCheckbox allCheckBoxButton;

    public TrashBinListAction(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isRestoreButtonPresent() {
        return restoreButton.isElementPresent(2);
    }

    public boolean isPurgeButtonPresent() {
        return purgeButton.isElementPresent(2);
    }

    public RestoreTestCasesModals clickRestoreButton() {
        restoreButton.click();
        return new RestoreTestCasesModals(getDriver());
    }

    public PurgeTestCasesModals clickPurgeButton() {
        purgeButton.click();
        return new PurgeTestCasesModals(getDriver());
    }
}
