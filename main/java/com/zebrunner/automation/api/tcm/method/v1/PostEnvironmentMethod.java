package com.zebrunner.automation.api.tcm.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.tcm.domain.Environment;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/environments?projectId=${projectId}"
)
public class PostEnvironmentMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostEnvironmentMethod(Long projectId, Environment environment) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(environment);
        super.setBodyContent(rawRequestBody);
    }

}
