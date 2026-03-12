package com.zebrunner.automation.api.reporting.service;

import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;

import java.util.List;

@Deprecated
public interface TestServiceAPIV1 {
    Long startTest(Long testRunId);

    TestExecution startTest(TestExecution testExecution, Long testRunId);

    Long startTestWithMethodName(Long testRunId, String methodName);

    String finishTestAsResult(Long testRunId, Long testId, String result);

    String finishTestAsResult(Long testRunId, Long testId, FinishTestRequest finishTestRequest);

    TestExecution finishTextExecutionAsResult(Long testRunId, Long testId, String result);

}
