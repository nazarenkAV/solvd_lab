package com.zebrunner.automation.legacy;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum TestCardStatuesEnum {

    FAILED(1, "FAILED", "#df4150"),
    SKIPPED(2, "SKIPPED", "#fea521"),
    IN_PROGRESS(3, "IN_PROGRESS", "#82b8fd"),
    PASSED(4, "PASSED", "#44c480"),
    ABORTED(5, "ABORTED", "#aeb8be"),
    UNKNOWN(0, "error: unknown type of card status or element not found", "no colour");

    private final int orderId;
    private final String value;
    private final String colour;

    public static String getColourStatus(String colour) {
        for (TestCardStatuesEnum enumValue : values()) {
            if (colour.contains(enumValue.colour)) {
                return enumValue.value;
            }
        }
        return null;
    }

    public static TestCardStatuesEnum getEnumStatusFromColor(String colour) {
        for (TestCardStatuesEnum enumValue : values()) {
            if (colour.contains(enumValue.colour)) {
                return enumValue;
            }
        }
        return UNKNOWN;
    }

}
