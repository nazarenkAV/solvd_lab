package com.zebrunner.automation.gui.iam;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class PasswordForgotPage extends LoginPage {

    @FindBy(id = "email")
    private Element emailField;

    @FindBy(id = "password")
    private Element passwordField;
    @FindBy(xpath = "//button[text()='Send']")
    private Element sendButton;

    @FindBy(xpath = "//button[text()='Apply']")
    private Element applyButton;

    @FindBy(xpath = "//div[@class='email-sent']/p")
    private Element resetPasswordMessage;

    @FindBy(id = "passwordConfirmation")
    private Element confirmPwd;

    @FindBy(xpath = "//span[@class='input-message-animation item-enter-done']")
    private Element confirmPwdError;

    @FindBy(id = "passwordConfirmation")
    private ExtendedWebElement uiLoadMarker;

    public PasswordForgotPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadMarker);
    }

    public void typeEmail(String email) {
        WaitUtil.waitComponentByCondition(emailField, inputField -> inputField.isStateMatches(Condition.VISIBLE));
        emailField.sendKeys(email);
    }

    public void sendClick() {
        sendButton.click();
    }

    public String getResetMessage() {
        return resetPasswordMessage.getText();
    }

    public void typeNewPassword(String email) {
        passwordField.sendKeys(email);
    }

    public void typeConfirmPassword(String email) {
        pause(1);
        confirmPwd.sendKeys(email);
    }

    public LoginPage apply() {
        applyButton.click();
        return new LoginPage(getDriver());
    }

    public boolean isSendButtonActive() {
        return sendButton.isStateMatches(Condition.CLICKABLE);
    }

    public String getConfirmPwdFieldError() {
        return confirmPwdError.getText();
    }

}
