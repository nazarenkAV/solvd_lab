package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class TestRunPage extends TenantProjectBasePage {

    @FindBy(xpath = "//div[@class='run-head-wrapper']")
    private ExtendedWebElement nameLabel;

    @FindBy(xpath = "//div[@class='run-head__env-wrapper']")
    private ExtendedWebElement environmentLabel;

    @FindBy(xpath = "//div[contains(@class, 'run-dashboard__full-container')]//button[contains(@class, 'run-dashboard__accordion-button')]")
    private ExtendedWebElement collapseHeaderButton;

    @FindBy(xpath = "//div[contains(@class, 'run-dashboard__small-container')]//button[contains(@class, 'run-dashboard__accordion-button')]")
    private ExtendedWebElement expandHeaderButton;

    @FindBy(xpath = ExpandedTestRunHeader.ROOT_XPATH)
    private ExpandedTestRunHeader expandedHeader;

    @FindBy(xpath = CollapsedTestRunHeader.ROOT_XPATH)
    private CollapsedTestRunHeader collapsedHeader;

    @FindBy(xpath = "//*[text()='Close']//parent::button")
    private ExtendedWebElement closeButton;

    @FindBy(xpath = "//div[@class='button-placeholder']")
    private ExtendedWebElement closedLabel; // appears when test run closed

    @FindBy(xpath = "//*[text()='Rerun']//parent::button")
    @CaseInsensitiveXPath
    private ExtendedWebElement rerunButton;

    @FindBy(xpath = "//div[@class='run-head__buttons-container']//button[contains(@class, 'icon info')]")
    private ExtendedWebElement menuButton;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown menu;

    @FindBy(xpath = TestRunSuiteItem.ROOT_XPATH)
    private List<TestRunSuiteItem> testSuites;

    @FindBy(xpath = "//div[@aria-label='Expand all']")
    private ExtendedWebElement expandSuitesButton;

    @FindBy(xpath = "//div[@aria-label='Collapse all']")
    private ExtendedWebElement collapseSuitesButton;

    @FindBy(xpath = "//button[contains(@class, 'checkbox-with-dropdown__dropdown-btn')]")
    private ExtendedWebElement checkboxArrow;

    @FindBy(xpath = "//span[@class='run-case-actions__select-info']//span[contains(@class, 'zbrClearSelectionText')]")
    private ExtendedWebElement clearSelectionButton;

    @FindBy(xpath = "//div[contains(@class, 'add-filter-button__dropdown-wrapper')]")
    private TestRunPageFiltersBlock filters;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='RESET']")
    protected ExtendedWebElement resetFiltersButton;

    @FindBy(xpath = "//button[text()='Set result']")
    @CaseInsensitiveXPath
    private ExtendedWebElement setResultButton;

    @FindBy(xpath = "//*[text()='Filter']//parent::button")
    private ExtendedWebElement filterButton;

    @FindBy(xpath = "//div[@class='run-dashboard__content']")
    private ExtendedWebElement uiLoadedMarker;

    public TestRunPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static TestRunPage getPageInstance(WebDriver driver) {
        return new TestRunPage(driver);
    }

    public TestRunsGridPage backToTestRunsGrid() {
        breadcrumbs.clickBreadcrumb(TestRunsGridPage.PAGE_TITLE);

        pause(2);
        return new TestRunsGridPage(getDriver());
    }

    public String getName() {
        return nameLabel.getText();
    }

    public ExpandedTestRunHeader getExpandedHeader() {
        if (expandHeaderButton.isVisible(3)) {
            expandHeaderButton.click();
            pause(1);
        }
        return expandedHeader;
    }

    public CollapsedTestRunHeader getCollapsedHeader() {
        if (collapseHeaderButton.isVisible(3)) {
            collapseHeaderButton.click();
            pause(1);
        }
        return collapsedHeader;
    }

    public String getEnvironmentName() {
        return environmentLabel.getText();
    }

    public boolean isPageOpened() {
        return nameLabel.isVisible(3);
    }

    public CloseTestRunModal clickCloseButton() {
        closeButton.click();
        return new CloseTestRunModal(getDriver());
    }

    public boolean isClosedLabelAppear() {
        return closedLabel.isPresent(3);
    }

    public String getClosedLabelText() {
        return closedLabel.getText();
    }

    public Dropdown open3DotMenu() {
        menuButton.click();
        return menu;
    }

    public void openMenuAndSelectOption(TestRunPageMenuActions action) {
        Dropdown menu = open3DotMenu();
        menu.findItem(action.getValue()).click();
    }

    public List<TestRunSuiteItem> expandAndGetAllTestSuites() {
        expandSuitesButton.click();

        return testSuites;
    }

    public List<TestRunSuiteItem> collapseAndGetAllTestSuites() {
        collapseSuitesButton.click();

        return testSuites;
    }

    public boolean isTestCaseAppear(String caseName) {
        return testSuites.stream().anyMatch(suite -> suite.isTestCaseExist(caseName));
    }

    public TestRunSuiteItem getTestSuite(String suiteName) {
        WaitUtil.waitCheckListIsNotEmpty(testSuites);

        return testSuites.stream()
                .filter(testSuite -> testSuite.getSuiteName().equalsIgnoreCase(suiteName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test suite '" + suiteName + "' not found"));
    }

    public RerunModal openRerunModal() {
        rerunButton.click();
        return new RerunModal(getDriver());
    }

    public AddExecutionResultModal openAddExecutionResultModal() {
        setResultButton.click();
        return new AddExecutionResultModal(getDriver());
    }

    public TestRunPage refreshPage() {
        getDriver().navigate().refresh();

        TestRunPage testRunPage = new TestRunPage(getDriver());
        testRunPage.assertPageOpened();

        return testRunPage;
    }

    public Dropdown openFilterList() {
        filterButton.click();
        return new Dropdown(getDriver());
    }

    public AddExecutionResultModal openAddExecutionResultModalForTestCase(String testSuiteTitle, String testCaseTitle) {
        getTestSuite(testSuiteTitle).getTestCase(testCaseTitle)
                .clickCheckbox();
        return openAddExecutionResultModal();
    }

    public Menu clickCheckboxArrow() {
        checkboxArrow.click();
        return new Menu(getDriver());
    }

    public void clickClearSelectionCheckboxButton() {
        clearSelectionButton.click();
    }

    public TestRunPageFiltersBlock openFiltersList() {
        filterButton.click();
        return filters;
    }

    public void clickResetFiltersButton() {
        resetFiltersButton.click();
    }

    @Getter
    @AllArgsConstructor
    public enum TestRunPageMenuActions {
        ASSIGN_TO_MILESTONE("Assign to milestone"),
        COPY_ID("Copy ID"),
        EDIT("Edit"),
        PUBLIC_ACCESS("Public access"),
        DELETE("Delete");

        private final String value;
    }
}
