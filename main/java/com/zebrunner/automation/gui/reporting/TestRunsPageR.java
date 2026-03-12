package com.zebrunner.automation.gui.reporting;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.launcher.TestRunsLauncher;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.gui.common.FilterSemiWindow;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.launcher.domain.LauncherWeb;
import com.zebrunner.automation.api.tcm.domain.Suite;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.testng.Assert;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Getter
@Deprecated
public class TestRunsPageR extends TenantProjectBasePage {

    public static final String URL_MATCHER =
            "https://.+\\.zebrunner\\..+/projects/.+/automation-launches(\\?|\\z).*";
    public static final String PAGE_URL =
            ConfigHelper.getTenantUrl() + "/projects/%s/automation-launches";

    public static final String PAGE_NAME = "Test runs";
    public static final Duration TEST_RUN_WAITING_TIME = Duration.ofSeconds(700);
    public static final Duration TEST_RUN_INTERVAL_WAITING_TIME = Duration.ofSeconds(5);

    @FindBy(xpath = "//div[contains(@class, 'launch-card ')]")
    private List<LaunchCard> testRunCards;

    @FindBy(xpath = "//div[@class='test-run-card__model ng-binding ng-scope']")
    private ExtendedWebElement launchering;

    //@FindBy(id = "noDataYet")
    @FindBy(xpath = "//div[@class='docs-section']")
    private ExtendedWebElement noDataField;

    @FindBy(xpath = "//span[text()='Launcher']/ancestor::button")
    private Element launcherButton;

    @FindBy(xpath = "//button[@id='bulkDeleteTestRuns']")
    private Element bulkDeleteButton;

    @FindBy(xpath = "//div[contains(@class,'test-runs__bulk')]//div[@class='md-container md-ink-ripple']")
    private Element bulkCheckBox;

    @FindBy(xpath = "//span[contains(text(),'Send as email')]/ancestor::button")
    private Element bulkSendAsEmail;

    @FindBy(xpath = "//span[contains(text(),'More')]/ancestor::div[@class='menu__button']")
    private Element bulkMoreButton;

    @FindBy(xpath = "//span[contains(text(),'Rerun')]/ancestor::button")
    private Element bulkRerun;

    @FindBy(xpath = "//span[contains(text(),'Abort')]/ancestor::button")
    private Element bulkAbortButton;

    @FindBy(xpath = "//span[contains(text(),'Compare')]/ancestor::button")
    private Element bulkCompareButton;

    @FindBy(xpath = "//md-icon[contains(@aria-label,'Reset selection')]/ancestor::button")
    private Element bulkExitButton;

    @FindBy(xpath = "//input[contains(@class,'runs-filter')]")
    private Element searchField;

    @FindBy(xpath = "//md-select[@name='browser']")
    private Element browserFilterButton;

    @FindBy(xpath = "//md-select[@name='environment']")
    private Element envFilterButton;

    @FindBy(xpath = "//md-select[@name='platform']")
    private Element platformFilterButton;

    @FindBy(xpath = ".//div[@class='md-text ng-binding']")
    private List<ExtendedWebElement> filterParameterListList;

    @FindBy(xpath = "//button[text()='RESET']")
    private Element resetButton;

    @FindBy(xpath = "//button[text()='SAVE']")
    private Element saveButton;

    //more button and subsequence list in more
    @FindBy(xpath = "//button[contains(@class,'more-button')]")
    private Element filterMoreButton;

    @FindBy(xpath = "//div[contains(text(),'Status')]/ancestor::md-menu-item[@class='runs-filter__more-menu-item ng-scope']")
    private Element statusFilter;

    @FindBy(xpath = "//div[contains(text(),'Environment')]/ancestor::md-menu-item[@class='runs-filter__more-menu-item ng-scope']")
    private Element envFilter;

    @FindBy(xpath = "//div[contains(text(),'Locale')]/ancestor::md-menu-item[@class='runs-filter__more-menu-item ng-scope']")
    private Element localeFilter;

    @FindBy(xpath = "//div[contains(text(),'Date')]/ancestor::md-menu-item[@class='runs-filter__more-menu-item ng-scope']")
    private Element dateFilter;

    @FindBy(xpath = "//div[contains(text(),'Date')]/ancestor::md-menu-item[@class='runs-filter__more-menu-item ng-scope']")
    private Element reviewedFilter;

    @FindBy(xpath = "//button[contains(@aria-label,'Close menu')]")
    private Element closeMoreFilterMenu;

    @FindBy(xpath = "//a[@class='runs-filter__action-link ng-scope']")
    private Element showAllSavedFilters;

    @FindBy(xpath = ".//div[@class='saved-searches']")
    private FilterSemiWindow semiWindow;

    @FindBy(xpath = " //div[@class='test-run-card__progressbar ng-scope']")
    private ExtendedWebElement progressbar;

    @FindBy(xpath = "//div[@class='launches-page']")
    private ExtendedWebElement uiLoadedMarker;

    @FindBy(xpath = "//*[@class='styled-modal']//div[@class='onboarding-modal__header']")
    private ExtendedWebElement onboardingModal;

    @FindBy(xpath = "//button[@id='close']")
    private Element closeOnboardingModalButton;

    public TestRunsPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static TestRunsPageR getPageInstance(WebDriver driver) {
        return new TestRunsPageR(driver);
    }

    @Override
    public boolean isPageOpened() {
        boolean isUrlMatches = waitUntil(ExpectedConditions.urlMatches(URL_MATCHER), DEFAULT_EXPLICIT_TIMEOUT);
        return isUrlMatches && super.isPageOpened();
    }

    public TestRunsPageR openPageDirectly(String projectKey) {
        this.openURL(String.format(PAGE_URL, projectKey));
        assertPageOpened();
        return this;
    }

    public TestRunsLauncher toLaunchesPage() {
        launcherButton.click();
        return TestRunsLauncher.openPage(getDriver());
    }

    public boolean isLauncherButtonVisible() {
        return launcherButton.isStateMatches(Condition.VISIBLE);
    }

    public TestRunsPageR deleteAllTestRunCards() {
        while (!noDataField.isVisible(2)) {
            testRunCards.get(0).getCheckBox().click();
            if (testRunCards.size() > 1) {
                bulkCheckBox.click();
            }
            pause(2);
            bulkDeleteButton.click();
            try {
                acceptAlert();
            } catch (NoAlertPresentException ex) {
                log.error("Can't find alert", ex);
            }
        }
        return TestRunsPageR.getPageInstance(getDriver());
    }

    public LaunchCard waitTestRunCardFinish(LauncherWeb launcher) {
        return waitTestRunCardFinish(launcher, TEST_RUN_WAITING_TIME);
    }

    public LaunchCard waitTestRunCardFinish(LauncherWeb launcher, Duration testRunWaitTime) {
        Suite suite = launcher.getSuite();
        Clock clock = Clock.systemDefaultZone();
        Instant end = clock.instant().plusSeconds(testRunWaitTime.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;
        Duration interval = Duration.ofSeconds(Math.min(30, testRunWaitTime.toSeconds() / 8));
        log.info("Waiting suite " + suite.getRunName() + " to run");
        while (end.isAfter(clock.instant())) {
            for (LaunchCard card : testRunCards) {
                if (card.getCardName().toLowerCase().contains(suite.getRunName().toLowerCase()) && card.getStatus().getStatusColourFromCss().getColour()
                        .equals(ColorEnum.WAITING_TO_START.getHexColor()) && card.getDuration().isStateMatches(Condition.NON_VISIBLE)) {
                    return card;
                }
            }
            try {
                sleeper.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.fail("Can't find test run with name: " + suite.getRunName());
        return null;
    }


    public LaunchCard findTestRunCardByName(String name) {
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                (card -> card.getCardName().equalsIgnoreCase(name)),
                "Found test run card with name " + name,
                "There are no test run card with name:" + name
        );

    }

    /**
     * Searching test run card by launcher
     * TODO What card this method search - finished, or started, or stopped?
     */
    public LaunchCard waitTestRunCardAppearByLaunchName(LauncherWeb launcher) {
        log.info("Wait until test card with launch name {} will appeared on {} page", launcher.getLaunchName(), PAGE_NAME);
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().equals(launcher.getLaunchName()),
                TEST_RUN_WAITING_TIME,
                TEST_RUN_INTERVAL_WAITING_TIME,
                "Found test run card with name " + launcher.getLaunchName(),
                "Test run card with name " + launcher.getLaunchName() + " was not found or timeout exceeded"
        );
    }

    public LaunchCard waitTestRunCardAppearByRunName(LauncherWeb launcher) {
        log.info("Wait until test card with launch name {} will appeared on {} page", launcher.getLaunchName(), PAGE_NAME);
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().equals(launcher.getLaunchName()),
                TEST_RUN_WAITING_TIME,
                TEST_RUN_INTERVAL_WAITING_TIME,
                "Found test run card with name " + launcher.getLaunchName(),
                "Test run with name " + launcher.getLaunchName() + " was not found or timeout exceeded"
        );
    }

    public boolean isSearchFieldPresent() {
        return searchField.isStateMatches(Condition.PRESENT);
    }

    public boolean isShowAllSavedFiltersButtonPresent() {
        return showAllSavedFilters.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isFilterMoreButtonPresent() {
        return filterMoreButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isBrowserFilterButtonPresent() {
        return browserFilterButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isPlatformFilterButtonPresent() {
        return platformFilterButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isResetButtonPresent() {
        return resetButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isSaveButtonPresent() {
        return saveButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public String getNumberOfTestRunCards() {
        return String.valueOf(testRunCards.size());
    }


    public boolean isDeleteFromCheckBoxPresent() {
        return bulkDeleteButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isSendByEmailFromCheckBoxPresent() {
        return bulkCheckBox.isStateMatches(Condition.CLICKABLE);
    }

    public void clickMore() {
        bulkMoreButton.click();
    }

    public boolean isCompareVisible() {
        return bulkCompareButton.isStateMatches(Condition.VISIBLE);
    }

    public void closeTestRunTab() {
        bulkExitButton.waitUntil(Condition.VISIBLE).click();
    }

    public void selectPlatform(String platform) {
        log.info("Selecting platform " + platform + "...");
        platformFilterButton.waitUntil(Condition.VISIBLE).click();
        getFilterParameterBy(platform).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectEnv(String env) {
        log.info("Selecting environment " + env + "...");
        filterMoreButton.waitUntil(Condition.VISIBLE).click();
        envFilter.waitUntil(Condition.CLICKABLE).click();
        ComponentUtil.pressEscape(getDriver());
        envFilterButton.waitUntil(Condition.CLICKABLE).click();
        getFilterParameterBy(env).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public FilterSemiWindow clickSaveFilter() {
        saveButton.waitUntil(Condition.VISIBLE).click();
        return semiWindow;
    }

    public void clickResetFilter() {
        resetButton.waitUntil(Condition.VISIBLE).click();
    }

    public FilterSemiWindow toSavedFilters() {
        showAllSavedFilters.click();
        return semiWindow;
    }

    public List<LaunchCard> getAllTestRunCards() {
        log.info("Getting all test run cards ...");
        WaitUtil.waitCheckListIsNotEmpty(testRunCards);
        return testRunCards;
    }

    /**
     * Searching filter parameter by parameter's name
     */
    public ExtendedWebElement getFilterParameterBy(String parameterName) {
        log.info("Getting filter parameter {} ...", parameterName);
        return WaitUtil.waitElementAppearedInListByCondition(filterParameterListList,
                filterParameter -> filterParameter.getText().trim().equalsIgnoreCase(parameterName),
                "Filter parameter with name " + parameterName + " was found",
                "Filter parameter with name " + " was not found");
    }

    public Boolean isCertainTestRunCard(String name) {
        log.info("Checking test run card with name {} exists", name);
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().trim().equalsIgnoreCase(name));
    }

    public void selectBrowser(String browser) {
        browserFilterButton.click();
        getFilterParameterBy(browser).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public boolean waitLoadingOfJob() {
        log.info("Waiting for job loading.... ");
        return launchering.waitUntilElementDisappear(50);
    }

    public LauncherPage toLauncherPage() {
        launcherButton.click();
        return LauncherPage.openPage(getDriver());
    }

    public AddRepoPage launchFirstTime() {
        launcherButton.click();
        return AddRepoPage.openPage(getDriver());
    }

    public void closeOnboardingModalIfExists() {
        if (onboardingModal.isVisible(2)) {
            onboardingModal.findExtendedWebElement(closeOnboardingModalButton.getRootExtendedElement().getBy()).click();
            onboardingModal.waitUntilElementDisappear(2);
        }
    }

}
