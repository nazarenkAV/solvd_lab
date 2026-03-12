package com.zebrunner.automation.api.project.service;

import com.zebrunner.automation.api.project.method.v1.ProjectAssignment;
import com.zebrunner.automation.api.project.domain.Project;

import java.util.List;

@Deprecated
public interface ProjectV1Service {

    List<String> getAllProjectKeys();

    void deleteProjectByKey(String projectKey);

    void deleteProjectById(Long projectId);

    List<ProjectAssignment> getProjectAssignments(int projectId);

    ProjectAssignment getProjectAssignmentForUser(int projectId, String username);

    void assignUserToProject(Long projectId, int userId, String role);

    String createProject(String projectName, String projectKey);

    Project createProject();

    Long getProjectIdByKey(String projectKey);

    void deleteAllProjects();

    Project createPrivateProject();
}
