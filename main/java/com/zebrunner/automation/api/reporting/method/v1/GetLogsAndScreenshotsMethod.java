package com.zebrunner.automation.api.reporting.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.GET,
        url = "${base_api_url}/api/test-execution-logs/v1/test-runs/${testRunId}/tests/${testId}/logs-and-screenshots?maxPageSize=10000"
)
public class GetLogsAndScreenshotsMethod extends AbstractApiMethodV2 {

    public GetLogsAndScreenshotsMethod(long testRunId, long testId) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));
        replaceUrlPlaceholder("testId", String.valueOf(testId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
