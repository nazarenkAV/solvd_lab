package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum IntegrationCategories {

    TEST_MANAGEMENT("Test Management"),
    ISSUE_MANAGEMENT("Issue Management"),
    TESTING_ENVIRONMENT("Testing Environment"),
    MESSAGING_AND_NOTIFICATION("Messaging and Notifications"),
    APP_LIFECYCLE_MANAGEMENT("Application Lifecycle Management"),
    REQUIREMENTS_MANAGEMENT("Requirements Management");

    private final String categoryName;

}
