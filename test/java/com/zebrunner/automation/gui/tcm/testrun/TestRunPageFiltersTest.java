package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class TestRunPageFiltersTest extends TcmLogInBase {

    private Project project;
    private User createdUserForAssign;
    private TestSuite testSuite;
    private List<TestCase> createdTestCases;
    private TestRun testRun;
    private TestRunSettings testRunSettings;

    @BeforeClass
    public void preparation() {
        project = super.getCreatedProject();
        createdUserForAssign = usersService.create(usersService.generateRandomUser());

        testSuite = tcmService.createTestSuite(project.getId(), new TestSuite("Suite for automation " + RandomStringUtils.randomNumeric(5)));
        createdTestCases = tcmService.createTestCases(project.getId(), testSuite.getId(), 2);

        TestCase deprecatedTestCase = tcmService.createTestCase(project.getId(), testSuite.getId());
        deprecatedTestCase.setDeprecated(true);

        deprecatedTestCase = tcmService.updateTestCase(project.getId(), deprecatedTestCase.getId(), deprecatedTestCase);
        createdTestCases.add(deprecatedTestCase);

        testRun = tcmService.createTestRun(project.getId(), createdTestCases, TestRun.createWithRandomName());
        testRunSettings = tcmService.getTestRunSettings(project.getId());

        tcmService.addTestRunResults(project.getId(), testRun, createdTestCases.get(0), testRunSettings, "Passed");
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedData() {
        tcmService.deleteTestSuite(project.getId(), testSuite.getId());
        usersService.deleteUserById(createdUserForAssign.getId());
    }

    @Test
    @TestCaseKey({"ZTP-2943", "ZTP-2945"})
    public void userIsAbleToUseCheckboxOptions() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        String untestedOption = "Untested";
        String assignedToMeOption = "Assigned to me";
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        Menu checkboxOptions = testRunPage.clickCheckboxArrow();
        softAssert.assertFalse(checkboxOptions.getMenuItem().isEmpty(),
                "There are no available options in list after click checkbox arrow");

        checkboxOptions.findItem(untestedOption).click();
        TestRunSuiteItem suite = testRunPage.getTestSuite(testSuite.getTitle());

        // ZTP-2943 User is able to select only untested test cases via checkbox
        softAssert.assertFalse(suite.getTestCase(createdTestCases.get(0).getTitle()).isSelected(),
                "Checkbox is clicked on 'Passed' test case after selecting '" + untestedOption + "' option");
        softAssert.assertTrue(suite.getTestCase(createdTestCases.get(1).getTitle()).isSelected(),
                "Checkbox isn't clicked on untested test case after selecting '" + untestedOption + "' option");
        softAssert.assertTrue(suite.getTestCase(createdTestCases.get(2).getTitle()).isSelected(),
                "Checkbox isn't clicked on deprecated test case after selecting '" + untestedOption + "' option");

        testRunPage.clickClearSelectionCheckboxButton();

        for (int i = 0; i < 3; i++) {
            softAssert.assertFalse(suite.getTestCase(createdTestCases.get(i).getTitle()).isSelected(),
                    "Checkbox still clicked after clearing selection for test case " + i);
        }

        checkboxOptions = testRunPage.clickCheckboxArrow();
        checkboxOptions.findItem(assignedToMeOption).click();

        // ZTP-2945 User is able to select only assigned to me test cases via checkbox
        softAssert.assertTrue(suite.getTestCase(createdTestCases.get(0).getTitle()).isSelected(),
                "Checkbox isn't clicked on assigned to me 'Passed' test case " +
                        "after selecting '" + assignedToMeOption + "' option");
        softAssert.assertFalse(suite.getTestCase(createdTestCases.get(1).getTitle()).isSelected(),
                "Checkbox is clicked on NOT assigned to me test case after " +
                        "selecting '" + assignedToMeOption + "' option");
        softAssert.assertFalse(suite.getTestCase(createdTestCases.get(2).getTitle()).isSelected(),
                "Checkbox is clicked on NOT assigned to me deprecated test case after " +
                        "selecting '" + assignedToMeOption + "' option");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5519")
    public void verifyDeprecatedFilter() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        testRunPage.openFiltersList().selectDeprecatedFilterItem("No");
        softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(0).getTitle()),
                "NOT deprecated case isn't visible with option 'No'");
        softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(1).getTitle()),
                "NOT deprecated case isn't visible with option 'No'");
        softAssert.assertFalse(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(2).getTitle()),
                "Deprecated test case still visible after selecting option 'No'");

        testRunPage.clickResetFiltersButton();
        testRunPage.openFiltersList().selectDeprecatedFilterItem("Yes");
        softAssert.assertFalse(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(0).getTitle()),
                "NOT deprecated case is visible with option 'Yes'");
        softAssert.assertFalse(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(1).getTitle()),
                "NOT deprecated case is visible with option 'Yes'");
        softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(2).getTitle()),
                "Deprecated test case isn't visible after selecting option 'Yes'");

        testRunPage.clickResetFiltersButton();
        testRunPage.openFiltersList().selectDeprecatedFilterItem("Any");
        softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(0).getTitle()),
                "NOT deprecated case isn't visible with option 'Any'");
        softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(1).getTitle()),
                "NOT deprecated case isn't visible with option 'Any'");
        softAssert.assertTrue(testRunPage.getTestSuite(testSuite.getTitle())
                        .isTestCaseVisible(createdTestCases.get(2).getTitle()),
                "Deprecated test case isn't visible after selecting option 'Any'");

        softAssert.assertAll();
    }
}
