package com.zebrunner.automation.assets;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.zebrunner.automation.api.common.ErrorResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseAssert {

    private static final String STATUS_CODE_DOES_NOT_MATCH_MESSAGE = "Status code does not match.";
    private static final String CONTENT_TYPE_DOES_NOT_MATCH_MESSAGE = "Content-Type does not match.";
    private static final String ERROR_RESPONSE_CODE_DOES_NOT_MATCH_MESSAGE = "Error response code does not match.";

    public static void assertStatusCode (Response response, int expectedStatusCode) {
        Assert.assertEquals(response.statusCode(), expectedStatusCode, STATUS_CODE_DOES_NOT_MATCH_MESSAGE);
    }

    public static void softAssertStatusCode(SoftAssert softAssert, Response response, int expectedStatusCode) {
        softAssert.assertEquals(response.statusCode(), expectedStatusCode, STATUS_CODE_DOES_NOT_MATCH_MESSAGE);
    }

    public static void assertResponseContentType(Response response, String expectedContentType) {
        Assert.assertEquals(response.contentType(), expectedContentType, CONTENT_TYPE_DOES_NOT_MATCH_MESSAGE);
    }

    public static void softAssertResponseContentType(SoftAssert softAssert, Response response, String expectedContentType) {
        softAssert.assertEquals(response.contentType(), expectedContentType, CONTENT_TYPE_DOES_NOT_MATCH_MESSAGE);
    }

    public static void assertErrorResponseCode(Response response, String expectedCode) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        Assert.assertEquals(errorResponse.getCode(), expectedCode, ERROR_RESPONSE_CODE_DOES_NOT_MATCH_MESSAGE);
    }

    public static void softAssertErrorResponseCode(SoftAssert softAssert, Response response, String expectedCode) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        softAssert.assertEquals(errorResponse.getCode(), expectedCode, ERROR_RESPONSE_CODE_DOES_NOT_MATCH_MESSAGE);
    }

}
