package com.zebrunner.automation.api.iam.method.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.HideRequestBodyPartsInLogs;
import com.zebrunner.carina.api.annotation.ResponseTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.legacy.ObjectMapperFactory;
import lombok.SneakyThrows;

@Endpoint(url = "${base_api_url}/api/iam/v1/auth/login", methodType = HttpMethodType.POST)
@HideRequestBodyPartsInLogs(paths = {"password", "username"})
public class PostGenerateAuthTokenMethodIAM extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostGenerateAuthTokenMethodIAM(User user) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);

        ObjectMapper objectMapper = ObjectMapperFactory.buildNew();
        String rawRequestBody = objectMapper.writeValueAsString(user);
        super.setBodyContent(rawRequestBody);
    }
}
