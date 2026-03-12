package com.zebrunner.automation.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.zebrunner.carina.utils.common.CommonUtils;

public class ComponentUtil {

    @Deprecated
    public static void pressEscape(WebDriver driver) {
        Actions builder = new Actions(driver);
        builder.sendKeys(Keys.ESCAPE)
                .perform();
    }

    public static void closeAnyMenuOrModal(WebDriver driver) {
        PageUtil.guaranteedToHideDropDownList(driver);
    }

    public static void scrollToElementCenter(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
                        element);
        CommonUtils.pause(1);
    }

}
