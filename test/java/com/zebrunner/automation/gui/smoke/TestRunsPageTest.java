package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.agent.core.config.ConfigurationHolder;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.launcher.domain.Config;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.config.TestLabels;
import com.zebrunner.automation.config.TestMaintainers;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.GettingStartedPage;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.launcher.LauncherItem;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.reporting.launch.AbortModal;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.CardMenuR;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.gui.reporting.launch.ResultSessionWindowR;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.PageUrlEnum;
import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.RepoTypeEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Slf4j
@Maintainer("azarouski")
public class TestRunsPageTest extends LogInBase {

    private static final String expectedWebResult = "2\n" + "1|0\n" + "0";
    private static final String expectedApiResult = "4\n" + "0|0\n" + "0";
    private final String expectedStartedAgoRegex = "^Launched (\\d+) \\w+ ago by \\w+$";
    private final List<String> expectedMethodNames = List.of(
            "Zebrunner web tests - settingsPageTest",
            "Zebrunner web tests - documentationPageTest",
            "Zebrunner web tests - usersPageTest");
    private final String launcherNameApi = "Carina API";
    private final String launcherNameWeb = "Carina WEB";
    private final String milestoneName = "new_milestone".concat(RandomStringUtils.randomAlphabetic(3));
    private String projectKey;
    private Long projectId;
    private Long apiRepoId;
    private Long webRepoId;

    @BeforeClass
    public void createProject() {
        projectKey = LogInBase.projectV1Service.createProject(
                "TRPageTestProject",
                RandomStringUtils.randomAlphabetic(4).toUpperCase()
        );
        projectId = LogInBase.projectV1Service.getProjectIdByKey(projectKey);

        apiRepoId = launcherService.addGitRepo(
                projectId,
                ConfigHelper.getGithubProperties().getUrl() + "/" + PUBLIC_REPO_NAME,
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken(),
                GitProvider.GITHUB.toString()
        );

        webRepoId = launcherService.addGitRepo(
                projectId,
                ConfigHelper.getGitlabProperties().getUrl() + "/zebrunner/tests/tests-ui",
                ConfigHelper.getGitlabProperties().getUsername(),
                ConfigHelper.getGitlabProperties().getAccessToken(),
                RepoTypeEnum.GITLAB.getType()
        );

        apiHelperService.createMilestone(projectId, milestoneName);
    }

    @AfterClass(alwaysRun = true)
    public void deleteProject() {
        projectV1Service.deleteProjectById(projectId);
    }

    @AfterMethod
    public void delimit(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 5, "GMT+3:00");
    }

    //======================================== Test ========================================================//

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-3010", "ZTP-5928"})
    public void _checkMainElementsOnAutomationLaunchesPageWithoutLaunches() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        launchesPage.assertPageOpened();
        log.info(ConfigurationHolder.getRunContext());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(launchesPage.isLauncherButtonVisible(),
                "Launcher button is not present on 'Automation launches' page");
        softAssert.assertTrue(launchesPage.isSearchFieldPresent(),
                "Search field is not present on 'Automation launches' page");
        softAssert.assertTrue(launchesPage.isAccessKeyIconClickable(),
                "Access key icon is not present or not clickable on 'Automation launches' page");

        softAssert.assertEquals(launchesPage.getEmptyPlaceholder().getEmptyPagePlaceholder().getText(),
                MessageEnum.AUT_LAUNCHES_EMPTY_PAGE_PLACEHOLDER_TEXT.getDescription(),
                "Empty placeholder text is not as expected!");
        softAssert.assertEquals(launchesPage.getEmptyPlaceholder().getConfigureReportingAgent().getAttribute("href"),
                PageUrlEnum.DOC_REPORTING_CONCEPTS.getPageUrl(),
                "configure reporting agent link is not as expected!");
        softAssert.assertEquals(launchesPage.getEmptyPlaceholder().getSetUpLaunchers().getAttribute("href"),
                PageUrlEnum.DOC_GUIDE_LAUNCHERS.getPageUrl(),
                "Set up launches link is not as expected!");
        softAssert.assertTrue(launchesPage.getEmptyPlaceholder().getGoToDocsButton()
                                          .isStateMatches(Condition.CLICKABLE),
                "'Go to docs button' is not present or clickable on 'Automation launches' page");
        launchesPage.getEmptyPlaceholder().getGoToDocsButton().click();
        PageUtil.toOtherTabWithoutClosingFirstOne(getDriver());

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        wait.until(ExpectedConditions.titleContains(PageUrlEnum.DOCUMENTATION.getPageTitle()));

        softAssert.assertEquals(getDriver().getTitle(), PageUrlEnum.DOCUMENTATION.getPageTitle(), "Documentation page title is not as expected!");
        softAssert.assertEquals(getDriver().getCurrentUrl(), PageUrlEnum.DOCUMENTATION.getPageUrl(), "Documentation page link is not as expected!");

        PageUtil.toOtherTab(getDriver());

        softAssert.assertTrue(launchesPage.getEmptyPlaceholder()
                                          .isEmptyPlaceholderImagePresent(), "Img is absent on the page!");
        softAssert.assertTrue(launchesPage.getEmptyPlaceholder().getShowMeHowButton()
                                          .isStateMatches(Condition.PRESENT_AND_CLICKABLE),
                "'Show me how' is not present or clickable on 'Automation launches' page");

        GettingStartedPage gettingStartedPage = launchesPage.getEmptyPlaceholder().openGettingStartedPage();

        softAssert.assertTrue(gettingStartedPage.isPageOpened(5), "Getting started page should be opened !");

        gettingStartedPage.clickReportingApiGuideLink();
        PageUtil.toOtherTabWithoutClosingFirstOne(getDriver());

        wait.until(ExpectedConditions.titleContains(PageUrlEnum.DOC_REPORTING_API_GUIDE.getPageTitle()));

        softAssert.assertEquals(getDriver().getTitle(), PageUrlEnum.DOC_REPORTING_API_GUIDE.getPageTitle(),
                "Reporting API guide page title is not as expected!");
        softAssert.assertEquals(getDriver().getCurrentUrl(), PageUrlEnum.DOC_REPORTING_API_GUIDE.getPageUrl(),
                "Reporting API guide page link is not as expected!");

        PageUtil.toOtherTab(getDriver());

        gettingStartedPage.clickReportingConceptsLink();
        PageUtil.toOtherTabWithoutClosingFirstOne(getDriver());

        wait.until(ExpectedConditions.titleContains(PageUrlEnum.DOC_REPORTING_CONCEPTS.getPageTitle()));

        softAssert.assertEquals(getDriver().getTitle(), PageUrlEnum.DOC_REPORTING_CONCEPTS.getPageTitle(),
                "Reporting concepts page title is not as expected!");
        softAssert.assertEquals(getDriver().getCurrentUrl(), PageUrlEnum.DOC_REPORTING_CONCEPTS.getPageUrl(),
                "Reporting concepts page link is not as expected!");

        PageUtil.toOtherTab(getDriver());

        softAssert.assertAll();
    }

    @Test(
            enabled = false,
            dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches"
    )
    @TestCaseKey("ZTP-3011")
    public void apiJobRun() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.TYPE, TestLabelsConstant.API);

        Launcher createdApiLauncher = launcherService.addDefaultApiTestsLauncher(projectId, apiRepoId, launcherNameApi, "api");
        launcherService.launchLauncher(projectId, apiRepoId, createdApiLauncher);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        LaunchCard apiRunCard = automationLaunchesPage.findTestRunCardByName(launcherNameApi).waitFinish(
                Duration.ofSeconds(500),
                Duration.ofSeconds(15));

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(apiRunCard.getStatistics().getText(), expectedApiResult,
                "Test run result differ expected");
        softAssert.assertTrue(apiRunCard.getStateAndTimeFromStart().getText().matches(expectedStartedAgoRegex),
                "Test card should have info about when it test started");
        softAssert.assertTrue(apiRunCard.getMenuButton().isPresent(),
                "Can't find settings button on result card");
        softAssert.assertTrue(apiRunCard.getCheckBox().isPresent(),
                "Checkbox of api test card is no active");
        softAssert.assertTrue(automationLaunchesPage.isLauncherButtonVisible(),
                "Launcher button is not present on 'Automation launches' page");//ZTP-3011 - Verify Automation Launches page when Launch is completed
        softAssert.assertTrue(automationLaunchesPage.isSearchFieldPresent(),
                "Search field is not present on 'Automation launches' page");
        softAssert.assertTrue(automationLaunchesPage.getFilters().isAddFilterButtonPresent(),
                "Filter button is not present on 'Automation launches' page");

        PaginationR testRunsPagination = automationLaunchesPage.getPagination();
        softAssert.assertEquals(testRunsPagination.getNumberOfItemsOnThePage(),
                automationLaunchesPage.getNumberOfTestRunCards(),
                "Number of test cards differ to pagination info");
        softAssert.assertAll();
    }

    @Test(
            enabled = false,
            groups = TestGroups.MINIMAL_ACCEPTANCE,
            dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches"
    )
    public void webJobRun() {
        WebDriver webDriver = super.getDriver();

        Launcher createdWebLauncher = launcherService.addDefaultUiTestsLauncher(
                projectId, webRepoId, launcherNameWeb + " JOB", "main", "web"
        );
        launcherService.launchLauncher(projectId, webRepoId, createdWebLauncher);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, projectKey);
        LaunchCard webRunCard = automationLaunchesPage.findTestRunCardByName(launcherNameWeb + " JOB")
                                                      .waitFinish(Duration.ofSeconds(800), Duration.ofSeconds(10));

        Assert.assertEquals(webRunCard.getStatistics().getText(), expectedWebResult, "Test run result differ expected");
        Assert.assertEquals(webRunCard.getBrowser()
                                      .getType(), PlatformTypeR.CHROME, "Can't find info about chrome browser");
        Assert.assertTrue(webRunCard.getStateAndTimeFromStart().getText()
                                    .matches(expectedStartedAgoRegex), "Test card should have info about when it test started");
        Assert.assertTrue(webRunCard.getMenuButton().isPresent(), "Can't find settings button on card");
        Assert.assertTrue(webRunCard.getCheckBox().isPresent(), "Checkbox of web test card is not active");

        PaginationR testRunsPagination = automationLaunchesPage.getPagination();
        Assert.assertEquals(
                testRunsPagination.getNumberOfItemsOnThePage(), automationLaunchesPage.getNumberOfTestRunCards(),
                "Number of test cards differ to pagination info"
        );

        webRunCard.getRootExtendedElement().click();
        TestRunResultPageR testRunResultPage = TestRunResultPageR.getPageInstance(webDriver);
        testRunResultPage.assertPageOpened();

        for (ResultTestMethodCardR testCard : testRunResultPage.getTestCards()) {
            testRunResultPage.getCertainTest(testCard.getCardTitle().getText())
                             .clickOnCard();

            TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(webDriver);
            testDetailsPage.assertPageOpened();

            String testName = testDetailsPage.getTitle();
            String sessionId = testDetailsPage.getSessionsIds().get(0);

            Assert.assertTrue(testDetailsPage.isVideoPresent(sessionId), "Video should be present in test " + testName);
            Assert.assertTrue(testDetailsPage.isRandomScreenshotLogContainsImage(), "Screenshots should be present in test " + testName);
            Assert.assertTrue(testDetailsPage.isSessionLogsPresent(sessionId), "Session log should be present in test " + testName);
            Assert.assertTrue(testDetailsPage.isLogMessagesPresent(), "Logs should be present in test " + testName);

            webDriver.navigate().back();
            super.pause(3);

            Assert.assertTrue(testRunResultPage.isPageOpened(), "Test run result page is not opened after back from test " + testName);
        }
    }

    @Test(
            enabled = false,
            groups = TestGroups.MINIMAL_ACCEPTANCE,
            dependsOnMethods = "webJobRun"
    )
    public void webRunTestVerificationOfResultPage() {
        WebDriver webDriver = super.getDriver();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, projectKey);
        LaunchCard testRunCard = automationLaunchesPage.findTestRunCardByName(launcherNameWeb + " JOB");
        testRunCard.getRootExtendedElement().click();

        TestRunResultPageR testRunResultPage = TestRunResultPageR.getPageInstance(webDriver);
        testRunResultPage.assertPageOpened();

        if (testRunResultPage.getCollapseTestRunView().isStateMatches(Condition.VISIBLE)) {
            testRunResultPage.collapseTestRunViewHeader();
        }

        Assert.assertEquals(testRunResultPage.getTitle(), launcherNameWeb + " JOB",
                "Expected page title didn't match the actual");

        Assert.assertEquals(
                testRunResultPage.expandTestRunViewHeader().getBrowser().getType(), PlatformTypeR.CHROME,
                "Result page card should has info about chrome browser"
        );
        Assert.assertTrue(
                testRunResultPage.expandTestRunViewHeader().getTime().matches(expectedStartedAgoRegex),
                "Result test card should have info about when it test started"
        );
        Assert.assertTrue(
                testRunResultPage.getResultActionBar().isSettingsButtonPresent(),
                "Can't find settings button on result card"
        );
        Assert.assertTrue(
                testRunResultPage.isNumberOfTestsAsExpected(3),
                "Number of tests differs with number in pagination"
        );
        Assert.assertTrue(testRunResultPage.isAllFailedTestsHaveErrorTrace(), "Some failed tests has no error trace");
        Assert.assertTrue(testRunResultPage.isAllPassedTestsHaveNoErrorTrace(), "Some passed tests has error trace");

        ActionsBlockR resultBar = testRunResultPage.getActionsBlockR();
        Assert.assertTrue(resultBar.isCheckboxPresent(), "Can't find checkbox in result page bar");
        Assert.assertTrue(resultBar.isSortByLabels(), "Can't find Sort by labels in result page bar");

        for (ResultTestMethodCardR methodCard : testRunResultPage.getTestCards()) {
            Assert.assertTrue(methodCard.isCheckboxPresent(), "Can't find checkbox on method card " + methodCard.getCardTitleText());
            Assert.assertTrue(methodCard.isDurationPresent(), "Can't find test duration on method card" + methodCard.getCardTitleText());
            Assert.assertTrue(methodCard.isStabilityPresent(), "Can't find test stability on method card" + methodCard.getCardTitleText());
            Assert.assertEquals(methodCard.getTestMaintainer(), "anonymous", "Unexpected test owner on method card " + methodCard.getCardTitleText());

            if (methodCard.isErrorStacktracePresent()) {
                PageUtil.guaranteedToHideDropDownList(webDriver);
                CardMenuR menu = methodCard.getCardMenuR();

                Assert.assertTrue(
                        menu.getMarkAsPassed().isStateMatches(Condition.VISIBLE),
                        "Can't find mark as passed on method card " + methodCard.getCardTitleText()
                );
                Assert.assertTrue(
                        methodCard.getLinkIssueButton().isStateMatches(Condition.VISIBLE),
                        "Can't find link issue button on method card " + methodCard.getCardTitleText()
                );

                testRunResultPage.hideDropdownMenu();
            } else {
                Assert.assertTrue(
                        methodCard.getCardMenuR().getMarkAsFailed().isStateMatches(Condition.VISIBLE),
                        "Can't find mark as failed on method card " + methodCard.getCardTitleText()
                );

                testRunResultPage.hideDropdownMenu();
            }

            ResultSessionWindowR sessionWindow = testRunResultPage.openResultSessionWindow(methodCard);
            Assert.assertTrue(
                    sessionWindow.isUIObjectPresent(5),
                    "Can't open session info window for test method " + methodCard.getCardTitleText()
            );

            sessionWindow.closeWindow();
            Assert.assertFalse(
                    sessionWindow.isUIObjectPresent(3),
                    "Session info window for test method should be closed after clicking on 'Close' button!"
            );
        }

        for (String testName : testRunResultPage.getAllTestsNames()) {
            Assert.assertTrue(expectedMethodNames.contains(testName), String.format("Test name %s not as expected!", testName));
        }
    }

    @Test(
            enabled = false,
            dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches"
    )
    public void z_abortTestRun() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        Launcher createdWebLauncher = launcherService.addDefaultUiTestsLauncher(projectId, webRepoId, launcherNameWeb + " ABORT", "main", "web");
        launcherService.launchLauncher(projectId, webRepoId, createdWebLauncher);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(launcherNameWeb + " ABORT", false);

        testRunCard.clickMenu().getAbort().click();
        new AbortModal(getDriver()).getAbortButton().click();

        String expectedPopup = "Launch is aborted";
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(automationLaunchesPage.waitIsPopUpMessageAppear(expectedPopup),
                "This popup message is not found!");
        softAssert.assertEquals(testRunCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.ABORTED);
        softAssert.assertAll();
    }

    @Maintainer(TestMaintainers.DKAZAK)
    @TestCaseKey({"ZTP-3922", "ZTP-3923", "ZTP-3926"})
    @TestLabel(name = TestLabels.Name.GROUP, value = TestLabels.Value.LAUNCHES)
    @Test(
            enabled = false,
            dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches"
    )
    public void bulkActionsCheckboxPaginationAndAbort() {
        WebDriver webDriver = super.getDriver();

        Launcher createdWebLauncher = launcherService.addDefaultUiTestsLauncher(
                projectId, webRepoId, launcherNameWeb + "BULK ABORT", "main", "web"
        );
        launcherService.launchLauncher(projectId, webRepoId, createdWebLauncher);

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, projectKey);

        // ZTP-3922
        Assert.assertTrue(launchesPage.isSelectAllLaunchesCheckboxPresent(), "Checkbox is not visible!");

        PaginationR topPagination = launchesPage.getTopPagination();
        Assert.assertTrue(topPagination.isFullPaginationPresent(), "Top pagination is not Present!");

        //ZTP-3923
        PaginationR bottomPagination = launchesPage.getBottomPagination();
        Assert.assertTrue(bottomPagination.isFullPaginationPresent(), "Bottom pagination is not Present!");

        LaunchCard testRunCard = launchesPage.getCertainTestRunCard(launcherNameWeb + "BULK ABORT", false);
        Assert.assertTrue(testRunCard.waitForCheckBoxInProgress(), "Card checkbox while in progress is not visible!");

        testRunCard.clickCheckboxInProgress();
        launchesPage.getActionsBlockR()
                    .getBulkActionSection()
                    .clickAbort()
                    .clickAbortModalButton();

        Assert.assertTrue(
                launchesPage.waitIsPopUpMessageAppear("Launch is aborted"),
                "This popup message is not found!"
        );
        // ZTP-3926
        Assert.assertEquals(testRunCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.ABORTED);
        Assert.assertFalse(launchesPage.getActionsBlockR()
                                       .isBulkActionSectionPresent(), "Bulk actions should not be visible!");
        Assert.assertTrue(testRunCard.isReviewedBadgePresent(), "Reviewed badge is not present!");
        Assert.assertEquals(testRunCard.getLaunchCardAttributes()
                                       .getTime(), "Aborted now", "Card time does not match!");
    }

    @TestCaseKey("ZTP-3928")
    @Maintainer(TestMaintainers.DKAZAK)
    @TestLabel(name = TestLabels.Name.GROUP, value = TestLabels.Value.LAUNCHES)
    @Test(dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches", alwaysRun = true)
    public void deleteAllLaunchesFromPage() {
        WebDriver webDriver = super.getDriver();

        testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());

        List<Launch> launches = testRunService.startMultipleLaunches(projectKey, 2);
        launches.forEach(launch -> testRunService.finishLaunch(launch.getId()));

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, projectKey);
        do {
            launchesPage.clickSelectAllLaunchesCheckbox();

            List<LaunchCard> launchCards = launchesPage.getAllTestRunCards();
            List<String> launchNamesBeforeDeletion = StreamUtils.mapToList(launchCards, LaunchCard::getCardName);

            for (LaunchCard launchCard : launchCards) {
                Assert.assertTrue(launchCard.isCheckBoxSelected(), "Checkbox is not selected for: " + launchCard.getCardName());
            }

            launchesPage.getActionsBlockR()
                        .getBulkActionSection()
                        .delete();

            Assert.assertEquals(
                    launchesPage.getPopUp(),
                    "Launches have been successfully deleted",
                    "Popup message is not found, after deleting launches!"
            );

            launchesPage.waitPopupDisappears();

            Assert.assertFalse(
                    launchesPage.getActionsBlockR().isBulkActionSectionPresent(),
                    "Bulk actions should not be visible!"
            );

            StreamUtils.mapToStream(launchesPage.getAllTestRunCards(), LaunchCard::getCardName)
                       .forEach(launchName -> Assert.assertFalse(
                               launchNamesBeforeDeletion.contains(launchName),
                               "Launch with name '" + launchName + "' should be deleted!"
                       ));
        } while (!launchesPage.getEmptyPlaceholder().isUIObjectPresent());
    }

    @Test(
            enabled = false,
            groups = TestGroups.MINIMAL_ACCEPTANCE,
            dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches"
    )
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-3934", "ZTP-1206"})
    public void relaunchLaunches() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        Launcher createdApiLauncher = launcherService.addDefaultApiTestsLauncher(projectId, apiRepoId, launcherNameApi + " Relaunch", "api");
        launcherService.launchLauncher(projectId, apiRepoId, createdApiLauncher);

        String launcherName = launcherNameWeb + " #1";
        String launcherName2 = launcherNameWeb + " #2";

        Launcher createdWebLauncher = launcherService.addDefaultUiTestsLauncher(projectId, webRepoId, launcherName, "main", "web");
        launcherService.launchLauncher(projectId, webRepoId, createdWebLauncher);

        Launcher createdWebLauncher2 = launcherService.addDefaultUiTestsLauncher(projectId, webRepoId, launcherName2, "main", "web");
        launcherService.launchLauncher(projectId, webRepoId, createdWebLauncher2);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        automationLaunchesPage.findTestRunCardByName(launcherName)
                              .waitFinish(Duration.ofSeconds(800), Duration.ofSeconds(10));
        automationLaunchesPage.findTestRunCardByName(launcherName2)
                              .waitFinish(Duration.ofSeconds(800), Duration.ofSeconds(10));

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launcherName, true);
        LaunchCard secondLaunch = automationLaunchesPage.getCertainTestRunCard(launcherName2, true);
        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();

        softAssert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getCardName());
        softAssert.assertTrue(secondLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + secondLaunch.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible!");

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().relaunch();

        pause(2);
        softAssert.assertTrue(
                firstLaunch.getStatus().getStatusColourFromCss().equals(TestRunStatusEnumR.QUEUED)
                        || firstLaunch.getStatus().getStatusColourFromCss().equals(TestRunStatusEnumR.IN_PROGRESS),
                "Status should be QUEUED or IN_PROGRESS for card: " + firstLaunch.getCardName());
        softAssert.assertTrue(
                secondLaunch.getStatus().getStatusColourFromCss().equals(TestRunStatusEnumR.QUEUED)
                        || secondLaunch.getStatus().getStatusColourFromCss().equals(TestRunStatusEnumR.IN_PROGRESS),
                "Status should be QUEUED or IN_PROGRESS for card: " + secondLaunch.getCardName());

        firstLaunch.waitForCheckBoxInProgress();
        softAssert.assertEquals(firstLaunch.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.IN_PROGRESS,
                "Colour should be " + TestRunStatusEnumR.IN_PROGRESS + " for card: " + firstLaunch.getCardName());
        secondLaunch.waitForCheckBoxInProgress();
        softAssert.assertEquals(secondLaunch.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.IN_PROGRESS,
                "Colour should be " + TestRunStatusEnumR.IN_PROGRESS + " for card: " + secondLaunch.getCardName());

        //  automationLaunchesPage.findTestRunCardByName(launcherName).waitFinish(Duration.ofSeconds(800), Duration.ofSeconds(10));
        //  automationLaunchesPage.findTestRunCardByName(launcherName2).waitFinish(Duration.ofSeconds(800), Duration.ofSeconds(10));

        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches", alwaysRun = true)
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5444")
    public void _selectionRemainsWhenFilterApplied() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage passedLaunch = PreparationUtil.startAndFinishLaunchWithTests(1, 0, projectKey);

        TestClassLaunchDataStorage failedLaunch = PreparationUtil.startAndFinishLaunchWithTests(0, 1, projectKey);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        LaunchCard passedCard = automationLaunchesPage.getCertainTestRunCard(passedLaunch.getLaunch()
                                                                                         .getName(), true);
        passedCard.clickCheckbox();

        softAssert.assertTrue(passedCard.isCheckBoxSelected(), "Checkbox is not selected for: " + passedCard.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible after selecting passed test!");

        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        pause(2);
        softAssert.assertTrue(passedCard.isCheckBoxSelected(), "Checkbox is not selected for: " + passedCard.getCardName() + ", after selecting filter!");
        softAssert.assertFalse(automationLaunchesPage.isCertainLaunchAppears(failedLaunch.getLaunch().getName()),
                "Only cards with status " + TestRunStatusEnumR.PASSED + " should be visible.");
        automationLaunchesPage.getAllTestRunCards().forEach(card -> {
            softAssert.assertEquals(
                    card.getStatus().getStatusColourFromCss(),
                    TestRunStatusEnumR.PASSED,
                    "Card status should be " + TestRunStatusEnumR.PASSED + ", for card: " + passedLaunch.getLaunch()
                                                                                                        .getName()
            );
        });
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible after selecting PASSED filter!");

        automationLaunchesPage.getFilters().clickResetFilter();
        passedCard.clickCheckbox();
        LaunchCard failedCard = automationLaunchesPage.getCertainTestRunCard(failedLaunch.getLaunch()
                                                                                         .getName(), true);
        failedCard.clickCheckbox();

        softAssert.assertTrue(failedCard.isCheckBoxSelected(), "Checkbox is not selected for: " + failedCard.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible after selecting failed test!");

        automationLaunchesPage.getActionsBlockR().openStatusSettings();
        automationLaunchesPage.getActionsBlockR().clickClearSelectionButton();
        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        pause(2);
        // softAssert.assertFalse(failedCard.isCheckBoxSelected(), "Checkbox is selected for failed test when it should not be.");
        softAssert.assertFalse(automationLaunchesPage.isCertainLaunchAppears(failedLaunch.getLaunch().getName()),
                "Only cards with status " + TestRunStatusEnumR.PASSED + " should be visible.");
        softAssert.assertEquals(automationLaunchesPage.getActionsBlockR().getBulkActionSection()
                                                      .getSelectedCardsAmountText(),
                "1 selected (0 visible)", "Selected cards amount do not match!");
        automationLaunchesPage.getAllTestRunCards().forEach(card -> {
            softAssert.assertEquals(
                    card.getStatus().getStatusColourFromCss(),
                    TestRunStatusEnumR.PASSED,
                    "Card status should be " + TestRunStatusEnumR.PASSED + ", for card: " + passedLaunch.getLaunch()
                                                                                                        .getName()
            );
        });
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible after verifying failed test not visible!");

        automationLaunchesPage.getActionsBlockR().openStatusSettings();
        automationLaunchesPage.getActionsBlockR().clickClearSelectionButton();
        automationLaunchesPage.getActionsBlockR()
                              .selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_IN_PROGRESS);

        pause(2);
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_IN_PROGRESS),
                "Status '" + Dropdown.DropdownItemsEnum.STATUS_IN_PROGRESS + "' was not selected properly");
        softAssert.assertFalse(automationLaunchesPage.getActionsBlockR()
                                                     .isBulkActionSectionPresent(), "Bulk actions should not be visible when filter IN PROGRESS is selected!");
        softAssert.assertEquals(automationLaunchesPage.getEmptyPlaceholder().getEmptyPlaceHolderTitle(),
                MessageEnum.NO_RESULT_MESSAGE.getDescription(), "Message about the absence of tests is not as expected");
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "_checkMainElementsOnAutomationLaunchesPageWithoutLaunches", alwaysRun = true, priority = Integer.MAX_VALUE)
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5443")
    public void selectedLaunchesNotVisibleOnNextPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        List<Launch> startedTestRuns = testRunService.startMultipleLaunches(projectKey, 3);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
        }

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(startedTestRuns.get(2).getName(), true);
        testRunCard.clickCheckbox();

        softAssert.assertTrue(testRunCard.isCheckBoxSelected(), "Checkbox is not selected for: " + testRunCard.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible!");

        PaginationR pagination = automationLaunchesPage.getPagination();
        int numberOfItems = Integer.parseInt(pagination.getNumberOfItemsOnThePage());

        while (numberOfItems < 21) {
            Launch launch = testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());
            testRunService.finishLaunch(launch.getId());

            numberOfItems = Integer.parseInt(pagination.getNumberOfItemsOnThePage());
        }
        pagination.clickToNextPagePagination();

        pause(2);
        softAssert.assertEquals(automationLaunchesPage.getActionsBlockR().getBulkActionSection()
                                                      .getSelectedCardsAmountText(),
                "1 selected (0 visible)", "Selected cards amount do not match!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-1176", "ZTP-1175", "ZTP-1180"})
    public void verifyNewLaunchAppearsInTheTopOfListAndProjectScoped() {
        SoftAssert softAssert = new SoftAssert();

        String newLauncherName = "Carina API".concat(RandomStringUtils.randomNumeric(5));

        Launch launch = testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());
        testRunService.finishLaunch(launch.getId());

        launcherService.addDefaultApiTestsLauncher(projectId, apiRepoId, newLauncherName, "api");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), projectKey);

        //ZTP-1175
        AutomationLaunchesPage automationLaunchesPage = testCasesPage.getNavigationMenu().toTestRunsPage();
        automationLaunchesPage.assertPageOpened();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), projectKey);

        launcherPage.chooseRepo(PUBLIC_REPO_NAME)
                    .clickOnRepository()
                    .getLauncherWithName(newLauncherName)
                    .ifPresent(LauncherItem::clickOnLauncherName);

        automationLaunchesPage = launcherPage.launchLauncher();

        //ZTP-1180
        softAssert.assertTrue(automationLaunchesPage.getBreadcrumbs().isBreadcrumbPresent(projectKey),
                "Correct project page should be opened !");
        //ZTP-1176
        softAssert.assertEquals(newLauncherName, automationLaunchesPage.getAllTestRunCards().get(0).getCardName(),
                "Card name is not as expected - card should be at top !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4238")
    public void verifyDateTimeToolTipAfterHoveringLaunchedDate() {
        WebDriver webDriver = super.getDriver();

        Launch launch = testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());

        String timeZone = (String) ((JavascriptExecutor) webDriver).executeScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;");
        ZonedDateTime zonedDateTime = launch.getStartedAt().atZoneSameInstant(ZoneId.of(timeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm:ss a");
        String expectedTime = zonedDateTime.format(formatter);

        testRunService.finishLaunch(launch.getId());

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, projectKey);

        LaunchCard launchCard = launchesPage.getCertainTestRunCard(launch.getName(), true);
        String launchedTime = launchCard.getLaunchCardAttributes()
                                        .hoverLaunchedTimeAndGetToolTipValue();
        Assert.assertEquals(
                launchedTime.toLowerCase(),
                expectedTime.toLowerCase(),
                "Time is not as excepted!"
        );
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5751")
    public void verifyToolTipValuesAfterHoveringLaunchStatisticsIcons() {
        String LAUNCHER_NAME = "ToolTip-statistics-test";
        Launcher launcher = launcherService.addDefaultUiTestsLauncher(
                projectId, webRepoId, LAUNCHER_NAME, "main", "demo"
        );

        Config.Parameter envVar = new Config.Parameter().setName("REPORTING_MILESTONE_NAME")
                                                        .setDefaultValue(milestoneName);
        launcher.getConfig()
                .getEnvVars()
                .add(envVar);

        launcherService.launchLauncher(projectId, webRepoId, launcher);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);

        automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME)
                              .waitFinish(Duration.ofSeconds(500), Duration.ofSeconds(5));

        AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        LaunchCard launchCard = automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME);

        String timeZone = (String) ((JavascriptExecutor) getDriver()).executeScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;");
        String startedLaunchDate = launchCard.getLaunchCardAttributes().hoverLaunchedTimeAndGetToolTipValue();

        ZonedDateTime startedLaunchDateUTC = DateUtil.parseDate(
                startedLaunchDate,
                ZoneId.of(timeZone),
                ZoneId.of("UTC"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm:ss a", Locale.ENGLISH)
        );

        String exceptedExecutionLogLabel = "Execution Log";
        String exceptedMilestoneIconToolTipMessage = "Milestone";
        String exceptedDurationIconToolTipMessage = "Duration";
        String exceptedEnvironmentIconToolTipMessage = "Environment: PROD";

        Assert.assertEquals(
                launchCard.getLaunchCardAttributes().hoverDurationIconAndGetToolTipValue(),
                exceptedDurationIconToolTipMessage,
                "Duration icon tooltip message is not as excepted !"
        );
        Assert.assertEquals(
                launchCard.getLaunchCardAttributes().hoverMilestoneIconAndGetToolTipValue(),
                exceptedMilestoneIconToolTipMessage,
                "Milestone icon tooltip message is not as excepted !"
        );
        Assert.assertEquals(
                launchCard.hoverEnvironmentAndGetToolTipValue(),
                exceptedEnvironmentIconToolTipMessage,
                "Environment tooltip message is not as excepted !"
        );

        String executionLogToolTipValue = launchCard.getLaunchCardConfiguration().expandArtifacts().hoverExecutionLogAndGetToolTipValue();

        Pattern fullPattern = Pattern.compile("^Execution Log \\((\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) UTC\\)$");

        Assert.assertTrue(
                Pattern.matches(String.valueOf(fullPattern), executionLogToolTipValue),
                "Execution log tooltip value doesn't match the expected pattern: " + fullPattern
        );

        String executionLogLabel = StringUtil.getByPattern(executionLogToolTipValue, "^(.*?)\\s*\\(.*?\\)$");
        LocalDateTime executionTime = DateUtil.getByPatternAndFormat(executionLogToolTipValue, "\\((.*?)\\sUTC\\)", "yyyy-MM-dd HH:mm:ss");

        Assert.assertEquals(exceptedExecutionLogLabel, executionLogLabel, "Execution log label mismatch !");
        Assert.assertTrue(
                Duration.between(executionTime, startedLaunchDateUTC).abs().compareTo(Duration.ofMinutes(5)) <= 0,
                "Execution log time is not within expected tolerance!"
        );
    }

}
