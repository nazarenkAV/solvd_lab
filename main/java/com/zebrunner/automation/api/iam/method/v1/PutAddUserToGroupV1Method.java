package com.zebrunner.automation.api.iam.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${base_api_url}/api/iam/v1/users/${id}/groups/${groupId}"
)
@RequestTemplatePath(path = "api/user/v1/_put/rq_add_to_group.json")
public class PutAddUserToGroupV1Method extends AbstractApiMethodV2 {

    public PutAddUserToGroupV1Method(int groupId, int userId) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("groupId", String.valueOf(groupId));
        replaceUrlPlaceholder("id", String.valueOf(userId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("id", String.valueOf(userId));
    }

}
