package com.zebrunner.automation.gui.tcm;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.gui.tcm.testcase.EditTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.ConfirmCancelOfTestCaseCreationModal;
import com.zebrunner.automation.gui.tcm.testcase.CreateTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.ImportTestCasesModal;
import com.zebrunner.automation.gui.tcm.testcase.CloneTestCaseModal;
import com.zebrunner.automation.gui.tcm.testsuite.BaseSuiteItem;
import com.zebrunner.automation.gui.tcm.testsuite.CreateOrEditSuiteModal;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import com.zebrunner.automation.gui.tcm.repository.SuiteSelectItem;
import com.zebrunner.automation.gui.tcm.testcase.RestoreTestCasesModals;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinPage;
import com.zebrunner.automation.gui.tcm.testcase.DedicatedTestCasePage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

@Slf4j
@Maintainer("obabich")
public class TestSuiteTest extends TcmLogInBase {

    private Project project;
    private TestSuite testSuite_1;
    private TestCase testCaseForSuite_1;


    @BeforeClass
    public void getProjectKey() {
        project = super.getCreatedProject();

        testSuite_1 = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        testCaseForSuite_1 = tcmService.createTestCase(project.getId(), testSuite_1.getId());
    }


    @Test
    @TestCaseKey({"ZTP-5211", "ZTP-5198"})
    public void userCanCreateSuiteDirectlyFromTestCaseCreationModalTest() {
        String suiteName1 = "Suite1" + RandomStringUtils.randomAlphanumeric(10);
        String suiteName2 = "Suite2" + RandomStringUtils.randomAlphanumeric(10);

        List<String> suites = List.of(suiteName1, suiteName2);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        CreateTestCaseModal createTestCaseModal = testCasesPage.clickCreateTestCaseBtn();

        for (String suiteName : suites) {
            createTestCaseModal.typeParentSuite(suiteName);

            SelectSuiteListBoxMenu listBoxMenu = new SelectSuiteListBoxMenu(getDriver());
            SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

            softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                    "New option label is not as expected! ");

            suiteSelectItem.click();
        }

        createTestCaseModal.clickCancel();

        ConfirmCancelOfTestCaseCreationModal confirmModal = new ConfirmCancelOfTestCaseCreationModal(getDriver());
        confirmModal.clickConfirm();

        testCasesPage.waitUntil(ExpectedConditions.invisibilityOf(createTestCaseModal), 3);

        suites.forEach(suiteName -> verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName));

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5196"})
    public void createTestCaseButtonIsAvailableWithoutCreatedSuites() {
        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), getEmptyProject().getKey());

        //ZTP-5196 Verify that +case button is available without created suites
        Assert.assertTrue(testCasesPage.isCreateCaseButtonClickable(),
                "'Create case' button should be available without suites!");

    }

    @Test
    @TestCaseKey({"ZTP-5197"})
    public void userCanCreateSuiteDirectlyFromEditTestCaseModalTest() {
        String suiteName = "Suite1" + RandomStringUtils.randomAlphanumeric(10);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        EditTestCaseModal editTestCaseModal = testCasesPage.findTestCase(testCaseForSuite_1.getTitle()).clickEdite();

        editTestCaseModal.typeParentSuite(suiteName);

        SelectSuiteListBoxMenu listBoxMenu = new SelectSuiteListBoxMenu(getDriver());
        SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

        softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                "New option label is not as expected! ");

        suiteSelectItem.click();
        editTestCaseModal.clickCancel();

        ConfirmCancelOfTestCaseCreationModal confirmModal = new ConfirmCancelOfTestCaseCreationModal(getDriver());
        confirmModal.clickConfirm();

        testCasesPage.waitUntil(ExpectedConditions.invisibilityOf(editTestCaseModal), 3);

        verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName);

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5200"})
    public void userCanCreateSuiteDirectlyFromCloneTestCaseModalTest() {
        String suiteName = "Suite1" + RandomStringUtils.randomAlphanumeric(10);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        CloneTestCaseModal cloneTestCaseModal = testCasesPage.findTestCase(testCaseForSuite_1.getTitle()).clickClone();

        cloneTestCaseModal.typeParentSuite(suiteName);

        SelectSuiteListBoxMenu listBoxMenu = new SelectSuiteListBoxMenu(getDriver());
        SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

        softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                "New option label is not as expected! ");

        suiteSelectItem.click();
        cloneTestCaseModal.clickCancel();

        testCasesPage.waitUntil(ExpectedConditions.invisibilityOf(cloneTestCaseModal), 3);

        verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName);

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5201"})
    public void userCanCreateSuiteDirectlyFromEditTestSuiteModalTest() {
        String suiteName = "Suite1" + RandomStringUtils.randomAlphanumeric(10);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        CreateOrEditSuiteModal editeSuiteModal = testCasesPage.getRepository().getSuite(testSuite_1.getTitle()).edit();
        editeSuiteModal.typeParentSuite(suiteName);

        SelectSuiteListBoxMenu listBoxMenu = new SelectSuiteListBoxMenu(getDriver());
        SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

        softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                "New option label is not as expected! ");

        suiteSelectItem.click();
        editeSuiteModal.clickCancel();

        testCasesPage.waitUntil(ExpectedConditions.invisibilityOf(editeSuiteModal), 3);

        verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName);

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5206", "ZTP-5210"})
    public void userCanCreateSuiteDirectlyFromRestoreTestCaseModalTest() {
        String suiteName = "Suite1" + RandomStringUtils.randomAlphanumeric(10);

        TestCase testCaseForDeletion = tcmService.createTestCase(project.getId(), testSuite_1.getId());
        tcmService.deleteTestCase(project.getId(), testCaseForDeletion.getId());

        SoftAssert softAssert = new SoftAssert();

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        RestoreTestCasesModals modal = trashBinPage.findTestCase(testCaseForDeletion.getTitle()).hoverAndClickRestore();

        modal.typeParentSuite(suiteName);

        SelectSuiteListBoxMenu listBoxMenu = new SelectSuiteListBoxMenu(getDriver());
        SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

        softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                "New option label is not as expected! ");

        suiteSelectItem.click();
        modal.clickCancel();

        trashBinPage.waitUntil(ExpectedConditions.invisibilityOf(modal), 3);

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName);

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5202"})
    public void userCanCreateSuiteDirectlyFromImportTestCaseModalTest() {
        String suiteName = "Suite1" + RandomStringUtils.randomAlphanumeric(10);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        ImportTestCasesModal modal = testCasesPage.openImportModal();

        modal.typeTargetSuite(suiteName);

        SelectSuiteListBoxMenu listBoxMenu = new SelectSuiteListBoxMenu(getDriver());
        SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

        softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                "New option label is not as expected! ");

        suiteSelectItem.click();
        modal.clickCancel();

        testCasesPage.waitUntil(ExpectedConditions.invisibilityOf(modal), 3);

        verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName);

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5246"})
    public void userCanCreateSuiteDirectlyFromDedicatedTestCasePageTest() {
        String suiteName = "Suite1" + RandomStringUtils.randomAlphanumeric(10);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        DedicatedTestCasePage dedicatedTestCasePage = testCasesPage
                .findTestCase(testCaseForSuite_1.getTitle())
                .clickTestCase()
                .clickTestCaseKeyAndSwitchTab();

        dedicatedTestCasePage.assertPageOpened();

        SelectSuiteListBoxMenu listBoxMenu = dedicatedTestCasePage.typeSuite(suiteName);
        SuiteSelectItem suiteSelectItem = listBoxMenu.findItem(suiteName);

        softAssert.assertEquals(suiteSelectItem.getNewOptionLabelValue(), SuiteSelectItem.NEW_TOP_LEVEL_SUITE,
                "New option label is not as expected! ");

        suiteSelectItem.click();

        testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        verifyTestSuiteAppearance.accept(softAssert, testCasesPage, suiteName);

        softAssert.assertAll();
    }

    TriConsumer<SoftAssert, TestCasesPage, String> verifyTestSuiteAppearance = (softAssert, testCasesPage, suiteName) -> {
        BaseSuiteItem suiteFromSuiteTree = testCasesPage.findTestSuiteInSuiteTree(suiteName);

        softAssert.assertTrue(suiteFromSuiteTree.isPresent(3),
                "Test suite " + suiteName + " should appear in suite tree" +
                        " after adding it in modal! ");

        // ZTP-5210 Verify that it is only possible to create top-level suites in such fashion
        softAssert.assertEquals(suiteFromSuiteTree.getDelimitersCount(), 0,
                "Delimiter count should be 0 as created suite should be on the top level");

        softAssert.assertTrue(testCasesPage.getTestSuite(suiteName).isPresent(),
                "Test suite " + suiteName + " should appear in repository" +
                        " after adding it in modal! ");
    };


}
