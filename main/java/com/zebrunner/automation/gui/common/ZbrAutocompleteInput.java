package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.NoSuchElementException;

@Getter
public class ZbrAutocompleteInput extends AbstractUIObject {

    public static final String PARENT_ROOT_XPATH = "//parent::div[contains(@class,'zbr-autocomplete   MuiBox-root')]";
    public static final String ROOT_XPATH = "div[contains(@class,'zbr-autocomplete   MuiBox-root')]";

    @FindBy(xpath = ".//input")
    private Element input;

    public ZbrAutocompleteInput(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void input(String text) {
        input.sendKeys(text);
    }

    public String getValue() {
        return input.getAttributeValue("value");
    }

    public ZbrAutocomplete inputClick() {
        input.click();
        return new ZbrAutocomplete(getDriver());
    }

    public void selectValue(String value) {
        inputClick().getOption(value).ifPresentOrElse(Element::click, () -> {
            throw new NoSuchElementException("Option '" + value + "' not found");
        });
    }

    public boolean isDisabled() {
        String disabledAttributeValue = input.getAttribute("disabled");
        return disabledAttributeValue != null && disabledAttributeValue.equals("true");
    }
}
