package com.zebrunner.automation.api.tcm.method.v1;

import org.apache.http.HttpHeaders;

import com.zebrunner.automation.api.tcm.domain.ConfigurationGroup;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${host}/api/tcm/v1/configuration-groups/${groupId}?projectId=${projectId}"
)
public class DeleteConfigurationGroupMethod extends AbstractApiMethodV2 {

    public DeleteConfigurationGroupMethod(Long projectId, ConfigurationGroup configurationGroup) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("groupId", String.valueOf(configurationGroup.getId()));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
