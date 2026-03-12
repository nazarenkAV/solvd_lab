package com.zebrunner.automation.api.reporting.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}"
)
@RequestTemplatePath(path = "api/test_run/v1/_put/rq_for_finish_test_run.json")
public class PutFinishTestRunV1Method extends AbstractApiMethodV2 {

    public PutFinishTestRunV1Method(Long testRunId, String date) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("endedAt", date);
    }

}
