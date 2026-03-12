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
        url = "${base_url}/v1/auth/login"
)
public class Login extends AbstractApiMethodV2 {

    private Login(String username, String password) {
        super.replaceUrlPlaceholder("base_url", ApiUrl.IAM);

        LoginRequest request = new LoginRequest(username, password);
        super.setRequestBody(request, ObjectMapperHolder.COMMON_MAPPER);
    }

    public static AuthenticationData invoke(String username, String password) {
        Login method = new Login(username, password);

        Response response = method.callAPI();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), HttpStatus.SC_OK, "Status code does not match.");
        softAssert.assertEquals(response.contentType(), ContentType.APPLICATION_JSON.getMimeType(), "Content type does not match.");
        softAssert.assertAll("Login response doesn't meet conditions.");

        return response.as(AuthenticationData.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Data
    @AllArgsConstructor
    private static class LoginRequest {

        private String username;
        private String password;

    }

}
