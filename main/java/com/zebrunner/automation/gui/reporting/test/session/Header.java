package com.zebrunner.automation.gui.reporting.test.session;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class Header extends AbstractUIObject {

    @FindBy(xpath = ".")
    private ExtendedWebElement header;

    private Element platformIcon;

    private Element browserIcon;

    private Element browserVersion;

    @FindBy(xpath = ".//*[contains(@class, 'header-title')]/span")
    private Element sessionId;

    public Header(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void click() {
        header.click();
    }

    public String getSessionId() {
        return sessionId.getText();
    }
}
