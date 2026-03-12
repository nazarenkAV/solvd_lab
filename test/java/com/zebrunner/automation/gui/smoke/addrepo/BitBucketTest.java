package com.zebrunner.automation.gui.smoke.addrepo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.launcher.AddRepoPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.helper.IExtendedWebElementHelper;

public class BitBucketTest extends LogInBase implements IExtendedWebElementHelper {

    private Project project;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }


    //====================================== Test ======================================================


    @Test
    @TestCaseKey("ZTP-1838")
    public void addPrivateBitbucketRepoTest() {
        WebDriver webDriver = super.getDriver();

        String gitRepositorySlug = "zebrunner/private-repo";
        String gitRepositoryUrl = ConfigHelper.getBitbucketProperties().getUrl() + "/" + gitRepositorySlug;

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        addRepoPage.addGitRepository(GitProvider.BITBUCKET, gitRepositoryUrl, "", "");

        Assert.assertEquals(
                addRepoPage.getPopUp(),
                "Resource was not found in Bitbucket. Error message: 'You may not have access to this repository or it no longer exists in this workspace. If you think this repository exists and you have access, make sure you are authenticated.'",
                "Bitbucket resource was not found Popup is not as expected !"
        );

        LauncherPage launcherPage = LauncherPage.openPageDirectly(webDriver, project);
        Assert.assertFalse(launcherPage.isRepoPresentInList(gitRepositorySlug), "Private repo should not present in list !");

        addRepoPage = launcherPage.addRepo();
        addRepoPage.addGitRepository(
                GitProvider.BITBUCKET,
                gitRepositoryUrl,
                ConfigHelper.getBitbucketProperties().getUsername(),
                ConfigHelper.getBitbucketProperties().getAccessToken()
        );

        launcherPage = new LauncherPage(webDriver);
        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Private repo should present on list !");

        launcherPage.chooseRepo(gitRepositorySlug);
        launcherPage.connect();

        Assert.assertEquals(
                launcherPage.getPopUp(), "Repository was successfully updated",
                "Repository was successfully updated popup is not as expected !");
        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Repo is connected.",
                "Repository should be connected !"
        );

        launcherPage.toSelectedRepo();

        PageUtil.toOtherTabWithoutClosingFirstOne(webDriver);
        super.pause(2);

        if (findExtendedWebElement(By.xpath("//*[@class = 'aui-page-panel-inner']")).isElementPresent(3)) {
            findExtendedWebElement(By.xpath("//*[@class = 'aui-button aui-button-primary']")).click();
        }

        Assert.assertTrue(
                findExtendedWebElement(By.xpath("//*[contains(text(), 'Bitbucket')]")).isElementPresent(15),
                "Bitbucket page should be opened !"
        );

        PageUtil.toOtherTab(webDriver);
        super.pause(2);

        launcherPage.typeUsername("invalid");
        launcherPage.typeAccessToken("invalid");
        launcherPage.connect();

        Assert.assertEquals(
                launcherPage.getPopUp(), "Bitbucket rejected the action due to unauthenticated access. Error message: ''",
                "Bitbucket rejected action popup is not as excepted !"
        );
        Assert.assertEquals(
                launcherPage.getRepoStatus(), "Unable to connect: please check credentials and access permissions.",
                "Repository should not be connected !"
        );
    }

    @Test
    @TestCaseKey({"ZTP-1838", "ZTP-1839"})
    public void addPublicBitbucketRepoAndRedirectToBitbucketPageTest() {
        WebDriver webDriver = super.getDriver();

        String gitRepositorySlug = "zebrunner/public-repo";
        String gitRepositoryUrl = ConfigHelper.getBitbucketProperties().getUrl() + "/" + gitRepositorySlug;

        AddRepoPage addRepoPage = AddRepoPage.openPageDirectly(webDriver, project);
        LauncherPage launcherPage = addRepoPage.addGitRepository(GitProvider.BITBUCKET, gitRepositoryUrl, null, null);

        Assert.assertTrue(launcherPage.isRepoPresentInList(gitRepositorySlug), "Public repo should present on list !");

        launcherPage.chooseRepo(gitRepositorySlug);
        launcherPage.toSelectedRepo();

        PageUtil.toOtherTabWithoutClosingFirstOne(webDriver);
        super.pause(2);

        String url = webDriver.getCurrentUrl();
        ExtendedWebElement repositoryName = findExtendedWebElement(By.xpath("//*[text() = 'public-repo']"));

        Assert.assertTrue(url.contains(gitRepositorySlug), "Url should contain " + gitRepositorySlug);
        Assert.assertTrue(repositoryName.isElementPresent(10), "Repository should present on page !");

        PageUtil.toOtherTab(webDriver);
        super.pause(2);
    }

}
