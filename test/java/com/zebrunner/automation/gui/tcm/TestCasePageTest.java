package com.zebrunner.automation.gui.tcm;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.tcm.testcase.AbstractTestCasePreview;
import com.zebrunner.automation.gui.tcm.testcase.CloneTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.DeleteTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseModalView;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseSideBarView;
import com.zebrunner.automation.util.KeyCombinations;
import com.zebrunner.automation.legacy.TooltipEnum;
import com.zebrunner.automation.gui.common.EmptyPlaceholder;
import com.zebrunner.automation.gui.common.ZbrSearch;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.tcm.repository.BaseRepositoryItem;
import com.zebrunner.automation.gui.tcm.repository.RepositoryList;
import com.zebrunner.automation.gui.tcm.testcase.ConfirmCancelOfTestCaseCreationModal;
import com.zebrunner.automation.gui.tcm.testcase.CreateTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.RepositoryCaseItem;
import com.zebrunner.automation.gui.tcm.testcase.ModalGeneralTab;
import com.zebrunner.automation.gui.tcm.testcase.SidebarGeneralTab;
import com.zebrunner.automation.gui.tcm.testcase.AttachmentItem;
import com.zebrunner.automation.gui.tcm.testcase.AttachmentsTab;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionItem;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionsTab;
import com.zebrunner.automation.gui.tcm.testcase.PropertiesTab;
import com.zebrunner.automation.gui.tcm.testsuite.BaseSuiteItem;
import com.zebrunner.automation.gui.tcm.testsuite.CreateOrEditSuiteModal;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.GeneralField;
import com.zebrunner.automation.api.tcm.domain.TestCaseFieldsLayout;
import com.zebrunner.automation.api.tcm.domain.TestCaseFieldsTab;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import com.zebrunner.automation.api.tcm.service.TcmServiceImpl;
import com.zebrunner.automation.util.PageUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Maintainer("obabich")
public class TestCasePageTest extends TcmLogInBase {

    private final static String IMAGES_ZEB_PNG = "src/test/resources/images/zeb.png";
    private Project project;

    private TestSuite testSuite_1;
    private TestSuite subForSuite_1;
    private TestCase testCaseForSuite_1;
    private TestSuite testSuite_2;
    private TestCase testCaseForSuite_2;

    private TestRun testRun;
    private TestRunSettings testRunSettings;


    @BeforeClass
    public void getProjectKey() {
        project = super.getCreatedProject();
//        project = Project.builder()
//                .id(1L)
//                .key("DEF")
//                .build();
        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        testRunService.finishTestRun(launch.getId()); // to avoid the appearance of getting-started page

        testSuite_1 = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        subForSuite_1 = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName(testSuite_1.getId()));
        testCaseForSuite_1 = tcmService.createTestCase(project.getId(), testSuite_1.getId());

        testSuite_2 = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        testCaseForSuite_2 = tcmService.createTestCase(project.getId(), testSuite_2.getId());

        testRunSettings = tcmService.getTestRunSettings(project.getId());
        testRun = tcmService.createTestRun(project.getId(), Collections.singletonList(testCaseForSuite_1), TestRun.createWithRandomName());

    }

    @AfterClass(alwaysRun = true)
    public void deleteSuitesWithCases() {
        tcmService.deleteTestSuite(project.getId(), testSuite_1.getId());
        tcmService.deleteTestSuite(project.getId(), testSuite_2.getId());
    }

    @Test
    @TestCaseKey({"ZTP-2781", "ZTP-4849", "ZTP-4854", "ZTP-4879"})
    public void testCasePageNavigation() {
        WebDriver webDriver = super.getDriver();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());

        //ZTP-2781
        TestCasesPage testCasesPage = automationLaunchesPage.getNavigationMenu().toTestCasesPage();
        testCasesPage.assertPageOpened(7);

        //ZTP-4849
        testCasesPage.getBreadcrumbs().clickBreadcrumb(project.getKey());
        automationLaunchesPage.assertPageOpened(7);

        testCasesPage = TestCasesPage.openPageDirectly(webDriver, project.getKey());
        //ZTP-4854
        Assert.assertTrue(testCasesPage.isLeftSuiteTreeVisible(), "Test suite tree should be visible by default!");

        testCasesPage.collapseSuitePanel();
        Assert.assertFalse(testCasesPage.isLeftSuiteTreeVisible(), "Test suite tree shouldn't be visible after collapsing!");

        //ZTP-4854
        testCasesPage.expandSuitePanel();
        Assert.assertTrue(testCasesPage.isLeftSuiteTreeVisible(), "Test suite tree should be visible after expanding!");

        testCasesPage.selectSuiteFromSuiteTree(testSuite_1.getTitle());

        //ZTP-4879
        BaseRepositoryItem suite = testCasesPage.getRepository().getSuite(testSuite_1.getTitle());
        suite.expandCasesIfNeeded();

        List<RepositoryCaseItem> suiteCases = suite.getDirectSuiteTestCases(false);
        Assert.assertFalse(
                suiteCases.isEmpty(),
                "User should see test cases for suite " + testSuite_1.getTitle() + " when it was expanded!"
        );

        suite.collapseCasesIfNeeded();
        Assert.assertTrue(
                suite.getDirectSuiteTestCases(false).isEmpty(),
                "User shouldn't see test cases for suite " + testSuite_1.getTitle() + " when it was collapsed!"
        );
    }

    @Test
    @TestCaseKey("ZTP-4867")
    public void repositoryViewsTypesVerification() {
        String suite_1 = testSuite_1.getTitle();
        String sub_suite_1 = subForSuite_1.getTitle();
        String case_from_suite_1 = testCaseForSuite_1.getTitle();

        String suite_2 = testSuite_2.getTitle();
        String case_from_suite_2 = testCaseForSuite_2.getTitle();

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.selectSuiteFromSuiteTree(suite_1);

        SuiteViewListbox listbox = testCasesPage.clickSwitchRepoView();

        List<SuiteViewListbox.SuiteViewTypes> types = List.of(SuiteViewListbox.SuiteViewTypes.values());//ZTP-4867

        SuiteViewListbox expectedTypes = listbox;
        types.forEach(t ->
                expectedTypes.getOptionalItem(t)
                             .ifPresentOrElse(
                                     obtainedType ->
                                             softAssert.assertEquals(obtainedType.getItemDescription(), t.getViewDescription(),
                                                     "Type description is not as expected!"),
                                     () -> softAssert.fail("Unable to find view type: " + t)
                             )
        );

        listbox.clickItem(SuiteViewListbox.SuiteViewTypes.TREE_VIEW);
        testCasesPage.expandCases();
        // verifying all suites with cases in list

        softAssert.assertNotNull(testCasesPage.getTestSuite(suite_1, false),
                "Suite(1) with name " + suite_1 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.TREE_VIEW);
        softAssert.assertNotNull(testCasesPage.getTestSuite(sub_suite_1, false),
                "Sub-suite(from 1st suite) with name" + sub_suite_1 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.TREE_VIEW);
        softAssert.assertNotNull(testCasesPage.getTestCaseWithSwipe(case_from_suite_1),
                "Case(from 1st suite) with name" + case_from_suite_1 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.TREE_VIEW);

        softAssert.assertNotNull(testCasesPage.getTestSuite(suite_2, false),
                "Suite(2) with name" + suite_2 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.TREE_VIEW);
        softAssert.assertNotNull(testCasesPage.getTestCaseWithSwipe(case_from_suite_2),
                "Case(from 2d suite) with name " + case_from_suite_2 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.TREE_VIEW);

        listbox = testCasesPage.clickSwitchRepoView();
        listbox.clickItem(SuiteViewListbox.SuiteViewTypes.SUITE_VIEW);
        //verifying only 1 suite cases in list(without sub-suites)

        softAssert.assertNotNull(testCasesPage.getTestSuite(suite_1, false),
                "Suite(1) with name " + suite_1 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.SUITE_VIEW);
        softAssert.assertNull(testCasesPage.getTestSuite(sub_suite_1, 3),
                "Sub-suite(from 1st suite) with name " + sub_suite_1 + " shouldn't be in list when " +
                        SuiteViewListbox.SuiteViewTypes.SUITE_VIEW);
        softAssert.assertNotNull(testCasesPage.getTestCaseWithSwipe(case_from_suite_1),
                "Case(from 1st suite) with name " + case_from_suite_1 + " should be in list when " +
                        SuiteViewListbox.SuiteViewTypes.SUITE_VIEW);

        softAssert.assertNull(testCasesPage.getTestSuite(case_from_suite_2, 3),
                "Suite(2) with name " + case_from_suite_2 + " shouldn't be in list when " +
                        SuiteViewListbox.SuiteViewTypes.SUITE_VIEW);

        listbox = testCasesPage.clickSwitchRepoView();
        listbox.clickItem(SuiteViewListbox.SuiteViewTypes.SUITE_WITH_SUB_SUITES);
        testCasesPage.expandCases();
        //verifying only 1 suite with sub-suites cases in list

        softAssert.assertNotNull(testCasesPage.getTestSuite(suite_1, false),
                "Suite(1) with name " + suite_1 + " should be in list when "
                        + SuiteViewListbox.SuiteViewTypes.SUITE_WITH_SUB_SUITES);
        softAssert.assertNotNull(testCasesPage.getTestSuite(sub_suite_1, false),
                "Sub-suite(from 1st suite) with name " + sub_suite_1 + " should be in list when "
                        + SuiteViewListbox.SuiteViewTypes.SUITE_WITH_SUB_SUITES);
        softAssert.assertNotNull(testCasesPage.getTestCaseWithSwipe(case_from_suite_1),
                "Case(from 1st suite) with name " + case_from_suite_1 + " should be in list when "
                        + SuiteViewListbox.SuiteViewTypes.SUITE_WITH_SUB_SUITES);

        softAssert.assertNull(testCasesPage.getTestSuite(suite_2, 3),
                "Suite(2) with name " + suite_2 + " shouldn't be in list when "
                        + SuiteViewListbox.SuiteViewTypes.SUITE_WITH_SUB_SUITES);
        softAssert.assertNull(testCasesPage.getTestCaseWithSwipe(case_from_suite_2, 3),
                "Case(from 2d suite) with name " + case_from_suite_2 + " shouldn't be in list when "
                        + SuiteViewListbox.SuiteViewTypes.SUITE_WITH_SUB_SUITES);
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4850")
    public void createTestCaseViaCreateButton() {
        String caseTitle = "Case " + UUID.randomUUID();
        String suiteName = testSuite_1.getTitle();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.createTestCase(caseTitle, suiteName);

        testCasesPage.getTestSuiteTree().selectSuite(suiteName);
        testCasesPage.expandCases();

        Assert.assertTrue(testCasesPage.isTestCasePresent(caseTitle),
                "Case with name " + caseTitle + " was not found!");
    }

    @Test
    @TestCaseKey("ZTP-4903")
    public void createQuickTestCase() {
        String caseTitle = "Case(quick) " + UUID.randomUUID();
        String suiteName = testSuite_1.getTitle();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.getTestSuite(suiteName)
                     .clickCreateQuickTestCase(caseTitle);

        BaseRepositoryItem suite = testCasesPage.getTestSuite(suiteName);

        boolean isCasePresent = suite.getDirectSuiteTestCases().stream()
                                     .anyMatch(testCase -> testCase.getTestCaseTitleValue()
                                                                   .equalsIgnoreCase(caseTitle));

        Assert.assertTrue(isCasePresent,
                "Case with name " + caseTitle + " was not found!");
    }

    @Test
    @TestCaseKey("ZTP-4870")
    public void userCanCancelTestCaseCreation() {
        String caseTitle = "Case " + UUID.randomUUID();
        String suiteName = testSuite_1.getTitle();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        SoftAssert softAssert = new SoftAssert();
        CreateTestCaseModal createTestCaseModal = testCasesPage.clickCreateTestCaseBtn();
        softAssert.assertTrue(createTestCaseModal.isModalOpened(),
                "Modal should be opened after click on 'Create' case button!");

        createTestCaseModal.clickCancel();

        softAssert.assertFalse(new ConfirmCancelOfTestCaseCreationModal(getDriver()).isVisible(3),
                "Confirmation modal shouldn't be visible after canceling not filled 'Create test case' modal!");
        softAssert.assertFalse(createTestCaseModal.isVisible(3),
                "Modal shouldn't be opened after click 'Cancel' button!");


        for (int i = 0; i < 3; i++) {
            createTestCaseModal = testCasesPage.clickCreateTestCaseBtn();
            createTestCaseModal.inputTitle(caseTitle)
                               .selectParentSuite(suiteName);

            softAssert.assertTrue(createTestCaseModal.isModalOpened(), "Modal should be opened! (i=" + i + ")");
            softAssert.assertEquals(createTestCaseModal.getModalTitleText(), CreateTestCaseModal.MODAL_TITLE,
                    "Modal title is not as excepted!(i=" + i + ")");

            if (i == 0) {
                createTestCaseModal.clickCancel();
            } else if (i == 1) {
                createTestCaseModal.clickClose();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            ConfirmCancelOfTestCaseCreationModal confirmModal = new ConfirmCancelOfTestCaseCreationModal(getDriver());
            softAssert.assertEquals(confirmModal.getTitleModal(), ConfirmCancelOfTestCaseCreationModal.MODAL_TITLE,
                    "Confirmation modal title is not as expected!(i=" + i + ")");

            confirmModal.clickConfirm();

            softAssert.assertFalse(confirmModal.isVisible(),
                    "Confirmation modal shouldn't be visible after click 'Confirm' button!(i=" + i + ")");

            softAssert.assertFalse(createTestCaseModal.isVisible(3),
                    "Create test case modal shouldn't be visible after click 'Confirm' button!(i=" + i + ")");
        }

        BaseRepositoryItem suite = testCasesPage.getTestSuite(suiteName);

        boolean isCasePresent = suite.getDirectSuiteTestCases().stream()
                                     .anyMatch(testCase -> testCase.getTestCaseTitleValue()
                                                                   .equalsIgnoreCase(caseTitle));

        Assert.assertFalse(isCasePresent,
                "Case with name " + caseTitle + " shouldn't be in list!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4871")
    public void userIsAbleToDeleteCreatedTestCase() {
        SoftAssert softAssert = new SoftAssert();
        TestCase testCaseForRemoving = tcmService.createTestCase(project.getId(), testSuite_1.getId());

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCaseForRemoving.getTitle());

        Assert.assertTrue(caseItem.isPresent(3), "Created test case with name "
                + testCaseForRemoving.getTitle() + " should be in repository list!");

        caseItem.hover();
        softAssert.assertEquals(caseItem.getBackgroundColor(), RepositoryCaseItem.BACKGROUND_HEX_COLOR_ON_HOVER,
                "Background color is not as expected after hovering element!");

        DeleteTestCaseModal deleteTestCaseModal = caseItem.clickDelete();

        softAssert.assertTrue(deleteTestCaseModal.isModalOpened(), "'Delete test case modal' should be opened!");
        softAssert.assertEquals(deleteTestCaseModal.getHeader()
                                                   .getTitleText(), DeleteTestCaseModal.MODAL_TITLE, "Modal title is not as expected!");
        softAssert.assertEquals(deleteTestCaseModal.getModalContentText(),
                deleteTestCaseModal.getExpectedModalContentText(testCaseForRemoving.getTitle()), "Modal content is not as expected!");

        deleteTestCaseModal.clickDelete();

        RepositoryCaseItem caseItemAfterDeletion = testCasesPage.findTestCase(testCaseForRemoving.getTitle());

        Assert.assertFalse(caseItemAfterDeletion.isPresent(3), "Deleted test case with name "
                + testCaseForRemoving.getTitle() + " shouldn't be in repository list after removing!");

        softAssert.assertAll();

    }

    @Test
    @TestCaseKey("ZTP-4872")
    public void userIsAbleToCancelTestCaseRemoving() {
        SoftAssert softAssert = new SoftAssert();
        TestCase testCaseForRemoving = tcmService.createTestCase(project.getId(), testSuite_1.getId());

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCaseForRemoving.getTitle());

        Assert.assertTrue(caseItem.isPresent(3), "Created test case with name "
                + testCaseForRemoving.getTitle() + " should be in repository list!");


        for (int i = 0; i < 3; i++) {
            DeleteTestCaseModal deleteTestCaseModal = caseItem.clickDelete();
            softAssert.assertTrue(deleteTestCaseModal.isModalOpened(), "Modal should be opened! (i=" + i + ")");

            if (i == 0) {
                deleteTestCaseModal.clickCancel();
            } else if (i == 1) {
                deleteTestCaseModal.clickClose();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            softAssert.assertTrue(caseItem.isPresent(3), "Created test case with name "
                    + testCaseForRemoving.getTitle() + " should be in repository list!(i=" + i + ")");
        }
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4877")
    public void userIsAbleToCloneTestCase() {
        SoftAssert softAssert = new SoftAssert();
        TestCase testCaseForCloning = tcmService.createTestCase(project.getId(), testSuite_2.getId());
        String newTitle = "Cloned " + UUID.randomUUID() + " from suite " + testSuite_2.getTitle();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem caseForClone = testCasesPage.findTestCase(testCaseForCloning.getTitle());

        Assert.assertTrue(caseForClone.isPresent(3), "Created test case with name "
                + testCaseForCloning.getTitle() + " should be in repository list!");

        caseForClone.hover();
        softAssert.assertEquals(caseForClone.getBackgroundColor(), RepositoryCaseItem.BACKGROUND_HEX_COLOR_ON_HOVER,
                "Background color is not as expected after hovering element!");

        CloneTestCaseModal cloneTestCaseModal = caseForClone.clickClone();
        softAssert.assertEquals(cloneTestCaseModal.getModalTitleText(), CloneTestCaseModal.MODAL_TITLE, "Modal title is wrong!");
        cloneTestCaseModal
                .selectParentSuite(testSuite_1.getTitle())
                .inputTitle(newTitle)
                .clickClone();

        log.info("Wait until 'Clone test case' modal disappears...");
        cloneTestCaseModal.waitUntilElementDisappear(15);
        log.info("'Clone test case' modal disappears...");

        List<RepositoryCaseItem> directCases = testCasesPage.getTestSuite(testSuite_1.getTitle())
                                                            .getDirectSuiteTestCases();

        Optional<RepositoryCaseItem> clonedCase =
                directCases.stream()
                           .filter(c -> c.getTestCaseTitleValue().equals(newTitle))
                           .findFirst();

        if (clonedCase.isPresent()) {
            softAssert.assertNotEquals(clonedCase.get().getCaseKeyValue(), testCaseForCloning.getKey(),
                    "Test case keys shouldn't be the same after cloning!");
        } else {
            softAssert.fail("Cloned test case should be in list of cases for suite " + testSuite_1.getTitle());
        }

        caseForClone = testCasesPage.findTestCase(testCaseForCloning.getTitle());
        softAssert.assertTrue(caseForClone.isPresent(3), "Firs case should be in list after cloning too!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4878")
    public void userIsAbleToCancelTestCaseCloning() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCaseForCloning = tcmService.createTestCase(project.getId(), testSuite_1.getId());
        String newTitle = "Cloned " + UUID.randomUUID() + " from suite " + testSuite_1.getTitle();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        for (int i = 0; i < 3; i++) {
            RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCaseForCloning.getTitle());

            Assert.assertTrue(caseItem.isPresent(3), "Created test case with name "
                    + testCaseForCloning.getTitle() + " should be in repository list!");

            CloneTestCaseModal cloneTestCaseModal = caseItem.clickClone();
            softAssert.assertTrue(cloneTestCaseModal.isModalOpened(), "Modal should be opened! (i=" + i + ")");
            cloneTestCaseModal
                    .inputTitle(newTitle)
                    .selectParentSuite(testSuite_2.getTitle());

            if (i == 0) {
                cloneTestCaseModal.clickCancel();
            } else if (i == 1) {
                cloneTestCaseModal.clickClose();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

        }
        RepositoryCaseItem clonedCase = testCasesPage.findTestCase(newTitle);
        softAssert.assertFalse(clonedCase.isPresent(3), "Cloned test case with name "
                + newTitle + " shouldn't be in repository list!");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2788", "ZTP-2798"})
    public void generalTabVerification() {
        SoftAssert softAssert = new SoftAssert();

        TestCase.Step step = TestCase.Step.regular(TestCaseStep.withRandomActionAndExpectedResult());

        TestCase testCase = new TestCase();
        testCase.setTitle("Case " + UUID.randomUUID());
        testCase.setTestSuiteId(testSuite_1.getId());
        testCase.addStep(step);

        TestCase createdTestCase = tcmService.createTestCase(project.getId(), testSuite_1.getId(), testCase);

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem createdUiCase = testCasesPage.findTestCase(createdTestCase.getTitle());

        Assert.assertTrue(createdUiCase.isPresent(3), "Created test case with name "
                + createdUiCase.getTitle() + " should be in repository list!");

        // ZTP-2788 Verify that 'General' tab is present and available for user
        AbstractTestCasePreview<?> testCaseView = createdUiCase.clickTestCase();

        TestCaseSideBarView sideBarView = testCaseView.toSideBarView();
        SidebarGeneralTab sidebarGeneralTab = sideBarView.openGeneralTab();

        // ZTP-2798 Verify that 'General' tab contains those fields which are added in Test case field for 'General' tab
        List<String> expectedFields = getExpectedTabNames();
        List<String> actualSideBarFields = sidebarGeneralTab.getExistingFieldNames();

        expectedFields.forEach(expectedField ->
                softAssert.assertTrue(actualSideBarFields.contains(expectedField),
                        "Sidebar field names don't contain name " + expectedField));

        TestCaseModalView modal = sideBarView.toModalView();
        ModalGeneralTab modalGeneralTab = modal.openGeneralTab();

        List<String> actualModalFields = modalGeneralTab.getExistingFieldNames();

        expectedFields.forEach(expectedField ->
                softAssert.assertTrue(actualModalFields.contains(expectedField),
                        "Modal field names don't contain name " + expectedField));

        softAssert.assertAll();

    }

    @Test
    @SneakyThrows
    @TestCaseKey({"ZTP-2787", "ZTP-2799"})
    public void propertiesTabVerification() {
        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem createdUiCase = testCasesPage.findTestCase(testCaseForSuite_1.getTitle());

        Assert.assertTrue(createdUiCase.isPresent(3), "Created test case with name "
                + createdUiCase.getTitle() + " should be in repository list!");

        // ZTP-2787 Verify that 'Properties' tab is present and available for user
        AbstractTestCasePreview<?> testCaseView = createdUiCase.clickTestCase();

        TestCaseSideBarView sideBarView = testCaseView.toSideBarView();
        PropertiesTab sidebarPropertiesTab = sideBarView.openPropertiesTab();

        // ZTP-2799 Verify that 'Properties' tab contains Author, Created on, Priority, Automation State, Deprecated, Draft
        List<String> expectedFields = getExpectedPropertyNames();

        expectedFields.forEach(expectedField ->
                softAssert.assertTrue(sidebarPropertiesTab.isTabPresent(expectedField),
                        "Sidebar field names don't contain name " + expectedField));

        TestCaseModalView modal = sideBarView.toModalView();
        PropertiesTab modalPropertiesTab = modal.openPropertiesTab();

        expectedFields.forEach(expectedField ->
                softAssert.assertTrue(modalPropertiesTab.isTabPresent(expectedField),
                        "Modal field names don't contain name " + expectedField));

        softAssert.assertAll();

    }

    @Test
    @TestCaseKey({"2789"})
    public void addTestCaseWithAttachmentAndVerifyAttachmentsTab() {
        SoftAssert softAssert = new SoftAssert();

        String caseName = "Case with attachments № ".concat(RandomStringUtils.randomNumeric(5));
        File file = new File(IMAGES_ZEB_PNG);

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        CreateTestCaseModal createTestCaseModal = testCasesPage.clickCreateTestCaseBtn();
        createTestCaseModal
                .inputTitle(caseName)
                .selectParentSuite(testSuite_1.getTitle())
                .addAttachment(IMAGES_ZEB_PNG)
                .submitModal();

        RepositoryCaseItem createdUiCase = testCasesPage.findTestCase(caseName);

        Assert.assertTrue(createdUiCase.isPresent(3), "Created test case with name "
                + createdUiCase.getTitle() + " should be in repository list!");

        // ZTP-2789 Verify that 'Attachments' is present and available for user
        AbstractTestCasePreview<?> testCaseView = createdUiCase.clickTestCase();

        TestCaseSideBarView sideBarView = testCaseView.toSideBarView();
        AttachmentsTab attachmentsTab = sideBarView.openAttachmentsTab();

        AttachmentItem attachmentItem = attachmentsTab.getAttachment(file.getName());
        File act = attachmentItem.getImgFile();

        softAssert.assertEquals(act.length(), file.length(), "File length is not as for added file(for sidebar!)");

        TestCaseModalView modal = sideBarView.toModalView();
        attachmentsTab = modal.openAttachmentsTab();

        attachmentItem = attachmentsTab.getAttachment(file.getName());
        act = attachmentItem.getImgFile();

        softAssert.assertEquals(act.length(), file.length(), "File length is not as for added file(for modal view!)");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4902")
    public void executionTabVerificationTest() {
        SoftAssert softAssert = new SoftAssert();

        tcmService.addTestRunResults(project.getId(), testRun, testCaseForSuite_1, testRunSettings, "Failed");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem createdUiCase = testCasesPage.findTestCase(testCaseForSuite_1.getTitle());

        Assert.assertTrue(createdUiCase.isPresent(3), "Created test case with name "
                + testCaseForSuite_1.getTitle() + " should be in repository list!");

        // ZTP-4902 Verify that 'Executions' tab is present
        AbstractTestCasePreview<?> testCaseView = createdUiCase.clickTestCase();

        TestCaseSideBarView sideBarView = testCaseView.toSideBarView();
        ExecutionsTab executionsTab = sideBarView.openExecutionsTab();
        List<ExecutionItem> executions = executionsTab.getExecutions();

        softAssert.assertFalse(executions.isEmpty(), "Executions list shouldn't be empty!(for side bar)");

        TestCaseModalView modal = sideBarView.toModalView();
        executionsTab = modal.openExecutionsTab();

        softAssert.assertFalse(executionsTab.getExecutions()
                                            .isEmpty(), "Executions list shouldn't be empty!(for modal view)");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4698")
    public void userCanCreateSubSuiteFromCasesPanelTest() {
        String subSuiteName = "Sub-suite for suite ".concat(testSuite_2.getTitle());

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        BaseSuiteItem suiteItem = testCasesPage.findTestSuiteInSuiteTree(testSuite_2.getTitle());
        suiteItem.click();

        CreateOrEditSuiteModal createOrEditSuiteModal = testCasesPage.getTestSuite(testSuite_2.getTitle())
                                                                     .getSuiteActions()
                                                                     .clickCreateSubSuiteOrCaseButtonAndSelectCreateSubSuite();

        createOrEditSuiteModal
                .inputName(subSuiteName)
                .submitModal();

        boolean isPresent = testCasesPage.getTestSuiteTree()
                                         .getSuite(testSuite_2.getTitle())
                                         .getSubSuites().stream()
                                         .anyMatch(suiteI -> suiteI.getSuiteName().equalsIgnoreCase(subSuiteName));

        Assert.assertTrue(isPresent, "Created suite should be in list of sub-suites for " + testSuite_2);
    }

    @Test
    @TestCaseKey("ZTP-4893")
    public void userCanCreateCaseFromCasesPanelTest() {
        String caseTitle = "Case for suite ".concat(testSuite_2.getTitle());

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        BaseSuiteItem suiteItem = testCasesPage.findTestSuiteInSuiteTree(testSuite_2.getTitle());
        suiteItem.click();

        CreateTestCaseModal createTestCaseModal = testCasesPage.getTestSuite(testSuite_2.getTitle())
                                                               .getSuiteActions()
                                                               .clickCreateSubSuiteOrCaseButtonAndSelectCreateCase();

        createTestCaseModal
                .inputTitle(caseTitle)
                .submitModal();

        boolean isPresent = testCasesPage.getRepository()
                                         .getTestCase(caseTitle).isPresent();

        Assert.assertTrue(isPresent, "Created case should be in list of cases for " + testSuite_2);
    }

    @Test
    @TestCaseKey("ZTP-4880")
    public void searchFieldVerificationsTest() {
        // ZTP-4880 User is able to search the test case
        String textForSearch = testCaseForSuite_1.getTitle().substring(1, 3).trim();
        String existingKey = testCaseForSuite_1.getKey();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.searchTestCase(textForSearch);

        SoftAssert softAssert = new SoftAssert();

        testCasesPage.expandCases();
        List<RepositoryCaseItem> testCases = testCasesPage.getRepository().getAllTestCases();

        Assert.assertFalse(testCases.isEmpty(),
                "List with results shouldn't be empty and should contain at least one case with text " + textForSearch);

        testCases.forEach(testCase -> softAssert.assertTrue(testCase.getTestCaseTitleValue().contains(textForSearch),
                "Test case name should contains text " + textForSearch + " but name " + testCase.getTestCaseTitleValue()));

        // search by not existing value
        String notExistingCase = RandomStringUtils.randomAlphabetic(15);

        testCasesPage.searchTestCase(notExistingCase);
        testCases = testCasesPage.getRepository().getAllTestCases();

        Assert.assertTrue(testCases.isEmpty(),
                "List with results should be empty as we don't have cases with text" + textForSearch);

        EmptyPlaceholder emptyPlaceholder = testCasesPage.getEmptyPlaceholder();

        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_TITLE_WHEN_SEARCH, emptyPlaceholder.getEmptyPlaceHolderTitle(),
                "Empty placeholder test is not as expected!");
        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_DESCRIPTION_WHEN_SEARCH, emptyPlaceholder.getEmptyPlaceHolderDescription(),
                "Empty placeholder description is not as expected!");

        //search by key
        testCasesPage.searchTestCase(existingKey);
        testCasesPage.expandCases();
        testCases = testCasesPage.getRepository().getAllTestCases();

        Assert.assertFalse(testCases.isEmpty(),
                "List with results shouldn't be empty and should contain at least one case with text " + textForSearch);

        testCases.forEach(testCase -> softAssert.assertTrue(testCase.getCaseKeyValue().contains(existingKey),
                "Test case key should contains text " + existingKey));

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4881")
    public void resetSearchFieldVerificationsTest() {
        // ZTP-4881 User is able to reset the search results
        String textForSearch = testCaseForSuite_1.getKey();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.searchTestCase(textForSearch);

        SoftAssert softAssert = new SoftAssert();

        testCasesPage.expandCases();
        List<RepositoryCaseItem> testCases = testCasesPage.getRepository().getAllTestCases();
        Assert.assertFalse(testCases.isEmpty(),
                "List with results shouldn't be empty and should contain at least one case with text " + textForSearch);

        boolean isAllMatch = testCases.stream()
                                      .allMatch(testCase -> testCase.getCaseKeyValue().contains(textForSearch));

        softAssert.assertTrue(isAllMatch, "All cases should contain text " + textForSearch + " in title!");
        softAssert.assertEquals(testCasesPage.getSearch().getSearchValue(), textForSearch,
                "Search value should as search text");

        // clear search field with 'Reset' button
        testCasesPage.clickResetButton();
        pause(1);

        testCasesPage.expandCases();
        testCases = testCasesPage.getRepository().getAllTestCases();
        isAllMatch = testCases.stream()
                              .allMatch(testCase -> testCase.getCaseKeyValue().contains(textForSearch));

        softAssert.assertFalse(isAllMatch, "After click 'Reset' button all cases should be displayed!");
        softAssert.assertEquals(testCasesPage.getSearch().getSearchValue(), "",
                "Search value should be cleared after click on 'Reset' button");

        String notExistingCase = RandomStringUtils.randomAlphabetic(15);
        testCasesPage.searchTestCase(notExistingCase);

        EmptyPlaceholder emptyPlaceholder = testCasesPage.getEmptyPlaceholder();
        softAssert.assertTrue(emptyPlaceholder.isPresent(), "Empty placeholder should be visible!");

        // clear search field with click on cross input button
        ZbrSearch search = testCasesPage.getSearch().clearSearch();
        pause(1);

        softAssert.assertEquals(search.getSearchValue(), "",
                "Search value should be cleared after clicking on cross input button!");

        testCasesPage.expandCases();

        List<BaseRepositoryItem> items = testCasesPage.getRepository().getRepositoryItems();
        softAssert.assertFalse(items.isEmpty(),
                "List with results shouldn't be empty after clicking on cross search input button!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4859")
    public void emptyTestCaseRepositoryVerificationsTest() {
        // ZTP-4859 Verify test repository if there are no test suites/cases
        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), getEmptyProject().getKey());

        softAssert.assertTrue(testCasesPage.isCreateSuiteButtonClickable(), "Create suite button should be clickable in empty repository!");
        softAssert.assertTrue(testCasesPage.isCreateCaseButtonClickable(), "Create test case button should be clickable in empty repository!");
        softAssert.assertFalse(testCasesPage.getSearch()
                                            .isSearchClickable(), "Search field shouldn't be clickable in empty repository!");
        softAssert.assertTrue(testCasesPage.getRepository().getAllTestCases()
                                           .isEmpty(), "List with results should be empty in empty repository");

        EmptyPlaceholder emptyPlaceholder = testCasesPage.getEmptyPlaceholder();

        softAssert.assertTrue(emptyPlaceholder.isEmptyPlaceholderImagePresent(),
                "Empty placeholder image should be in empty repository!");
        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_TITLE_FOR_EMPTY_REPO, emptyPlaceholder.getEmptyPlaceHolderTitle(),
                "Empty placeholder test is not as expected!");
        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_DESCRIPTION_FOR_EMPTY_REPO, emptyPlaceholder.getEmptyPlaceHolderDescription(),
                "Empty placeholder description is not as expected!");

        softAssert.assertTrue(testCasesPage.isCreateTestSuiteButtonClickable(), "Create test suite button should be clickable in empty repository!");
        softAssert.assertTrue(testCasesPage.isImportButtonClickable(), "Import button should be clickable in empty repository!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5634")
    public void multipleTestCasesSelectTest() {
        // ZTP-5634 User is able to select multiple test cases using Shift
        SoftAssert softAssert = new SoftAssert();
        tcmService.createTestCases(project.getId(), testSuite_2.getId(), 4);

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.expandCases();
        testCasesPage.getTestSuite(testSuite_2.getTitle(), false).expandCasesIfNeeded();

        List<String> existingCases = testCasesPage.getRepository().getAllTestCases().stream()
                                                  .map(RepositoryCaseItem::getCaseKeyValue)
                                                  .collect(Collectors.toList());

        log.info("Existing cases " + existingCases);

        if (existingCases.size() < 4) {
            throw new IllegalArgumentException("The list must have at least 4 elements.");
        }

        List<String> expectedCheckedCases = new ArrayList<>(existingCases.subList(1, 4));
        log.info("Sublist (expected checked cases): " + expectedCheckedCases);

        existingCases.removeAll(expectedCheckedCases);
        log.info("List after removing expectedCheckedCases: " + existingCases);

        List<String> expectedUnCheckedCases = new ArrayList<>(existingCases.subList(0, Math.min(3, existingCases.size())));
        log.info("Sublist (expected unChecked cases) 3 items: " + expectedUnCheckedCases);

        KeyCombinations.CTRL_SHIFT_S.createAction(getDriver()).perform();

        String firstElementInSubList = expectedCheckedCases.isEmpty() ? null : expectedCheckedCases.get(0);
        log.info("First case for select: " + firstElementInSubList);

        testCasesPage.findTestCase(firstElementInSubList).getCheckbox()._click();

        KeyCombinations.SHIFT_DOWN.createAction(getDriver()).perform();

        String lastCaseForSelect = expectedCheckedCases.get(expectedCheckedCases.size() - 1);
        log.info("Last case for select: " + lastCaseForSelect);

        testCasesPage.findTestCase(lastCaseForSelect).getCheckbox()._click();

        KeyCombinations.SHIFT_UP.createAction(getDriver()).perform();

        RepositoryList repository = testCasesPage.getRepository();

        expectedCheckedCases.forEach(checked ->
                softAssert.assertTrue(repository.getTestCase(checked).getCheckbox()._isChecked(),
                        "Test case with key " + checked + " should be selected!")
        );

        expectedUnCheckedCases.forEach(unchecked ->
                softAssert.assertFalse(repository.getTestCase(unchecked).getCheckbox()._isChecked(),
                        "Test case with key" + unchecked + " shouldn't be selected!")
        );

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4897", "ZTP-4899"})
    public void copyTestSuiteIdAndLinkTest() {
        WebDriver webDriver = super.getDriver();

        String expectedSuiteLink = ConfigHelper.getTenantUrl() + "/projects/" + project.getKey() + "/test-cases?suiteId=" + testSuite_1.getId();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(webDriver, project.getKey());
        BaseRepositoryItem testSuite = testCasesPage.getTestSuite(testSuite_1.getTitle());
        testSuite.copyId();

        Tooltip tooltip = new Tooltip(webDriver);

        Assert.assertEquals(
                tooltip.getTextFromTooltipDirectly(), "Copied!",
                "Tooltip is not as expected after coping ID!"
        );
        Assert.assertEquals(
                testSuite.getClipboardText(), testSuite_1.getId().toString(),
                "Test suite id is not as expected!"
        );
        Assert.assertEquals(testSuite.copyLink(), expectedSuiteLink, "Test suite link is not as expected!");
        Assert.assertEquals(
                tooltip.getTextFromTooltipDirectly(), "Copied!",
                "Tooltip is not as expected after coping link!"
        );
    }

    private List<String> getExpectedTabNames() {
        TestCaseFieldsLayout rs = new TcmServiceImpl().getTestCaseFields(project.getId());

        Long generalTabId = rs.getTabs().stream()
                              .filter(tab -> tab.getName().equals(TestCaseFieldsTab.GENERAL_TAB_NAME))
                              .findFirst()
                              .orElseThrow()
                              .getId();

        List<String> expectedFields = rs.getFields().stream()
                                        .filter(field -> field.getTabId() != null && field.getTabId()
                                                                                          .equals(generalTabId))
                                        .map(GeneralField::getName)
                                        .collect(Collectors.toList());

        log.info("Expected general tabs " + expectedFields);

        Assert.assertFalse(expectedFields.isEmpty(), "List with General tabs shouldn't be empty!");
        return expectedFields;
    }

    private List<String> getExpectedPropertyNames() {
        TestCaseFieldsLayout rs = new TcmServiceImpl().getTestCaseFields(project.getId());

        Long propTabId = rs.getTabs().stream()
                           .filter(tab -> tab.getName().equals(TestCaseFieldsTab.PROPERTIES_TAB_NAME))
                           .findFirst()
                           .orElseThrow()
                           .getId();

        List<String> expectedFields = rs.getFields().stream()
                                        .filter(field -> field.getTabId() != null && field.getTabId().equals(propTabId))
                                        .map(GeneralField::getName)
                                        .collect(Collectors.toList());

        log.info("Expected properties tabs " + expectedFields);

        Assert.assertFalse(expectedFields.isEmpty(), "List with Properties tabs shouldn't be empty!");
        return expectedFields;
    }
}
