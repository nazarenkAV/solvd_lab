package com.zebrunner.automation.gui.launcher;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.tcm.domain.Repository;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.launcher.preset.PresetItem;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.utils.common.CommonUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;

@Slf4j
@Getter
public class LauncherPage extends TenantProjectBasePage {

    public static final String PAGE_NAME = "Launcher";
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/automation-launchers";

    @FindBy(xpath = "//div[@class='launcher-tree__repo']/ancestor::li[contains(@class,'launcher-tree__item')]")
    private List<LauncherTree> launcherTrees;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Launch']")
    private Element launchButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='Repository']//ancestor::button")
    private Element addRepoButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Delete launcher']")
    private Element deleteLauncherBtn;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Delete Repo']")
    private Element deleteRepoButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Connect']")
    private Element connectRepoButton;

    @FindBy(xpath = "//input[@id='username']")
    private Element usernameInput;

    @FindBy(xpath = "//input[@id='accessToken']")
    private Element accessTokenInput;

    @FindBy(xpath = "//span[@class='selected-repo__connection-status-message-text']")
    private Element selectedRepoStatus;

    @FindBy(xpath = "//a[@class='selected-repo__header-link']")
    private ExtendedWebElement selectedRepo;

    @FindBy(xpath = "//a[@class='selected-repo__header-link']//*[@viewBox='0 0 24 24']")
    private ExtendedWebElement toSelectedRepoOnGithub;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//input[@placeholder='Search by name']")
    private Element searchInput;

    @FindBy(xpath = "//form[contains(@class,'selected-launcher')]")
    private SelectedLauncherForm selectedLauncherForm;

    @FindBy(xpath = "//div[@class='section-title']")
    private TitleSection titleSection;

    @FindBy(xpath = "//span[text()='Repository']//ancestor::button")
    private ExtendedWebElement uiLoadedMarker;

    public LauncherPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    @Deprecated
    public static LauncherPage openPage(WebDriver driver) {
        LauncherPage launcherPage = new LauncherPage(driver);
        launcherPage.pause(2);
        return launcherPage;
    }

    public static LauncherPage getInstance(WebDriver driver) {
        LauncherPage launcherPage = new LauncherPage(driver);
        launcherPage.pause(2);
        return launcherPage;
    }

    public static LauncherPage openPageDirectly(WebDriver driver, Project project) {
        LauncherPage launcherPage = new LauncherPage(driver);
        launcherPage.openURL(String.format(PAGE_URL, project.getKey()));
        CommonUtils.pause(1);
        launcherPage.waitUntilStalenessOfPage();
        launcherPage.assertPageOpened();
        return launcherPage;
    }

    public static LauncherPage openPageDirectly(WebDriver driver, String projectKey) {
        LauncherPage launcherPage = new LauncherPage(driver);
        launcherPage.openURL(String.format(PAGE_URL, projectKey));
        CommonUtils.pause(1);
        launcherPage.waitUntilStalenessOfPage();
        launcherPage.assertPageOpened();
        return launcherPage;
    }

    public void waitUntilStalenessOfPage() {
        waitUntil(ExpectedConditions.stalenessOf(uiLoadedMarker.getElement()), 3);
    }

    public AutomationLaunchesPage launchLauncher() {
        launchButton.click();
        return AutomationLaunchesPage.getPageInstance(getDriver());
    }

    public AutomationLaunchesPage clickLaunch() {
        getSelectedLauncherForm().getFooterSection().clickLaunchButton();
        return AutomationLaunchesPage.getPageInstance(getDriver());
    }

    public AddRepoPage addRepo() {
        addRepoButton.click();
        return new AddRepoPage(getDriver());
    }

    public boolean isRepoPresentInList(String repoName) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(launcherTrees,
                tree -> {
                    String repo;
                    try {
                        repo = tree.getRepository().getText();
                        log.info("Repo is " + repo);
                    } catch (Exception e) {
                        return false;
                    }
                    return repo.equalsIgnoreCase(repoName);
                });
    }

    public String getRepoStatus() {
        pause(4);
        return selectedRepoStatus.getText();
    }

    public String getSelectedRepoName() {
        pause(1);
        return selectedRepo.getText().split("launch")[0];
    }

    public DeleteRepoAlertModal deleteRepo() {
        deleteRepoButton.click();
        return DeleteRepoAlertModal.openAlert(getDriver());
    }

    public void searchByName(String name) {
        searchInput.sendKeys(name);
        pause(3);
    }

    public String getUsername() {
        return usernameInput.getAttributeValue("value");
    }

    public String getAccessToken() {
        return accessTokenInput.getAttributeValue("value");
    }

    public void typeUsername(String username) {
        usernameInput.sendKeys(username);
    }

    public void typeAccessToken(String accessToken) {
        accessTokenInput.sendSecretKeys(accessToken);
    }

    public void connect() {
        connectRepoButton.click();
    }

    public LauncherTree chooseRepo(String repoName) {
        return WaitUtil.waitElementAppearedInListByCondition(
                launcherTrees,
                tree -> tree.getRepository().getText().trim().contains(repoName.trim()),
                "Found repo with name " + repoName,
                "Can't find repo with name " + repoName
        );
    }

    @Deprecated(forRemoval = true)
    public LauncherPage chooseRepo(Repository repo) {
        WaitUtil.waitElementAppearedInListByCondition(
                        launcherTrees,
                        tree -> tree.getRepository().getText().contains(repo.getTitle()),
                        "Found repo with name " + repo.getTitle(),
                        "There are no repository with name: " + repo.getTitle()
                )
                .getRepository()
                .click();

        return this;
    }

    public void toSelectedRepo() {
        selectedRepo.click();
        selectedRepo.waitUntilElementDisappear(3);
    }

    public void clickDeleteLauncherBtn() {
        deleteLauncherBtn.waitUntil(Condition.VISIBLE_AND_CLICKABLE);
        deleteLauncherBtn.click();
        DeleteLauncherAlertModal deleteLauncherAlertModal = new DeleteLauncherAlertModal(getDriver());
        deleteLauncherAlertModal.getDeleteModalButton().waitUntil(Condition.VISIBLE_AND_CLICKABLE);
        deleteLauncherAlertModal.getDeleteModalButton().click();
    }

    @Deprecated
    public AddOrEditLauncherPage openAddingNewLauncher() {
        return AddOrEditLauncherPage.openPage(getDriver());
    }

    public Optional<PresetItem> getPresetWithName(String repoName, String launcherName, String presetName) {
        this.getLauncherWithName(repoName, launcherName)
            .ifPresent(launcherItem -> {
                if (launcherItem.getPresetList().isEmpty()) {
                    launcherItem.clickOnLauncherName();

                    super.pause(1);
                }
            });

        return this.chooseRepo(repoName)
                   .getLauncherWithName(launcherName)
                   .flatMap(launcherWithName -> launcherWithName.getPresetWithName(presetName));
    }

    public Optional<LauncherItem> getLauncherWithName(String repoName, String launcherName) {
        return this.chooseRepo(repoName)
                   .clickOnRepository()
                   .getLauncherWithName(launcherName);
    }

}
