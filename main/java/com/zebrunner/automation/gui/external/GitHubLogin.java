package com.zebrunner.automation.gui.external;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zebrunner.automation.util.EmailManager;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;

public class GitHubLogin extends AbstractPage {

    @FindBy(id = "login_field")
    private ExtendedWebElement usernameOrEmailInput;
    @FindBy(id = "password")
    private ExtendedWebElement passwordInput;
    @FindBy(xpath = "//input[@name='commit']")
    private ExtendedWebElement signInButton;
    @FindBy(id = "otp")
    private ExtendedWebElement deviceVerificationCodeInput;

    public GitHubLogin(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(usernameOrEmailInput);
    }

    public static GitHubLogin openPage(WebDriver driver) {
        GitHubLogin gitHubLogin = new GitHubLogin(driver);

        gitHubLogin.pause(2);

        return gitHubLogin;
    }

    public AddRepoPage signIn(String login, String password) {
        super.pause(3);

        usernameOrEmailInput.type(login);
        passwordInput.type(password);
        signInButton.click();
        signInButton.waitUntilElementDisappear(3);

        if (deviceVerificationCodeInput.isElementPresent(3)) {
            String gitHubCode = EmailManager.primaryInstance.pollGitHubCode();

            deviceVerificationCodeInput.type(gitHubCode);
        }

        super.waitUntil(ExpectedConditions.urlMatches(AddRepoPage.PAGE_URL_MATCHER), 15);
        super.pause(2);

        return new AddRepoPage(super.getDriver());
    }

}
