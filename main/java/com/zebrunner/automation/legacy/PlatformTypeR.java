package com.zebrunner.automation.legacy;

@Deprecated
public enum PlatformTypeR {

    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    ANDROID("android"),
    LINUX("linux"),
    API("api"),
    ND("n/d"),
    UNKNOWN("error: undefined type of platform or element is not found");

    private final String value;

    PlatformTypeR(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static PlatformTypeR ifContains(String line) {
        for (PlatformTypeR enumValue : values()) {
            if (line.toLowerCase().contains(enumValue.value)) {
                return enumValue;
            }
        }
        return null;
    }

}