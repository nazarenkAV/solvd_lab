package com.zebrunner.automation.gui.smoke;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.iam.PrivacyPolicyPage;
import com.zebrunner.automation.gui.iam.TermsOfServicePage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.carina.core.IAbstractTest;
import com.zebrunner.carina.utils.R;

import static com.zebrunner.automation.util.PageUtil.switchToWindow;

public class LoginFormTest implements IAbstractTest {

    private final String userUsername = ConfigHelper.getUserProperties().getTestUser().getUsername();
    private final String userPassword = ConfigHelper.getUserProperties().getTestUser().getPassword();

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE, priority = 99)
    public void userIsAbleTo0penPrivacyPolicyAndTermsOfServicePagesTest() {
        R.CONFIG.put("explicit_timeout", String.valueOf(90), true);
        R.CONFIG.put("capabilities.pageLoadStrategy", "eager", true);

        LoginPage loginPage = LoginPage.openPageDirectly(getDriver());
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");

        PrivacyPolicyPage privacyPolicyPage = loginPage.clickPrivacyPolicyButton();
        switchToWindow(privacyPolicyPage.getDriver(), privacyPolicyPage.getExpectedPageTitle());
        Assert.assertTrue(privacyPolicyPage.isPageOpened(), "Privacy Policy page was not opened!");

        loginPage = LoginPage.openPageDirectly(getDriver());
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");

        TermsOfServicePage termsOfServicePage = loginPage.clickTermsOfServiceButton();
        switchToWindow(privacyPolicyPage.getDriver(), termsOfServicePage.getExpectedPageTitle());
        Assert.assertTrue(termsOfServicePage.isPageOpened(), "Terms Of Service page was not opened!");
    }

    @TestCaseKey("ZTP-698")
    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void verifyUsernameAndPasswordCaseSensitive() {
        String validLogin = userUsername;
        String validPassword = userPassword;

        LoginPage loginPage = LoginPage.openPageDirectly(getDriver());
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");

        loginPage.login(validLogin.toUpperCase(), validPassword);
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(validLogin.toUpperCase()), "Email was correct: Message is not as expected!");
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");

        loginPage = LoginPage.openPageDirectly(getDriver());
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");

        loginPage.login(validLogin, validPassword.toUpperCase());
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(), "Password was correct: Message is not as expected!");
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");
    }

    @TestCaseKey("ZTP-697")
    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void userIsNotAbleToSignIn_Invalid_Input_Test() {
        String validLogin = userUsername;
        String validPassword = userPassword;
        LoginPage loginPage = LoginPage.openPageDirectly(getDriver());

        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");
        Assert.assertFalse(loginPage.isSubmitButtonActive(), "Submit button should be inactive because of empty input fields");

        loginPage.login(validLogin, validPassword + "?");
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(), "Message is not as expected!");

        loginPage = LoginPage.openPageDirectly(getDriver());
        loginPage.login(validLogin + "?", validPassword);
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(validLogin + "?"), "Message is not as expected!");

        loginPage = LoginPage.openPageDirectly(getDriver());
        loginPage.login(validLogin + "?", validPassword + "?");
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(validLogin + "?"), "Message is not as expected!");

        loginPage = LoginPage.openPageDirectly(getDriver());
        loginPage.login("<>???", validPassword);
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription("<>???"), "Message is not as expected!");
        loginPage.cancelPopupMessage();

        loginPage.inputUsernameOrEmail(validLogin);
        loginPage.inputPassword(validPassword);
        Assert.assertEquals(loginPage.getUsername(), validLogin, "Login is not as expected");
        Assert.assertEquals(loginPage.getPassword(), validPassword, "Password is not as expected");

        loginPage.refresh(1);
        this.pause(4);

        Assert.assertEquals(loginPage.getUsername(), "", "Login is not as expected");
        Assert.assertEquals(loginPage.getPassword(), "", "Password is not as expected");
        Assert.assertFalse(loginPage.isSubmitButtonActive(), " Submit button should be inactive");
        Assert.assertTrue(loginPage.isPageOpened(), " Login page is not opened!");
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-696", "ZTP-688", "ZTP-699"})
    public void userIsNotAbleToSignInWithEmptyFieldsAndChangedLetterCaseCredsTest() {
        String validLogin = userUsername;
        String validPassword = userPassword;
        LoginPage loginPage = LoginPage.openPageDirectly(getDriver());

        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened!");
        Assert.assertFalse(loginPage.isSubmitButtonActive(), "Submit button should be inactive because of empty input fields");

        loginPage = LoginPage.openPageDirectly(getDriver());
        loginPage.login(validLogin.toUpperCase(Locale.ROOT), validPassword);
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(validLogin.toUpperCase(Locale.ROOT)), "Message is not as expected!");

        loginPage.login(validLogin, validPassword.toUpperCase(Locale.ROOT));
        Assert.assertEquals(loginPage.getErrorMessageContent(), MessageEnum.WE_NOT_RECOGNIZE_CREDS.getDescription(validLogin.toUpperCase(Locale.ROOT)), "Message is not as expected!");

        loginPage.inputUsernameOrEmail("");
        loginPage.inputPassword("");
        Assert.assertFalse(loginPage.isSubmitButtonActive(), "Submit button should be because username and password empty!");//ZTP-699

        loginPage.inputUsernameOrEmail("");
        loginPage.inputPassword(validPassword);
        Assert.assertFalse(loginPage.isSubmitButtonActive(), "Submit button should be because username is empty!");//ZTP-699

        loginPage.inputUsernameOrEmail(validLogin);
        loginPage.inputPassword("");
        Assert.assertFalse(loginPage.isSubmitButtonActive(), "Submit button should be because password is empty!");//ZTP-699

        loginPage.login(validLogin, validPassword);//ZTP-688
        ProjectsPage projectsPage = new ProjectsPage(getDriver());
        Assert.assertTrue(projectsPage.getHeader().isUIObjectPresent(), "Tenant header should be present after login!");
        projectsPage.getHeader().logout();
    }

}
