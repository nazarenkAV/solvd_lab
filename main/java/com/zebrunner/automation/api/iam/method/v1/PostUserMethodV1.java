package com.zebrunner.automation.api.iam.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.HideRequestBodyPartsInLogs;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base_api_url}/api/iam/v1/users"
)
@HideRequestBodyPartsInLogs(paths = "password")
public class PostUserMethodV1 extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostUserMethodV1(User user) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(user);
        super.setBodyContent(rawRequestBody);
    }

}
