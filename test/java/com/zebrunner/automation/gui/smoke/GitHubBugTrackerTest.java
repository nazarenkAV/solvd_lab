package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.project.method.v1.ProjectAssignment;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.reporting.launch.LinkIssueModal;
import com.zebrunner.automation.gui.reporting.launch.LinkedIssueCard;
import com.zebrunner.automation.gui.reporting.launch.SearchType;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.external.GitHubIssuePage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Maintainer("Gmamaladze")
public class GitHubBugTrackerTest extends LogInBase {

    private final String GITHUB_BUG_TRACKER = "GitHub";
    private final String JIRA_BUG_TRACKER = "Jira";
    private final String EXISTING_GITHUB_ISSUE_TITLE = "Add a method to quickly tap n times";
    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private TestClassLaunchDataStorage testClassLaunchDataStorage;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;

        IntegrationManager.addIntegration(project.getId(), Tool.GITHUB);
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    //----------------------------------Test--------------------------------------------//

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-1411", "ZTP-1412"})
    public void changingBugTrackingSystemWhenLinkingIssueViaTestCardTest() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());

        softAssert.assertEquals(linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "Link Issue Modal should be opened");

        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_GITHUB);

        //ZTP-1411
        softAssert.assertEquals(linkIssueModal.getBugTrackerText(), GITHUB_BUG_TRACKER, "Bug tracker should be Github");

        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);

        //ZTP-1412
        softAssert.assertEquals(linkIssueModal.getBugTrackerText(), JIRA_BUG_TRACKER, "Bug tracker should be Jira");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1369", "ZTP-1372", "ZTP-1415"})
    public void searchBySummaryAndLinkUnlinkGitHubIssueViaTestCardTest() {
        WebDriver webDriver = super.getDriver();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch()
                                                                                                                                              .getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(webDriver);
        Assert.assertEquals(
                linkIssueModal.getModalTitleText(), LinkIssueModal.MODAL_TITLE,
                "Link Issue Modal should be opened"
        );

        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_GITHUB);
        linkIssueModal.typeIdOrSummary(EXISTING_GITHUB_ISSUE_TITLE);
        Assert.assertTrue(
                linkIssueModal.isTicketPresentInSuggestionList(EXISTING_GITHUB_ISSUE_TITLE),
                "Github issue was not found"
        );

        linkIssueModal.selectSuggestionByIdOrSummary(EXISTING_GITHUB_ISSUE_TITLE);
        Assert.assertEquals(
                linkIssueModal.getSelectedLinkIssueTitleText(), EXISTING_GITHUB_ISSUE_TITLE,
                "Github issue was not selected"
        );

        linkIssueModal.clickLinkIssueButton();
        LinkedIssueCard linkedIssueCard = linkIssueModal.findIssueCard(EXISTING_GITHUB_ISSUE_TITLE, SearchType.BY_TITLE);

        Assert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear("Issue was linked successfully"),
                "Popup message of successfully issue linking is not appear"
        );

        Assert.assertEquals(
                linkIssueModal.getColorOfLinkIssueButton(), "#dfe3e5",
                "'Link issue' button color should be grey"
        );
        Assert.assertEquals(
                linkedIssueCard.getIssueTicketTitleText(), EXISTING_GITHUB_ISSUE_TITLE,
                "Github issue title should be displayed"
        );
        Assert.assertEquals(
                linkedIssueCard.getColorOfLinkUnlinkButton(), "#26a69a",
                "Link/unlink issue button should be green"
        );
        Assert.assertEquals(
                linkedIssueCard.getTitleOfLinkUnlinkButton(), "Unlink",
                "Title of Link/unlink button should be 'Unlink'"
        );
        Assert.assertTrue(
                DateUtil.isTimeWithinTolerance(
                        LocalTime.now(),
                        linkedIssueCard.getCardDate(),
                        1,
                        DateTimeFormatter.ofPattern("HH:mm")
                ),
                "Time difference exceeds tolerance."
        );

        linkedIssueCard.clickLinkUnlinkIssueButton();

        Assert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear("Issue was unlinked successfully"),
                "Popup message of successfully issue unlinking is not appear");
        Assert.assertEquals(
                linkedIssueCard.getIssueTicketTitleText(), EXISTING_GITHUB_ISSUE_TITLE,
                "Github issue title should be displayed"
        );
        Assert.assertEquals(
                linkedIssueCard.getColorOfLinkUnlinkButton(), "#000000",
                "Link/unlink issue button should be black"
        );
        Assert.assertEquals(
                linkedIssueCard.getTitleOfLinkUnlinkButton(), "Link",
                "Title of Link/unlink button should be 'Link'"
        );

        linkIssueModal.clickClose();
        Assert.assertFalse(resultTestMethodCard.isLinkedIssuePresent(), "Github issue should not be linked");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1366")
    public void redirectionToGithubTicketAfterClickingGithubIssueButtonTest() {
        WebDriver webDriver = super.getDriver();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(webDriver);
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_GITHUB);
        linkIssueModal.linkIssue(EXISTING_GITHUB_ISSUE_TITLE);

        Assert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear("Issue was linked successfully"),
                "Popup message of successfully issue linking is not appear"
        );

        resultTestMethodCard.clickLinkedIssueButton();

        PageUtil.toOtherTabWithoutClosingFirstOne(webDriver);

        GitHubIssuePage gitHubIssuePage = new GitHubIssuePage(webDriver);

        Assert.assertEquals(
                gitHubIssuePage.getGitHubIssueTitle(), EXISTING_GITHUB_ISSUE_TITLE,
                "Github Issue page was not opened properly - Issue title is not as expected !"
        );
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5766")
    public void verifyUserTooltipAppearsWhenHoveringLinkedIssueCardAssignedPerson() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        User mainAdmin = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        ProjectAssignment assignedUser = projectV1Service.getProjectAssignmentForUser(Math.toIntExact(project.getId()), mainAdmin.getUsername());

        TestExecution failedTest = testClassLaunchDataStorage.getFailedTests().get(0);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR resultTestMethodCard = testRunResultPage.getCertainTest(failedTest.getName());
        resultTestMethodCard.getLinkIssueButton().click();

        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_GITHUB);
        linkIssueModal.typeIdOrSummary(EXISTING_GITHUB_ISSUE_TITLE);
        linkIssueModal.selectSuggestionByIdOrSummary(EXISTING_GITHUB_ISSUE_TITLE);
        linkIssueModal.clickLinkIssueButton();

        LinkedIssueCard linkedIssueCard = linkIssueModal.findIssueCard(EXISTING_GITHUB_ISSUE_TITLE, SearchType.BY_TITLE);
        linkedIssueCard.hoverAssignedPerson()
                       .verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "At Link Issue Modal!");
    }
}