package com.zebrunner.automation.gui.smoke;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.CurrentTest;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.JiraProperties;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;

@Slf4j
@Maintainer("Gmamaladze")
public class FilterTestsByStatus extends LogInBase {
    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private TestClassLaunchDataStorage testClassLaunchDataStorage;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @BeforeMethod
    public void createLaunchWithTests() {
        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithAllTestStatuses(project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    @DataProvider(name = "statusProvider")
    public Object[][] statusProvider() {
        return new Object[][]{
                {Dropdown.DropdownItemsEnum.STATUS_FAILED},
                {Dropdown.DropdownItemsEnum.STATUS_SKIPPED},
                {Dropdown.DropdownItemsEnum.STATUS_PASSED},
                {Dropdown.DropdownItemsEnum.STATUS_ABORTED},
                {Dropdown.DropdownItemsEnum.STATUS_IN_PROGRESS},
        };
    }

    // -------------------------------------------TEST---------------------------------------------------------------------

    @Test
    @TestCaseKey("ZTP-1336")
    public void filterByFailedNoLinkedIssueStatus() {
        this.skipTestIfJiraNotEnabled();

        WebDriver webDriver = super.getDriver();

        IntegrationManager.addIntegration(project.getId(), Tool.JIRA);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(webDriver).openPageDirectly(
                project.getKey(), testClassLaunchDataStorage.getLaunch().getId()
        );

        ActionsBlockR actionsBlock = testRunResultPage.getActionsBlockR();
        actionsBlock.selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_FAILED_NO_LINKED_ISSUE);

        Assert.assertTrue(
                actionsBlock.isStatusSelected(Dropdown.DropdownItemsEnum.STATUS_FAILED_NO_LINKED_ISSUE),
                "Selected status should be '" + Dropdown.DropdownItemsEnum.STATUS_FAILED_NO_LINKED_ISSUE + "'"
        );

        List<ResultTestMethodCardR> testCards = testRunResultPage.getTestCards();
        Assert.assertFalse(testCards.isEmpty(), "The testCards list is empty !");

        ResultTestMethodCardR failedTest = testCards.get(0);
        failedTest.linkIssueViaTestCard("ZEB-133");

        for (ResultTestMethodCardR testCard : testCards) {
            Assert.assertEquals(
                    testCard.getStatusByLeftBorderColor(), "FAILED",
                    String.format("Test status should be '%s' - test card left border colour should be red !", testCard.getStatusByLeftBorderColor())
            );
            Assert.assertFalse(testCard.isLinkedIssuePresent(), "Linked issue jira card shouldn't present !");
        }
    }

    private void skipTestIfJiraNotEnabled() {
        JiraProperties jiraProperties = ConfigHelper.getJiraProperties();

        if (!jiraProperties.getEnabled()) {
            CurrentTest.revertRegistration();

            throw new SkipException("Jira is not enabled");
        }
    }

    @Test(dataProvider = "statusProvider")
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1336")
    public void filterByStatus(Dropdown.DropdownItemsEnum status) {
        SoftAssert softAssert = new SoftAssert();

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.selectSingleStatusAndClose(status);

        softAssert.assertTrue(actionsBlockR.isStatusSelected(status),
                String.format("Selected status should be '%s' !", status));

        List<ResultTestMethodCardR> testCards = testRunResultPage.getTestCards();
        Assert.assertFalse(testCards.isEmpty(), "The testCards list is empty !");

        for (ResultTestMethodCardR testCard : testCards) {
            softAssert.assertEquals(testCard.getStatusByLeftBorderColor(), StringUtil.replaceSpaceWithHyphen(status.getItemValue()
                                                                                                                   .toUpperCase()),
                    String.format("Test status should be '%s'!", status.getItemValue()));
        }

        softAssert.assertAll();
    }
}
