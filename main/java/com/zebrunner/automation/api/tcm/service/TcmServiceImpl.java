package com.zebrunner.automation.api.tcm.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.zebrunner.automation.api.tcm.domain.SharedStepsBunch;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestCaseFieldsLayout;
import com.zebrunner.automation.api.tcm.domain.TestCaseSystemFields;
import com.zebrunner.automation.api.tcm.domain.TestPlan;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunResult;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.request.v1.AddTestRunCaseResultsRequest;
import com.zebrunner.automation.api.tcm.domain.request.v1.CreateTestPlanRequest;
import com.zebrunner.automation.api.tcm.domain.request.v1.CreateTestRunRequest;
import com.zebrunner.automation.api.tcm.domain.request.v1.UpdateTestRunCasesRequest;
import com.zebrunner.automation.api.tcm.method.v1.DeleteTestCaseMethod;
import com.zebrunner.automation.api.tcm.method.v1.DeleteTestSuiteMethod;
import com.zebrunner.automation.api.tcm.method.v1.GetTestCaseFieldsLayoutMethod;
import com.zebrunner.automation.api.tcm.method.v1.GetTestCaseSystemFieldsMethod;
import com.zebrunner.automation.api.tcm.method.v1.GetTestRunSettingsMethod;
import com.zebrunner.automation.api.tcm.method.v1.PatchAssignTestCaseMethod;
import com.zebrunner.automation.api.tcm.method.v1.PatchUpdateTestCaseMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostAddTestRunResultsMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostCloseTestRunMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostCreateSharedStepsMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostCreateTestCaseMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostCreateTestPlanMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostCreateTestRunMethod;
import com.zebrunner.automation.api.tcm.method.v1.PostCreateTestSuiteMethod;
import com.zebrunner.automation.api.tcm.method.v1.PutUpdateTestRunMethod;
import com.zebrunner.automation.legacy.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.SkipException;

@Deprecated
public class TcmServiceImpl implements TcmService {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.buildNew();

    @Override
    public TestSuite createTestSuite(Long projectId, TestSuite testSuite) {
        PostCreateTestSuiteMethod createMethod = new PostCreateTestSuiteMethod(projectId, testSuite);
        createMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestSuite.class);
    }

    @Override
    public TestCase createTestCase(Long projectId, Long testSuiteId, TestCase testCase) {
        PostCreateTestCaseMethod createMethod = new PostCreateTestCaseMethod(projectId, testSuiteId, testCase);
        createMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestCase.class);
    }

    @Override
    public TestCase createTestCase(Long projectId, Long testSuiteId) {

        TestCase testCase = new TestCase("Case " + UUID.randomUUID());

        PostCreateTestCaseMethod createMethod = new PostCreateTestCaseMethod(projectId, testSuiteId, testCase);
        createMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestCase.class);
    }

    @Override
    public List<TestCase> createTestCases(Long projectId, Long testSuiteId, int numberOfCases) {
        return IntStream.range(0, numberOfCases)
                        .mapToObj(i -> createTestCase(projectId, testSuiteId))
                        .collect(Collectors.toList());
    }

    @Override
    public TestCase updateTestCase(Long projectId, Long testCaseId, TestCase testCase) {
        PatchUpdateTestCaseMethod updateTestCaseMethod = new PatchUpdateTestCaseMethod(projectId, testCaseId, testCase);
        String rs = updateTestCaseMethod.callAPI().asString();

        updateTestCaseMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);

        return JsonPath.from(rs).getObject("data", TestCase.class);
    }

    @Override
    public SharedStepsBunch createSharedStep(Long projectId, SharedStepsBunch bunch) {
        PostCreateSharedStepsMethod createMethod = new PostCreateSharedStepsMethod(projectId, bunch);
        createMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", SharedStepsBunch.class);
    }

    @Override
    public void deleteTestCase(Long projectId, Long testCaseId) {
        DeleteTestCaseMethod deleteMethod = new DeleteTestCaseMethod(projectId, testCaseId);
        deleteMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        deleteMethod.callAPI();
    }

    public void deleteTestSuite(Long projectId, Long testSuiteId) {
        DeleteTestSuiteMethod deleteMethod = new DeleteTestSuiteMethod(projectId, testSuiteId);
        deleteMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        deleteMethod.callAPI();
    }

    @Override
    public TestRun createTestRun(Long projectId, TestRun testRun) {
        CreateTestRunRequest request = OBJECT_MAPPER.convertValue(testRun, CreateTestRunRequest.class);

        PostCreateTestRunMethod createMethod = new PostCreateTestRunMethod(projectId, request);
        createMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestRun.class);
    }

    @Override
    public List<TestRun> createTestRuns(Long projectId, int numberOfRuns) {
        return IntStream.range(0, numberOfRuns)
                        .mapToObj(i -> createTestRun(projectId, TestRun.createWithRandomName()))
                        .collect(Collectors.toList());
    }

    @Override
    public TestRun createTestRun(Long projectId, List<TestCase> testCases, TestRun testRun) {
        CreateTestRunRequest request = OBJECT_MAPPER.convertValue(testRun, CreateTestRunRequest.class);
        List<CreateTestRunRequest.TestCase> testCaseRequests = testCases.stream()
                                                                        .map(testCase -> new CreateTestRunRequest.TestCase(testCase.getId()))
                                                                        .collect(Collectors.toList());
        request.setTestCases(testCaseRequests);

        PostCreateTestRunMethod createMethod = new PostCreateTestRunMethod(projectId, request);
        createMethod.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestRun.class);
    }

    @Override
    public void addTestRunResults(Long projectId, Long testRunId, List<Long> testCaseIds, TestRunResult result) {
        PostAddTestRunResultsMethod addResultsMethod = new PostAddTestRunResultsMethod(projectId, testRunId, testCaseIds, result);
        addResultsMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        addResultsMethod.callAPI();
    }

    @Override
    public TestRunSettings getTestRunSettings(Long projectId) {
        GetTestRunSettingsMethod getMethod = new GetTestRunSettingsMethod(projectId);

        getMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestRunSettings.class);
    }

    @Override
    public void addTestRunResults(Long projectId, TestRun testRun, TestCase testCase, TestRunSettings testRunSettings, String statusName) {
        AddTestRunCaseResultsRequest.ResultStatus resultStatus = testRunSettings.getResultStatuses()
                                                                                .stream()
                                                                                .filter(status -> status.getName()
                                                                                                        .equalsIgnoreCase(statusName))
                                                                                .findFirst()
                                                                                .orElseThrow(() -> new SkipException("There are no existing result with status '" + statusName + "'"));

        TestRunResult testRunResult = TestRunResult.with(resultStatus);
        addTestRunResults(projectId, testRun.getId(), List.of(testCase.getId()), testRunResult);
    }

    @Override
    public void addTestRunResults(Long projectId, TestRun testRun, List<TestCase> testCases, TestRunSettings testRunSettings, String statusName) {
        AddTestRunCaseResultsRequest.ResultStatus resultStatus = testRunSettings.getResultStatuses()
                                                                                .stream()
                                                                                .filter(status -> status.getName()
                                                                                                        .equalsIgnoreCase(statusName))
                                                                                .findFirst()
                                                                                .orElseThrow(() -> new SkipException("There are no existing result with status '" + statusName + "'"));

        TestRunResult testRunResult = TestRunResult.with(resultStatus);
        List<Long> testCaseIds = testCases.stream().map(TestCase::getId).collect(Collectors.toList());
        addTestRunResults(projectId, testRun.getId(), testCaseIds, testRunResult);
    }

    @Override
    public void assignTestCases(Long projectId, TestRun testRun, List<TestCase> testCases, Long assigneeId) {
        List<UpdateTestRunCasesRequest.TestCase> requestTestCases = testCases.stream()
                                                                             .map(tc -> new UpdateTestRunCasesRequest.TestCase(tc.getId(), assigneeId))
                                                                             .collect(Collectors.toList());

        UpdateTestRunCasesRequest request = new UpdateTestRunCasesRequest();
        request.setItems(requestTestCases);

        PatchAssignTestCaseMethod assignTestCaseMethod = new PatchAssignTestCaseMethod(projectId, testRun.getId(), request);
        assignTestCaseMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        assignTestCaseMethod.callAPI();
    }

    @Override
    public TestCaseFieldsLayout getTestCaseFields(Long projectId) {
        GetTestCaseFieldsLayoutMethod getMethod = new GetTestCaseFieldsLayoutMethod(projectId);

        getMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestCaseFieldsLayout.class);
    }

    @Override
    public TestCaseSystemFields getTestCaseSystemFields(Long projectId) {
        GetTestCaseSystemFieldsMethod getMethod = new GetTestCaseSystemFieldsMethod(projectId);

        getMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestCaseSystemFields.class);
    }

    public void closeTestRun(Long projectId, Long testRunId) {
        PostCloseTestRunMethod postCloseTestRunMethod = new PostCloseTestRunMethod(projectId, testRunId);

        postCloseTestRunMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        postCloseTestRunMethod.callAPI();
    }

    public TestRun updateTestRun(Long projectId, TestRun testRun) {
        PutUpdateTestRunMethod updateTestRunMethod = new PutUpdateTestRunMethod(projectId, testRun);
        updateTestRunMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);

        String rs = updateTestRunMethod.callAPI().asString();
        return JsonPath.from(rs).getObject("data", TestRun.class);
    }

    @Override
    public TestPlan createTestPlan(Long projectId, TestPlan testPlan) {
        CreateTestPlanRequest request = OBJECT_MAPPER.convertValue(testPlan, CreateTestPlanRequest.class);

        TestRun testRun = new TestRun(testPlan.getTitle());
        testRun.setConfigurations(testPlan.getConfigurations());

        request.setTestRuns(Collections.singletonList(createTestRun(projectId, testRun)));

        PostCreateTestPlanMethod createMethod = new PostCreateTestPlanMethod(projectId, request);
        String rs = createMethod.callAPI().asString();

        return JsonPath.from(rs).getObject("data", TestPlan.class);
    }
}
