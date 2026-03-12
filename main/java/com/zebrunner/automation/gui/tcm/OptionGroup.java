package com.zebrunner.automation.gui.tcm;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class OptionGroup extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//label[contains(@class, 'run__config-option')]";

    @FindBy(xpath = ".//span[@class='testing-config__option-name']")
    private ExtendedWebElement nameLabel;
    @FindBy(xpath = ".//*[name()='svg' and @class='testing-config__option-action'][1]")
    private ExtendedWebElement editButton;
    @FindBy(xpath = ".//*[name()='svg' and @class='testing-config__option-action'][2]")
    private ExtendedWebElement deleteIcon;
    @FindBy(xpath = ".//input[contains(@class, 'PrivateSwitchBase-input')]")
    private ExtendedWebElement selectButton;

    public OptionGroup(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public EditConfigurationModal clickEditButton() {
        nameLabel.hover();
        editButton.clickByActions();

        return new EditConfigurationModal(getDriver());
    }

    public DeleteModal clickDeleteIcon() {
        nameLabel.hover();
        deleteIcon.clickByActions();

        return new DeleteModal(getDriver());
    }

    public String getOptionName() {
        return nameLabel.getText();
    }

    public void select() {
        nameLabel.click();
    }

    public boolean isSelected() {
        return selectButton.getAttribute("checked") != null;
    }

}
