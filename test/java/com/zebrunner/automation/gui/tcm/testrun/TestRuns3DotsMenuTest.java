package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.tcm.DeleteModal;
import com.zebrunner.automation.gui.reporting.launch.AssignToMilestoneModalR;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;
import com.zebrunner.automation.api.tcm.domain.Environment;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.reporting.domain.Milestone;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import com.zebrunner.automation.legacy.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Maintainer("akhivyk")
public class TestRuns3DotsMenuTest extends TcmLogInBase {

    private Environment environment;
    private Milestone milestone;
    private User createdUserForAssign;
    private TestRun testRun;
    private String testRunName;
    private Project project;
    private TestSuite testSuite;
    private List<TestCase> failedTestCases;
    private List<TestCase> skippedTestCases;
    private List<TestCase> retestedTestCases;
    private TestRunSettings testRunSettings;

    @BeforeClass
    public void preparation() {
        String milestoneName = "Milestone_" + RandomStringUtils.randomAlphabetic(5);
        project = super.getCreatedProject();
        createdUserForAssign = usersService.create(usersService.generateRandomUser());

        environment = environmentService.createEnvironment(project.getId(), Environment.createRandom());
        milestone = apiHelperService.createMilestone(project.getId(), Milestone.createMilestoneWithTitle(milestoneName));

        testSuite = tcmService.createTestSuite(project.getId(), new TestSuite("Suite for automation " + RandomStringUtils.randomNumeric(5)));
        failedTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);
        skippedTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);
        retestedTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);

        List<TestCase> allTestCases = Stream.of(
                        failedTestCases,
                        skippedTestCases,
                        retestedTestCases
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());

        testRun = tcmService.createTestRun(project.getId(), allTestCases, TestRun.createWithRandomName());
        testRunSettings = tcmService.getTestRunSettings(project.getId());

        tcmService.addTestRunResults(project.getId(), testRun, failedTestCases, testRunSettings, "Failed");
        tcmService.addTestRunResults(project.getId(), testRun, skippedTestCases, testRunSettings, "Skipped");
        tcmService.addTestRunResults(project.getId(), testRun, retestedTestCases, testRunSettings, "Retest");

        tcmService.assignTestCases(project.getId(), testRun, failedTestCases, Long.valueOf(createdUserForAssign.getId()));
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedData() {
        environmentService.deleteEnvironment(project.getId(), environment.getId());
        apiHelperService.deleteMilestone(project.getId(), milestone.getId());
        tcmService.deleteTestSuite(project.getId(), testSuite.getId());
        usersService.deleteUserById(createdUserForAssign.getId());
    }

    @Test
    @TestCaseKey({"ZTP-5491", "ZTP-2927", "ZTP-4222"})
    public void verifyUserIsAbleToCloseTestRunVia3DotMenu() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        String expectedClosedLabel = "Closed";

        testRunName = "Test run " + RandomStringUtils.randomAlphabetic(5);
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        TestRunPage testRunPage = createTestRunPage.inputTitle(testRunName)
                .clickCreateButton();

        testRunsGridPage = testRunPage.backToTestRunsGrid();
        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRunName);

        Dropdown menuOptions = testRunItem.clickMenuButton();
        // ZTP-2927 User is able to open 3 dot menu
        softAssert.assertFalse(menuOptions.getDropdownItems().isEmpty(), "Menu options isn't present!");

        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.CLOSE);
        CloseTestRunModal closeTestRunModal = new CloseTestRunModal(getDriver());

        softAssert.assertTrue(closeTestRunModal.isModalOpened(),
                "Close test run modal isn't opened after clicking 'Close' button via 3 dot menu");

        closeTestRunModal.clickCloseButton();
        softAssert.assertEquals(testRunsGridPage.getPopUp(), MessageEnum.TEST_RUN_CLOSED.getDescription(),
                "Popup message isn't equals to expected after closing test run");

        testRunsGridPage = testRunsGridPage.clickOpenedTestRuns();
        softAssert.assertFalse(testRunsGridPage.isTestRunExist(testRunName),
                "Test run " + testRunName + " is still present in opened test runs list after closing via 3 dot menu");

        testRunsGridPage = testRunsGridPage.clickClosedTestRuns();
        // ZTP-5491 User is able to Close run via 3 dot menu
        softAssert.assertTrue(testRunsGridPage.isTestRunExist(testRunName),
                "Test run " + testRunName + " isn't present in closed test runs list after closing via 3 dot menu");

        // ZTP-4222 Verify after closing test run, corresponding label appears
        testRunPage = testRunsGridPage.getTestRunItem(testRunName)
                        .clickTestRunItem();
        softAssert.assertTrue(testRunPage.isClosedLabelAppear(), "Closed label isn't appear after closing test run");
        softAssert.assertEquals(testRunPage.getClosedLabelText(), expectedClosedLabel,
                "Text on closed label isn't equals to expected");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2929", "ZTP-5490"})
    public void verifyUserIsAbleToEditTestRunAndCopyIdVia3DotMenu() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();

        testRunName = "Test run " + RandomStringUtils.randomAlphabetic(5);
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        TestRunPage testRunPage = createTestRunPage.inputTitle(testRunName)
                .clickCreateButton();

        testRunsGridPage = testRunPage.backToTestRunsGrid();
        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRunName);

        testRunItem.clickMenuButton();
        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.EDIT);

        EditTestRunPage editTestRunPage = new EditTestRunPage(getDriver());
        softAssert.assertTrue(editTestRunPage.isPageOpened(), "Edit test run page isn't opened via 3 dot menu");

        String updatedTestRunName = "New test run " + RandomStringUtils.randomAlphabetic(5);
        editTestRunPage.inputTitle(updatedTestRunName);

        testRunPage = editTestRunPage.clickSaveButton();
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_RUN_UPDATED.getDescription(),
                "Popup message isn't equals to expected after updating test run");

        testRunsGridPage = testRunPage.backToTestRunsGrid();
        softAssert.assertFalse(testRunsGridPage.isTestRunExist(testRunName),
                "Test run with previous name sill exist in all test run grid");

        testRunsGridPage.getFilters().clickResetFilter();
        softAssert.assertTrue(testRunsGridPage.isTestRunExist(updatedTestRunName),
                "Test run with updated name isn't present in all test run grid"); // ZTP-2929 able 'Edit run' via 3 dot menu

        testRunItem = testRunsGridPage.getTestRunItem(testRun.getTitle());

        testRunItem.clickMenuButton();
        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.COPY_ID);

        softAssert.assertEquals(testRunsGridPage.getPopUp(), MessageEnum.TEST_RUN_ID_COPIED.getDescription(),
                "Popup message isn't equals to expected after copying test run id");
        softAssert.assertEquals(testRunsGridPage.getClipboardText(), testRun.getId().toString(),
                "Copied id in clipboard isn't equals to expected"); // ZTP-5490 User is able to Copy ID via 3 dot menu

        testRunItem.clickTestRunItem();
        Long testRunIdFromUrl = Long.parseLong(getDriver().getCurrentUrl().replaceAll(".*test-runs/(\\d+).*", "$1"));
        softAssert.assertEquals(testRunIdFromUrl, testRun.getId(), "Test run id from url isn't equals to expected");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5668")
    public void verifyUserIsAbleToDeleteTestRun() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();

        testRunName = "Test run " + RandomStringUtils.randomAlphabetic(5);
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        TestRunPage testRunPage = createTestRunPage.inputTitle(testRunName)
                .selectMilestone(milestone.getName())
                .selectEnvironment(environment.getName())
                .clickCreateButton();

        TestRunItem testRunItem = testRunPage.backToTestRunsGrid()
                .getTestRunItem(testRunName);

        testRunItem.clickMenuButton();
        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.DELETE);

        DeleteModal deleteModal = new DeleteModal(getDriver());
        deleteModal.clickDeleteButton();
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_RUN_DELETED.getDescription(),
                "Popup message isn't equals to expected after deleting test run");

        testRunsGridPage.getFilters().clickResetFilter();
        softAssert.assertFalse(testRunsGridPage.isTestRunExist(testRunName),
                "Test run is still exist in all test runs list"); // ZTP-5668 User is able to Delete run via 3 dot menu

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5666", "ZTP-5669", "ZTP-5667", "ZTP-5670"})
    public void verifyUserIsAbleToAssignToMilestoneVia3DotMenu() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();

        testRunName = "Test run " + RandomStringUtils.randomAlphabetic(5);
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        TestRunItem testRunItem = createTestRunPage.inputTitle(testRunName)
                .clickCreateButton()
                .backToTestRunsGrid()
                .getTestRunItem(testRunName);

        testRunItem.clickMenuButton();
        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.ASSIGN_TO_MILESTONE);

        AssignToMilestoneModalR assignToMilestoneModal = new AssignToMilestoneModalR(getDriver());
        softAssert.assertTrue(assignToMilestoneModal.isAssignToMilestoneModalOpened(),
                "Assign to milestone modal isn't opened via 3 dot menu"); // ZTP-5666 User is able open 'Assign to milestone' window

        assignToMilestoneModal.chooseMilestoneAndAssign(milestone.getName());
        softAssert.assertEquals(testRunItem.getMilestoneName(), milestone.getName(),
                "Milestone of assigned test run isn't equals to expected"); // ZTP-5669 User is able to assign milestone via 3 dot menu

        String secondTestRunName = "Test Run " + RandomStringUtils.randomAlphabetic(5);
        testRunItem = testRunsGridPage.clickCreateTestRunButton()
                .inputTitle(secondTestRunName)
                .clickCreateButton()
                .backToTestRunsGrid()
                .getTestRunItem(secondTestRunName);

        TestRunPage testRunPage = testRunItem.clickTestRunItem();
        testRunPage.openMenuAndSelectOption(TestRunPage.TestRunPageMenuActions.ASSIGN_TO_MILESTONE);

        // ZTP-5667 User is able to open ''Assign to milestone'' window via 3 dot menu in Test run page
        softAssert.assertTrue(assignToMilestoneModal.isAssignToMilestoneModalOpened(),
                "Assign to milestone modal isn't opened via 3 dot menu");
        assignToMilestoneModal.chooseMilestoneAndAssign(milestone.getName());

        // ZTP-5670 User is able to assign milestone via 3 dot menu in Test run page
        softAssert.assertEquals(testRunPage.getCollapsedHeader().getMilestoneName(), milestone.getName(),
                "Milestone name isn't equals to expected after assigning milestone to test run on test run page");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2930", "ZTP-5489"})
    public void verifyUserIsAbleToOpenRerunModalWindowAndClose() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        Actions actions = new Actions(getDriver());
        SoftAssert softAssert = new SoftAssert();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRun.getTitle());

        for (int i = 0; i < 3; i++) {

            testRunItem.clickMenuButton();
            testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.RERUN);

            // ZTP-2930 User is able to open rerun modal window via 3 dot menu
            RerunModal rerunModal = new RerunModal(getDriver());
            softAssert.assertTrue(rerunModal.isModalOpened(), "Rerun modal window isn't opened!");

            if (i == 0) {
                rerunModal.clickCancel();
            } else if (i == 1) {
                rerunModal.clickCrossButton();
            } else {
                actions.keyDown(Keys.ESCAPE).perform();
                actions.keyUp(Keys.ESCAPE).perform();
            }

            // ZTP-5489 User is able to close rerun modal window
            softAssert.assertFalse(rerunModal.isModalOpened(), "Rerun modal is still open after " + i + " iteration!");
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-2931")
    public void verifyUserIsAbleToSelectDifferentTestStatusesOnRerun() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        String testRunNameAfterRerun = "Rerun " + RandomStringUtils.randomNumeric(3) + " " + testRun.getTitle();
        String expectedCountCasesOnRerun = "4";

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRun.getTitle());

        testRunItem.clickMenuButton();
        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.RERUN);

        RerunModal rerunModal = new RerunModal(getDriver());
        rerunModal.selectOption(RerunModal.TestRunStatuses.FAILED);

        softAssert.assertTrue(rerunModal.isOptionSelected(RerunModal.TestRunStatuses.FAILED),
                RerunModal.TestRunStatuses.FAILED.getValue() + " status isn't marked as selected after clicking checkbox");

        rerunModal.selectOption(RerunModal.TestRunStatuses.RETEST);
        softAssert.assertTrue(rerunModal.isOptionSelected(RerunModal.TestRunStatuses.RETEST),
                RerunModal.TestRunStatuses.RETEST.getValue() + " status isn't marked as selected after clicking checkbox");
        softAssert.assertTrue(rerunModal.isOptionSelected(RerunModal.TestRunStatuses.FAILED),
                RerunModal.TestRunStatuses.FAILED.getValue() + " status isn't marked as selected after selecting another option");

        CreateTestRunPage createTestRunPage = rerunModal.clickOkButton();
        softAssert.assertEquals(createTestRunPage.getLinkedTestCasesNumber(), expectedCountCasesOnRerun,
                "Count of linked cases isn't equals to expected on create test run page after rerun window");

        TestRunPage testRunPage = createTestRunPage.inputTitle(testRunNameAfterRerun)
                        .clickCreateButton();

        List<TestRunSuiteItem> existingSuites = testRunPage.expandAndGetAllTestSuites();
        int countCases = existingSuites.stream()
                .mapToInt(TestRunSuiteItem::getCountOfCases)
                .sum();

        softAssert.assertEquals(countCases, Integer.parseInt(expectedCountCasesOnRerun), "Count of cases in rerun isn't equals to expected");

        Map<String, List<TestCase>> testCaseMap = new HashMap<>();
        testCaseMap.put(RerunModal.TestRunStatuses.FAILED.getValue(), failedTestCases);
        testCaseMap.put(RerunModal.TestRunStatuses.RETEST.getValue(), retestedTestCases);
        testCaseMap.put(RerunModal.TestRunStatuses.SKIPPED.getValue(), skippedTestCases);

        testCaseMap.forEach((status, cases) -> {
            boolean expectedToAppear = !status.equals(RerunModal.TestRunStatuses.SKIPPED.getValue());
            cases.forEach(testCase -> {
                String message = "Test case with status " + status + " isn't found after rerun";
                softAssert.assertEquals(
                        expectedToAppear,
                        testRunPage.isTestCaseAppear(testCase.getTitle()),
                        message
                );
            });
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-2932")
    public void verifyUserIsAbleToClickCopyAssignedToCheckbox() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        String testRunNameAfterRerun = "Rerun " + RandomStringUtils.randomNumeric(3) + " " + testRun.getTitle();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRun.getTitle());

        testRunItem.clickMenuButton();
        testRunItem.clickMenuAction(TestRunItem.TestRunMenuActions.RERUN);

        RerunModal rerunModal = new RerunModal(getDriver());
        rerunModal.selectOption(RerunModal.TestRunStatuses.SKIPPED);
        rerunModal.selectOption(RerunModal.TestRunStatuses.FAILED);
        rerunModal.clickCopyAssignedTo();

        TestRunPage testRunPage = rerunModal.clickOkButton()
                .inputTitle(testRunNameAfterRerun)
                .clickCreateButton();

        for (TestCase failedTestCase : failedTestCases) {
            String assignedUsername = testRunPage.getTestSuite(testSuite.getTitle())
                    .getTestCase(failedTestCase.getTitle()).getAssignedUsername();

            softAssert.assertEquals(assignedUsername, StringUtil.getExpectedAuthor(createdUserForAssign),
                    "Assigned username isn't equals to expected after rerun!");
        }

        softAssert.assertAll();
    }
}
