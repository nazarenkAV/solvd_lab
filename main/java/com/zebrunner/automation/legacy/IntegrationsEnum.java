package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum IntegrationsEnum {

    ZEBRUNNER_ENGINE(
            "Zebrunner Engine",
            "Cloud-based testing platform for web cross-browser web applications testing.",
            List.of(IntegrationCategories.TESTING_ENVIRONMENT)
    ),
    ZEBRUNNER_DEVICE_FARM(
            "Zebrunner Device Farm",
            "Cloud-based testing platform for mobile app testing on real devices.",
            List.of(IntegrationCategories.TESTING_ENVIRONMENT)
    );

    private final String name;
    private final String description;
    private final List<IntegrationCategories> categories;

}
