package com.zebrunner.automation.api.integration.domain.request.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.automation.api.integration.domain.ProjectsMapping;
import com.zebrunner.automation.api.integration.domain.ToolConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveIntegrationRequest {

    private boolean enabled;
    private ToolConfig config;
    private ProjectsMapping projectsMapping;

    public static SaveIntegrationRequest enabledWith(ToolConfig config, ProjectsMapping projectsMapping) {
        return SaveIntegrationRequest.builder()
                .enabled(true)
                .config(config)
                .projectsMapping(projectsMapping)
                .build();
    }
}
