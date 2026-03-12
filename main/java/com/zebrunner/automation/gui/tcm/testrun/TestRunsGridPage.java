package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
public class TestRunsGridPage extends TenantProjectBasePage {

    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/test-runs";
    public static final String PAGE_TITLE = "Test runs";

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='Test Run']/parent::button")
    private ExtendedWebElement createTestRunButton;

    @FindBy(xpath = "//button[contains(text(),'Open')]")
    private ExtendedWebElement openButton;

    @FindBy(xpath = "//button[contains(text(),'Closed')]")
    private ExtendedWebElement closedButton;

    @FindBy(xpath = "//div[@class='runs-control-panel__checkbox-container']")
    private ExtendedWebElement checkboxForAllTestRuns;

    @FindBy(xpath = BulkActionsPanel.ROOT_XPATH)
    private BulkActionsPanel bulkActionsPanel;

    @FindBy(xpath = "//div[@class='runs-filters']")
    private TestRunsFilterBlock filters;

    @FindBy(xpath = "//div[@class='runs-item']")
    private List<TestRunItem> testRunsList;

    @FindBy(xpath = "//h1[text()='Test runs']")
    private ExtendedWebElement uiLoadedMarker;

    public TestRunsGridPage(WebDriver driver) {
        super(driver);
        super.setUiLoadedMarker(uiLoadedMarker);
    }

    public static TestRunsGridPage openPageByUrl(WebDriver driver, Project project) {
        TestRunsGridPage testRunsGridPage = new TestRunsGridPage(driver);

        testRunsGridPage.openURL(String.format(PAGE_URL, project.getKey()));
        testRunsGridPage.assertPageOpened();

        return testRunsGridPage;
    }

    public CreateTestRunPage clickCreateTestRunButton() {
        createTestRunButton.click();
        return new CreateTestRunPage(getDriver());
    }

    public boolean isCreateTestRunButtonClickable() {
        return createTestRunButton.isClickable(3);
    }

    public boolean isTestRunExist(String testRunName) {
        if (getEmptyPlaceholder().isEmptyPlaceholderImagePresent()) {
            return false;
        }

        search(testRunName);
        WaitUtil.waitCheckListIsNotEmpty(testRunsList);

        return testRunsList.stream()
                .anyMatch(testRun -> testRun.getTestRunName().equalsIgnoreCase(testRunName));
    }

    public List<TestRunItem> search(String testRunName) {
        WaitUtil.waitComponentByCondition(search.getSearchField(), searchField
                -> searchField.isStateMatches(Condition.CLICKABLE));

        search.search(testRunName);
        pause(3);

        return testRunsList;
    }

    public TestRunItem getTestRunItem(String itemName) {
        search(itemName);
        return WaitUtil.waitElementAppearedInListByCondition(testRunsList,
                (testRunItem -> testRunItem.getTestRunName().equalsIgnoreCase(itemName)),
                "Test run item '" + itemName + "' was found in grid!",
                "Test run item '" + itemName + "' not found in grid"
        );
    }

    public boolean isPageOpened() {
        return search.isVisible(3);
    }

    public TestRunsGridPage clickClosedTestRuns() {
        closedButton.click();
        return this;
    }

    public TestRunsGridPage clickOpenedTestRuns() {
        openButton.click();
        return this;
    }

    public TestRunPage searchAndOpenTestRun(String testRunName) {
        return getTestRunItem(testRunName).clickTestRunItem();
    }

    public List<TestRunItem> getTestRunItems() {
        WaitUtil.waitCheckListIsNotEmpty(testRunsList);

        return testRunsList;
    }

    public boolean isSearchFieldDisabled() {
        return search.getSearchField().getAttribute("class").contains("Mui-disabled");
    }

    public boolean isCheckboxForAllTestRunsAppear() {
        return checkboxForAllTestRuns.isPresent(1);
    }
}
