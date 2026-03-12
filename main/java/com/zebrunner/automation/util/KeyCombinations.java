package com.zebrunner.automation.util;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.decorators.Decorated;

@Slf4j
public enum KeyCombinations {

    CTRL_SHIFT_S {
        @Override
        public Actions createAction(WebDriver driver) {
            Actions actions = new Actions(driver);
            Keys controlKey = isMac(driver) ? Keys.COMMAND : Keys.CONTROL;

            log.info("Using {} + SHIFT + S combination", controlKey == Keys.COMMAND ? "COMMAND" : "CONTROL");

            return actions
                    .keyDown(controlKey)
                    .keyDown(Keys.SHIFT)
                    .sendKeys("S")
                    .keyUp(Keys.SHIFT)
                    .keyUp(controlKey);
        }
    },
    CTRL_A {
        @Override
        public Actions createAction(WebDriver driver) {
            Actions actions = new Actions(driver);
            Keys key = isMac(driver) ? Keys.COMMAND : Keys.CONTROL;

            log.debug("Using {} + C combination", key == Keys.COMMAND ? "COMMAND" : "CONTROL");

            return actions
                    .keyDown(key)
                    .sendKeys("A")
                    .keyUp(key);
        }
    },

    SHIFT_DOWN {
        @Override
        public Actions createAction(WebDriver driver) {
            Actions actions = new Actions(driver);
            return actions.keyDown(Keys.SHIFT);
        }
    },
    SHIFT_UP {
        @Override
        public Actions createAction(WebDriver driver) {
            Actions actions = new Actions(driver);
            return actions.keyUp(Keys.SHIFT);
        }
    };


    public abstract Actions createAction(WebDriver driver);

    public static boolean isMac(WebDriver drv) {
        String os;

        if (drv instanceof Decorated<?>) {
            log.debug("Using remote driver");

            os = ((RemoteWebDriver) (((Decorated<?>) drv).getOriginal())).getCapabilities()
                    .getPlatformName().toString().toLowerCase();
        } else
            os = System.getProperty("os.name").toLowerCase();

        log.debug("OS is: " + os);
        return os.contains("mac");
    }

}
