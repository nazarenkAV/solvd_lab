package com.zebrunner.automation.api.tcm.method.v1;

import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/test-suites?projectId=${projectId}"
)
public class PostCreateTestSuiteMethod extends AbstractApiMethodV2 {

    public PostCreateTestSuiteMethod(Long projectId, TestSuite testSuite) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        super.setRequestBody(testSuite);
    }

}
