package com.zebrunner.automation.api.reporting.method.v1;

import java.util.List;

import com.zebrunner.automation.api.common.ItemsPayload;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/tests/${testId}/labels"
)
@RequestTemplatePath(path = "api/test_run/v1/_post/rq_label.json")
public class PutTestLabelsMethod extends AbstractApiMethodV2 {

    public PutTestLabelsMethod(Long testRunId, Long testId) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));
        replaceUrlPlaceholder("testId", String.valueOf(testId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

    public PutTestLabelsMethod(Long testRunId, Long testId, List<Label> labels) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));
        replaceUrlPlaceholder("testId", String.valueOf(testId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        ItemsPayload<Label> payload = new ItemsPayload<>(labels);
        super.setRequestTemplate(null);
        super.setRequestBody(payload);
    }

}

