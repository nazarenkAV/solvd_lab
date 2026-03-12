package com.zebrunner.automation.api.tcm.domain.request.v1;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.zebrunner.automation.api.tcm.domain.TestRun;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTestPlanRequest {
    private String title;
    private List<CreateTestRunRequest.TestCase> testCases;
    private List<CreateTestRunRequest.Configuration> configurations;
    private String description;
    private CreateTestRunRequest.Environment environment;
    private List<TestRun> testRuns;
}
