package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class ZbrSwitch extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[contains(@class,'ZbrSwitch-root')]";

    @FindBy(xpath = ".//input")
    private Element input;

    @FindBy(xpath = ".//span[contains(@class,'switchBase')]")
    private Element isSwitchedChecker;

    public ZbrSwitch(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public Boolean isSwitched() {
        return Boolean.valueOf(isSwitchedChecker.getAttributeValue("class").contains("Mui-checked"));
    }

    public void click() {
        input.click();
    }
}
