package com.zebrunner.automation.api.launcher.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.launcher.domain.Preset;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${api_url}/v1/git-repositories/${gitRepositoryId}/launchers/${launcherId}/presets?projectId=${projectId}"
)
public class PostPresetMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostPresetMethod(Long projectId, Long gitRepositoryId, Long launcherId, Preset preset) {
        replaceUrlPlaceholder("api_url", APIContextManager.LAUNCHER_API_URL);
        replaceUrlPlaceholder("gitRepositoryId", String.valueOf(gitRepositoryId));
        replaceUrlPlaceholder("launcherId", String.valueOf(launcherId));
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String rawRequestBody = objectMapper.writeValueAsString(preset);
        super.setBodyContent(rawRequestBody);
    }
}
