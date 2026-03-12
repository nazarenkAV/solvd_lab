package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class RerunOption extends AbstractUIObject {

    public static final String ROOT_XPATH = "//label[contains(@class, 'MuiFormControlLabel-labelPlacementEnd')]";

    @FindBy(xpath = ".//span[contains(@class, 'MuiFormControlLabel-label')]")
    private ExtendedWebElement nameLabel;

    @FindBy(xpath = ".//input[contains(@class, 'PrivateSwitchBase-input')]")
    private ExtendedWebElement checkbox;

    public RerunOption(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getOptionName() {
        return nameLabel.getText();
    }

    public void clickCheckbox() {
        checkbox.click();
    }

    public boolean isOptionSelected() {
        return checkbox.isSelected();
    }
}
