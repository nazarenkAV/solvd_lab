package com.zebrunner.automation.gui.user;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.iam.service.IAMService;
import com.zebrunner.automation.api.iam.service.IAMServiceImpl;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.EmailManager;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.util.LocalStorageManager;
import com.zebrunner.automation.gui.iam.user.UserCard;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;

public class ReadOnlyUsersTest extends LogInBase {

    private final IAMService iamService = new IAMServiceImpl();
    private final EmailManager emailManager = EmailManager.primaryInstance;

    private String username;

    @BeforeMethod
    public void setLocalStorage() {
        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(UsersEnum.MAIN_ADMIN.getUser()));
    }

    @AfterMethod(alwaysRun = true)
    public void deleteCreatedUser() {
        userService.getUserId(username)
                   .ifPresent(userService::deleteUserById);
    }

    // =========================================== Test ==============================================

    @Test
    @TestCaseKey({"ZTP-5963", "ZTP-5971", "ZTP-5986"})
    public void createReadOnlyUserAndVerifyTheSeatIsNotOccupiedAfterCreationTest() {
        SoftAssert softAssert = new SoftAssert();

        String defaultProjectKey = "DEF";

        User user = userService.generateRandomUser();
        username = user.getUsername();

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        int initialSeats = usersPage.getSeatsInUse();

        usersPage.createUser(username, user.getPassword(), user.getEmail(), true);

        softAssert.assertEquals(usersPage.getSeatsInUse(), initialSeats, "Seats should be equal !");
        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_CREATED.getDescription(username),
                "Pop up about user creation is not as excepted !");

        usersPage.searchUser(username);
        softAssert.assertTrue(usersPage.isUserPresentInListOfUsers(username), "User should present in list !");

        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(user));

        Assert.assertTrue(new AutomationLaunchesPage(getDriver()).getHeader().isUIObjectPresent(),
                "Header should be visible after login!");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), defaultProjectKey);

        softAssert.assertFalse(testCasesPage.isCreateCaseButtonClickable(), "Create case button should be disable !");
        softAssert.assertFalse(testCasesPage.isCreateSuiteButtonClickable(), "Create suite button should be disable !");

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), defaultProjectKey);

        softAssert.assertFalse(automationLaunchesPage.isLauncherButtonVisible(), "Launcher button shouldn't be visible !");

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        Assert.assertFalse(projectsPage.isNewProjectButtonClickable(), "New project button should be disabled !");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5964")
    public void inviteReadOnlyUserTest() {
        WebDriver webDriver = super.getDriver();

        User validUser = this.prepareUserForInvitation();
        username = StringUtil.getTemporaryUsernameFromEmail(validUser.getEmail());

        UsersPageR.openPageDirectly(webDriver)
                  .openInviteUserModal()
                  .fillInviteUserFormForReadOnlyUser(validUser);

        String inviteLink = emailManager.pollWorkspaceInvitationLink();
        UsersPageR.openPageDirectly(webDriver);

        LocalStorageManager localStorageManager = new LocalStorageManager(webDriver);
        localStorageManager.clear();

        LoginPage loginPage = LoginPage.openPageByUrl(webDriver, inviteLink);
        Assert.assertTrue(loginPage.isPageOpened(), "Login page was not opened");

        loginPage.loginFirstTime(validUser.getUsername(), validUser.getPassword());
        Assert.assertTrue(
                new AutomationLaunchesPage(webDriver).getHeader().isUIObjectPresent(),
                "Header should be visible after login!"
        );

        username = validUser.getUsername();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);
        Assert.assertFalse(projectsPage.isNewProjectButtonClickable(), "New project button should be disabled !");
    }

    @Test
    @TestCaseKey({"ZTP-5967", "ZTP-5969"})
    public void verifyUserCanBeConvertedFromReadOnlyUserToRegularUserAndViceVersa() {
        SoftAssert softAssert = new SoftAssert();

        User user = userService.generateRandomUser();
        username = user.getUsername();

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        usersPage.createUser(username, user.getPassword(), user.getEmail(), true);

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_CREATED.getDescription(username),
                "Pop up about user creation is not as excepted !");

        usersPage.searchUser(username);
        softAssert.assertTrue(usersPage.isUserPresentInListOfUsers(username), "User should present in list !");

        UserCard userCard = usersPage.getUserCard(username);

        softAssert.assertTrue(userCard.isReadOnlyUserLabelPresent(), "Read only user label should be present after user creation!");

        userCard.openUserCardMenu().clickConvertToRegularUserButton().confirm();

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_UPDATED.getDescription(username),
                "Pop up about user update is not as excepted !");
        softAssert.assertFalse(userCard.isReadOnlyUserLabelPresent(), "Read only user label shouldn't be present !");

        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());
        localStorageManager.clear();

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(user));

        Assert.assertTrue(new AutomationLaunchesPage(getDriver()).getHeader().isUIObjectPresent(),
                "Header should be visible after login!");

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        Assert.assertTrue(projectsPage.isNewProjectButtonClickable(), "New project button shouldn't be disabled !");

        localStorageManager.clear();

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(UsersEnum.MAIN_ADMIN.getUser()));

        UsersPageR.openPageDirectly(getDriver());

        usersPage.searchUser(username);

        userCard = usersPage.getUserCard(username);
        userCard.openUserCardMenu()
                .clickConvertToReadOnlyUserButton().confirm();

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_UPDATED.getDescription(username),
                "Pop up about user update is not as excepted !");
        softAssert.assertTrue(userCard.isReadOnlyUserLabelPresent(), "Read only user label should be present !");

        localStorageManager.clear();

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(user));

        projectsPage = ProjectsPage.openPageDirectly(getDriver());

        Assert.assertFalse(projectsPage.isNewProjectButtonClickable(), "New project button should be disabled !");

        softAssert.assertAll();
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