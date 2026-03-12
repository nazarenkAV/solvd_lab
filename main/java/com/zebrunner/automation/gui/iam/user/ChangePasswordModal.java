package com.zebrunner.automation.gui.iam.user;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ChangePasswordModal extends AbstractModal<ChangePasswordModal> {

    @FindBy(id = "password")
    private ExtendedWebElement passwordTextField;

    @FindBy(xpath = ".//i[@class='fa fa-eye']")
    private ExtendedWebElement showPass;

    @FindBy(xpath = "//*[text()='save']")
    private ExtendedWebElement saveButton;

    public ChangePasswordModal(WebDriver driver) {
        super(driver);
    }

    public void typePassword(String password) {
        passwordTextField.type(password);
    }

    public void save() {
        saveButton.click();
    }
}
