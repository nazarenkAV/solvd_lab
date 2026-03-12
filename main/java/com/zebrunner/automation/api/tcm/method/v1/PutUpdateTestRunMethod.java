package com.zebrunner.automation.api.tcm.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${host}/api/tcm/v1/test-runs/${testRunId}?projectId=${projectId}"
)
public class PutUpdateTestRunMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PutUpdateTestRunMethod(Long projectId, TestRun testRun) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("testRunId", String.valueOf(testRun.getId()));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(testRun);
        super.setBodyContent(rawRequestBody);
    }

}
