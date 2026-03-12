package com.zebrunner.automation.api.project.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.ResponseTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.GET,
        url = "${base_api_url}/api/projects/v1/projects?page=1&pageSize=100&sortOrder=ASC"
)
@ResponseTemplatePath(path = "api/projectsV1/api/projects/_get/rs.json")
public class GetAllProjectsMethod extends AbstractApiMethodV2 {

    public GetAllProjectsMethod() {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
