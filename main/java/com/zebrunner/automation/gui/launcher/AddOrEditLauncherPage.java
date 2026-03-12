package com.zebrunner.automation.gui.launcher;

import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.api.launcher.domain.LauncherWeb;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.launcher.preset.CreatePresetModal;
import com.zebrunner.automation.gui.launcher.preset.DeletePresetModal;
import com.zebrunner.automation.legacy.LauncherDataEnum;

@Getter
@Slf4j
public class AddOrEditLauncherPage extends LauncherPage {
    public static final String PAGE_NAME = "Add new launcher";

    @FindBy(id = "name")
    private Element launcherNameInput;

    public AddOrEditLauncherPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(launcherNameInput.getRootExtendedElement());
    }

    public static AddOrEditLauncherPage openPage(WebDriver driver) {
        log.info("Attempt to go to the page '{}'", PAGE_NAME);
        AddOrEditLauncherPage addOrEditLauncherPage = new AddOrEditLauncherPage(driver);
        addOrEditLauncherPage.pause(2);
        return addOrEditLauncherPage;
    }

    public AddOrEditLauncherPage fillLaunchingPageByLauncher(LauncherWeb launcher)
            throws NoSuchElementException {
        log.info("Trying to add a new launcher with name {}...", launcher.getLaunchName());
        AddOrEditLauncherPage addLauncherPage = AddOrEditLauncherPage.openPage(getDriver())
                .typeLauncherName(launcher.getLaunchName())
                .findBranchAndChoose(launcher.getBranch());
        if (launcher.getExecutionEnvironment() != null && !getSelectedLauncherForm().getExecutionEnvSection().getExecutionEnv().getText()
                .equalsIgnoreCase("zebrunner executor")) {
            addLauncherPage.findAndChooseExecutionEnv(launcher.getExecutionEnvironment());
        }
        if (launcher.getDockerImage() != null) {
            addLauncherPage.findAndChooseDockerImage(launcher.getDockerImage());
        }
        if (launcher.getLaunchCommand() != null) {
            addLauncherPage.typeLaunchCommand(launcher.getLaunchCommand());
        }

        if (launcher.getOs() != null) {
            addLauncherPage.clickOperationSystem()
                    .selectOS(launcher.getOs(), launcher.getOsType());
        }
        if (launcher.getDevice() != null) {
            if (launcher.getDevice().equalsIgnoreCase(LauncherDataEnum.REDROID.get())) {
                log.info("Devise is redroid!");
                AndroidDeviceModal androidDeviceModal = addLauncherPage.openDeviceChoosingModal();
                if (launcher.getDeviceVersion() == null) {
                    log.info("Version is not set!");
                    log.info("Latest will be selected!");
                    androidDeviceModal.selectDevice(launcher.getDevice());
                } else {

                    androidDeviceModal.selectVersion(launcher.getDeviceVersion());
                }
            } else {
                log.info("Devise is " + launcher.getDevice());
                AndroidDeviceModal androidDeviceModal = addLauncherPage.openDeviceChoosingModal();
                androidDeviceModal.selectDevice(launcher.getDevice());
            }
        }
        if (launcher.getBrowser() != null) {
            BrowsersModal browsersModal = addLauncherPage.openBrowserChoosingModal();
            if (launcher.getBrowserVersion() == null) {
                browsersModal.getVersions(launcher.getBrowser())
                        // choosing the last version of launcher
                        .selectBrowserVersionByOrder(0);
            } else {
                browsersModal.getVersions(launcher.getBrowser())
                        .selectBrowserVersion(launcher.getBrowserVersion());
            }
        }
        if (launcher.getEnvVariables() != null) {
            launcher.getEnvVariables().
                    forEach(this::createNewEnvVariable);
        }

        if (launcher.getCustomVariables() != null) {
            launcher.getCustomVariables().
                    forEach(this::createNewCapability);
        }
        return this;
    }

    public AddOrEditLauncherPage chooseBrowserVersion(String browserName, String browserVersion) {
        openBrowserChoosingModal()
                .getVersions(browserName)
                .selectBrowserVersion(browserVersion);
        return this;
    }

    public List<String> getBrowserVersions(String browserName) {
        return openBrowserChoosingModal()
                .getVersions(browserName)
                .getBrowserVersions();
    }

    public List<String> getRedroidVersions() {
        return openDeviceChoosingModal()
                .getRedroidVersions();
    }

    public AddOrEditLauncherPage guaranteedToHideDropDownList() {
        Actions builder = new Actions(this.getDriver());
        builder.sendKeys(new CharSequence[]{Keys.ESCAPE}).perform();
        return this;
    }

    public boolean isLauncherNameFieldActive() {
        guaranteedToHideDropDownList();
        log.info("Checking launcher name field...");
        return launcherNameInput.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isBranchFieldActive() {
        return getSelectedLauncherForm().getSelectedBranchSection().isBranchFieldActive();
    }

    public boolean isDockerImageFieldActive() {
        return getSelectedLauncherForm().getExecutionEnvSection().isDockerImageClickable();
    }

    public boolean isLaunchCommandFieldActive() {
        return getSelectedLauncherForm().getExecutionEnvSection().isLaunchCommandClickable();
    }

    public AddOrEditLauncherPage clickLaunchCommandField() {
        launcherNameInput.click();
        return this;
    }

    public AddOrEditLauncherPage typeLauncherName(String name) {
        guaranteedToHideDropDownList();

        Actions action = new Actions(getDriver());
        action.click(launcherNameInput.getElement())
                .pause(1)
                .click(launcherNameInput.getElement())
                .click(launcherNameInput.getElement()).perform();

        launcherNameInput.sendKeys(name);
        return this;
    }

    public AddOrEditLauncherPage clickLaunchNameInput() {
        launcherNameInput.click();
        return this;
    }

    public AddOrEditLauncherPage findBranchAndChoose(String branchName) throws NoSuchElementException {
        getSelectedLauncherForm().getSelectedBranchSection().findBranchAndChoose(branchName);
        return this;
    }

    public AddOrEditLauncherPage clickDockerImage() {
        guaranteedToHideDropDownList();
        getSelectedLauncherForm().getExecutionEnvSection().getDockerImageInput().click();
        return this;
    }

    public AddOrEditLauncherPage clickBranch() {
        getSelectedLauncherForm().getSelectedBranchSection().clickBranch();
        return this;
    }

    public AddOrEditLauncherPage typeLaunchCommand(String launchCommand) {
        getSelectedLauncherForm().getExecutionEnvSection().typeLaunchCommand(launchCommand);
        return this;
    }

    public AddOrEditLauncherPage findAndChooseDockerImage(String imageName) throws NoSuchElementException {
        getSelectedLauncherForm().getExecutionEnvSection().findAndChooseDockerImage(imageName);
        return this;
    }

    public AddOrEditLauncherPage findAndChooseExecutionInstanceType(String instanceTypeName) throws NoSuchElementException {
        getSelectedLauncherForm().getExecutionEnvSection().findAndChooseExecutionInstanceType(instanceTypeName);
        return this;
    }

    public AddOrEditLauncherPage findAndChooseExecutionEnv(String execEnvName) throws NoSuchElementException {
        guaranteedToHideDropDownList();
        getSelectedLauncherForm().getExecutionEnvSection().findAndChooseExecutionEnv(execEnvName);
        return this;
    }

    public CustomVariableAddingForm createNewEnvironmentVariable() {
        return getSelectedLauncherForm().getEnvVariablesSection().createNewEnvironmentVariable();
    }

    public CustomVariableAddingForm createNewCapability(CustomVariable var) {
        return getSelectedLauncherForm().getCustomCapabilitiesSection().createNewCapability(var);
    }

    public CustomVariableAddingForm createNewEnvVariable(CustomVariable var) {
        return getSelectedLauncherForm().getEnvVariablesSection().createNewEnvVariable(var);
    }

    public boolean isVariablePresentInCapabilitiesList(String varName) {
        return getSelectedLauncherForm().getCustomCapabilitiesSection().isVariablePresentInCapabilitiesList(varName);
    }

    public boolean isVariablePresentInEnvVariablesList(String varName) {
        return getSelectedLauncherForm().getEnvVariablesSection().isVariablePresentInEnvVariablesList(varName);
    }

    public CustomVariableAddingForm getEnvVariableWithName(String variableName) {
        return getSelectedLauncherForm().getEnvVariablesSection().getEnvVariableWithName(variableName);
    }

    public CustomVariableAddingForm getCapabilityWithName(String variableName) {
        return getSelectedLauncherForm().getCustomCapabilitiesSection().getCapabilityWithName(variableName);
    }

    public AddOrEditLauncherPage typeSlackChannel(String slackChannel) {
        getSelectedLauncherForm().getNotificationChannelsSection().typeSlackChannel(slackChannel);
        return this;
    }

    public AddOrEditLauncherPage typeMSTeamsChannel(String MSTeamsChannel) {
        getSelectedLauncherForm().getNotificationChannelsSection().typeMSTeamsChannel(MSTeamsChannel);
        return this;
    }

    public AddOrEditLauncherPage typeEmail(String email) {
        getSelectedLauncherForm().getNotificationChannelsSection().typeEmail(email);
        return this;
    }

    public AddOrEditLauncherPage expandChannels() {
        getSelectedLauncherForm().getNotificationChannelsSection().expandChannel();
        return this;
    }

    public LauncherPage submitLauncherAdding() {
        log.info("Adding launcher...");
        guaranteedToHideDropDownList();
        getSelectedLauncherForm().getFooterSection().clickAddButton();
        pause(2);
        return LauncherPage.openPage(getDriver());
    }

    public AddOrEditLauncherPage selectPlatform(String platformName) throws NoSuchElementException {
        getSelectedLauncherForm().getTestingPlatformSection().selectPlatform(platformName);
        return this;
    }

    public String getSelectedPlatform() {
        return getSelectedLauncherForm().getTestingPlatformSection().getSelectedTestingPlatform();
    }

    public OperationSystemWindow clickOperationSystem() {
        return getSelectedLauncherForm().getTestingPlatformSection().clickOperationSystem();
    }

    public BrowsersModal openBrowserChoosingModal() {
        return getSelectedLauncherForm().getTestingPlatformSection().openBrowserChoosingModal();
    }

    public AndroidDeviceModal openDeviceChoosingModal() {
        return getSelectedLauncherForm().getTestingPlatformSection().openDeviceChoosingModal();
    }

    public String getSelectedBrowser() {
        return getSelectedLauncherForm().getTestingPlatformSection().getSelectedBrowser();
    }

    public String getSelectedOS() {
        return getSelectedLauncherForm().getTestingPlatformSection().getSelectedOS();
    }

    public String getCapabilityVariablesValues() {
        return getSelectedLauncherForm().getTestingPlatformSection().getCapabilityVariables();
    }

    public String getEnvVariablesValues() {
        return getSelectedLauncherForm().getExecutionEnvSection().getEnvVariables();
    }

    public void clickChangeDefaultsButton() {
        this.getSelectedLauncherForm().getChangeDefaultsBtn().click();
    }

    public CreatePresetModal clickSavePresetButton() {
        this.getSelectedLauncherForm().getFooterSection().getSavePresetBtn().click();
        return CreatePresetModal.getModalInstance(getDriver());
    }

    public AddOrEditLauncherPage savePreset(String presetName) {
        this.getSelectedLauncherForm().getFooterSection().getSavePresetBtn().click();
        CreatePresetModal.getModalInstance(getDriver()).typePresetName(presetName)
                .submitModal();
        return this;
    }

    public DeletePresetModal clickDeletePresetButton() {
        this.getSelectedLauncherForm().getFooterSection().getDeletePresetBtn().click();
        return DeletePresetModal.getModalInstance(getDriver());
    }

    public void clickEditPresetButton() {
        this.getSelectedLauncherForm().getFooterSection().clickEditButton();
    }

    public void typePresetName(String name) {
        launcherNameInput.sendKeys(name);
    }

    public AddOrEditLauncherPage clickSaveButton() { //when try to edit preset
        this.getSelectedLauncherForm().getFooterSection().getSaveBtn().click();
        waitUntil(ExpectedConditions.invisibilityOf(getSelectedLauncherForm().getFooterSection().getSaveBtn().getRootExtendedElement().getElement()),
                5);
        return this;
    }

    public boolean isSaveButtonClickable() {//when try to edit preset
        return getSelectedLauncherForm().getFooterSection().isSaveButtonClickable();
    }

    public boolean isAddButtonClickable() {
        return getSelectedLauncherForm().getFooterSection().isAddButtonClickable();
    }

    public String getDockerImageValue() {
        return getSelectedLauncherForm().getExecutionEnvSection().getDockerImage();
    }

    public String getLaunchCommandValue() {
        return getSelectedLauncherForm().getExecutionEnvSection().getLaunchCommand();
    }

    public void clearLauncherInput() {
        String inputValue = launcherNameInput.getAttribute("value");

        for (int i = 0; i < inputValue.length(); i++) {
            launcherNameInput.sendKeys("\u0008");
        }
    }
}
