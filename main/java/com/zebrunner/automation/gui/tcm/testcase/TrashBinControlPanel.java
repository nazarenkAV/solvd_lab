package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.ZbrSearch;
import com.zebrunner.automation.gui.tcm.testrun.TestCaseFilterBlock;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class TrashBinControlPanel extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[@class = 'trash-bin-control-panel']";

    @FindBy(xpath = ".//span[text() = 'Restore all']//ancestor::button[contains(@class, 'trash-bin')]")
    private ExtendedWebElement restoreAllButton;

    @FindBy(xpath = ".//span[text() = 'Purge all']//ancestor::button[contains(@class, 'trash-bin')]")
    private ExtendedWebElement purgeAllButton;

    @FindBy(xpath = ZbrSearch.ROOT_XPATH)
    private ZbrSearch searchCasesTextField;

    @FindBy(xpath = ".//*[@class = 'Zbr-reset-button']")
    private ExtendedWebElement searchResetButton;

    @FindBy(xpath = ".//*[contains(text(),'Filter')]")
    private ExtendedWebElement filterButton;

    @FindBy(xpath = ".//*[@class = 'Zbr-reset-button']")
    private ExtendedWebElement filterResetButton;

    public TrashBinControlPanel(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isRestoreAllButtonClickable() {
        return restoreAllButton.isClickable(2);
    }

    public boolean isPurgeAllButtonClickable() {
        return purgeAllButton.isClickable(2);
    }

    public RestoreTestCasesModals clickRestoreAllButton() {
        restoreAllButton.click();
        return new RestoreTestCasesModals(getDriver());
    }

    public PurgeTestCasesModals clickPurgeAllButton() {
        purgeAllButton.click();
        return new PurgeTestCasesModals(getDriver());
    }

    public void searchTestCase(String testCase) {
        searchCasesTextField.search(testCase);
    }

    public void clickSearchResetButton() {
        searchResetButton.click();
    }

    public boolean isFilterButtonClickable() {
        return filterButton.isClickable(2);
    }

    public TestCaseFilterBlock clickFilterButton() {
        filterButton.click();
        return new TestCaseFilterBlock(getDriver());
    }

    public void clickFilterResetButton() {
        filterResetButton.click();
    }
}
