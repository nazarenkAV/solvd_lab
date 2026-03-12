package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
@Slf4j
public class PlatformR extends AbstractUIObject {

    @FindBy(xpath = ".//div[@class='system-label__value']")
    private Element browserWithVersion;

    public PlatformR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public PlatformTypeR getPlatformType() {
        if (browserWithVersion.isStateMatches(Condition.PRESENT)) {
            String browser = browserWithVersion.getText().split("\\s+")[1];
            browser.replace(" ", "");
            log.info("Browser is " + browser);
            return PlatformTypeR.ifContains(browser);
        } else {
            return PlatformTypeR.UNKNOWN;
        }
    }

    public String getBrowserVersion() {
        if (browserWithVersion.isStateMatches(Condition.PRESENT)) {
            String version = browserWithVersion.getText().split("\\s+")[2];
            log.info("Browser version is " + version);
            return version;
        }
        return null;
    }

}