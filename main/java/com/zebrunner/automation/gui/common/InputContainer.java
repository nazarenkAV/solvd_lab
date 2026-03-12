package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class InputContainer extends AbstractUIObject {
    public static final String ROOT_XPATH = "//parent::div[contains(@class,'input-container')]";

    @FindBy(xpath = ".//input")
    private Element input;

    @FindBy(xpath = ".//span[contains(@class,'input-message-animation')]")
    private Element errorMessage;

    public InputContainer(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void input(String text) {
        input.sendKeys(text);
    }

    public String getValue() {
        return input.getAttributeValue("value");
    }

    public String getErrorMessageText() {
        return errorMessage.getText();
    }
}
