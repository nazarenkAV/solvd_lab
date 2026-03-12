package com.zebrunner.automation.api.project.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.GET,
        url = "${projects_url}/v1/projects/${projectId}/members"
)
public class GetProjectAssignmentsMethod extends AbstractApiMethodV2 {

    public GetProjectAssignmentsMethod(int projectId) {
        replaceUrlPlaceholder("projects_url", APIContextManager.PROJECTS_API_URL);
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
