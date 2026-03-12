package com.zebrunner.automation.api.project.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${projects_url}/api/projects/v1/projects/${projectId}/members"
)
@RequestTemplatePath(path = "api/projectsV1/_put/rq.json")
public class PutProjectAssignmentMethod extends AbstractApiMethodV2 {

    public PutProjectAssignmentMethod(Long projectId, int userId, String role) {
        replaceUrlPlaceholder("projects_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("userId", String.valueOf(userId));
        addProperty("role", role);
    }

}
