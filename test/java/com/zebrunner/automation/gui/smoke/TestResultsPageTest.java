package com.zebrunner.automation.gui.smoke;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.launcher.DeleteLauncherAlertModal;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.CollapsedLaunchCardConfiguration;
import com.zebrunner.automation.gui.reporting.launch.CollapsedTestRunViewHeaderR;
import com.zebrunner.automation.gui.reporting.launch.ExpandedTestRunViewHeaderR;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.gui.reporting.launch.RelaunchModal;
import com.zebrunner.automation.gui.reporting.launch.ReviewModalR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.TcmType;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Slf4j
public class TestResultsPageTest extends LogInBase {
    private final List<Long> testRunIdList = new ArrayList<>();
    private final String NO_TEST_PRESENT = "This launch has no test executions reported";
    private final String NO_TEST_PRESENT_YET = "This launch has no test executions reported yet";
    private final String NO_DATA_DESCRIPTION = "No data here, but plenty of virtual tumbleweeds rolling by. Yeehaw!";
    private final String TEST_EXECUTION_DESCRIPTION = "Test executions will appear here and the view will be refreshed automatically.";
    private final String milestoneName = "new_milestone".concat(RandomStringUtils.randomAlphabetic(3));
    private final String BUILD = "1.1.1-SNAPSHOT";
    private final String ENV = "ORG";
    private final String launcherName = "Test launcher";
    private Long milestoneId;
    private PlatformTypeR browser;
    private Label label;
    private ArtifactReference artifactReference;
    private Project project;
    private Long repoId;
    private Launcher createdWebLauncher;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
        repoId = LogInBase.repositoryId;
        milestoneId = apiHelperService.createMilestone(project.getId(), milestoneName);

        createdWebLauncher = launcherService.addDefaultUiTestsLauncher(
                project.getId(),
                repoId,
                launcherName,
                "main",
                "web");
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    @AfterClass
    public void deleteMilestone() {
        apiHelperService.deleteMilestone(project.getId(), milestoneId);
    }

    // --------------------- Additional Methods ----------------------- //


    private void finishLaunchWithTestAndConfiguration(Launch launch) {
        label = new Label("Platform", "Zebrunner");
        artifactReference = new ArtifactReference("Zebrunner", "https://zebrunner.com/documentation/");

        apiHelperService.addLabelToTestRun(launch.getId(), label.getKey(), label.getValue());
        apiHelperService.addArtRefToTestRun(launch.getId(), artifactReference.getName(), artifactReference.getValue());

        browser = PlatformTypeR.CHROME;

        TestExecution testExecution = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        apiHelperService.startSession(launch.getId(), Collections.singletonList(testExecution.getId()), "", browser.value());
        testService.finishTestAsResult(launch.getId(), testExecution.getId(), "PASSED");

        testRunService.finishTestRun(launch.getId());
    }

    // ___________________Basic tests__________________

    @Test
    @TestCaseKey({"ZTP-4668", "ZTP-4669"})
    public void verifyPlaceholderForLaunchWithNoTests() {
        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        testRunIdList.add(launch.getId());

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        softAssert.assertEquals(testRunResultPage.getEmptyPlaceholder().getEmptyPlaceHolderTitle(), NO_TEST_PRESENT_YET,
                "Message is not as expected,test shouldn't be present yet!");
        softAssert.assertEquals(testRunResultPage.getEmptyPlaceholder()
                                                 .getEmptyPlaceHolderDescription(), TEST_EXECUTION_DESCRIPTION,
                "Description is not as expected,test execution description should present!");

        testRunService.finishLaunch(launch.getId());

        testRunResultPage.openPageDirectly(project.getKey(), launch.getId());

        softAssert.assertEquals(testRunResultPage.getEmptyPlaceholder().getEmptyPlaceHolderTitle(), NO_TEST_PRESENT,
                "Message is not as expected, test shouldn't present!");
        softAssert.assertEquals(testRunResultPage.getEmptyPlaceholder()
                                                 .getEmptyPlaceHolderDescription(), NO_DATA_DESCRIPTION,
                "Description is not as expected, No date description should present!");

        softAssert.assertAll();
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-1316", "ZTP-1317", "ZTP-1330", "ZTP-1331"})
    public void verifyTestRunResultHeaderAfterCollapseAndExpand() {
        WebDriver webDriver = super.getDriver();

        Launch launch = testRunService.startTestRunWithName(
                project.getKey(),
                Launch.getLaunchWithConfigAndMilestone(ENV, BUILD, milestoneName)
        );
        finishLaunchWithTestAndConfiguration(launch);
        testRunIdList.add(launch.getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launch.getId());
        ExpandedTestRunViewHeaderR expandedTestRunViewHeader = testRunResultPage.expandTestRunViewHeader();
        Assert.assertEquals(expandedTestRunViewHeader.getEnvironment(), ENV, "Env is not as expected !");

        expandedTestRunViewHeader.clickLabels();
        super.pause(2);

        Assert.assertTrue(
                expandedTestRunViewHeader.containsLabel(label),
                "Label (key=" + label.getKey() + ", value=" + label.getValue() + ") could not be found"
        );

        ComponentUtil.pressEscape(webDriver);

        expandedTestRunViewHeader.clickArtifactIcon();
        super.pause(2);

        Assert.assertTrue(
                expandedTestRunViewHeader.containsArtifactReference(artifactReference),
                String.format("Artifact %s was not found!", artifactReference.getName())
        );

        ComponentUtil.pressEscape(webDriver);
        super.pause(2);

        Assert.assertEquals(expandedTestRunViewHeader.getMilestoneName(), milestoneName, "Milestone name is not as expected !");
        Assert.assertTrue(expandedTestRunViewHeader.isDurationPresent(), "Duration should be present !");
        Assert.assertTrue(expandedTestRunViewHeader.isLaunchTimePresent(), "Launch time should be present !");

        Assert.assertTrue(
                expandedTestRunViewHeader.getLaunchCardConfiguration().isBrowserPresent(browser),
                String.format("Browser %s not found!", browser.value())
        );
        Assert.assertTrue(
                expandedTestRunViewHeader.getLaunchCardConfiguration()
                                         .isBuildPresent(String.format("Build\n: %s", BUILD)),
                String.format("Build %s not found!", BUILD)
        );

        CollapsedTestRunViewHeaderR collapsedTestRunViewHeader = testRunResultPage.collapseTestRunViewHeader();

        collapsedTestRunViewHeader.clickLabelIcon();
        super.pause(2);

        Assert.assertTrue(
                collapsedTestRunViewHeader.containsLabel(label),
                String.format("Label %s: %s was not found after collapse!", label.getKey(), label.getValue())
        );

        ComponentUtil.pressEscape(webDriver);

        collapsedTestRunViewHeader.clickArtifactIcon();
        super.pause(2);

        Assert.assertTrue(
                collapsedTestRunViewHeader.containsArtifactReference(artifactReference),
                String.format("Artifact %s was not found after collapse!", artifactReference.getName())
        );

        ComponentUtil.pressEscape(webDriver);
        Assert.assertEquals(collapsedTestRunViewHeader.getMilestoneName(), milestoneName, "Milestone is not as expected after collapse !");
        Assert.assertTrue(collapsedTestRunViewHeader.isDurationPresent(), "Duration should be present after collapse !");
        Assert.assertTrue(collapsedTestRunViewHeader.isLaunchTimePresent(), "Launch time should be present after collapse !");

        CollapsedLaunchCardConfiguration collapsedLaunchCardConfiguration = collapsedTestRunViewHeader.openConfiguration();

        Assert.assertTrue(
                collapsedLaunchCardConfiguration.isBrowserPresent(browser),
                String.format("Browser %s not found after collapse !", browser.value())
        );
        Assert.assertTrue(
                collapsedLaunchCardConfiguration.isBuildPresent(
                        String.format("Build\n: %s", BUILD)), String.format("Build %s not found after collapse !", BUILD)
        );
    }

    @Test
    @TestCaseKey("ZTP-1320")
    public void verifyUserCanAbortTestRunInQueuedStatus() {
        launcherService.launchLauncher(project.getId(), repoId, createdWebLauncher);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        LaunchCard testRunCard = automationLaunchesPage.findTestRunCardByName(launcherName);
        TestRunResultPageR testRunResultPage = testRunCard.toTests();
        Long testRunId = testRunResultPage.getTestRunIdFromUrl();
        testRunIdList.add(testRunId);
        testRunResultPage.getActionBar().clickAbortButton();

        Assert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear("Launch #" + testRunId + " is aborted"),
                "Popup is not as expected !"
        );

        super.pause(5);
        Assert.assertFalse(
                testRunResultPage.getActionBar().isAbortButtonPresent(),
                "Abort button shouldn't be present !"
        );
        Assert.assertEquals(
                testRunResultPage.getLeftBoardColorOfLaunchHeader(), "#aeb8be",
                "Color of header is not as expected !"
        );
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey("ZTP-1321")
    public void verifyUserCanReviewTestRun() {
        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage testClassLaunchDataStorage =
                PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ReviewModalR reviewModal = testRunResultPage.getActionBar().openReviewModal();

        softAssert.assertTrue(reviewModal.isReviewModalOpened(), "Review Modal should be opened !");

        String review = RandomStringUtils.randomAlphabetic(5);
        reviewModal.typeComment(review);
        reviewModal.clickSubmitButton();

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.LAUNCH_HAS_BEEN_MARKED_AS_REVIEWED.getDescription()),
                "Popup is not as expected !");
        softAssert.assertTrue(testRunResultPage.getActionBar().isTestRunReviewed(), "Test run should be reviewed !");

        reviewModal = testRunResultPage.getActionBar().openReviewModal();

        softAssert.assertEquals(reviewModal.getReviewText(), review, "Review is not as expected !");

        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-1322")
    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void verifyUserCanRelaunchLauncher() {
        WebDriver webDriver = super.getDriver();

        String launcherName = "Launch-launcher";
        Launcher launcher = launcherService.addDefaultUiTestsLauncher(
                project.getId(), repoId, launcherName, "main", "helloWorld"
        );
        launcherService.launchLauncher(project.getId(), repoId, launcher);

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());
        LaunchCard launchCard = launchesPage.findTestRunCardByName(launcherName)
                                            .waitFinish(Duration.ofSeconds(800), Duration.ofSeconds(10));

        TestRunResultPageR launchPage = launchCard.toTests();
        Long launchId = launchPage.getTestRunIdFromUrl();
        testRunIdList.add(launchId);

        launchPage.getActionBar().openAndSelectLaunchAction(Menu.MenuItemEnum.RELAUNCH);
        RelaunchModal relaunchModal = new RelaunchModal(webDriver);
        Assert.assertTrue(relaunchModal.isModalOpened(), "Relaunch modal should be opened !");

        LauncherPage launcherPage = relaunchModal.clickRelaunchButton();
        Assert.assertTrue(launcherPage.isPageOpened(), "Launcher page was not opened !");

        launchesPage = launcherPage.clickLaunch();
        Assert.assertTrue(
                launchesPage.waitIsPopUpMessageAppear("Launch is queued"),
                "Popup is not as expected !"
        );

        launchCard = launchesPage.findTestRunCardByName(launcherName);
        Assert.assertEquals(
                launchCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.QUEUED,
                "Colour is not as expected !"
        );
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey("ZTP-1326")
    public void verifyUserCanAssignMilestoneToTestRun() {
        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage testClassLaunchDataStorage =
                PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        testRunResultPage.getResultActionBar().assignToMilestone().chooseMilestoneAndAssign(milestoneName);

        softAssert.assertTrue(testRunResultPage
                        .waitIsPopUpMessageAppear(MessageEnum.LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE.getDescription()),
                "Popup message is not as expected!");
        softAssert.assertEquals(testRunResultPage.expandTestRunViewHeader().getMilestoneName(), milestoneName,
                "Milestone is not assigned");

        softAssert.assertAll();
    }

    @Test(enabled = false)
    @TestCaseKey("ZTP-1335")
    public void verifyTestCaseStateFilter() {
        SoftAssert softAssert = new SoftAssert();

        String passedTestName = RandomStringUtils.randomAlphabetic(3);
        String failedTestName = RandomStringUtils.randomAlphabetic(3);

        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        testRunIdList.add(launch.getId());

        long passedTest = testService.startTestWithMethodName(launch.getId(), passedTestName);
        long failedTest = testService.startTestWithMethodName(launch.getId(), failedTestName);

        ArrayList<Label> labels = new ArrayList<>();
        labels.add(new Label(TcmType.TESTRAIL.getLabelKey(), "C1"));
        labels.add(new Label(TcmType.TESTRAIL.getLabelKey(), "C2"));

        apiHelperService.addLabelsToTest(launch.getId(), passedTest, Collections.singletonList(labels.get(0)));
        testService.finishTestAsResult(launch.getId(), passedTest, "PASSED");

        apiHelperService.addLabelsToTest(launch.getId(), failedTest, Collections.singletonList(labels.get(1)));
        testService.finishTestAsResult(launch.getId(), failedTest, "FAILED");

        testRunService.finishTestRun(Math.toIntExact(launch.getId()));

        pause(7);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();

        actionsBlock.openAndSelectTestCaseState(Dropdown.DropdownItemsEnum.TEST_CASE_STATE_UPDATED);

        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                "Tests with updated test case state should be present after applying filter 'Updated'");
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                "Tests with deprecated test case state shouldn't be present after applying filter 'Updated'");

        actionsBlock.clickResetButton();

        actionsBlock.openAndSelectTestCaseState(Dropdown.DropdownItemsEnum.TEST_CASE_STATE_DEPRECATED);

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                "Tests with deprecated test case state should be present after applying filter 'Deprecated'");
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                "Tests with updated test case state shouldn't be present after applying filter 'Deprecated'");

        softAssert.assertAll();
    }


    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey("ZTP-1337")
    public void verifyLabelFilter() {
        SoftAssert softAssert = new SoftAssert();

        String passedTestName = RandomStringUtils.randomAlphabetic(3);
        String failedTestName = RandomStringUtils.randomAlphabetic(3);

        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        testRunIdList.add(launch.getId());

        long passedTest = testService.startTestWithMethodName(launch.getId(), passedTestName);
        long failedTest = testService.startTestWithMethodName(launch.getId(), failedTestName);

        String zebLabelKey = "Zebrunner";
        String zebLabelValue = "ZEB";
        String tsLabelKey = "TestRail";
        String tsLabelValue = "TS";
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(new Label(zebLabelKey, zebLabelValue));
        labels.add(new Label(tsLabelKey, tsLabelValue));

        String formattedZebLabel = zebLabelKey + ": " + zebLabelValue;
        String formattedTsLabel = tsLabelKey + ": " + tsLabelValue;

        apiHelperService.addLabelsToTest(launch.getId(), passedTest, Collections.singletonList(labels.get(0)));
        testService.finishTestAsResult(launch.getId(), passedTest, "PASSED");

        apiHelperService.addLabelsToTest(launch.getId(), failedTest, Collections.singletonList(labels.get(1)));
        testService.finishTestAsResult(launch.getId(), failedTest, "FAILED");

        testRunService.finishTestRun(Math.toIntExact(launch.getId()));

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();

        actionsBlock.openAndSelectLabel(formattedZebLabel);

        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                "Test with zebrunner label should be present");
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                "Test with test rail label shouldn't be present");

        actionsBlock.clickResetButton();

        actionsBlock.openAndSelectLabel(formattedTsLabel);

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                "Test with test rail label should be present");
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                "Test with zebrunner label shouldn't be present");

        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey("ZTP-5959")
    public void verifyUserIsAbleToCancelDeletingLaunch() {
        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage testClassLaunchDataStorage =
                PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        for (int i = 0; i < 3; i++) {
            DeleteLauncherAlertModal deleteLauncherAlertModal = testRunResultPage.getActionBar().clickDelete();

            softAssert.assertEquals(deleteLauncherAlertModal.getModalTitleText(), DeleteLauncherAlertModal.MODAL_NAME,
                    "Delete launcher alert modal is not opened !");

            if (i == 0) {
                deleteLauncherAlertModal.clickCancel();
            } else if (i == 1) {
                deleteLauncherAlertModal.closeAlert();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            softAssert.assertFalse(deleteLauncherAlertModal.isModalOpened(), "Modal should be closed !");
            softAssert.assertTrue(testRunResultPage.isPageOpened(), "Launcher page should be opened !");
            softAssert.assertEquals(testRunResultPage.getLaunchName(), testClassLaunchDataStorage.getLaunch().getName(),
                    "Launcher name is not as excepted !");
        }

        softAssert.assertAll();
    }
}