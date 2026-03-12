package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.CurrentTest;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.JiraProperties;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.reporting.launch.CardUpdateModalR;
import com.zebrunner.automation.gui.reporting.launch.CreateJiraIssueModal;
import com.zebrunner.automation.gui.reporting.launch.LinkIssueModal;
import com.zebrunner.automation.gui.reporting.launch.LinkedIssueCard;
import com.zebrunner.automation.gui.reporting.launch.SearchType;
import com.zebrunner.automation.gui.reporting.launch.BulkActionSection;
import com.zebrunner.automation.gui.reporting.launch.JiraIssueModal;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.external.JiraLogin;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Slf4j
@Maintainer("Gmamaladze")
public class JiraIssueTest extends LogInBase {

    public final String JIRA_BUG_TRACKER = "Jira";
    private final String EXPECTED_JIRA_PROJECT_NAME = "ZEB";
    private final List<String> EXPECTED_JIRA_ISSUE_TYPE_LIST = List.of(
            "Task",
            "Bug",
            "Story",
            "Test"
    );
    private final List<String> EXPECTED_PRIORITY_LIST = List.of(
            "Highest",
            "High",
            "Medium",
            "Low",
            "Lowest"
    );
    private final String EXISTING_JIRA_ISSUE_ID = "ZEB-133";
    private final String EXISTING_SECOND_JIRA_ISSUE_ID = "ZEB-187";
    private final String EXISTING_JIRA_ISSUE_TITLE = "Test issue for AQA tests";
    private final String EXPECTED_FLAG = "Impediment";
    private final List<Long> launchIds = new ArrayList<>();
    private Project project;
    private TestClassLaunchDataStorage testClassLaunchDataStorage;

    @BeforeClass
    public void getProject() {
        if (ConfigHelper.getJiraProperties().getEnabled()) {
            project = LogInBase.project;
            IntegrationManager.addIntegration(project.getId(), Tool.JIRA);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        launchIds.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    //----------------------------------Test--------------------------------------------//

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1361", "ZTP-1362"})
    public void linkIssuePopOpeningUpViaTestGridAndTestResultPage() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());

        //ZTP-1361
        softAssert.assertEquals(linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "Link issue modal was not opened - Title is not as expected !");

        linkIssueModal.clickClose();

        resultTestMethodCard.clickOnCard();
        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());

        linkIssueModal = testDetailsPage.getTestHeader().clickLinkIssueButton();

        //ZTP-1362
        softAssert.assertEquals(linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "Link issue modal was not opened - Title is not as expected !");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1363")
    public void markAsPassedFailedTestWithLinkedIssueViaTestRunResultPage() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");

        resultTestMethodCard.getCardMenuR().getMarkAsPassed().click();
        new CardUpdateModalR(getDriver()).getSubmitButton().click();

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.TEST_WAS_MARKED_AS_PASSED.getDescription()),
                "Popup message of successfully marking as passed is not appear");
        softAssert.assertEquals(resultTestMethodCard.getLeftCardBorderColor(), ColorEnum.PASSED_TEST_CARD.getHexColor(),
                "Test should be mark as passed - Left card border color should be green");
        softAssert.assertEquals(testRunResultPage.getLeftBoardColorOfLaunchHeader(), ColorEnum.PASSED.getHexColor(),
                "Test should be mark as passed - The color of header left board should be green");
        softAssert.assertFalse(resultTestMethodCard.isLinkedIssuePresent(), "Linked issue should be disappear");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1364")
    public void markAsPassedFailedTestWithLinkedIssueViaTestResultPage() {
        this.skipTestIfJiraNotEnabled();

        WebDriver webDriver = super.getDriver();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        TestRunResultPageR launchPage = new TestRunResultPageR(webDriver);
        launchPage = launchPage.openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR testCard = launchPage.getCertainTest(failedTest.getName());
        testCard.clickOnCard();

        TestDetailsPageR testPage = new TestDetailsPageR(webDriver);

        LinkIssueModal linkIssueModal = testPage.getTestHeader().clickLinkIssueButton();
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        Assert.assertTrue(
                testPage.waitIsPopUpMessageAppear("Issue was linked successfully"),
                "Popup message of successfully issue linking is not appear"
        );

        testPage.getPageHeader().clickMarkAsPassed();
        new CardUpdateModalR(webDriver).getSubmitButton().click();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(
                testPage.waitIsPopUpMessageAppear("Test was marked as passed"),
                "Popup message of successfully marking as passed is not appear"
        );
        softAssert.assertEquals(
                testPage.getTestHeader().getLeftCardBorderColor(),
                "#44c480",
                "Test should be mark as passed - Color of Left card border should be green"
        );
        softAssert.assertFalse(testPage.getTestHeader().isLinkedIssuePresent(), "Linked issue should be disappear");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1365")
    public void redirectionToJiraTicketAfterClickingJiraIssueButtonTest() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        final String EXPECTED_JIRA_ISSUE_LINK = "https://zebrunner.atlassian.net/browse/ZEB-133";
        final String EXPECTED_JIRA_BASE_URL = "zebrunner.atlassian.net";

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");

        resultTestMethodCard.clickLinkedIssueButton();

        JiraIssueModal jiraIssueModal = new JiraIssueModal(getDriver());

        softAssert.assertEquals(jiraIssueModal.getJiraIssueTitleLink(), EXPECTED_JIRA_ISSUE_LINK,
                "Jira issue title link is not as expected !");

        JiraLogin jiraLogin = jiraIssueModal.clickJiraIssueTitleLink();
        PageUtil.toOtherTabWithoutClosingFirstOne(getDriver());

        softAssert.assertTrue(jiraLogin.getUrl().contains(EXPECTED_JIRA_BASE_URL),
                "URL should contain '" + EXPECTED_JIRA_BASE_URL + "' !");
        softAssert.assertTrue(jiraLogin.isJiraLoginTitlePresent(), "Jira log in page was not opened");

        PageUtil.toOtherTab(getDriver());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1367")
    public void openStacktraceAfterLinkIssueTest() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");

        resultTestMethodCard.expandErrorStackTrace();

        softAssert.assertTrue(resultTestMethodCard.isStackTraceExpanded(), "Stacktrace should be opened");

        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-3834", "ZTP-1242"})
    public void verifyTestRunStatusWithLinkedIssueToFailedTest() {
        this.skipTestIfJiraNotEnabled();

        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        String testRunName = testClassLaunchDataStorage.getLaunch().getName();
        //        String methodName = launchStorage.getFailedTests().get(0).getMethodName();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getFailedTestCards().get(0);
        resultTestMethodCard.linkIssueViaTestCard(EXISTING_JIRA_ISSUE_ID);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(
                testRunResultPage.getLeftBoardColorOfLaunchHeader(),
                ColorEnum.PASSED.getHexColor(),
                "The color of the left launch header should be green");

        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(),
                EXISTING_JIRA_ISSUE_ID, "Jira ticket is not as expected");

        AutomationLaunchesPage testRunsPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        LaunchCard testRunCard = testRunsPage.getCertainTestRunCard(testRunName, true);

        softAssert.assertEquals(
                testRunCard.getStatus().getStatusColourFromCss().getColour(),
                ColorEnum.PASSED.getHexColor());
        softAssert.assertEquals(
                testRunCard.getStatistics().getPassed().getText(),
                "1", "Number of passed tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getFailedIssues(),
                "1", "Number of failed tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getKnownIssues(),
                "1", "Number of known issue tests is not as expected!");
        softAssert.assertEquals(
                testRunCard.getStatistics().getSkipped().getText(),
                "0", "Number of skipped tests is not as expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1379", "ZTP-1380", "ZTP-1383", "ZTP-1384", "ZTP-1385", "ZTP-1386", "ZTP-1387", "ZTP-1388", "ZTP-1389", "ZTP-1395",
            "ZTP-1396"})
    public void createNewIssueModalJiraProjectsAndIssueTypeTest() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        final String VIEW_IN_ZEBRUNNER_EXPECTED_LINK = String.format("[View in Zebrunner|%s" +
                        "/projects/%s/automation-launches/%d/tests/%s]", ConfigHelper.getTenantUrl(), project.getKey(),
                testClassLaunchDataStorage.getLaunch().getId(),
                failedTest.getId());

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);

        softAssert.assertEquals(linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "Link Issue Modal should be opened");

        softAssert.assertTrue(linkIssueModal.isCreateNewIssueButtonPresentAndClickable(),
                "Create a new issue button should be present and should be clickable"); // ZTP-1380

        linkIssueModal.clickCreateNewIssueButton();

        CreateJiraIssueModal createJiraIssueModal = new CreateJiraIssueModal(getDriver());

        softAssert.assertEquals(createJiraIssueModal.getModalTitleText(), CreateJiraIssueModal.MODAL_TITLE,
                "Create issue modal title is not as expected"); // ZTP-1379

        createJiraIssueModal.openJiraProjectListBox();

        softAssert.assertTrue(createJiraIssueModal.isJiraProjectPresentInList(EXPECTED_JIRA_PROJECT_NAME),
                String.format("%s project should be present in Jira Projects list box", EXPECTED_JIRA_PROJECT_NAME)); // ZTP-1383

        createJiraIssueModal.selectJiraProject(EXPECTED_JIRA_PROJECT_NAME);

        // From ZTP-1383 to ZTP-1387
        for (String jiraIssueType : EXPECTED_JIRA_ISSUE_TYPE_LIST) {
            createJiraIssueModal.openIssueTypeListBox();
            createJiraIssueModal.selectIssueType(jiraIssueType);

            softAssert.assertEquals(createJiraIssueModal.getIssueTypeText(), jiraIssueType,
                    String.format("Jira issue type _ %s should be selected", jiraIssueType));
            softAssert.assertEquals(createJiraIssueModal.getTextOfDescriptionInputField(),
                    VIEW_IN_ZEBRUNNER_EXPECTED_LINK,
                    "Incorrect ‘View to Zebrunner’ link"
            );
            softAssert.assertTrue(createJiraIssueModal.isSummaryInputFieldPresent(),
                    "Summary input field should be present");
        }

        String randomString = RandomStringUtils.randomAlphabetic(3) + RandomStringUtils.randomNumeric(3) +
                "[|]’~<!--@/$%^&#/()?>";
        createJiraIssueModal.typeSummary(randomString);
        createJiraIssueModal.typeDescription(randomString);

        String actualSummary = createJiraIssueModal.getSummaryText();
        String actualDescription = createJiraIssueModal.getTextOfDescriptionInputField();

        //ZTP-1395
        softAssert.assertEquals(actualSummary, randomString, "Summary is not as expected !");
        //ZTP-1396
        softAssert.assertEquals(actualDescription, VIEW_IN_ZEBRUNNER_EXPECTED_LINK + randomString,
                "Description is not as expected !");

        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        createJiraIssueModal.clickCancel();

        softAssert.assertEquals(linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "User should be redirected to Link issue modal"); // ZTP-1388 ZTP-1389

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1390", "ZTP-1391", "ZTP-1393", "ZTP-1394", "ZTP-1399", "ZTP-1402", "ZTP-1404",
            "ZTP-1405", "ZTP-1406", "ZTP-1407", "ZTP-1410"})
    public void createNewIssueWithMoreOptionsTest() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        LocalDate timeStamp = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String expectedStartDate = timeStamp.format(dateFormatter);

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);

        linkIssueModal.clickCreateNewIssueButton();

        CreateJiraIssueModal createJiraIssueModal = new CreateJiraIssueModal(getDriver());

        createJiraIssueModal.fillAlMandatoryFields(EXPECTED_JIRA_PROJECT_NAME, EXPECTED_JIRA_ISSUE_TYPE_LIST.get(0),
                RandomStringUtils.randomAlphabetic(5));

        createJiraIssueModal.clickMoreFieldsButton();

        //ZTP-1390
        softAssert.assertTrue(createJiraIssueModal.isAdditionalElementsPopUP(),
                "'More fields' pop up should be opened and additional fields should be present");
        softAssert.assertTrue(createJiraIssueModal.isLessFieldsButtonPresent(),
                "'More fields' pop up should be opened and 'Less fields' button should be present");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        createJiraIssueModal.clickLessFieldsButton();

        //ZTP-1391
        softAssert.assertFalse(createJiraIssueModal.isAdditionalElementsPopUP(),
                "Fields should be return to initial count");
        softAssert.assertTrue(createJiraIssueModal.isMoreFieldsButtonPresent(),
                "Fields should be return to initial count and 'More fields' button should be present");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        createJiraIssueModal.openAndSelectIssueType(EXPECTED_JIRA_ISSUE_TYPE_LIST.get(1));
        createJiraIssueModal.clickMoreFieldsButton();

        //ZTP-1393
        createJiraIssueModal.clickTimeStampCalendarLogo();

        softAssert.assertTrue(createJiraIssueModal.getCalendar().isCalendarOpened(),
                "Time Stamp Calendar should be opened");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        //ZTP-1394
        createJiraIssueModal.getCalendar().selectDate(timeStamp);

        softAssert.assertEquals(createJiraIssueModal.getSelectedTimeStampDate(), expectedStartDate,
                "Date was not selected properly");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        String randomNumeric = RandomStringUtils.randomNumeric(2);
        createJiraIssueModal.typeNumber(randomNumeric);

        String actualNumber = createJiraIssueModal.getNumber();

        //ZTP-1399
        softAssert.assertEquals(actualNumber, randomNumeric,
                "Number is not as expected !");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        //ZTP-1402
        String selectedPerson = createJiraIssueModal.selectAnyPersonStartsWith("a");

        softAssert.assertTrue(createJiraIssueModal.isPersonSelected(selectedPerson),
                "Person was not selected on People text field");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        //ZTP-1404
        String selectedReporter = createJiraIssueModal.selectAnyReporterStartWith("a");

        softAssert.assertEquals(selectedReporter, createJiraIssueModal.getReporter(),
                "Reporter was not selected");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        //ZTP-1405
        for (String priority : EXPECTED_PRIORITY_LIST) {
            createJiraIssueModal.openAndSelectPriority(priority);

            softAssert.assertEquals(priority, createJiraIssueModal.getPriority(),
                    String.format(" Priority _ %s should be selected", priority));
            softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                    "Create and link button should be clickable. mandatory fields are not filled");
        }

        //ZTP-1406
        createJiraIssueModal.openAndSelectFlag(EXPECTED_FLAG);
        createJiraIssueModal.closeFlagSelectBox();

        softAssert.assertEquals(EXPECTED_FLAG, createJiraIssueModal.getFlag(),
                String.format("Flag %s was not selected properly !", EXPECTED_FLAG));
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        //ZTP-1407
        String selectedLabel = createJiraIssueModal.selectAnyLabelStartsWith("t");

        softAssert.assertTrue(createJiraIssueModal.isLabelSelected(selectedLabel),
                "Label was not selected properly");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        //ZTP-1410
        String selectedAssignee = createJiraIssueModal.selectAnyAssigneeStartWith("a");

        softAssert.assertEquals(selectedAssignee, createJiraIssueModal.getAssignedPerson(),
                "Assigned person was not selected properly");
        softAssert.assertTrue(createJiraIssueModal.isCreateAndLinkButtonClickable(),
                "Create and link button should be clickable. mandatory fields are not filled");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1368")
    public void searchJiraIssueById() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);
        linkIssueModal.typeIdOrSummary(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(linkIssueModal.isTicketPresentInSuggestionList(EXISTING_JIRA_ISSUE_ID),
                "Jira issue was not found");
        linkIssueModal.selectSuggestionByIdOrSummary(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertEquals(linkIssueModal.getSelectedLinkIssueIdText(), EXISTING_JIRA_ISSUE_ID,
                "Jira ticket was not selected");
        softAssert.assertEquals(linkIssueModal.getBugTrackerText(), JIRA_BUG_TRACKER,
                "Bug tracker should be 'Jira'");

        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-1371")
    @Test(groups = "min_acceptance")
    public void jiraTicketUnlinkTest() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);

        linkIssueModal.typeAndSelectIdOrSummary(EXISTING_JIRA_ISSUE_ID);

        linkIssueModal.clickLinkIssueButton();

        LinkedIssueCard linkedIssueCard = linkIssueModal.findIssueCard(EXISTING_JIRA_ISSUE_ID, SearchType.BY_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(linkIssueModal.getColorOfLinkIssueButton(), ColorEnum.DISABLED_LINK_ISSUE_BUTTON.getHexColor(),
                "'Link issue' button color should be grey");
        softAssert.assertEquals(linkedIssueCard.getIssueTicketTitleText(),
                EXISTING_JIRA_ISSUE_TITLE, "Jira issue title should be displayed");
        softAssert.assertEquals(linkedIssueCard.getColorOfLinkUnlinkButton(),
                ColorEnum.ISSUE_UNLINK_BUTTON.getHexColor(), "Link/unlink issue button should be green");
        softAssert.assertEquals(linkedIssueCard.getTitleOfLinkUnlinkButton(),
                "Unlink", "Title of Link/unlink button should be 'Unlink'");
        softAssert.assertTrue(DateUtil.isTimeWithinTolerance(LocalTime.now(), linkedIssueCard.getCardDate(),
                1, DateTimeFormatter.ofPattern("HH:mm")), "Time difference exceeds tolerance.");

        linkedIssueCard.clickLinkUnlinkIssueButton();

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_UNLINKED.getDescription()),
                "Popup message of successfully issue unlinking is not appear");
        softAssert.assertEquals(linkedIssueCard.getIssueTicketTitleText(),
                EXISTING_JIRA_ISSUE_TITLE, "Jira issue title should be displayed");
        softAssert.assertEquals(linkedIssueCard.getColorOfLinkUnlinkButton(),
                ColorEnum.ISSUE_LINK_BUTTON_HOVER.getHexColor(), "Link/unlink issue button should be black");
        softAssert.assertEquals(linkedIssueCard.getTitleOfLinkUnlinkButton(),
                "Link", "Title of Link/unlink button should be 'Link'");

        linkIssueModal.clickClose();

        softAssert.assertFalse(
                resultTestMethodCard.isLinkedIssuePresent(), "Jira ticket should not be linked");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1377")
    public void linkJiraIssueById() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);
        linkIssueModal.typeAndSelectIdOrSummary(EXISTING_JIRA_ISSUE_ID);
        linkIssueModal.clickLinkIssueButton();

        softAssert.assertEquals(linkIssueModal.findIssueCard(EXISTING_JIRA_ISSUE_ID, SearchType.BY_ID)
                                              .getColorOfLinkUnlinkButton(),
                ColorEnum.ISSUE_UNLINK_BUTTON.getHexColor(), "Link/unlink issue button should be green");
        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");

        linkIssueModal.clickClose();

        softAssert.assertEquals(resultTestMethodCard.getLinkedIssueText(),
                EXISTING_JIRA_ISSUE_ID, "Jira ticket was not linked");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1378")
    public void linkJiraIssueByTicketTitle() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.clickLinkIssue();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);
        linkIssueModal.typeAndSelectIdOrSummary(EXISTING_JIRA_ISSUE_TITLE);
        linkIssueModal.clickLinkIssueButton();

        softAssert.assertEquals(linkIssueModal.findIssueCard(EXISTING_JIRA_ISSUE_TITLE, SearchType.BY_TITLE)
                                              .getColorOfLinkUnlinkButton(),
                ColorEnum.ISSUE_UNLINK_BUTTON.getHexColor(), "Link/unlink issue button should be green");
        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(resultTestMethodCard.getLinkedIssueText(),
                EXISTING_JIRA_ISSUE_ID, "Jira ticket was not linked");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1435")
    public void verifyFailureTagDonNotChangeAfterLinkIssueTest() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());

        final String INITIAL_FAILURE_TAG = resultTestMethodCard.getFailureTagText();

        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(),
                EXISTING_JIRA_ISSUE_ID, "Jira ticket is not as expected");
        softAssert.assertEquals(resultTestMethodCard.getFailureTagText(), INITIAL_FAILURE_TAG,
                "Tag was not same - Tag was changed");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1178")
    public void testRunIsMarkedAsPassedAfterLinkAllFailedTests() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(), EXISTING_JIRA_ISSUE_ID,
                "Jira ticket is not as expected");
        softAssert.assertEquals(testRunResultPage.getLeftBoardColorOfLaunchHeader(), ColorEnum.PASSED.getHexColor(),
                "The color of the left launch header should be green");

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.findTestRunCardByName(testClassLaunchDataStorage.getLaunch()
                                                                                                        .getName());

        softAssert.assertEquals(testRunCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.PASSED,
                "Test run should be marked as passed - left border color should be green");

        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-1243")
    @Test(groups = "min_acceptance")
    public void testKnownIssueRemainsAfterSecondRun() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.linkIssueViaTestCard(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(), EXISTING_JIRA_ISSUE_ID,
                "Jira ticket is not as expected");

        AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        TestExecution testExecution = TestExecution.builder()
                                                   .name(failedTest.getName())
                                                   .className(failedTest.getClassName())
                                                   .methodName(failedTest.getMethodName())
                                                   .startedAt(OffsetDateTime.now())
                                                   .build();
        testExecution = testService.startTest(testExecution, launch.getId());
        testService.finishTextExecutionAsResult(launch.getId(), testExecution.getId(), TestExecution.Status.FAILED.getStatus());
        testRunService.finishLaunch(launch.getId());
        launchIds.add(launch.getId());

        testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());

        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(), EXISTING_JIRA_ISSUE_ID,
                "Jira ticket should be linked");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1244")
    public void testLinkNewIssueToKnownIssue() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(), EXISTING_JIRA_ISSUE_ID,
                "Jira ticket is not as expected");

        linkIssueModal = resultTestMethodCard.clickLinkIssueEditButton();
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_SECOND_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear after linking different issue");
        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(), EXISTING_SECOND_JIRA_ISSUE_ID,
                "Jira ticket is not as expected after linking different issue");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1246")
    public void testHistoryOfPreviouslyLinkedIssues() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(0, 2, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA)
                      .linkIssue(EXISTING_JIRA_ISSUE_ID);

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear");
        softAssert.assertEquals(
                resultTestMethodCard.getLinkedIssueText(), EXISTING_JIRA_ISSUE_ID,
                "Jira ticket is not as expected");

        linkIssueModal = resultTestMethodCard.clickLinkIssueEditButton();

        softAssert.assertTrue(linkIssueModal.isIssueCardIdPresentOnHistorySection(EXISTING_JIRA_ISSUE_ID),
                "Jira ticket should present on history section");

        LinkedIssueCard linkedIssueCard = linkIssueModal.findIssueCard(EXISTING_JIRA_ISSUE_ID, SearchType.BY_ID);

        softAssert.assertTrue(DateUtil.isTimeWithinTolerance(LocalTime.now(), linkedIssueCard.getCardDate(),
                1, DateTimeFormatter.ofPattern("HH:mm")), "Time difference exceeds tolerance.");
        softAssert.assertEquals(linkedIssueCard.getAssignedPerson(), UsersEnum.MAIN_ADMIN.getUser()
                                                                                         .getUsername(), "Assigned user should be " + UsersEnum.MAIN_ADMIN.getUser()
                                                                                                                                                          .getUsername());

        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-1251")
    @Test(groups = "min_acceptance")
    public void linkIssueToSeveralFailedTestViaCheckBox() {
        this.skipTestIfJiraNotEnabled();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 2, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        List<ResultTestMethodCardR> failedTestCards = testRunResultPage.getFailedTestCards();

        for (ResultTestMethodCardR testCard : failedTestCards) {
            testCard.clickCardCheckbox();
            softAssert.assertTrue(testCard.isCardCheckboxChecked(), "Card check box should be selected");
        }

        BulkActionSection bulkActionSection = testRunResultPage.getActionsBlockR().getBulkActionSection();
        LinkIssueModal linkIssueModal = bulkActionSection.clickLinkIssueButton();

        softAssert.assertEquals(linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "Link issue modal should be opened");

        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);
        linkIssueModal.typeAndSelectIdOrSummary(EXISTING_JIRA_ISSUE_ID);
        linkIssueModal.clickLinkIssueButton();

        softAssert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear(MessageEnum.ISSUE_LINKED.getDescription()),
                "Popup message of successfully issue linking is not appear after linking different issue");
        for (ResultTestMethodCardR testCard : failedTestCards) {
            softAssert.assertEquals(testCard.getLinkedIssueText(), EXISTING_JIRA_ISSUE_ID,
                    "Jira issue should be linked");
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1252")
    public void verifyUserCantLinkIssueToFailedTestWithoutStacktrace() {
        this.skipTestIfJiraNotEnabled();

        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        TestExecution testExecution = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testService.finishTestAsResult(launch.getId(), testExecution.getId(), FinishTestRequest.getRequestWithoutReason("FAILED"));
        testRunService.finishTestRun(launch.getId());
        launchIds.add(launch.getId());

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(testExecution.getName());

        softAssert.assertFalse(
                resultTestMethodCard.getLinkIssueButton().isStateMatches(Condition.PRESENT),
                "Link issue button shouldn't be present on test card - " +
                        "User can't link issue to failed test without stack trace");
        softAssert.assertFalse(
                resultTestMethodCard.getCardMenuR().getLinkIssue().isStateMatches(Condition.PRESENT),
                "Link issue button shouldn't be present in card menu - " +
                        "User can't link issue to failed test without stack trace");

        PageUtil.guaranteedToHideDropDownList(getDriver());

        resultTestMethodCard.clickCardCheckbox();
        BulkActionSection bulkActionSection = testRunResultPage.getActionsBlockR().getBulkActionSection();

        softAssert.assertTrue(bulkActionSection.isLinkIssueButtonPresent(3),
                "Link issue button shouldn't be present in bulk section - " +
                        "User can't link issue to failed test without stack trace");

        softAssert.assertAll();
    }

    private void skipTestIfJiraNotEnabled() {
        JiraProperties jiraProperties = ConfigHelper.getJiraProperties();

        if (!jiraProperties.getEnabled()) {
            CurrentTest.revertRegistration();

            throw new SkipException("Jira is not enabled");
        }
    }

}