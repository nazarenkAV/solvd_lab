package com.zebrunner.automation.gui.tcm.testrun.testcase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.common.ZbrSearch;
import com.zebrunner.automation.gui.tcm.testrun.CreateTestRunPage;
import com.zebrunner.automation.gui.tcm.testrun.TestCaseFilterBlock;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

public class SelectTestCasesModal extends AbstractModal<SelectTestCasesModal> {

    @FindBy(xpath = ZbrSearch.ROOT_XPATH)
    private ZbrSearch searchField;

    @FindBy(xpath = ".//div[contains(@class, 'select-test-cases-modal__suite') and contains(@class, 'MuiBox-root')]")
    private List<ModalSuiteItem> testSuites;

    @FindBy(xpath = ".//*[@d='M16.637 12.863a.9.9 0 0 1 0 1.273l-4 4a.9.9 0 0 1-1.273 0l-4-4a.9.9 0 0 1 1.273-1.273L12 16.227l3.364-3.364a.9.9 0 0 1 1.273 0ZM17.637 5.863a.9.9 0 0 1 0 1.273l-5 5a.9.9 0 0 1-1.273 0l-5-5a.9.9 0 0 1 1.273-1.273L12 10.227l4.364-4.364a.9.9 0 0 1 1.273 0Z']")
    private ExtendedWebElement expandTestSuitesButton;

    @FindBy(xpath = ".//*[@d='M16.637 11.136a.9.9 0 0 0 0-1.273l-4-4a.9.9 0 0 0-1.273 0l-4 4a.9.9 0 0 0 1.272 1.273L12 7.772l3.364 3.364a.9.9 0 0 0 1.273 0ZM17.637 18.136a.9.9 0 0 0 0-1.273l-5-5a.9.9 0 0 0-1.273 0l-5 5a.9.9 0 0 0 1.272 1.273L12 13.772l4.364 4.364a.9.9 0 0 0 1.273 0Z']")
    private ExtendedWebElement collapseTestSuitesButton;

    @FindBy(xpath = ".//span[@class='select-test-cases-modal__main-case-count']")
    private ExtendedWebElement countSelectedCasesLabel;

    @FindBy(xpath = ".//span[contains(@class, 'zbrClearSelectionText')]")
    private ExtendedWebElement clearSelectionButton;

    @FindBy(xpath = ".//*[contains(text(),'Filter')]")
    private ExtendedWebElement filterButton;

    @FindBy(xpath = ".//button[text()='Done']")
    private ExtendedWebElement doneButton;

    public SelectTestCasesModal(WebDriver driver) {
        super(driver);
    }

    public boolean isOpened() {
        return searchField.isVisible(3) && header.getText().equalsIgnoreCase("Select test cases");
    }

    public List<ModalSuiteItem> expandTestSuites() {
        expandTestSuitesButton.click();

        WaitUtil.waitCheckListIsNotEmpty(testSuites);
        return testSuites;
    }

    public TestCasesPanel selectTestSuite(String suiteName) {
        testSuites = expandTestSuites();

        return this.findSuiteItemBySuiteName(suiteName)
                   .select();
    }

    public TestCasesPanel clickOnTestSuite(String suiteName) {
        testSuites = this.expandTestSuites();

        return this.findSuiteItemBySuiteName(suiteName)
                   .clickOnName();
    }

    private ModalSuiteItem findSuiteItemBySuiteName(String suiteName) {
        return StreamUtils.findFirst(
                                  testSuites,
                                  testSuite -> testSuite.getSuiteName().equalsIgnoreCase(suiteName)
                          )
                          .orElseThrow(() -> new NoSuchElementException("Could not find test suite with name '" + suiteName + "'"));
    }

    public List<ModalSuiteItem> searchTestSuitesWithTestCaseWithName(String testCaseName) {
        searchField.search(testCaseName);
        pause(3);

        return expandTestSuites();
    }

    public List<ModalSuiteItem> collapseTestSuites() {
        collapseTestSuitesButton.click();

        return getTestSuites();
    }

    public List<ModalSuiteItem> getTestSuites() {
        WaitUtil.waitCheckListIsNotEmpty(testSuites);
        return testSuites;
    }

    public boolean isTestSuiteVisible(String testSuiteName) {
        return getTestSuites().stream().anyMatch(suite -> suite.getSuiteName().equals(testSuiteName));
    }

    public String getCountSelectedCases() {
        return countSelectedCasesLabel.getText().replaceAll(".*?(\\d+) selected.*", "$1");
    }

    public CreateTestRunPage clickDoneButton() {
        doneButton.click();

        return new CreateTestRunPage(getDriver());
    }

    public SelectTestCasesModal clickClearSelectionButton() {
        clearSelectionButton.click();

        return this;
    }

    public TestCaseFilterBlock getFilters() {
        filterButton.click();
        return new TestCaseFilterBlock(getDriver());
    }

}
