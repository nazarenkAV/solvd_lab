package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.gui.iam.MemberCard;
import com.zebrunner.automation.gui.iam.AddMemberModal;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.legacy.StringUtil;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.stream.Collectors;

@Maintainer("obabich")
@Slf4j
public class AddMemberProcessTest extends LogInBase {

    private String memberUsername;
    private User randomUser;
    private static Project project;
    private static String projectKeyForMember;
    private List<User> createdUsers = new ArrayList<>();

    @BeforeClass
    public void getProjectKey() {
        project = LogInBase.project;
        projectKeyForMember = project.getKey();
        //  projectV1Service.assignUserToProject(project.getId(), userService.getUserId(MAIN_ADMIN.getUsername()).get(), "MANAGER");
    }

    @BeforeMethod
    public void createUser() {
        randomUser = userService.addRandomUserToTenant();
        memberUsername = randomUser.getUsername();
        createdUsers.add(randomUser);
        log.info("User with '{}' username was created!", memberUsername);
    }

    @AfterMethod
    public void deleteUser() {
        createdUsers.forEach(user -> userService.deleteUserById(user.getId()));
        createdUsers.clear();
    }

    @DataProvider(name = "allRoles")
    public static Object[][] dataprovider2() {
        return new Object[][]{
                {RoleEnum.ADMINISTRATOR},
                {RoleEnum.MANAGER},
                {RoleEnum.ENGINEER},
                {RoleEnum.GUEST}
        };
    }

    @Test(dataProvider = "allRoles", groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-895", "ZTP-896", "ZTP-897", "ZTP-898"})
    public void addMemberTest(RoleEnum role) throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);
        Label.attachToTest(TestLabelsConstant.MEMBERS, TestLabelsConstant.CREATION);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        AddMemberModal memberWindow = membersPage.openAddMemberModal();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(memberWindow.isUsernameFieldPresent(), "Can't find username field");
        softAssert.assertTrue(memberWindow.isRoleFieldPresent(), "Can't find role field");

        memberWindow.typeUsername(memberUsername);

        softAssert.assertTrue(memberWindow.isUsernamePresentInSuggestions(memberUsername),
                "Can't find " + memberUsername + " in username field");
        memberWindow.typeUsernameWithSuggestionChoosing(memberUsername);

        softAssert.assertFalse(memberWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should not be active because role field is empty");
        softAssert.assertTrue(memberWindow.getCancelButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Cancel button should not be active because role field is empty");

        memberWindow.chooseRoleByName(role.getName());

        softAssert.assertTrue(memberWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be active because all fields are filled");
        memberWindow.getSubmitButton().click();
        softAssert.assertEquals(membersPage.getPopUp(), "Project member was successfully updated", "Message is not as expected!");
        softAssert.assertTrue(membersPage.isMemberPresent(memberUsername),
                "Can't find new member with username " + memberUsername);
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    public void changeMemberRoleTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);
        Label.attachToTest(TestLabelsConstant.MEMBERS, TestLabelsConstant.EDITING);
        Label.attachToTest(TestLabelsConstant.MEMBERS, TestLabelsConstant.MEMBER_ROLES);

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        AddMemberModal memberWindow = membersPage.openAddMemberModal();
        String role = "Administrator";
        memberWindow.fillMemberAndSubmitR(memberUsername, role);

        List<String> changeRoleNames = Arrays.stream(RoleEnum.values()).map(RoleEnum::getName)
                                             .collect(Collectors.toList());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(membersPage.isMemberPresent(memberUsername),
                "Can't find new member with username " + memberUsername);

        for (String changeRoleName : changeRoleNames) {
            MemberCard memberCard = membersPage.getMemberByName(memberUsername);
            memberCard.changeRole(changeRoleName);
            memberCard = membersPage.getMemberByName(memberUsername);
            softAssert.assertEquals(memberCard.getRole()
                                              .toLowerCase(Locale.ROOT), changeRoleName.toLowerCase(Locale.ROOT),
                    "Role " + memberCard.getRole() + " didn't changed to " + changeRoleName);
            pause(2);
        }
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-882", "ZTP-883", "ZTP-884"})
    public void memberSearchFieldTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);

        User user = userService.create(userService.generateRandomUser());
        String fullUserName = user.getFirstName() + " " + user.getLastName();
        createdUsers.add(user);
        projectV1Service.assignUserToProject(project.getId(), randomUser.getId(), RoleEnum.MANAGER.getName()
                                                                                                  .toUpperCase());
        projectV1Service.assignUserToProject(project.getId(), user.getId(), RoleEnum.MANAGER.getName().toUpperCase());

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        SoftAssert softAssert = new SoftAssert();
        membersPage.typeInSearchField(memberUsername);//ZTP-883 Verify that any user can search in Search field by username
        softAssert.assertTrue(membersPage.isMemberPresent(memberUsername), "Can't find member after search field filter");
        pause(1);
        membersPage.getMemberCards()
                   .forEach(member -> softAssert.assertTrue(member.getMemberName().contains(randomUser.getUsername()),
                           "All usernames should contain string from search field, member " + member.getName()
                                   + ", search field: " + randomUser.getUsername()));

        membersPage.typeInSearchField(user.getFirstName());//ZTP-884 Verify that any user can search in Search field by  F/L name
        softAssert.assertTrue(membersPage.isMemberPresent(fullUserName),
                "Can't find member after search field filter");
        membersPage.getMemberCards()
                   .forEach(member -> softAssert.assertTrue(member.getMemberName().contains(fullUserName),
                           "All usernames should contain string from search field, member " + member.getName()
                                   + ", search field: " + memberUsername));

        membersPage.typeInSearchField(user.getLastName());//ZTP-884 Verify that any user can search in Search field by  F/L name
        softAssert.assertTrue(membersPage.isMemberPresent(fullUserName),
                "Can't find member after search field filter");
        membersPage.getMemberCards()
                   .forEach(member -> softAssert.assertTrue(member.getMemberName().contains(fullUserName),
                           "All usernames should contain string from search field, member " + member.getName()
                                   + ", search field: " + memberUsername));

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-892", "ZTP-893"})
    public void addMultiplyUsersForOneRoleAsProjectMemberTest() {
        WebDriver webDriver = super.getDriver();

        createdUsers.add(userService.addRandomUserToTenant());
        createdUsers.add(userService.addRandomUserToTenant());

        MembersPageR membersPage = MembersPageR.openPageDirectly(webDriver, projectKeyForMember);
        AddMemberModal memberWindow = membersPage.openAddMemberModal();
        createdUsers.forEach(user -> memberWindow.typeUsernameWithSuggestionChoosing(user.getUsername()));

        memberWindow.chooseRoleByName("Manager");
        memberWindow.getSubmitButton().click();
        super.pause(4);

        for (User user : createdUsers) {
            Assert.assertTrue(
                    membersPage.isMemberPresent(user.getUsername()),
                    "Can't find new member with username " + user.getUsername()
            );
        }

        AddMemberModal newMemberWindow = membersPage.openAddMemberModal();
        newMemberWindow.typeUsernameOrEmail(createdUsers.get(0).getUsername());
        Assert.assertEquals(
                newMemberWindow.getSuggestionsError().getText(),
                "No active workspace users matching the search criteria were found",
                "Message about mistake is not as expected!"
        );

        Assert.assertFalse(
                newMemberWindow.isUsernamePresentInSuggestions(createdUsers.get(0).getUsername()),
                "Suggestion should not contain: " + createdUsers.get(0).getUsername()
        );

        newMemberWindow.getClose().click();

        User user = userService.addRandomUserToTenant();
        createdUsers.add(user);

        membersPage = MembersPageR.openPageDirectly(webDriver, projectKeyForMember);
        membersPage.openAddMemberModal().fillMemberAndSubmitR(user.getUsername(), "Guest");
        Assert.assertTrue(membersPage.isMemberPresent(user.getUsername()), "Can't find new member with username " + user.getUsername());
    }

    @Test
    @TestCaseKey({"ZTP-879", "ZTP-880"})
    public void openingMembersPageFromDifferentPlacesTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);
        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage launchesPageTest = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKeyForMember);
        MembersPageR membersPage = launchesPageTest.getNavigationMenu().toMembersPageR();
        softAssert.assertEquals(membersPage.getTitle(), MembersPageR.PAGE_NAME, "Opened page title is not as expected!(ZTP-879)");//ZTP-879
        softAssert.assertTrue(membersPage.isPageOpened(), "Unable to open 'Members page' from side bar!(ZTP-879)");//ZTP-879

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        membersPage = projectsPage.getProjectCardByProjectKey(projectKeyForMember).toMembersPageR();
        softAssert.assertEquals(membersPage.getTitle(), MembersPageR.PAGE_NAME, "Opened page title is not as expected!(ZTP-879)");//ZTP-880
        softAssert.assertTrue(membersPage.isPageOpened(), "Unable to open 'Members page' from edit project popup!(ZTP-880)");//ZTP-880

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-887", "ZTP-890"})
    public void possibilityToChangeRolesToAnotherMembersAndDefaultSortingTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);
        Label.attachToTest(TestLabelsConstant.MEMBERS, TestLabelsConstant.MEMBER_ROLES);

        SoftAssert softAssert = new SoftAssert();
        createdUsers.add(userService.addRandomUserToTenant());
        createdUsers.add(userService.addRandomUserToTenant());
        createdUsers.add(userService.addRandomUserToTenant());

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        AddMemberModal memberWindow = membersPage.openAddMemberModal();

        createdUsers.forEach(user -> {
            memberWindow.typeUsernameWithSuggestionChoosing(user.getUsername());
        });

        memberWindow.chooseRoleByName(RoleEnum.MANAGER.getName());
        memberWindow.getSubmitButton().click();
        pause(3);

        List<String> uiMemberNames = membersPage.getMemberCards().stream().map(MemberCard::getMemberName)
                                                .collect(Collectors.toList());

        List<String> sortedByNames = uiMemberNames;
        sortedByNames.sort(String::compareToIgnoreCase);

        log.info("Sorted names list " + sortedByNames);
        log.info("Actual names list " + uiMemberNames);

        softAssert.assertEquals(uiMemberNames, sortedByNames, "Default sorting is not alphabetical!");//ZTP-887

        createdUsers.forEach(user -> {
            log.info("Changing role for user " + user.getUsername());
            membersPage.getMemberByName(user.getUsername())
                       .changeRole(RoleEnum.MANAGER.getName());
            MemberCard memberCard = membersPage.getMemberByName(user.getUsername());
            softAssert.assertEquals(memberCard.getRole().toLowerCase(Locale.ROOT), RoleEnum.MANAGER.getName()
                                                                                                   .toLowerCase(Locale.ROOT),
                    "Role " + memberCard.getRole() + " didn't changed to " + RoleEnum.MANAGER.getName() + "for user "
                            + user.getUsername());//ZTP-ZTP-890
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-885"})
    public void sortByRoleTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBER_ROLES);
        SoftAssert softAssert = new SoftAssert();
        createdUsers.add(userService.addRandomUserToTenant());
        createdUsers.add(userService.addRandomUserToTenant());
        createdUsers.add(userService.addRandomUserToTenant());

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);

        createdUsers.forEach(user -> {
            AddMemberModal memberWindow = membersPage.openAddMemberModal();
            memberWindow.typeUsernameWithSuggestionChoosing(user.getUsername());
            memberWindow.clickRandomRole();
            memberWindow.getSubmitButton().click();
        });

        pause(2);
        List<String> memberRolesBeforeSorting = membersPage.getMemberCards()
                                                           .stream().map(MemberCard::getRole)
                                                           .collect(Collectors.toList());

        log.info("Roles before sorting: " + memberRolesBeforeSorting);
        memberRolesBeforeSorting.sort(Comparator.naturalOrder());
        log.info("Roles after sorting: " + memberRolesBeforeSorting);

        membersPage.sortByRole();
        List<String> memberRolesAfterSorting = membersPage.getMemberCards()
                                                          .stream().map(MemberCard::getRole)
                                                          .collect(Collectors.toList());
        softAssert.assertEquals(memberRolesAfterSorting, memberRolesBeforeSorting, "Sorting by role work not as expected!");//ZTP-885

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-891"})
    public void userCanNotDeleteHimselfTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);

        projectV1Service.assignUserToProject(project.getId(), randomUser.getId(), RoleEnum.ENGINEER.getName()
                                                                                                   .toUpperCase());

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        SoftAssert softAssert = new SoftAssert();
        User user = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        softAssert.assertTrue(membersPage.isMemberPresent(StringUtil.getExpectedAuthor(user) + " (me)"),
                "Can't find new member with username " + memberUsername);
        softAssert.assertFalse(membersPage.getMemberByName(StringUtil.getExpectedAuthor(user) + " (me)")
                                          .isDeleteButtonVisible(),
                "User can't delete himself from the project!");
        softAssert.assertTrue(membersPage.getMemberByName(randomUser.getUsername()).isDeleteButtonVisible(),
                "User can delete other member of project!");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-886", "ZTP-894"})
    public void sortByAddedAndVerifyingAddingOneUserToDifferentProjectsTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MEMBERS);

        SoftAssert softAssert = new SoftAssert();

        MembersPageR membersPage = MembersPageR.openPageDirectly(getDriver(), projectKeyForMember);
        createdUsers.forEach(user -> {
            AddMemberModal memberWindow = membersPage.openAddMemberModal();
            memberWindow.typeUsernameWithSuggestionChoosing(user.getUsername());
            memberWindow.clickRandomRole();
            memberWindow.getSubmitButton().click();
        });
        softAssert.assertTrue(membersPage.isMemberPresent(randomUser.getUsername()),
                "Can't find new member with username "
                        + randomUser.getUsername());//ZTP-894 Verify that it is possible to add the same member to various projects

        MembersPageR membersPageDefProject = MembersPageR.openPageDirectly(getDriver(), "DEF");
        AddMemberModal memberWindow = membersPageDefProject.openAddMemberModal();
        memberWindow.typeUsernameWithSuggestionChoosing(randomUser.getUsername());
        memberWindow.clickRandomRole();
        memberWindow.getSubmitButton().click();

        softAssert.assertTrue(membersPageDefProject.isMemberPresent(randomUser.getUsername()),
                "Can't find new member with username "
                        + randomUser.getUsername());//ZTP-894 Verify that it is possible to add the same member to various projects

        List<Date> addedBeforeSortingDefProject = membersPageDefProject.getMemberCards()
                                                                       .stream().map(MemberCard::getAddedDate)
                                                                       .sorted(Comparator.naturalOrder())
                                                                       .collect(Collectors.toList());
        // should use DEF project because there are members with different added date
        membersPageDefProject.sortByAdded();
        List<Date> addedAfterSortingDefProject = membersPageDefProject.getMemberCards()
                                                                      .stream().map(MemberCard::getAddedDate)
                                                                      .collect(Collectors.toList());
        softAssert.assertEquals(addedAfterSortingDefProject, addedBeforeSortingDefProject,
                "Sorting by added work not as expected!");//ZTP-886 Verify that any user can sort by Added column

        softAssert.assertAll();
    }
}
