package com.zebrunner.automation.api.launcher.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${api_url}/v1/git-repositories/id:${repoId}/?projectId=${projectId}"
)
public class DeleteGithubRepoByIdMethod extends AbstractApiMethodV2 {

    public DeleteGithubRepoByIdMethod(Long projectId, Long repoId) {
        replaceUrlPlaceholder("api_url", APIContextManager.LAUNCHER_API_URL);
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));
        replaceUrlPlaceholder("repoId", String.valueOf(repoId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        request.urlEncodingEnabled(false);
    }

}
