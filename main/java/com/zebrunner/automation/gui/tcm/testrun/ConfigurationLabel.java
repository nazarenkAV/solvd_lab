package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ConfigurationLabel extends AbstractUIObject {
    public static final String ROOT_XPATH = ".//div[@class='system-label  ']";

    @FindBy(xpath = ".//div[@class='system-label__key']")
    private ExtendedWebElement configurationGroupLabel;

    @FindBy(xpath = ".//div[@class='system-label__value']")
    private ExtendedWebElement configurationOptionLabel;

    public ConfigurationLabel(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public String getConfigurationGroupName() {
        return configurationGroupLabel.getText();
    }

    public String getConfigurationOptionName() {
        return configurationOptionLabel.getText().replace(": ", "").trim();
    }
}
