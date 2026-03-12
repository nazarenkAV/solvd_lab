package com.zebrunner.automation.api.reporting.method.v1;

import java.util.List;

import com.zebrunner.automation.api.common.ItemsPayload;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/tests/${testId}/artifact-references"
)
public class PutTestArtifactReferencesMethod extends AbstractApiMethodV2 {

    public PutTestArtifactReferencesMethod(Long testRunId, Long testId, List<ArtifactReference> artifactReferences) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));
        replaceUrlPlaceholder("testId", String.valueOf(testId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        ItemsPayload<ArtifactReference> payload = new ItemsPayload<>(artifactReferences);
        super.setRequestBody(payload);
    }

}

