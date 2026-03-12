package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class CollapsedLaunchCardConfiguration extends AbstractUIObject {

    @FindBy(xpath = ".//div[contains(text(), 'Browser')]/ancestor::div[contains(@class, 'system-label')]")
    private Browser browser;

    @FindBy(xpath = ".//div[contains(text(), 'Build')]/ancestor::div[contains(@class, 'system-label')]")
    private Element build;

    public CollapsedLaunchCardConfiguration(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//*[@role = 'menu']"));
    }

    public boolean isBrowserPresent(PlatformTypeR browser) {
        return this.browser.getType().equals(browser);
    }

    public boolean isBuildPresent(String build) {
        return this.build.getText().equalsIgnoreCase(build);
    }
}
