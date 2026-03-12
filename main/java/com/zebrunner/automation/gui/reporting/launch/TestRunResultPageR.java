package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.CornerClickerUtil;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Slf4j
@Getter
public class TestRunResultPageR extends TenantProjectBasePage {

    private final String FAILED_TEST_CARDS_XPATH = "//div[contains(@class, 'test-card FAILED')]/parent::div[@class='test-card-wrapper']";

    private final String PASSED_TEST_CARDS_XPATH = "//div[contains(@class, 'test-card PASSED')]/parent::div[@class='test-card-wrapper']";

    private final String SKIPPED_TEST_CARDS_XPATH = "//div[contains(@class, 'test-card SKIPPED')]/parent::div[@class='test-card-wrapper']";

    private final String TEST_CARD_XPATH = "//div[@class='test-card-wrapper']";

    @FindBy(xpath = "//*[@class = 'launch-header__wrapper']/h1")
    private ExtendedWebElement launchName;

    @FindBy(xpath = "//div[contains(@class,'launch-details__header')]")
    private CollapsedTestRunViewHeaderR collapsedTestRunViewHeaderR;

    @FindBy(xpath = "//div[contains(@class,'actions-wrapper')]")
    private ActionsBlockR actionsBlockR;

    @FindBy(xpath = "//div[contains(@class,'launch-header__actions')]")
    private ActionsR actionBar;

    @FindBy(xpath = "//div[@class='test-card-wrapper']")
    private List<ResultTestMethodCardR> testCards;

    @FindBy(xpath = PASSED_TEST_CARDS_XPATH)
    private List<ResultTestMethodCardR> passedTestCards;

    @FindBy(xpath = SKIPPED_TEST_CARDS_XPATH)
    private List<ResultTestMethodCardR> skippedTestCards;

    @FindBy(xpath = FAILED_TEST_CARDS_XPATH)
    private List<ResultTestMethodCardR> failedTestCards;

    @FindBy(xpath = "//div[contains(@class, 'sessions-sidebar__wrapper')]")
    private ResultSessionWindowR resultSessionWindow;

    @FindBy(xpath = "//div[contains(@class,'Mui-expanded MuiAccordion-gutters launch-card small-version')]//div[@class='launch-card__expand-button']//button")
    private Element expandTestRunView;

    @FindBy(xpath = "//div[contains(@class,' launch-card full-version')]//div[contains(@class, 'launch-card__expand-button')]//button")
    private Element collapseTestRunView;

    @FindBy(xpath = "//div[contains(@class,'Mui-expanded MuiAccordion-gutters launch-card small-version')]")
    private CollapsedTestRunViewHeaderR collapseTestRunViewElement;

    @FindBy(xpath = "//div[contains(@class,'Mui-expanded MuiAccordion-gutters launch-card full-version')]")
    private ExpandedTestRunViewHeaderR expandedTestRunViewHeader;

    @FindBy(xpath = "//div[@role='presentation']")
    private ExtendedWebElement clickCatcher;

    @FindBy(xpath = "//div[contains(@class,'launch-details__header')]")
    private ExtendedWebElement uiLoadedMarker;

    @FindBy(xpath = "//*[@class = 'test-details__table-row _group']")
    private ExtendedWebElement selectedGroupTable;

    @FindBy(xpath = TestDetailsTableRow.ROOT_XPATH)
    private List<TestDetailsTableRow> testTableRows;

    public TestRunResultPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static TestRunResultPageR getPageInstance(WebDriver driver) {
        return new TestRunResultPageR(driver);
    }

    public TestRunResultPageR openViaDirectLink(WebDriver driver, String link) {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(driver);

        testRunResultPage.openURL(link.trim());
        testRunResultPage.pause(4);
        super.assertPageOpened();

        return testRunResultPage;
    }

    public TestRunResultPageR openPageDirectly(String projectKey, Long testRunId) {
        String url = String.format("%s/projects/%s/automation-launches/%d", ConfigHelper.getTenantUrl(), projectKey, testRunId);

        super.openURL(url);
        super.assertPageOpened();

        return this;
    }

    public void hideDropdownMenu() {
        if (clickCatcher.isPresent()) {
            CornerClickerUtil.clickToCorner(true, true, getDriver(), clickCatcher);
            clickCatcher.waitUntilElementDisappear(5);
        }
    }

    public boolean isNumberOfTestsAsExpected(int expected) {
        return (testCards.size() == getNumberOfMethods()) && (testCards.size() == expected);
    }

    private int getNumberOfMethods() {
        log.debug("Counting total number of tests");
        return passedTestCards.size() + failedTestCards.size() + skippedTestCards.size();
    }

    public boolean isAllFailedTestsHaveErrorTrace() {
        for (ResultTestMethodCardR testMethodCard : failedTestCards) {
            if (!testMethodCard.isErrorStacktracePresent()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllPassedTestsHaveNoErrorTrace() {
        for (ResultTestMethodCardR testMethodCard : passedTestCards) {
            if (testMethodCard.isErrorStacktracePresent()) {
                return false;
            }
        }
        return true;
    }

    public ResultSessionWindowR openResultSessionWindow(ResultTestMethodCardR card) {
        card.clickTestSessionInfoRef();
        return resultSessionWindow;
    }

    public String getLeftBoardColorOfLaunchHeader() {
        String color = collapsedTestRunViewHeaderR.getRootExtendedElement().getElement()
                                                  .getCssValue("border-left-color");
        return Color.fromString(color).asHex();
    }

    public ResultTestMethodCardR getCertainTest(String testName) {
        log.info("Waiting for the list of tests to load....");
        pause(3);
        for (ResultTestMethodCardR test : testCards) {
            if (test.getCardTitle().getText().equalsIgnoreCase(testName)) {
                log.info("Test with name {} was found!", testName);
                return test;
            }
        }
        throw new RuntimeException("Can't find test with name  " + testName);
    }

    /**
     * Getting tests names on the page
     */
    public List<String> getAllTestsNames() {
        log.info("Getting all tests names");
        WaitUtil.waitCheckListIsNotEmpty(testCards);
        List<String> testsNames = testCards.stream()
                                           .map(resultTestMethodCardR -> resultTestMethodCardR.getCardTitle().getText())
                                           .collect(Collectors.toList());
        log.info("Actual list of test names {} is ", testsNames);
        return testsNames;
    }

    public CollapsedTestRunViewHeaderR collapseTestRunViewHeader() {
        if (collapseTestRunView.isStateMatches(Condition.CLICKABLE)) {
            collapseTestRunView.click();
            pause(1);
        }
        return collapseTestRunViewElement;
    }

    public ExpandedTestRunViewHeaderR expandTestRunViewHeader() {
        if (expandTestRunView.isStateMatches(Condition.CLICKABLE)) {
            expandTestRunView.click();
            pause(1);
        }
        return expandedTestRunViewHeader;
    }

    public ActionsR getResultActionBar() {
        return actionBar;
    }

    public Long getTestRunIdFromUrl() {
        assertPageOpened();
        String url = getDriver().getCurrentUrl();
        String pattern = "automation-launches/(\\d+)(?:[/?]|$)";
        String numberString = StringUtil.getByPattern(url, pattern);
        return Long.valueOf(numberString);
    }

    public boolean isTestPresent(String testName) {
        boolean condition = false;
        for (ResultTestMethodCardR test : getTestCards()) {
            if (test.getCardTitle().getText().equalsIgnoreCase(testName)) {
                condition = true;
            }
        }
        return condition;
    }

    public List<ResultTestMethodCardR> getTestCards() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), TEST_CARD_XPATH);
        return testCards;
    }

    public List<ResultTestMethodCardR> getFailedTestCards() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), FAILED_TEST_CARDS_XPATH);
        return failedTestCards;
    }

    public List<ResultTestMethodCardR> getPassedTestCards() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), PASSED_TEST_CARDS_XPATH);
        return passedTestCards;
    }

    public void clickSelectedGroupTable() {
        selectedGroupTable.click();
    }

    public List<TestDetailsTableRow> getTestTableRows() {
        WaitUtil.waitNotEmptyListOfElements(super.getDriver(), TestDetailsTableRow.ROOT_XPATH);

        return testTableRows;
    }

    public boolean isTableWithNamePresent(String name) {
        return StreamUtils.findFirst(
                                  this.getTestTableRows(),
                                  row -> row.getTableNameText().equalsIgnoreCase(name)
                          )
                          .isPresent();
    }

    public TestDetailsTableRow getTableByName(String name) {
        return StreamUtils.findFirst(
                                  this.getTestTableRows(),
                                  row -> row.getTableNameText().equalsIgnoreCase(name)
                          )
                          .orElseThrow(() -> new RuntimeException("Test table with name '" + name + "' doesn't exist"));
    }

    public String getLaunchName() {
        return launchName.getText();
    }

}