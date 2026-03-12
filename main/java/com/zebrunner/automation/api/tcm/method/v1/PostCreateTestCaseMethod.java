package com.zebrunner.automation.api.tcm.method.v1;

import org.apache.http.HttpHeaders;

import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/test-cases?projectId=${projectId}"
)
public class PostCreateTestCaseMethod extends AbstractApiMethodV2 {

    public PostCreateTestCaseMethod(Long projectId, Long testSuiteId, TestCase testCase) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        testCase.setTestSuiteId(testSuiteId);
        super.setRequestBody(testCase);
    }

}
