package com.zebrunner.automation.legacy;

import io.restassured.path.json.JsonPath;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.api.iam.method.v1.PostGenerateAuthTokenMethodIAM;

@Deprecated
public class APIContextManager {

    private String accessToken;
    private Integer tokenExpirationTime;

    public static final String TENANT_URL = ConfigHelper.getTenantUrl();
    public static final String LANDING_URL = ConfigHelper.getLandingUrl();

    public static final String LAUNCHER_API_URL = TENANT_URL + "/api/launcher";
    public static final String PROJECTS_API_URL = TENANT_URL + "/api/projects";
    public static final String REPORTING_API_URL = TENANT_URL + "/api/reporting";
    public static final String INTEGRATION_API_URL = TENANT_URL + "/api/integrations";

    public APIContextManager() {
        setAccessToken();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken() {
        String rsString = new PostGenerateAuthTokenMethodIAM(UsersEnum.MAIN_ADMIN.getUser()).callAPI().asString();
        accessToken = JsonPath.from(rsString).get("authToken");

        tokenExpirationTime = (JsonPath.from(rsString).get("authTokenExpirationInSecs") != null)
                ? JsonPath.from(rsString).getInt("authTokenExpirationInSecs") : 3600;
    }

    public Integer getTokenExpirationTime() {
        return tokenExpirationTime;
    }

}
