package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class PurgeTestCasesModals extends AbstractModal<PurgeTestCasesModals> {

    public static final String PURGE_ALL_TEST_CASES_MODAL_TITLE = "Purge all test cases?";
    public static final String PURGE_TEST_CASE_MODAL_TITLE = "Purge test case?";

    @FindBy(xpath = ".//button[text() = 'Purge']")
    private ExtendedWebElement purgeButton;

    public PurgeTestCasesModals(WebDriver driver) {
        super(driver);
    }

    public void clickPurgeButton() {
        purgeButton.click();
    }
}
