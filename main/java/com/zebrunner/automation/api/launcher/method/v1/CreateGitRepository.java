package com.zebrunner.automation.api.launcher.method.v1;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.testng.asserts.SoftAssert;

import com.zebrunner.automation.api.ApiUrl;
import com.zebrunner.automation.api.common.DataPayload;
import com.zebrunner.automation.api.launcher.domain.GitRepository;
import com.zebrunner.automation.api.launcher.domain.request.v1.CreateGitRepositoryRequest;
import com.zebrunner.automation.assets.ResponseAssert;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.HideRequestBodyPartsInLogs;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base-url}/v1/git-repositories?projectId=${project-id}"
)
@HideRequestBodyPartsInLogs(paths = {
        CreateGitRepositoryRequest.USERNAME_FIELD,
        CreateGitRepositoryRequest.ACCESS_TOKEN_FIELD
})
public class CreateGitRepository extends AbstractApiMethodV2 {

    public static final TypeRef<DataPayload<GitRepository>> DATA_PAYLOAD_TYPE_REF = new TypeRef<>() {
    };

    public CreateGitRepository(Long projectId, String authToken, CreateGitRepositoryRequest request) {
        super.replaceUrlPlaceholder("base-url", ApiUrl.LAUNCHER);
        super.replaceUrlPlaceholder("project-id", projectId.toString());

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);

        super.setRequestBody(request, ObjectMapperHolder.COMMON_MAPPER);
    }

    public static GitRepository invoke(Long projectId, String authToken, CreateGitRepositoryRequest request) {
        CreateGitRepository method = new CreateGitRepository(projectId, authToken, request);

        Response response = method.callAPI();

        SoftAssert softAssert = new SoftAssert();
        ResponseAssert.softAssertStatusCode(softAssert, response, HttpStatus.SC_OK);
        ResponseAssert.softAssertResponseContentType(softAssert, response, ContentType.APPLICATION_JSON.getMimeType());
        softAssert.assertAll("Create git repository response doesn't meet conditions.");

        return response.as(DATA_PAYLOAD_TYPE_REF)
                       .getData();
    }

}
