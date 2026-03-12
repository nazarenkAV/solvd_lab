package com.zebrunner.automation.gui.launcher;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;

import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.external.GitHubLogin;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;

@Getter
public class AddRepoPage extends TenantProjectBasePage {

    public static final String PAGE_NAME = "Add repo";
    public static final String PAGE_URL_MATCHER = "https://.*/projects/.*?/automation-launchers/add-repo";
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/automation-launchers/add-repo";

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[contains(text(),'Github')]//ancestor::button")
    private Element gitHubButton;
    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[contains(text(),'Gitlab')]//ancestor::button")
    private Element gitLabButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[contains(text(),'Bitbucket')]//ancestor::button")
    private Element bitBucketButton;

    @FindBy(xpath = "//input[@id='url']")
    private Element urlInput;

    @FindBy(xpath = "//input[@id='username']")
    private Element usernameInput;

    @FindBy(xpath = "//*[@type = 'password']")
    private Element accessTokenInput;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='ADD']")
    private Element addRepoButton;

    @FindBy(xpath = "//a[contains(@class,'oauth-login__link')]")
    private Element loginWithGitHub;

    @FindBy(xpath = "//li[@class='repository-list__item']")
    private List<GitHubRepo> gitHubRepos;

    @FindBy(xpath = "//div[@class='add-repo__intro-message']")
    private Element noRepoMessage;
    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[contains(text(),'Github')]//ancestor::button")
    private ExtendedWebElement uiLoadedMarker;

    public AddRepoPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static AddRepoPage openPage(WebDriver driver) {
        AddRepoPage addRepoPage = new AddRepoPage(driver);
        addRepoPage.pause(2);
        return addRepoPage;
    }

    public static AddRepoPage openPageDirectly(WebDriver driver, Project project) {
        AddRepoPage addRepoPage = new AddRepoPage(driver);
        addRepoPage.openURL(String.format(PAGE_URL, project.getKey()));

        Assert.assertTrue(addRepoPage.isPageOpened(), PAGE_NAME + " was not opened!");
        return addRepoPage;
    }

    public void selectRepoType(GitProvider gitProvider) {
        switch (gitProvider) {
            case GITHUB:
                if (gitHubButton.isStateMatches(Condition.CLICKABLE)) {
                    gitHubButton.click();
                }
                break;
            case GITLAB:
                if (gitLabButton.isStateMatches(Condition.CLICKABLE)) {
                    gitLabButton.click();
                }
                break;
            case BITBUCKET:
                if (bitBucketButton.isStateMatches(Condition.CLICKABLE)) {
                    bitBucketButton.click();
                }
                break;
        }
    }

    public LauncherPage addGitRepository(GitProvider gitProvider,
                                         String gitRepositoryUrl,
                                         @Nullable String gitUsername,
                                         @Nullable String gitAccessToken) {
        this.selectRepoType(gitProvider);

        urlInput.sendKeys(gitRepositoryUrl);
        if (gitUsername != null) {
            usernameInput.sendKeys(gitUsername);
        }
        if (gitAccessToken != null) {
            accessTokenInput.sendSecretKeys(gitAccessToken);
        }

        super.pause(1);
        addRepoButton.click();

        return LauncherPage.openPage(super.getDriver());
    }

    public void typeUrl(String repoUrl) {
        urlInput.sendKeys(repoUrl);
    }

    public void typeUsername(String username) {
        usernameInput.sendKeys(username);
    }

    public void typeAccessToken(String accessToken) {
        accessTokenInput.sendKeys(accessToken);
    }

    public void clickAdd() {
        addRepoButton.click();
    }

    public void waitUntilAddDisappeared() {
        addRepoButton.isDisappear();

        super.pause(1);
    }

    public GitHubLogin loginWithGitHub() {
        loginWithGitHub.click();

        return GitHubLogin.openPage(super.getDriver());
    }

    public GitHubRepo getCertainRepo(String repoName) {
        return WaitUtil.waitElementAppearedInListByCondition(
                gitHubRepos,
                gitHubRepo -> gitHubRepo.getRepoName().getText().equalsIgnoreCase(repoName),
                "Github repo with name " + repoName + " was found",
                "Github repo with name " + repoName + " was not found"
        );
    }

}
