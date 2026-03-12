package com.zebrunner.automation.api.iam.method.v1;

import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.testng.asserts.SoftAssert;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.zebrunner.automation.api.ApiUrl;
import com.zebrunner.automation.api.iam.domain.ApiToken;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        url = "${base_url}/v1/api-tokens",
        methodType = HttpMethodType.POST
)
public class CreateApiToken extends AbstractApiMethodV2 {

    private CreateApiToken(CreateApiTokenRequest request, String authToken) {
        super.replaceUrlPlaceholder("base_url", ApiUrl.IAM);

        super.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);

        super.setRequestBody(request, ObjectMapperHolder.COMMON_MAPPER);
    }

    public static ApiToken invoke(String name, String authToken) {
        CreateApiTokenRequest request = new CreateApiTokenRequest(name);
        CreateApiToken method = new CreateApiToken(request, authToken);

        Response response = method.callAPI();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), HttpStatus.SC_CREATED, "Status code does not match.");
        softAssert.assertEquals(response.contentType(), ContentType.APPLICATION_JSON.getMimeType(), "Content type does not match.");
        softAssert.assertAll("Create api token response doesn't meet conditions.");

        return response.as(ApiToken.DATA_PAYLOAD_TYPE)
                       .getData();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Data
    @AllArgsConstructor
    private static class CreateApiTokenRequest {

        private String name;

    }

}
