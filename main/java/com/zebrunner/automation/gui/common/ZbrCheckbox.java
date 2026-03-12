package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class ZbrCheckbox extends AbstractUIObject {
    public static final String PARENT_ROOT_XPATH = "//parent::*//*[contains(@class,'ZbrCheckbox')]";
    public static final String ROOT_XPATH = ".//*[contains(@class,'ZbrCheckbox')]";

    @FindBy(xpath = ".//input")
    private ExtendedWebElement input;


    public ZbrCheckbox(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public ZbrCheckbox(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Boolean _isChecked() {
        return Boolean.valueOf(input.getAttribute("value"));
    }

    public void _click() {
        input.click();
    }
}
