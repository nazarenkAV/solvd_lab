package com.zebrunner.automation.gui.iam.user;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Getter
public class UserProcessModal extends AbstractModal<UserProcessModal> {
    public static final String NEW_USER_MODAL_TITLE = "New user";
    public static final String EDIT_USER_MODAL_TITLE = "Edit user";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(id = "modalTitle")
    private Element modalTitle;

    @FindBy(id = "username")
    private Element username;

    @FindBy(id = "firstName")
    private Element firstName;

    @FindBy(id = "lastName")
    private Element lastName;

    @FindBy(id = "email")
    private Element email;

    @FindBy(id = "password")
    private Element password;

    @FindBy(id = "Repeat password")
    private Element repeatPassword;

    @FindBy(id = "deactivate")
    private Element deactivate;

    @FindBy(id = "close")
    private Element closeWindow;

    @FindBy(xpath = "//*[@class = 'checkbox-label'][contains(text(), 'Read-only')]/parent::div//input")
    private Element readOnlyUserCheckBox;

    public UserProcessModal(WebDriver driver) {
        super(driver);
    }

    public boolean isUserNamePresent() {
        LOGGER.info("Checking the field 'Username'...");
        return username.isStateMatches(Condition.PRESENT);
    }

    public boolean isFirstNamePresent() {
        LOGGER.info("Checking the field 'Firstname'...");
        return firstName.isStateMatches(Condition.PRESENT);
    }

    public boolean isLastNamePresent() {
        LOGGER.info("Checking the field 'Lastname'...");
        return lastName.isStateMatches(Condition.PRESENT);
    }

    public boolean isEmailPresent() {
        LOGGER.info("Checking the field 'Email'...");
        return email.isStateMatches(Condition.PRESENT);
    }

    public boolean isPasswordPresent() {
        LOGGER.info("Checking the field 'Password'...");
        return password.isStateMatches(Condition.PRESENT);
    }

    public void fillNewUserForm(String userName, String password, String userEmail) {
        LOGGER.info("Filling the form.....");
        username.type(userName);
        firstName.type(userName);
        lastName.type(userName);
        email.type(userEmail);
        this.password.type(password);
        repeatPassword.type(password);
    }

    public void clickReadOnlyUserCheckBox() {
        readOnlyUserCheckBox.click();
    }

    public void typeFirstName(String firstName) {
        this.firstName.sendKeys(firstName, true, false);
    }

    public void typeLastName(String lastName) {
        this.lastName.sendKeys(lastName, true, false);
    }

    public void typeEmail(String email) {
        this.email.type(email);
    }
}