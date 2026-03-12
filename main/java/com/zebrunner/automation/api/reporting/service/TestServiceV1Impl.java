package com.zebrunner.automation.api.reporting.service;

import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;
import com.zebrunner.automation.api.reporting.method.v1.PostStartTestsInTestRunV1Method;
import com.zebrunner.automation.api.reporting.method.v1.PutFinishTestInTestRunV1Method;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;

@Deprecated
public class TestServiceV1Impl implements TestServiceAPIV1 {

    @Override
    public Long startTest(Long testRunId) {
        PostStartTestsInTestRunV1Method postStartTestsInTestRunV1Method = new PostStartTestsInTestRunV1Method(testRunId);
        postStartTestsInTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String response = postStartTestsInTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getLong("id");
    }

    @Override
    public TestExecution startTest(TestExecution testExecution, Long testRunId) {
        PostStartTestsInTestRunV1Method postStartTestsInTestRunV1Method = new PostStartTestsInTestRunV1Method(testExecution, testRunId);
        postStartTestsInTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String response = postStartTestsInTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getObject("", TestExecution.class);
    }

    @Override
    public Long startTestWithMethodName(Long testRunId, String methodName) {
        PostStartTestsInTestRunV1Method postStartTestsInTestRunV1Method = new PostStartTestsInTestRunV1Method(testRunId);
        postStartTestsInTestRunV1Method.addProperty("methodName", methodName);
        postStartTestsInTestRunV1Method.addProperty("name", methodName);
        postStartTestsInTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String response = postStartTestsInTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getLong("id");
    }

    @Override
    public String finishTestAsResult(Long testRunId, Long testId, String result) {
        PutFinishTestInTestRunV1Method putFinishTestInTestRunV1Method = new PutFinishTestInTestRunV1Method(testRunId, testId, result);
        putFinishTestInTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String response = putFinishTestInTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getString("result");
    }

    @Override
    public String finishTestAsResult(Long testRunId, Long testId, FinishTestRequest finishTestRequest) {
        PutFinishTestInTestRunV1Method putFinishTestInTestRunV1Method = new PutFinishTestInTestRunV1Method(testRunId, testId, finishTestRequest);
        putFinishTestInTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String response = putFinishTestInTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getString("result");
    }

    @Override
    public TestExecution finishTextExecutionAsResult(Long testRunId, Long testId, String result) {
        PutFinishTestInTestRunV1Method putFinishTestInTestRunV1Method = new PutFinishTestInTestRunV1Method(testRunId, testId, result);
        putFinishTestInTestRunV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String response = putFinishTestInTestRunV1Method.callAPI().asString();
        return JsonPath.from(response).getObject("", TestExecution.class);
    }

}
