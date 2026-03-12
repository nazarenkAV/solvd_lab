package com.zebrunner.automation.api.integration.client;

import lombok.extern.slf4j.Slf4j;

import com.zebrunner.automation.api.integration.domain.IntegrationPayload;
import com.zebrunner.automation.api.integration.domain.IntegrationResource;
import com.zebrunner.automation.api.integration.domain.ProjectsMapping;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.integration.domain.ToolConfig;
import com.zebrunner.automation.api.integration.domain.request.v2.SaveIntegrationRequest;
import com.zebrunner.automation.api.integration.method.v2.DeleteIntegrationV2Method;
import com.zebrunner.automation.api.integration.method.v2.PostIntegrationV2Method;
import com.zebrunner.automation.legacy.ValidConfigProvider;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;

@Slf4j
@Deprecated
public class IntegrationClient {

    public static IntegrationResource create(Tool tool, SaveIntegrationRequest saveIntegrationRequest) {
        log.info("Saving integration for tool " + tool.name());

        PostIntegrationV2Method saveIntegration = new PostIntegrationV2Method(tool, saveIntegrationRequest);
        saveIntegration.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = saveIntegration.callAPI().asString();

        return JsonPath.from(rs).getObject("", IntegrationPayload.class).getData();
    }

    public static IntegrationResource create(Tool tool, ToolConfig config, ProjectsMapping projectsMapping) {

        return create(tool, SaveIntegrationRequest.enabledWith(config, projectsMapping));
    }

    public static IntegrationResource create(Tool tool, Long projectId) {
        log.info("Saving integration for tool " + tool.name() + " and project with ID " + projectId);

        SaveIntegrationRequest saveIntegrationRequest =
                ValidConfigProvider.getSaveIntegrationRequest(projectId, tool);

        PostIntegrationV2Method saveIntegration = new PostIntegrationV2Method(tool, saveIntegrationRequest);
        saveIntegration.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = saveIntegration.callAPI().asString();

        return JsonPath.from(rs).getObject("", IntegrationPayload.class).getData();
    }

    public static IntegrationResource update(Tool tool, SaveIntegrationRequest saveIntegrationRequest) {
        //FIXME: should be implemented
        return null;
    }

    public static void delete(Tool tool, Long id) {
        log.info("Removing integration for tool " + tool.name());

        DeleteIntegrationV2Method delete = new DeleteIntegrationV2Method(tool, id);
        delete.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        delete.callAPI();
    }
}
