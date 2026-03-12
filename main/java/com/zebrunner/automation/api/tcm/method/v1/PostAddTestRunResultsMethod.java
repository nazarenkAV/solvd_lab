package com.zebrunner.automation.api.tcm.method.v1;

import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;

import com.zebrunner.automation.api.tcm.domain.TestRunResult;
import com.zebrunner.automation.api.tcm.domain.request.v1.AddTestRunCaseResultsRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${host}/api/tcm/v1/test-runs/${testRunId}/test-case-results:batch?projectId=${projectId}"
)
public class PostAddTestRunResultsMethod extends AbstractApiMethodV2 {

    @SneakyThrows
    public PostAddTestRunResultsMethod(Long projectId, Long testRunId, List<Long> testCaseIds, TestRunResult result) {
        super.replaceUrlPlaceholder("host", APIContextManager.TENANT_URL);
        super.replaceUrlPlaceholder("projectId", projectId.toString());
        super.replaceUrlPlaceholder("testRunId", testRunId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        AddTestRunCaseResultsRequest.Item item = ObjectMapperHolder.COMMON_MAPPER.convertValue(result, AddTestRunCaseResultsRequest.Item.class);
        item.setTestCaseIds(testCaseIds);
        if (item.getAttachments() == null) {
            item.setAttachments(Collections.emptyList());
        }

        AddTestRunCaseResultsRequest request = new AddTestRunCaseResultsRequest(List.of(item));
        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(request);
        super.setBodyContent(rawRequestBody);
    }

}
