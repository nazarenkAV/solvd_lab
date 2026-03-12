package com.zebrunner.automation.gui.iam.user;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class UserCardMenu extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//*[@role ='menu']";

    @FindBy(xpath = ".//span[text()='Edit']")
    private ExtendedWebElement editButton;

    @FindBy(xpath = ".//span[text()='Deactivate']")
    private ExtendedWebElement deactivateButton;

    @FindBy(xpath = ".//span[text()='Change password']")
    private ExtendedWebElement changePassword;

    @FindBy(xpath = ".//span[text()='Resend invitation']")
    private ExtendedWebElement resentInvitation;

    @FindBy(xpath = ".//span[text()='Activate']")
    private ExtendedWebElement activateButton;

    @FindBy(xpath = ".//span[text()='Convert to regular user']")
    private ExtendedWebElement convertToRegularUser;

    @FindBy(xpath = ".//span[text()='Convert to read-only user']")
    private ExtendedWebElement convertToReadOnlyUser;

    public UserCardMenu(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_LOCATOR));
    }

    public UserProcessModal openEditUserModal() {
        editButton.click();
        return new UserProcessModal(getDriver());
    }

    public UserStatusConfirmationModal clickDeactivateButton() {
        deactivateButton.click();
        return new UserStatusConfirmationModal(getDriver());
    }

    public UserStatusConfirmationModal clickActivateButton() {
        activateButton.click();
        return new UserStatusConfirmationModal(getDriver());
    }

    public UserStatusConfirmationModal clickConvertToRegularUserButton() {
        convertToRegularUser.click();
        return new UserStatusConfirmationModal(getDriver());
    }

    public UserStatusConfirmationModal clickConvertToReadOnlyUserButton() {
        convertToReadOnlyUser.click();
        return new UserStatusConfirmationModal(getDriver());
    }

    public ChangePasswordModal openChangePasswordModal() {
        changePassword.click();
        return new ChangePasswordModal(getDriver());
    }

    public void resentInvitation() {
        resentInvitation.click();
    }
}
