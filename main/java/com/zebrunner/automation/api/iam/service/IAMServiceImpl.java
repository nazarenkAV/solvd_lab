package com.zebrunner.automation.api.iam.service;

import com.zebrunner.automation.api.iam.domain.AuthenticationData;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.iam.method.v1.PostGenerateAuthTokenMethodIAM;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;

@Deprecated
public class IAMServiceImpl implements IAMService {

    @Override
    public AuthenticationData login(User user) {
        PostGenerateAuthTokenMethodIAM login = new PostGenerateAuthTokenMethodIAM(user);
        login.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = login.callAPI().asString();
        return JsonPath.from(rs).getObject("", AuthenticationData.class);
    }

}
