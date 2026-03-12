package com.zebrunner.automation.gui.smoke;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.launcher.domain.Config;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.LogAndScreenshotItem;
import com.zebrunner.automation.api.reporting.domain.LogItem;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.api.tcm.domain.SharedStepsBunch;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.automation.api.tcm.domain.TestCaseStepType;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.GitlabProperties;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.config.TestSuites;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.DemoTest;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.CustomCapabilitiesModal;
import com.zebrunner.automation.gui.reporting.issue.JiraIssuePreview;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.CardUpdateModalR;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.test.LogLevel;
import com.zebrunner.automation.gui.reporting.test.LogRow;
import com.zebrunner.automation.gui.reporting.test.ScreenshotItem;
import com.zebrunner.automation.gui.reporting.test.ScreenshotsView;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.gui.reporting.test.TestHeader;
import com.zebrunner.automation.gui.tcm.TcmLabelPreview;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.TcmType;
import com.zebrunner.automation.legacy.UrlUtils;
import com.zebrunner.automation.util.FileUtils;
import com.zebrunner.carina.utils.report.ReportContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Maintainer("obabich")
public class TestDetailsTest extends LogInBase {

    private static final List<String> LOG_LEVELS = List.of("FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");

    private static final String LAUNCHER_NAME = "Demo Suite";
    private static final String REPORTING_RUN_ENVIRONMENT = "PROD";

    private final List<Long> launchIds = new ArrayList<>();

    @BeforeClass
    public void onBeforeClass() {
        GitlabProperties gitlabProperties = ConfigHelper.getGitlabProperties();

        Long repoId = launcherService.addGitRepo(
                project.getId(),
                gitlabProperties.getUrl() + "/zebrunner/tests/tests-ui",
                gitlabProperties.getUsername(),
                gitlabProperties.getAccessToken(),
                GitProvider.GITLAB.toString()
        );

        Launcher launcher = launcherService.addDefaultUiTestsLauncher(
                project.getId(), repoId, LAUNCHER_NAME, "main", TestSuites.DEMO
        );

        Config.Parameter env = new Config.Parameter().setName("REPORTING_RUN_ENVIRONMENT");
        env.setName("REPORTING_RUN_ENVIRONMENT");
        env.setDefaultValue(REPORTING_RUN_ENVIRONMENT);
        launcher.getConfig().getEnvVars().add(env);

        Config.Parameter idleTimeout = new Config.Parameter();
        idleTimeout.setName("zebrunner:idleTimeout");
        idleTimeout.setValue("120");
        launcher.getConfig().getCustomCapabilities().add(idleTimeout);

        launcherService.launchLauncher(project.getId(), repoId, launcher);
    }

    @AfterMethod(alwaysRun = true)
    public void deleteLaunches() {
        launchIds.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    private List<TestCase> addTestCases() {
        TestSuite testSuite = tcmService.createTestSuite(project.getId(), new TestSuite("Suite for automation"));

        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TestCase.Step regular = TestCase.Step.regular(TestCaseStep.withRandomActionAndExpectedResult());
            SharedStepsBunch shared = tcmService.createSharedStep(project.getId(), SharedStepsBunch.generateRandom(1));

            TestCase.Step sharedStep = TestCase.Step.shared(shared);

            TestCase testCase = TestCase.builder()
                                        .title("Test case № ".concat(RandomStringUtils.randomNumeric(7)))
                                        .steps(List.of(regular, sharedStep))
                                        .build();
            testCase = tcmService.createTestCase(project.getId(), testSuite.getId(), testCase);

            testCases.add(testCase);
        }

        return testCases;
    }

    private boolean isValidLogLevel(String logLevel, String logLevelToFilter) {
        List<String> logLevels = Arrays.stream(LogLevel.values()).map(Enum::name).collect(Collectors.toList());
        int currentLevelIndex = logLevels.indexOf(logLevelToFilter);
        int logLevelIndex = logLevels.indexOf(logLevel);

        if (logLevelIndex == -1) {
            return false;
        }

        return logLevelIndex <= currentLevelIndex;
    }

    //==================================== Test ===========================================================

    @TestCaseKey({"ZTP-1341", "ZTP-1355"})
    @Test(groups = {TestGroups.MINIMAL_ACCEPTANCE, "test_details"}, priority = 5)
    public void verifyMainTestDetailPageElementsAndOpeningViaClickOnTest() {
        WebDriver webDriver = super.getDriver();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());
        LaunchCard launchCard = automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME)
                                                      .waitFinish(Duration.ofSeconds(500), Duration.ofSeconds(15));

        TestRunResultPageR launchPage = launchCard.toTests();
        launchPage.assertPageOpened();

        launchPage.getCertainTest(DemoTest.TEST_NAME).clickOnCard();

        TestDetailsPageR testPage = new TestDetailsPageR(webDriver);
        testPage.assertPageOpened();

        Assert.assertEquals(testPage.getPageHeader().getEnv(), REPORTING_RUN_ENVIRONMENT, "Env is not as expected!");

        String sessionId = testPage.getSessionsIds().get(0);
        Assert.assertTrue(testPage.isVideoPresent(sessionId), "Video should be present in test " + DemoTest.TEST_NAME);
        Assert.assertTrue(testPage.isLogMessagesPresent(), "Logs should be present in test " + DemoTest.TEST_NAME);
        Assert.assertTrue(testPage.isRandomScreenshotLogContainsImage(), "Screenshots should be present in test " + DemoTest.TEST_NAME);

        webDriver.navigate().back();
        Assert.assertTrue(launchPage.isPageOpened(), "Test result page should be opened when clicking on 'Back' browser arrow");
    }

    @Test(groups = "test_details", priority = 5)
    @TestCaseKey({"ZTP-1346"})
    public void verifyScreenshotOpening() {
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        LaunchCard launchCard = automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME).waitFinish(
                Duration.ofSeconds(500),
                Duration.ofSeconds(15));

        TestRunResultPageR testRunResultPage = launchCard.toTests();
        testRunResultPage.assertPageOpened();

        testRunResultPage.getCertainTest(DemoTest.TEST_NAME).clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        testDetailsPage.assertPageOpened();

        SoftAssert softAssert = new SoftAssert();

        String pageUrl = getDriver().getCurrentUrl();
        long testRunId = Long.parseLong(Objects.requireNonNull(UrlUtils.extractValueBetweenSegments(pageUrl, "automation-launches", "tests")));
        long testId = Long.parseLong(Objects.requireNonNull(UrlUtils.extractValueAfterSegment(pageUrl, "tests")));

        List<LogAndScreenshotItem> screenshotItems = apiHelperService.getLogsAndScreenshots(testRunId, testId)
                                                                     .stream()
                                                                     .filter(item ->
                                                                             item.getKind()
                                                                                 .equalsIgnoreCase("screenshot"))
                                                                     .collect(Collectors.toList());

        testDetailsPage.swipeToLogWithScreenshot();

        Optional<List<LogRow>> logsWithScreenshot = testDetailsPage.getLogsTable().getLogsWithScreenshots();

        if (logsWithScreenshot.isEmpty()) {
            softAssert.fail("No logs with screenshot at all!");
            softAssert.assertAll();
        } else {
            LogRow logWithScreen = logsWithScreenshot.get().get(0);

            logWithScreen.clickOnScreenshot();
            ScreenshotsView screenshotsView = testDetailsPage.getScreenshotsView();
            String bigScreenSrc = screenshotsView.getBigScreenshotSrc();

            List<ScreenshotItem> smallScreenshots = testDetailsPage
                    .getScreenshotsView()
                    .getAllScreenshotsPanel()
                    .getScreenshots();

            ScreenshotItem activeScreenshot = smallScreenshots.get(0);

            softAssert.assertEquals(screenshotItems.size(), smallScreenshots.size(),
                    "Number of screenshots on the screenshots panel should be the same as on the test details page!");

            softAssert.assertEquals(logWithScreen.getScreenshotSrc(), activeScreenshot.getSmallImgSrc(),
                    "Screenshot in the logs should have the same path as selected(small)!");

            softAssert.assertEquals(bigScreenSrc,
                    activeScreenshot.getSmallImgSrc().split("\\?")[0],
                    "Opened(big) screenshot should have the same path as selected(small)"
                            + " except '?version=thumbnail' parameter !");

            softAssert.assertTrue(
                    screenshotsView.isBigScreenshotPresent(),
                    "Big screenshot should be visible!");
            softAssert.assertTrue(screenshotsView.getCloseBtn().isStateMatches(Condition.CLICKABLE),
                    "Close button should be clickable!");
            softAssert.assertTrue(screenshotsView.getDownloadBtn().isStateMatches(Condition.CLICKABLE),
                    "Download button should be clickable!");
            softAssert.assertTrue(screenshotsView.getHideScreenListBtn().isStateMatches(Condition.CLICKABLE),
                    "Button to hide list of screenshots should be clickable!");

            screenshotsView.clickHideListOfScreenshots();
            screenshotsView.getAllScreenshotsPanel().getRootExtendedElement().waitUntilElementDisappear(3);
            softAssert.assertFalse(screenshotsView.getAllScreenshotsPanel().getRootExtendedElement().isVisible(5),
                    "List with small screenshots shouldn't be visible!");
            screenshotsView.clickHideListOfScreenshots();
            softAssert.assertTrue(screenshotsView.getAllScreenshotsPanel().getRootExtendedElement().isVisible(5),
                    "List with small screenshots shouldn't be visible!");

            screenshotsView.closeScreenshotsView();

            screenshotsView.getRootExtendedElement().waitUntilElementDisappear(5);
            softAssert.assertFalse(screenshotsView.getRootExtendedElement().isVisible(3),
                    "After clicking on 'Close' button screenshot view shouldn't be visible!");
        }
        softAssert.assertAll();
    }


    @SneakyThrows
    @Test(priority = 5)
    @TestCaseKey("ZTP-1347")
    public void verifyDownloadArtifacts() {
        WebDriver webDriver = super.getDriver();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());
        LaunchCard launchCard = automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME).waitFinish(
                Duration.ofSeconds(500),
                Duration.ofSeconds(5)
        );

        TestRunResultPageR testRunResultPage = launchCard.toTests();
        testRunResultPage.assertPageOpened();

        testRunResultPage.getCertainTest(DemoTest.TEST_NAME).clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(webDriver);
        testDetailsPage.assertPageOpened();
        testDetailsPage.getPageHeader().clickDownloadArtifacts();

        String pageUrl = webDriver.getCurrentUrl();
        long testId = Long.parseLong(Objects.requireNonNull(UrlUtils.extractValueAfterSegment(pageUrl, "tests")));

        String fileName = testId +
                "__" + testDetailsPage.getTitle()
                                      .replace("-", "_")
                                      .replace(" ", "_")
                                      .concat(".zip");

        String fileNameToFind = "logs.txt";
        String pattern = "artifacts/screenshot.*\\.png";

        String expectedContent = testDetailsPage.getLogsTable().getLogs()
                                                .orElseThrow()
                                                .get(0).getMessage().split("\n")[0];

        Assert.assertFalse(expectedContent.isEmpty(), "Log message shouldn't be empty!");

        URL url = FileUtils.getFileUrl(webDriver, fileName);

        Optional<File> file = FileUtils.waitFile(url, 30);

        Path baseDirectoryPath = ReportContext.getBaseDirectory()
                                              .resolve("downloads");

        String zipFilePath = baseDirectoryPath + "/" + fileName;

        if (file.isPresent()) {

            URL copiedFileUrl = FileUtils.copyFileFromURL(url, zipFilePath);
            FileUtils.waitFile(copiedFileUrl, 30);

            Optional<File> foundFile = FileUtils.getFileFromZip(zipFilePath, fileNameToFind);

            foundFile.ifPresent(f -> {

                Optional<String> content = FileUtils.getContentFromFile(f);

                content.ifPresentOrElse(
                        c -> Assert.assertTrue(c.contains(expectedContent), "File content doesn't contain " + expectedContent),
                        () -> Assert.fail("No content in file " + fileNameToFind)
                );
            });

            List<File> foundFiles = FileUtils.findFilesByPattern(zipFilePath, pattern);

            Assert.assertFalse(foundFiles.isEmpty(), "Zip file should contain files with pattern in name " + pattern);

            foundFiles.forEach(png -> Assert.assertTrue(FileUtils.isPngImage(foundFiles.get(0)), "Should be png file with name " + png.getName()));

        } else {
            Assert.fail("Downloaded artifacts was not found by URL  " + url);
        }
    }

    @Test(priority = 5)
    public void verifyExecutionLogLink() {
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        LaunchCard launchCard = automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME)
                                                      .waitFinish(Duration.ofSeconds(500), Duration.ofSeconds(5));

        launchCard.getLaunchCardConfiguration()
                  .clickExecutionLogs();

        String fileName = "console.log";
        String expectedContent = "Cloning into";

        URL url = FileUtils.getFileUrl(getDriver(), fileName);

        Optional<File> file = FileUtils.waitFile(url, 30);

        if (file.isPresent()) {
            Optional<String> content = FileUtils.getContentFromFile(file.get());

            content.ifPresentOrElse(
                    c -> Assert.assertTrue(c.contains(expectedContent), "File content doesn't contain " + expectedContent),
                    () -> Assert.fail("No content in file " + fileName)
            );
        } else {
            Assert.fail("Downloaded execution logs was not found by URL  " + url);
        }
    }

    @Test(groups = {TestGroups.MINIMAL_ACCEPTANCE, "test_details"})
    @TestCaseKey("ZTP-1343")
    public void verifyMarkingAsPassed() {
        WebDriver webDriver = super.getDriver();

        String launchName = "Marking as Passed launch " + project.getKey() + " № " + RandomStringUtils.randomNumeric(7);
        String methodName = "Passed test № " + RandomStringUtils.randomNumeric(7);

        Long launchId = testRunService.startTestRunWithName(project.getKey(), launchName);
        launchIds.add(launchId);

        Long testId = testService.startTestWithMethodName(launchId, methodName);

        testService.finishTestAsResult(launchId, testId, "PASSED");
        testRunService.finishTestRun(launchId);

        TestRunResultPageR launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launchId);
        Assert.assertTrue(launchPage.isPageOpened(), "Test run results page was not opened!");

        launchPage.getCertainTest(methodName).clickOnCard();

        TestDetailsPageR testPage = TestDetailsPageR.getPageInstance(webDriver);
        testPage.assertPageOpened();

        CardUpdateModalR cardUpdateModal = testPage.getPageHeader().clickMarkAsFiled();
        cardUpdateModal.getSubmitButton().click();

        Assert.assertEquals(launchPage.getPopUp(), "Test was marked as failed");
        Assert.assertEquals(
                testPage.getTestHeader().getLeftCardBorderColor(), "#df4150",
                "Color on test header is not as expected!"
        );
        Assert.assertEquals(
                testPage.getTestHistory().getHistoryItemColorByTestId(testId), "#df4150",
                "Color on history tab is not as expected!"
        );
    }

    @TestCaseKey("ZTP-1344")
    @Test(groups = {TestGroups.MINIMAL_ACCEPTANCE, "test_details"})
    public void verifyMarkingAsFailed() {
        WebDriver webDriver = super.getDriver();

        String launchName = "Marking as Failed launch " + project.getKey() + " № " + RandomStringUtils.randomNumeric(7);
        String methodName = "Failed test № " + RandomStringUtils.randomNumeric(7);

        Long launchId = testRunService.startTestRunWithName(project.getKey(), launchName);
        launchIds.add(launchId);

        Long testId = testService.startTestWithMethodName(launchId, methodName);

        testService.finishTestAsResult(launchId, testId, "FAILED");
        testRunService.finishTestRun(launchId);

        TestRunResultPageR launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launchId);
        Assert.assertTrue(launchPage.isPageOpened(), "Test run results page was not opened!");

        launchPage.getCertainTest(methodName).clickOnCard();

        TestDetailsPageR testPage = TestDetailsPageR.getPageInstance(webDriver);
        testPage.assertPageOpened();

        CardUpdateModalR cardUpdateModal = testPage.getPageHeader().clickMarkAsPassed();
        cardUpdateModal.getSubmitButton().click();

        Assert.assertEquals(launchPage.getPopUp(), "Test was marked as passed");
        Assert.assertEquals(
                testPage.getTestHeader().getLeftCardBorderColor(), "#aee2c8",
                "Color on test header is not as expected!"
        );

        String historyItemColor = testPage.getTestHistory().getHistoryItemColorByTestId(testId);
        Assert.assertTrue(
                historyItemColor.equals("#44c480") || historyItemColor.equals("#aee2c8"),
                "Color on history tab is not as expected!"
        );
    }

    @TestCaseKey({"ZTP-1352", "ZTP-1353"})
    @Test(groups = {TestGroups.MINIMAL_ACCEPTANCE, "test_details"})
    public void possibilitySwitchingBetweenHistoryTabsAndMarkingAsForOldTests() {
        String launchName = "Launch to check history tab " + project.getKey() + " " + RandomStringUtils.randomAlphabetic(3);
        String methodName = "Test from " + launchName;

        Long launchId = testRunService.startTestRunWithName(project.getKey(), launchName);
        launchIds.add(launchId);

        Long testId = testService.startTestWithMethodName(launchId, methodName);

        testService.finishTestAsResult(launchId, testId, "PASSED");
        testRunService.finishTestRun(launchId);

        Long launchId1 = testRunService.startTestRunWithName(project.getKey(), launchName);
        launchIds.add(launchId1);

        Long testId1 = testService.startTestWithMethodName(launchId1, methodName);
        testService.finishTestAsResult(launchId1, testId1, "PASSED");
        testRunService.finishTestRun(launchId1);

        TestDetailsPageR testPage = new TestDetailsPageR(getDriver()).openPageDirectly(project.getKey(), launchId1, testId1);

        testPage.getTestHistory()
                .pollHistoryItemByTestId(testId)
                .ifPresent(Element::click);
        testPage.waitUntil(ExpectedConditions.urlContains("tests/" + testId), 3);

        Assert.assertTrue(
                testPage.getCurrentUrl().contains("tests/" + testId),
                "Page URL should contain test with id " + testId
        );
        super.pause(1);

        // ZTP-1353
        CardUpdateModalR cardUpdateModal = testPage.getPageHeader().clickMarkAsFiled();
        cardUpdateModal.getSubmitButton().click();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(
                testPage.getPopUp(),
                "Test was marked as failed"
        );
        softAssert.assertEquals(
                testPage.getTestHeader().getLeftCardBorderColor(),
                "#df4150",
                "Color on test header is not as expected!"
        );
        softAssert.assertEquals(
                testPage.getTestHistory().getHistoryItemColorByTestId(testId),
                "#df4150",
                "Color on history tab is not as expected!"
        );
        softAssert.assertAll();

        testPage.getTestHistory()
                .pollHistoryItemByTestId(testId1)
                .ifPresent(Element::click);
        testPage.waitUntil(ExpectedConditions.urlContains("tests/" + testId1), 3);

        softAssert.assertTrue(
                testPage.getCurrentUrl().contains("tests/" + testId1),
                "Page URL should contain test with id " + testId1
        );
        softAssert.assertEquals(
                testPage.getTestHistory().getHistoryItemColorByTestId(testId1),
                "#44c480",
                "Color on history tab of last test is not as expected!"
        );
        softAssert.assertAll();
    }

    @Test(groups = "test_details")
    @TestCaseKey("ZTP-4235")
    public void verifyTestLabels() {
        String testRunName = "Labels launch for project with key ".concat(project.getKey()).concat(" № ")
                                                                  .concat(RandomStringUtils.randomNumeric(3));
        String methodNamePassed = "Labels test № ".concat(RandomStringUtils.randomNumeric(7));

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);
        launchIds.add(testRunId);
        long passedTest = testService.startTestWithMethodName(testRunId, methodNamePassed);

        Label label1 = Label.builder()
                            .key("Platform")
                            .value("Zebrunner")
                            .build();
        Label label2 = Label.builder()
                            .key("Framework")
                            .value("Carina")
                            .build();

        ArrayList<Label> labels = new ArrayList<>(Arrays.asList(label1, label2));

        apiHelperService.addLabelsToTest(testRunId, passedTest, labels);
        testService.finishTestAsResult(testRunId, passedTest, "PASSED");
        testRunService.finishTestRun(Math.toIntExact(testRunId));

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testRunId);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                testRunResultPage.isPageOpened(),
                "Test run results page was not opened!");

        testRunResultPage.getCertainTest(methodNamePassed).clickOnCard();
        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();

        TestHeader testHeader = testDetailsPage.getTestHeader();
        softAssert.assertTrue(testHeader.isLabelsExpanded(), "Labels should be expanded by default!");
        testHeader.expandLabels();

        softAssert.assertTrue(testHeader.getCustomLabels().size() >= labels.size(),
                "Size of obtained artifact references contain linked labels!");

        labels.forEach(label -> testHeader.findLabelsByKey(label.getKey()).ifPresentOrElse(
                existingRef -> {

                    softAssert.assertEquals(existingRef.getTextValue(), label.getValue(),
                            "Obtained label value should be the same as in the linked label with key "
                                    + label.getKey());
                },
                () -> softAssert.fail(String.format("Label with key '%s' was not found!", label.getKey()))
        ));
        testHeader.collapseLabels();
        softAssert.assertTrue(testHeader.getCustomLabels().isEmpty(),
                "Size of obtained labels should be '0' when they are collapsed!");

        softAssert.assertAll();
    }

    @Test(groups = "test_details")
    @TestCaseKey("ZTP-3818")
    public void verifyArtifactReferences() {

        String testRunName = "Artifact references launch for project with key ".concat(project.getKey()).concat(" № ")
                                                                               .concat(RandomStringUtils.randomNumeric(3));
        String methodNamePassed = "Artifact references test № ".concat(RandomStringUtils.randomNumeric(7));

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);
        launchIds.add(testRunId);
        long passedTest = testService.startTestWithMethodName(testRunId, methodNamePassed);

        ArtifactReference documentation = ArtifactReference.builder()
                                                           .name("Zebrunner Documentation")
                                                           .value("https://zebrunner.com/documentation/")
                                                           .build();
        ArtifactReference google = ArtifactReference.builder()
                                                    .name("Google")
                                                    .value("https://www.google.com/")
                                                    .build();

        ArrayList<ArtifactReference> artifactReferences = new ArrayList<>(Arrays.asList(documentation, google));

        apiHelperService.addArtReferencesToTest(testRunId, passedTest, artifactReferences);
        testService.finishTestAsResult(testRunId, passedTest, "PASSED");
        testRunService.finishTestRun(Math.toIntExact(testRunId));

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testRunId);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                testRunResultPage.isPageOpened(),
                "Test run results page was not opened!");

        testRunResultPage.getCertainTest(methodNamePassed).clickOnCard();
        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();
        TestHeader testHeader = testDetailsPage.getTestHeader();
        testHeader.collapseArtifacts();
        testHeader.expandArtifacts();

        softAssert.assertEquals(testHeader.getArtifactReferenceLabels().size(), artifactReferences.size(),
                "Size of obtained artifact references should be the same as linked!");

        artifactReferences.forEach(linkedReference -> {
            testHeader.findArtifactReferenceByName(linkedReference.getName()).ifPresentOrElse(
                    existingRef -> {
                        softAssert.assertEquals(existingRef.getTextKey(), linkedReference.getName(),
                                "Obtained name should be the same as linked!");
                        softAssert.assertEquals(existingRef.getHref(), linkedReference.getValue(),
                                "Obtained link value should be the same as in the linked reference with name "
                                        + linkedReference.getName());
                    },
                    () -> softAssert.fail(String.format("Reference with name %s was not found!", linkedReference.getName()))
            );
        });
        testHeader.collapseArtifacts();
        softAssert.assertEquals(testHeader.getArtifactReferenceLabels().size(), 0,
                "Size of obtained artifact references should be '0' when they are collapsed!");

        softAssert.assertAll();
    }

    @Test(groups = {TestGroups.MINIMAL_ACCEPTANCE, "test_details"})
    @TestCaseKey({"ZTP-4239", "ZTP-4240"})
    public void verifyTcmLabelsDisplayedOnThePageWithoutAnyOthersLabelsAndReferencesAndPreviewOpeningForCaseWithSharedStep() {
        String testRunName = "Tcm labels launch for project with key ".concat(project.getKey()).concat(" № ")
                                                                      .concat(RandomStringUtils.randomNumeric(3));
        String methodNamePassed = "Tcm labels test №".concat(RandomStringUtils.randomNumeric(7));

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);

        launchIds.add(testRunId);
        long passedTest = testService.startTestWithMethodName(testRunId, methodNamePassed);
        List<TestCase> addedCases = addTestCases();

        ArrayList<Label> labels = new ArrayList<>();
        addedCases.forEach(testCase -> labels.add(new Label(TcmType.ZEBRUNNER.getLabelKey(), testCase.getKey())));

        apiHelperService.addLabelsToTest(testRunId, passedTest, labels);
        testService.finishTestAsResult(testRunId, passedTest, "PASSED");
        testRunService.finishTestRun(Math.toIntExact(testRunId));

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testRunId);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(testRunResultPage.isPageOpened(),
                "Test run results page was not opened!");

        testRunResultPage.getCertainTest(methodNamePassed).clickOnCard();
        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();

        TestHeader testHeader = testDetailsPage.getTestHeader();

        softAssert.assertEquals(testHeader.getTcmLabels().size(), labels.size(),
                "Size of obtained tcm labels should be the same as linked!");

        labels.forEach(label -> softAssert.assertTrue(testHeader
                        .findTcmLabel(label.getValue())
                        .isPresent(),
                String.format("Linked label '%s' should be visible on UI ", label.getValue())));

        TestCase testCase = addedCases.get(0);

        TcmLabelPreview tcmLabelPreview = testHeader
                .findTcmLabel(testCase.getKey())
                .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", testCase.getKey())))
                .clickOnTcmLabel();

        softAssert.assertEquals(tcmLabelPreview.getCaseTitle().getText(), testCase.getTitle(),
                "Title on preview should be the same as in case!");

        TestCaseStep sharedStep = testCase.getSteps().stream()
                                          .filter(step ->
                                                  step.getType().equals(TestCaseStepType.SHARED))
                                          .findFirst()
                                          .orElseThrow(() -> new RuntimeException("No shared steps were found!"))
                                          .getSharedSteps().getSteps().get(0);

        tcmLabelPreview.findWithAction(sharedStep.getAction())
                       .ifPresentOrElse(obtainedSharedStep ->
                                       softAssert.assertEquals(obtainedSharedStep.getExpectedResultText(),
                                               sharedStep.getExpectedResult(),
                                               "Expected results should be the same!"),
                               () -> softAssert.fail("Unable to find step with action " + sharedStep.getAction())
                       );

        TestCaseStep regularStep = testCase.getSteps().stream()
                                           .filter(step ->
                                                   step.getType().equals(TestCaseStepType.REGULAR))
                                           .findFirst()
                                           .orElseThrow(() -> new RuntimeException("No regular steps were found!"))
                                           .getRegularStep();

        tcmLabelPreview.findWithAction(regularStep.getAction())
                       .ifPresentOrElse(obtainedSharedStep ->
                                       softAssert.assertEquals(obtainedSharedStep.getExpectedResultText(),
                                               regularStep.getExpectedResult(),
                                               "Expected results should be the same!"),
                               () -> softAssert.fail("Unable to find step with action " + regularStep.getAction())
                       );

        softAssert.assertAll();
    }

    @Test(groups = {TestGroups.MINIMAL_ACCEPTANCE, "test_details"})
    @TestCaseKey({"ZTP-3834", "ZTP-3835", "ZTP-1351", "ZTP-3823"})
    public void verifyLinkIssueToFailedAndSkippedTest() {
        String issueId = "ZEB-133";
        WebDriver webDriver = super.getDriver();

        IntegrationManager.addIntegration(project.getId(), Tool.JIRA);

        String launchName = "Link issue launch " + project.getKey() + " № " + RandomStringUtils.randomNumeric(7);
        String methodNameFailed = "Failed test № " + RandomStringUtils.randomNumeric(7);
        String methodNameSkipped = "Skipped test № " + RandomStringUtils.randomNumeric(7);

        long launchId = testRunService.startTestRunWithName(project.getKey(), launchName);
        launchIds.add(launchId);

        long failedTestId = testService.startTestWithMethodName(launchId, methodNameFailed);
        long skippedTestId = testService.startTestWithMethodName(launchId, methodNameSkipped);

        testService.finishTestAsResult(launchId, failedTestId, "FAILED");
        testService.finishTestAsResult(launchId, skippedTestId, "SKIPPED");
        testRunService.finishTestRun(launchId);

        TestRunResultPageR launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launchId);
        Assert.assertTrue(launchPage.isPageOpened(), "Test run results page was not opened!");

        launchPage.getCertainTest(methodNameFailed).clickOnCard();
        TestDetailsPageR testPage = TestDetailsPageR.getPageInstance(webDriver);
        testPage.assertPageOpened();

        testPage.getTestHeader()
                .clickLinkIssueButton()
                .openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                .linkIssue(issueId);

        Assert.assertTrue(
                launchPage.waitIsPopUpMessageAppear("Issue was linked successfully"),
                "Popup message of successfully issue linking is not appear"
        );
        Assert.assertEquals(
                testPage.getTestHeader().getLinkedIssueValue(),
                issueId,
                "Jira id is not as the linked to the test"
        );

        // ZTP-3835
        launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launchId);

        launchPage.getCertainTest(methodNameSkipped).clickOnCard();
        testPage = TestDetailsPageR.getPageInstance(webDriver);
        testPage.assertPageOpened();

        testPage.getTestHeader()
                .clickLinkIssueButton()
                .openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                .linkIssue(issueId);

        Assert.assertTrue(
                launchPage.waitIsPopUpMessageAppear("Issue was linked successfully"),
                "Popup message of successfully issue linking is not appear"
        );
        Assert.assertEquals(
                testPage.getTestHeader().getLinkedIssueValue(),
                issueId,
                "Jira id is not as the linked to the test"
        );

        launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launchId);
        launchPage.getCertainTest(methodNameFailed).clickOnCard();
        testPage = TestDetailsPageR.getPageInstance(webDriver);
        testPage.assertPageOpened();

        // Jira issue preview verification.
        // Jira issue is hardcoded value and there
        // are only a few fields that show up in the bug, when in fact there could be many more.
        JiraIssuePreview jiraIssuePreview = testPage
                .getTestHeader()
                .getLinkedIssue()
                .clickLinkedIssue();
        jiraIssuePreview.getLinkedIssueTitle().waitUntil(Condition.VISIBLE);

        Assert.assertEquals(
                jiraIssuePreview.getTitle(),
                "Test issue for AQA tests",
                "The issue title on modal is not as in the linked issue!"
        );
        Assert.assertEquals(
                jiraIssuePreview.findAttributeWithName("Type")
                                .orElseThrow(() -> new NoSuchElementException("Unable to find attribute with text 'Type'"))
                                .getAttributeValueText(),
                "Bug",
                "Type value is not as expected!"
        );
        Assert.assertEquals(
                jiraIssuePreview.findAttributeWithName("Status")
                                .orElseThrow(() -> new NoSuchElementException("Unable to find attribute with text 'Status'"))
                                .getAttributeValueText(),
                "To Do",
                "Type value is not as expected!"
        );

        jiraIssuePreview.close();
        // ZTP-3823
        CardUpdateModalR cardUpdateModal = testPage.getPageHeader().clickMarkAsPassed();
        cardUpdateModal.getSubmitButton().click();
        Assert.assertEquals(
                launchPage.getPopUp(),
                "Test was marked as passed"
        );
        Assert.assertFalse(
                testPage.getTestHeader().getLinkedIssue().isUIObjectPresent(5),
                "Linked issue should not be visible when test marking as passed"
        );
    }

    @Test(groups = {"test_details"})
    @TestCaseKey("ZTP-1348")
    public void verifyShowMoreShowLessLogs() {
        String testRunName = "Logs launch ".concat(project.getKey()).concat(" № ")
                                           .concat(RandomStringUtils.randomNumeric(7));
        String methodNameFailed = "Different logs test № ".concat(RandomStringUtils.randomNumeric(7));

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);
        launchIds.add(testRunId);
        long idForTestWithLogs = testService.startTestWithMethodName(testRunId, methodNameFailed);
        testService.finishTestAsResult(testRunId, idForTestWithLogs, "FAILED");
        testRunService.finishTestRun(testRunId);

        List<LogItem> logItems = new ArrayList<>();

        LOG_LEVELS.forEach(logLevel -> {
            logItems.add(LogItem.generateRandomLogWithLevel(idForTestWithLogs, logLevel));
        });

        LogItem withLongMessageLog = LogItem.builder()
                                            .level("INFO")
                                            .testId(idForTestWithLogs)
                                            .message("Log message ".concat(RandomStringUtils.randomNumeric(1000)))
                                            .timestamp(System.currentTimeMillis())
                                            .build();

        logItems.add(withLongMessageLog);
        apiHelperService.addLogsToTest(testRunId, logItems);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testRunId);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(testRunResultPage.isPageOpened(), "Test run results page was not opened!");

        testRunResultPage.getCertainTest(methodNameFailed).clickOnCard();
        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();

        LogRow logRowWithLongMessage = testDetailsPage
                .getLogsTable()
                .findLogWithMessageContaining(withLongMessageLog.getMessage().substring(1, 70));

        String collapsedLogMessage = logRowWithLongMessage.getMessage(false);// with showMore text

        int maxLength = 255;
        softAssert.assertTrue(collapsedLogMessage.length() <= maxLength, "The row of test log exceeds 255 symbols!");

        logRowWithLongMessage.expandCollapseLogMessage();// expand message

        softAssert.assertEquals(logRowWithLongMessage.getBackGroundColor(), LogRow.YELLOW_BACKGROUND_COLOR_OF_ACTIVE_LOG,
                "Background color is not as expected when log message is expanded!");
        softAssert.assertEquals(logRowWithLongMessage.getMessage(false).replace("Show less", "").replace("\n", ""),
                withLongMessageLog.getMessage(), "Expanded log message should be the same as inputted!");

        logRowWithLongMessage.clickShowLess();// collapse message

        pause(3);// should wait a bit until message collapsed
        softAssert.assertEquals(logRowWithLongMessage.getMessage(false), collapsedLogMessage, "Collapsed message is not as expected!");
        softAssert.assertEquals(logRowWithLongMessage.getBackGroundColor(), LogRow.YELLOW_BACKGROUND_COLOR_OF_ACTIVE_LOG,
                "Background color is not as expected when log message is collapsed!");

        logRowWithLongMessage.expandCollapseLogMessage();// expand message 3 time to check background color https://solvd.atlassian.net/browse/ZEB-6801

        softAssert.assertEquals(logRowWithLongMessage.getBackGroundColor(), LogRow.YELLOW_BACKGROUND_COLOR_OF_ACTIVE_LOG,
                "Background color is not as expected when log message is expanded!");

        softAssert.assertAll();
    }

    @Test(groups = "test_details")
    @TestCaseKey({"ZTP-1349", "ZTP-1350", "ZTP-3674"})
    public void verifyPermalinkAndLogLineCoping() {
        String testRunName = "Logs launch ".concat(project.getKey()).concat(" № ")
                                           .concat(RandomStringUtils.randomNumeric(7));
        String methodNameFailed = "Different logs test № ".concat(RandomStringUtils.randomNumeric(7));

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);
        launchIds.add(testRunId);
        long idForTestWithLogs = testService.startTestWithMethodName(testRunId, methodNameFailed);
        testService.finishTestAsResult(testRunId, idForTestWithLogs, "FAILED");
        testRunService.finishTestRun(testRunId);

        List<LogItem> logItems = new ArrayList<>();

        LOG_LEVELS.forEach(logLevel -> {
            logItems.add(LogItem.generateRandomLogWithLevel(idForTestWithLogs, logLevel));
        });

        LogItem withLongMessageLog = LogItem.builder()
                                            .level("INFO")
                                            .testId(idForTestWithLogs)
                                            .message("Log message ".concat(RandomStringUtils.randomNumeric(1000)))
                                            .timestamp(System.currentTimeMillis())
                                            .build();

        logItems.add(withLongMessageLog);
        apiHelperService.addLogsToTest(testRunId, logItems);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testRunId);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(testRunResultPage.isPageOpened(), "Test run results page was not opened!");

        testRunResultPage.getCertainTest(methodNameFailed).clickOnCard();
        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();

        LogRow logRowWithLongMessage = testDetailsPage.getLogsTable()
                                                      .findLogWithMessageContaining(withLongMessageLog.getMessage()
                                                                                                      .substring(1, 70));

        logRowWithLongMessage.openSettingsAndSelect(Dropdown.DropdownItemsEnum.COPY_LINE);
        softAssert.assertEquals(testDetailsPage.getPopUp(), MessageEnum.LOG_LINK_WAS_COPIED.getDescription(),
                "Popup is not as expected!");

        String copiedLog = logRowWithLongMessage.getClipboardText();
        String timeZone = (String) ((JavascriptExecutor) getDriver()).executeScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;");

        softAssert.assertEquals(copiedLog.replaceFirst("(AM|PM)\\s+", ""),
                withLongMessageLog.generateLogLineAsOnUi(timeZone),
                "Copied text is not as expected!");
        testDetailsPage.waitPopupDisappears();

        LogRow logRowToCheckPermalink = testDetailsPage.getLogsTable().getLogs()
                                                       .orElseThrow(() -> new NoSuchElementException("No logs at all!"))
                                                       .get(3);

        String logRowToCheckPermalinkMessage = logRowToCheckPermalink.getMessage(false);

        logRowToCheckPermalink.openSettingsAndSelect(Dropdown.DropdownItemsEnum.COPY_PERMALINK);//ZTP-1350

        softAssert.assertEquals(testDetailsPage.getPopUp(), MessageEnum.PERMALINK_WAS_COPIED.getDescription(),
                "Popup is not as expected!");

        String copiedPermalink = logRowToCheckPermalink.getClipboardText();

        getDriver().get(copiedPermalink);
        testDetailsPage.assertPageOpened();

        LogRow logRowToCheckPermalinkOnNewPage = testDetailsPage
                .getLogsTable()
                .findLogWithMessageContaining(logRowToCheckPermalinkMessage);

        softAssert.assertEquals(logRowToCheckPermalinkOnNewPage.getBackGroundColor(),
                LogRow.YELLOW_BACKGROUND_COLOR_OF_ACTIVE_LOG,
                "Background color of log message is not as expected when opening via permalink!");//ZTP-3674
        // Verify that log is highlighted when user follows permalink

        softAssert.assertAll();
    }

    @Test(groups = "test_details")
    @TestCaseKey({"ZTP-1342"})
    public void verifyLogFiltering() {
        String testRunName = "Logs launch ".concat(project.getKey()).concat(" № ")
                                           .concat(RandomStringUtils.randomNumeric(7));
        String methodNameFailed = "Different logs test № ".concat(RandomStringUtils.randomNumeric(7));

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);
        launchIds.add(testRunId);
        long idForTestWithLogs = testService.startTestWithMethodName(testRunId, methodNameFailed);
        testService.finishTestAsResult(testRunId, idForTestWithLogs, "FAILED");
        testRunService.finishTestRun(testRunId);

        List<LogItem> logItems = new ArrayList<>();
        List<String> logLevelsExceptFirst = new ArrayList<>(LOG_LEVELS);
        logLevelsExceptFirst.remove(0);// exclude 1st level to check 'No logs matching selected criteria' message

        logLevelsExceptFirst.forEach(logLevel -> {
            logItems.add(LogItem.generateRandomLogWithLevel(idForTestWithLogs, logLevel));
            logItems.add(LogItem.generateRandomLogWithLevel(idForTestWithLogs, logLevel));
        });

        Collections.shuffle(logItems);
        apiHelperService.addLogsToTest(testRunId, logItems);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testRunId);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(testRunResultPage.isPageOpened(), "Test run results page was not opened!");
        testRunResultPage.getCertainTest(methodNameFailed).clickOnCard();

        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();

        testDetailsPage.getLogsTable()
                       .filterLogsBy(List.of(LogLevel.values()).get(0));// find excluded log level and filter by it

        softAssert.assertTrue(testDetailsPage.getLogsTable().getLogs()
                                             .isEmpty(), "List should be empty when no logs with selected level!");
        softAssert.assertEquals(testDetailsPage.getLogsTable().getNologsText()
                                               .getText(), "No logs matching selected criteria",
                "Text is not as expected!");

        String existingFirstLogLevel = LOG_LEVELS.get(0);

        LogItem logWithFatalLevel = LogItem.generateRandomLogWithLevel(idForTestWithLogs, existingFirstLogLevel);
        logItems.add(logWithFatalLevel);

        apiHelperService.addLogsToTest(testRunId, Collections.singletonList(logWithFatalLevel));

        getDriver().navigate().refresh();
        testDetailsPage.isPageOpened();

        List<LogLevel> logLevels = new ArrayList<>(List.of(LogLevel.values()));

        logLevels.forEach(logLevelForFiltering -> {
            testDetailsPage.getLogsTable().filterLogsBy(logLevelForFiltering);
            testDetailsPage.getLogsTable().getLogs()
                           .ifPresentOrElse(logRows -> logRows
                                           .forEach(
                                                   logRow -> softAssert.assertTrue(isValidLogLevel(logRow.getLevel(), logLevelForFiltering.name()),
                                                           "Not expected log level after sorting by " + logLevelForFiltering)
                                           ),
                                   () -> softAssert.fail("No logs have found when sorting by level " + logLevelForFiltering)
                           );

        });

        softAssert.assertAll();
    }

    @Test(groups = "test_details", priority = 5)
    @TestCaseKey("ZTP-3677")
    public void verifyUserCanEditCustomCapabilitiesAfterOpeningLauncherTest() {
        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        LaunchCard launchCard = automationLaunchesPage.findTestRunCardByName(LAUNCHER_NAME).waitFinish(
                Duration.ofSeconds(500),
                Duration.ofSeconds(15));

        AddOrEditLauncherPage launcherPage = launchCard.clickMenu().goToLauncher();

        CustomCapabilitiesModal capModal = launcherPage.getSelectedLauncherForm().getTestingPlatformSection()
                                                       .openCustomCapabilitiesModal();
        capModal.getCapabilityWithName("zebrunner:idleTimeout").get().typeCapabilityValue("240");
        capModal.submitModal();

        softAssert.assertEquals(launcherPage.getCapabilityVariablesValues(), "zebrunner:idleTimeout".concat("=")
                                                                                                    .concat("240"),
                "Capability variable is not as expected after changing it!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1357")
    @Maintainer("Gmamaladze")
    public void verifyTestLabelsCanBeViewedInTestDetailsPage() {
        SoftAssert softAssert = new SoftAssert();

        final String UPDATED_TESTRAIL_CASE_ID = "C1";
        final String UPDATED_TESTRAIL_LABEL_ON_CARD = "1";

        List<Label> testLabelsList = List.of(
                new Label(TcmType.TESTRAIL.getLabelKey(), UPDATED_TESTRAIL_CASE_ID),
                new Label("Platform", "Zebrunner")
        );

        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        TestExecution test = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());


        apiHelperService.addLabelsToTest(launch.getId(), test.getId(), testLabelsList);
        testService.finishTestAsResult(launch.getId(), test.getId(), "PASSED");
        testRunService.finishTestRun(Math.toIntExact(launch.getId()));

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        testRunResultPage.getCertainTest(test.getName()).clickOnCard();

        TestDetailsPageR testDetailsPage = TestDetailsPageR.getPageInstance(getDriver());
        testDetailsPage.assertPageOpened();

        pause(4);

        TestHeader testHeader = testDetailsPage.getTestHeader();

        Label customLabel = testLabelsList.get(1);
        softAssert.assertTrue(testHeader.findLabelsByKey(customLabel.getKey()).isPresent(),
                String.format("Label with key '%s' was not found!", customLabel.getKey()));

        softAssert.assertTrue(testHeader.findTcmLabel(UPDATED_TESTRAIL_LABEL_ON_CARD).isPresent(),
                String.format("TCM label '%s' was not found!", UPDATED_TESTRAIL_LABEL_ON_CARD));

        softAssert.assertAll();
    }
}

