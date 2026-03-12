package com.zebrunner.automation.api.iam.method.v1;

import org.apache.http.HttpHeaders;

import com.zebrunner.automation.api.iam.domain.request.v1.UpdateUserRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.PATCH,
        url = "${base_api_url}/api/iam/v1/users/${id}"
)
public class PatchUserV1Method extends AbstractApiMethodV2 {

    public PatchUserV1Method(int id, UpdateUserRequest requestBody) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("id", String.valueOf(id));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        request.body(requestBody);
    }

}
