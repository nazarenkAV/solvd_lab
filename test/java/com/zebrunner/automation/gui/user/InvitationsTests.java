package com.zebrunner.automation.gui.user;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.util.EmailManager;
import com.zebrunner.automation.gui.iam.user.UserCard;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.carina.utils.R;

@Slf4j
@Maintainer("obabich")
public class InvitationsTests extends LogInBase {

    private final EmailManager emailManager = EmailManager.primaryInstance;

    private String userName;

    @AfterMethod(alwaysRun = true)
    public void deleteCreatedUser() {
        userService.getUserId(userName)
                   .ifPresent(userId -> userService.deleteUserById(userId));
    }

    // =========================================== Test ==============================================

    @Test(priority = 5)
    @TestCaseKey({"ZTP-1515", "ZTP-1545", "ZTP-1557", "ZTP-5974"})
    public void loginToZebrunnerViaEmailInvitationLink() {
        WebDriver webDriver = super.getDriver();
        R.CONFIG.put("capabilities.zebrunner:options.idleTimeout[String]", String.valueOf(240), true);

        User validUser = this.prepareUserForInvitation();
        userName = StringUtil.getTemporaryUsernameFromEmail(validUser.getEmail());
        UsersPageR usersPage = UsersPageR.openPageDirectly(webDriver)
                                         .openInviteUserModal()
                                         .fillInviteUserForm(validUser);

        super.pause(4);

        usersPage.searchUser(userName);
        super.pause(3);

        UserCard userCard = usersPage.getUserCard(userName);

        Assert.assertEquals(userCard.getStatus(), "Pending", "Status should be Pending !");

        String invitationLink = emailManager.pollWorkspaceInvitationLink();

        UsersPageR.openPageDirectly(webDriver);
        String loginPageDriverName = "login-page-driver";
        LoginPage loginPage = LoginPage.openPageByUrl(super.getDriver(loginPageDriverName), invitationLink);
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened");

        loginPage.loginFirstTime(LogInBase.notProjectMember.getUsername(), validUser.getPassword());

        Assert.assertEquals(
                loginPage.getSignUpErrorMessage(),
                "User with the provided username or email already exists.",
                "Username or email already exists: Message is not as expected!"
        );

        loginPage.loginFirstTime(validUser.getUsername(), validUser.getPassword());
        Assert.assertTrue(
                new AutomationLaunchesPage(super.getDriver(loginPageDriverName)).getHeader().isUIObjectPresent(),
                "Header should be visible after login!"
        );

        usersPage = UsersPageR.openPageDirectly(webDriver);
        userName = validUser.getUsername();
        usersPage.searchUser(userName);
        Assert.assertTrue(usersPage.isUserPresentInListOfUsers(userName), "User should present in list !");

        userCard = usersPage.getUserCard(userName);
        Assert.assertEquals(userCard.getStatus(), "Active", "Status should be active !");

        super.quitDriver(loginPageDriverName);
    }

    @SneakyThrows
    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE, priority = 5)
    @TestCaseKey("ZTP-4711")
    public void loginToZebrunnerViaInvitationWithDotInUsername() {
        User validUser = this.prepareUserForInvitation();
        validUser.setUsername(validUser.getUsername() + "." + validUser.getUsername());
        userName = StringUtil.getTemporaryUsernameFromEmail(validUser.getEmail());

        UsersPageR.openPageDirectly(getDriver())
                  .openInviteUserModal()
                  .fillInviteUserForm(validUser);

        String inviteLink = emailManager.pollWorkspaceInvitationLink();
        UsersPageR.openPageDirectly(getDriver());

        String loginPageDriverName = "login-page-driver";
        LoginPage loginPage = LoginPage.openPageByUrl(getDriver(loginPageDriverName), inviteLink);
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened");

        loginPage.loginFirstTime(validUser.getUsername(), validUser.getPassword());
        Assert.assertTrue(new AutomationLaunchesPage(getDriver(loginPageDriverName)).getHeader()
                                                                                    .isUIObjectPresent(), "Header should be visible after login!");
        userName = validUser.getUsername();

        super.quitDriver(loginPageDriverName);
    }

    @TestCaseKey("ZTP-1549")
    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void userIsAbleToResendAnInvitationTest() {
        WebDriver webDriver = super.getDriver();

        User validUser = this.prepareUserForInvitation();
        userName = StringUtil.getTemporaryUsernameFromEmail(validUser.getEmail());

        UsersPageR usersPage = UsersPageR.openPageDirectly(webDriver)
                                         .openInviteUserModal()
                                         .fillInviteUserForm(validUser);
        Assert.assertEquals(
                usersPage.getPopUp(),
                "Invitations were successfully sent",
                "Invitation popup message is not as expected or is not appeared!"
        );

        String inviteLink = emailManager.pollWorkspaceInvitationLink();
        String firstInvitationLinkToken = StringUtil.extractToken(inviteLink);

        usersPage.searchUser(userName);

        super.pause(3);

        UserCard userCard = usersPage.getUserCard(userName);
        userCard.openUserCardMenu().resentInvitation();
        Assert.assertEquals(
                usersPage.getPopUp(),
                "Invitation was successfully resent",
                "Invitation popup message is not as expected or is not appeared!"
        );

        inviteLink = emailManager.pollWorkspaceInvitationLink();
        String secondInvitationLinkToken = StringUtil.extractToken(inviteLink);
        Assert.assertEquals(firstInvitationLinkToken, secondInvitationLinkToken, "Tokens mismatch !");
    }

    @Test
    @SneakyThrows
    @TestCaseKey("ZTP-1551")
    public void userCanSendInviteToSameEmailTest() {
        WebDriver webDriver = super.getDriver();

        User validUser = this.prepareUserForInvitation();
        userName = StringUtil.getTemporaryUsernameFromEmail(validUser.getEmail());

        UsersPageR usersPage = UsersPageR.openPageDirectly(webDriver)
                                         .openInviteUserModal()
                                         .fillInviteUserForm(validUser);

        Assert.assertEquals(
                usersPage.getPopUp(),
                "Invitations were successfully sent",
                "Invitation popup message is not as expected or is not appeared!"
        );
        emailManager.waitUntilEmailDelivered("Join the workspace");

        usersPage.openInviteUserModal().fillInviteUserForm(validUser);

        Assert.assertEquals(
                usersPage.getPopUp(),
                "Invitations were successfully sent",
                "Invitation popup message is not as expected or is not appeared after resent invitation !"
        );
        emailManager.waitUntilEmailDelivered("Join the workspace");
    }

    @Test
    @SneakyThrows
    @TestCaseKey("ZTP-5973")
    public void verifyUserIsNotAbleToLoginViaInvitationIfUserWasDeactivated() {
        WebDriver webDriver = super.getDriver();

        User validUser = this.prepareUserForInvitation();
        userName = StringUtil.getTemporaryUsernameFromEmail(validUser.getEmail());

        UsersPageR usersPage = UsersPageR.openPageDirectly(webDriver)
                                         .openInviteUserModal()
                                         .fillInviteUserForm(validUser);

        Assert.assertEquals(
                usersPage.getPopUp(), "Invitations were successfully sent",
                "Invitation popup message is not as expected or is not appeared!"
        );

        usersPage.searchUser(userName);
        Assert.assertTrue(usersPage.isUserPresentInListOfUsers(userName), "User should present in list !");

        UserCard userCard = usersPage.getUserCard(userName);

        userCard.deactivateUser();

        Assert.assertEquals(
                usersPage.getPopUp(), "User '" + userName + "' was deactivated",
                "Pop up about user deactivate is not as excepted !"
        );

        String inviteLink = emailManager.pollWorkspaceInvitationLink();
        UsersPageR.openPageDirectly(webDriver);

        String loginPageDriverName = "login-page-driver";
        LoginPage loginPage = LoginPage.openPageByUrl(getDriver(loginPageDriverName), inviteLink);
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened");

        loginPage.loginFirstTime(validUser.getUsername(), validUser.getPassword());

        Assert.assertEquals(
                loginPage.getSignUpErrorMessage(), "User is not active.",
                "'User is not active' Message is not as expected!"
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private User prepareUserForInvitation() {
        String emailAccountUsername = ConfigHelper.getEmailAccountProperties().getUsername();

        String[] emailParts = emailAccountUsername.split("@");
        String randomSeed = RandomStringUtils.randomNumeric(5);

        String username = emailParts[0] + randomSeed;
        String email = emailParts[0] + "+" + randomSeed + "@" + emailParts[1];

        return new User().setUsername(username)
                         .setPassword(RandomStringUtils.randomAlphabetic(12))
                         .setEmail(email)
                         .setZebrunnerRole("Users");
    }

}