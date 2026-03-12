package com.zebrunner.automation.api.tcm.method.v1;

import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PATCH,
        url = "${host}/api/tcm/v1/test-cases/${id}?projectId=${projectId}"
)
public class PatchUpdateTestCaseMethod extends AbstractApiMethodV2 {

    public PatchUpdateTestCaseMethod(Long projectId, Long testCaseId, TestCase testCase) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("id", testCaseId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        setRequestTemplate(null);
        this.setRequestBody(testCase, ObjectMapperHolder.COMMON_MAPPER);
    }

}
