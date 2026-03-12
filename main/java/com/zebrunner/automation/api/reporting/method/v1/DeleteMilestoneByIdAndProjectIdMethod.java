package com.zebrunner.automation.api.reporting.method.v1;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.DELETE,
        url = "${api_url}/v1/milestones/${id}?projectId=${projectId}"
)
public class DeleteMilestoneByIdAndProjectIdMethod extends AbstractApiMethodV2 {

    public DeleteMilestoneByIdAndProjectIdMethod(Long projectId, Long milestoneId) {
        replaceUrlPlaceholder("api_url", APIContextManager.TENANT_URL + "/api/reporting");
        replaceUrlPlaceholder("id", String.valueOf(milestoneId));
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());
    }

}
