package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
@Slf4j
public class LaunchCardConfiguration extends AbstractUIObject {

    @FindBy(xpath = ".//*[@d='" + SvgPaths.ARTIFACT
            + "']/ancestor::div[contains(@class,'custom-label   _main')]")
    public Element artifactIcon;

    @FindBy(xpath = ".//div[contains(@class, 'system-label') and contains(text(),'Locale')]/parent::div")
    protected Element locale;

    @FindBy(xpath = ".//div[contains(@class, 'system-label') and contains(text(),'Platform')]/parent::div")
    protected PlatformR platform;

    @FindBy(xpath = ".//div[contains(@class, 'system-label') and contains(text(),'Browser')]/parent::div")
    protected Browser browser;

    @FindBy(xpath = ".//div[contains(@class, 'system-label') and contains(text(),'Build')]/parent::div")
    protected Element build;

    @FindBy(xpath = ".//*[contains(text(),'Execution Log')]/ancestor::a[@href]")
    protected Element executionLogLink;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public LaunchCardConfiguration(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isBrowserPresent(PlatformTypeR browser) {
        return this.browser.getType().equals(browser);
    }

    public boolean isBuildPresent(String build) {
        return this.build.getText().equalsIgnoreCase(build);
    }

    public boolean isArtifactsExpanded() {
        return Boolean.parseBoolean(artifactIcon.getAttributeValue("class").concat("_expanded"));
    }

    public LaunchCardConfiguration expandArtifacts() {
        if (!isArtifactsExpanded()) {
            artifactIcon.click();
            log.info("artifacts are expanded!");
        }
        return this;
    }

    public void clickExecutionLogs() {
        expandArtifacts();
        executionLogLink.click();
    }

    public String hoverExecutionLogAndGetToolTipValue() {
        executionLogLink.hover();
        pause(1);
        return tooltip.getTooltipText();
    }
}
