package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.reporting.launch.AssignToMilestoneModalR;
import com.zebrunner.automation.gui.reporting.launch.CollapsedTestRunViewHeaderR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Launch;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

@Maintainer("akhivyk")
public class AssignMilestonesTest extends LogInBase {
    private final List<Long> milestoneIds = new ArrayList<>();
    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private List<Launch> startedTestRuns;
    private String firstMilestoneName;
    private String secondMilestoneName;

    @BeforeClass
    public void preparation() {
        project = LogInBase.project;

        firstMilestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(7));
        secondMilestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(7));

        milestoneIds.add(apiHelperService.createMilestone(project.getId(), firstMilestoneName));
        milestoneIds.add(apiHelperService.createMilestone(project.getId(), secondMilestoneName));

        startedTestRuns = testRunService.startMultipleLaunches(project.getKey(), 2);

        for (Launch testRun : startedTestRuns) {
            testRunService.finishLaunch(testRun.getId());
            testRunIdList.add(testRun.getId());
        }
    }

    @AfterClass(alwaysRun = true)
    public void deleteMilestonesAndRuns() {
        milestoneIds.forEach(milestoneId -> apiHelperService.deleteMilestone(project.getId(), milestoneId));
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @Test
    @TestCaseKey("ZTP-3780")
    public void verifyUserIsAbleUnassignLaunchFromMilestoneViaBulkAction() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        firstLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(firstMilestoneName);

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        firstLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().unAssign();

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_UNASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        softAssert.assertFalse(automationLaunchesPage.getCertainTestRunCard(launchName, true)
                .isMilestonePresent(), "Milestone should not be present for card: " + firstLaunch.getCardName());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3781")
    public void verifyUserIsAbleUnassignLaunchFromMilestoneVia3Dots() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        AssignToMilestoneModalR assignToMilestoneModal = firstLaunch.clickMenu().openAssignToMilestoneModal();

        softAssert.assertTrue(assignToMilestoneModal.isAssignToMilestoneModalOpened(), "Assign to milestone modal isn't open!");

        assignToMilestoneModal.chooseMilestone(firstMilestoneName).assign();

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        firstLaunch.clickMenu().openAssignToMilestoneModal().unAssign();

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_UNASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        softAssert.assertFalse(automationLaunchesPage.getCertainTestRunCard(launchName, true)
                .isMilestonePresent(), "Milestone should not be present for card: " + firstLaunch.getCardName());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3791")
    public void verifyUserIsAbleUnassignLaunchFromDifferentMilestonesViaBulkAction() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String firstLaunchName = startedTestRuns.get(0).getName();
        String secondLaunchName = startedTestRuns.get(1).getName();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true);
        firstLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(firstMilestoneName);

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        LaunchCard secondLaunch = automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true);
        secondLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(secondMilestoneName);

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        firstLaunch.clickCheckbox();
        secondLaunch.clickCheckbox();
        automationLaunchesPage.getActionsBlockR().getBulkActionSection().unAssign();

        softAssert.assertEquals(automationLaunchesPage.getPopUp(), MessageEnum.LAUNCHES_HAVE_BEEN_UNASSIGNED_FROM_MILESTONE.getDescription(),
                "Launches have been assigned to milestone message is not as expected!");
        automationLaunchesPage.waitPopupDisappears();

        softAssert.assertFalse(automationLaunchesPage.getCertainTestRunCard(firstLaunchName, true)
                .isMilestonePresent(), "Milestone should not be present for card: " + firstLaunch.getCardName());
        softAssert.assertFalse(automationLaunchesPage.getCertainTestRunCard(secondLaunchName, true)
                .isMilestonePresent(), "Milestone should not be present for card: " + secondLaunch.getCardName());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-3784", "ZTP-3785"})
    public void verifyMilestoneAssignedToLaunchAfterReloadingPageAndMarkAsReview() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        AssignToMilestoneModalR assignToMilestoneModal = firstLaunch.clickMenu().openAssignToMilestoneModal();
        assignToMilestoneModal.chooseMilestone(firstMilestoneName).assign();

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        String reviewMessage = "Review Message ".concat(RandomStringUtils.randomAlphabetic(10));
        firstLaunch.clickMenu().openMarkAsReviewModal().inputReviewMessage(reviewMessage).submitModal();

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        getDriver().get(getDriver().getCurrentUrl());

        firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3786")
    public void verifyMilestoneAssignedToLaunchAfterRelaunching() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        firstLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(firstMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        testRunService.startTestRunWithName(project.getKey(), startedTestRuns.get(0));
        testRunService.finishLaunch(startedTestRuns.get(0).getId());

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3787")
    public void verifyUserAbleAssignMilestoneViaBulkActionAndReassignVia3Dots() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        firstLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(firstMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        firstLaunch.clickMenu().openAssignToMilestoneModal().chooseMilestone(secondMilestoneName).assign();

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), secondMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3788")
    public void verifyUserAbleAssignMilestoneVia3DotsAndReassignViaBulkAction() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        firstLaunch.clickMenu().openAssignToMilestoneModal().chooseMilestone(firstMilestoneName).assign();

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        firstLaunch.clickCheckbox();
        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(secondMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), secondMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3789")
    public void verifyUserAbleAssignMilestoneViaBulkActionAndUnassignVia3Dots() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        firstLaunch.clickCheckbox();

        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(firstMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        firstLaunch.clickMenu().openAssignToMilestoneModal().unAssign();

        softAssert.assertFalse(firstLaunch.isMilestonePresent(), "Milestone shouldn't present for card: " + firstLaunch.getCardName());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3790")
    public void verifyUserAbleAssignMilestoneVia3DotsAndUnassignViaBulkAction() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);

        firstLaunch.clickMenu().openAssignToMilestoneModal().chooseMilestoneAndAssign(firstMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        firstLaunch.clickCheckbox();
        automationLaunchesPage.getActionsBlockR().getBulkActionSection().unAssign();

        softAssert.assertFalse(firstLaunch.isMilestonePresent(), "Milestone shouldn't present for card: " + firstLaunch.getCardName());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3797")
    public void verifyMilestoneDisplayedWhenUserOpenLaunch() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);

        firstLaunch.clickMenu().openAssignToMilestoneModal().chooseMilestoneAndAssign(firstMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        TestRunResultPageR testRunPage = firstLaunch.toTests();
        softAssert.assertTrue(testRunPage.getExpandedTestRunViewHeader().isMilestonePresent(),
                "Milestone isn't present on test run page with expanded header!");
        softAssert.assertEquals(testRunPage.getExpandedTestRunViewHeader().getMilestoneName(), firstMilestoneName,
                "Milestone name isn't equals to expected with expanded header!");

        CollapsedTestRunViewHeaderR collapsedTestRunView = testRunPage.collapseTestRunViewHeader();
        softAssert.assertTrue(collapsedTestRunView.isMilestonePresent(),
                "Milestone isn't present on test run page with collapsed header!");
        softAssert.assertEquals(collapsedTestRunView.getMilestoneName(), firstMilestoneName,
                "Milestone name isn't equals to expected with collapsed header!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-3792", "ZTP-3793"})
    public void verifyNoneOptionSelectedWhenOpenAssignMilestoneModal() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        SoftAssert softAssert = new SoftAssert();

        String launchName = startedTestRuns.get(0).getName();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard firstLaunch = automationLaunchesPage.getCertainTestRunCard(launchName, true);
        firstLaunch.clickCheckbox();
        automationLaunchesPage.getActionsBlockR().getBulkActionSection().assignToMilestone(firstMilestoneName);

        softAssert.assertTrue(firstLaunch.isMilestonePresent(), "Milestone should present for card: " + firstLaunch.getCardName());
        softAssert.assertEquals(firstLaunch.getNameOfAssignedMilestone(), firstMilestoneName,
                "Milestone name on launch card isn't equals to expected!");

        AssignToMilestoneModalR assignToMilestoneModal = firstLaunch.clickMenu().openAssignToMilestoneModal();
        softAssert.assertTrue(assignToMilestoneModal.isNoneButtonSelected(),
                "None option isn't selected when open assign to milestone modal!");
        assignToMilestoneModal.clickCancel();

        firstLaunch.clickCheckbox();
        assignToMilestoneModal = automationLaunchesPage.getActionsBlockR().getBulkActionSection().openAssignToMilestoneModal();

        softAssert.assertTrue(assignToMilestoneModal.isNoneButtonSelected(),
                "None option isn't selected when open assign to milestone modal!");

        softAssert.assertAll();
    }
}
