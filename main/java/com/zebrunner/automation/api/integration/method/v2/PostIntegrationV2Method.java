package com.zebrunner.automation.api.integration.method.v2;

import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.integration.domain.request.v2.SaveIntegrationRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${api_url}/v2/integrations/tool:${tool}"
)
public class PostIntegrationV2Method extends AbstractApiMethodV2 {

    public PostIntegrationV2Method(Tool tool, SaveIntegrationRequest saveIntegrationRequest) {
        replaceUrlPlaceholder("api_url", APIContextManager.INTEGRATION_API_URL);
        replaceUrlPlaceholder("tool", tool.getApiPathVariable());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        this.setRequestBody(saveIntegrationRequest);
        this.request.urlEncodingEnabled(false);
    }

}
