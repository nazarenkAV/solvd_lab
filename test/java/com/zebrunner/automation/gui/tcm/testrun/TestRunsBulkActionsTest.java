package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class TestRunsBulkActionsTest extends TcmLogInBase {

    private Project project;
    private TestRun testRun;

    @BeforeClass
    public void preparation() {
        project = super.getCreatedProject();

        testRun = tcmService.createTestRun(project.getId(), TestRun.createWithRandomName());
    }

    @Test
    @TestCaseKey({"ZTP-4810", "ZTP-4824"})
    public void verifyCheckboxIsVisibleOnTestRunsGridPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        softAssert.assertTrue(testRunsGridPage.isPageOpened(), "Test runs page isn't opened");
        softAssert.assertTrue(testRunsGridPage.isCheckboxForAllTestRunsAppear(),
                "Checkbox for all test runs isn't appear on test runs grid"); // ZTP-4810 Checkbox is visible on test runs grid

        testRunsGridPage.clickOpenedTestRuns();
        testRunsGridPage.getTestRunItem(testRun.getTitle())
                .clickCheckBox();
        BulkActionsPanel bulkActionsPanel = testRunsGridPage.getBulkActionsPanel();

        // ZTP-4824 bulk actions are available for open test runs
        softAssert.assertTrue(bulkActionsPanel.isBulkActionExist(BulkActionsPanel.TestRunsBulkActionsEnum.DELETE),
                "Bulk action '" + BulkActionsPanel.TestRunsBulkActionsEnum.DELETE + "' isn't appear");
        softAssert.assertTrue(bulkActionsPanel.isBulkActionExist(BulkActionsPanel.TestRunsBulkActionsEnum.CLOSE),
                "Bulk action '" + BulkActionsPanel.TestRunsBulkActionsEnum.CLOSE + "' isn't appear");
        softAssert.assertTrue(bulkActionsPanel.isBulkActionExist(BulkActionsPanel.TestRunsBulkActionsEnum.ASSIGN_TO_MILESTONE),
                "Bulk action '" + BulkActionsPanel.TestRunsBulkActionsEnum.ASSIGN_TO_MILESTONE.getItemValue() + "' isn't appear");

        softAssert.assertAll();
    }
}
