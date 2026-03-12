package com.zebrunner.automation.api.integration.method.v2;

import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${api_url}/v2/integrations/tool:${tool}/${id}"
)
public class DeleteIntegrationV2Method extends AbstractApiMethodV2 {

    public DeleteIntegrationV2Method(Tool tool, Long id) {
        replaceUrlPlaceholder("api_url", APIContextManager.INTEGRATION_API_URL);
        replaceUrlPlaceholder("tool", tool.getApiPathVariable());
        replaceUrlPlaceholder("id", String.valueOf(id));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        this.request.urlEncodingEnabled(false);
    }

}
