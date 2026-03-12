package com.zebrunner.automation.api.tcm.domain.request.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zebrunner.automation.api.tcm.domain.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddTestRunCaseResultsRequest {

    private List<Item> items;

    @Data
    @NoArgsConstructor
    public static class Item {

        private List<Long> testCaseIds;
        private ResultStatus status;
        private String details;
        private IssueType issueType;
        private String issueId;
        private List<Attachment> attachments = List.of();

        private TestCaseExecutionType executionType;
        private Long executionTimeInMillis;

    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultStatus {

        private Long id;
        private Integer relativePosition;
        private String name;
        private String colorHex;
        private Boolean isFinal;
        private Boolean deleted;
        private Boolean isAssignable;
    }

    public enum IssueType {

        JIRA,
        GITHUB

    }

    public enum TestCaseExecutionType {

        MANUAL,
        AUTOMATED

    }

}
