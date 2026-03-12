package com.zebrunner.automation.gui.smoke;

import groovy.util.logging.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.project.method.v1.ProjectAssignment;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.TestLabels;
import com.zebrunner.automation.config.TestMaintainers;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.EmailManager;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.util.LocalStorageManager;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.automation.gui.reporting.launch.DeleteTestRunModal;
import com.zebrunner.automation.gui.reporting.launch.FailureTagModal;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.common.UserInfoTooltip;

@Slf4j
@Maintainer("obabich")
public class AutomationLaunchesPageTest extends LogInBase {

    private final String emailUsername = ConfigHelper.getEmailAccountProperties().getUsername();

    private Project project;
    private final List<Long> launchIds = new ArrayList<>();

    @BeforeClass
    public void getProjectAndAddJiraIntegration() {
        project = LogInBase.project;

        String launcherName = "WEB tests - automation_launches";
        launcherService.addDefaultUiTestsLauncher(
                project.getId(), LogInBase.repositoryId, launcherName, "main", "helloWorld"
        );
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        launchIds.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    //========================================== Test ======================================================

    @SneakyThrows
    @Test(enabled = false)
    @TestCaseKey("ZTP-3931")
    @Maintainer(TestMaintainers.DKAZAK)
    @TestLabel(name = TestLabels.Name.GROUP, value = TestLabels.Value.LAUNCHES)
    public void sendEmailToMultipleLaunchesTest() {
        String email = ConfigHelper.getEmailAccountProperties().getUsername();

        List<EmailManager> emailManagers = List.of(EmailManager.primaryInstance);

        List<Launch> startedLaunches = testRunService.startMultipleLaunches(project.getKey(), 4);
        for (Launch launch : startedLaunches) {
            testRunService.finishLaunch(launch.getId());

            launchIds.add(launch.getId());
        }

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(super.getDriver(), project.getKey());

        LaunchCard firstLaunch = launchesPage.getCertainTestRunCard(startedLaunches.get(0).getName(), true);
        LaunchCard secondLaunch = launchesPage.getCertainTestRunCard(startedLaunches.get(1).getName(), true);
        LaunchCard thirdLaunch = launchesPage.getCertainTestRunCard(startedLaunches.get(2).getName(), true);

        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getTitle());
        softAssert.assertTrue(secondLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + secondLaunch.getTitle());
        softAssert.assertTrue(
                launchesPage.getActionsBlockR().isBulkActionSectionPresent(),
                "Bulk actions should be visible!"
        );
        softAssert.assertAll();

        launchesPage.getActionsBlockR()
                    .getBulkActionSection()
                    .clickBulkSendAsEmail()
                    .fillEmailsAndSend(List.of(emailUsername, email));

        Assert.assertEquals(
                launchesPage.getPopUp(),
                "Launch reports were successfully sent",
                "Popup message of successful email sending is not appeared"
        );

        launchesPage.waitPopupDisappears();
        Assert.assertFalse(
                launchesPage.getActionsBlockR().isBulkActionSectionPresent(),
                "Bulk actions should not be visible!"
        );

        for (EmailManager emailManager : emailManagers) {
            String emailSubject = this.getLaunchStatus(firstLaunch) + ": " + firstLaunch.getCardName();
            emailManager.waitUntilEmailDelivered(emailSubject);

            emailSubject = this.getLaunchStatus(secondLaunch) + ": " + secondLaunch.getCardName();
            emailManager.waitUntilEmailDelivered(emailSubject);

            emailSubject = this.getLaunchStatus(thirdLaunch) + ": " + thirdLaunch.getCardName();
            softAssert.assertTrue(
                    emailManager.getOptionalEmailMessageContent(emailSubject).isEmpty(),
                    "Email should not be delivered for third card: " + thirdLaunch.getCardName()
            );
        }
        softAssert.assertAll();
    }

    private String getLaunchStatus(LaunchCard launch) {
        return launch.getStatus().getStatusColourFromCss().value();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-3933", "ZTP-5750"})
    public void clearSelectionTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        List<String> names = new ArrayList<>();
        List<Launch> startedTestRuns = testRunService.startMultipleLaunches(project.getKey(), 3);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
            launchIds.add(testRun.getId());
            names.add(testRun.getName());
        }

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(names.get(0), true);
        LaunchCard secondLaunch = automationLaunchesPage.getCertainTestRunCard(names.get(1), true);
        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();

        softAssert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getCardName());
        softAssert.assertTrue(secondLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + secondLaunch.getCardName());

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().clearSelection();

        softAssert.assertFalse(firstLaunch.isCheckBoxSelected(), "Checkbox should not be selected for card: " + firstLaunch.getCardName());
        softAssert.assertFalse(secondLaunch.isCheckBoxSelected(), "Checkbox should not be selected for card: " + secondLaunch.getCardName());
        softAssert.assertFalse(automationLaunchesPage.getActionsBlockR()
                                                     .isBulkActionSectionPresent(), "Bulk actions should not be visible!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-3927")
    public void quantityOfSelectedLaunchesUpdatedOnDeletionViaKebabMenu() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        List<String> names = new ArrayList<>();
        List<Launch> startedTestRuns = testRunService.startMultipleLaunches(project.getKey(), 4);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
            launchIds.add(testRun.getId());
            names.add(testRun.getName());
        }

        SoftAssert softAssert = new SoftAssert();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        int checkboxesClicked = 0;
        for (String name : names) {
            LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(name, true);
            if (testRunCard != null && checkboxesClicked < 3) {
                testRunCard.clickCheckbox();
                checkboxesClicked++;
            }
        }

        softAssert.assertEquals(automationLaunchesPage.getActionsBlockR().getBulkActionSection()
                                                      .getSelectedCardsAmountText(),
                "3 selected", "Selected cards amount do not match!");
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR().isBulkActionSectionPresent(),
                "Bulk actions should be visible!");

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(names.get(0), true);
        DeleteTestRunModal deleteTestRunModal = firstLaunch.clickMenu().openDeleteModal();

        softAssert.assertTrue(deleteTestRunModal.getDeleteButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Modal with 'Delete' button should be present!");

        deleteTestRunModal.clickDelete();

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_DELETED.getDescription(),
                "Popup message is not found, after deleting launch!");
        softAssert.assertEquals(automationLaunchesPage.getActionsBlockR().getBulkActionSection()
                                                      .getSelectedCardsAmountText(),
                "2 selected", "Selected cards amount do not match!");
        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5639")
    public void userCanSelectMultipleLaunchesUsingShiftTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        List<Launch> startedTestRuns = testRunService.startMultipleLaunches(project.getKey(), 4);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
            launchIds.add(testRun.getId());
        }

        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(getDriver());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        List<LaunchCard> launchCard = automationLaunchesPage.getAllTestRunCards();

        actions.keyDown(Keys.SHIFT).perform();
        launchCard.get(0).clickCheckbox();
        launchCard.get(launchCard.size() - 1).clickCheckbox();
        actions.keyUp(Keys.SHIFT).perform();

        for (int i = 0; i < launchCard.size(); i++) {
            softAssert.assertTrue(launchCard.get(i).isCheckBoxSelected(), "Test " + i + " is not selected!");
        }
        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5445")
    public void selectionRemainsWhenSearchUsed() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        List<String> names = new ArrayList<>();
        List<Launch> startedTestRuns = testRunService.startMultipleLaunches(project.getKey(), 3);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
            launchIds.add(testRun.getId());
            names.add(testRun.getName());
        }

        String firstLaunchName = names.get(0);
        String secondLaunchName = names.get(1);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true);
        LaunchCard secondLaunch = automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true);
        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();

        softAssert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getCardName());
        softAssert.assertTrue(secondLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + secondLaunch.getCardName());

        automationLaunchesPage.getSearchField().sendKeys(firstLaunchName);

        softAssert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible");

        automationLaunchesPage.getFilters().clickResetFilter();
        pause(2);
        softAssert.assertEquals(automationLaunchesPage.getActionsBlockR().getBulkActionSection()
                                                      .getSelectedCardsAmountText(),
                "2 selected", "Selected cards amount do not match!");
        softAssert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getCardName());
        secondLaunch = automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true);
        softAssert.assertTrue(secondLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + secondLaunch.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible");
        softAssert.assertTrue(automationLaunchesPage.isSearchFieldEmpty(), "Search field should be empty after resetting");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-5762", "ZTP-5761"})
    public void verifyLaunchUserInfoTooltips() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        User mainAdmin = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        ProjectAssignment assignedUser = projectV1Service.getProjectAssignmentForUser(Math.toIntExact(project.getId()), mainAdmin.getUsername());

        TestClassLaunchDataStorage reviewedTR = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        launchIds.add(reviewedTR.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(reviewedTR.getLaunch().getName(), true);
        testRunCard.markAsReviewed("New comment for project with key " + project.getKey());
        UserInfoTooltip userInfoTootip = testRunCard.getLaunchCardAttributes().hoverUsername();
        userInfoTootip.verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "At automation launches page for launch!");//ZTP-5761

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), reviewedTR.getLaunch().getId());

        testRunResultPage.expandTestRunViewHeader().hoverUsername();
        userInfoTootip.verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "When launch summary is expanded!");

        testRunResultPage.collapseTestRunViewHeader();
        testRunResultPage.getCollapsedTestRunViewHeaderR().hoverUsername();
        userInfoTootip.verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "When launch summary is collapsed!");

        testRunResultPage.getResultActionBar().openReviewModal().hoverReviewer();
        userInfoTootip.verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "At Review Modal!");
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5765")
    public void verifyFailureTagUserInfoTooltip() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        User mainAdmin = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        ProjectAssignment assignedUser = projectV1Service.getProjectAssignmentForUser(Math.toIntExact(project.getId()), mainAdmin.getUsername());

        TestClassLaunchDataStorage launch = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        launchIds.add(launch.getLaunch().getId());

        AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getLaunch().getId());
        ResultTestMethodCardR failedTestCard = testRunResultPage.getFailedTestCards().get(0);

        String failedTestTag = StringUtil.replaceSpaceWithHyphen(failedTestCard.getFailureTagText());

        FailureTagModal failureTagModal = failedTestCard.getFailureTagModal();

        if (!failedTestTag.equals(FailureTagModal.UNCATEGORIZED_TAG)) {
            failureTagModal.clickUncategorizedTagButton();
        } else {
            failureTagModal.clickBusinessIssueTagButton();
        }

        failureTagModal.clickSaveButton();

        failedTestCard.getFailureTagModal();
        UserInfoTooltip userInfoTootip = failureTagModal.hoverFirstAssigner();
        userInfoTootip.verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "At failure tag modal!");
    }

    @Test
    @SneakyThrows
    @TestCaseKey("ZTP-3667")
    public void verifyUserCanOpenLaunchViaDirectLink() {
        WebDriver webDriver = super.getDriver();

        TestClassLaunchDataStorage launchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(launchDataStorage.getLaunch().getId());

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());

        String launchName = launchDataStorage.getLaunch().getName();
        LaunchCard launch = launchesPage.getCertainTestRunCard(launchName, true);
        launch.clickCheckbox();

        Assert.assertTrue(launch.isCheckBoxSelected(), "Checkbox is not selected for: " + launch.getCardName());

        launchesPage.getActionsBlockR()
                    .getBulkActionSection()
                    .sendAsEmail(emailUsername);

        Assert.assertEquals(
                launchesPage.getPopUp(), "Launch reports were successfully sent",
                "Popup message of successful email sending is not appeared"
        );
        launchesPage.waitPopupDisappears();

        String emailSubject = launch.getStatus().getStatusColourFromCss().value() + ": " + launch.getCardName();
        String launchLink = EmailManager.primaryInstance.pollLaunchLink(emailSubject);

        LocalStorageManager localStorageManager = new LocalStorageManager(webDriver);
        localStorageManager.clear();
        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(LogInBase.notProjectMember));

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openViaDirectLink(webDriver, launchLink);
        Assert.assertEquals(testRunResultPage.getLaunchName(), launchName, "Launch page is not opened !");
    }

}
