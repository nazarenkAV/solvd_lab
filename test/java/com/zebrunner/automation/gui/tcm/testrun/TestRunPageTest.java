package com.zebrunner.automation.gui.tcm.testrun;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.config.TestMaintainers;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.tcm.DeleteModal;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.gui.tcm.testcase.EditTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.AbstractTestCasePreview;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseModalView;
import com.zebrunner.automation.gui.tcm.testcase.AttachmentItem;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionItem;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionsTab;
import com.zebrunner.automation.gui.tcm.testcase.PropertiesTab;
import com.zebrunner.automation.gui.tcm.testrun.testcase.SelectTestCasesModal;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;

@Slf4j
@Maintainer(TestMaintainers.DKAZAK)
@TestLabel(name = TestLabelsConstant.GROUP, value = TestLabelsConstant.TEST_RUNS)
public class TestRunPageTest extends TcmLogInBase {
    private TestRun testRun;
    private TestRun testRunForDifferentStatuses;
    private Project project;
    private User mainAdmin;
    private TestSuite testSuite;
    private TestSuite testSuiteForDifferentStatuses;
    private List<TestCase> allTestCases;
    private List<TestCase> failedTestCases;
    private List<TestCase> skippedTestCases;
    private List<TestCase> blockedTestCases;
    private List<TestCase> createdTestCasesForDifferentStatuses;
    private TestRunSettings testRunSettings;
    private User createdUserForAssign;
    private final static String IMAGES_ZEB_PNG = "src/test/resources/images/zeb.png";

    @BeforeClass
    public void preparation() {
        project = super.getCreatedProject();
        createdUserForAssign = usersService.create(usersService.generateRandomUser());
        mainAdmin = usersService.getUserByUsername(MAIN_ADMIN.getUsername());

        testSuite = TestSuite.builder()
                             .title("Suite for automation " + RandomStringUtils.randomNumeric(5))
                             .description("Description " + RandomStringUtils.randomAlphabetic(5))
                             .build();
        testSuite = tcmService.createTestSuite(project.getId(), testSuite);

        failedTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);
        skippedTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);
        blockedTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);

        allTestCases = Stream.of(
                                     failedTestCases,
                                     skippedTestCases,
                                     blockedTestCases
                             )
                             .flatMap(List::stream)
                             .collect(Collectors.toList());

        testRun = tcmService.createTestRun(project.getId(), allTestCases, TestRun.createWithRandomName());
        testRunSettings = tcmService.getTestRunSettings(project.getId());

        tcmService.addTestRunResults(project.getId(), testRun, failedTestCases, testRunSettings, "Failed");
        tcmService.addTestRunResults(project.getId(), testRun, skippedTestCases, testRunSettings, "Skipped");
        tcmService.addTestRunResults(project.getId(), testRun, blockedTestCases, testRunSettings, "Blocked");

        testSuiteForDifferentStatuses = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        createdTestCasesForDifferentStatuses = tcmService.createTestCases(project.getId(),
                testSuiteForDifferentStatuses.getId(), 6);
        testRunForDifferentStatuses = tcmService.createTestRun(project.getId(), createdTestCasesForDifferentStatuses, TestRun.createWithRandomName());
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedData() {
        tcmService.deleteTestSuite(project.getId(), testSuite.getId());
        tcmService.deleteTestSuite(project.getId(), testSuiteForDifferentStatuses.getId());
        usersService.deleteUserById(createdUserForAssign.getId());
    }

    @Test
    @TestCaseKey({"ZTP-2934", "ZTP-5492"})
    public void userIsAbleToOpenEditPageAndCopyIdOnTestRunPage() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        testRunPage.openMenuAndSelectOption(TestRunPage.TestRunPageMenuActions.EDIT);
        EditTestRunPage editTestRunPage = new EditTestRunPage(getDriver());

        softAssert.assertTrue(editTestRunPage.isPageOpened(), "Edit run page isn't opened!");

        String newRunName = "Edit run " + RandomStringUtils.randomAlphabetic(5);
        editTestRunPage.inputTitle(newRunName);
        editTestRunPage.clickSaveButton();

        // ZTP-2934 User is able to open 'Edit test run' modal window in Test run page
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_RUN_UPDATED.getDescription(),
                "Popup message isn't equals to expected after updating test run");
        softAssert.assertEquals(testRunPage.getName(), newRunName, "Title od test run isn't updated!");

        testRun.setTitle(newRunName);

        testRunPage.openMenuAndSelectOption(TestRunPage.TestRunPageMenuActions.COPY_ID);

        // ZTP-5492 User is able to Copy ID in Test run page
        softAssert.assertEquals(testRunPage.getClipboardText(), testRun.getId().toString(),
                "Copied id in clipboard isn't equals to expected");

        Long testRunIdFromUrl = Long.parseLong(getDriver().getCurrentUrl().replaceAll(".*test-runs/(\\d+).*", "$1"));
        softAssert.assertEquals(testRunIdFromUrl, testRun.getId(), "Test run id from url isn't equals to expected");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-2935")
    public void verifyUserIsAbleToUseRerunOptionOnTestRunPage() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());
        String testRunNameAfterRerun = "Rerun " + RandomStringUtils.randomNumeric(3) + " " + testRun.getTitle();
        String expectedCountCasesOnRerun = "4";

        RerunModal rerunModal = testRunPage.openRerunModal();
        softAssert.assertTrue(rerunModal.isModalOpened(), "Rerun modal window isn't opened!");

        rerunModal.selectOption(RerunModal.TestRunStatuses.FAILED);

        softAssert.assertTrue(rerunModal.isOptionSelected(RerunModal.TestRunStatuses.FAILED),
                RerunModal.TestRunStatuses.FAILED.getValue() + " status isn't marked as selected after clicking checkbox");

        rerunModal.selectOption(RerunModal.TestRunStatuses.BLOCKED);
        softAssert.assertTrue(rerunModal.isOptionSelected(RerunModal.TestRunStatuses.BLOCKED),
                RerunModal.TestRunStatuses.BLOCKED.getValue() + " status isn't marked as selected after clicking checkbox");
        softAssert.assertTrue(rerunModal.isOptionSelected(RerunModal.TestRunStatuses.FAILED),
                RerunModal.TestRunStatuses.FAILED.getValue() + " status isn't marked as selected after selecting another option");

        CreateTestRunPage createTestRunPage = rerunModal.clickOkButton();
        softAssert.assertEquals(createTestRunPage.getLinkedTestCasesNumber(), expectedCountCasesOnRerun,
                "Count of linked cases isn't equals to expected on create test run page after rerun window");

        TestRunPage testRunPageRerun = createTestRunPage.inputTitle(testRunNameAfterRerun)
                                                        .clickCreateButton();

        List<TestRunSuiteItem> existingSuites = testRunPageRerun.expandAndGetAllTestSuites();
        int countCases = existingSuites.stream()
                                       .mapToInt(TestRunSuiteItem::getCountOfCases)
                                       .sum();

        softAssert.assertEquals(countCases, Integer.parseInt(expectedCountCasesOnRerun), "Count of cases in rerun isn't equals to expected");

        Map<String, List<TestCase>> testCaseMap = new HashMap<>();
        testCaseMap.put(RerunModal.TestRunStatuses.FAILED.getValue(), failedTestCases);
        testCaseMap.put(RerunModal.TestRunStatuses.BLOCKED.getValue(), blockedTestCases);
        testCaseMap.put(RerunModal.TestRunStatuses.SKIPPED.getValue(), skippedTestCases);

        testCaseMap.forEach((status, cases) -> {
            boolean expectedToAppear = !status.equals(RerunModal.TestRunStatuses.SKIPPED.getValue());
            cases.forEach(testCase -> {
                String message = "Test case with status " + status + " isn't found after rerun";
                softAssert.assertEquals(
                        expectedToAppear,
                        testRunPageRerun.isTestCaseAppear(testCase.getTitle()),
                        message
                );
            });
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-2947")
    public void verifyUserIsAbleToSelectTestCasesViaShift() {
        Actions actions = new Actions(getDriver());
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        softAssert.assertTrue(testRunsGridPage.isPageOpened(), "Test runs grid page isn't opened!");

        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        actions.keyDown(Keys.SHIFT).perform();

        testRunPage.getTestSuite(testSuite.getTitle())
                   .getTestCase(failedTestCases.get(1).getTitle())
                   .clickCheckbox();

        testRunPage.getTestSuite(testSuite.getTitle())
                   .getTestCase(blockedTestCases.get(0).getTitle())
                   .clickCheckbox();

        actions.keyUp(Keys.SHIFT).perform();

        List<TestCase> testRunCases = allTestCases;

        testRunCases.removeIf(a -> a.getTitle().equalsIgnoreCase(blockedTestCases.get(1).getTitle()));
        testRunCases.removeIf(a -> a.getTitle().equalsIgnoreCase(failedTestCases.get(0).getTitle()));

        for (TestCase testCase : testRunCases) {
            softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                                             .isTestCaseSelected(testCase.getTitle()), "Test case " + testCase.getTitle() + " isn't selected!");
        }

        softAssert.assertFalse(testRunPage.getTestSuite(testSuite.getTitle())
                                          .isTestCaseSelected(blockedTestCases.get(1).getTitle()), "Test case " +
                blockedTestCases.get(1).getTitle() + " is selected!");

        softAssert.assertFalse(testRunPage.getTestSuite(testSuite.getTitle())
                                          .isTestCaseSelected(failedTestCases.get(0).getTitle()), "Test case " +
                failedTestCases.get(0).getTitle() + " is selected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2948", "ZTP-2957"})
    public void verifyUserIsAbleToCollapseAndExpandTestSuites() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        // Able to open the test case preview
        AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                                                             .getTestCase(skippedTestCases.get(0).getTitle())
                                                             .clickTestCase();
        softAssert.assertTrue(testCaseView.isPresent(3),
                "Test case preview isn't opened");

        testCaseView.getCloseButton().click();

        // Verify visibility after expanding
        List<TestRunSuiteItem> expandedSuiteList = testRunPage.expandAndGetAllTestSuites();
        softAssert.assertFalse(expandedSuiteList.isEmpty(), "List of suites is empty after expanding");

        expandedSuiteList.forEach(suite ->
                suite.getTestCases().forEach(testCase ->
                        softAssert.assertTrue(suite.isTestCaseVisible(testCase.getCaseTitle()),
                                "Test case " + testCase.getCaseTitle() + " isn't visible after expanding!")
                )
        );

        // Verify visibility after collapsing
        List<TestRunSuiteItem> collapsedSuiteList = testRunPage.collapseAndGetAllTestSuites();
        softAssert.assertFalse(collapsedSuiteList.isEmpty(), "List of suites is empty after collapsing");

        collapsedSuiteList.forEach(suite ->
                suite.getTestCases().forEach(testCase ->
                        softAssert.assertFalse(suite.isTestCaseVisible(testCase.getCaseTitle()),
                                "Test case " + testCase.getCaseTitle() + " is visible after collapsing!")
                )
        );

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2949", "ZTP-5493", "ZTP-5944"})
    public void z_verifyUserIsAbleToEditAndDeleteTestCaseOnTestRunPage() {
        SoftAssert softAssert = new SoftAssert();
        String newCaseTitle = "Edit Case " + UUID.randomUUID();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        TestRunCaseItem testCase = testRunPage.expandAndGetAllTestSuites()
                                              .get(0)
                                              .getTestCases()
                                              .get(0);

        EditTestCaseModal editTestCaseModal = testCase.openEditTestCaseModal();
        softAssert.assertTrue(editTestCaseModal.isUIObjectPresent(), "Edit test case modal isn't open!");

        editTestCaseModal.inputTitle(newCaseTitle)
                         .submitModal();
        testCase = testRunPage.expandAndGetAllTestSuites()
                              .get(0)
                              .getTestCases()
                              .get(0);

        // ZTP-2949 User is able to open the 'Edit test case' modal window
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_CASE_EDITED.getDescription(newCaseTitle),
                "Popup message isn't equals to expected after editing test case!");
        softAssert.assertEquals(testCase.getCaseTitle(), newCaseTitle,
                "Test case title isn't equals to expected after editing!");
        testRunPage.waitPopupDisappears();

        int countCasesBeforeDeleting = Integer.parseInt(testRunPage.getTestSuite(testSuite.getTitle())
                                                                   .getCountAllCasesFromLabel());
        DeleteModal deleteModal = testCase.openDeleteModal();
        softAssert.assertTrue(deleteModal.isModalOpened(), "Delete modal isn't opened!");

        deleteModal.clickDeleteButton();

        // ZTP-5493 User is able to delete test case in Test run page
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_CASE_DELETED.getDescription(),
                "Popup message isn't equals to expected after deleting test case!");
        softAssert.assertFalse(testRunPage.getTestSuite(testSuite.getTitle()).isTestCaseExist(newCaseTitle),
                "Test case is still exist after deleting");

        // ZTP-5944 amount of cases in the suite adapts when you delete case
        softAssert.assertEquals(testRunPage.getTestSuite(testSuite.getTitle())
                                           .getCountAllCasesFromLabel(),
                String.valueOf(countCasesBeforeDeleting - 1),
                "Test case count on label isn't equals to expected after deleting test case");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2951", "ZTP-2952", "ZTP-5460"})
    public void verifyUserIsAbleToAssignUserAndStatusToTestCase() {
        WebDriver webDriver = super.getDriver();
        String testRunName = "Case " + UUID.randomUUID();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(webDriver, project);

        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        createTestRunPage.inputTitle(testRunName);

        SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
        selectTestCasesModal.selectTestSuite(testSuite.getTitle());

        TestRunPage testRunPage = selectTestCasesModal.clickDoneButton()
                                                      .clickCreateButton();

        TestRunSuiteItem testRunSuiteItem = testRunPage.getTestSuite(testSuite.getTitle());
        Assert.assertEquals(
                testRunSuiteItem.getSuiteDescription(), testSuite.getDescription(),
                "Suite description on test run page isn't equals to expected"
        );

        TestRunCaseItem testRunCaseItem = testRunSuiteItem.getTestCase(blockedTestCases.get(0).getTitle());

        String displayedUser = StringUtil.getExpectedAuthor(createdUserForAssign);

        testRunCaseItem.selectUserForAssign(displayedUser);
        Assert.assertEquals(
                testRunCaseItem.getAssignedUsername(), displayedUser,
                "Assigned username isn't equals to expected"
        );

        testRunCaseItem.selectStatus(RerunModal.TestRunStatuses.BLOCKED);

        AddExecutionResultModal addExecutionResultModal = new AddExecutionResultModal(webDriver);
        Assert.assertTrue(addExecutionResultModal.isModalOpened(), "Add execution result modal isn't opened!");

        addExecutionResultModal.submitModal();
        super.pause(1);

        Assert.assertEquals(
                testRunCaseItem.getStatus(), RerunModal.TestRunStatuses.BLOCKED.getValue(),
                "Assigned test status isn't equals to expected"
        );
    }

    @Test
    @TestCaseKey("ZTP-5760")
    public void verifyUserCardAppearsOnTestCasePreview() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        testRunPage.getExpandedHeader().hoverUsername();
        UserInfoTooltip userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering username on test run page with expanded header");

        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test run page with expanded header");

        CollapsedTestRunHeader collapsedTestRunHeader = testRunPage.getCollapsedHeader();
        softAssert.assertFalse(userInfoTooltip.isPresent(2),
                "User info tooltip still present after collapsing header");

        collapsedTestRunHeader.hoverUsername();
        userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering username on test run page with collapsed header");

        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test run page with collapsed header");

        AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuite.getTitle())
                                                             .getTestCase(skippedTestCases.get(0).getTitle())
                                                             .clickTestCase();

        PropertiesTab propertiesTab = testCaseView.openPropertiesTab();
        userInfoTooltip = propertiesTab.hoverAuthorLabel();
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering Author on test case preview Properties tab");
        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test case preview Properties tab");

        userInfoTooltip = testCaseView.openExecutionsTab()
                                      .getLastExecution()
                                      .hoverExecutedBy();
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering user on test case preview Executions tab");
        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test case preview Executions tab");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5895", "ZTP-5897"})
    public void userIsAbleToCloseSelectTestCaseAndSelectConfigurationModals() {
        WebDriver webDriver = super.getDriver();
        Actions actions = new Actions(webDriver);

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(webDriver, project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        // ZTP-5895
        for (int i = 0; i < 3; i++) {
            SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
            Assert.assertTrue(selectTestCasesModal.isOpened(), "Select test case modal isn't opened!");

            if (i == 0) {
                selectTestCasesModal.clickCancel();
            } else if (i == 1) {
                selectTestCasesModal.clickClose();
            } else {
                actions.keyDown(Keys.ESCAPE).perform();
                actions.keyUp(Keys.ESCAPE).perform();
            }

            Assert.assertFalse(selectTestCasesModal.isModalOpened(), "Select test case modal is still open after " + i + " iteration closing");
        }

        // ZTP-5897
        for (int i = 0; i < 3; i++) {
            CreateTestRunPage.SelectConfigurationDialog selectConfigurationDialog = createTestRunPage.clickAddConfigurationButton();
            Assert.assertTrue(selectConfigurationDialog.isOpened(), "Select configuration modal isn't opened!");

            if (i == 0) {
                selectConfigurationDialog.clickCancelButton();
            } else if (i == 1) {
                selectConfigurationDialog.clickCloseButton();
            } else {
                actions.keyDown(Keys.ESCAPE).perform();
                actions.keyUp(Keys.ESCAPE).perform();
            }

            Assert.assertFalse(selectConfigurationDialog.isOpened(), "Select configuration modal is still open after " + i + " iteration closing");
        }
    }

    @Test
    @TestCaseKey({"ZTP-3724", "ZTP-5940", "ZTP-4211", "ZTP-4228"})
    public void verifyUserCanSetResultWithAttachmentAndVerifyCountOfTestedCases() {
        File file = new File(IMAGES_ZEB_PNG);
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        createTestRunPage.inputTitle("Test Run " + RandomStringUtils.randomAlphabetic(5));

        SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
        selectTestCasesModal.selectTestSuite(testSuite.getTitle());
        selectTestCasesModal.clickDoneButton();

        TestRunPage testRunPage = createTestRunPage.clickCreateButton();

        TestRunSuiteItem testRunSuite = testRunPage.getTestSuite(testSuite.getTitle());
        testRunSuite.getTestCases()
                    .get(0)
                    .clickCheckbox();

        AddExecutionResultModal executionResultModal = testRunPage.openAddExecutionResultModal();
        softAssert.assertTrue(executionResultModal.isModalOpened(), "Add execution result modal isn't opened");

        executionResultModal.addAttachment(IMAGES_ZEB_PNG)
                            .selectExecutionType(AddExecutionResultModal.ExecutionTypesEnum.MANUAL)
                            .submitModal();

        AbstractTestCasePreview<?> testCaseView = testRunSuite.getTestCases()
                                                              .get(0)
                                                              .clickTestCase();

        TestCaseModalView modal = testCaseView.toModalView();
        ExecutionsTab executionsTab = modal.openExecutionsTab();
        ExecutionItem executionItem = executionsTab.getExecutions()
                                                   .get(0);
        pause(1);
        AttachmentItem attachmentItem = executionItem.getAttachments()
                                                     .get(0);

        // ZTP-3724 User can set attachment to test case result
        File act = attachmentItem.getImgFile();
        softAssert.assertEquals(act.length(), file.length(), "File length is not as for added file");

        softAssert.assertEquals(executionItem.getExecutionTypeValue(), AddExecutionResultModal.ExecutionTypesEnum.MANUAL.getValue(),
                "Execution type value after selecting 'Manual' isn't equals to expected"); // ZTP-4211
        ComponentUtil.pressEscape(getDriver());

        testRunSuite = testRunPage.getTestSuite(testSuite.getTitle());
        testRunSuite.getTestCases()
                    .get(0)
                    .clickCheckbox();

        executionResultModal = testRunPage.openAddExecutionResultModal();
        executionResultModal.selectExecutionType(AddExecutionResultModal.ExecutionTypesEnum.AUTOMATED)
                            .submitModal();
        testCaseView = testRunSuite.getTestCases()
                                   .get(0)
                                   .clickTestCase();

        modal = testCaseView.toModalView();
        executionsTab = modal.openExecutionsTab();
        executionItem = executionsTab.getExecutions()
                                     .get(0);

        // ZTP-4211 save the result of the execution with any selected type
        softAssert.assertEquals(executionItem.getExecutionTypeValue(), "Automation",
                "Execution type value after selecting 'Manual' isn't equals to expected");

        // ZTP-5940 User is able to see the amount of tested cases in suite
        ComponentUtil.pressEscape(getDriver());
        softAssert.assertEquals(testRunPage.getTestSuite(testSuite.getTitle())
                                           .getCountTestedCases(), String.valueOf(1), "Count of tested cases isn't equals to expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5941", "ZTP-5942"})
    public void verifyTestCaseStatusesWillAffectTheNumberOfTestedCases() {
        int countCreatedCases = 6;

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);

        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRunForDifferentStatuses.getTitle());

        TestRunSuiteItem testRunSuite = testRunPage.getTestSuite(testSuiteForDifferentStatuses.getTitle());
        softAssert.assertEquals(testRunSuite.getCountAllCasesFromLabel(), String.valueOf(countCreatedCases),
                "Test case count from label isn't equals to expected");
        softAssert.assertEquals(testRunSuite.getCountTestedCases(), String.valueOf(0),
                "Count of tested cases isn't equals to expected");

        // ZTP-5942 statuses that NOT affect number tested cases
        String[] statusesNotAffectTestedCountCases = {"Blocked", "Retest"};
        for (int i = 0; i < statusesNotAffectTestedCountCases.length; i++) {
            String status = statusesNotAffectTestedCountCases[i];

            tcmService.addTestRunResults(project.getId(), testRunForDifferentStatuses,
                    Collections.singletonList(createdTestCasesForDifferentStatuses.get(i)), testRunSettings, status);
            testRunSuite = testRunPage.refreshPage()
                                      .getTestSuite(testSuiteForDifferentStatuses.getTitle());

            softAssert.assertEquals(testRunSuite.getCountAllCasesFromLabel(), String.valueOf(countCreatedCases),
                    "Test case count from label isn't equals to expected after status " + status);
            softAssert.assertEquals(testRunSuite.getCountTestedCases(), "0",
                    "Count of tested cases isn't equals to expected after status " + status);
        }

        // ZTP-5941 statuses that affect number tested cases
        String[] statusesAffectTestedCountCases = {"Passed", "Failed", "Skipped", "Invalid"};
        for (int i = 0; i < statusesAffectTestedCountCases.length; i++) {
            String status = statusesAffectTestedCountCases[i];

            tcmService.addTestRunResults(project.getId(), testRunForDifferentStatuses,
                    Collections.singletonList(createdTestCasesForDifferentStatuses.get(i + 2)), testRunSettings, status);
            testRunSuite = testRunPage.refreshPage()
                                      .getTestSuite(testSuiteForDifferentStatuses.getTitle());

            softAssert.assertEquals(testRunSuite.getCountAllCasesFromLabel(), String.valueOf(countCreatedCases),
                    "Test case count from label isn't equals to expected after status " + status);
            softAssert.assertEquals(testRunSuite.getCountTestedCases(), String.valueOf(i + 1),
                    "Count of tested cases isn't equals to expected after status " + status);
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5943", "ZTP-5945"})
    public void verifyAmountOfTestedCasesAdaptsWhenApplyFilter() {
        WebDriver webDriver = super.getDriver();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(webDriver, project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        int testCasesNumber = Integer.parseInt(
                testRunPage.getTestSuite(testSuite.getTitle())
                           .getCountAllCasesFromLabel()
        );

        Dropdown filterList = testRunPage.openFilterList();
        filterList.findItem("Result Status").click();
        filterList.findItem("Skipped").click();
        ComponentUtil.pressEscape(webDriver);

        Assert.assertEquals(
                testRunPage.getTestSuite(testSuite.getTitle()).getCountAllCasesFromLabel(),
                String.valueOf(skippedTestCases.size()),
                "Test case count on label isn't equals to expected after applying filter"
        );

        TestCase newTestCase = tcmService.createTestCase(project.getId(), testSuite.getId());

        testRunPage.openMenuAndSelectOption(TestRunPage.TestRunPageMenuActions.EDIT);
        EditTestRunPage editTestRunPage = new EditTestRunPage(webDriver);

        SelectTestCasesModal selectTestCasesModal = editTestRunPage.clickChangeLinkedTestCasesButton();
        selectTestCasesModal.clickOnTestSuite(testSuite.getTitle())
                            .selectTestCase(newTestCase.getTitle());
        selectTestCasesModal.clickDoneButton();

        testRunPage = editTestRunPage.clickSaveButton();
        Assert.assertEquals(
                testRunPage.getPopUp(), "Test run was successfully updated",
                "Pop up text isn't equals to expected after adding test cases to test run"
        );
        Assert.assertEquals(
                testRunPage.getTestSuite(testSuite.getTitle()).getCountAllCasesFromLabel(),
                String.valueOf(testCasesNumber + 1),
                "Count of cases after adding test case isn't equals to expected"
        );
    }

    @Test
    @TestCaseKey({"ZTP-4209", "ZTP-4213", "ZTP-4210"})
    public void verifyTypeFieldOnAddExecutionModal() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRunForDifferentStatuses.getTitle());

        TestRunCaseItem testRunCase = testRunPage.getTestSuite(testSuiteForDifferentStatuses.getTitle())
                                                 .getTestCases()
                                                 .get(0);
        testRunCase.clickCheckbox();

        AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModal();
        softAssert.assertEquals(addExecutionResultModal.getSelectedExecutionType(),
                AddExecutionResultModal.ExecutionTypesEnum.MANUAL.getValue(),
                "Selected by default automation type isn't 'Manual'"); // ZTP-4209 default type is 'Manual'

        ListBoxMenu automationStateOptions = addExecutionResultModal.getListExecutionTypes();

        // ZTP-4213 clicking on a type field a dropdown opens with 2 options 'Manual' and 'Automated'
        softAssert.assertEquals(automationStateOptions.getCount(), 2,
                "Options of automation type more than 2");
        softAssert.assertTrue(automationStateOptions.getItems()
                                                    .stream()
                                                    .anyMatch(item -> item.getText()
                                                                          .equals(AddExecutionResultModal.ExecutionTypesEnum.MANUAL.getValue())),
                "Element 'Manual' isn't found in the list options");
        softAssert.assertTrue(automationStateOptions.getItems()
                                                    .stream()
                                                    .anyMatch(item -> item.getText()
                                                                          .equals(AddExecutionResultModal.ExecutionTypesEnum.AUTOMATED.getValue())),
                "Element 'Automated' isn't found in the list options");

        // ZTP-4210 able to change 'Type' from 'Manual' to 'Automated' and vice versa
        automationStateOptions.clickItem(AddExecutionResultModal.ExecutionTypesEnum.AUTOMATED.getValue());
        softAssert.assertEquals(addExecutionResultModal.getSelectedExecutionType(),
                AddExecutionResultModal.ExecutionTypesEnum.AUTOMATED.getValue(),
                "Automation isn't switched from 'Manual' to 'Automated'");

        automationStateOptions = addExecutionResultModal.getListExecutionTypes();
        automationStateOptions.clickItem(AddExecutionResultModal.ExecutionTypesEnum.MANUAL.getValue());
        softAssert.assertEquals(addExecutionResultModal.getSelectedExecutionType(),
                AddExecutionResultModal.ExecutionTypesEnum.MANUAL.getValue(),
                "Automation isn't switched from 'Automated' to 'Manual'");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4212")
    public void verifyUserCanSaveDifferentExecutionTypesWithDifferentStatuses() {
        String selectedStatus;
        int countOfExistingResults = 6;

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton()
                                                              .inputTitle("Test Run " + RandomStringUtils.randomAlphabetic(5));

        SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
        selectTestCasesModal.selectTestSuite(testSuiteForDifferentStatuses.getTitle());

        createTestRunPage = selectTestCasesModal.clickDoneButton();
        TestRunPage testRunPage = createTestRunPage.clickCreateButton();

        for (int i = 0; i < countOfExistingResults * 2; i++) {
            TestRunCaseItem testRunCase = testRunPage.getTestSuite(testSuiteForDifferentStatuses.getTitle())
                                                     .getTestCases()
                                                     .get(1);
            testRunCase.clickCheckbox();

            AddExecutionResultModal addExecutionResultModal = testRunPage.openAddExecutionResultModal();
            ListBoxMenu listBoxMenu = addExecutionResultModal.clickResultStatuses();
            List<Element> resultsList = listBoxMenu.getItems();

            int index = (i / 2) % resultsList.size();
            log.info("Current selected status - {}", resultsList.get(index).getText());

            selectedStatus = resultsList.get(index).getText();
            resultsList.get(index)
                       .click();

            String expectedType;
            if (i % 2 == 0) {
                addExecutionResultModal.selectExecutionType(AddExecutionResultModal.ExecutionTypesEnum.MANUAL);
                expectedType = "Manual";
            } else {
                addExecutionResultModal.selectExecutionType(AddExecutionResultModal.ExecutionTypesEnum.AUTOMATED);
                expectedType = "Automation";
            }
            log.info("Current selected automation type - {}", expectedType);

            addExecutionResultModal.submitModal();

            AbstractTestCasePreview<?> testCaseView = testRunPage.getTestSuite(testSuiteForDifferentStatuses.getTitle())
                                                                 .getTestCases()
                                                                 .get(1)
                                                                 .clickTestCase();

            TestCaseModalView modal = testCaseView.toModalView();
            ExecutionsTab executionsTab = modal.openExecutionsTab();
            ExecutionItem executionItem = executionsTab.getExecutions()
                                                       .get(0);

            Assert.assertEquals(
                    executionItem.getExecutionTypeValue(), expectedType,
                    "Execution type isn't equals to expected"
            );
            Assert.assertEquals(
                    executionItem.getExecutionStatus(), selectedStatus,
                    "Execution status isn't equals to expected " + selectedStatus
            );

            modal.getCloseButton().click();
        }
    }

}
