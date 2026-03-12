package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum PermissionEnum {

    BILLING_ORG_PLANS_UPDATE("billing:org-plans:update"),
    BILLING_PAYMENT_METHOD_UPDATE("billing:payment-methods:update"),
    BILLING_READ("billing:read"),
    CONFIGURATION_SSO_SALM("configuration:sso:saml"),
    IAM_API_TOKENS_READ("iam:api-tokens:read"),
    IAM_API_TOKENS_REVOKE("iam:api-tokens:revoke"),
    IAM_GROUPS_DELETE("iam:groups:delete"),
    IAM_GROUPS_READ("iam:groups:read"),
    IAM_GROUPS_UPDATE("iam:groups:update"),
    IAM_INVITATIONS_DELETE("iam:invitations:delete"),
    IAM_INVITATIONS_READ("iam:invitations:read"),
    IAM_INVITATIONS_UPDATE("iam:invitations:update"),
    IAM_USERS_DELETE("iam:users:delete"),
    IAM_USERS_READ("iam:users:read"),
    IAM_USERS_UPDATE("iam:users:update"),
    PROJECTS_DELETE("projects:delete"),
    PROJECTS_READ("projects:read"),
    PROJECTS_UPDATE("projects:update"),
    REPORTING_COMPANY_LOGOS_UPDATE("reporting:company-logos:update"),
    REPORTING_DASHBOARDS_ACCESS_PRIVATE("reporting:dashboards:access-private"),
    REPORTING_FILTERS_CREATE_PUBLIC("reporting:filters:create-public"),
    REPORTING_FILTERS_DELETE_ANY("reporting:filters:delete-any"),
    REPORTING_INTEGRATIONS_ACCESS_PRIVATE("reporting:integrations:access-private"),
    REPORTING_INTEGRATIONS_READ("reporting:integrations:read"),
    REPORTING_INTEGRATIONS_UPDATE("reporting:integrations:update"),
    REPORTING_MILESTONES_ACCESS_PRIVATE("reporting:milestones:access-private"),
    REPORTING_STACKTRACE_LABELS_ASSIGN("reporting:stacktrace-labels:assign"),
    REPORTING_TEST_RUN_FILTERS_ACCESS_PRIVATE("reporting:test-run-filters:access-private"),
    REPORTING_TEST_RUNS_ACCESS_PRIVATE("reporting:test-runs:access-private"),
    REPORTING_TEST_SESSIONS_TOKEN_REFRESH("reporting:test-sessions:token:refresh");

    private final String name;

}
