package com.zebrunner.automation.api.reporting.service;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.zebrunner.automation.api.reporting.domain.IssueReference;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.reporting.domain.LogAndScreenshotItem;
import com.zebrunner.automation.api.reporting.domain.LogItem;
import com.zebrunner.automation.api.reporting.domain.Milestone;
import com.zebrunner.automation.api.reporting.domain.request.v1.LinkIssueReferencesRequest;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.api.reporting.method.v1.DeleteMilestoneByIdAndProjectIdMethod;
import com.zebrunner.automation.api.reporting.method.v1.GetLogsAndScreenshotsMethod;
import com.zebrunner.automation.api.reporting.method.v1.PostLinkIssueMethod;
import com.zebrunner.automation.api.reporting.method.v1.PostLogsMethod;
import com.zebrunner.automation.api.reporting.method.v1.PostMilestoneMethod;
import com.zebrunner.automation.api.reporting.method.v1.PostSessionV1Method;
import com.zebrunner.automation.api.reporting.method.v1.PutTestArtifactReferencesMethod;
import com.zebrunner.automation.api.reporting.method.v1.PutTestLabelsMethod;
import com.zebrunner.automation.api.reporting.method.v1.PutTestRunArtifactReferencesMethod;
import com.zebrunner.automation.api.reporting.method.v1.PutTestRunLabelsMethod;
import com.zebrunner.automation.api.tcm.domain.ConfigurationGroup;
import com.zebrunner.automation.api.tcm.domain.ConfigurationOption;
import com.zebrunner.automation.api.tcm.method.v1.DeleteConfigurationGroupMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostConfigurationGroupMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostConfigurationOptionMethod;

import static com.zebrunner.automation.util.WebhooksUtil.calculateSignature;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

@Slf4j
@Deprecated
public class ApiHelperServiceImpl implements ApiHelperService {
    private final TestRunServiceAPIImplV1 testRunServiceAPIImplV1 = new TestRunServiceAPIImplV1();
    private final TestServiceV1Impl testServiceV1 = new TestServiceV1Impl();

    @Override
    public Long startTR(String projectKey) {
        long id = testRunServiceAPIImplV1.startTestRunWithName(projectKey.toUpperCase(), projectKey.toUpperCase());
        long testId = testServiceV1.startTest(id);
        testServiceV1.finishTestAsResult(id, testId, "FAILED");
        testRunServiceAPIImplV1.finishTestRun(id);
        return id;
    }

    @Override
    public Long startTRWithCertainConfig(String projectKey, String env) {
        String config = "{\"environment\": \"" + env + "\"}";
        long id = testRunServiceAPIImplV1.startTestRunWithCertainConfig(projectKey.toUpperCase(), config);
        long testId = testServiceV1.startTest(id);
        testServiceV1.finishTestAsResult(id, testId, "FAILED");
        testRunServiceAPIImplV1.finishTestRun(id);
        return id;
    }

    @Override
    public void startSession(Long testRunId, List testIds, String platform, String browser) {
        PostSessionV1Method postSessionV1Method = new PostSessionV1Method(testRunId, testIds);
        postSessionV1Method.addProperty("platformNameDesired", platform);
        postSessionV1Method.addProperty("browserNameDes", browser);
        postSessionV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        postSessionV1Method.callAPI();
    }

    @Override
    public void addLabelToTestRun(Long testRunId, String labelKey, String labelValue) {
        PutTestRunLabelsMethod putTestRunLabelsMethod = new PutTestRunLabelsMethod(testRunId);
        putTestRunLabelsMethod.addProperty("labelKey", labelKey);
        putTestRunLabelsMethod.addProperty("labelValue", labelValue);
        putTestRunLabelsMethod.callAPI();
    }

    @Override
    public void addLabelToTest(Long testRunId, Long testId, String labelKey, String labelValue) {
        PutTestLabelsMethod putTestLabelsMethod = new PutTestLabelsMethod(testRunId, testId);
        putTestLabelsMethod.addProperty("labelKey", labelKey);
        putTestLabelsMethod.addProperty("labelValue", labelValue);
        putTestLabelsMethod.callAPI();
    }

    @Override
    public void addLabelsToTest(Long testRunId, Long testId, List<Label> labels) {
        PutTestLabelsMethod putTestLabelsMethod = new PutTestLabelsMethod(testRunId, testId, labels);
        putTestLabelsMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        putTestLabelsMethod.callAPI();
    }

    @Override
    public void addArtRefToTestRun(Long testRunId, String name, String value) {
        PutTestRunArtifactReferencesMethod putTestRunArtifactReferencesMethod = new PutTestRunArtifactReferencesMethod(testRunId);
        putTestRunArtifactReferencesMethod.addProperty("name", name);
        putTestRunArtifactReferencesMethod.addProperty("value", value);
        putTestRunArtifactReferencesMethod.callAPI();
    }

    @Override
    public void addArtReferencesToTest(Long testRunId, Long testId, List<ArtifactReference> artifactReferences) {
        PutTestArtifactReferencesMethod addTestArtifactReferences
                = new PutTestArtifactReferencesMethod(testRunId, testId, artifactReferences);
        addTestArtifactReferences.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        addTestArtifactReferences.callAPI();
    }

    @Override
    public void linkIssueToTest(Long testId, IssueReference.Type type, String issueKey) {
        LinkIssueReferencesRequest linkIssueRequest = new LinkIssueReferencesRequest(
                List.of(
                        new LinkIssueReferencesRequest.Item(testId, type, issueKey))
        );
        PostLinkIssueMethod postTestIssueLinkMethod = new PostLinkIssueMethod(linkIssueRequest);
        postTestIssueLinkMethod.callAPI();

    }

    @Override
    public Long createMilestone(Long projectId, String milestoneName) {
        PostMilestoneMethod postMilestoneMethod =
                new PostMilestoneMethod(projectId, milestoneName);
        postMilestoneMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = postMilestoneMethod.callAPI().asString();
        return JsonPath.from(rs).getLong("data.id");
    }

    @Override
    public Milestone createMilestone(Long projectId, Milestone milestone) {
        PostMilestoneMethod postMilestoneMethod =
                new PostMilestoneMethod(projectId, milestone);
        postMilestoneMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = postMilestoneMethod.callAPI().asString();
        return JsonPath.from(rs).getObject("data", Milestone.class);
    }

    @Override
    public void deleteMilestone(Long projectId, Long milestoneId) {
        DeleteMilestoneByIdAndProjectIdMethod deleteMilestoneByIdAndProjectId =
                new DeleteMilestoneByIdAndProjectIdMethod(projectId, milestoneId);
        deleteMilestoneByIdAndProjectId.callAPI();
    }

    @Override
    public void addLogsToTest(Long testRunId, List<LogItem> logItems) {
        PostLogsMethod postLogsMethod =
                new PostLogsMethod(testRunId, logItems);
        postLogsMethod.callAPI();
    }

    @Override
    public List<LogAndScreenshotItem> getLogsAndScreenshots(long testRunId, long testId) {
        GetLogsAndScreenshotsMethod getLogsAndScreenshotsMethod =
                new GetLogsAndScreenshotsMethod(testRunId, testId);
        String rs = getLogsAndScreenshotsMethod.callAPI().asString();
        return JsonPath.from(rs).getList("items", LogAndScreenshotItem.class);
    }

    @Override
    public Response triggerWebhook(String link, String secretKey, String timestampHeader, boolean isSecretKeyExist) {
        RequestSpecification request = RestAssured.given()
                .urlEncodingEnabled(false)
                .log()
                .all();

        if (isSecretKeyExist) {
            String signatureHeader = calculateSignature(link, secretKey, timestampHeader);

            request.header("x-zbr-timestamp", timestampHeader);
            request.header("x-zbr-signature", signatureHeader);
        }

        request.contentType("application/json");

        return request.post(link);
    }

    @Override
    public Response getResultsAfterTriggeringWebhookFromResultLink(String linkResult, boolean isSecretKeyExist,
                                                                   String secretKey, String timestampHeader) {
        RequestSpecification request = RestAssured.given()
                .urlEncodingEnabled(false)
                .log()
                .all();

        if (isSecretKeyExist) {
            String signatureHeader = calculateSignature(linkResult, secretKey, timestampHeader);

            request.header("x-zbr-timestamp", timestampHeader);
            request.header("x-zbr-signature", signatureHeader);
        }

        request.contentType("application/json");

        return request.get(linkResult);
    }

    @Override
    public ConfigurationGroup createConfigurationGroup(Long projectId, ConfigurationGroup group) {
        PostConfigurationGroupMethod postConfigurationGroupMethod
                = new PostConfigurationGroupMethod(projectId, group);

        postConfigurationGroupMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = postConfigurationGroupMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", ConfigurationGroup.class);
    }

    @Override
    public ConfigurationOption createConfigurationOption(Long projectId, Long groupId, ConfigurationOption option) {
        PostConfigurationOptionMethod postConfigurationOptionMethod
                = new PostConfigurationOptionMethod(projectId, groupId, option);

        postConfigurationOptionMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = postConfigurationOptionMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", ConfigurationOption.class);
    }

    @Override
    public void deleteConfigurationGroup(Long projectId, ConfigurationGroup configurationGroup) {
        DeleteConfigurationGroupMethod deleteConfigurationGroupMethod
                = new DeleteConfigurationGroupMethod(projectId, configurationGroup);

        deleteConfigurationGroupMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        deleteConfigurationGroupMethod.callAPI();
    }
}
