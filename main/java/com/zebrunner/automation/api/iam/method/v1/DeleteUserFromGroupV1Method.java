package com.zebrunner.automation.api.iam.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${base_api_url}/api/iam/v1/users/${userId}/groups/${groupId}"
)
public class DeleteUserFromGroupV1Method extends AbstractApiMethodV2 {

    public DeleteUserFromGroupV1Method(int groupId, int userId) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("groupId", String.valueOf(groupId));
        replaceUrlPlaceholder("userId", String.valueOf(userId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
