package com.zebrunner.automation.api.reporting.method.v1;

import java.util.List;

import com.zebrunner.automation.api.reporting.domain.LogItem;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base_api_url}/api/reporting/v1/test-runs/${testRunId}/logs"
)
public class PostLogsMethod extends AbstractApiMethodV2 {

    public PostLogsMethod(Long testRunId, List<LogItem> logItems) {
        replaceUrlPlaceholder("base_api_url", APIContextManager.TENANT_URL);
        replaceUrlPlaceholder("testRunId", String.valueOf(testRunId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        this.setRequestBody(logItems);
    }

}
