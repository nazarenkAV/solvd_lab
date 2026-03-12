package com.zebrunner.automation.api.tcm.method.v1;

import org.apache.http.HttpHeaders;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.GET,
        url = "${host}/api/tcm/v1/test-case-settings/fields-layout?projectId=${projectId}"
)
public class GetTestCaseFieldsLayoutMethod extends AbstractApiMethodV2 {

    public GetTestCaseFieldsLayoutMethod(Long projectId) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
