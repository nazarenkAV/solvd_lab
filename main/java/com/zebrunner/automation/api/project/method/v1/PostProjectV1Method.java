package com.zebrunner.automation.api.project.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@RequestTemplatePath(path = "api/projectsV1/_post/rq.json")
@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base_api_url}/api/projects/v1/projects"
)
public class PostProjectV1Method extends AbstractApiMethodV2 {

    public PostProjectV1Method(String name, String key) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        setProperties("api/projectV1.properties");
        addProperty("name", name);
        addProperty("key", key);
    }

    public PostProjectV1Method(String name, String key, boolean isPublic) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        setProperties("api/projectV1.properties");
        addProperty("name", name);
        addProperty("key", key);
        addProperty("publiclyAccessible", isPublic);
    }

}
