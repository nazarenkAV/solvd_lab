package com.zebrunner.automation.api.tcm.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.tcm.domain.request.v1.UpdateTestRunCasesRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PATCH,
        url = "${host}/api/tcm/v1/test-runs/${testRunId}/test-cases:batch?projectId=${projectId}"
)
public class PatchAssignTestCaseMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PatchAssignTestCaseMethod(Long projectId, Long testRunId, UpdateTestRunCasesRequest request) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("testRunId", testRunId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(request);
        super.setBodyContent(rawRequestBody);
    }

}
