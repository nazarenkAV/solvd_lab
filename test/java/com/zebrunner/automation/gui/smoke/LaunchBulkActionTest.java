package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
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
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.TestLabels;
import com.zebrunner.automation.gui.launcher.DeleteLauncherModal;
import com.zebrunner.automation.gui.launcher.LauncherItem;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.reporting.launch.AssignToMilestoneModalR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.BulkActionSection;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.gui.reporting.launch.RelaunchModal;
import com.zebrunner.automation.gui.reporting.widget.SendByEmailWindow;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.util.EmailManager;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

public class LaunchBulkActionTest extends LogInBase {

    private static final String LAUNCHER_NAME = "WEB tests - bulk_action";

    private final EmailManager emailManager = EmailManager.primaryInstance;

    private Project project;
    private final List<Long> launchIds = new ArrayList<>();

    @BeforeClass
    public void getProject() {
        project = projectV1Service.createProject();

        Long apiRepoId = launcherService.addGitRepo(
                project.getId(),
                ConfigHelper.getGithubProperties().getUrl() + "/" + PUBLIC_REPO_NAME,
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken(),
                GitProvider.GITHUB.toString()
        );

        launcherService.addDefaultUiTestsLauncher(
                project.getId(), apiRepoId, LAUNCHER_NAME, "main", "helloWorld");
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        launchIds.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    @AfterClass(alwaysRun = true)
    public void deleteProject() {
        super.projectV1Service.deleteProjectById(project.getId());
    }

    //========================================== Test ======================================================

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-3927", "ZTP-1207"})
    @TestLabel(name = TestLabels.Name.GROUP, value = TestLabels.Value.LAUNCHES)
    public void deleteMultipleTestRunsTest() {
        WebDriver webDriver = super.getDriver();

        List<String> launchNames = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());

            testRunService.finishLaunch(launch.getId());
            launchIds.add(launch.getId());

            launchNames.add(launch.getName());
        }

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());

        String numberOfLaunchesOnPagination = launchesPage.getPagination()
                                                          .getNumberOfItemsOnThePage();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(launchesPage.isSelectAllLaunchesCheckboxPresent(), "Checkbox is not present!");
        softAssert.assertTrue(launchesPage.getTopPagination()
                                          .isFullPaginationPresent(), "Top pagination is not Present!");
        softAssert.assertTrue(launchesPage.getBottomPagination()
                                          .isFullPaginationPresent(), "Bottom pagination is not Present!");
        softAssert.assertAll();

        launchesPage.getCertainTestRunCard(launchNames.get(0), true)
                    .clickCheckbox();
        launchesPage.getCertainTestRunCard(launchNames.get(1), true)
                    .clickCheckbox();
        launchesPage.getActionsBlockR()
                    .getBulkActionSection()
                    .delete();

        softAssert.assertEquals(
                launchesPage.getPopUp(),
                "Launches have been successfully deleted",
                "Popup message is not found, after deleting launches!"
        );
        launchesPage.waitPopupDisappears();

        String numberOfLaunchesOnPaginationAfter = launchesPage.getPagination().getNumberOfItemsOnThePage();

        softAssert.assertNotEquals(numberOfLaunchesOnPagination, numberOfLaunchesOnPaginationAfter, "Number of launches on pagination have not changed!");
        softAssert.assertFalse(launchesPage.getActionsBlockR()
                                           .isBulkActionSectionPresent(), "Bulk actions should not be visible!");
        softAssert.assertFalse(launchesPage.isCertainLaunchAppears(launchNames.get(0)), "Launch with name: " + launchNames.get(0) + " should be deleted!");
        softAssert.assertFalse(launchesPage.isCertainLaunchAppears(launchNames.get(1)), "Launch with name: " + launchNames.get(1) + " should be deleted!");
        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-3929", "ZTP-3778", "ZTP-3795", "ZTP-3782"})
    public void assignLaunchesToMilestoneTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        String milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));

        apiHelperService.createMilestone(project.getId(), milestoneName);

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
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible!");

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(milestoneName);

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCHES_HAVE_BEEN_ASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        softAssert.assertEquals(firstLaunch.getMilestone()
                                           .getText(), milestoneName, "Milestone name is different for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(secondLaunch.getMilestone()
                                            .getText(), milestoneName, "Milestone name is different for card: " + secondLaunch.getCardName());

        softAssert.assertNotEquals(automationLaunchesPage.searchAndFindTestRunCardByName(names.get(2))
                                                         .getMilestone().getText(), milestoneName,
                "Milestone should not be present for card: " + names.get(2));

        softAssert.assertAll();
    }

    @Test
    @SneakyThrows
    @TestCaseKey("ZTP-3930")
    @Maintainer("bmakharadze")
    public void sendAsEmailTest() {
        WebDriver webDriver = super.getDriver();
        String targetEmail = ConfigHelper.getEmailAccountProperties().getUsername();

        List<String> names = new ArrayList<>();
        List<Launch> startedTestRuns = testRunService.startMultipleLaunches(project.getKey(), 2);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
            launchIds.add(testRun.getId());
            names.add(testRun.getName());
        }

        String firstLaunchName = names.get(0);
        String secondLaunchName = names.get(1);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true);
        LaunchCard secondLaunch = automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true);
        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();

        Assert.assertTrue(firstLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + firstLaunch.getCardName());
        Assert.assertTrue(secondLaunch.isCheckBoxSelected(), "Checkbox is not selected for: " + secondLaunch.getCardName());
        Assert.assertTrue(
                automationLaunchesPage.getActionsBlockR().isBulkActionSectionPresent(),
                "Bulk actions should be visible!"
        );

        automationLaunchesPage.getActionsBlockR()
                              .getBulkActionSection()
                              .sendAsEmail(targetEmail);

        Assert.assertEquals(
                automationLaunchesPage.getPopUp(), "Launch reports were successfully sent",
                "Popup message of successful email sending is not appeared"
        );

        automationLaunchesPage.waitPopupDisappears();
        Assert.assertFalse(
                automationLaunchesPage.getActionsBlockR().isBulkActionSectionPresent(),
                "Bulk actions should not be visible!"
        );

        emailManager.waitUntilEmailDelivered(this.getEmailSubject(firstLaunch));
        emailManager.waitUntilEmailDelivered(this.getEmailSubject(secondLaunch));
    }

    private String getEmailSubject(LaunchCard launchCard) {
        return launchCard.getStatus().getStatusColourFromCss().value() + ": " + launchCard.getCardName();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5934")
    public void verifyUserCanCancelRelaunch() {
        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);

        launcherPage.chooseRepo(PUBLIC_REPO_NAME)
                    .clickOnRepository()
                    .getLauncherWithName(LAUNCHER_NAME)
                    .ifPresent(LauncherItem::clickOnLauncherName);

        AutomationLaunchesPage automationLaunchesPage = launcherPage.launchLauncher();

        LaunchCard launchCard = automationLaunchesPage.waitLaunchAppearByName(LAUNCHER_NAME).waitFinish();

        launchCard.clickCheckbox();
        softAssert.assertTrue(launchCard.isCheckBoxSelected(), "Checkbox is not selected for: " + launchCard.getCardName());

        for (int i = 0; i < 3; i++) {
            RelaunchModal relaunchModal = automationLaunchesPage.getActionsBlockR().getBulkActionSection()
                                                                .clickRelaunchButton();

            softAssert.assertEquals(relaunchModal.getModalTitleText(), relaunchModal.MODAL_TITLE,
                    "Relaunch modal should be opened !");

            if (i == 0) {
                relaunchModal.clickCancel();
            } else if (i == 1) {
                relaunchModal.clickClose();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            softAssert.assertFalse(relaunchModal.isModalOpened(), "Modal should be closed !");
            automationLaunchesPage.assertPageOpened();

            softAssert.assertAll();
        }
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5931")
    @Maintainer("Gmamaladze")
    public void verifyUserIsAbleToCancelDeletingLaunch() {
        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage testClassLaunchDataStorage =
                PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        automationLaunchesPage.getCertainTestRunCard(testClassLaunchDataStorage.getLaunch().getName(), true)
                              .clickCheckbox();

        for (int i = 0; i < 3; i++) {
            DeleteLauncherModal deleteLauncherAlertModal = automationLaunchesPage
                    .getActionsBlockR().getBulkActionSection().clickDelete();

            softAssert.assertEquals(deleteLauncherAlertModal.getTitleText(), DeleteLauncherModal.MODAL_NAME,
                    "Delete launcher alert modal is not opened !");

            if (i == 0) {
                deleteLauncherAlertModal.clickCancel();
            } else if (i == 1) {
                deleteLauncherAlertModal.clickClose();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            softAssert.assertFalse(deleteLauncherAlertModal.isModalOpened(), "Modal should be closed !");
            automationLaunchesPage.assertPageOpened();

            softAssert.assertTrue(automationLaunchesPage.isCertainLaunchAppears(testClassLaunchDataStorage.getLaunch()
                                                                                                          .getName()),
                    "Launcher should be present in list !");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1205")
    public void testCheckmarkFunctionality() {
        WebDriver webDriver = super.getDriver();
        SoftAssert softAssert = new SoftAssert();

        List<Launch> launches = testRunService.startMultipleLaunches(project.getKey(), 4);
        for (Launch launch : launches) {
            testRunService.finishLaunch(launch.getId());
            launchIds.add(launch.getId());
        }

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());

        List<LaunchCard> launchCards = launchesPage.getAllTestRunCards();

        launchCards.get(0).clickCheckbox();
        softAssert.assertTrue(launchCards.get(0).isCheckBoxSelected(), "Check box should be selected !");

        for (LaunchCard launchCard : launchCards) {
            softAssert.assertTrue(launchCard.isCheckBoxPresent(4), "Check box should be present for all launch !");
        }
        softAssert.assertAll();

        BulkActionSection bulkActionSection = launchesPage.getActionsBlockR().getBulkActionSection();
        softAssert.assertTrue(
                bulkActionSection.isDeleteButtonPresent(5),
                "Delete button should be present in bulk action section !"
        );
        softAssert.assertTrue(
                bulkActionSection.isSendAsEmailButtonPresent(5),
                "Send as email button should be present in bulk action section !"
        );
        softAssert.assertTrue(
                bulkActionSection.isAssignToMilestoneButtonPresent(5),
                "Assign to milestone button should be present in bulk action section !"
        );
        softAssert.assertTrue(
                bulkActionSection.isRelaunchButtonPresent(5),
                "Relaunch button should be present in bulk action section !"
        );
        softAssert.assertTrue(
                bulkActionSection.isAbortButtonPresent(5),
                "Abort button should be present in bulk action section !"
        );
        softAssert.assertAll();

        launchesPage.clickSelectAllLaunchesCheckbox();
        for (LaunchCard launchCard : launchCards) {
            softAssert.assertTrue(launchCard.isCheckBoxSelected(), "Check box should be selected for all launch !");
        }
        softAssert.assertAll();

        launchesPage.clickSelectAllLaunchesCheckbox();
        for (LaunchCard launchCard : launchCards) {
            softAssert.assertFalse(launchCard.isCheckBoxSelected(), "Check box shouldn't be selected for all launch !");
        }
        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5932")
    public void verifyUserIsAbleToCancelSendAsEmailLaunchAction() {
        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage testClassLaunchDataStorage =
                PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard launchCard = automationLaunchesPage
                .getCertainTestRunCard(testClassLaunchDataStorage.getLaunch().getName(), true);

        launchCard.clickCheckbox();

        for (int i = 0; i < 3; i++) {
            SendByEmailWindow sendByEmailWindow = automationLaunchesPage.getActionsBlockR()
                                                                        .getBulkActionSection()
                                                                        .clickBulkSendAsEmail();

            softAssert.assertEquals(sendByEmailWindow.getModalTitle()
                                                     .getText(), SendByEmailWindow.SHARE_LAUNCH_RESULT_MODAL_TITLE,
                    "Share launch result modal is not opened !");

            if (i == 0) {
                sendByEmailWindow.clickCancel();
            } else if (i == 1) {
                sendByEmailWindow.closeModal();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            softAssert.assertFalse(sendByEmailWindow.isModalOpened(), "Modal should be closed !");
            softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Launcher page should be opened !");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-3932", "ZTP-3794", "ZTP-3783", "ZTP-5749"})
    public void unassignLaunchesFromMilestoneTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        String milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));

        apiHelperService.createMilestone(project.getId(), milestoneName);

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
        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(milestoneName);

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCHES_HAVE_BEEN_ASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();
        softAssert.assertEquals(automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true).getMilestone()
                                                      .getText(),
                milestoneName, "Milestone name is different for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true).getMilestone()
                                                      .getText(),
                milestoneName, "Milestone name is different for card: " + secondLaunch.getCardName());

        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();

        softAssert.assertTrue(automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true).isCheckBoxSelected(),
                "Checkbox is not selected for: " + firstLaunch.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true).isCheckBoxSelected(),
                "Checkbox is not selected for: " + secondLaunch.getCardName());
        softAssert.assertTrue(automationLaunchesPage.getActionsBlockR()
                                                    .isBulkActionSectionPresent(), "Bulk actions should be visible!");

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().unAssign();

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCHES_HAVE_BEEN_UNASSIGNED_FROM_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        softAssert.assertFalse(automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true)
                                                     .getMilestone()
                                                     .isUIObjectPresent(3), "Milestone should not be present for card: " + firstLaunch.getCardName());
        softAssert.assertFalse(automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true)
                                                     .getMilestone()
                                                     .isUIObjectPresent(3), "Milestone should not be present for card: " + secondLaunch.getCardName());

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5933")
    public void verifyUserIsAbleToCancelAssignToMilestoneProcess() {
        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage testClassLaunchDataStorage =
                PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard launchCard = automationLaunchesPage
                .getCertainTestRunCard(testClassLaunchDataStorage.getLaunch().getName(), true);

        launchCard.clickCheckbox();

        for (int i = 0; i < 3; i++) {
            AssignToMilestoneModalR assignToMilestoneModal = automationLaunchesPage.getActionsBlockR()
                                                                                   .getBulkActionSection()
                                                                                   .openAssignToMilestoneModal();

            softAssert.assertTrue(assignToMilestoneModal.isAssignToMilestoneModalOpened(),
                    "Assign to milestone modal should be opened !");

            if (i == 0) {
                assignToMilestoneModal.clickCancel();
            } else if (i == 1) {
                assignToMilestoneModal.closeModal();
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
            }

            softAssert.assertFalse(assignToMilestoneModal.isModalOpened(), "Modal should be closed !");
            softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Launcher page should be opened !");
        }

        softAssert.assertAll();
    }
}