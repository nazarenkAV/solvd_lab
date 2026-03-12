package com.zebrunner.automation.util;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class CornerClickerUtil {

    /**
     * Util to click to the selected corner of selected element.
     * Useful when element you need if partial behind the other element.
     *
     * @param up      true = upper corners, false = bottom corners
     * @param left    true = left corners, false = right corners
     * @param driver  driver
     * @param element element which corner you need to click
     */
    public static void clickToCorner(Boolean up, Boolean left, WebDriver driver, ExtendedWebElement element) {
        Actions actions = new Actions(driver);

        if (up) {
            if (left) {
                // upper left
                actions.moveToElement(
                                element.getElement(),
                                -(element.getSize().width / 2) + 1, -(element.getSize().height / 2) + 1)
                        .click();
            } else {
                // upper right
                actions.moveToElement(
                                element.getElement(),
                                (element.getSize().width / 2) - 1, -(element.getSize().height / 2) + 1)
                        .click();
            }
        } else {
            if (left) {
                // bottom left
                actions.moveToElement(
                                element.getElement(),
                                -(element.getSize().width / 2) + 1, (element.getSize().height / 2) - 1)
                        .click();
            } else {
                // bottom right
                actions.moveToElement(
                                element.getElement(),
                                (element.getSize().width / 2) - 1, (element.getSize().height / 2) - 1)
                        .click();
            }
        }
        actions.perform();
    }

}
