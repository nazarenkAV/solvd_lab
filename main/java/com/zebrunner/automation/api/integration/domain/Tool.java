package com.zebrunner.automation.api.integration.domain;

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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tool {

    ZEBRUNNER_ENGINE("zebrunner-engine", ZebrunnerEngineConfig.class),
    ZEBRUNNER_DEVICE_FARM("zebrunner-device-farm", ZebrunnerDeviceFarmConfig.class),
    BROWSER_STACK("browser-stack", BrowserStackConfig.class),
    LAMBDA_TEST("lambda-test", LambdaTestConfig.class),
    SAUCE_LABS("sauce-labs", SauceLabsConfig.class),
    TESTING_BOT("testing-bot", TestingBotConfig.class),
    //   JENKINS("jenkins", JenkinsConfig.class),

    JIRA("jira", JiraConfig.class),
    GITHUB("github", GithubConfig.class),
    TEST_RAIL("test-rail", TestRailConfig.class),
    //   OCTANE("octane", OctaneConfig.class),
    //  AZURE_DEVOPS("azure-devops", AzureDevOpsConfig.class),

    TEAMS_WEBHOOK("teams-webhook", TeamsWebhookConfig.class),
    SLACK("slack", SlackConfig.class);


    private final String apiPathVariable;
    private final Class<? extends ToolConfig> configClass;

}


