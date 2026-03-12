package com.zebrunner.automation.api.reporting.method.v1;

import lombok.SneakyThrows;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/tests"
)
@RequestTemplatePath(path = "api/test_run/v1/_post/for_tests_rq.json")
public class PostStartTestsInTestRunV1Method extends AbstractApiMethodV2 {

    public PostStartTestsInTestRunV1Method(Long testRunId) {
        setProperties("api/test_runV1_test.properties");
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("uuid", UUID.randomUUID());
        addProperty("startedAt", OffsetDateTime.now());
    }

    @SneakyThrows
    public PostStartTestsInTestRunV1Method(TestExecution testExecution, Long testRunId) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(testExecution);
        super.setRequestTemplate(null);
        super.setBodyContent(rawRequestBody);
    }

}
