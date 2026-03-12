package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.gui.common.ZbrTimeInput;
import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.tcm.ExecutionsControlPanel;
import com.zebrunner.automation.gui.reporting.launch.CreateJiraIssueModal;
import com.zebrunner.automation.gui.reporting.launch.LinkIssueModal;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.tcm.testcase.AbstractTestCasePreview;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseModalView;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionItem;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionsTab;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.util.ComponentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

@Slf4j
@Maintainer("akhivyk")
public class TestRunsElapsedTimeTest extends TcmLogInBase {

    private Project project;
    private TestSuite testSuite;
    private List<TestCase> createdTestCases;
    private TestRun testRun;
    private static final String ELAPSED_TIME_DEFAULT_VALUE = "00:00:00";
    private final static String IMAGES_ZEB_PNG = "src/test/resources/images/zeb.png";

    @BeforeClass
    public void preparation() {
        project = super.getCreatedProject();

        testSuite = tcmService.createTestSuite(project.getId(), new TestSuite("Suite for automation " + RandomStringUtils.randomNumeric(5)));
        createdTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);

        testRun = tcmService.createTestRun(project.getId(), createdTestCases, TestRun.createWithRandomName());
    }

    @Test
    @TestCaseKey({"ZTP-4214", "ZTP-4215"})
    public void verifyElapsedTimeIsOptional() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                testSuite.getTitle(), createdTestCases.get(0).getTitle()
        );
        softAssert.assertEquals(addExecutionResultModal.getCurrentElapsedTimeValue(), ELAPSED_TIME_DEFAULT_VALUE,
                "Elapsed time value by default isn't equals to expected"); // ZTP-4215 Elapsed time format 00:00:00

        addExecutionResultModal.submitModal();
        AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                .getTestCase(createdTestCases.get(0).getTitle())
                .clickTestCase();
        TestCaseModalView modalView = testCaseView.toModalView();

        ExecutionsTab executionsTab = modalView.openExecutionsTab();
        softAssert.assertFalse(executionsTab.getLastExecution().isElapsedTimePresent(),
                "Elapsed time shouldn't present with default value on execution item"); // ZTP-4214 Elapsed time is optional

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4216", "ZTP-4217", "ZTP-4220"})
    public void verifyMaximumValueOfElapsedTimeAndTimeCanBeChangedByArrows() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        String expectedMaximumValueElapsedTime = "99:59:59";
        String expectedMaximumValueElapsedTimeFromExecutionTab = "99h 59m 59s";
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                testSuite.getTitle(), createdTestCases.get(0).getTitle()
        );
        ZbrTimeInput elapsedTime = addExecutionResultModal.getElapsedTime();

        elapsedTime.setTimeValue(99, 99, 99);
        softAssert.assertEquals(addExecutionResultModal.getCurrentElapsedTimeValue(), expectedMaximumValueElapsedTime,
                "Maximum value of elapsed time isn't equals to expected on execution modal!");

        addExecutionResultModal.submitModal();
        AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                .getTestCase(createdTestCases.get(0).getTitle())
                .clickTestCase();

        TestCaseModalView modalView = testCaseView.toModalView();
        ExecutionsTab executionsTab = modalView.openExecutionsTab();

        // ZTP-4216 Verify 'Elapsed time' maximum set time can be 99h 59m 59s
        softAssert.assertEquals(executionsTab.getLastExecution().getExecutionElapsedTimeText(),
                expectedMaximumValueElapsedTimeFromExecutionTab,
                "Maximum value of elapsed time isn't equals to expected on execution tab!");

        modalView.getCloseButton().click();
        addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                testSuite.getTitle(), createdTestCases.get(0).getTitle()
        );
        elapsedTime = addExecutionResultModal.getElapsedTime();

        int countOfPressesHours = 5;
        int countOfPressesMinutes = 8;
        int countOfPressesSeconds = 3;
        String expectedTimeAfterPressingArrows = countOfPressesHours + "h "
                + countOfPressesMinutes + "m " + countOfPressesSeconds + "s";

        elapsedTime.clickHoursInput();
        for (int i = 0; i < countOfPressesHours; i++) {
            elapsedTime.getHoursInput().sendKeys(Keys.ARROW_UP);
        }
        softAssert.assertEquals(elapsedTime.getCurrentHoursValue(), "0" + countOfPressesHours,
                "Value of hours isn't equals to expected after using arrow up button");

        elapsedTime.clickMinutesInput();
        for (int i = 0; i < countOfPressesMinutes; i++) {
            elapsedTime.getMinutesInput().sendKeys(Keys.ARROW_UP);
        }
        softAssert.assertEquals(elapsedTime.getCurrentMinutesValue(), "0" + countOfPressesMinutes,
                "Value of minutes isn't equals to expected after using arrow up button");

        elapsedTime.clickSecondsInput();
        for (int i = 0; i < countOfPressesSeconds; i++) {
            elapsedTime.getSecondsInput().sendKeys(Keys.ARROW_UP);
        }
        softAssert.assertEquals(elapsedTime.getCurrentSecondsValue(), "0" + countOfPressesSeconds,
                "Value of seconds isn't equals to expected after using arrow up button");

        addExecutionResultModal.submitModal();
        testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                .getTestCase(createdTestCases.get(0).getTitle())
                .clickTestCase();

        modalView = testCaseView.toModalView();
        executionsTab = modalView.openExecutionsTab();

        // ZTP-4217 Verify 'Elapsed time' field user can change time with arrows on keyboard
        softAssert.assertEquals(executionsTab.getLastExecution().getExecutionElapsedTimeText(),
                expectedTimeAfterPressingArrows,
                "Time isn't equals to expected after clicking arrows");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4221")
    public void verifyElapsedTimeCanBeSetToAnyResult() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        int countOfExistingResults = 6;
        for (int i = 0; i < countOfExistingResults; i++) {
            AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                    testSuite.getTitle(), createdTestCases.get(0).getTitle()
            );

            ListBoxMenu listBoxMenu = addExecutionResultModal.clickResultStatuses();
            List<Element> resultsList = listBoxMenu.getItems();
            String currentSelectedStatus = resultsList.get(i).getText();
            resultsList.get(i)
                    .click();

            log.info("Current selected status - " + currentSelectedStatus);

            int generatedHours = Integer.parseInt(RandomStringUtils.randomNumeric(1));
            int generatedMinutes = Integer.parseInt(RandomStringUtils.randomNumeric(1));
            int generatedSeconds = Integer.parseInt(RandomStringUtils.randomNumeric(1));

            ZbrTimeInput elapsedTime = addExecutionResultModal.getElapsedTime();
            elapsedTime.setTimeValue(generatedHours, generatedMinutes, generatedSeconds);

            addExecutionResultModal.submitModal();

            AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                    .getTestCase(createdTestCases.get(0).getTitle())
                    .clickTestCase();

            TestCaseModalView modalView = testCaseView.toModalView();
            ExecutionsTab executionsTab = modalView.openExecutionsTab();
            ExecutionItem executionItem = executionsTab.getLastExecution();

            softAssert.assertEquals(executionItem.getExecutionStatus(), currentSelectedStatus,
                    "Selected status isn't equals to expected");
            softAssert.assertEquals(executionItem.getExecutionElapsedTimeText(),
                    formatTime(generatedHours, generatedMinutes, generatedSeconds),
                    "Entered elapsed time isn't equals to expected with '" + currentSelectedStatus + "' status");

            modalView.getCloseButton().click();
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5692", "ZTP-5693", "ZTP-5694"})
    public void verifyUserIsAbleToStartAndStopAndResumeTimer() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        int timeForPause = 20;
        int maximumRange = 5;
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                testSuite.getTitle(), createdTestCases.get(0).getTitle()
        );
        ZbrTimeInput elapsedTime = addExecutionResultModal.getElapsedTime();

        elapsedTime.clickStartButton(); // ZTP-5692 User is able to start timer
        softAssert.assertFalse(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field still editable after clicking 'Start timer' button");
        softAssert.assertTrue(elapsedTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Start' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Start' button");

        pause(timeForPause);

        elapsedTime.clickStopButton(); // ZTP-5693 User is able to stop timer
        softAssert.assertTrue(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field isn't become editable after clicking 'Stop timer' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Stop' button");
        softAssert.assertTrue(elapsedTime.isResumeButtonPresent(),
                "'Resume' button isn't appear after clicking 'Stop' button");

        int currentSecondsValue = Integer.parseInt(elapsedTime.getCurrentSecondsValue());
        softAssert.assertTrue(currentSecondsValue >= (timeForPause - maximumRange) && currentSecondsValue <= timeForPause,
                "Seconds value after stopping timer isn't in expected range");

        elapsedTime.clickResumeButton(); // ZTP-5694 User is able to resume timer
        softAssert.assertFalse(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field still editable after clicking 'Resume timer' button");
        softAssert.assertTrue(elapsedTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Resume' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Resume' button");

        pause(timeForPause);

        elapsedTime.clickStopButton();
        int secondsValueAfterResumingTimer = currentSecondsValue + timeForPause;
        softAssert.assertTrue(secondsValueAfterResumingTimer >= (timeForPause * 2 - maximumRange)
                        && secondsValueAfterResumingTimer <= timeForPause * 2,
                "Seconds value after resuming and stopping timer isn't in expected range");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5695", "ZTP-5706", "ZTP-5703", "ZTP-5704"})
    public void userIsAbleToResetTimer() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                testSuite.getTitle(), createdTestCases.get(0).getTitle()
        );
        ZbrTimeInput elapsedTime = addExecutionResultModal.getElapsedTime();

        elapsedTime.clickStartButton();

        // ZTP-5703 User is not able to edit the time manually when timer is running
        softAssert.assertFalse(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field still editable after clicking 'Start timer' button");
        softAssert.assertTrue(elapsedTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Start' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Start' button");

        pause(10);

        elapsedTime.clickStopButton();

        // ZTP-5704 User is able to edit the time manually when timer is stopped
        softAssert.assertTrue(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field isn't become editable after clicking 'Stop timer' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Stop' button");
        softAssert.assertTrue(elapsedTime.isResumeButtonPresent(),
                "'Resume' button isn't appear after clicking 'Stop' button");

        int currentSecondsValue = Integer.parseInt(elapsedTime.getCurrentSecondsValue());
        softAssert.assertTrue(currentSecondsValue >= 5 && currentSecondsValue <= 10,
                "Seconds value after stopping timer isn't in expected range");

        elapsedTime.clickResetButton(); // ZTP-5695 User able reset timer when the timer is stopped
        softAssert.assertEquals(addExecutionResultModal.getCurrentElapsedTimeValue(), ELAPSED_TIME_DEFAULT_VALUE,
                "Elapsed time value isn't equals to expected after clicking 'Reset' button");
        softAssert.assertTrue(elapsedTime.isStartButtonPresent(),
                "'Start timer' button isn't appear after clicking 'Reset' button");

        elapsedTime.clickStartButton();
        softAssert.assertFalse(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field still editable after clicking 'Start timer' button");
        softAssert.assertTrue(elapsedTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Start' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Start' button");

        pause(7);

        currentSecondsValue = Integer.parseInt(elapsedTime.getCurrentSecondsValue());
        softAssert.assertTrue(currentSecondsValue > 0,
                "Timer is not start after clicking 'Start' button");

        elapsedTime.clickResetButton(); // ZTP-5706 User able reset timer when timer is running
        softAssert.assertEquals(addExecutionResultModal.getCurrentElapsedTimeValue(), ELAPSED_TIME_DEFAULT_VALUE,
                "Elapsed time value isn't equals to expected after clicking 'Reset' button");
        softAssert.assertTrue(elapsedTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Reset' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Reset' button");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5714")
    public void timeContinuesRunningWhenUserAddAttachment() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModalForTestCase(
                testSuite.getTitle(), createdTestCases.get(0).getTitle()
        );
        ZbrTimeInput elapsedTime = addExecutionResultModal.getElapsedTime();

        elapsedTime.clickStartButton();
        softAssert.assertFalse(elapsedTime.isElapsedTimeFieldEditable(),
                "Elapsed time field still editable after clicking 'Start timer' button");
        softAssert.assertTrue(elapsedTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Start' button");
        softAssert.assertTrue(elapsedTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Start' button");

        // ZTP-5714 Time continues running when user clicks the '+Attachment' button
        addExecutionResultModal.clickAddAttachmentButton();
        pause(7);
        addExecutionResultModal.addAttachment(IMAGES_ZEB_PNG);
        ComponentUtil.pressEscape(getDriver());

        int currentSecondsValue = Integer.parseInt(elapsedTime.getCurrentSecondsValue());
        softAssert.assertTrue(currentSecondsValue >= 5 && currentSecondsValue <= 15,
                "Seconds value after stopping timer isn't in expected range");

        addExecutionResultModal.submitModal();
        AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                .getTestCase(createdTestCases.get(0).getTitle())
                .clickTestCase();

        TestCaseModalView modalView = testCaseView.toModalView();
        ExecutionsTab executionsTab = modalView.openExecutionsTab();
        int elapsedTimeOnExecutionTab = Integer.parseInt(executionsTab.getLastExecution()
                .getExecutionElapsedTimeText()
                .replaceAll("\\D", ""));

        softAssert.assertTrue(elapsedTimeOnExecutionTab >= 7 && elapsedTimeOnExecutionTab <= 14,
                "Value of elapsed time isn't equals to expected after saving without stopping time on model");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5696")
    public void timeContinuesRunningWhenUserOpenLinkAndCreateIssueModal() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        IntegrationManager.addIntegration(project.getId(), Tool.JIRA);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                .getTestCase(createdTestCases.get(1).getTitle())
                .clickTestCase();

        TestCaseModalView modalView = testCaseView.toModalView();
        modalView.openExecutionsTab();
        ExecutionsControlPanel executionsPanel = new ExecutionsControlPanel(getDriver());
        ZbrTimeInput zbrTime = executionsPanel.getZbrTimeInput();

        zbrTime.clickStartButton();
        softAssert.assertFalse(zbrTime.isElapsedTimeFieldEditable(),
                "Elapsed time field still editable after clicking 'Start timer' button");
        softAssert.assertTrue(zbrTime.isStopButtonPresent(),
                "'Stop' button isn't visible after clicking 'Start' button");
        softAssert.assertTrue(zbrTime.isResetTimeButtonPresent(),
                "'Reset' button isn't appear after clicking 'Start' button");

        pause(3);
        softAssert.assertTrue(Integer.parseInt(zbrTime.getCurrentSecondsValue()) > 0,
                "Timer isn't start after clicking 'Start' button");

        LinkIssueModal linkIssueModal = executionsPanel.clickLinkIssueButton();
        softAssert.assertTrue(linkIssueModal.isModalOpened(), "Link issue modal isn't opened");

        linkIssueModal.clickCreateNewIssueButton();
        CreateJiraIssueModal createJiraIssueModal = new CreateJiraIssueModal(getDriver());
        softAssert.assertTrue(createJiraIssueModal.isModalOpened(), "Create new Jira issue modal isn't opened");

        pause(5);
        ComponentUtil.pressEscape(getDriver());

        zbrTime = executionsPanel.getZbrTimeInput();
        softAssert.assertTrue(Integer.parseInt(zbrTime.getCurrentSecondsValue()) > 5,
                "Timer isn't continue running after clicking link issue modal");

        softAssert.assertAll();
    }

    public String formatTime(int hours, int minutes, int seconds) {
        String formattedToStringTime = "";
        if (hours != 0) {
            formattedToStringTime += hours + "h ";
        }

        if (minutes != 0) {
            formattedToStringTime += minutes + "m ";
        }

        if (seconds != 0) {
            formattedToStringTime += seconds + "s";
        }

        return formattedToStringTime.trim();
    }
}
