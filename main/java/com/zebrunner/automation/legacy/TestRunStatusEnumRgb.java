package com.zebrunner.automation.legacy;

import org.openqa.selenium.support.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum TestRunStatusEnumRgb {

    PASSED("Passed", "rgb(68, 196, 128)"),
    FAILED("Failed", "rgb(223, 65, 80)"),
    SKIPPED("Skipped", "rgb(251, 190, 46)"),
    RETEST("Retest", "rgb(130, 184, 253)"),
    INVALID("Invalid", "rgb(174, 184, 190)"),
    BLOCKED("Blocked", "rgb(151, 94, 242)");

    private final String status;
    private final String rgbColor;

    public static TestRunStatusEnumRgb fromRgb(String rgbColor) {
        Color color = Color.fromString(rgbColor);
        int r = color.getColor().getRed();
        int g = color.getColor().getGreen();
        int b = color.getColor().getBlue();

        rgbColor = String.format("rgb(%d, %d, %d)", r, g, b);
        for (TestRunStatusEnumRgb status : values()) {
            if (status.getRgbColor().equalsIgnoreCase(rgbColor)) {
                return status;
            }
        }

        throw new IllegalArgumentException("No enum constant found for rgb color: " + rgbColor);
    }

}
