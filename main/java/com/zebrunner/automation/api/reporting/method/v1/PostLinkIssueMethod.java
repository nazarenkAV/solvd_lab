package com.zebrunner.automation.api.reporting.method.v1;

import com.zebrunner.automation.api.reporting.domain.request.v1.LinkIssueReferencesRequest;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

import org.apache.http.HttpHeaders;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${api_url}/v1/issue-references"
)
public class PostLinkIssueMethod extends AbstractApiMethodV2 {

    public PostLinkIssueMethod(LinkIssueReferencesRequest linkIssueReferencesRequest) {
        replaceUrlPlaceholder("api_url", APIContextManager.REPORTING_API_URL);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        this.setRequestBody(linkIssueReferencesRequest);
    }

}
