package com.zebrunner.automation.legacy;

import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.service.TestRunServiceAPIImplV1;
import com.zebrunner.automation.api.reporting.service.TestServiceV1Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated
public class PreparationUtil {
    private static final TestRunServiceAPIImplV1 testRunService = new TestRunServiceAPIImplV1();
    private static final TestServiceV1Impl testService = new TestServiceV1Impl();

    public static TestClassLaunchDataStorage startAndFinishLaunchWithAllTestStatuses(String projectKey) {
        Launch launch = testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());

        List<TestExecution> testsList = new ArrayList<>();

        TestExecution secondTestExecutionFailed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testService.finishTextExecutionAsResult(launch.getId(), secondTestExecutionFailed.getId(), "FAILED");
        testsList.add(secondTestExecutionFailed);

        TestExecution testExecutionFailed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testService.finishTextExecutionAsResult(launch.getId(), testExecutionFailed.getId(), "FAILED");
        testsList.add(testExecutionFailed);

        TestExecution testExecutionPassed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testService.finishTextExecutionAsResult(launch.getId(), testExecutionPassed.getId(), "PASSED");
        testsList.add(testExecutionPassed);

        TestExecution testExecutionSkipped = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testService.finishTextExecutionAsResult(launch.getId(), testExecutionSkipped.getId(), "SKIPPED");
        testsList.add(testExecutionSkipped);

        TestExecution testExecutionAborted = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testService.finishTextExecutionAsResult(launch.getId(), testExecutionAborted.getId(), "ABORTED");
        testsList.add(testExecutionAborted);

        TestExecution testExecutionInProgress = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testsList.add(testExecutionInProgress);

        return TestClassLaunchDataStorage.builder()
                .launch(launch)
                .testsList(testsList)
                .build();
    }

    public static TestClassLaunchDataStorage startAndFinishLaunchWithRandomTestStatuses(String projectKey, int numberOfTests) {
        if (numberOfTests < 2) {
            throw new IllegalArgumentException("numberOfTests must be at least 2");
        }

        Launch launch = testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());

        List<TestExecution> testsList = new ArrayList<>();
        List<TestExecution> passedTests = new ArrayList<>();
        List<TestExecution> failedTests = new ArrayList();

        // Ensure at least 1 passed and 1 failed test
        int minimumPassedTests = 1;
        int minimumFailedTests = 1;

        // Randomly select 1 test to pass
        for (int i = 0; i < minimumPassedTests; i++) {
            TestExecution testExecution = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
            testExecution = testService.finishTextExecutionAsResult(launch.getId(), testExecution.getId(), "PASSED");
            passedTests.add(testExecution);
            testsList.add(testExecution);
        }

        // Randomly select 1 test to fail
        for (int i = 0; i < minimumFailedTests; i++) {
            TestExecution testExecution = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
            testExecution = testService.finishTextExecutionAsResult(launch.getId(), testExecution.getId(), "FAILED");
            failedTests.add(testExecution);
            testsList.add(testExecution);
        }

        // Randomly select the remaining tests to pass or fail
        for (int i = minimumPassedTests + minimumFailedTests; i < numberOfTests; i++) {
            TestExecution testExecution = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
            int random = new Random().nextInt(2); // Generate a random number 0 or 1
            if (random == 0) {
                testExecution = testService.finishTextExecutionAsResult(launch.getId(), testExecution.getId(), "PASSED");
                passedTests.add(testExecution);
            } else {
                testExecution = testService.finishTextExecutionAsResult(launch.getId(), testExecution.getId(), "FAILED");
                failedTests.add(testExecution);
            }
            testsList.add(testExecution);
        }

        launch = testRunService.finishLaunch(launch.getId());

        return TestClassLaunchDataStorage.builder()
                .launch(launch)
                .testsList(testsList)
                .passedTests(passedTests)
                .failedTests(failedTests)
                .build();
    }

    public static TestClassLaunchDataStorage startAndFinishLaunchWithTests(int numberOfPassed, int numberOfFailed, String projectKey) {
        Launch launch = testRunService.startTestRunWithName(projectKey, Launch.getRandomLaunch());

        List<TestExecution> testsList = new ArrayList<>();
        List<TestExecution> passedTests = new ArrayList<>();
        List<TestExecution> failedTests = new ArrayList<>();

        Random random = new Random();

        while (numberOfPassed > 0 || numberOfFailed > 0) {
            // Decide whether to start a "PASSED" or "FAILED" test
            boolean startPassed = random.nextBoolean();

            if (startPassed && numberOfPassed > 0) {
                TestExecution testExecutionPassed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
                testExecutionPassed = testService.finishTextExecutionAsResult(launch.getId(), testExecutionPassed.getId(), "PASSED");
                passedTests.add(testExecutionPassed);
                testsList.add(testExecutionPassed);
                numberOfPassed--;
            } else if (!startPassed && numberOfFailed > 0) {
                TestExecution testExecutionFailed = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
                testExecutionFailed = testService.finishTextExecutionAsResult(launch.getId(), testExecutionFailed.getId(), "FAILED");
                failedTests.add(testExecutionFailed);
                testsList.add(testExecutionFailed);
                numberOfFailed--;
            }
        }

        launch = testRunService.finishLaunch(launch.getId());

        return TestClassLaunchDataStorage.builder()
                .launch(launch)
                .testsList(testsList)
                .passedTests(passedTests)
                .failedTests(failedTests)
                .build();
    }
}
