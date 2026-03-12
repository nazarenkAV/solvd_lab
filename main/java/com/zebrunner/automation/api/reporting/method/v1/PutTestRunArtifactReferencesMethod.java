package com.zebrunner.automation.api.reporting.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/artifact-references",
        methodType = HttpMethodType.PUT
)
@RequestTemplatePath(path = "api/test_run/v1/_post/rq_art_ref.json")
public class PutTestRunArtifactReferencesMethod extends AbstractApiMethodV2 {

    public PutTestRunArtifactReferencesMethod(Long testRunId) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}

