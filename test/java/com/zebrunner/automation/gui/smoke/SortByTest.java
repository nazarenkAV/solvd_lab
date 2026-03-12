package com.zebrunner.automation.gui.smoke;

import com.google.common.collect.Comparators;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.config.TestMaintainers;
import com.zebrunner.automation.legacy.TestCardStatuesEnum;
import com.zebrunner.automation.gui.reporting.launch.ActionsBlockR;
import com.zebrunner.automation.gui.reporting.launch.FailureTagModal;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.common.SelectWrapperMenu;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;
import com.zebrunner.automation.legacy.SortUtil;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Slf4j
@Maintainer("Gmamaladze")
public class SortByTest extends LogInBase {

    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private Launch launch;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @BeforeMethod
    public void createLaunchWithTests() {
        launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());

        OffsetDateTime currentDateTime = OffsetDateTime.now();

        OffsetDateTime fiveMinuteAgo = currentDateTime.minus(5, ChronoUnit.MINUTES);
        OffsetDateTime fourMinuteAgo = currentDateTime.minus(4, ChronoUnit.MINUTES);
        OffsetDateTime threeMinuteAgo = currentDateTime.minus(3, ChronoUnit.MINUTES);
        OffsetDateTime twoMinuteAgo = currentDateTime.minus(2, ChronoUnit.MINUTES);
        OffsetDateTime oneMinuteAgo = currentDateTime.minus(1, ChronoUnit.MINUTES);

        TestExecution testExecutionFailed = testService.startTest(TestExecution.getTestExecution("Test - a", oneMinuteAgo), launch.getId());
        testService.finishTestAsResult(launch.getId(), testExecutionFailed.getId(), FinishTestRequest.getRequestWithReason(RandomStringUtils.randomAlphabetic(6)));

        TestExecution secondTestExecutionFailed = testService.startTest(TestExecution.getTestExecution("Test - b", twoMinuteAgo), launch.getId());
        testService.finishTestAsResult(launch.getId(), secondTestExecutionFailed.getId(), FinishTestRequest.getRequestWithReason(RandomStringUtils.randomAlphabetic(6)));

        TestExecution thirdTestExecutionFailed = testService.startTest(TestExecution.getTestExecution("Test - c", threeMinuteAgo), launch.getId());
        testService.finishTestAsResult(launch.getId(), thirdTestExecutionFailed.getId(), FinishTestRequest.getRequestWithReason(RandomStringUtils.randomAlphabetic(6)));

        TestExecution testExecutionPassed = testService.startTest(TestExecution.getTestExecution("Test - d", fourMinuteAgo), launch.getId());
        testService.finishTestAsResult(launch.getId(), testExecutionPassed.getId(), "PASSED");

        TestExecution testExecutionSkipped = testService.startTest(TestExecution.getTestExecution("Test - e", OffsetDateTime.now()), launch.getId());
        testService.finishTestAsResult(launch.getId(), testExecutionSkipped.getId(), "SKIPPED");

        TestExecution testExecutionAborted = testService.startTest(TestExecution.getTestExecution("Test - f", fiveMinuteAgo), launch.getId());
        testService.finishTestAsResult(launch.getId(), testExecutionAborted.getId(), "ABORTED");

        testRunService.finishTestRun(launch.getId());
        testRunIdList.add(launch.getId());
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }


    // -------------------------------------------TEST---------------------------------------------------------------------


    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1339")
    public void sortByStatus() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.openAndSelectSort(SelectWrapperMenu.WrapperItemEnum.SORT_STATUS);

        pause(2);

        List<TestCardStatuesEnum> orderedStatus = testRunResultPage.getTestCards().stream()
                                                                   .map(ResultTestMethodCardR::getEnumStatusByLeftBorderColor)
                                                                   .collect(Collectors.toList());

        boolean result = SortUtil.isSorted(orderedStatus, Comparator.comparing(TestCardStatuesEnum::getOrderId), true);
        Assert.assertTrue(result, "List should be ordered by statuses !");
    }


    @Test
    @TestCaseKey("ZTP-1339")
    @Maintainer(TestMaintainers.DKAZAK)
    public void sortByFailureTag() {
        TestRunResultPageR launchPage = new TestRunResultPageR(super.getDriver()).openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR failedTestCard = launchPage.getFailedTestCards().get(1);

        // Selecting different tags for sorting test
        String failureTag = failedTestCard.getFailureTagText();
        FailureTagModal failureTagModal = failedTestCard.getFailureTagModal();

        if (!failureTag.equalsIgnoreCase("business issue")) {
            failureTagModal.clickBusinessIssueTagButton();
            failureTagModal.clickSaveButton();
        } else {
            failureTagModal.clickUncategorizedTagButton();
            failureTagModal.clickSaveButton();
        }

        ActionsBlockR actionsBlockR = launchPage.getActionsBlockR();
        actionsBlockR.openAndSelectSort(SelectWrapperMenu.WrapperItemEnum.SORT_FAILURE_TAG);
        super.pause(2);

        List<String> failureTags = StreamUtils.filterToStream(
                                                      launchPage.getTestCards(),
                                                      ResultTestMethodCardR::isFailureTagButtonPresent
                                              )
                                              .map(ResultTestMethodCardR::getFailureTagText)
                                              .collect(Collectors.toList());

        Assert.assertFalse(failureTags.isEmpty(), "Could not sort by failure tags: failure tags list is empty");
        Assert.assertTrue(Comparators.isInOrder(failureTags, String::compareTo), "Tests should be ordered by failure tags in ascending order");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1339")
    public void sortByFastest() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.openAndSelectSort(SelectWrapperMenu.WrapperItemEnum.SORT_FASTEST_FIRST);

        pause(2);

        List<Integer> orderedCardsDuration = testRunResultPage.getTestCards().stream()
                                                              .map(ResultTestMethodCardR::getDurationText)
                                                              .map(StringUtil::convertToSeconds)
                                                              .collect(Collectors.toList());

        boolean result = SortUtil.isSorted(orderedCardsDuration, Comparator.naturalOrder(), true);
        Assert.assertTrue(result, "List should be ordered by fastest first !");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1339")
    public void sortBySlowest() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.openAndSelectSort(SelectWrapperMenu.WrapperItemEnum.SORT_SLOWEST_FIRST);

        pause(2);

        List<Integer> orderedCardsDuration = testRunResultPage.getTestCards().stream()
                                                              .map(ResultTestMethodCardR::getDurationText)
                                                              .map(StringUtil::convertToSeconds)
                                                              .collect(Collectors.toList());

        boolean result = SortUtil.isSorted(orderedCardsDuration, Comparator.reverseOrder(), true);
        Assert.assertTrue(result, "List should be ordered by slowest first !");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1339")
    public void sortByName() {
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ActionsBlockR actionsBlockR = testRunResultPage.getActionsBlockR();
        actionsBlockR.openAndSelectSort(SelectWrapperMenu.WrapperItemEnum.SORT_NAME);

        pause(2);

        List<String> orderedCardsName = testRunResultPage.getTestCards().stream()
                                                         .map(ResultTestMethodCardR::getCardTitleText)
                                                         .collect(Collectors.toList());

        boolean result = SortUtil.isSorted(orderedCardsName, Comparator.naturalOrder(), true);
        Assert.assertTrue(result, "List should be ordered by name !");
    }

}
