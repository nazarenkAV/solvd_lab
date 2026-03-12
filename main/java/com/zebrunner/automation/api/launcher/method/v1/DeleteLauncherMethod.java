package com.zebrunner.automation.api.launcher.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${api_url}/v1/git-repositories/id:${repoId}/launchers/id:${launcherId}?projectId=${projectId}"
)
public class DeleteLauncherMethod extends AbstractApiMethodV2 {

    public DeleteLauncherMethod(Long projectId, Long repoId, Long launcherId) {
        replaceUrlPlaceholder("api_url", APIContextManager.LAUNCHER_API_URL);
        replaceUrlPlaceholder("repoId", String.valueOf(repoId));
        replaceUrlPlaceholder("launcherId", String.valueOf(launcherId));
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}