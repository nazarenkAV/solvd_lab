package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.automation.gui.integration.SettingsPageR;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.legacy.DockerImageEnum;
import com.zebrunner.automation.legacy.UsersEnum;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeactivatedUserAccessTest extends LogInBase {
    private String projectKey;

    private User randomForDeactivation;
    private User mainAdmin = UsersEnum.MAIN_ADMIN.getUser();

    @BeforeMethod
    public void addUsers() {
        User user = userService.generateRandomUser();

        randomForDeactivation = userService.create(user);
        randomForDeactivation.setPassword(user.getPassword());

        userService.addUserToGroup(2, randomForDeactivation.getId());

        mainAdmin = userService.getUserByUsername(mainAdmin.getUsername());
    }

    @AfterMethod(alwaysRun = true)
    public void deleteUsersAndProject() {
        userService.deleteUserById(randomForDeactivation.getId());
        projectV1Service.deleteProjectByKey(projectKey);
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-3478")
    public void verifyLaunchersAccess() {
        WebDriver webDriver = super.getDriver();

        LoginPage loginPage = new LoginPage(webDriver);
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);
        projectsPage.getHeader().logout();

        loginPage.login(randomForDeactivation.getUsername(), randomForDeactivation.getPassword());

        boolean isUserAdmin = SettingsPageR.openPageDirectly(webDriver)
                                           .toGroupsAndPermissionsPage()
                                           .getUserGroupByName("Admins")
                                           .isUserPresentInGroup(randomForDeactivation.getFirstName() + " " + randomForDeactivation.getLastName());
        Assert.assertTrue(isUserAdmin, "Created user is not in admin group!");

        String projectName = "Automation " + RandomStringUtils.randomAlphabetic(5);
        projectKey = ("aut" + RandomStringUtils.randomAlphabetic(3)).toUpperCase();

        projectsPage = ProjectsPage.openPageDirectly(webDriver);
        projectsPage.createProject(projectName, projectKey);

        super.pause(2);

        Long projectId = projectV1Service.getProjectIdByKey(projectKey);
        projectV1Service.assignUserToProject(projectId, mainAdmin.getId(), "ADMINISTRATOR");

        LauncherPage launcherPage = new LauncherPage(webDriver);
        launcherPage.openURL(String.format(LauncherPage.PAGE_URL, projectKey));

        AddRepoPage addRepoPage = new AddRepoPage(webDriver);
        String gitRepositorySlug = "dikazak/carina-demo";
        String gitRepositoryUrl = ConfigHelper.getGithubProperties().getUrl() + "/" + gitRepositorySlug;

        addRepoPage.addGitRepository(
                GitProvider.GITHUB,
                gitRepositoryUrl,
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken()
        );

        String launcherName = "Launcher N" + RandomStringUtils.randomAlphabetic(5);
        AddOrEditLauncherPage addOrEditLauncherPage = launcherPage.chooseRepo(gitRepositorySlug)
                                                                  .clickOnRepository()
                                                                  .clickAddNewLauncherBtn()
                                                                  .typeLauncherName(launcherName)
                                                                  .findAndChooseDockerImage(DockerImageEnum.MAVEN_3_8_11.getDockerImage())
                                                                  .findBranchAndChoose("main")
                                                                  .typeLaunchCommand("mvn clean test -Dsuite=helloWorld");

        addOrEditLauncherPage.submitLauncherAdding();

        addOrEditLauncherPage.getHeader().logout();

        userService.deactivateUser(randomForDeactivation);

        loginPage.login(randomForDeactivation.getUsername(), randomForDeactivation.getPassword());

        Assert.assertEquals(
                loginPage.getErrorMessageContent(), "We were not able to log you in: your account was deactivated by workspace administrator and access to the system is suspended.",
                "User deactivation message when trying to log in is different than expected!"
        );

        loginPage.login(UsersEnum.MAIN_ADMIN.getUser().getUsername(), UsersEnum.MAIN_ADMIN.getUser().getPassword());

        super.pause(2);

        UsersPageR usersPageR = UsersPageR.openPageDirectly(webDriver);
        usersPageR.getSearchUserField().type(randomForDeactivation.getUsername());

        Assert.assertEquals(
                usersPageR.getUserCard(randomForDeactivation.getUsername()).getStatus(), "Inactive",
                "User should have inactive status!"
        );

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, projectKey);
        automationLaunchesPage.toLauncherPage();

        Assert.assertTrue(
                launcherPage.getLauncherWithName(gitRepositorySlug, launcherName).isPresent(),
                "Launcher created by deactivated user is not present!"
        );

        super.pause(2);
        launcherPage.chooseRepo(gitRepositorySlug)
                    .getLauncherWithName(launcherName)
                    .orElseThrow(() -> new AssertionError("Launcher with name '" + launcherName + "' must be present"));
    }

}
