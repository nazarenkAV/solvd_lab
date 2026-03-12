package com.zebrunner.automation.api.reporting.service;

import com.zebrunner.automation.api.reporting.domain.Launch;

@Deprecated
public interface TestRunServiceAPIV1 {

    long start(String projectKey);

    long startTestRunWithName(String projectKey, String name);

    Launch startTestRunWithName(String projectKey, Launch launch);

    long startTestRunWithCertainConfig(String projectKey, String config);

    String finishTestRun(long testRunId);

    void deleteLaunch(long projectId, long testRunId);

}
