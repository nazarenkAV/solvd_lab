package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum InstanceTypeEnum {

    SMALL("Small (1 CPU, 1 GB RAM)"),
    MEDIUM("Medium (2 CPU, 2 GB RAM)"),
    LARGE("Large (4 CPU, 4 GB RAM)"),
    XLARGE("X large (8 CPU, 8 GB RAM)");

    private final String value;

}
