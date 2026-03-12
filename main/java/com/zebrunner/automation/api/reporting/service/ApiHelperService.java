package com.zebrunner.automation.api.reporting.service;

import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.launcher.domain.LauncherWeb;
import com.zebrunner.automation.api.reporting.domain.LogAndScreenshotItem;
import com.zebrunner.automation.api.reporting.domain.LogItem;
import com.zebrunner.automation.api.reporting.domain.IssueReference;
import com.zebrunner.automation.api.reporting.domain.Milestone;
import com.zebrunner.automation.api.tcm.domain.ConfigurationGroup;
import com.zebrunner.automation.api.tcm.domain.ConfigurationOption;
import io.restassured.response.Response;

import java.util.List;

@Deprecated
public interface ApiHelperService {

    Long startTR(String projectKey);

    Long startTRWithCertainConfig(String projectKey, String config);

    void startSession(Long testRunId, List testIds, String platform, String browser);

    void addLabelToTestRun(Long testRunId, String labelKey, String labelValue);

    void addLabelToTest(Long testRunId, Long testId, String labelKey, String labelValue);

    void addLabelsToTest(Long testRunId, Long testId, List<Label> labels);

    void addArtRefToTestRun(Long testRunId, String name, String value);

    void addArtReferencesToTest(Long testRunId, Long testId, List<ArtifactReference> artifactReferences);

    void linkIssueToTest(Long testId, IssueReference.Type type, String issueKey);

    Long createMilestone(Long projectId, String milestoneName);

    Milestone createMilestone(Long projectId, Milestone milestone);

    void deleteMilestone(Long projectId, Long milestoneId);

    void addLogsToTest(Long testRunId, List<LogItem> logItems);

    List<LogAndScreenshotItem> getLogsAndScreenshots(long testRunId, long testId);

    Response triggerWebhook(String link, String secretKey, String timestampHeader, boolean isSecretKeyExist);

    Response getResultsAfterTriggeringWebhookFromResultLink(String linkResult, boolean isSecretKeyExist, String secretKey, String timestampHeader);

    ConfigurationGroup createConfigurationGroup(Long projectId, ConfigurationGroup group);

    ConfigurationOption createConfigurationOption(Long projectId, Long groupId, ConfigurationOption option);

    void deleteConfigurationGroup(Long projectId, ConfigurationGroup configurationGroup);
}
