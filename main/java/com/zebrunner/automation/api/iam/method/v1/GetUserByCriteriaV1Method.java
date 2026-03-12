package com.zebrunner.automation.api.iam.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.GET,
        url = "${base_api_url}/api/iam/v1/users?public=false&status=${status}&query=${query}"
)
public class GetUserByCriteriaV1Method extends AbstractApiMethodV2 {

    public GetUserByCriteriaV1Method(String query, String status) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("query", query);
        replaceUrlPlaceholder("status", status);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
