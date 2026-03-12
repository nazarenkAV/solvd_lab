package com.zebrunner.automation.api.reporting.method.v1;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.RequestTemplatePath;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/test-sessions"
)
@RequestTemplatePath(path = "api/sessions/rq_for_start_with_Caps.json")
public class PostSessionV1Method extends AbstractApiMethodV2 {

    public PostSessionV1Method(Long testRunId, List testIds) {
        setProperties("api/test_session.properties");
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("startedAt", OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(3)
                                               .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        addProperty("testId", String.valueOf(testIds));
        addProperty("sessionId", UUID.randomUUID());
        addProperty("status", "RUNNING");
    }

}
