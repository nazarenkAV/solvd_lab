package com.zebrunner.automation.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.zebrunner.automation.config.ConfigHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiUrl {

    public static final String TENANT_URL = ConfigHelper.getTenantUrl();
    private static final String LANDING_URL = ConfigHelper.getLandingUrl();

    public static final String IAM = TENANT_URL + "/api/iam";
    public static final String TCM = TENANT_URL + "/api/tcm";
    public static final String OMS = LANDING_URL + "/api/oms";
    public static final String FILES = TENANT_URL + "/api/files";
    public static final String PUBLIC = TENANT_URL + "/api/public";
    public static final String JIRA_APP = TENANT_URL + "/api/jira-app";
    public static final String PROJECTS = TENANT_URL + "/api/projects";
    public static final String LAUNCHER = TENANT_URL + "/api/launcher";
    public static final String REPORTING = TENANT_URL + "/api/reporting";
    public static final String INTEGRATIONS = TENANT_URL + "/api/integrations";
    public static final String USER_PREFERENCES = TENANT_URL + "/api/user-preferences";

}
