package com.zebrunner.automation.legacy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TestRunStatusEnumR {

    PASSED("PASSED", "#44c480"),
    FAILED("FAILED", "#df4150"),
    IN_PROGRESS("IN_PROGRESS", "#82b8fd"),
    SKIPPED("SKIPPED", "#fea521"),
    QUEUED("QUEUED", "#dfe3e5"),
    ABORTED("ABORTED", "#aeb8be"),
    UNKNOWN("error: unknown type of run status or element not found", "no colour");

    private final String value;
    private final String colour;

    public String value() {
        return value;
    }

    public static TestRunStatusEnumR colourStatus(String colour) {
        for (TestRunStatusEnumR enumValue : values()) {
            if (colour.contains(enumValue.colour)) {
                return enumValue;
            }
        }
        return null;
    }

}