package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

public class TestRunSuiteItem extends AbstractUIObject {

    public static final String ROOT_XPATH = "//div[@data-index]";

    @FindBy(xpath = ".//span[@class='run-suite__title']")
    private ExtendedWebElement titleLabel;

    @FindBy(xpath = ".//span[contains(@class, 'ZbrCheckbox')]")
    private ExtendedWebElement checkbox;

    @FindBy(xpath = ".//p[@class='test-suite-description']")
    private ExtendedWebElement suiteDescription;

    @FindBy(xpath = ".//span[@class='run-suite__case-count']")
    private ExtendedWebElement countCasesLabel;

    @FindBy(xpath = TestRunCaseItem.ROOT_XPATH)
    private List<TestRunCaseItem> testCases;

    public TestRunSuiteItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getSuiteName() {
        return titleLabel.getText();
    }

    public void clickCheckbox() {
        checkbox.click();
    }

    public boolean isTestCaseExist(String testCaseName) {
        return testCases.stream()
                .anyMatch(testCase -> testCase.getCaseTitle().equalsIgnoreCase(testCaseName));
    }

    public TestRunCaseItem getTestCase(String testCaseName) {
        return testCases.stream()
                .filter(testCase -> testCase.getCaseTitle().equalsIgnoreCase(testCaseName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test case '" + testCaseName + "' not found"));
    }

    public boolean isTestCaseSelected(String testCaseName) {
        return getTestCase(testCaseName).isSelected();
    }

    public List<TestRunCaseItem> getTestCases() {
        return testCases;
    }

    public Integer getCountOfCases() {
        return testCases.size();
    }

    public boolean isTestCaseVisible(String testCaseName) {
        try {
            TestRunCaseItem testCase = getTestCase(testCaseName);
            return testCase.isVisible(1);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getSuiteDescription() {
        return suiteDescription.getText();
    }

    public String getCountTestedCases() {
        return countCasesLabel.getText().replaceAll(".*\\((\\d+)/.*", "$1");
    }

    public String getCountAllCasesFromLabel() {
        return countCasesLabel.getText().replaceAll(".*?/(\\d+)\\).*", "$1");
    }
}
