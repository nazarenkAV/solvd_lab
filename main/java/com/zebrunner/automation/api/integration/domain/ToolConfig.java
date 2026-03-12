package com.zebrunner.automation.api.integration.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import com.zebrunner.automation.api.integration.domain.vo.BrowserStackConfig;
import com.zebrunner.automation.api.integration.domain.vo.GithubConfig;
import com.zebrunner.automation.api.integration.domain.vo.JiraConfig;
import com.zebrunner.automation.api.integration.domain.vo.LambdaTestConfig;
import com.zebrunner.automation.api.integration.domain.vo.SauceLabsConfig;
import com.zebrunner.automation.api.integration.domain.vo.SlackConfig;
import com.zebrunner.automation.api.integration.domain.vo.TeamsWebhookConfig;
import com.zebrunner.automation.api.integration.domain.vo.TestRailConfig;
import com.zebrunner.automation.api.integration.domain.vo.TestingBotConfig;
import com.zebrunner.automation.api.integration.domain.vo.ZebrunnerDeviceFarmConfig;
import com.zebrunner.automation.api.integration.domain.vo.ZebrunnerEngineConfig;

@JsonSubTypes({
        @JsonSubTypes.Type(value = BrowserStackConfig.class, name = "BROWSER_STACK"),
        @JsonSubTypes.Type(value = GithubConfig.class, name = "GITHUB"),
        @JsonSubTypes.Type(value = JiraConfig.class, name = "JIRA"),
        @JsonSubTypes.Type(value = LambdaTestConfig.class, name = "LAMBDA_TEST"),
        @JsonSubTypes.Type(value = SauceLabsConfig.class, name = "SAUCE_LABS"),
        @JsonSubTypes.Type(value = SlackConfig.class, name = "SLACK"),
        @JsonSubTypes.Type(value = TeamsWebhookConfig.class, name = "TEAMS_WEBHOOK"),
        @JsonSubTypes.Type(value = TestingBotConfig.class, name = "TESTING_BOT"),
        @JsonSubTypes.Type(value = TestRailConfig.class, name = "TEST_RAIL"),
        @JsonSubTypes.Type(value = ZebrunnerDeviceFarmConfig.class, name = "ZEBRUNNER_DEVICE_FARM"),
        @JsonSubTypes.Type(value = ZebrunnerEngineConfig.class, name = "ZEBRUNNER_ENGINE")
})
public interface ToolConfig {
}
