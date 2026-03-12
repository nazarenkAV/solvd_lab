package com.zebrunner.automation.api.launcher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TestExecutionResults {
    private Integer passed;
    private Integer failed;
    private Integer failedAsKnown;
    private Integer skipped;
    private Integer aborted;
    private Integer inProgress;
    private Integer total;
}
