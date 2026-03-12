package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.reporting.test.TestCardResultDetails;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageHeader;
import com.zebrunner.automation.gui.reporting.test.TestNavigation;
import com.zebrunner.automation.gui.reporting.launch.CardUpdateModalR;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.FailureTagModal;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.common.SelectWrapperMenu;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Maintainer("Gmamaladze")
public class TestNavigationTest extends LogInBase {

    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private TestClassLaunchDataStorage testClassLaunchDataStorage;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }


    // ---------------------------------- Navigation Methods -------------------------------------//


    private int getCurrentIndex(List<TestExecution> allTestCases, String currentTestName) {
        for (int i = 0; i < allTestCases.size(); i++) {
            if (allTestCases.get(i).getName().equals(currentTestName)) {
                return i;
            }
        }
        return -1; // Current test method not found
    }

    public String getNextTestMethodName(List<TestExecution> allTestCases, String currentTestName) {
        int currentIndex = getCurrentIndex(allTestCases, currentTestName);

        if (currentIndex != -1 && currentIndex < allTestCases.size() - 1) {
            return allTestCases.get(currentIndex + 1).getName();
        } else {
            return null; // No next test method found
        }
    }

    public String getPreviousTestMethodName(List<TestExecution> allTestCases, String currentTestName) {
        int currentIndex = getCurrentIndex(allTestCases, currentTestName);

        if (currentIndex > 0) {
            return allTestCases.get(currentIndex - 1).getName();
        } else {
            return null; // No previous test method found
        }
    }

    public String getNextNonSuccessfullyTestName(List<TestExecution> allTestCases, String currentTestName) {
        int currentIndex = getCurrentIndex(allTestCases, currentTestName);
        if (currentIndex != -1) {
            for (int i = currentIndex + 1; i < allTestCases.size(); i++) {
                if (Objects.equals(allTestCases.get(i).getStatus(), TestExecution.Status.FAILED.getStatus())) {
                    return allTestCases.get(i).getName();
                }
            }
        }
        return null;
    }

    public String getPreviousNonSuccessfullyTestName(List<TestExecution> allTestCases, String currentTestName) {
        int currentIndex = getCurrentIndex(allTestCases, currentTestName);
        if (currentIndex != -1) {
            for (int i = currentIndex - 1; i >= 0; i--) {
                if (Objects.equals(allTestCases.get(i).getStatus(), TestExecution.Status.FAILED.getStatus())) {
                    return allTestCases.get(i).getName();
                }
            }
        }
        return null; // No previous non-successful test method found
    }

    //----------------------------------Test--------------------------------------------//

    @Test(groups = {"min_acceptance"})
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4629")
    public void switchBetweenTestsByArrowsTest() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 3, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> allTests = testClassLaunchDataStorage.getTestsList();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(allTests.get(0).getName());
        resultTestMethodCard.clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestNavigation testNavigation = testDetailsPage.getPageHeader().getTestNavigation();

        String initialTestTitle = allTests.get(0).getName();

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), initialTestTitle,
                "Test details page wast not opened");

        testNavigation.clickNextTestButton();

        String currentTestTitle = getNextTestMethodName(allTests, initialTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to next one");

        testNavigation.clickPreviousTestButton();

        currentTestTitle = getPreviousTestMethodName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to previous one");

        testNavigation.clickNextNonSuccessfullyTestButton();

        currentTestTitle = getNextNonSuccessfullyTestName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to non successfully one");

        testNavigation.clickNextNonSuccessfullyTestButton();

        currentTestTitle = getNextNonSuccessfullyTestName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to next non successfully one");

        testNavigation.clickPreviousNonSuccessfullyTestButton();

        currentTestTitle = getPreviousNonSuccessfullyTestName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to previous non successfully one");

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4630")
    public void switchBetweenTestsByKeyBoardTest() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 3, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> allTests = testClassLaunchDataStorage.getTestsList();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(allTests.get(0).getName());
        resultTestMethodCard.clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());

        String initialTestTitle = allTests.get(0).getName();

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), initialTestTitle,
                "Test details page wast not opened");

        PageUtil.clickOnKeyBoard(TestNavigation.NEXT_TEST_KEYBOARD_KEY, getDriver());

        String currentTestTitle = getNextTestMethodName(allTests, initialTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to next one");

        PageUtil.clickOnKeyBoard(TestNavigation.PREVIOUS_TEST_KEYBOARD_KEY, getDriver());

        currentTestTitle = getPreviousTestMethodName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to previous one");

        PageUtil.clickOnKeyBoard(TestNavigation.NEXT_NON_SUCCESSFULLY_TEST_KEY, getDriver());

        currentTestTitle = getNextNonSuccessfullyTestName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to non successfully one");

        PageUtil.clickOnKeyBoard(TestNavigation.NEXT_NON_SUCCESSFULLY_TEST_KEY, getDriver());

        currentTestTitle = getNextNonSuccessfullyTestName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to next non successfully one");

        PageUtil.clickOnKeyBoard(TestNavigation.PREVIOUS_NON_SUCCESSFULLY_TEST_KEY, getDriver());

        currentTestTitle = getPreviousNonSuccessfullyTestName(allTests, currentTestTitle);

        softAssert.assertEquals(testDetailsPage.getPageHeader().getTestTitleText(), currentTestTitle,
                "Test should be changed to previous non successfully one");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4631")
    public void controlDisabledWithNoNextPrevTests() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 3, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> allTests = testClassLaunchDataStorage.getTestsList();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        String firstTestName = allTests.get(0).getName();

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(firstTestName);
        resultTestMethodCard.clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestDetailsPageHeader testDetailsPageHeader = testDetailsPage.getPageHeader();
        TestNavigation testNavigation = testDetailsPageHeader.getTestNavigation();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), firstTestName,
                "Test details page wast not opened");
        softAssert.assertFalse(testNavigation.isPreviousTestButtonClickable(),
                "Previous test button should be disabled");
        softAssert.assertFalse(testNavigation.isPreviousNonSuccessfullyTestButtonClickable(),
                "Previous Non Successfully test button should be disabled");

        getDriver().navigate().back();

        String lastTestName = allTests.get(allTests.size() - 1).getName();
        testRunResultPage.getCertainTest(lastTestName).clickOnCard();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), lastTestName,
                "Test details page wast not opened");
        softAssert.assertFalse(testNavigation.isNextTestButtonClickable(),
                "Next test button should be disabled");
        softAssert.assertFalse(testNavigation.isNextNonSuccessfullyTestButtonClickable(),
                "Next Non Successfully test button should be disabled");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4632")
    public void navigationWithinGroupsAndFiltersTest() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 3, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> failedTests = testClassLaunchDataStorage.getFailedTests();
        List<TestExecution> passedTests = testClassLaunchDataStorage.getPassedTests();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        softAssert.assertTrue(actionsBlockR.isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_PASSED),
                "Status should be 'PASSED'");

        List<ResultTestMethodCardR> updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.PASSED_TEST_CARD.getHexColor(),
                    "Passed test should be displayed - colour of left card border should be green");
        }

        updatedTestCards.get(0).clickOnCard();
        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestDetailsPageHeader testDetailsPageHeader = testDetailsPage.getPageHeader();
        TestNavigation testNavigation = testDetailsPageHeader.getTestNavigation();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), passedTests.get(0).getName(),
                "Test details page wast not opened");

        for (int i = 0; i < passedTests.size() - 1; i++) {
            testNavigation.clickNextTestButton();
            softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), passedTests.get(i + 1).getName(),
                    "Test should switched to another one");
        }

        softAssert.assertFalse(testNavigation.isNextTestButtonClickable(),
                "Next test button shouldn't be active - Filtered status cases should be switched to last one");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(testRunResultPage.isPageOpened(), "Test run result page should be opened");

        actionsBlockR.clickResetButton();
        actionsBlockR.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE);
        testRunResultPage.clickSelectedGroupTable();

        updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "Failed test should be displayed - colour of left card border should be red");
        }

        updatedTestCards.get(0).clickOnCard();
        testDetailsPageHeader = new TestDetailsPageR(getDriver()).getPageHeader();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), failedTests.get(0).getName(),
                "Test details page wast not opened");

        for (int i = 0; i < failedTests.size() - 1; i++) {
            testNavigation.clickNextTestButton();
            softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), failedTests.get(i + 1).getName(),
                    "Test should switched to another one");
        }

        softAssert.assertFalse(testNavigation.isNextTestButtonClickable(),
                "Next test button shouldn't be active - Filtered group cases should be switched to last one");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4633")
    public void verifyLastItemConsistencyAfterPaginationNavigationTest() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithRandomTestStatuses(project.getKey(), 11);
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> allTests = testClassLaunchDataStorage.getTestsList();

        String lastTestName = allTests.get(10).getName();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.getPagination().selectTenItems();

        softAssert.assertEquals(actionsBlockR.getPagination().getPages(), "1–10 of 11", "Pagination should be 10");
        softAssert.assertFalse(testRunResultPage.isTestPresent(lastTestName), "The 11th test shouldn't be on page");

        ResultTestMethodCardR firstTestCard = testRunResultPage.getCertainTest(allTests.get(0).getName());
        firstTestCard.clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestDetailsPageHeader testDetailsPageHeader = testDetailsPage.getPageHeader();
        TestNavigation testNavigation = testDetailsPageHeader.getTestNavigation();

        for (int i = 0; i < allTests.size() - 1; i++) {
            testNavigation.clickNextTestButton();
            softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), allTests.get(i + 1).getName(),
                    "Test should switched to another one");
        }

        softAssert.assertFalse(testNavigation.isNextTestButtonClickable(),
                "Next test button shouldn't be active - test should be last one");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(testRunResultPage.isPageOpened(), "Test run result page should be opened");
        softAssert.assertTrue(testRunResultPage.isTestPresent(lastTestName),
                "Test Run result page should contain test - " + lastTestName);

        for (int i = 0; i < allTests.size() - 1; i++) {
            softAssert.assertFalse(testRunResultPage.isTestPresent(allTests.get(i).getName()),
                    "Test case _ " + lastTestName + " shouldn't be present. only last one should be in this page");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4634")
    public void testQuantityVerificationAfterChangingTestStatusAndReloadingPage() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 3, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> passedTests = testClassLaunchDataStorage.getPassedTests();
        List<TestExecution> failedTests = testClassLaunchDataStorage.getFailedTests();

        final String EXPECTED_TAG = "BUSINESS ISSUE";

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        List<ResultTestMethodCardR> failedTestCards = testRunResultPage.getFailedTestCards();

        for (ResultTestMethodCardR testCard : failedTestCards) {
            if (!testCard.isDefaultTagSelected()) {
                testCard.selectDefaultTag();
                testCard.hoverCard();
                testRunResultPage.waitPopupDisappears();
            }
        }

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        softAssert.assertTrue(actionsBlockR.isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_PASSED),
                "Status should be 'PASSED'");

        List<ResultTestMethodCardR> updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.PASSED_TEST_CARD.getHexColor(),
                    "Passed test should be displayed - colour of left card border should be green");
        }

        String testName = updatedTestCards.get(0).getCardTitleText();
        updatedTestCards.get(0).clickOnCard();
        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestDetailsPageHeader testDetailsPageHeader = testDetailsPage.getPageHeader();
        TestNavigation testNavigation = testDetailsPageHeader.getTestNavigation();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), testName, "Test details page was not opened");
        softAssert.assertEquals(testNavigation.getQuantityOfTests(), String.format("1 of %s", passedTests.size()), "Quantity of passed tests is not as expected !");

        testDetailsPageHeader.clickMarkAsFiled();
        new CardUpdateModalR(getDriver()).submit();

        softAssert.assertTrue(
                testDetailsPage.waitIsPopUpMessageAppear("Test was marked as failed"),
                "Popup is not as expected.");
        softAssert.assertEquals(testDetailsPage.getTestHeader().getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                "Test card should be failed - color left border should be red");
        softAssert.assertEquals(testNavigation.getQuantityOfTests(), String.format("1 of %s", passedTests.size()), "Quantity of tests is not as expected " +
                "- it should be same as passed test quantity !");

        getDriver().navigate().refresh();
        pause(4);

        softAssert.assertEquals(testDetailsPage.getTestHeader().getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                "Test card should be failed - color left border should be red");
        softAssert.assertEquals(testNavigation.getQuantityOfTests(), String.format("1 of %s", passedTests.size()), "Quantity of tests is not as expected after reload" +
                "- it should be same as passed test quantity !");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(testRunResultPage.isPageOpened(), "Test run result page should be opened");

        actionsBlockR.clickResetButton();
        actionsBlockR.openSelectFailureTagAndClose(Menu.MenuItemEnum.UNCATEGORIZED);

        softAssert.assertTrue(actionsBlockR.isFailureTagSelected(Menu.MenuItemEnum.UNCATEGORIZED),
                "Failure tag should be 'Uncategorized'");

        updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "Failed test should be displayed - colour of left card border should be red");
            softAssert.assertTrue(updatedTest.isDefaultTagSelected(),
                    "Failed test should be displayed with tag - UNCATEGORIZED");
        }

        testName = updatedTestCards.get(0).getCardTitleText();
        updatedTestCards.get(0).clickOnCard();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), testName, "Test details page was not opened");
        softAssert.assertEquals(testNavigation.getQuantityOfTests(), String.format("1 of %s", failedTests.size()), "Quantity of Uncategorized tests is not as expected !");

        TestCardResultDetails testCardResultDetails = testDetailsPage.getTestCardResultDetails();
        FailureTagModal failureTagModal = testCardResultDetails.getFailureTagModal();
        failureTagModal.clickBusinessIssueTagButton();
        failureTagModal.clickSaveButton();

        softAssert.assertTrue(
                testDetailsPage.waitIsPopUpMessageAppear(MessageEnum.FAILURE_TAG_WAS_SUCCESSFULLY_ASSIGNED.getDescription()),
                "Popup is not as expected.");
        softAssert.assertEquals(testCardResultDetails.getFailureTagText(), EXPECTED_TAG,
                "Test card tag should be - " + EXPECTED_TAG);
        softAssert.assertEquals(testNavigation.getQuantityOfTests(), String.format("1 of %s", failedTests.size()), "Quantity of tests is not as expected !");

        getDriver().navigate().refresh();
        pause(4);

        softAssert.assertEquals(testCardResultDetails.getFailureTagText(), EXPECTED_TAG,
                "Test card tag should be - " + EXPECTED_TAG);
        softAssert.assertEquals(testNavigation.getQuantityOfTests(), String.format("1 of %s", failedTests.size()), "Quantity is not as expected !");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4635")
    public void verifyTestFiltersStateAfterReturningFromTestDetailsPage() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 3, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> passedTests = testClassLaunchDataStorage.getPassedTests();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        List<ResultTestMethodCardR> failedTestCards = testRunResultPage.getFailedTestCards();

        for (ResultTestMethodCardR testCard : failedTestCards) {
            if (!testCard.isDefaultTagSelected()) {
                testCard.selectDefaultTag();
                testCard.hoverCard();
                testRunResultPage.waitPopupDisappears();
            }
        }

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        softAssert.assertTrue(actionsBlockR.isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_PASSED),
                "Status should be 'PASSED'");

        List<ResultTestMethodCardR> updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.PASSED_TEST_CARD.getHexColor(),
                    "Passed test should be displayed - colour of left card border should be green");
        }

        String testName = updatedTestCards.get(0).getCardTitleText();
        updatedTestCards.get(0).clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestDetailsPageHeader testDetailsPageHeader = testDetailsPage.getPageHeader();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), testName, "Test details page was not opened");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(actionsBlockR.isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_PASSED),
                "Status should be 'PASSED'");

        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.PASSED_TEST_CARD.getHexColor(),
                    "Passed test should still be displayed - colour of left card border should be green");
        }

        actionsBlockR.clickResetButton();
        actionsBlockR.openSelectFailureTagAndClose(Menu.MenuItemEnum.UNCATEGORIZED);

        softAssert.assertTrue(actionsBlockR.isFailureTagSelected(Menu.MenuItemEnum.UNCATEGORIZED),
                "Failure tag should be 'Uncategorized'");

        updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "Failed test should be displayed - colour of left card border should be red");
            softAssert.assertTrue(updatedTest.isDefaultTagSelected(),
                    "Failed test should be displayed with tag - UNCATEGORIZED");
        }

        testName = updatedTestCards.get(0).getCardTitleText();
        updatedTestCards.get(0).clickOnCard();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), testName, "Test details page was not opened");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(actionsBlockR.isFailureTagSelected(Menu.MenuItemEnum.UNCATEGORIZED),
                "Failure tag should be 'Uncategorized'");

        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "Failed test should still be displayed - colour of left card border should be red");
            softAssert.assertTrue(updatedTest.isDefaultTagSelected(),
                    "Failed test should still be displayed with tag - UNCATEGORIZED");
        }

        actionsBlockR.clickResetButton();
        actionsBlockR.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE);

        softAssert.assertTrue(actionsBlockR.isGroupSelected(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE),
                "Group should be 'Failure'");

        testRunResultPage.clickSelectedGroupTable();

        updatedTestCards = testRunResultPage.getTestCards();
        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "Failed test should be displayed - colour of left card border should be red");
        }

        testName = updatedTestCards.get(0).getCardTitleText();
        updatedTestCards.get(0).clickOnCard();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), testName, "Test details page was not opened");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(actionsBlockR.isGroupSelected(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE),
                "Group should be 'Failure'");

        testRunResultPage.clickSelectedGroupTable();

        for (ResultTestMethodCardR updatedTest : updatedTestCards) {
            softAssert.assertEquals(updatedTest.getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "Failed test should still be displayed  - colour of left card border should be red");
        }

        actionsBlockR.clickResetButton();
        actionsBlockR.openAndSelectSort(SelectWrapperMenu.WrapperItemEnum.SORT_STATUS);

        softAssert.assertTrue(actionsBlockR.isSortSelected(SelectWrapperMenu.WrapperItemEnum.SORT_STATUS),
                "Sort should be 'Status'");

        updatedTestCards = testRunResultPage.getTestCards();
        for (int i = 0; i < updatedTestCards.size() - passedTests.size(); i++) {
            softAssert.assertEquals(updatedTestCards.get(i).getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "First top should be failed test - colour of left card border should be red");
        }

        testName = updatedTestCards.get(0).getCardTitleText();
        updatedTestCards.get(0).clickOnCard();

        softAssert.assertEquals(testDetailsPageHeader.getTestTitleText(), testName, "Test details page was not opened");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());

        softAssert.assertTrue(actionsBlockR.isSortSelected(SelectWrapperMenu.WrapperItemEnum.SORT_STATUS),
                "Sort should be 'Status'");

        for (int i = 0; i < updatedTestCards.size() - passedTests.size(); i++) {
            softAssert.assertEquals(updatedTestCards.get(i).getLeftCardBorderColor(), ColorEnum.FAILED_TEST_CARD.getHexColor(),
                    "First top should still be failed test - colour of left card border should be red");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4636")
    public void testControlNotDisplayedWithSingleTest() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        List<TestExecution> allTests = testClassLaunchDataStorage.getTestsList();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(allTests.get(0).getName());
        testCard.clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        TestNavigation testNavigation = testDetailsPage.getPageHeader().getTestNavigation();

        softAssert.assertFalse(testNavigation.isTestNavigationPresent(),
                "Test navigation 'Switcher' shouldn't be present, test should be only one");

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        softAssert.assertTrue(actionsBlockR.isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_PASSED),
                "Status should be 'PASSED'");

        List<ResultTestMethodCardR> updatedTestCards = testRunResultPage.getTestCards();

        softAssert.assertEquals(updatedTestCards.size(), 1, "Only 1 test card should be in list");

        updatedTestCards.get(0).clickOnCard();

        softAssert.assertFalse(testNavigation.isTestNavigationPresent(),
                "Test navigation 'Switcher' shouldn't be present, test should be only one");

        testDetailsPage.clickBreadcrumb(testClassLaunchDataStorage.getLaunch().getName());
        actionsBlockR.clickResetButton();
        actionsBlockR.openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum.GROUP_FAILURE);
        testRunResultPage.clickSelectedGroupTable();

        updatedTestCards = testRunResultPage.getTestCards();

        softAssert.assertEquals(updatedTestCards.size(), 1, "Only 1 test card should be in list");

        updatedTestCards.get(0).clickOnCard();

        softAssert.assertFalse(testNavigation.isTestNavigationPresent(),
                "Test navigation 'Switcher' shouldn't be present, test should be only one");

        softAssert.assertAll();
    }
}