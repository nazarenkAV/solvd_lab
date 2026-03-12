package com.zebrunner.automation.api.tcm.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.tcm.domain.ConfigurationOption;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/configuration-groups/${group-id}/options?projectId=${projectId}"
)
public class PostConfigurationOptionMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostConfigurationOptionMethod(Long projectId, Long groupId, ConfigurationOption optionRequest) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("group-id", groupId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(optionRequest);
        super.setBodyContent(rawRequestBody);
    }

}
