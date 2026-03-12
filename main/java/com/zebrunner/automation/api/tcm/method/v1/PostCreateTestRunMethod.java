package com.zebrunner.automation.api.tcm.method.v1;

import lombok.SneakyThrows;

import com.zebrunner.automation.api.tcm.domain.request.v1.CreateTestRunRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/test-runs?projectId=${projectId}"
)
public class PostCreateTestRunMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostCreateTestRunMethod(Long projectId, CreateTestRunRequest request) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(request);
        super.setBodyContent(rawRequestBody);
    }

}
