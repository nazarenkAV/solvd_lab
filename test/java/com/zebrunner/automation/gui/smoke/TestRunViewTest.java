package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.reporting.service.ApiHelperService;
import com.zebrunner.automation.api.reporting.service.ApiHelperServiceImpl;
import com.zebrunner.automation.api.reporting.service.TestServiceV1Impl;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.CollapsedTestRunViewHeaderR;
import com.zebrunner.automation.gui.reporting.launch.ExpandedTestRunViewHeaderR;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.launch.ReviewModalR;
import com.zebrunner.automation.gui.reporting.launch.ShareLaunchForm;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Slf4j
@Maintainer("azarouski")
public class TestRunViewTest extends LogInBase {
    private static final Duration TEST_RUN_WAITING_TIME = Duration.ofSeconds(500);
    private final ApiHelperService apiHelperService = new ApiHelperServiceImpl();
    private final TestServiceV1Impl testServiceV1 = new TestServiceV1Impl();
    private final String milestoneName = "new_milestone".concat(RandomStringUtils.randomAlphabetic(3));
    private final String linkName = "TestLinkName";
    private final String linkValue = "www.test-value.com";
    private final String labelKey = "TestLabelKey";
    private final String labelValue = "TestLabelValue";
    private Long milestoneId;
    private Long projectId;
    private String projectKey;
    private Long createdRepoId;
    private Launcher createdApiLauncher;
    private Long testRunId;
    private TestRunResultPageR testRunResultPage;

    @BeforeTest
    public void launchTestRun() {
        projectId = LogInBase.project.getId();
        projectKey = LogInBase.project.getKey();
        createdRepoId = LogInBase.repositoryId;
        milestoneId = apiHelperService.createMilestone(projectId, milestoneName);

        String launcherName = "Carina API";
        createdApiLauncher = launcherService.addDefaultApiTestsLauncher(projectId, createdRepoId, launcherName, "api");
        launcherService.launchLauncher(projectId, createdRepoId, createdApiLauncher);
    }

    @AfterTest
    public void deleteLauncher() {
        apiHelperService.deleteMilestone(projectId, milestoneId);
        launcherService.deleteLauncher(projectId, createdRepoId, createdApiLauncher.getId());
    }

    @BeforeMethod
    public void goToTestRunResultPage() {
        if (testRunId == null) {
            AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
            launchesPage.assertPageOpened();
            LaunchCard testRunCard = launchesPage.findTestRunCardByName("Carina API");
            testRunCard.waitFinish(TEST_RUN_WAITING_TIME, LaunchCard.TEST_RUN_FINISH_INTERVAL_WAITING_TIME);
            testRunCard.getRootExtendedElement().click();
            testRunResultPage = TestRunResultPageR.getPageInstance(getDriver());
            testRunId = testRunResultPage.getTestRunIdFromUrl();

            apiHelperService.addLabelToTestRun(testRunId, labelKey, labelValue);
            apiHelperService.addArtRefToTestRun(testRunId, linkName, linkValue);
            testRunResultPage.openPageDirectly(projectKey, testRunId);
        } else {
            testRunResultPage = new TestRunResultPageR(getDriver()).openPageDirectly(projectKey, testRunId);
        }
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    //----------------------------------------------------------------------------------------//

    @Test(dependsOnMethods = "checkTestRunLabelsAndReference", alwaysRun = true)
    public void collapseTestRunView() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCH_VIEW, TestLabelsConstant.HEADER);

        SoftAssert softAssert = new SoftAssert();

        CollapsedTestRunViewHeaderR collapsedTestRunViewHeader = testRunResultPage.collapseTestRunViewHeader();
        collapsedTestRunViewHeader.assertCollapsedTestRunViewElementPresence(softAssert);

        ExpandedTestRunViewHeaderR expandedTestRunViewHeader = testRunResultPage.expandTestRunViewHeader();
        expandedTestRunViewHeader.assertExpandedTestRunVDetailsElementsPresence(softAssert);

        softAssert.assertAll();
    }

    @Test
    public void reviewedTestRun() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCH_VIEW, TestLabelsConstant.HEADER);

        String reviewText = "new comment";
        testRunResultPage.getResultActionBar().openReviewModal().fillReviewAndMaskAsReviewed(reviewText);
        String expectedPopup = MessageEnum.LAUNCH_HAS_BEEN_MARKED_AS_REVIEWED.getDescription();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(testRunResultPage.getPopUp(), expectedPopup,
                String.format("Popup %s is not appeared! ", expectedPopup));
        softAssert.assertTrue(testRunResultPage.getResultActionBar().isTestRunReviewed(),
                "Review button is not mark as reviewed");
        ReviewModalR reviewModal = testRunResultPage.getResultActionBar().openReviewModal();
        softAssert.assertEquals(reviewModal.getReviewText(), reviewText,
                "Review message is not as entered!");
        softAssert.assertTrue(reviewModal.getReviewers().contains(UsersEnum.MAIN_ADMIN.getUser().getUsername()),
                "Reviewer is not present in reviewers list!");
        softAssert.assertTrue(reviewModal.getSubmitButton().isStateMatches(Condition.VISIBLE),
                "Submit should be present on modal!");
        softAssert.assertTrue(reviewModal.getPostUpdateToNotificationChannelCheckbox()
                                         .isStateMatches(Condition.PRESENT),
                "Checkbox should be present on modal!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1324")
    public void shareTestRun() {
        ShareLaunchForm shareFormModal = testRunResultPage.getResultActionBar().openShareFormModal();
        shareFormModal.getEmailInput().sendKeys("invalid_email", false, true);
        shareFormModal.addMessage("new message");

        Assert.assertFalse(shareFormModal.isSendButtonActive(), "Send button should be inactive");
        Assert.assertTrue(shareFormModal.getEmailInput().getText()
                                        .isEmpty(), "Share modal email input must be empty after typing incorrect email");

        shareFormModal.getCloseButton().click();
        ShareLaunchForm shareFormModalCheckCopy = testRunResultPage.getResultActionBar().openShareFormModal();

        shareFormModalCheckCopy.clickCopyUrlButton();
        Assert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear("Link copied to clipboard"),
                "Popup is not as expected!"
        );

        String launchUrl = shareFormModalCheckCopy.getClipboardText();
        Assert.assertEquals(launchUrl, testRunResultPage.getCurrentUrl(), "Url is not as expected!");

        shareFormModal.getCloseButton().click();
        ShareLaunchForm shareFormModalCheckSend = testRunResultPage.getResultActionBar().openShareFormModal();

        shareFormModalCheckSend.getEmailInput().sendKeys("testvalidemail@gmail.com", false, true);
        shareFormModalCheckSend.getEmailInput().getRootExtendedElement().sendKeys(Keys.ESCAPE);
        shareFormModalCheckSend.addMessage("TEST-message_test!@#$%^&*()+=");
        Assert.assertTrue(shareFormModalCheckSend.isSendButtonActive(), "Send button should be active!");

        shareFormModalCheckSend.send();
        Assert.assertTrue(
                testRunResultPage.waitIsPopUpMessageAppear("Launch reports were successfully sent"),
                "Popup is not as expected!"
        );
    }

    @Test
    @TestCaseKey("ZTP-1332")
    public void checkTestRunLabelsAndReference() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCH_VIEW, TestLabelsConstant.HEADER);
        String testWithLabelName = "Labels test";

        long testId1 = testServiceV1.startTestWithMethodName(testRunId, testWithLabelName);
        apiHelperService.addLabelToTest(testRunId, testId1, "label1", "value1");
        apiHelperService.addLabelToTest(testRunId, testId1, "label2", "value2");

        testServiceV1.finishTestAsResult(testRunId, testId1, "FAILED");
        testRunResultPage.openPageDirectly(projectKey, testRunId);

        ExpandedTestRunViewHeaderR expandedTestRunViewHeader = testRunResultPage.expandTestRunViewHeader();
        expandedTestRunViewHeader.clickLabels();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(expandedTestRunViewHeader.containsLabel(new com.zebrunner.automation.api.reporting.domain.Label(labelKey, labelValue)),
                String.format("Label %s: %s was not found!", labelKey, labelValue));

        ComponentUtil.pressEscape(getDriver());

        expandedTestRunViewHeader.clickLinks();

        softAssert.assertTrue(
                expandedTestRunViewHeader.isLinkPresentInList(linkName),
                String.format("Link with name %s was not found!", linkName));

        ComponentUtil.pressEscape(getDriver());

        testRunResultPage.getResultActionBar().showLabels();

        List<ResultTestMethodCardR> testMethodCardsShow = testRunResultPage.getTestCards();
        testMethodCardsShow.stream()
                           .filter(resultTestMethodCard ->
                                   Objects.equals(resultTestMethodCard.getCardTitle().getText(), testWithLabelName))
                           .findFirst()
                           .ifPresent(resultTestMethodCard ->
                                   softAssert.assertTrue(resultTestMethodCard.isLabelsVisible(),
                                           "Element should be visible when click show label "));

        testRunResultPage.getResultActionBar().hideLabels();

        List<ResultTestMethodCardR> testMethodCardsHide = testRunResultPage.getTestCards();
        testMethodCardsHide.stream()
                           .filter(resultTestMethodCard ->
                                   Objects.equals(resultTestMethodCard.getCardTitle().getText(), testWithLabelName))
                           .findFirst().ifPresent(resultTestMethodCard ->
                                   softAssert.assertFalse(resultTestMethodCard.isLabelsVisible(),
                                           "Element shouldn't be visible when click show label "));

        testRunResultPage.getResultActionBar().assignToMilestone().chooseMilestoneAndAssign(milestoneName);
        softAssert.assertTrue(testRunResultPage
                        .waitIsPopUpMessageAppear(MessageEnum.LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE.getDescription()),
                "Popup message is not as expected!");
        softAssert.assertAll();
    }

}
