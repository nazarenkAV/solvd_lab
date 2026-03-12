package com.zebrunner.automation.api.tcm.method.v1;

import org.apache.http.HttpHeaders;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${host}/api/tcm/v1/environments/${environmentId}?projectId=${projectId}"
)
public class DeleteEnvironmentMethod extends AbstractApiMethodV2 {

    public DeleteEnvironmentMethod(Long projectId, Long environmentId) {
        replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("environmentId", String.valueOf(environmentId));
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
