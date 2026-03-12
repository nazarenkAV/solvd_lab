package com.zebrunner.automation.api.integration.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectsMapping {

    private Boolean enabledForAllProjects;
    private List<Long> enabledForProjectIds;

    private Boolean enabledForAllJiraProjects;
    private List<Long> enabledForJiraProjectIds;

    private Boolean enabledForAllZebrunnerProjects;
    private List<Long> enabledForZebrunnerProjectIds;

    public static ProjectsMapping forZebrunnerProjectIds(List<Long> jiraProjectIds) {

        ProjectsMapping projectsMapping = new ProjectsMapping();
        projectsMapping.setEnabledForAllJiraProjects(true);
        projectsMapping.setEnabledForZebrunnerProjectIds(jiraProjectIds);

        return projectsMapping;
    }

    public static ProjectsMapping forProjectIds(List<Long> jiraProjectIds) {

        ProjectsMapping projectsMapping = new ProjectsMapping();
        projectsMapping.setEnabledForProjectIds(jiraProjectIds);

        return projectsMapping;
    }
}