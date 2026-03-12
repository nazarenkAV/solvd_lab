package com.zebrunner.automation.gui.smoke.addrepo;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.GitlabProperties;
import com.zebrunner.automation.legacy.RepoTypeEnum;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.helper.IExtendedWebElementHelper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Maintainer("Gmamaladze")
@Slf4j
public class GitLabTest extends LogInBase implements IExtendedWebElementHelper {

    private Project project;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }


    // ------------------------------------- Tests -----------------------------------------------------------

    @Test
    @TestCaseKey("ZTP-1842")
    public void addPrivateGitLabRepoTest() {
        WebDriver webDriver = super.getDriver();

        String gitRepositorySlug = "zebrunner/tests/automation-configurations";
        String gitRepositoryUrl = ConfigHelper.getGitlabProperties().getUrl() + "/" + gitRepositorySlug;

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        addRepoPage.addGitRepository(GitProvider.GITLAB, gitRepositoryUrl, "", "");

        Assert.assertEquals(
                addRepoPage.getPopUp(),
                "Resource was not found in GitLab. Error message: '404 Project Not Found'",
                "Gitlab resource was not found Popup is not as expected !"
        );

        LauncherPage launcherPage = LauncherPage.openPageDirectly(webDriver, project);
        Assert.assertFalse(launcherPage.isRepoPresentInList(gitRepositorySlug), "Private repo should not present in list !");

        addRepoPage = launcherPage.addRepo();
        addRepoPage.addGitRepository(
                GitProvider.GITLAB,
                gitRepositoryUrl,
                ConfigHelper.getGitlabProperties().getUsername(),
                ConfigHelper.getGitlabProperties().getAccessToken()
        );

        launcherPage = new LauncherPage(webDriver);
        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Private repo should present on list !");

        launcherPage.chooseRepo(gitRepositorySlug);
        launcherPage.connect();

        Assert.assertEquals(
                launcherPage.getPopUp(),
                "Repository was successfully updated",
                "Repository was successfully updated popup is not as expected !"
        );
        Assert.assertEquals(
                launcherPage.getRepoStatus(),
                "Repo is connected.",
                "Repository should be connected !"
        );

        launcherPage.toSelectedRepo();

        PageUtil.toOtherTabWithoutClosingFirstOne(webDriver);
        super.pause(2);

        Assert.assertTrue(
                findExtendedWebElement(By.xpath("//*[contains(text(), 'gitlab')]")).isElementPresent(10),
                "Gitlab page should be opened !"
        );

        PageUtil.toOtherTab(webDriver);
        super.pause(2);

        launcherPage.typeUsername("invalid");
        launcherPage.typeAccessToken("invalid");
        launcherPage.connect();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(
                launcherPage.getPopUp(),
                "GitLab rejected the action due to unauthenticated access. Error message: '401 Unauthorized'",
                "Gitlab rejected action popup is not as excepted !"
        );
        softAssert.assertEquals(
                launcherPage.getRepoStatus(),
                "Unable to connect: please check credentials and access permissions.",
                "Repository should not be connected !"
        );
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1842", "ZTP-1843"})
    public void addPublicGitLabRepoAndRedirectToGitLabPageTest() {
        WebDriver webDriver = super.getDriver();

        String gitRepositorySlug = "dhreben/carina-demo";
        String gitRepositoryUrl = ConfigHelper.getGitlabProperties().getUrl() + "/" + gitRepositorySlug;

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        LauncherPage launcherPage = addRepoPage.addGitRepository(
                GitProvider.GITLAB, gitRepositoryUrl, "", ""
        );
        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Public repo should present on list !");

        launcherPage.chooseRepo(gitRepositorySlug);
        launcherPage.toSelectedRepo();

        PageUtil.toOtherTabWithoutClosingFirstOne(webDriver);
        super.pause(2);

        String url = webDriver.getCurrentUrl();
        ExtendedWebElement repositoryName = findExtendedWebElement(By.xpath("//*[text() = 'carina-demo']"));

        Assert.assertTrue(url.contains(gitRepositorySlug), "Url should contain " + gitRepositorySlug);
        Assert.assertTrue(repositoryName.isElementPresent(10), "Repository should present on page !");

        PageUtil.toOtherTab(webDriver);
        super.pause(2);
    }

}
