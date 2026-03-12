package com.zebrunner.automation.api.iam.method.v1;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.testng.asserts.SoftAssert;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.zebrunner.automation.api.ApiUrl;
import com.zebrunner.automation.api.iam.domain.AuthenticationData;
import com.zebrunner.automation.util.ObjectMapperHolder;
import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.http.HttpMethodType;

@Endpoint(
        methodType = HttpMethodType.POST,
        url = "${base_url}/v1/auth/refresh"
)
public class RefreshToken extends AbstractApiMethodV2 {

    private RefreshToken(String refreshToken) {
        super.replaceUrlPlaceholder("base_url", ApiUrl.IAM);

        RefreshAuthTokenRequest request = new RefreshAuthTokenRequest(refreshToken);
        super.setRequestBody(request, ObjectMapperHolder.COMMON_MAPPER);
    }

    public static AuthenticationData invoke(String refreshToken) {
        RefreshToken method = new RefreshToken(refreshToken);

        Response response = method.callAPI();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), HttpStatus.SC_OK, "Status code does not match.");
        softAssert.assertEquals(response.contentType(), ContentType.APPLICATION_JSON.getMimeType(), "Content type does not match.");
        softAssert.assertAll("Refresh auth token response doesn't meet conditions.");

        return response.as(AuthenticationData.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Data
    @AllArgsConstructor
    private static class RefreshAuthTokenRequest {

        private String refreshToken;

    }

}
