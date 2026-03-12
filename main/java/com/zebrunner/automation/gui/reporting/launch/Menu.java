package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class Menu extends AbstractUIObject {

    @FindBy(xpath = "//ul[@role='menu']")
    public MenuContent menuContent;

    public Menu(WebDriver driver) {
        super(driver);
    }
}



