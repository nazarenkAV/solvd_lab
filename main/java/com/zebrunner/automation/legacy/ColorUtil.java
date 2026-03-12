package com.zebrunner.automation.legacy;

import org.openqa.selenium.support.Color;

@Deprecated
public class ColorUtil {

    public static String getHexColorFromString(String str) {
        return Color.fromString(str).asHex();
    }
}
