package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum BreadcrumbsEnum {

    PROJECTS("Projects"),
    TEST_RUNS("Test runs"),
    LAUNCHERS("Launchers"),
    LAUNCHES("Launches"),
    INTEGRATIONS("Integrations");

    private final String breadcrumb;

}
