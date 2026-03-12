package com.zebrunner.automation.legacy;

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
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.api.integration.domain.ProjectsMapping;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.integration.domain.ToolConfig;
import com.zebrunner.automation.api.integration.domain.request.v2.SaveIntegrationRequest;

import java.util.List;

@Deprecated
public class ValidConfigProvider {

    public static SaveIntegrationRequest getSaveIntegrationRequest(Long projectId, Tool tool) {

        ToolConfig toolConfig = getToolConfig(tool);
        ProjectsMapping projectsMapping = getProjectsMapping(tool, projectId);

        return SaveIntegrationRequest.enabledWith(toolConfig, projectsMapping);
    }

    private static ToolConfig getToolConfig(Tool tool) {
        switch (tool) {
            case JIRA:
                return JiraConfig.of(ConfigHelper.getJiraProperties());
            case BROWSER_STACK:
                return BrowserStackConfig.of(ConfigHelper.getBrowserStackProperties());
            case SAUCE_LABS:
                return SauceLabsConfig.of(ConfigHelper.getSauceLabsProperties());
            case SLACK:
                return SlackConfig.of(ConfigHelper.getSlackProperties());
            case TEAMS_WEBHOOK:
                return TeamsWebhookConfig.createValidConfig();
            case GITHUB:
                return GithubConfig.of(ConfigHelper.getGithubProperties());
            case TEST_RAIL:
                return TestRailConfig.of(ConfigHelper.getTestRailProperties());
            case LAMBDA_TEST:
                return LambdaTestConfig.of(ConfigHelper.getLambdaTestProperties());
            case TESTING_BOT:
                return TestingBotConfig.createValidConfig();
            case ZEBRUNNER_DEVICE_FARM:
                return ZebrunnerDeviceFarmConfig.createValidConfig();

            default:
                throw new IllegalArgumentException("Unknown tool: " + tool);
        }
    }

    private static ProjectsMapping getProjectsMapping(Tool tool, Long projectId) {
        switch (tool) {
            case JIRA:
                return ProjectsMapping.forZebrunnerProjectIds(List.of(projectId));
            case BROWSER_STACK:
            case SAUCE_LABS:
            case ZEBRUNNER_DEVICE_FARM:
            case TESTING_BOT:
            case LAMBDA_TEST:
            case GITHUB:
            case TEST_RAIL:
            case TEAMS_WEBHOOK:
            case SLACK:
            case ZEBRUNNER_ENGINE:
                return ProjectsMapping.forProjectIds(List.of(projectId));
            default:
                throw new IllegalArgumentException("Unknown tool: " + tool);
        }
    }
}
