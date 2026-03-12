package com.zebrunner.automation.gui.tcm;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.gui.common.EmptyPlaceholder;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.gui.tcm.testcase.RepositoryCaseItem;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinTestCasePreview;
import com.zebrunner.automation.gui.tcm.testcase.SidebarGeneralTab;
import com.zebrunner.automation.gui.tcm.testrun.TestCaseFilterBlock;
import com.zebrunner.automation.gui.tcm.testrun.TextFilterMenu;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinControlPanel;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinTestCaseCard;
import com.zebrunner.automation.gui.tcm.testcase.PurgeTestCasesModals;
import com.zebrunner.automation.gui.tcm.testcase.RestoreTestCasesModals;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.TestCasePriority;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.automation.util.LocalStorageManager;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

public class TrashBinTest extends TcmLogInBase {

    private final List<Integer> usersIds = new ArrayList<>();
    private Project project;
    private TestSuite testSuite;

    @BeforeClass
    public void getProjectKey() {
        project = projectService.createProject();

        testSuite = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        tcmService.createTestCase(project.getId(), testSuite.getId());
    }

    @AfterClass(alwaysRun = true)
    public void deleteProject() {
        projectService.deleteProjectById(project.getId());
    }

    @AfterClass(groups = "user-created")
    public void deleteUser() {
        usersIds.forEach(usersService::deleteUserById);
    }


    //=================================================== Test =========================================================

    @Test()
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-3015", "ZTP-3017"})
    public void verifyUserCanOpenTrashBinPageAndVerifyEmptyBinPageElements() {
        SoftAssert softAssert = new SoftAssert();

        TrashBinPage trashBinPage = TestCasesPage.openPageDirectly(getDriver(), getEmptyProject().getKey())
                                                 .openTrashBinPage();

        softAssert.assertTrue(trashBinPage.isPageOpened(), "Trash bin page should be opened !");

        TrashBinControlPanel trashBinControlPanel = trashBinPage.getTrashBinControlPanel();

        softAssert.assertTrue(trashBinPage.getTrashBinTestCaseCardList().isEmpty(),
                "Trash bin should be empty, no test case should present !");

        softAssert.assertFalse(trashBinControlPanel.isRestoreAllButtonClickable(),
                "Restore all button shouldn't be clickable !");
        softAssert.assertFalse(trashBinControlPanel.isPurgeAllButtonClickable(),
                "Purge all button shouldn't be clickable !");
        softAssert.assertFalse(trashBinControlPanel.getSearchCasesTextField().isSearchClickable(),
                "Search case text field shouldn't be active !");
        softAssert.assertFalse(trashBinControlPanel.isFilterButtonClickable(),
                "Filter button shouldn't be clickable !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3016")
    public void verifyDeletedCaseAppearsOnTrashBinPage() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCase.getTitle());

        caseItem.clickDelete().clickDelete();

        softAssert.assertEquals(testCasesPage.getPopUp(), MessageEnum.TEST_CASE_SUCCESSFULLY_DELETED
                .getDescription(testCase.getTitle()), "Case deletion message is not as expected !");

        TrashBinPage trashBinPage = testCasesPage.openTrashBinPage();

        softAssert.assertTrue(trashBinPage.isPageOpened(), "Trash bin page should be opened !");
        softAssert.assertTrue(trashBinPage.isTestCasePresent(testCase.getTitle()),
                "Test case should be present in trash bin page !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3018")
    public void verifyUserIsAbleToRestoreAllTestCases() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());
        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());

        softAssert.assertTrue(trashBinPage.isPageOpened(), "Trash bin page should be opened !");
        softAssert.assertTrue(trashBinPage.getTrashBinControlPanel().isRestoreAllButtonClickable(),
                "Restore all button should be clickable !");

        RestoreTestCasesModals restoreAllTestCasesModal = trashBinPage.getTrashBinControlPanel()
                                                                      .clickRestoreAllButton();

        softAssert.assertEquals(restoreAllTestCasesModal.getHeader()
                                                        .getTitleText(), RestoreTestCasesModals.RESTORE_ALL_TEST_CASE_MODAL_TITLE,
                "Restore all test cases modal should be opened !");

        restoreAllTestCasesModal.selectSuite(testSuite.getTitle());
        restoreAllTestCasesModal.clickRestoreButton();

        softAssert.assertEquals(trashBinPage.getPopUp(), MessageEnum.TEST_CASES_WERE_RESTORED.getDescription(),
                "Message is not as expected !");
        softAssert.assertTrue(trashBinPage.getTrashBinTestCaseCardList()
                                          .isEmpty(), "No test case should be present on the page !");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        testCasesPage.expandSuitePanel();

        testCasesPage.searchTestCase(testCase.getTitle());
        pause(4);

        softAssert.assertTrue(testCasesPage.isTestCasePresent(testCase.getTitle()), "Test case should be restored !");
        softAssert.assertEquals(testCasesPage.findTestCase(testCase.getTitle())
                                             .getParentSuiteName(), testSuite.getTitle(),
                "Test case should be restored to correct suite !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5274")
    public void verifyUserCanCancelRestoreAllTestCase() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());
        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());

        softAssert.assertTrue(trashBinPage.getTrashBinControlPanel().isRestoreAllButtonClickable(),
                "Restore all button should be clickable !");

        for (int i = 0; i < 3; i++) {
            RestoreTestCasesModals restoreAllTestCasesModal = trashBinPage.getTrashBinControlPanel()
                                                                          .clickRestoreAllButton();

            softAssert.assertEquals(restoreAllTestCasesModal.getHeader()
                                                            .getTitleText(), RestoreTestCasesModals.RESTORE_ALL_TEST_CASE_MODAL_TITLE,
                    "Restore all test cases modal should be opened !");

            if (i == 0) {
                restoreAllTestCasesModal.clickCancel();
                softAssert.assertFalse(restoreAllTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'Cancel' button !");
            } else if (i == 1) {
                restoreAllTestCasesModal.clickClose();
                softAssert.assertFalse(restoreAllTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'X' button !");
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
                softAssert.assertFalse(restoreAllTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'Esc' key !");
            }
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5275")
    public void verifyUserIsAblePurgeAllTestCase() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());
        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());

        softAssert.assertTrue(trashBinPage.getTrashBinControlPanel().isPurgeAllButtonClickable(),
                "Purge all button should be clickable !");

        PurgeTestCasesModals purgeAllTestCasesModal = trashBinPage.getTrashBinControlPanel().clickPurgeAllButton();

        softAssert.assertEquals(purgeAllTestCasesModal.getHeader()
                                                      .getTitleText(), PurgeTestCasesModals.PURGE_ALL_TEST_CASES_MODAL_TITLE,
                "Purge all test case modal should be opened !");

        purgeAllTestCasesModal.clickPurgeButton();

        softAssert.assertEquals(trashBinPage.getPopUp(), MessageEnum.TEST_CASES_WERE_DELETED.getDescription(),
                "Message is not as expected !");
        softAssert.assertTrue(trashBinPage.getTrashBinTestCaseCardList().isEmpty(), "Trash bin should be empty !");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        testCasesPage.expandSuitePanel();

        testCasesPage.searchTestCase(testCase.getTitle());

        pause(4);

        EmptyPlaceholder emptyPlaceholder = testCasesPage.getEmptyPlaceholder();

        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_TITLE_WHEN_SEARCH, emptyPlaceholder.getEmptyPlaceHolderTitle(),
                "Test case shouldn't be present !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5276")
    public void verifyUserCanCancelPurgeAllTestCase() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());
        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());

        softAssert.assertTrue(trashBinPage.getTrashBinControlPanel().isPurgeAllButtonClickable(),
                "Purge all button should be clickable !");

        for (int i = 0; i < 3; i++) {
            PurgeTestCasesModals purgeAllTestCasesModal = trashBinPage.getTrashBinControlPanel().clickPurgeAllButton();

            softAssert.assertEquals(purgeAllTestCasesModal.getHeader()
                                                          .getTitleText(), PurgeTestCasesModals.PURGE_ALL_TEST_CASES_MODAL_TITLE,
                    "Purge all test cases modal should be opened !");

            if (i == 0) {
                purgeAllTestCasesModal.clickCancel();
                softAssert.assertFalse(purgeAllTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'Cancel' button !");
            } else if (i == 1) {
                purgeAllTestCasesModal.clickClose();
                softAssert.assertFalse(purgeAllTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'X' button !");
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
                softAssert.assertFalse(purgeAllTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'Esc' key !");
            }
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-3020", "ZTP-4043"})
    public void verifyUserCanSearchTestCaseAndAfterClickingTestSearchFilterStillApplied() {
        SoftAssert softAssert = new SoftAssert();

        List<TestCase> testCaseList = tcmService.createTestCases(project.getId(), testSuite.getId(), 11);
        for (TestCase testCase : testCaseList) {
            tcmService.deleteTestCase(project.getId(), testCase.getId());
        }

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());

        softAssert.assertTrue(trashBinPage.getTrashBinControlPanel().getSearchCasesTextField().isSearchClickable(),
                "Search filed should be active !");

        trashBinPage.getTrashBinControlPanel().searchTestCase(testCaseList.get(1).getTitle());

        pause(4);

        int countOfTestCases = trashBinPage.getTrashBinTestCaseCardList().size();

        //ZTP-3020 ->  Verify that user can use search field
        softAssert.assertTrue(trashBinPage.isTestCasePresent(testCaseList.get(1)
                                                                         .getTitle()), "Test case should be present !");
        softAssert.assertEquals(countOfTestCases, 1, "Other test case shouldn't be present !");

        String expectedPagesAndTestCases = trashBinPage.getPagination().getPages();
        trashBinPage.getTrashBinTestCaseCardList().get(0).click();

        //ZTP-4043 -> Verify that filter is still applied after click on the test case
        softAssert.assertEquals(trashBinPage.getPagination().getPages(), expectedPagesAndTestCases,
                "Searched filter should be still applied after clicking test case !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaldze")
    @TestCaseKey({"ZTP-3022", "ZTP-3026"})
    public void verifyUserCanSelectTestCaseAndAllTestCasesViaCheckbox() {
        SoftAssert softAssert = new SoftAssert();

        List<TestCase> testCaseList = tcmService.createTestCases(project.getId(), testSuite.getId(), 11);
        for (TestCase testCase : testCaseList) {
            tcmService.deleteTestCase(project.getId(), testCase.getId());
        }

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard checkedTrashBinTestCaseCard = trashBinPage.findTestCase(testCaseList.get(2).getTitle());
        checkedTrashBinTestCaseCard.getCheckbox()._click();

        //ZTP-3022 - Verify that user can select test cases via checkboxes
        softAssert.assertTrue(checkedTrashBinTestCaseCard.getCheckbox()._isChecked(), "Check box should be checked !");
        softAssert.assertTrue(trashBinPage.getTrashBinListAction()
                                          .isPurgeButtonPresent(), "Purge button should be present !");
        softAssert.assertTrue(trashBinPage.getTrashBinListAction()
                                          .isRestoreButtonPresent(), "Restore button should be present !");

        for (TrashBinTestCaseCard card : trashBinPage.getTrashBinTestCaseCardList()) {
            if (!card.getTestCaseTitleText().equals(checkedTrashBinTestCaseCard.getTestCaseTitleText())) {
                softAssert.assertFalse(card.getCheckbox()._isChecked(), "Other test cases shouldn't be checked !");
            }
        }

        trashBinPage.getTrashBinListAction().getAllCheckBoxButton()._click();

        //ZTP-3026 - Verify that user can select all test cases via checkbox
        for (TrashBinTestCaseCard card : trashBinPage.getTrashBinTestCaseCardList()) {
            softAssert.assertTrue(card.getCheckbox()._isChecked(), "All Test cases should be checked !");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3023")
    public void verifyChoiceTestCaseDoesNotRemainAfterChangingPage() {
        SoftAssert softAssert = new SoftAssert();

        List<TestCase> testCaseList = tcmService.createTestCases(project.getId(), testSuite.getId(), 11);
        for (TestCase testCase : testCaseList) {
            tcmService.deleteTestCase(project.getId(), testCase.getId());
        }

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.getTopPagination().selectTenItems();
        pause(2);

        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCaseList.get(10).getTitle());
        trashBinTestCaseCard.getCheckbox()._click();

        softAssert.assertTrue(trashBinTestCaseCard.getCheckbox()._isChecked(), "Check box should be checked !");

        trashBinPage.getTopPagination().clickToNextPagePagination();

        pause(2);

        trashBinPage.getTopPagination().clickToPreviousPagePagination();

        pause(2);

        trashBinTestCaseCard = trashBinPage.findTestCase(testCaseList.get(10).getTitle());

        softAssert.assertFalse(trashBinTestCaseCard.getCheckbox()._isChecked(), "Check box shouldn't be checked !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3024")
    public void verifyUserCanRestoreTestCaseViaCheckBox() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        trashBinTestCaseCard.getCheckbox()._click();
        RestoreTestCasesModals restoreTestCaseModal = trashBinPage.getTrashBinListAction().clickRestoreButton();

        softAssert.assertEquals(restoreTestCaseModal.getHeader()
                                                    .getTitleText(), RestoreTestCasesModals.RESTORE_TEST_CASE_MODAL_TITLE,
                "Restore test case modal should be opened !");

        restoreTestCaseModal.selectSuite(testSuite.getTitle());
        restoreTestCaseModal.clickRestoreButton();

        softAssert.assertEquals(trashBinPage.getPopUp(), MessageEnum.TEST_CASE_WAS_RESTORED.getDescription(),
                "Message is not as expected !");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        testCasesPage.expandSuitePanel();

        testCasesPage.searchTestCase(testCase.getTitle());
        pause(4);

        softAssert.assertTrue(testCasesPage.isTestCasePresent(testCase.getTitle()), "Test case should be restored !");
        softAssert.assertEquals(testCasesPage.findTestCase(testCase.getTitle())
                                             .getParentSuiteName(), testSuite.getTitle(),
                "Test case should be restored to correct suite !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3025")
    public void verifyUserCanPurgeTestCaseViaCheckBox() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        trashBinTestCaseCard.getCheckbox()._click();
        PurgeTestCasesModals purgeTestCaseModal = trashBinPage.getTrashBinListAction().clickPurgeButton();

        softAssert.assertEquals(purgeTestCaseModal.getHeader()
                                                  .getTitleText(), PurgeTestCasesModals.PURGE_TEST_CASE_MODAL_TITLE,
                "Purge test case modal should be opened !");

        purgeTestCaseModal.clickPurgeButton();

        softAssert.assertEquals(trashBinPage.getPopUp(), MessageEnum.TEST_CASE_DELETED.getDescription(),
                "Message is not as expected !");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.expandSuitePanel();

        testCasesPage.searchTestCase(testCase.getTitle());
        pause(4);

        EmptyPlaceholder emptyPlaceholder = testCasesPage.getEmptyPlaceholder();

        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_TITLE_WHEN_SEARCH, emptyPlaceholder.getEmptyPlaceHolderTitle(),
                "Test case shouldn't be present !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3027")
    public void verifyUserCanRestoreTestCaseViaRestoreIcon() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        trashBinTestCaseCard.hoverTestCase();

        softAssert.assertTrue(trashBinTestCaseCard.isRestoreTestCaseIconPresent(),
                "Restore test case icon should be present !");

        RestoreTestCasesModals restoreTestCaseModal = trashBinTestCaseCard.clickRestoreTestCaseIcon();
        softAssert.assertEquals(restoreTestCaseModal.getHeader()
                                                    .getTitleText(), RestoreTestCasesModals.RESTORE_TEST_CASE_MODAL_TITLE,
                "Restore test case modal should be opened !");

        restoreTestCaseModal.selectSuite(testSuite.getTitle());
        restoreTestCaseModal.clickRestoreButton();

        softAssert.assertEquals(trashBinPage.getPopUp(), MessageEnum.TEST_CASE_WAS_RESTORED.getDescription(),
                "Message is not as expected !");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.expandSuitePanel();
        testCasesPage.searchTestCase(testCase.getTitle());
        pause(4);

        softAssert.assertTrue(testCasesPage.isTestCasePresent(testCase.getTitle()), "Test case should be restored !");
        softAssert.assertEquals(testCasesPage.findTestCase(testCase.getTitle())
                                             .getParentSuiteName(), testSuite.getTitle(),
                "Test case should be restored to correct suite !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3028")
    public void verifyUserCanPurgeTestCaseViaPurgeIcon() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        trashBinTestCaseCard.hoverTestCase();

        softAssert.assertTrue(trashBinTestCaseCard.isPurgeTestCaseIconPresent(),
                "Purge test case icon should be present !");

        PurgeTestCasesModals purgeTestCasesModal = trashBinTestCaseCard.clickPurgeTestCaseIcon();
        softAssert.assertEquals(purgeTestCasesModal.getHeader()
                                                   .getTitleText(), PurgeTestCasesModals.PURGE_TEST_CASE_MODAL_TITLE,
                "Purge test case modal should be opened !");

        purgeTestCasesModal.clickPurgeButton();

        softAssert.assertEquals(trashBinPage.getPopUp(), MessageEnum.TEST_CASE_DELETED.getDescription(),
                "Message is not as expected !");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.expandSuitePanel();
        testCasesPage.searchTestCase(testCase.getTitle());
        pause(4);

        EmptyPlaceholder emptyPlaceholder = testCasesPage.getEmptyPlaceholder();

        softAssert.assertEquals(TestCasesPage.EMPTY_PLACEHOLDER_TITLE_WHEN_SEARCH, emptyPlaceholder.getEmptyPlaceHolderTitle(),
                "Test case shouldn't be present !");

        softAssert.assertAll();
    }


    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-3029", "ZTP-3030"})
    public void verifyUserCanViewButCannotEditTestCasePreview() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        TrashBinTestCasePreview testCasePreview = trashBinTestCaseCard.clickTestCard();

        softAssert.assertTrue(testCasePreview.isPresent(2), "Test case preview is not opened !");

        SidebarGeneralTab sidebarGeneralTab = testCasePreview.openGeneralTab();

        softAssert.assertTrue(sidebarGeneralTab.isPreconditionsInputActive(), "Any field should not be editable !");
        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-3021")
    public void verifyUserCanUseFilter() {
        SoftAssert softAssert = new SoftAssert();

        TestCasePriority highPriority = tcmService.getTestCaseSystemFields(project.getId())
                                                  .getPriorities().stream()
                                                  .filter(p -> p.getName()
                                                                .equals(TestCasePriority.Priority.HIGH.getPriority()))
                                                  .findFirst()
                                                  .orElse(null);

        TestCasePriority lowPriority = tcmService.getTestCaseSystemFields(project.getId())
                                                 .getPriorities().stream()
                                                 .filter(p -> p.getName()
                                                               .equals(TestCasePriority.Priority.LOW.getPriority()))
                                                 .findFirst()
                                                 .orElse(null);

        TestCase highPrioritytestCase = TestCase.builder()
                                                .title("Test case № ".concat(RandomStringUtils.randomNumeric(7)))
                                                .priority(highPriority)
                                                .build();
        highPrioritytestCase = tcmService.createTestCase(project.getId(), testSuite.getId(), highPrioritytestCase);

        TestCase lowPriorityTestCase = TestCase.builder()
                                               .title("Test case № ".concat(RandomStringUtils.randomNumeric(7)))
                                               .priority(lowPriority)
                                               .build();
        lowPriorityTestCase = tcmService.createTestCase(project.getId(), testSuite.getId(), lowPriorityTestCase);

        tcmService.deleteTestCase(project.getId(), highPrioritytestCase.getId());
        tcmService.deleteTestCase(project.getId(), lowPriorityTestCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.getTrashBinControlPanel().clickFilterButton()
                    .selectFilterItem(TestCaseFilterBlock.TestCaseFiltersEnum.PRIORITY, TestCasePriority.Priority.HIGH.getPriority());

        pause(2);

        List<TrashBinTestCaseCard> trashBinTestCaseCards = trashBinPage.getTrashBinTestCaseCardList();

        Assert.assertFalse(trashBinTestCaseCards.isEmpty(), "Test cards should be present (List should not be empty) !");

        for (TrashBinTestCaseCard trashBinTestCaseCard : trashBinTestCaseCards) {
            softAssert.assertEquals(trashBinTestCaseCard.getPriority(), TestCasePriority.Priority.HIGH.getPriority(),
                    "High priority test cases should be present !");
        }
        softAssert.assertAll();
    }


    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5297")
    public void verifyUserCanCancelRestoringTestCase() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.getTrashBinControlPanel().searchTestCase(testCase.getTitle());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());

        for (int i = 0; i < 3; i++) {
            trashBinTestCaseCard.hoverTestCase();

            softAssert.assertTrue(trashBinTestCaseCard.isRestoreTestCaseIconPresent(),
                    "Restore test case icon should be present !");

            RestoreTestCasesModals restoreTestCaseModal = trashBinTestCaseCard.clickRestoreTestCaseIcon();

            softAssert.assertEquals(restoreTestCaseModal.getHeader()
                                                        .getTitleText(), RestoreTestCasesModals.RESTORE_TEST_CASE_MODAL_TITLE,
                    "Restore test case modal should be opened !");

            if (i == 0) {
                restoreTestCaseModal.clickCancel();
                softAssert.assertFalse(restoreTestCaseModal.isModalOpened(),
                        "Modal should be closed after clicking 'Cancel' button !");
            } else if (i == 1) {
                restoreTestCaseModal.clickClose();
                softAssert.assertFalse(restoreTestCaseModal.isModalOpened(),
                        "Modal should be closed after clicking 'X' button !");
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
                softAssert.assertFalse(restoreTestCaseModal.isModalOpened(),
                        "Modal should be closed after clicking 'Esc' key !");
            }
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5298")
    public void verifyUserCanCancelPurgingTestCase() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.getTrashBinControlPanel().searchTestCase(testCase.getTitle());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());

        for (int i = 0; i < 3; i++) {
            trashBinTestCaseCard.hoverTestCase();

            softAssert.assertTrue(trashBinTestCaseCard.isPurgeTestCaseIconPresent(),
                    "Purge test case icon should be present !");

            PurgeTestCasesModals purgeTestCasesModal = trashBinTestCaseCard.clickPurgeTestCaseIcon();

            softAssert.assertEquals(purgeTestCasesModal.getHeader()
                                                       .getTitleText(), PurgeTestCasesModals.PURGE_TEST_CASE_MODAL_TITLE,
                    "Purge test case modal should be opened !");

            if (i == 0) {
                purgeTestCasesModal.clickCancel();
                softAssert.assertFalse(purgeTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'Cancel' button !");
            } else if (i == 1) {
                purgeTestCasesModal.clickClose();
                softAssert.assertFalse(purgeTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'X' button !");
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
                softAssert.assertFalse(purgeTestCasesModal.isModalOpened(),
                        "Modal should be closed after clicking 'Esc' key !");
            }
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5758")
    public void verifyThatUserCardAppearsWhenUserMouseOverUsername() {
        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.getTrashBinControlPanel().searchTestCase(testCase.getTitle());
        pause(3);
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        UserInfoTooltip userInfoTooltip = trashBinTestCaseCard.hoverUsername();

        Assert.assertEquals(userInfoTooltip.getUsername(), MAIN_ADMIN.getUsername(), "Username is not as excepted !");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5560")
    public void verifyTokenIsRemovedFromUrlAfterCleaningSearch() {
        SoftAssert softAssert = new SoftAssert();

        final String tokenAttributeInUrl = "searchToken";

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinControlPanel trashBinControlPanel = trashBinPage.getTrashBinControlPanel();

        for (int i = 0; i < 3; i++) {
            trashBinControlPanel.searchTestCase(testCase.getTitle());
            pause(3);

            String url = trashBinPage.getCurrentUrl();

            softAssert.assertTrue(url.contains(tokenAttributeInUrl), "Token should be present in the URL after search !");

            if (i == 0) {
                trashBinControlPanel.clickSearchResetButton();
                pause(3);

                url = trashBinPage.getCurrentUrl();

                softAssert.assertFalse(url.contains(tokenAttributeInUrl),
                        "Token shouldn't be present in the URL after resetting search!");
            }
            if (i == 1) {
                trashBinControlPanel.getSearchCasesTextField().clearSearch();
                pause(3);

                url = trashBinPage.getCurrentUrl();

                softAssert.assertFalse(url.contains(tokenAttributeInUrl),
                        "Token shouldn't be present in the URL after clearing search!");
            }
            if (i == 2) {
                ExtendedWebElement searchTextField = trashBinControlPanel.getSearchCasesTextField().getSearchField();
                String searchText = searchTextField.getAttribute("value");

                for (int j = 0; j < searchText.length() + 20; j++) {
                    searchTextField.sendKeys(Keys.BACK_SPACE);
                }

                pause(3);

                url = trashBinPage.getCurrentUrl();

                softAssert.assertFalse(url.contains(tokenAttributeInUrl),
                        "Token shouldn't be present in the URL after clearing search via backspace!");
            }
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5557")
    public void verifyUserCanApplyDeletedFilter() {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinControlPanel trashBinControlPanel = trashBinPage.getTrashBinControlPanel();
        trashBinControlPanel.clickFilterButton().selectDeletedDate(Dropdown.DropdownItemsEnum.ON, now);

        Assert.assertTrue(
                trashBinPage.isTestCasePresent(testCase.getTitle()),
                "Test case should be present after choosing deleted filter!"
        );

        trashBinControlPanel.clickFilterResetButton();
        trashBinControlPanel.clickFilterButton()
                            .selectDeletedDate(Dropdown.DropdownItemsEnum.ON_OR_BEFORE, yesterday);

        Assert.assertTrue(
                trashBinPage.getTrashBinTestCaseCardList().isEmpty(),
                "Trash bin should be empty, no test case should present !"
        );
    }

    @Test(groups = "user-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-5558", "ZTP-5559"})
    public void verifyUserCanSearchAndApplyDeletedByFilter() {
        WebDriver webDriver = getDriver();
        SoftAssert softAssert = new SoftAssert();

        User user = usersService.addRandomUserToTenant();
        usersIds.add(user.getId());
        projectService.assignUserToProject(project.getId(), user.getId(), RoleEnum.ADMINISTRATOR.getName()
                                                                                                .toUpperCase());
        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());

        LocalStorageManager localStorageManager = new LocalStorageManager(webDriver);
        localStorageManager.clear();
        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(user));

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(webDriver, project.getKey());
        super.pause(4);

        testCasesPage.searchTestCase(testCase.getTitle());
        testCasesPage.findTestCase(testCase.getTitle())
                     .clickDelete()
                     .clickDelete();

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(webDriver, project.getKey());
        TestCaseFilterBlock testCaseFilterBlock = trashBinPage.getTrashBinControlPanel()
                                                              .clickFilterButton();

        Dropdown dropdown = testCaseFilterBlock.getDropdown();
        dropdown.findItem(TestCaseFilterBlock.TestCaseFiltersEnum.DELETED_BY.getValue()).click();

        testCaseFilterBlock.searchDropDownItem(user.getUsername());
        ExtendedWebElement dropdownItem = dropdown.findItem(user.getUsername());

        //ZTP-5559 -> User can use search in 'Deleted by' filter
        softAssert.assertTrue(dropdownItem.isElementPresent(1), "Dropdown item should be present after search !");

        //ZTP-5558 -> User can apply 'Deleted by' filter
        dropdownItem.click();
        for (TrashBinTestCaseCard testCaseCard : trashBinPage.getTrashBinTestCaseCardList()) {
            softAssert.assertEquals(
                    testCaseCard.getUsername().getText(),
                    user.getUsername(),
                    "Only test deleted by user '" + user.getUsername() + "' should be present !"
            );
        }
        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-5317", "ZTP-5318"})
    public void verifyToolTipWithFullTestCaseNameAndTestIcons() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = TestCase.builder()
                                    .title("Test case № ".concat(RandomStringUtils.randomNumeric(200)))
                                    .build();

        testCase = tcmService.createTestCase(project.getId(), testSuite.getId(), testCase);
        tcmService.deleteTestCase(project.getId(), testCase.getId());
        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());

        softAssert.assertEquals(trashBinTestCaseCard.hoverAndGetTestCaseTitleToolTip(), testCase.getTitle(),
                "Test case full name tooltip should be displayed !");

        final String expectedPurgeTestCaseIconToolTip = "Purge test case";
        final String expectedRestoreTestCaseIconToolTip = "Restore test case";

        trashBinTestCaseCard.hoverTestCase();

        softAssert.assertEquals(trashBinTestCaseCard.hoverAndGetRestoreTestIconToolTip(), expectedRestoreTestCaseIconToolTip,
                "Restore test case icon tooltip should be displayed !");
        softAssert.assertEquals(trashBinTestCaseCard.hoverAndGetPurgeTestIconToolTip(), expectedPurgeTestCaseIconToolTip,
                "Purge test case icon tooltip should be displayed!");

        softAssert.assertAll();
    }

    @Test(priority = 7)
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5319")
    public void verifyPaginationInTheTrashBin() {
        SoftAssert softAssert = new SoftAssert();

        List<TestCase> testCaseList = tcmService.createTestCases(project.getId(), testSuite.getId(), 26);
        for (TestCase testCase : testCaseList) {
            tcmService.deleteTestCase(project.getId(), testCase.getId());
        }

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        String testCaseTitle = trashBinPage.getTrashBinTestCaseCardList().get(0).getTestCaseTitleText();

        trashBinPage.getTopPagination().clickToNextPagePagination();

        pause(4);
        softAssert.assertFalse(trashBinPage.isTestCasePresent(testCaseTitle),
                "Next page should be opened, test case shouldn't be present !");

        trashBinPage.getTopPagination().clickToPreviousPagePagination();

        pause(4);
        softAssert.assertTrue(trashBinPage.isTestCasePresent(testCaseTitle),
                "Previous page should be opened, test case should be present !");

        trashBinPage.getTopPagination().clickToLastPagePagination();

        pause(4);
        softAssert.assertFalse(trashBinPage.isTestCasePresent(testCaseTitle),
                "Last page should be opened, test case shouldn't be present !");

        trashBinPage.getTopPagination().clickToFirstPagePagination();

        pause(4);
        softAssert.assertTrue(trashBinPage.isTestCasePresent(testCaseTitle),
                "First page should be opened, test case should be present !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-5653", "ZTP-5654", "ZTP-5655", "ZTP-5656", "ZTP-5657", "ZTP-5658"})
    public void verifyTypeOfFilters() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        final String exceptedAction = "This is step";
        TestCase.Step regular = TestCase.Step.regular(TestCaseStep.with(exceptedAction, "good"));

        TestCase testCaseWithStep = TestCase.builder()
                                            .title("Test case № ".concat(RandomStringUtils.randomNumeric(7)))
                                            .steps(Collections.singletonList(regular))
                                            .build();
        testCaseWithStep = tcmService.createTestCase(project.getId(), testSuite.getId(), testCaseWithStep);

        tcmService.deleteTestCase(project.getId(), testCaseWithStep.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());

        TestCaseFilterBlock testCaseFilterBlock = trashBinPage.getTrashBinControlPanel().clickFilterButton();
        List<TestCaseFilterBlock.TestCaseFiltersEnum> testCaseFiltersEnumList = List.of(TestCaseFilterBlock.TestCaseFiltersEnum.values());

        //ZTP-5653, ZTP-5654, ZTP-5656, ZTP-5657
        for (TestCaseFilterBlock.TestCaseFiltersEnum filter : testCaseFiltersEnumList) {
            softAssert.assertTrue(testCaseFilterBlock.isFilterTypeDisplayed(filter),
                    String.format("Type of filter '%s' should be displayed !", filter));
            softAssert.assertEquals(testCaseFilterBlock.getFilterType(filter), filter.getType(),
                    String.format("Type for filter '%s' is not as excepted !", filter.getValue()));
        }

        TextFilterMenu stepToReproduceTextFilterMenu = testCaseFilterBlock.openStepsToReproduce();

        //ZTP-5655 - Verify fields type of filter for Steps for reproduce
        softAssert.assertTrue(stepToReproduceTextFilterMenu.isContainsLabelPresent(), "Contains label should be present !");
        softAssert.assertTrue(stepToReproduceTextFilterMenu.isTextAreaActive(), "Text area should be active !");
        softAssert.assertTrue(stepToReproduceTextFilterMenu.isApplyButtonClickable(), "Apply button should be clickable !");
        softAssert.assertEquals(stepToReproduceTextFilterMenu.getTextArea().getAttribute("placeholder"),
                "Enter your search query...", "Text are placeholder is not as excepted !");

        //ZTP-5658 - Verify that searching takes place after clicking the ‘Apply’ button
        stepToReproduceTextFilterMenu.searchQuery(exceptedAction);
        stepToReproduceTextFilterMenu.clickApplyButton();

        pause(3);

        softAssert.assertTrue(trashBinPage.isTestCasePresent(testCaseWithStep.getTitle()), "Test case should be present !");
        softAssert.assertEquals(trashBinPage.getTrashBinTestCaseCardList()
                                            .size(), 1, "Other test case shouldn't be present !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4593")
    public void verifyFullDateToolTipDisplayedAfterHoveringDeletedDate() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());
        LocalDateTime currentTime = LocalDateTime.now();

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.getTrashBinControlPanel().searchTestCase(testCase.getTitle());
        TrashBinTestCaseCard trashBinTestCaseCard = trashBinPage.findTestCase(testCase.getTitle());
        String actualTime = trashBinTestCaseCard.hoverAndGetDeletedDateToolTip();

        String regexPattern = "^(January|February|March|April|May|June|July|August|September|October|November|December) "
                + "\\d{1,2}, \\d{4} at \\d{1,2}:\\d{2}:\\d{2} [AP]M$";
        Pattern pattern = Pattern.compile(regexPattern);
        boolean isRegexCorrect = pattern.matcher(actualTime).matches();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm:ss a");

        boolean isFormatCorrect = true;
        try {
            LocalDateTime.parse(actualTime, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            isFormatCorrect = false;
        }

        softAssert.assertTrue(isRegexCorrect, "The date format does not match the expected pattern!");
        softAssert.assertTrue(isFormatCorrect, "The date format is incorrect!");
        softAssert.assertTrue(DateUtil.isDateWithinTolerance(actualTime, currentTime, 2, dateTimeFormatter),
                "Date difference exceeds tolerance!");

        softAssert.assertAll();
    }
}
