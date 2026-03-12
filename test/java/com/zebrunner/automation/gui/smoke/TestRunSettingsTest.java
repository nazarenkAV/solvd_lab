package com.zebrunner.automation.gui.smoke;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.automation.gui.reporting.launch.DeleteTestRunModal;
import com.zebrunner.automation.gui.reporting.launch.MarkAsReviewedModal;
import com.zebrunner.automation.gui.reporting.launch.CardUpdateModalR;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.BulkActionSection;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.carina.webdriver.DriverHelper;

@Slf4j
@Maintainer("azarouski")
public class TestRunSettingsTest extends LogInBase {

    private final String env = "NEW";
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

    // ___________________Basic tests__________________

    /**
     * Test for testing all testrun card components
     */
    @Test
    public void mainElementsPresenceOnAutomationLaunchCardTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        testRunIdList.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard("TestRunV1", true);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(
                testRunCard.getStatus().getStatusColourFromCss(),
                TestRunStatusEnumR.FAILED, "Card status is wrong.");
        softAssert.assertFalse(
                testRunCard.isCheckBoxSelected(),
                "Checkbox selected == true, expected false.");
        testRunCard.getCheckBox().click();
        softAssert.assertTrue(
                testRunCard.isCheckBoxSelected(),
                "Checkbox selected == false, expected true.");
        testRunCard.getCheckBox().click();
        softAssert.assertFalse(
                testRunCard.isCheckBoxSelected(),
                "Checkbox selected == true, expected false.");
        softAssert.assertEquals(
                testRunCard.getTitleName().getText(),
                "TestRunV1", "Card name is wrong.");
        softAssert.assertEquals(
                testRunCard.getPlatform().getPlatformType(),
                PlatformTypeR.API, "Platform type is wrong.");

        softAssert.assertEquals(
                testRunCard.getStatistics().getPassed().getText(),
                "0", "Amount of passed tests is wrong.");
        softAssert.assertEquals(
                testRunCard.getStatistics().getFailedIssues(),
                "1", "Amount of failed tests is wrong.");
        softAssert.assertEquals(
                testRunCard.getStatistics().getKnownIssues(),
                "0", "Amount of known failed tests is wrong.");
        softAssert.assertEquals(
                testRunCard.getStatistics().getSkipped().getText(),
                "0", "Amount of skipped tests is wrong.");
        softAssert.assertTrue(
                testRunCard.getStateAndTimeFromStart().getText().matches("^Launched (\\d+) \\w+ ago by \\w+$"),
                "State/time of card is not as expected.");
        softAssert.assertTrue(
                testRunCard.getDuration().getText().matches("\\d+s"),
                "Duration is not as expected.");

        softAssert.assertAll();
    }

    // ___________________Functional tests_____________

    @TestCaseKey("ZTP-1269")
    @Test(groups = "min_acceptance")
    public void deleteTestRunTest() {
        WebDriver webDriver = super.getDriver();

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(0, 0, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());

        LaunchCard testRunCard = launchesPage.getCertainTestRunCard(
                testClassLaunchDataStorage.getLaunch().getName(), true
        );
        testRunCard.clickMenu().getDelete().click();

        DeleteTestRunModal deleteLaunchModal = new DeleteTestRunModal(webDriver);

        Assert.assertTrue(
                deleteLaunchModal.getDeleteButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Modal with 'Delete' button should be present!"
        );
        Assert.assertEquals(
                deleteLaunchModal.getModalBodyText().getText(),
                String.format("You are about to permanently delete the “%s“ launch, its tests and associated resources (such as screenshots and logs).\n" +
                                "Are you sure you want to proceed with this action?",
                        testClassLaunchDataStorage.getLaunch().getName()), "Warning message is not as expected!"
        );

        deleteLaunchModal.getDeleteButton().click();

        Assert.assertTrue(launchesPage.waitIsPopUpMessageAppear("Launch has been deleted"));
        Assert.assertFalse(
                launchesPage.isCertainTestRunCard(testClassLaunchDataStorage.getLaunch().getName()),
                String.format("The test run named '%s' must not be on the page!", testClassLaunchDataStorage.getLaunch()
                                                                                                            .getName())
        );
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1264"})
    public void testRunMarkAsReviewedTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(
                testClassLaunchDataStorage.getLaunch().getName(), true);
        testRunCard.clickMenu().getMarkAsReviewed().click();
        MarkAsReviewedModal markAsReviewedModal = new MarkAsReviewedModal(getDriver());
        markAsReviewedModal.getMessageForReview().sendKeys("New comment for project with key" + project.getKey(),
                false,
                false);
        markAsReviewedModal.getSubmitButton().click();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                automationLaunchesPage.waitIsPopUpMessageAppear("Launch has been marked as reviewed"),
                "Popup is not as expected.");

        softAssert.assertTrue(
                testRunCard.getReviewedBadge().isStateMatches(Condition.VISIBLE),
                "Reviewed icon should be present on card!");

        //        softAssert.assertTrue(
        //                testRunCard.getCommentsButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
        //                "Comment icon should be present on card!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1196")
    public void testRunOpenInNewTabTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage
                .getCertainTestRunCard(testClassLaunchDataStorage.getLaunch().getName(), true);
        testRunCard.clickMenu().getOpenInNewTab().click();
        pause(2);

        List<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs.get(1));

        TestRunResultPageR testRunResultPage = TestRunResultPageR.getPageInstance(getDriver());

        SoftAssert softAssert = new SoftAssert();

        testRunResultPage.assertPageOpened();

        softAssert.assertEquals(testRunResultPage.getTitle(), testClassLaunchDataStorage.getLaunch().getName(),
                "testRunResultPage title not as expected");

        AutomationLaunchesPage testRunsPage1 = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        testRunsPage1.assertPageOpened();

        String windowsHandle = testRunsPage1.getDriver().getWindowHandle();
        LaunchCard testRunCard1 = testRunsPage1.getCertainTestRunCard(
                testClassLaunchDataStorage.getLaunch().getName(), true);
        testRunCard1.getRootExtendedElement().click();
        TestRunResultPageR testRunResultPageFromProjectGrid = TestRunResultPageR.getPageInstance(getDriver());

        testRunResultPageFromProjectGrid.assertPageOpened();

        String windowsHandleAfterLinks = testRunResultPageFromProjectGrid.getDriver().getWindowHandle();

        softAssert.assertEquals(windowsHandle, windowsHandleAfterLinks,
                "Page should open in the same tab!");

        getDriver().close();
        pause(1);
        getDriver().switchTo().window(tabs.get(0));

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1263")
    public void z_copyTestRunLinkTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(
                testClassLaunchDataStorage.getLaunch().getName(), true);

        testRunCard.clickMenu().getCopyLink().click();
        String actualLink = new DriverHelper(getDriver()).getClipboardText();

        Assert.assertFalse(actualLink.isEmpty(), "Link shouldn't be empty!");

        String firstTab = getDriver().getWindowHandle();
        getDriver().switchTo().newWindow(WindowType.TAB).get(actualLink);
        pause(4);

        TestRunResultPageR testRunResultPage = TestRunResultPageR.getPageInstance(getDriver());

        SoftAssert softAssert = new SoftAssert();
        testRunResultPage.assertPageOpened();
        softAssert.assertEquals(testRunResultPage.getTitle(),
                testClassLaunchDataStorage.getLaunch().getName(), "Title of opened page not as expected.");
        getDriver().close();
        pause(2);
        getDriver().switchTo().window(firstTab);
        softAssert.assertAll();

    }

    @Test(groups = "min_acceptance")
    @TestCaseKey("ZTP-1177")
    public void verifyTestRunStatusForOnlyPassedTests() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(2, 0, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(
                testClassLaunchDataStorage.getLaunch().getName(), true);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
                testRunCard.getStatus().getStatusColourFromCss(),
                TestRunStatusEnumR.PASSED, "Color of left border is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getPassed().getText(),
                "2", "Number of passed tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getFailedIssues(),
                "0", "Number of failed tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getSkipped().getText(),
                "0", "Number of skipped tests is not as expected!");

        testRunCard.getRootExtendedElement().click();
        TestRunResultPageR testRunResultPage = TestRunResultPageR.getPageInstance(getDriver());
        testRunResultPage.assertPageOpened();

        softAssert.assertEquals(testRunResultPage.getLeftBoardColorOfLaunchHeader(), TestRunStatusEnumR.PASSED.getColour(),
                "The color of the left launch header should be green");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1179")
    public void verifyTestRunStatusWithFailedTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(
                testClassLaunchDataStorage.getLaunch().getName(), true);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
                testRunCard.getStatus().getStatusColourFromCss(),
                TestRunStatusEnumR.FAILED, "Color of left border is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getPassed().getText(),
                "1", "Number of passed tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getFailedIssues(),
                "1", "Number of failed tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getSkipped().getText(),
                "0", "Number of skipped tests is not as expected!");

        testRunCard.getRootExtendedElement().click();
        TestRunResultPageR testRunResultPage = TestRunResultPageR.getPageInstance(getDriver());
        testRunResultPage.assertPageOpened();

        softAssert.assertEquals(testRunResultPage.getLeftBoardColorOfLaunchHeader(), TestRunStatusEnumR.FAILED.getColour(),
                "The color of the left launch header should be red");

        softAssert.assertAll();
    }

    // ___________________Additional tests_____________

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1240", "ZTP-1241"})
    public void verifyTestMarkingAs() {
        WebDriver webDriver = super.getDriver();

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        String failedMethodName = testClassLaunchDataStorage.getFailedTests().get(0).getName();
        String passedMethodName = testClassLaunchDataStorage.getPassedTests().get(0).getName();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch()
                                                                                                                                              .getId());
        Assert.assertTrue(testRunResultPage.isPageOpened(), "Test run results page was not opened!");

        ResultTestMethodCardR passedMethod = testRunResultPage.getCertainTest(passedMethodName);
        passedMethod.getCardMenuR().getMarkAsFailed().click();
        new CardUpdateModalR(webDriver).getSubmitButton().click();

        Assert.assertEquals(
                testRunResultPage.getPopUp(),
                MessageEnum.TEST_WAS_MARKED_AS_FAILED.getDescription()
        );
        Assert.assertEquals(
                testRunResultPage.getPassedTestCards().size(), 0,
                "Number of passed test should be 0"
        );
        Assert.assertEquals(
                testRunResultPage.getFailedTestCards().size(), 2,
                "Number of failed test should be 2"
        );
        Assert.assertEquals(
                passedMethod.getLeftCardBorderColor(), "#df4150",
                "Color is not as expected!"
        );

        ResultTestMethodCardR failedMethod = testRunResultPage.getCertainTest(failedMethodName);
        failedMethod.getCardMenuR().getMarkAsPassed().click();
        new CardUpdateModalR(webDriver).getSubmitButton().click();

        Assert.assertEquals(testRunResultPage.getPopUp(), "Test was marked as PASSED");
        Assert.assertEquals(
                failedMethod.getLeftCardBorderColor(), "#aee2c8",
                "Color is not as expected!"
        );
        Assert.assertEquals(
                testRunResultPage.getPassedTestCards().size(), 1,
                "Number of passed test should be 1"
        );
        Assert.assertEquals(
                testRunResultPage.getFailedTestCards().size(), 1,
                "Number of failed test should be 1"
        );
    }

    @Test
    @TestCaseKey({"ZTP-1249", "ZTP-1250"})
    public void testMarkAsPassedAndFailedTestViaCheckBox() {
        WebDriver webDriver = super.getDriver();

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(0, 2, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        Assert.assertEquals(
                testRunResultPage.getPassedTestCards().size(), 0,
                "Number of passed test should be 0"
        );

        for (ResultTestMethodCardR failedTest : testRunResultPage.getFailedTestCards()) {
            if (!failedTest.isCardCheckboxChecked()) {
                failedTest.getCardTitle().waitUntil(Condition.PRESENT_AND_CLICKABLE);
                failedTest.clickCardCheckbox();

                Assert.assertTrue(failedTest.isCardCheckboxChecked(), "Failed test was not checked");
            }
        }

        Assert.assertFalse(
                testRunResultPage.getActionsBlockR().getBulkActionSection().isMarkAsFailedClickable(),
                "'Mark as Failed' button should not be clickable"
        );

        testRunResultPage.getActionsBlockR().getBulkActionSection().clickMarkAsPassedButton();
        new CardUpdateModalR(webDriver).submit();

        Assert.assertEquals(
                testRunResultPage.getPassedTestCards().size(), 2,
                "Number of passed test should be 2"
        );
        Assert.assertEquals(
                testRunResultPage.getFailedTestCards().size(), 0,
                "Number of failed test should be 0"
        );
        Assert.assertTrue(
                testRunResultPage.getLeftBoardColorOfLaunchHeader().equals("#44c480")
                        || testRunResultPage.getLeftBoardColorOfLaunchHeader().equals("#aee2c8"),
                "The color of the left launch header should be green"
        );

        for (ResultTestMethodCardR passedTest : testRunResultPage.getPassedTestCards()) {
            passedTest.waitLeftCardBorderColor(ColorEnum.PASSED_TEST_CARD);

            Assert.assertTrue(
                    passedTest.getLeftCardBorderColor().equals("#44c480")
                            || passedTest.getLeftCardBorderColor().equals("#aee2c8"),
                    "Test card left border color should be green"
            );
        }

        for (ResultTestMethodCardR passedTest : testRunResultPage.getPassedTestCards()) {
            if (!passedTest.isCardCheckboxChecked()) {
                passedTest.getCardTitle().waitUntil(Condition.PRESENT_AND_CLICKABLE);
                passedTest.clickCardCheckbox();

                Assert.assertTrue(passedTest.isCardCheckboxChecked(), "Passed test was not checked");
            }
        }

        Assert.assertFalse(
                testRunResultPage.getActionsBlockR().getBulkActionSection().isMarkAsPassedClickable(),
                "'Mark as Passed' button should not be clickable"
        );

        testRunResultPage.getActionsBlockR().getBulkActionSection().clickMarkAsFailedButton();
        new CardUpdateModalR(webDriver).submit();

        Assert.assertEquals(
                testRunResultPage.getFailedTestCards().size(), 2,
                "Number of failed test should be 2"
        );
        Assert.assertEquals(
                testRunResultPage.getPassedTestCards().size(), 0,
                "Number of passed test should be 0"
        );
        Assert.assertEquals(
                testRunResultPage.getLeftBoardColorOfLaunchHeader(), "#df4150",
                "The color of the left launch header should be red"
        );

        for (ResultTestMethodCardR failedTest : testRunResultPage.getFailedTestCards()) {
            failedTest.waitLeftCardBorderColor(ColorEnum.FAILED_TEST_CARD);

            Assert.assertEquals(
                    failedTest.getLeftCardBorderColor(), "#df4150",
                    "Test card left border color should be red"
            );
        }
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1248")
    public void testCheckmarkFunctionality() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithRandomTestStatuses(project.getKey(), 9);
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        SoftAssert softAssert = new SoftAssert();

        List<TestExecution> allTests = testClassLaunchDataStorage.getTestsList();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        List<ResultTestMethodCardR> allTestCards = testRunResultPage.getTestCards();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.clickCheckbox();

        softAssert.assertTrue(actionsBlock.isCheckboxSelected(), "Check box should be selected");
        softAssert.assertTrue(actionsBlock.getBulkActionSection().isAmountOfSelectedTestsPresent(allTests.size()),
                "All test should be selected - number of selected test should be " + allTests.size());
        for (ResultTestMethodCardR testCard : allTestCards) {
            softAssert.assertTrue(testCard.isCardCheckboxChecked(), "All test should be selected - " +
                    "Card check box should be selected");
        }

        actionsBlock.clickCheckbox();

        softAssert.assertFalse(actionsBlock.isCheckboxSelected(), "Check box shouldn't be selected");
        softAssert.assertFalse(actionsBlock.isBulkActionSectionPresent(), "Bulk action section shouldn't be present");
        for (ResultTestMethodCardR testCard : allTestCards) {
            softAssert.assertFalse(testCard.isCardCheckboxChecked(), "All test shouldn't be selected - " +
                    "Card check box shouldn't be selected");
        }

        int amountOfSelectedTest = 3;

        for (int i = 0; i < amountOfSelectedTest; i++) {
            allTestCards.get(i).clickCardCheckbox();
            softAssert.assertTrue(allTestCards.get(i).isCardCheckboxChecked(), "Tests should be selected - " +
                    "Card check box should be selected");
        }
        softAssert.assertTrue(actionsBlock.getBulkActionSection().isAmountOfSelectedTestsPresent(amountOfSelectedTest),
                "Tests should be selected - number of selected test should be " + amountOfSelectedTest);

        for (int i = 0; i < amountOfSelectedTest; i++) {
            allTestCards.get(i).clickCardCheckbox();
        }

        softAssert.assertFalse(actionsBlock.isBulkActionSectionPresent(), "Bulk action section shouldn't be present");
        for (ResultTestMethodCardR testCard : allTestCards) {
            softAssert.assertFalse(testCard.isCardCheckboxChecked(), "All test shouldn't be selected - " +
                    "Card check box shouldn't be selected");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1253")
    public void markingAsAndLinkIssueAreUnavailableForMixedTestStatuses() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        List<ResultTestMethodCardR> allTestCards = testRunResultPage.getTestCards();

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.clickCheckbox();

        softAssert.assertTrue(actionsBlock.isCheckboxSelected(), "Check box should be selected");
        for (ResultTestMethodCardR testCard : allTestCards) {
            softAssert.assertTrue(testCard.isCardCheckboxChecked(), "All test should be selected - " +
                    "Card check box should be selected");
        }
        BulkActionSection bulkActionSection = actionsBlock.getBulkActionSection();

        softAssert.assertFalse(bulkActionSection.isMarkAsFailedClickable(), "'Mark as Failed' button shouldn't be clickable");
        softAssert.assertFalse(bulkActionSection.isMarkAsPassedClickable(), "'Mark as Passed' button shouldn't be clickable");
        softAssert.assertFalse(bulkActionSection.isLinkIssueButtonClickable(), "'Link issue' button shouldn't be clickable");
        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5638")
    public void verifyMultiplyTestSelectUsingShift() {
        SoftAssert softAssert = new SoftAssert();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithRandomTestStatuses(project.getKey(), 5);
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        List<ResultTestMethodCardR> allTestCards = testRunResultPage.getTestCards();

        Actions actions = new Actions(getDriver());

        actions.keyDown(Keys.SHIFT).perform();
        allTestCards.get(0).clickCardCheckbox();
        allTestCards.get(4).clickCardCheckbox();

        for (ResultTestMethodCardR testCard : allTestCards) {
            softAssert.assertTrue(testCard.isCardCheckboxChecked(), "Card check box should be checked");
        }

        actions.keyUp(Keys.SHIFT).perform();

        softAssert.assertAll();
    }
}