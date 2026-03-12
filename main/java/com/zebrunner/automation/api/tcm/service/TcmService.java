package com.zebrunner.automation.api.tcm.service;

import com.zebrunner.automation.api.tcm.domain.SharedStepsBunch;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunResult;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.TestCaseFieldsLayout;
import com.zebrunner.automation.api.tcm.domain.TestCaseSystemFields;
import com.zebrunner.automation.api.tcm.domain.TestPlan;

import java.util.List;

@Deprecated
public interface TcmService {

    TestSuite createTestSuite(Long projectId, TestSuite testSuite);

    TestCase createTestCase(Long projectId, Long testSuiteId, TestCase testCase);

    TestCase createTestCase(Long projectId, Long testSuiteId);

    TestCase updateTestCase(Long projectId, Long testCaseId, TestCase testCase);

    List<TestCase> createTestCases(Long projectId, Long testSuiteId, int numberOfCases);

    SharedStepsBunch createSharedStep(Long projectId, SharedStepsBunch bunch);

    void deleteTestCase(Long projectId, Long testCaseId);

    void deleteTestSuite(Long projectId, Long testSuiteId);

    TestRun createTestRun(Long projectId, TestRun testRun);

    TestRun createTestRun(Long projectId, List<TestCase> testCases, TestRun testRun);

    List<TestRun> createTestRuns(Long projectId, int numberOfRuns);

    TestPlan createTestPlan(Long projectId, TestPlan testPlan);

    TestRun updateTestRun(Long projectId, TestRun testRun);

    void addTestRunResults(Long projectId, Long testRunId, List<Long> testCaseIds, TestRunResult result);

    void addTestRunResults(Long projectId, TestRun testRun, TestCase testCase, TestRunSettings testRunSettings, String statusName);

    void addTestRunResults(Long projectId, TestRun testRun, List<TestCase> testCases, TestRunSettings testRunSettings, String statusName);

    void assignTestCases(Long projectId, TestRun testRun, List<TestCase> testCases, Long assigneeId);

    TestRunSettings getTestRunSettings(Long projectId);

    TestCaseFieldsLayout getTestCaseFields(Long projectId);

    TestCaseSystemFields getTestCaseSystemFields(Long projectId);

    void closeTestRun(Long projectId, Long testRunId);
}
