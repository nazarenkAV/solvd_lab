package com.zebrunner.automation.util;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.By;

public class GetBy {

    public static String elementByToString(ExtendedWebElement element) {
        String dirty = element.getBy().toString();
        return dirty.replaceFirst("By.*:.", "");
    }

    public static String byToString(By by) {
        String dirty = by.toString();
        return dirty.replaceFirst("By.*:.", "");
    }

    public static String addToBy(ExtendedWebElement element, String toAdd) {
        return elementByToString(element) + toAdd;
    }

}
