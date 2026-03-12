package com.zebrunner.automation.api.project.method.v1;

import org.apache.http.HttpHeaders;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${base_api_url}/api/projects/v1/projects/${id}:purge"
)
public class PurgeProjectByIdV1Method extends AbstractApiMethodV2 {

    public PurgeProjectByIdV1Method(Long id) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("id", String.valueOf(id));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
