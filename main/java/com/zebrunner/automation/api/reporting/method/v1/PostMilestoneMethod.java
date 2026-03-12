package com.zebrunner.automation.api.reporting.method.v1;

import lombok.SneakyThrows;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.zebrunner.automation.api.reporting.domain.Milestone;
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
        url = "${api_url}/v1/milestones?projectId=${projectId}"
)
@RequestTemplatePath(path = "api/milestone/_post/rq.json")
public class PostMilestoneMethod extends AbstractApiMethodV2 {

    public PostMilestoneMethod(Long projectId, String milestoneName) {
        replaceUrlPlaceholder("api_url", APIContextManager.TENANT_URL + "/api/reporting");
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        addProperty("name", milestoneName);
        addProperty("projectId", projectId);
        addProperty("completed", false);
        addProperty("dueDate", OffsetDateTime.now().plusMonths(1)
                                             .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        addProperty("startDate", OffsetDateTime.now()
                                               .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    }

    @SneakyThrows
    public PostMilestoneMethod(Long projectId, Milestone milestone) {
        replaceUrlPlaceholder("api_url", APIContextManager.TENANT_URL + "/api/reporting");
        replaceUrlPlaceholder("projectId", String.valueOf(projectId));

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationContext.getTenantAdminAuthToken());

        String rawRequestBody = ObjectMapperHolder.COMMON_MAPPER.writeValueAsString(milestone);
        super.setRequestTemplate(null);
        super.setBodyContent(rawRequestBody);
    }

}
