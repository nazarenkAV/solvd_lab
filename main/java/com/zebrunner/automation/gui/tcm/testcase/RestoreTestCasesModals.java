package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class RestoreTestCasesModals extends AbstractModal<RestoreTestCasesModals> {

    public static final String RESTORE_ALL_TEST_CASE_MODAL_TITLE = "Restore all test cases?";
    public static final String RESTORE_TEST_CASE_MODAL_TITLE = "Restore test case?";

    @FindBy(id = "select-suite")
    private ExtendedWebElement selectSuiteInput;

    @FindBy(xpath = ".//button[text() = 'Restore']")
    private ExtendedWebElement restoreButton;

    public RestoreTestCasesModals(WebDriver driver) {
        super(driver);
    }

    public void selectSuite(String suiteTitle) {
        selectSuiteInput.click();
        SelectSuiteListBoxMenu selectSuiteListBoxMenu = new SelectSuiteListBoxMenu(getDriver());
        selectSuiteListBoxMenu.findItem(suiteTitle).click();
    }

    public void clickRestoreButton() {
        restoreButton.click();
    }

    public void typeParentSuite(String parentSuiteName) {
        selectSuiteInput.type(parentSuiteName);
    }
}
