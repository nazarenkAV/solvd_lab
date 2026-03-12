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
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/platform"
)
@RequestTemplatePath(path = "api/test_run/v1/_put/rq_for_set_platform.json")
public class PutTestRunPlatformV1Method extends AbstractApiMethodV2 {

    public PutTestRunPlatformV1Method(Long testRunId, String platform, String version) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("name", platform);
        addProperty("version", version);
    }

}
