package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class CustomLabel extends AbstractUIObject {
    @FindBy(xpath = ".//div[@class='custom-label__key']")
    private Element key;
    @FindBy(xpath = ".//div[@class='custom-label__value']")
    private Element value;

    public CustomLabel(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTextValue() {
        return value.getText().replace(": ", "");
    }

    public String getTextKey() {
        return key.getText();
    }

    public String getHref() {
        return this.getRootExtendedElement().getAttribute("href");
    }
}
