package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.zebrunner.automation.api.tcm.domain.request.v1.AddTestRunCaseResultsRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestRunResult {

    private Long testCaseId;
    private AddTestRunCaseResultsRequest.ResultStatus status;
    private String details;
    private AddTestRunCaseResultsRequest.IssueType issueType;
    private String issueId;
    private List<Attachment> attachments;

    private TestCaseExecutionType executionType = TestCaseExecutionType.MANUAL;
    private Long executionTimeInMillis;

    private TestRunResult(AddTestRunCaseResultsRequest.ResultStatus status) {
        this.status = status;
    }

    public static TestRunResult with(AddTestRunCaseResultsRequest.ResultStatus status) {
        return new TestRunResult(status);
    }

    public TestRunResult withIssue(AddTestRunCaseResultsRequest.IssueType issueType, String issueId) {
        this.issueType = issueType;
        this.issueId = issueId;
        return this;
    }

    public enum TestCaseExecutionType {

        MANUAL,
        AUTOMATED

    }

}
