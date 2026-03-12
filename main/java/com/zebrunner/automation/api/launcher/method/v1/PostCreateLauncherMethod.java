package com.zebrunner.automation.api.launcher.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${api_url}/v1/git-repositories/id:${repoId}/launchers?projectId=${projectId}"
)
public class PostCreateLauncherMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostCreateLauncherMethod(Long projectId, Long repoId, String launcherName, Launcher launcher) {
        replaceUrlPlaceholder("api_url", APIContextManager.LAUNCHER_API_URL);
        replaceUrlPlaceholder("repoId", String.valueOf(repoId));
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));
        addProperty("name", launcherName);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        setBodyContent(ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(launcher));
    }

}
