package com.zebrunner.automation.api.integration.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
public class IntegrationResource {

    private Long id;
    private boolean enabled;
    private Tool tool;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "tool",
            visible = true
    )
    public ToolConfig config;

    private ProjectsMapping projectsMapping;
}

