package com.zebrunner.automation.api.reporting.method.v1;

import lombok.SneakyThrows;

import java.util.UUID;

import com.zebrunner.automation.api.reporting.domain.Launch;
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
        url = "${base_api_url}/api/reporting/v1/test-runs?projectKey=${project}"
)
@RequestTemplatePath(path = "api/test_run/v1/_post/rq_full.json")
public class PostStartTestRunV1Method extends AbstractApiMethodV2 {

    public PostStartTestRunV1Method(String project, String date) {
        setProperties("api/test_runV1.properties");
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("project", project);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("uuid", UUID.randomUUID());
        addProperty("startedAt", date);
    }

    @SneakyThrows
    public PostStartTestRunV1Method(Launch launch, String projectKey) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("project", projectKey);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(launch);
        super.setRequestTemplate(null);
        super.setBodyContent(rawRequestBody);
    }

}
