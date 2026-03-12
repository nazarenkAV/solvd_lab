package com.zebrunner.automation.api.reporting.method.v1;

import lombok.SneakyThrows;

import java.time.OffsetDateTime;

import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.PUT,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/tests/${testId}"
)
@RequestTemplatePath(path = "api/test_run/v1/_put/rq_for_finish_test.json")
public class PutFinishTestInTestRunV1Method extends AbstractApiMethodV2 {

    public PutFinishTestInTestRunV1Method(Long testRunId, Long testId, String result) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));
        replaceUrlPlaceholder("testId", String.valueOf(testId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("endedAt", OffsetDateTime.now().plusSeconds(3));
        addProperty("result", result);
    }

    @SneakyThrows
    public PutFinishTestInTestRunV1Method(Long testRunId, Long testId, FinishTestRequest finishTestRequest) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));
        replaceUrlPlaceholder("testId", String.valueOf(testId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(finishTestRequest);

        super.setRequestTemplate(null);
        super.setBodyContent(rawRequestBody);
    }

}
