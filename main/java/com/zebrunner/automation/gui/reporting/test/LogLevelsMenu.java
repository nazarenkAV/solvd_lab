package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class LogLevelsMenu extends AbstractUIObject {

    @FindBy(xpath = ".//span[text()='fatal']/ancestor::li")
    private Element fatal;

    @FindBy(xpath = ".//span[text()='error']/ancestor::li")
    private Element error;

    @FindBy(xpath = ".//span[text()='warn']/ancestor::li")
    private Element warn;

    @FindBy(xpath = ".//span[text()='info']/ancestor::li")
    private Element info;

    @FindBy(xpath = ".//span[text()='debug']/ancestor::li")
    private Element debug;

    @FindBy(xpath = ".//span[text()='trace']/ancestor::li")
    private Element trace;

    @FindBy(xpath = ".//span[text()='All']/ancestor::li")
    private Element all;

    public LogLevelsMenu(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void chooseLogLevel(LogLevel logLevel) {
        switch (logLevel) {
            case FATAL:
                fatal.click();
                break;
            case ERROR:
                error.click();
                break;
            case WARN:
                warn.click();
                break;
            case INFO:
                info.click();
                break;
            case DEBUG:
                debug.click();
                break;
            case TRACE:
                trace.click();
                break;
            case ALL:
                all.click();
                break;
            default:
                throw new RuntimeException("There are no handler for loglevel " + logLevel);
        }
    }

}
