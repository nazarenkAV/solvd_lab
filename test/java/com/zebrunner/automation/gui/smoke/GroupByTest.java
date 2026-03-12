package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.project.method.v1.ProjectAssignment;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.FailureTagModal;
import com.zebrunner.automation.gui.reporting.launch.TestDetailsTableRow;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.common.SelectWrapperMenu;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;
import com.zebrunner.automation.legacy.SortUtil;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Maintainer("Gmamaladze")
public class GroupByTest extends LogInBase {

    private final List<Long> testRunIdList = new ArrayList<>();
    private final Label FIRST_LABEL = new Label("Platform", "Zebrunner");
    private final Label SECOND_LABEL = new Label("Framework", "Carina");
    private Project project;
    private Launch launch;
    private TestExecution testExecutionFailed;
    private TestExecution secondTestExecutionFailed;
    private TestExecution testExecutionPassed;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @BeforeMethod
    public void createLaunchWithTests() {
        launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());

        // ------------------------------------- Failed -----------------------------------------------------

        testExecutionFailed = testService.startTest(TestExecution.getTestExecution(
                "Test1", UsersEnum.MAIN_ADMIN.getUser().getUsername(), "com.zebrunner.test.class"), launch.getId());
        apiHelperService.addLabelsToTest(launch.getId(), testExecutionFailed.getId(), Collections.singletonList(FIRST_LABEL));
        testService.finishTestAsResult(launch.getId(), testExecutionFailed.getId(), FinishTestRequest.getRequestWithReason("Java.lang.AssertionError: The following asserts failed:"));

        secondTestExecutionFailed = testService.startTest(TestExecution.getTestExecution(
                "Test2", UsersEnum.MAIN_ADMIN.getUser().getUsername(), "com.zebrunner.test.class"), launch.getId());
        apiHelperService.addLabelsToTest(launch.getId(), secondTestExecutionFailed.getId(), Collections.singletonList(FIRST_LABEL));
        testService.finishTestAsResult(launch.getId(), secondTestExecutionFailed.getId(), FinishTestRequest.getRequestWithReason(RandomStringUtils.randomAlphabetic(6)));

        // -------------------------------------- Passed -----------------------------------------------------

        testExecutionPassed = testService.startTest(TestExecution.getTestExecution(
                "Test3", "", "com.zebrunner.test2.class2"), launch.getId());
        apiHelperService.addLabelsToTest(launch.getId(), testExecutionPassed.getId(), Collections.singletonList(SECOND_LABEL));
        testService.finishTestAsResult(launch.getId(), testExecutionPassed.getId(), "PASSED");

        testRunService.finishTestRun(launch.getId());
        testRunIdList.add(launch.getId());
    }


    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }


    // ------------------------------------- Test ------------------------------------------------------

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1338")
    public void groupByFile() {
        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        String failedTestsClassName = testExecutionFailed.getClassName();
        String passedTestClassName = testExecutionPassed.getClassName();

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FILE);

        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(failedTestsClassName),
                "Table with file (class) name " + failedTestsClassName + " should be present !");
        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(passedTestClassName),
                "Table with file (class) name " + passedTestClassName + " should be present !");

        testRunResultPage.getTableByName(failedTestsClassName).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' should be present in file (class) '%s' table !", failedTestName, failedTestsClassName));
        softAssert.assertTrue(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should be present in file (class) '%s' table !", secondFailedTestName, failedTestsClassName));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present on file (class) '%s' table !", passedTestName, failedTestsClassName));

        testRunResultPage.getTableByName(passedTestClassName).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' should be present in file (class) '%s' table !", passedTestName, passedTestClassName));
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present on file (class) '%s' table !", failedTestName, passedTestClassName));
        softAssert.assertFalse(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' shouldn't be present on file (class) '%s' table !", secondFailedTestName, passedTestClassName));

        softAssert.assertAll();
    }


    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1338")
    public void groupByDirectory() {
        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        String failedTestsDirectory = testExecutionFailed.getClassName().split(".class")[0];
        String passedTestDirectory = testExecutionPassed.getClassName().split(".class2")[0];

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_DIRECTORY);

        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(failedTestsDirectory),
                "Table with directory name " + failedTestsDirectory + " should be present !");
        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(passedTestDirectory),
                "Table with directory name " + passedTestDirectory + " should be present !");

        testRunResultPage.getTableByName(failedTestsDirectory).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' should be present in directory '%s' table !", failedTestName, failedTestsDirectory));
        softAssert.assertTrue(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should be present in directory '%s' table !", secondFailedTestName, failedTestsDirectory));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present on directory '%s' table !", passedTestName, failedTestsDirectory));

        testRunResultPage.getTableByName(passedTestDirectory).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' should be present in directory '%s' table !", passedTestName, passedTestDirectory));
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present in directory '%s' table !", failedTestName, passedTestDirectory));
        softAssert.assertFalse(testRunResultPage.isTestPresent(secondTestExecutionFailed.getName()),
                String.format("Test '%s' shouldn't be present in directory '%s' table !", secondFailedTestName, passedTestDirectory));

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1338")
    public void groupByLabel() {
        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        String failedTestsLabelKey = FIRST_LABEL.getKey();
        String passedTestLabelKey = SECOND_LABEL.getKey();

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_LABEL);

        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(failedTestsLabelKey),
                "Table with label " + failedTestsLabelKey + " should be present !");
        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(passedTestLabelKey),
                "Table with label " + passedTestLabelKey + " should be present !");

        testRunResultPage.getTableByName(failedTestsLabelKey).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' should present in label '%s' table !", failedTestName, failedTestsLabelKey));
        softAssert.assertTrue(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should present in label '%s' table !", secondFailedTestName, failedTestsLabelKey));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in label '%s' table !", passedTestName, failedTestsLabelKey));

        testRunResultPage.getTableByName(passedTestLabelKey).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' should present in label '%s' table !", passedTestName, passedTestLabelKey));
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present in label '%s' table !", failedTestName, passedTestLabelKey));
        softAssert.assertFalse(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' shouldn't be present in label '%s' table !", secondFailedTestName, passedTestLabelKey));

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1338")
    public void groupByFailure() {
        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();

        String failedTestCardStackTrace = testRunResultPage.getCertainTest(testExecutionFailed.getName())
                .getStackTracePreview();
        String secondFailedTestCardStackTrace = testRunResultPage.getCertainTest(secondTestExecutionFailed.getName())
                .getStackTracePreview();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE);

        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(failedTestCardStackTrace),
                String.format("Table with stack trace '%s' should be present !", failedTestCardStackTrace));
        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(secondFailedTestCardStackTrace),
                String.format("Table with stack trace '%s' should be present !", secondFailedTestCardStackTrace));

        testRunResultPage.getTableByName(failedTestCardStackTrace).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' should be present in failure '%s' table !", failedTestName, failedTestCardStackTrace));
        softAssert.assertFalse(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' shouldn't be present in failure '%s' table !", secondFailedTestName, failedTestCardStackTrace));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in failure '%s' table !", passedTestName, failedTestCardStackTrace));

        testRunResultPage.getTableByName(secondFailedTestCardStackTrace).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should be present in failure '%s' table !", secondFailedTestName, secondFailedTestCardStackTrace));
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present in failure '%s' table !", failedTestName, secondFailedTestCardStackTrace));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in failure '%s' table !", passedTestName, secondFailedTestCardStackTrace));

        softAssert.assertAll();
    }


    @Test
    @TestCaseKey("ZTP-1338")
    @Maintainer("Gmamaladze")
    public void groupByFailureTag() {
        WebDriver webDriver = super.getDriver();

        TestRunResultPageR launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launch.getId());

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();

        ResultTestMethodCardR failedTestCard = launchPage.getCertainTest(failedTestName);
        ResultTestMethodCardR secondFailedTestCard = launchPage.getCertainTest(secondFailedTestName);

        String failedTestTag = failedTestCard.getFailureTagText();
        String secondFailedTestTag = secondFailedTestCard.getFailureTagText();

        if (failedTestTag.equals(secondFailedTestTag)) {
            FailureTagModal failureTagModal = launchPage.getCertainTest(failedTestName)
                                                        .getFailureTagModal();

            if (!failedTestTag.equals("Uncategorized")) {
                failureTagModal.clickUncategorizedTagButton();
                failureTagModal.clickSaveButton();
            } else {
                failureTagModal.clickBusinessIssueTagButton();
                failureTagModal.clickSaveButton();
            }
            super.pause(1);

            failedTestTag = failedTestCard.getFailureTagText();

            // We are hovering because to move mouse from notification
            failedTestCard.hoverCard();
            launchPage.waitPopupDisappears();
        }

        ActionsBlockR actionsBlock = launchPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE_TAG);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(
                launchPage.isTableWithNamePresent(failedTestTag),
                String.format("Table with failure tag '%s' should be present !", failedTestTag)
        );
        softAssert.assertTrue(
                launchPage.isTableWithNamePresent(secondFailedTestTag),
                String.format("Table with failure tag '%s' should be present !", secondFailedTestTag)
        );
        softAssert.assertAll();

        launchPage.getTableByName(failedTestTag).clickOnTable();

        softAssert.assertTrue(
                launchPage.isTestPresent(failedTestName),
                String.format("Test '%s' should be present in failure tag '%s' table !", failedTestName, failedTestTag)
        );
        softAssert.assertFalse(
                launchPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' shouldn't be present in failure tag '%s' table !", secondFailedTestName, failedTestTag)
        );
        softAssert.assertFalse(
                launchPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in failure tag '%s' table !", passedTestName, failedTestTag)
        );
        softAssert.assertAll();

        launchPage.getTableByName(secondFailedTestTag).clickOnTable();

        softAssert.assertTrue(
                launchPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should be present in failure tag '%s' table !", secondFailedTestName, secondFailedTestTag)
        );
        softAssert.assertFalse(
                launchPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present in failure tag '%s' table !", failedTestName, secondFailedTestTag)
        );
        softAssert.assertFalse(
                launchPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in failure tag '%s' table !", passedTestName, secondFailedTestTag)
        );
        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1338")
    public void groupByMaintainer() {
        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        String failedTestsMaintainer = testExecutionFailed.getMaintainer();
        String passedTestMaintainer = testExecutionPassed.getMaintainer();

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_MAINTAINER);

        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(failedTestsMaintainer),
                "Table with maintainer " + failedTestsMaintainer + " should be present !");
        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(passedTestMaintainer),
                "Table with maintainer " + passedTestMaintainer + " should be present !");

        testRunResultPage.getTableByName(failedTestsMaintainer).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' should present in maintainer '%s' table !", failedTestName, failedTestsMaintainer));
        softAssert.assertTrue(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should present in maintainer '%s' table !", secondFailedTestName, failedTestsMaintainer));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in maintainer '%s' table !", passedTestName, failedTestsMaintainer));

        testRunResultPage.getTableByName(passedTestMaintainer).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' should present in maintainer '%s' table !", passedTestName, passedTestMaintainer));
        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present in maintainer '%s' table !", failedTestName, passedTestMaintainer));
        softAssert.assertFalse(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' shouldn't be present in maintainer '%s' table !", secondFailedTestName, passedTestMaintainer));

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5763")
    public void verifyUserInfoTooltipIsVisibleWhenHoveringMaintainerTest() {
        User mainAdmin = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        ProjectAssignment assignedUser = projectV1Service.getProjectAssignmentForUser(Math.toIntExact(project.getId()), mainAdmin.getUsername());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver()).openPageDirectly(project.getKey(), launch.getId());

        testRunResultPage.getCertainTest(testExecutionFailed.getName()).hoverTestMaintainer()
                .verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "At test run results page!");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4355")
    public void verifyGroupByFailureAreSortedByCountDescending() {
        for (int i = 0; i < 3; i++) {
            TestExecution testExecutionFailed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
            testService.finishTestAsResult(launch.getId(), testExecutionFailed.getId(), FinishTestRequest.getRequestWithReason("Java.lang.AssertionError: The following asserts failed:"));
        }

        for (int i = 0; i < 2; i++) {
            TestExecution testExecutionFailed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
            testService.finishTestAsResult(launch.getId(), testExecutionFailed.getId(), FinishTestRequest.getRequestWithReason("another one"));
        }

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE);

        List<TestDetailsTableRow> failureTableList = testRunResultPage.getTestTableRows();

        boolean result = SortUtil.isSorted(failureTableList, Comparator.comparing(TestDetailsTableRow::getTestCountText), false);
        Assert.assertTrue(result, "Failed test count should be descending");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4504")
    public void verifyUngroupedGroup() {
        SoftAssert softAssert = new SoftAssert();

        final String TEST_GROUP = "Regression";
        final String UNGROUPED = "*Ungrouped";
        TestExecution testExecutionWithCustomGroup = testService.startTest(
                TestExecution.builder()
                        .name("Test № ".concat(RandomStringUtils.randomAlphabetic(5)))
                        .startedAt(OffsetDateTime.now())
                        .methodName("Method № ".concat(RandomStringUtils.randomAlphabetic(5)))
                        .className("Test.class")
                        .testGroups(Set.of(TEST_GROUP))
                        .build(),
                launch.getId()
        );
        testService.finishTestAsResult(launch.getId(), testExecutionWithCustomGroup.getId(), "PASSED");

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());
        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.CUSTOM_TEST_GROUP);

        String failedTestName = testExecutionFailed.getName();
        String secondFailedTestName = secondTestExecutionFailed.getName();
        String passedTestName = testExecutionPassed.getName();
        String groupedTestName = testExecutionWithCustomGroup.getName();

        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(TEST_GROUP),
                String.format("Table with name %s should be present !", TEST_GROUP));
        softAssert.assertTrue(testRunResultPage.isTableWithNamePresent(UNGROUPED),
                String.format("Table with name %s should be present !", UNGROUPED));

        testRunResultPage.getTableByName(TEST_GROUP).clickOnTable();

        softAssert.assertFalse(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' shouldn't be present in '%s' table !", failedTestName, TEST_GROUP));
        softAssert.assertFalse(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' shouldn't be present in '%s' table !", secondFailedTestName, TEST_GROUP));
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' shouldn't be present in '%s' table !", passedTestName, TEST_GROUP));
        softAssert.assertTrue(testRunResultPage.isTestPresent(groupedTestName),
                String.format("Test '%s' should be present in '%s' table !", groupedTestName, TEST_GROUP));

        testRunResultPage.getTableByName(UNGROUPED).clickOnTable();

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedTestName),
                String.format("Test '%s' should be present in '%s' table !", failedTestName, UNGROUPED));
        softAssert.assertTrue(testRunResultPage.isTestPresent(secondFailedTestName),
                String.format("Test '%s' should be present in '%s' table !", secondFailedTestName, UNGROUPED));
        softAssert.assertTrue(testRunResultPage.isTestPresent(passedTestName),
                String.format("Test '%s' should be present in '%s' table !", passedTestName, UNGROUPED));
        softAssert.assertFalse(testRunResultPage.isTestPresent(groupedTestName),
                String.format("Test '%s' shouldn't be present in '%s' table ! (Cause it has been already grouped)",
                        groupedTestName, UNGROUPED));

        softAssert.assertAll();
    }
}