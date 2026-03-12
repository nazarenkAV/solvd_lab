package com.zebrunner.automation.api.tcm.service;

import com.zebrunner.automation.api.tcm.domain.Environment;
import com.zebrunner.automation.api.tcm.method.v1.DeleteEnvironmentMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostEnvironmentMethod;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;

@Deprecated
public class EnvironmentServiceImpl implements EnvironmentService {

    @Override
    public Environment createEnvironment(Long projectId, Environment environment) {
        PostEnvironmentMethod postEnvironmentMethod = new PostEnvironmentMethod(projectId, environment);
        postEnvironmentMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);

        String rs = postEnvironmentMethod.callAPI().asString();
        return JsonPath.from(rs).getObject("data", Environment.class);
    }

    @Override
    public void deleteEnvironment(Long projectId, Long environmentId) {
        DeleteEnvironmentMethod deleteEnvironmentMethod = new DeleteEnvironmentMethod(projectId, environmentId);
        deleteEnvironmentMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);

        deleteEnvironmentMethod.callAPI();
    }
}
