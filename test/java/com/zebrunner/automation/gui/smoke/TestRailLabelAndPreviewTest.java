package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.CurrentTest;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.config.TestRailProperties;
import com.zebrunner.automation.legacy.TcmType;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.gui.tcm.TcmLabelPreview;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.external.TestRailCasePage;
import com.zebrunner.automation.gui.external.TestRailLogInPage;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class TestRailLabelAndPreviewTest extends LogInBase {

    final String UPDATED_TESTRAIL_CASE_ID = "C1";
    final String UPDATED_TESTRAIL_LABEL_ON_CARD = "1";
    //================================================//
    final String DEPRECATED_TESTRAIL_CASE_ID = "C2";
    final String DEPRECATED_TESTRAIL_LABEL_ON_CARD = "2";
    //================================================//
    final String NONE_TYPE_TESTRAIL_CASE_ID = "C5";
    final String NONE_TYPE_TESTRAIL_LABEL_ON_CARD = "5";
    //================================================//
    final String NOT_EXISTING_TESTRAIL_ID = RandomStringUtils.randomNumeric(8);
    //====================== Messages ==========================//
    final String TEST_CASE_IS_NOT_AVAILABLE = "This test case isn't available.\n"
            + "It may have been deleted or you don't have permissions to view it.";
    final String TEST_RAIL_CASE_UPDATED = "This test case was updated: please review its content.";
    final String TEST_RAIL_CASE_DEPRECATED = "This test case was marked as deprecated.";
    //================================================//
    final String attribute = "Expected result";
    //================================================//
    List<Label> testRailLabelList = List.of(
            new Label(TcmType.TESTRAIL.getLabelKey(), UPDATED_TESTRAIL_CASE_ID),
            new Label(TcmType.TESTRAIL.getLabelKey(), DEPRECATED_TESTRAIL_CASE_ID),
            new Label(TcmType.TESTRAIL.getLabelKey(), NONE_TYPE_TESTRAIL_CASE_ID),
            new Label(TcmType.TESTRAIL.getLabelKey(), NOT_EXISTING_TESTRAIL_ID)
    );
    private Project project;
    private Launch launch;
    private TestExecution test;

    @BeforeClass
    public void getProjectAndSetUpLaunch() {
        project = LogInBase.project;

        IntegrationManager.addIntegration(project.getId(), Tool.TEST_RAIL);

        launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());

        test = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());

        apiHelperService.addLabelsToTest(launch.getId(), test.getId(), testRailLabelList);

        testService.finishTestAsResult(launch.getId(), test.getId(), "PASSED");
        testRunService.finishTestRun(launch.getId());
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    // ====================================== Tests ===========================================

    @Maintainer("Gmamaladze")
    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1284", "ZTP-1285", "ZTP-1287", "ZTP-1289"})
    public void verifyTestRailPopUp() {
        this.skipTestRailTestIfDisabled();

        WebDriver webDriver = super.getDriver();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard.findTcmLabel(UPDATED_TESTRAIL_LABEL_ON_CARD)
                                                  .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", UPDATED_TESTRAIL_LABEL_ON_CARD)))
                                                  .clickOnTcmLabel();

        Assert.assertTrue(tcmLabelPreview.isTcmLabelPreviewOpened(), "TCM label preview should be opened !");

        String initialClassOfElement = tcmLabelPreview.getScrollableElement().getAttributeValue("class");
        tcmLabelPreview.scrollToAttribute(attribute);
        String afterActionClassOfElement = tcmLabelPreview.getScrollableElement().getAttributeValue("class");

        Assert.assertNotEquals(
                initialClassOfElement, afterActionClassOfElement,
                "The element should be scrollable (class of element should be changed)!"
        );
        Assert.assertTrue(testCard.isTcmLabelVisible(UPDATED_TESTRAIL_LABEL_ON_CARD), "Label should be visible !");

        tcmLabelPreview.close();
        Assert.assertFalse(tcmLabelPreview.isTcmLabelPreviewOpened(), "TCM label preview should be closed !");
    }

    private void skipTestRailTestIfDisabled() {
        TestRailProperties testRailProperties = ConfigHelper.getTestRailProperties();

        if (!testRailProperties.getEnabled()) {
            CurrentTest.revertRegistration();

            throw new SkipException("TestRail tests are disabled");
        }
    }

    @Test(priority = 5)
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1286")
    public void verifyTestRailCaseOpeningInNewTab() {
        this.skipTestRailTestIfDisabled();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard.findTcmLabel(UPDATED_TESTRAIL_LABEL_ON_CARD)
                                                  .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", UPDATED_TESTRAIL_LABEL_ON_CARD)))
                                                  .clickOnTcmLabelAndWaitIntegration();

        String tcmLabelPreviewTitle = tcmLabelPreview.getCaseTitleText();

        TestRailLogInPage testRailLogInPage = tcmLabelPreview.clickCaseTitleLink();

        softAssert.assertEquals(PageUtil.getNumberOfOpenedWindows(getDriver()), 2, "Opened tab should be 2");

        PageUtil.toOtherTabWithoutClosingFirstOne(getDriver());

        if (testRailLogInPage.isPageOpened(10)) {
            testRailLogInPage.typeEmail(ConfigHelper.getTestRailProperties().getUsername());
            testRailLogInPage.typePassword(ConfigHelper.getTestRailProperties().getApiKey());
            testRailLogInPage.clickLoginButton();
        }

        TestRailCasePage testRailCasePage = new TestRailCasePage(getDriver());

        softAssert.assertTrue(testRailCasePage.isPageOpened(), "Test case page should be opened !");
        softAssert.assertEquals(testRailCasePage.getTestCaseTitleText(), tcmLabelPreviewTitle,
                "Test case title is not as expected !");

        PageUtil.toOtherTab(getDriver());

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1299")
    @Maintainer("Gmamaladze")
    public void verifyTestCasePreviewForNonExistingTestCase() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard.findTcmLabel(NOT_EXISTING_TESTRAIL_ID)
                                                  .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", NOT_EXISTING_TESTRAIL_ID)))
                                                  .clickOnTcmLabel();

        Assert.assertEquals(tcmLabelPreview.getErrorText(), TEST_CASE_IS_NOT_AVAILABLE,
                "Notification is not as excepted !");
    }

    @Test
    @TestCaseKey("ZTP-1300")
    @Maintainer("Gmamaladze")
    public void verifyTestRailPreviewOnTestDetailsPageAndPossibilityToRedirectToTestrail() {
        this.skipTestRailTestIfDisabled();

        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());
        testCard.clickOnCard();

        TestDetailsPageR testDetailsPageR = new TestDetailsPageR(getDriver());
        softAssert.assertTrue(testDetailsPageR.isPageOpened(), "Test details page should be opened !");

        pause(2);

        TcmLabelPreview tcmLabelPreview = testDetailsPageR.getTestHeader().findTcmLabel(UPDATED_TESTRAIL_LABEL_ON_CARD)
                                                          .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", UPDATED_TESTRAIL_LABEL_ON_CARD)))
                                                          .clickOnTcmLabel();

        softAssert.assertTrue(tcmLabelPreview.isTcmLabelPreviewOpened(), "TCM label preview should be opened !");

        String initialClassOfElement = tcmLabelPreview.getScrollableElement().getAttributeValue("class");
        tcmLabelPreview.scrollToAttribute(attribute);
        String afterActionClassOfElement = tcmLabelPreview.getScrollableElement().getAttributeValue("class");

        softAssert.assertNotEquals(initialClassOfElement, afterActionClassOfElement,
                "The element should be scrollable (class of element should be changed)!");

        String tcmLabelPreviewTitle = tcmLabelPreview.getCaseTitleText();

        TestRailLogInPage testRailLogInPage = tcmLabelPreview.clickCaseTitleLink();

        softAssert.assertEquals(PageUtil.getNumberOfOpenedWindows(getDriver()), 2, "Opened tab should be 2");

        PageUtil.toOtherTabWithoutClosingFirstOne(getDriver());

        if (testRailLogInPage.isPageOpened(10)) {
            testRailLogInPage.typeEmail(ConfigHelper.getTestRailProperties().getUsername());
            testRailLogInPage.typePassword(ConfigHelper.getTestRailProperties().getApiKey());
            testRailLogInPage.clickLoginButton();
        }

        TestRailCasePage testRailCasePage = new TestRailCasePage(getDriver());

        softAssert.assertTrue(testRailCasePage.isPageOpened(), "Test case page should be opened !");
        softAssert.assertEquals(testRailCasePage.getTestCaseTitleText(), tcmLabelPreviewTitle,
                "Test case title is not as expected !");

        PageUtil.toOtherTab(getDriver());
        softAssert.assertAll();
    }

    @Test(enabled = false)
    @TestCaseKey({"ZTP-1301", "ZTP-1303"})
    @Maintainer("Gmamaladze")
    public void verifyTestRailPreviewWithUpdatedStatus() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard.findTcmLabel(UPDATED_TESTRAIL_LABEL_ON_CARD)
                                                  .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", UPDATED_TESTRAIL_LABEL_ON_CARD)))
                                                  .clickOnTcmLabelAndWaitIntegration();

        Assert.assertEquals(tcmLabelPreview.getTestCaseStatusNotification(), TEST_RAIL_CASE_UPDATED,
                "Notification is not as excepted");
    }

    @Test(enabled = false)
    @TestCaseKey({"ZTP-1302", "ZTP-1303"})
    @Maintainer("Gmamaladze")
    public void verifyTestRailPreviewWithDeprecatedStatus() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard.findTcmLabel(DEPRECATED_TESTRAIL_LABEL_ON_CARD)
                                                  .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", DEPRECATED_TESTRAIL_LABEL_ON_CARD)))
                                                  .clickOnTcmLabelAndWaitIntegration();

        Assert.assertEquals(tcmLabelPreview.getTestCaseStatusNotification(), TEST_RAIL_CASE_DEPRECATED,
                "Notification is not as excepted");
    }

    @Test
    @TestCaseKey({"ZTP-1304", "ZTP-1305"})
    @Maintainer("Gmamaladze")
    public void verifyTestRailPreviewWithNoneTypeStatus() {
        this.skipTestRailTestIfDisabled();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard.findTcmLabel(NONE_TYPE_TESTRAIL_LABEL_ON_CARD)
                                                  .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", NONE_TYPE_TESTRAIL_LABEL_ON_CARD)))
                                                  .clickOnTcmLabelAndWaitIntegration();

        Assert.assertFalse(tcmLabelPreview.isStatusNotificationPresent(), "Any status notification should not be present !");
    }
}