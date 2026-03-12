package com.zebrunner.automation.gui.smoke.addrepo;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.CurrentTest;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.GithubProperties;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.config.TestMaintainers;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.automation.gui.common.EmptyPlaceholder;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.external.GitHubLogin;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
@Maintainer(TestMaintainers.DKAZAK)
public class LauncherLoginWithGitHubTest extends LogInBase {

    private Project project;

    @BeforeTest
    public void getCreatedProjectAndAddGitRepo() {
        project = LogInBase.project;

        launcherService.addGitRepo(
                project.getId(),
                ConfigHelper.getGithubProperties().getUrl() + "/apiautotestzeb/carina-demo",
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken(),
                GitProvider.GITHUB.toString()
        );
    }

    @AfterMethod(alwaysRun = true)
    public void delimiter(ITestResult result) {
        WebDriver webDriver = super.getDriver();

        VideoDelimiterUtil.delimit(webDriver, result, 5, "GMT+3:00");
    }

    @Test(
            groups = TestGroups.MINIMAL_ACCEPTANCE,
            priority = 1
    )
    @TestCaseKey({"ZTP-1850", "ZTP-1835", "ZTP-1836", "ZTP-3668"})
    public void github_ShouldAddPublicRepository_WhenUserIsLoggedInGithub() {
        this.skipTestIfGithubNotEnabled();

        WebDriver webDriver = super.getDriver();

        String repoName = "apiautotestzeb/public-repository-example";
        String githubUsername = ConfigHelper.getGithubProperties().getUsername();
        String githubPassword = ConfigHelper.getGithubProperties().getAccessToken();

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);

        GitHubLogin gitHubLogin = addRepoPage.loginWithGitHub();
        addRepoPage = gitHubLogin.signIn(githubUsername, githubPassword);
        addRepoPage.isPageOpened();

        webDriver.navigate().refresh();
        addRepoPage.getCertainRepo(repoName).clickAddRepo();

        LauncherPage launcherPage = new LauncherPage(webDriver);

        Assert.assertTrue(launcherPage.isRepoPresentInList(repoName), "Repo should be present in list");

        launcherPage.searchByName("no repo");

        Assert.assertEquals(
                launcherPage.getEmptyPlaceholder().getEmptyPlaceHolderTitle(),
                "No results were found matching your search",
                "Description is not as expected!"
        );
        launcherPage.searchByName(repoName);
        Assert.assertTrue(launcherPage.isRepoPresentInList(repoName), "Repo should be present in search list");
        Assert.assertEquals(
                launcherPage.getRepoStatus(),
                "Repo is connected.",
                "No message 'Repo is connected.' after adding repo"
        );
    }

    @Test(
            groups = TestGroups.MINIMAL_ACCEPTANCE,
            priority = 2
    )
    @TestCaseKey({"ZTP-1830", "ZTP-1828"})
    public void github_ShouldAddPrivateRepository_WhenUserIsLoggedInGithub() {
        this.skipTestIfGithubNotEnabled();

        WebDriver webDriver = super.getDriver();
        String repoName = "apiautotestzeb/private-repository-example";

        SoftAssert softAssert = new SoftAssert();
        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);

        addRepoPage.getCertainRepo(repoName)
                   .clickAddRepo();

        LauncherPage launcherPage = new LauncherPage(webDriver);
        softAssert.assertTrue(
                launcherPage.isRepoPresentInList(repoName),
                "'" + repoName + "' is not present in the repositories list after clicking 'Add' button"
        );

        launcherPage.searchByName("no repo");
        EmptyPlaceholder launcherPagePlaceholder = launcherPage.getEmptyPlaceholder();
        Assert.assertEquals(
                launcherPagePlaceholder.getEmptyPlaceHolderTitle(),
                "No results were found matching your search",
                "Launcher page placeholder text is not as expected."
        );

        launcherPage.searchByName(repoName);
        Assert.assertTrue(
                launcherPage.isRepoPresentInList(repoName),
                "Added '" + repoName + "' is not present in the repositories list after searching by '" + repoName + "'."
        );
        Assert.assertEquals(
                launcherPage.getRepoStatus(),
                "Repo is connected.",
                "Repository connection status doesn't match."
        );
    }

    private void skipTestIfGithubNotEnabled() {
        GithubProperties githubProperties = ConfigHelper.getGithubProperties();

        if (!githubProperties.getEnabled()) {
            CurrentTest.revertRegistration();

            throw new SkipException("Github is not enabled");
        }
    }

}
