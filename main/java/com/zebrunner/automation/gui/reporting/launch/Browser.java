package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class Browser extends AbstractUIObject {

    @FindBy(xpath = ".//div[@class='system-label__value']")
    private Element browserWithVersion;

    public Browser(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public PlatformTypeR getType() {
        if (browserWithVersion.isStateMatches(Condition.PRESENT)) {
            String browser = browserWithVersion.getText().split("\\s+")[1];
            return PlatformTypeR.ifContains(browser);
        } else {
            return PlatformTypeR.UNKNOWN;
        }
    }

    public String getBrowserVersion() {
        if (browserWithVersion.isStateMatches(Condition.PRESENT)) {
            String version = browserWithVersion.getText().split("\\s+")[2];
            return version;
        }
        return null;
    }

}