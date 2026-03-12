package com.zebrunner.automation.api.reporting.service;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.method.v1.DeleteLaunchMethod;
import com.zebrunner.automation.api.reporting.method.v1.PostStartTestRunV1Method;
import com.zebrunner.automation.api.reporting.method.v1.PutFinishTestRunV1Method;
import com.zebrunner.automation.api.reporting.method.v1.PutTestRunPlatformV1Method;

import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class TestRunServiceAPIImplV1 implements TestRunServiceAPIV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Override
    public long start(String projectKey) {
        PostStartTestRunV1Method postStartTestRunV1Method = new PostStartTestRunV1Method(projectKey, OffsetDateTime.now()
                                                                                                                   .toString());
        String response = postStartTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getLong("id");
    }


    @Override
    public long startTestRunWithName(String projectKey, String name) {
        PostStartTestRunV1Method postStartTestRunV1Method = new PostStartTestRunV1Method(projectKey, OffsetDateTime.now()
                                                                                                                   .toString());
        postStartTestRunV1Method.addProperty("name", name);
        String response = postStartTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getLong("id");
    }

    @Override
    public Launch startTestRunWithName(String projectKey, Launch launch) {
        PostStartTestRunV1Method postStartTestRunV1Method = new PostStartTestRunV1Method(launch, projectKey);
        String response = postStartTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getObject("", Launch.class);
    }

    public List<Launch> startMultipleLaunches(String projectKey, int numberOfLaunches) {
        List<Launch> launches = new ArrayList<>();

        for (int i = 0; i < numberOfLaunches; i++) {
            PostStartTestRunV1Method postStartTestRunV1Method = new PostStartTestRunV1Method(Launch.getRandomLaunch(), projectKey);
            String response = postStartTestRunV1Method.callAPI().asString();
            Launch startedLaunch = JsonPath.from(response).getObject("", Launch.class);
            launches.add(startedLaunch);
        }

        return launches;
    }

    public Launch startTestRunWithSpecificDate(String projectKey, int year, int month, int day) {
        Launch launch = Launch.builder()
                              .name("Launch №".concat(RandomStringUtils.randomAlphabetic(5)))
                              .startedAt(OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC))
                              .framework("Framework")
                              .build();

        return startTestRunWithName(projectKey, launch);
    }

    @Override
    public long startTestRunWithCertainConfig(String projectKey, String config) {
        PostStartTestRunV1Method postStartTestRunV1Method = new PostStartTestRunV1Method(projectKey, OffsetDateTime.now()
                                                                                                                   .toString());
        postStartTestRunV1Method.addProperty("config", config);
        String response = postStartTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getLong("id");
    }

    @Override
    public String finishTestRun(long testRunId) {
        PutFinishTestRunV1Method putFinishTestRunV1Method = new PutFinishTestRunV1Method(testRunId,
                OffsetDateTime.now().plusSeconds(3).toString());
        putFinishTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = putFinishTestRunV1Method.callAPI().asString();
        String status = JsonPath.from(rs).getString("status");
        return status;
    }

    public Launch finishLaunch(long launchId) {
        PutFinishTestRunV1Method putFinishTestRunV1Method = new PutFinishTestRunV1Method(launchId,
                OffsetDateTime.now().plusSeconds(3).toString());
        putFinishTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = putFinishTestRunV1Method.callAPI().asString();
        Launch launch = JsonPath.from(rs).getObject("", Launch.class);
        return launch;
    }

    @Override
    public void deleteLaunch(long projectId, long testRunId) {
        DeleteLaunchMethod deleteLaunchMethod = new DeleteLaunchMethod(projectId, testRunId);
        deleteLaunchMethod.callAPI();
        LOGGER.info("Automation launch with id=" + testRunId + " was deleted!");
    }

    public void setPlatform(long testRunId, String platform, String version) {
        PutTestRunPlatformV1Method putTestRunPlatformV1Method = new PutTestRunPlatformV1Method(testRunId, platform, version);
        putTestRunPlatformV1Method.callAPI();
    }
}