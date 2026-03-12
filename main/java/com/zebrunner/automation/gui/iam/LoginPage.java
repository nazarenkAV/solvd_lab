package com.zebrunner.automation.gui.iam;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.PopUp;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;

public class LoginPage extends AbstractPage {

    public static final String PAGE_NAME = "Sign In";

    @FindBy(id = "email")
    private Element emailField;

    @FindBy(id = "accessKey")
    private Element accessKeyField;
    @FindBy(id = "username")
    private Element usernameField; // when login first time

    @FindBy(id = "password")
    private Element passwordField;

    @FindBy(id = "Repeat password")
    private Element repeatPasswordField;

    @FindBy(xpath = "//button[text()='Start']")
    private Element startButton;

    @FindBy(xpath = "//button[text()='Login']")
    private Element loginButton;

    @FindBy(xpath = "//div[contains(@class,'ZbrAlert warning')]")
    private Element errorMessage;

    @FindBy(xpath = "//*[contains(@class, 'ZbrAlert error')]/p")
    private Element signUpErrorMessage;

    @FindBy(xpath = PopUp.POPUP_XPATH)
    private PopUp popUp;

    @FindBy(xpath = "//button[@class='md-action md-button ng-scope md-ink-ripple']")
    private ExtendedWebElement cancelPopup;

    @FindBy(xpath = "//*[text()='Forgot password?']")
    private ExtendedWebElement resetPassword;

    @FindBy(xpath = "//div[contains(@ng-messages,'password')]//span")
    private Element passwordError;

    @FindBy(xpath = "//div[contains(@ng-messages,'username')]//span")
    private Element usernameError;

    @FindBy(id = "password")
    private ExtendedWebElement uiLoadedMarker;

    @FindBy(xpath = "//a[contains(., 'Privacy Policy')]")
    private Element privacyAndPolicyButton;

    @FindBy(xpath = "//a[contains(., 'Terms of Service')]")
    private Element termsOfServiceButton;

    public LoginPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
        setPageURL("/signin");
    }

    public static LoginPage openPageDirectly(WebDriver driver) {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();

        return loginPage;
    }

    public static LoginPage openPage(WebDriver driver) {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.pause(2);

        return loginPage;
    }

    public static LoginPage openPageByUrl(WebDriver driver, String url) {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.openURL(url.trim());
        loginPage.pause(4);

        return loginPage;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getUsername() {
        super.waitUntil(ExpectedConditions.visibilityOf(accessKeyField.getElement()), 2);

        return accessKeyField.getAttributeValue("value");
    }

    public String getEmail() {
        return emailField.getAttributeValue("value");
    }

    public String getPassword() {
        super.waitUntil(ExpectedConditions.visibilityOf(passwordField.getElement()), 2);

        return passwordField.getAttributeValue("value");
    }

    public String getErrorMessageContent() {
        if (errorMessage.isStateMatches(Condition.VISIBLE)) {
            return errorMessage.getText();
        } else {
            return "No error message appears or wrong xpath for this element!";
        }
    }

    public String getSignUpErrorMessage() {
        if (signUpErrorMessage.isStateMatches(Condition.VISIBLE)) {
            return signUpErrorMessage.getText();
        } else {
            return "No error message appears or wrong xpath for this element!";
        }
    }

    public boolean isSubmitButtonActive() {
        return loginButton.isStateMatches(Condition.CLICKABLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loginFirstTime(String username, String password) {
        this.assertPageOpened();

        this.inputUsername(username);
        this.inputPassword(password);
        this.inputRepeatPassword(password);

        startButton.click();
        startButton.isDisappear();
    }

    @Deprecated
    public void login(User user) {
        this.inputUsernameOrEmail(user.getUsername());
        this.inputPassword(user.getPassword());

        loginButton.click();
    }

    public ProjectsPage login(String username, String password) {
        WaitUtil.waitComponentByCondition(usernameField, inputField -> inputField.isStateMatches(Condition.VISIBLE_AND_CLICKABLE));

        this.inputUsernameOrEmail(username);
        this.inputPassword(password);

        loginButton.click();

        return new ProjectsPage(super.getDriver());
    }

    public PrivacyPolicyPage clickPrivacyPolicyButton() {
        privacyAndPolicyButton.click();

        return new PrivacyPolicyPage(getDriver());
    }

    public TermsOfServicePage clickTermsOfServiceButton() {
        termsOfServiceButton.click();

        return new TermsOfServicePage(getDriver());
    }

    public void cancelPopupMessage() {
        cancelPopup.clickIfPresent(3);
    }

    public void inputUsernameOrEmail(String username) {
        accessKeyField.sendKeys(username);
    }

    public void inputUsername(String username) {
        usernameField.sendKeys(username);
    }

    public void inputPassword(String password) {
        passwordField.sendSecretKeys(password);
    }

    public void inputRepeatPassword(String password) {
        repeatPasswordField.sendSecretKeys(password);
    }

}
