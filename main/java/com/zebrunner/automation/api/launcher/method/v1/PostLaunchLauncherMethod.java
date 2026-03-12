package com.zebrunner.automation.api.launcher.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.launcher.domain.LaunchArguments;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${api_url}/v1/launches?projectId=${projectId}"
)
public class PostLaunchLauncherMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostLaunchLauncherMethod(Long projectId, LaunchArguments launchArguments) {
        replaceUrlPlaceholder("api_url", APIContextManager.LAUNCHER_API_URL);
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(launchArguments);
        super.setBodyContent(rawRequestBody);
    }

}
