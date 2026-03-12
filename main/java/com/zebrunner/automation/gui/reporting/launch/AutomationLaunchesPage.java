package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.time.Duration;
import java.util.List;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.legacy.BreadcrumbsEnum;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.launcher.TestRunsLauncher;
import com.zebrunner.automation.api.launcher.domain.LauncherWeb;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import static com.zebrunner.automation.gui.common.PaginationR.ROOT_XPATH;

@Getter
public class AutomationLaunchesPage extends TenantProjectBasePage {

    public static final String URL = "https://.+.zebrunner..+/projects/.+/automation-launches";
    public static final String URL_MATCHER = "https://.+.zebrunner..+/projects/.+/automation-launches";
    public static final String PAGE_NAME = "Launches";
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/automation-launches";
    public static final Duration TEST_RUN_WAITING_TIME = Duration.ofSeconds(700);
    public static final Duration TEST_RUN_INTERVAL_WAITING_TIME = Duration.ofSeconds(5);

    @FindBy(xpath = "//div[contains(@class, 'launches-item ')]")
    private List<LaunchCard> testRunCards;

    @FindBy(xpath = "//div[contains(@class, 'launches__bulk')]//input[@type='checkbox']")
    private Element selectAllLaunchesCheckbox;

    @FindBy(xpath = "//*[text()='Launcher']//ancestor::button")
    private Element launcherButton;

    @FindBy(xpath = "//div[contains(@class,'launches-filter__controls')]")
    private ActionsBlockR actionsBlockR;

    @FindBy(xpath = "//input[@placeholder='Search launches']")
    private Element searchField;

    @FindBy(xpath = "//div[@class='launches-filter__controls']")
    private RunsFilters filters;

    @FindBy(xpath = "//div[@class='launches-actions']//button[contains(@class, 'button icon tertiary')]")
    private ExtendedWebElement accessKeyIcon;

    @FindBy(xpath = " //div[@class='test-run-card__progressbar ng-scope']")
    private ExtendedWebElement progressbar;

    @FindBy(xpath = "//div[@class='automation-launches-onboarding__content']")
    private ExtendedWebElement onboardingModal;

    @FindBy(xpath = "//*[contains(@class,'MuiButton-disableElevation button icon tertiary  css-1obwva5')]")
    private Element closeOnboardingModalButton;

    @FindBy(xpath = "//h1[text()='Launches']")
    private ExtendedWebElement uiLoadedMarker;

    @FindBy(xpath = "//div[contains(@class,'launches-filter__wrapper')]" + ROOT_XPATH)
    private PaginationR topPagination;

    @FindBy(xpath = "//div[contains(@class,'launches-table')]" + ROOT_XPATH)
    private PaginationR bottomPagination;

    public AutomationLaunchesPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static AutomationLaunchesPage openPageDirectly(WebDriver driver, Project project) {
        AutomationLaunchesPage automationLaunchesPage = new AutomationLaunchesPage(driver);

        automationLaunchesPage.openURL(String.format(PAGE_URL, project.getKey()));
        automationLaunchesPage.closeOnboardingModalIfExists();
        automationLaunchesPage.assertPageOpened();

        return automationLaunchesPage;
    }

    public static AutomationLaunchesPage openPageDirectly(WebDriver driver, String projectKey) {
        AutomationLaunchesPage automationLaunchesPage = new AutomationLaunchesPage(driver);

        automationLaunchesPage.openURL(String.format(PAGE_URL, projectKey));
        automationLaunchesPage.closeOnboardingModalIfExists();
        automationLaunchesPage.assertPageOpened();

        return automationLaunchesPage;
    }

    public static AutomationLaunchesPage getPageInstance(WebDriver driver) {
        AutomationLaunchesPage testRunsPage = new AutomationLaunchesPage(driver);

        testRunsPage.closeOnboardingModalIfExists();

        return testRunsPage;
    }

    public TestRunsLauncher toLaunchesPage() {
        launcherButton.click();
        return TestRunsLauncher.openPage(getDriver());
    }

    public boolean isSelectAllLaunchesCheckboxPresent() {
        return selectAllLaunchesCheckbox.isStateMatches(Condition.PRESENT);
    }

    public void clickSelectAllLaunchesCheckbox() {
        selectAllLaunchesCheckbox.click();
    }

    public boolean isLauncherButtonVisible() {
        return launcherButton.isStateMatches(Condition.VISIBLE);
    }

    public boolean isAccessKeyIconClickable() {
        return accessKeyIcon.isClickable(2);
    }

    public LaunchCard getCertainTestRunCard(String name, boolean isFinished) {
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                (el -> el.getTitleName().getText().equalsIgnoreCase(name)
                        && el.getDuration().isStateMatches(Condition.VISIBLE) == isFinished
                ),
                "Launch card with the name " + name + " appears and the finished state: " + isFinished,
                "There are no launch cards with the name " + name +
                        "  and the finished state: " + isFinished
        );
    }

    public LaunchCard findTestRunCardByName(String name) {
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                (card -> card.getTitleName().getText().equalsIgnoreCase(name)),
                "Launch card with the name " + name + " has been found! ",
                "There are no launch cards with the name:" + name
        );

    }

    public LaunchCard searchAndFindTestRunCardByName(String name) {
        search(name);
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                (card -> card.getTitleName().getText().equalsIgnoreCase(name)),
                "Launch card with the name " + name + " has been found! ",
                "There are no launch cards with the name:" + name
        );
    }

    public List<LaunchCard> search(String cardName) {
        searchField.sendKeys(cardName);
        pause(3);
        return testRunCards;
    }

    public LaunchCard waitTestRunCardAppearByRunName(LauncherWeb launcher) {
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().equalsIgnoreCase(launcher.getLaunchName()),
                TEST_RUN_WAITING_TIME,
                TEST_RUN_INTERVAL_WAITING_TIME,
                "Launch card with the name " + launcher.getLaunchName() + " has been found! ",
                "Launch with the name " + launcher.getLaunchName() + " has not been found or the timeout has been exceeded."
        );
    }

    public LaunchCard waitTestRunCardAppearByRunNameAndFinished(LauncherWeb launcher) {
        try {
            return
                    WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                            card -> (card.getCardName()
                                         .equalsIgnoreCase(launcher.getLaunchName()) && card.isLaunchFinished()),
                            TEST_RUN_WAITING_TIME,
                            TEST_RUN_INTERVAL_WAITING_TIME,
                            "Launch card with the name " + launcher.getLaunchName() + " has been found! ",
                            "Launch with the name " + launcher.getLaunchName() + " has not been found or the timeout has been exceeded."
                    );
        } catch (StaleElementReferenceException e) {
            super.pause(3);
            return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                    card -> (card.getCardName().equalsIgnoreCase(launcher.getLaunchName()) && card.isLaunchFinished()),
                    TEST_RUN_WAITING_TIME,
                    TEST_RUN_INTERVAL_WAITING_TIME,
                    "Launch card with the name " + launcher.getLaunchName() + " has been found! ",
                    "Launch with the name " + launcher.getLaunchName() + " has not been found or the timeout has been exceeded."
            );
        }
    }

    public LaunchCard waitTestRunCardAppearByRunNameAndFinished(LauncherWeb launcher, Duration TEST_RUN_WAITING_TIME) {
        try {
            return
                    WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                            card -> (card.getCardName()
                                         .equalsIgnoreCase(launcher.getLaunchName()) && card.isLaunchFinished()),
                            TEST_RUN_WAITING_TIME,
                            TEST_RUN_INTERVAL_WAITING_TIME,
                            "Launch card with the name " + launcher.getLaunchName() + " has been found! ",
                            "Launch with the name " + launcher.getLaunchName() + " has not been found or the timeout has been exceeded."
                    );
        } catch (StaleElementReferenceException e) {
            super.pause(3);
            return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                    card -> (card.getCardName().equalsIgnoreCase(launcher.getLaunchName()) && card.isLaunchFinished()),
                    TEST_RUN_WAITING_TIME,
                    TEST_RUN_INTERVAL_WAITING_TIME,
                    "Launch card with the name " + launcher.getLaunchName() + " has been found! ",
                    "Launch with the name " + launcher.getLaunchName() + " has not been found or the timeout has been exceeded."
            );
        }
    }

    public LaunchCard waitLaunchAppearByName(String launchName) {
        return WaitUtil.waitElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().equalsIgnoreCase(launchName),
                TEST_RUN_WAITING_TIME,
                TEST_RUN_INTERVAL_WAITING_TIME,
                "Launch card with the name " + launchName + " has been found! ",
                "Launch with the name " + launchName + " has not been found or the timeout has been exceeded."
        );
    }

    public boolean isSearchFieldPresent() {
        return searchField.isStateMatches(Condition.PRESENT);
    }

    public boolean isSearchFieldEmpty() {
        return searchField.getText().isEmpty();
    }

    public String getNumberOfTestRunCards() {
        return String.valueOf(testRunCards.size());
    }

    public List<LaunchCard> getAllTestRunCards() {
        WaitUtil.waitCheckListIsNotEmpty(testRunCards);

        return testRunCards;
    }

    public Boolean isCertainTestRunCard(String name) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().trim().equalsIgnoreCase(name));
    }

    public Boolean isCertainLaunchAppears(String name) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(testRunCards,
                card -> card.getCardName().trim().equalsIgnoreCase(name));
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
            clickBreadcrumb(BreadcrumbsEnum.LAUNCHES.getBreadcrumb());
            if (onboardingModal.isVisible(2)) {
                //need to check 2 times. It happens that the wizard is not closed the first time
                clickBreadcrumb(BreadcrumbsEnum.LAUNCHES.getBreadcrumb());
            }
        }
    }

}