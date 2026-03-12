package com.zebrunner.automation.api.project.service;

import java.lang.invoke.MethodHandles;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.project.method.v1.GetAllProjectsMethod;
import com.zebrunner.automation.api.project.method.v1.GetProjectAssignmentsMethod;
import com.zebrunner.automation.api.project.method.v1.GetProjectByKeyMethod;
import com.zebrunner.automation.api.project.method.v1.PostProjectV1Method;
import com.zebrunner.automation.api.project.method.v1.ProjectAssignment;
import com.zebrunner.automation.api.project.method.v1.PurgeProjectByIdV1Method;
import com.zebrunner.automation.api.project.method.v1.PutProjectAssignmentMethod;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.carina.api.APIMethodPoller;

import static com.zebrunner.carina.utils.common.CommonUtils.pause;

import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ProjectV1ServiceImpl implements ProjectV1Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public List<String> getAllProjectKeys() {
        GetAllProjectsMethod getAllProjectsMethod = new GetAllProjectsMethod();
        getAllProjectsMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getAllProjectsMethod.callAPI().asString();
        List<String> projectList = JsonPath.from(rs).getList("items.key");
        LOGGER.info("Existing projects: " + projectList);
        return projectList;
    }

    @Override
    public void deleteProjectByKey(String projectKey) {
        Long projectId = getProjectIdByKey(projectKey);
        if (!projectKey.equalsIgnoreCase("DEF")) {
            PurgeProjectByIdV1Method purgeProjectByIdV1Method = new PurgeProjectByIdV1Method(projectId);
            purgeProjectByIdV1Method.callAPI();
            LOGGER.info("Project with key {} was deleted!", projectKey);
            pause(2);
        }
    }

    @Override
    public void deleteProjectById(Long projectId) {
        if (projectId != (1)) { // not to delete the DEF project with id=1
            PurgeProjectByIdV1Method purgeProjectByIdV1Method = new PurgeProjectByIdV1Method(projectId);
            purgeProjectByIdV1Method.callAPI();
        }
    }

    @Override
    public List<ProjectAssignment> getProjectAssignments(int projectId) {
        GetProjectAssignmentsMethod getProjectAssignmentsMethod = new GetProjectAssignmentsMethod(projectId);
        getProjectAssignmentsMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getProjectAssignmentsMethod.callAPI().asString();
        return JsonPath.from(rs).getList("items", ProjectAssignment.class);
    }

    @Override
    public ProjectAssignment getProjectAssignmentForUser(int projectId, String username) {
        List<ProjectAssignment> projectAssignments = getProjectAssignments(projectId);
        for (ProjectAssignment assignment : projectAssignments) {
            if (assignment.getUser().getUsername().equals(username)) {
                return assignment;
            }
        }
        return null;
    }

    @Override
    public void assignUserToProject(Long projectId, int userId, String role) {
        PutProjectAssignmentMethod putProjectAssignmentMethod = new PutProjectAssignmentMethod(projectId, userId, role);
        putProjectAssignmentMethod.callAPIWithRetry()
                                  .withLogStrategy(APIMethodPoller.LogStrategy.ALL)
                                  .stopAfter(7, ChronoUnit.SECONDS)
                                  .pollEvery(500, ChronoUnit.MILLIS)
                                  .until(rs -> rs.getStatusCode() == 201)
                                  .execute();// we should wait a bit until the user information appears in the project service
        putProjectAssignmentMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        putProjectAssignmentMethod.callAPI();
    }

    @Override
    public String createProject(String projectName, String projectKey) {
        PostProjectV1Method postProjectV1Method = new PostProjectV1Method(projectName, projectKey);
        postProjectV1Method.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = postProjectV1Method.callAPI().asString();
        AuthenticationContext.refreshAuthToken(AuthenticationContext.getTenantAdminAuthToken());
        return JsonPath.from(rs).getString("data.key");
    }

    @Override
    public Project createProject() {
        String projectName = RandomStringUtils.randomAlphabetic(5).concat(" Project name");
        String projectKey = projectName.substring(0, 4).concat("1").toUpperCase(Locale.ROOT);
        PostProjectV1Method postProjectV1Method = new PostProjectV1Method(projectName, projectKey);
        postProjectV1Method.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = postProjectV1Method.callAPI().asString();
        AuthenticationContext.refreshAuthToken(AuthenticationContext.getTenantAdminAuthToken());
        return JsonPath.from(rs).getObject("data", Project.class);
    }

    @Override
    public Long getProjectIdByKey(String projectKey) {
        GetProjectByKeyMethod getProjectByKeyMethod = new GetProjectByKeyMethod(projectKey);
        int statusCode = getProjectByKeyMethod.callAPI().getStatusCode();
        if (statusCode != 200) {
            LOGGER.info("Project with key {} was not found!", projectKey);
            return -1L;
        }
        String rs = getProjectByKeyMethod.callAPI().asString();
        return JsonPath.from(rs).getLong("data.id");
    }

    @Override
    public void deleteAllProjects() {
        List<String> projectKeys = getAllProjectKeys();
        projectKeys.forEach(projectKey -> deleteProjectByKey(projectKey));
    }

    @Override
    public Project createPrivateProject() {
        String projectName = RandomStringUtils.randomAlphabetic(5).concat(" Project name");
        String projectKey = projectName.substring(0, 4).concat("1").toUpperCase(Locale.ROOT);
        PostProjectV1Method postProjectV1Method = new PostProjectV1Method(projectName, projectKey, false);
        postProjectV1Method.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = postProjectV1Method.callAPI().asString();
        AuthenticationContext.refreshAuthToken(AuthenticationContext.getTenantAdminAuthToken());
        return JsonPath.from(rs).getObject("data", Project.class);
    }
}
