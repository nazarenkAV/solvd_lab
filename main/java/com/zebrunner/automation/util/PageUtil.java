package com.zebrunner.automation.util;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Set;

public class PageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void toOtherTab(WebDriver driver) {
        String firstTab = driver.getWindowHandle();
        LOGGER.info("Closing current tab");
        driver.close();
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            if (!window.equals(firstTab)) {
                LOGGER.info("Switching to other existing tab");
                driver.switchTo().window(window);
            }
        }
    }

    public static void switchToWindow(WebDriver driver, String title) {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(title)){
                break;
            }
        }
    }

    public static void toOtherTabWithoutClosingFirstOne(WebDriver driver) {
        String firstTab = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            if (!window.equals(firstTab)) {
                LOGGER.info("Switching to other existing tab");
                driver.switchTo().window(window);
            }
        }
    }

    public static int getNumberOfOpenedWindows(WebDriver driver) {
        Set<String> windows = driver.getWindowHandles();
        return windows.size();
    }

    public static void guaranteedToHideDropDownList(WebDriver driver) {
        Actions builder = new Actions(driver);
        builder.sendKeys(Keys.ESCAPE).perform();
    }

    public static void clickOnKeyBoard(String key, WebDriver driver) {
        new Actions(driver)
                .sendKeys(key).build().perform();
    }
}
