package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class ZbrTitleInput extends AbstractUIObject {
    public static final String ROOT_XPATH = "//parent::div[contains(@class,'ZbrTitleInput ')]";

    @FindBy(xpath = ".//input")
    private Element input;

    @FindBy(xpath = ".//div[@aria-label]")
    private Element inputValue;

    public ZbrTitleInput(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void input(String text) {
        input.sendKeys(text);
    }

    public String getValue() {
        return input.getAttributeValue("value");
    }

    public String getInputValue() {
        return inputValue.getText();
    }

    public boolean isReadOnly() {
        return inputValue.getAttributeValue("class").contains("__read-only");
    }
}
