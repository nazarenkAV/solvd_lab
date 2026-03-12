package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.iam.service.IAMService;
import com.zebrunner.automation.api.iam.service.IAMServiceImpl;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.util.LocalStorageManager;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.iam.AddMemberModal;
import com.zebrunner.automation.gui.iam.MemberCard;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.common.UserInfoTooltip;

@Maintainer("obabich")
@Slf4j
public class AddMemberModalAndMembersPageTest extends LogInBase {

    private final IAMService iamService = new IAMServiceImpl();
    private final List<User> createdUsers = new ArrayList<>();
    private String projectKeyForMember;

    @BeforeMethod
    public void setLocalStorage() {
        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());
        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(UsersEnum.MAIN_ADMIN.getUser()));
    }


    @AfterMethod(alwaysRun = true)
    public void closeAnyPopupOrDropDownListAndDeleteUsers() {
        ComponentUtil.closeAnyMenuOrModal(getDriver());
        createdUsers.forEach(user -> userService.deleteUserById(user.getId()));
        createdUsers.clear();
    }

    @BeforeTest
    public void getProjectKey() {
        projectKeyForMember = LogInBase.project.getKey();
    }

    // ================================== Test =================================================

    @Test(groups = {"min_acceptance"})
    public void checkMainElementsOnMembersPageTest() {
        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        String expectedUsernameTitle = "MEMBER";
        String expectedRoleTitle = "ROLE";
        String expectedDataTitle = "ADDED";

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(membersPage.getTitle(), MembersPageR.PAGE_NAME, "Title of members page is not as expected");
        softAssert.assertTrue(membersPage.isSearchFieldPresent(), "Can't find search field");
        softAssert.assertTrue(membersPage.isAddMemberButtonActive(), "Can't find add member button");
        softAssert.assertEquals(membersPage.getUsernameTableTitle(), expectedUsernameTitle, "Table username title is not as expected");
        softAssert.assertEquals(membersPage.getRoleTableTitle(), expectedRoleTitle, "Table role title is not as expected");
        softAssert.assertEquals(membersPage.getAddingDate(), expectedDataTitle, "Table adding data title is not as expected");
        List<MemberCard> memberCards = membersPage.getMemberCards();
        PaginationR pagination = membersPage.getPagination();
        softAssert.assertEquals(String.valueOf(memberCards.size()), pagination.getNumberOfItemsOnThePage(),
                "Number of items differs to pagination");
        softAssert.assertTrue(membersPage.getHeader().isUIObjectPresent(), "There are no header on members page");
        softAssert.assertTrue(membersPage.getNavigationMenu()
                                         .isVisible(), "There are no navigation menu on members page");
        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-904")
    @Test(groups = {"min_acceptance"})
    public void addMemberModalMaimElementsPresentsTest() {
        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        AddMemberModal addMemberWindow = membersPage.openAddMemberModal();

        String expectedWindowTitle = "Add members";
        List<String> expectedRoles = RoleEnum.getNames();
        List<String> expectedRoleDescriptions = RoleEnum.getDescriptions();

        String modalTitle = addMemberWindow.getModalTitle().getText();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(
                modalTitle, expectedWindowTitle,
                "Modal title should be " + expectedWindowTitle + "but found " + modalTitle
        );
        softAssert.assertTrue(
                addMemberWindow.getClose().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Can't fin close button"
        );
        softAssert.assertFalse(
                addMemberWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of empty fields"
        );
        softAssert.assertTrue(
                addMemberWindow.getCancelButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Can't find cancel button"
        );
        softAssert.assertTrue(addMemberWindow.isRoleFieldPresent(), "Can't find role field");
        softAssert.assertTrue(addMemberWindow.isUsernameFieldPresent(), "Can't find username field");
        softAssert.assertEquals(addMemberWindow.getRoles(), expectedRoles, "Some roles differ expected");

        addMemberWindow.closePopup();
        softAssert.assertEquals(addMemberWindow.getRoleDescriptions(), expectedRoleDescriptions, "Some role descriptions differ expected!");

        addMemberWindow.closePopup();
        addMemberWindow.getClose().click();
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-900", "ZTP-901", "ZTP-902", "ZTP-903", "ZTP-905", "ZTP-906"})
    public void addMemberTest() {
        WebDriver webDriver = super.getDriver();

        String nonExistentName = RandomStringUtils.randomAlphabetic(6);
        String nonExistentEmail = RandomStringUtils.randomAlphabetic(6) + "@solvd.com";

        User user1 = userService.addRandomUserToTenant();
        createdUsers.add(user1);
        User user2 = userService.addRandomUserToTenant();
        createdUsers.add(user2);

        String existentUserName = user1.getUsername();

        MembersPageR membersPage = MembersPageR.openPageDirectly(webDriver, projectKeyForMember);
        AddMemberModal addMemberWindow = membersPage.openAddMemberModal();

        addMemberWindow.typeUsernameOrEmail(nonExistentName);
        Assert.assertFalse(
                addMemberWindow.isUsernamePresentInSuggestions(nonExistentName),
                nonExistentName + "\" were found., but it shouldn't exists"
        );

        addMemberWindow.closePopup();
        Assert.assertFalse(
                addMemberWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of empty fields"
        );

        addMemberWindow.typeUsernameOrEmail(nonExistentEmail);
        Assert.assertFalse(
                addMemberWindow.isUsernamePresentInSuggestions(nonExistentEmail),
                nonExistentEmail + "\" were found. But it shouldn't exists"
        );

        addMemberWindow.closePopup();
        Assert.assertFalse(
                addMemberWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of empty fields"
        );

        addMemberWindow.typeUsername(existentUserName);
        Assert.assertTrue(
                addMemberWindow.isUsernamePresentInSuggestions(existentUserName),
                "User with name " + existentUserName + " was not found!"
        );
        Assert.assertFalse(
                addMemberWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of empty fields"
        );

        addMemberWindow.getClose().click();
        membersPage.openAddMemberModal()
                   .fillMemberAndSubmitR(existentUserName, RoleEnum.GUEST.getName());
        Assert.assertTrue(
                membersPage.isMemberPresent(existentUserName),
                "User with name " + existentUserName + " was not found!!!"
        );

        addMemberWindow = membersPage.openAddMemberModal()
                                     .typeUsername(user2.getUsername())
                                     .chooseRoleByName(RoleEnum.MANAGER.getName());
        Assert.assertFalse(
                addMemberWindow.getAddButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Save button should be inactive because of empty fields"
        );
        Assert.assertTrue(
                addMemberWindow.getCancelButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Cancel button should be active because of empty fields"
        );

        addMemberWindow.getCancelButton().click();
        Assert.assertTrue(membersPage.isPageOpened(), "Members page was not opened!");
        Assert.assertFalse(
                addMemberWindow.isUsernamePresentInSuggestions(user2.getUsername()),
                "User with name " + user2.getUsername() + " was not found!!"
        );
    }

    @Test(groups = {"min_acceptance"})
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5753")
    public void verifyUserInfoTootipAtMembersPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);

        User user1 = userService.addRandomUserToTenant();
        createdUsers.add(user1);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        membersPage.openAddMemberModal().fillMemberAndSubmitR(user1.getUsername(), RoleEnum.ADMINISTRATOR.getName());

        UserInfoTooltip userInfoTootip = membersPage.getMemberByName(user1.getUsername()).hoverMemberName();
        userInfoTootip.verifyUserInfoTooltip(user1, true, RoleEnum.ADMINISTRATOR, "At Members Page!");
    }

    @Test(groups = {"min_acceptance"})
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5752")
    public void verifyUserInfoTootipAtProjectsPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);

        User user1 = userService.addRandomUserToTenant();
        createdUsers.add(user1);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        membersPage.openAddMemberModal().fillMemberAndSubmitR(user1.getUsername(), RoleEnum.ADMINISTRATOR.getName());

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.editCertainProjectByKey(projectKeyForMember)
                    .setLeadToTheProject(user1.getUsername(), false)
                    .getSubmitButton().click();

        UserInfoTooltip userInfoTootip = projectsPage.getProjectCardByProjectKey(projectKeyForMember).hoverLead();
        userInfoTootip.verifyUserInfoTooltip(user1, true, RoleEnum.ADMINISTRATOR, "At Projects Page!");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-5979", "ZTP-5978", "ZTP-5984"})
    public void verifyReadOnlyUsersCanBeAddedToProjectWithTheGuestRoleAndCantChangeTheRole() {
        WebDriver webDriver = super.getDriver();

        User readOnlyUser = userService.addRandomReadOnlyUserToTenant();
        createdUsers.add(readOnlyUser);

        MembersPageR membersPage = MembersPageR.openPageDirectly(webDriver, projectKeyForMember);
        AddMemberModal addMemberModal = membersPage.openAddMemberModal();

        addMemberModal.typeUsernameOrEmail(readOnlyUser.getUsername());

        Assert.assertTrue(
                addMemberModal.getUserSuggestionCard(readOnlyUser.getUsername())
                              .isReadOnlyLabelPresent(),
                "User should contain read only user label in suggestions!"
        );

        addMemberModal.chooseSuggestionUsername(readOnlyUser.getUsername());

        Assert.assertEquals(
                addMemberModal.hoverAndGetReadOnlyUserAnnotationToolTip(readOnlyUser.getUsername()),
                "Read-only users can only have a project role as Guest",
                "ToolTip is not as excepted!"
        );

        addMemberModal.chooseRoleByName(RoleEnum.GUEST.getName());
        addMemberModal.clickAddButton();

        Assert.assertEquals(
                membersPage.getPopUp(),
                "Project member was successfully updated",
                "Pop up about member update is not as excepted!"
        );
        Assert.assertTrue(
                membersPage.isMemberPresent(readOnlyUser.getUsername()),
                "User should present on members page!"
        );

        MemberCard memberCard = membersPage.getMemberByName(readOnlyUser.getUsername());

        Assert.assertEquals(memberCard.getRole(), RoleEnum.GUEST.getName(), "Role is not as excepted!");
        Assert.assertTrue(memberCard.isSelectRoleIconDisabled(), "Select role button shouldn't be present!");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-5985")
    public void verifyUserCanDeleteReadOnlyUserFromProject() {
        SoftAssert softAssert = new SoftAssert();

        User readOnlyUser = userService.addRandomReadOnlyUserToTenant();
        createdUsers.add(readOnlyUser);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        membersPage.addMemberToProject(readOnlyUser.getUsername(), RoleEnum.GUEST);

        MemberCard memberCard = membersPage.getMemberByName(readOnlyUser.getUsername());
        memberCard.delete();

        softAssert.assertEquals(membersPage.getPopUp(), MessageEnum.PROJECT_MEMBER_WAS_SUCCESSFULLY_DELETED.getDescription(),
                "Pop up about member delete is not as excepted!");
        softAssert.assertFalse(membersPage.isMemberPresent(readOnlyUser.getUsername()), "Member shouldn't present on list!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5980")
    @Maintainer("Gmamaladze")
    public void verifyReadOnlyUserCannotBeAddedWithTheAdminManagerAndEngineerRole() {
        WebDriver webDriver = super.getDriver();

        User readOnlyUser = userService.addRandomReadOnlyUserToTenant();
        createdUsers.add(readOnlyUser);

        MembersPageR membersPage = MembersPageR.openPageDirectly(webDriver, projectKeyForMember);
        AddMemberModal addMemberModal = membersPage.openAddMemberModal();

        addMemberModal.typeUsernameWithSuggestionChoosing(readOnlyUser.getUsername());

        SoftAssert softAssert = new SoftAssert();
        for (RoleEnum role : RoleEnum.values()) {
            if (role == RoleEnum.GUEST) {
                continue;
            }

            addMemberModal.chooseRoleByName(role.getName());

            softAssert.assertEquals(
                    addMemberModal.getAddMembersModalMessage(),
                    "Read-only users (1/1) will not be added to the project with the selected role",
                    "Message is not as expected after choosing " + role.getName() + " role!"
            );

            addMemberModal.clickAddButton();

            softAssert.assertEquals(
                    membersPage.getPopUp(),
                    "There is no valid Project Assignments to save",
                    "Pop up is not as expected after clicking on add button to create member with " + role.getName() + " role!"
            );
        }
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5981")
    @Maintainer("Gmamaladze")
    public void verifyCounterOfReadOnlyUser() {
        User readOnlyUser = userService.addRandomReadOnlyUserToTenant();
        createdUsers.add(readOnlyUser);

        User regularUser = userService.addRandomUserToTenant();
        createdUsers.add(regularUser);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        AddMemberModal addMemberModal = membersPage.openAddMemberModal();

        addMemberModal.chooseRoleByName(RoleEnum.ADMINISTRATOR.getName());
        addMemberModal.typeUsernameWithSuggestionChoosing(readOnlyUser.getUsername());
        addMemberModal.typeUsernameWithSuggestionChoosing(regularUser.getUsername());

        Assert.assertEquals(
                addMemberModal.getAddMembersModalMessage(),
                "Read-only users (1/2) will not be added to the project with the selected role",
                "Quantity of read only user is not as excepted in message!"
        );
    }

    @Test
    @TestCaseKey({"ZTP-5983", "ZTP-5982"})
    @Maintainer("Gmamaladze")
    public void verifyWhenAddingRegularAndReadOnlyUserToProjectWithRoleOtherThanGuestOnlyRegularIsAdded() {
        SoftAssert softAssert = new SoftAssert();

        User readOnlyUser = userService.addRandomReadOnlyUserToTenant();
        createdUsers.add(readOnlyUser);

        User regularUser = userService.addRandomUserToTenant();
        createdUsers.add(regularUser);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);

        for (RoleEnum role : RoleEnum.values()) {
            AddMemberModal addMemberModal = membersPage.openAddMemberModal();
            addMemberModal.chooseRoleByName(role.getName());
            addMemberModal.typeUsernameWithSuggestionChoosing(readOnlyUser.getUsername());
            addMemberModal.typeUsernameWithSuggestionChoosing(regularUser.getUsername());

            if (role == RoleEnum.GUEST) {
                softAssert.assertFalse(addMemberModal.isAddMembersModalMessagePresent(),
                        "Add members modal message shouldn't be present after choosing role 'Guest'");

                addMemberModal.clickAddButton();

                softAssert.assertEquals(membersPage.getPopUp(), MessageEnum.PROJECT_MEMBER_WAS_SUCCESSFULLY_UPDATED.getDescription(),
                        "Message is not as expected after adding user with the " + role.getName() + " role!");
                softAssert.assertTrue(membersPage.isMemberPresent(regularUser.getUsername()),
                        "User is not present in list after adding with " + role.getName() + " role!");
                softAssert.assertTrue(membersPage.isMemberPresent(readOnlyUser.getUsername()),
                        "Read only user should be in list!");

                membersPage.getMemberByName(regularUser.getUsername()).delete();
                membersPage.getMemberByName(readOnlyUser.getUsername()).delete();
            } else {
                addMemberModal.clickAddButton();

                softAssert.assertEquals(membersPage.getPopUp(), MessageEnum.PROJECT_MEMBER_WAS_SUCCESSFULLY_UPDATED.getDescription(),
                        "Message is not as expected after adding user with the " + role.getName() + " role!");
                softAssert.assertTrue(membersPage.isMemberPresent(regularUser.getUsername()),
                        "User is not present in list after adding with " + role.getName() + " role!");
                softAssert.assertFalse(membersPage.isMemberPresent(readOnlyUser.getUsername()),
                        "Read only user shouldn't be in list!");

                membersPage.getMemberByName(regularUser.getUsername()).delete();
            }
        }

        softAssert.assertAll();
    }


    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-5987", "ZTP-5988"})
    public void verifyReadOnlyUserCantSeePrivateProjectIfNotAddedAsGuest() {
        SoftAssert softAssert = new SoftAssert();

        User readOnlyUser = userService.addRandomReadOnlyUserToTenant();
        createdUsers.add(readOnlyUser);

        Project privateProject = projectV1Service.createPrivateProject();

        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());
        localStorageManager.clear();

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(readOnlyUser));

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        softAssert.assertFalse(projectsPage.isProjectWithNameAndKeyExists(privateProject.getKey(), privateProject.getKey()),
                "Project shouldn't be present in list!");

        localStorageManager.clear();
        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(UsersEnum.MAIN_ADMIN.getUser()));

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), privateProject.getKey());
        membersPage.addMemberToProject(readOnlyUser.getUsername(), RoleEnum.GUEST);

        softAssert.assertEquals(membersPage.getPopUp(), MessageEnum.PROJECT_MEMBER_WAS_SUCCESSFULLY_UPDATED.getDescription(),
                "Message is not as excepted!");
        softAssert.assertTrue(membersPage.isMemberPresent(readOnlyUser.getUsername()), "User is not present in list!");

        localStorageManager.clear();
        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(readOnlyUser));

        projectsPage = ProjectsPage.openPageDirectly(getDriver());

        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(privateProject.getName(), privateProject.getKey()),
                "Project should be present in list!");

        softAssert.assertAll();
    }

}
