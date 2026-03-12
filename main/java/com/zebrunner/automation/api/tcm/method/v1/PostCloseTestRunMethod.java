package com.zebrunner.automation.api.tcm.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/test-runs/${testRunId}:close?projectId=${projectId}"
)
public class PostCloseTestRunMethod extends AbstractApiMethodV2 {

    public PostCloseTestRunMethod(Long projectId, Long testRunId) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("testRunId", testRunId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
