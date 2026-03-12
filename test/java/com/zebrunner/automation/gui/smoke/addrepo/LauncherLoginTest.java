package com.zebrunner.automation.gui.smoke.addrepo;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.automation.legacy.BreadcrumbsEnum;

@Slf4j
public class LauncherLoginTest extends LogInBase {

    private final String usernameGitHab = ConfigHelper.getGithubProperties().getUsername();
    private final String tokenGitHub = ConfigHelper.getGithubProperties().getAccessToken();
    private Project project;


    @BeforeClass()
    public void createProject() {
        project = projectV1Service.createProject();
    }

    @AfterClass()
    public void deleteCreatedProject() {
        Long projectId = project.getId();
        projectV1Service.deleteProjectById(projectId);
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1820", "ZTP-1825", "ZTP-1847", "ZTP-1846", "ZTP-1829", "ZTP-1832", "ZTP-1831", "ZTP-3495"})
    public void _addFirstPublicGithubRepo() {
        WebDriver webDriver = super.getDriver();

        String gitUsername = ConfigHelper.getGithubProperties().getUsername();
        String gitAccessToken = ConfigHelper.getGithubProperties().getAccessToken();

        String gitRepositorySlug = "dikazak/public-repository";
        String gitRepositoryUrl = ConfigHelper.getGithubProperties().getUrl() + "/" + gitRepositorySlug;

        AutomationLaunchesPage testRunsPage = AutomationLaunchesPage.openPageDirectly(webDriver, project);
        AddRepoPage addRepoPage = testRunsPage.launchFirstTime();

        Assert.assertTrue(addRepoPage.isPageOpened(), "Add repo page was not opened!");
        Assert.assertEquals(addRepoPage.getNoRepoMessage()
                                       .getText(), "No launchers. Add repo to start launch.", "'No repo' message is not as expected");

        LauncherPage launcherPage = addRepoPage.addGitRepository(GitProvider.GITHUB, gitRepositoryUrl, gitUsername, gitAccessToken);

        launcherPage.assertPageOpened();
        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Repo should be present in list");
        Assert.assertEquals(launcherPage.getUsername(), gitUsername, "Username is not as expected!");

        launcherPage.searchByName("no repo");

        Assert.assertEquals(
                launcherPage.getEmptyPlaceholder().getEmptyPlaceHolderTitle(),
                "No results were found matching your search",
                "Description is not as expected!"
        );

        launcherPage.searchByName(gitRepositorySlug);
        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Repo should be present in search list");

        addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        addRepoPage.clickBreadcrumb(BreadcrumbsEnum.PROJECTS.getBreadcrumb());
        ProjectsPage projectsPage = ProjectsPage.getInstance(webDriver);
        Assert.assertTrue(projectsPage.isPageOpened(), "Projects page was not opened from breadcrumb!");

        addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        addRepoPage.clickBreadcrumb(project.getKey());
        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.getPageInstance(webDriver);
        Assert.assertTrue(launchesPage.isPageOpened(), "Launches page was not opened from breadcrumb!");
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1826", "ZTP-1819", "ZTP-1834"})
    public void validationsForFieldsOnAddRepoPage() {
        WebDriver webDriver = super.getDriver();

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        Assert.assertTrue(addRepoPage.isPageOpened(), "Add repo page was not opened!");

        addRepoPage.typeUrl("https://github.com/dikazak/invalid");
        addRepoPage.typeUsername(ConfigHelper.getGithubProperties().getUsername());
        addRepoPage.typeAccessToken(ConfigHelper.getGithubProperties().getAccessToken());
        super.pause(1L);

        addRepoPage.clickAdd();
        Assert.assertEquals(
                addRepoPage.getPopUp(),
                "The git repo was not found on the provider side. Probably, you don't have direct access to the repo",
                "Required message is not as expected!"
        );

        addRepoPage.typeUrl("invalid url");
        super.pause(1L);

        addRepoPage.clickAdd();
        Assert.assertTrue(
                addRepoPage.waitIsPopUpMessageAppear("Supplied request body fails to meet validation constraints"),
                "Popup message is not as expected!"
        );
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1821", "ZTP-1849", "ZTP-1851", "ZTP-1852", "ZTP-1853", "ZTP-1854"})
    public void addAndDeletePrivateGithubRepo() {
        String gitRepositorySlug = "dikazak/private-repository";
        String gitRepositoryUrl = ConfigHelper.getGithubProperties().getUrl() + "/" + gitRepositorySlug;

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(getDriver(), project);
        Assert.assertTrue(addRepoPage.isPageOpened(), "Add repo page was not opened!");

        addRepoPage.addGitRepository(GitProvider.GITHUB, gitRepositoryUrl, "invalid", "invalid");
        Assert.assertEquals(
                addRepoPage.getPopUp(), "The provided Github credentials are invalid",
                "Required message is not as expected!"
        );
        Assert.assertTrue(addRepoPage.isPageOpened(), "Add repo page was not opened!");

        addRepoPage.addGitRepository(GitProvider.GITHUB, gitRepositoryUrl, usernameGitHab, tokenGitHub);

        LauncherPage launcherPage = new LauncherPage(getDriver());
        launcherPage.isPageOpened();

        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Repo should be present in repo list!");
        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Repo is connected.",
                "Incorrect success message after checking connection!"
        );
        Assert.assertEquals(launcherPage.getSelectedRepoName(), gitRepositorySlug, "Selected repo name is not as expected!");
        Assert.assertEquals(launcherPage.getUsername(), usernameGitHab, "Username is not as expected!");
        Assert.assertNotEquals(launcherPage.getAccessToken(), "", "Access token is not as expected!");

        launcherPage.connect();
        super.pause(1);

        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Repo is connected.",
                "'Repo is connected.' should appears when click on 'Connect' button after adding new private repo!"
        );

        launcherPage.typeUsername("New");
        launcherPage.typeAccessToken("");
        launcherPage.refresh(1);
        super.pause(2);

        Assert.assertEquals(launcherPage.getUsername(), usernameGitHab, "Selected repo name is not as expected!");
        Assert.assertNotEquals(launcherPage.getAccessToken(), "", "Access token is not as expected!");

        launcherPage.typeUsername(usernameGitHab + "New");
        launcherPage.connect();

        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Repo is connected.",
                "'Repo is connected.' should appear after entering any username(We use only token checking when click on 'Connect button')!"
        );
        Assert.assertEquals(launcherPage.getUsername(), usernameGitHab + "New", "Selected repo name is not as expected!");

        launcherPage.typeAccessToken("invalid");
        super.pause(1);

        launcherPage.connect();
        super.pause(4);

        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Unable to connect: please check credentials and access permissions.",
                "Warning repo status is not as expected!"
        );

        launcherPage.typeUsername(usernameGitHab);
        launcherPage.typeAccessToken(tokenGitHub);
        super.pause(1);

        launcherPage.connect();
        super.pause(1);
        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Repo is connected.",
                "'Repo is connected.' should appear after entering a valid token!"
        );

        String newName = usernameGitHab + "New name";
        launcherPage.typeUsername(newName);
        super.pause(1);

        launcherPage.connect();
        super.pause(1);

        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Repo is connected.",
                "'Repo is connected.' should appear after entering a valid token!"
        );

        launcherPage.refresh(1);
        super.pause(1);
        Assert.assertEquals(launcherPage.getUsername(), newName, "Updated user name is not as expected!");
        Assert.assertNotEquals(launcherPage.getAccessToken(), "", "Access token is not as expected!");
        Assert.assertEquals(launcherPage.getRepoStatus(), "Repo is connected.",
                "'Repo is connected.' should appear after updating username!");

        launcherPage.deleteRepo().clickDelete();
        super.pause(3);
        Assert.assertFalse(launcherPage.isRepoPresentInList(gitRepositorySlug), "Repo should not be in repo list");
    }

}