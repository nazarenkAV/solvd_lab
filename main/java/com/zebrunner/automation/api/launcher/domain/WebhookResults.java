package com.zebrunner.automation.api.launcher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebhookResults {
    private boolean finished;
    private String status;
    private TestExecutionResults testExecutions;
}
