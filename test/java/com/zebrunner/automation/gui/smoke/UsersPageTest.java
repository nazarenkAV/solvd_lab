package com.zebrunner.automation.gui.smoke;

import lombok.extern.slf4j.Slf4j;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.iam.method.v1.PostGenerateAuthTokenMethodIAM;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.automation.gui.iam.user.ChangePasswordModal;
import com.zebrunner.automation.gui.iam.user.UserCard;
import com.zebrunner.automation.gui.iam.user.UserProcessModal;
import com.zebrunner.automation.gui.iam.user.UserStatusConfirmationModal;
import com.zebrunner.automation.gui.integration.SettingsPageR;
import com.zebrunner.automation.legacy.MessageEnum;

import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Slf4j
public class UsersPageTest extends LogInBase {

    private String username;

    @AfterMethod(onlyForGroups = "user-was-created", alwaysRun = true)
    public void deleteCreatedUser() {
        userService.getUserId(username)
                .ifPresent(userId -> userService.deleteUserById(userId));
    }


    // ================================== Test =========================================

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1533")
    public void _mainElementsPresentTest() {
        SoftAssert softAssert = new SoftAssert();

        SettingsPageR settingsPage = SettingsPageR.openPageDirectly(getDriver());

        UsersPageR usersPage = settingsPage.toUsersPage();
        Assert.assertTrue(usersPage.isPageOpened(), "Users page should be opened !");

        softAssert.assertEquals(usersPage.getTitle(), UsersPageR.PAGE_NAME);
        softAssert.assertTrue(usersPage.isSearchButtonClickable(), "Can't find search button!");
        softAssert.assertTrue(usersPage.isCreateUserButtonClickable(), "User from admin group should see button '+ USER'");
        softAssert.assertTrue(usersPage.isInviteUserButtonClickable(), "Cannot find invite user button!");
        softAssert.assertAll();
    }

    @Test(groups = "user-was-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-1516", "ZTP-1517"})
    public void verifyCreatedUserPresentOnListAndCanBeSearched() {
        SoftAssert softAssert = new SoftAssert();

        username = RandomStringUtils.randomAlphabetic(8);
        String password = RandomStringUtils.randomAlphabetic(8);
        String userEmail = RandomStringUtils.randomAlphabetic(9) + "@gmail.com";

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        UserProcessModal newUserModal = usersPage.clickNewUserButton();

        softAssert.assertEquals(newUserModal.getHeader().getModalTitle().getText(), UserProcessModal.NEW_USER_MODAL_TITLE,
                "New user modal is not opened !");

        newUserModal.fillNewUserForm(username, password, userEmail);
        newUserModal.submitModal();

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_CREATED.getDescription(username),
                "Pop up about user creation is not as excepted !");

        //"ZTP-1517"
        usersPage.searchUser(username);
        softAssert.assertTrue(usersPage.isUserPresentInListOfUsers(username), "User should present in list !");

        softAssert.assertAll();
    }

    @Test(groups = "user-was-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-1524", "ZTP-1526"})
    public void verifyUserIsAbleEditUserData() {
        SoftAssert softAssert = new SoftAssert();

        User user = userService.generateRandomUser();
        username = user.getUsername();
        user = userService.create(user);

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        usersPage.searchUser(user.getUsername());
        softAssert.assertTrue(usersPage.isUserPresentInListOfUsers(username), "User should present in list !");

        UserCard userCard = usersPage.getUserCard(user.getUsername());
        UserProcessModal editUserModal = userCard.openUserCardMenu().openEditUserModal();

        softAssert.assertEquals(editUserModal.getHeader().getModalTitle().getText(), UserProcessModal.EDIT_USER_MODAL_TITLE,
                "Edit user modal is not opened !");

        final String firstName = RandomStringUtils.randomAlphabetic(5);
        final String lastName = RandomStringUtils.randomAlphabetic(5);
        final String fullName = firstName + " " + lastName;
        user.setFirstName(firstName);
        user.setLastName(lastName);

        editUserModal.typeFirstName(firstName);
        editUserModal.typeLastName(lastName);
        editUserModal.submitModal();

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_UPDATED.getDescription(username),
                "Pop up about user update is not as excepted !");

        String actualUserFullName = userCard.getUserFullName();

        //ZTP-1524
        softAssert.assertEquals(actualUserFullName, fullName, "User full name is not as excepted !");

        usersPage.waitPopupDisappears();

        ChangePasswordModal changePasswordModal = userCard.openUserCardMenu().openChangePasswordModal();
        final String newPassword = RandomStringUtils.randomAlphabetic(8);
        user.setPassword(newPassword);
        changePasswordModal.typePassword(newPassword);
        changePasswordModal.save();

        //ZTP-1526
        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_PASSWORD_WAS_UPDATED.getDescription(username),
                "Pop up about password update is not as excepted !");

        PostGenerateAuthTokenMethodIAM login = new PostGenerateAuthTokenMethodIAM(user);
        login.getRequest().expect().statusCode(HttpStatus.SC_OK);
        login.callAPI();

        softAssert.assertAll();
    }

    @Test(groups = "user-was-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1525")
    public void verifyUserIsAbleDeactivateUser() {
        SoftAssert softAssert = new SoftAssert();

        User user = userService.generateRandomUser();
        username = user.getUsername();
        userService.create(user);

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        usersPage.searchUser(user.getUsername());
        softAssert.assertTrue(usersPage.isUserPresentInListOfUsers(username), "User should present in list !");

        UserCard userCard = usersPage.getUserCard(user.getUsername());
        UserStatusConfirmationModal deactivateStatusModal = userCard.openUserCardMenu().clickDeactivateButton();

        softAssert.assertEquals(deactivateStatusModal.getHeader().getText(), UserStatusConfirmationModal.DEACTIVATE_USER_MODAL_TITLE,
                "Deactivate user modal is not opened !");

        deactivateStatusModal.deactivate();

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_SUCCESSFULLY_DEACTIVATED.getDescription(username),
                "Pop up about user deactivate is not as excepted !");
        softAssert.assertEquals(userCard.getStatus(), "Inactive", "User status is not as excepted !");

        PostGenerateAuthTokenMethodIAM login = new PostGenerateAuthTokenMethodIAM(user);
        login.getRequest().expect().statusCode(HttpStatus.SC_UNAUTHORIZED);
        String rs = login.callAPI().asString();

        Assert.assertEquals(JsonPath.from(rs).getString("code"), "IAM-3102", "Message code is not as expected!");

        softAssert.assertAll();
    }

    @Test(groups = "user-was-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1530")
    public void verifyUserIsNotAbleToCreateAlreadyExistingUser() {
        SoftAssert softAssert = new SoftAssert();

        username = RandomStringUtils.randomAlphabetic(8);
        String password = RandomStringUtils.randomAlphabetic(8);
        String userEmail = RandomStringUtils.randomAlphabetic(9) + "@gmail.com";

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        usersPage.createUser(username, password, userEmail, false);

        UserProcessModal newUserModal = usersPage.clickNewUserButton();
        newUserModal.fillNewUserForm(username, password, userEmail);
        newUserModal.submitModal();

        softAssert.assertEquals(usersPage.getPopUp(), MessageEnum.USER_ALREADY_EXISTS.getDescription(),
                "Popup message is not as excepted !");

        newUserModal.clickCancel();

        usersPage.searchUser(username);

        pause(4);

        softAssert.assertEquals(usersPage.getUserCards().size(), 1, "Only one user with this username should be in list !");
        softAssert.assertAll();
    }

    @Test(groups = "user-was-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-4366")
    public void verifyQuantityOfSeatsAfterUserCreationAndUserDeactivation() {
        SoftAssert softAssert = new SoftAssert();

        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        int initialSeats = usersPage.getSeatsInUse();

        User user = userService.generateRandomUser();
        username = user.getUsername();
        userService.create(user);

        UsersPageR.openPageDirectly(getDriver());

        softAssert.assertEquals(usersPage.getSeatsInUse(), initialSeats + 1, "Seats is not as excepted after adding active user !");

        usersPage.searchUser(user.getUsername());

        UserCard userCard = usersPage.getUserCard(user.getUsername());

        initialSeats = usersPage.getSeatsInUse();

        UserStatusConfirmationModal deactivateStatusModal = userCard.openUserCardMenu().clickDeactivateButton();

        softAssert.assertEquals(deactivateStatusModal.getHeader().getTitleText(), UserStatusConfirmationModal.DEACTIVATE_USER_MODAL_TITLE,
                "Deactivate user modal is not opened !");

        deactivateStatusModal.deactivate();
        pause(2);

        softAssert.assertEquals(usersPage.getSeatsInUse(), initialSeats - 1, "Seats is not as excepted after deactivate user !");

        initialSeats = usersPage.getSeatsInUse();

        UserStatusConfirmationModal activateStatusModal = userCard.openUserCardMenu().clickActivateButton();

        softAssert.assertEquals(activateStatusModal.getHeader().getTitleText(), UserStatusConfirmationModal.ACTIVATE_USER_MODAL_TITLE,
                "Activate user modal is not opened !");

        activateStatusModal.activate();
        pause(2);

        softAssert.assertEquals(usersPage.getSeatsInUse(), initialSeats + 1, "Seats is not as excepted after activate user !");

        softAssert.assertAll();
    }
}