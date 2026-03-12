package com.zebrunner.automation.gui.smoke.launcher;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.collections.Lists;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.api.launcher.domain.LauncherWeb;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.LauncherItem;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.launcher.NotificationChannelsSection;
import com.zebrunner.automation.legacy.DockerImageEnum;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.legacy.LauncherDataEnum;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
@Maintainer("obabich")
public class LauncherCreationTest extends LogInBase {

    private static final String REPO_NAME = "test-zeb/launcher-add-variables";

    private Long repoId;
    private Project project;

    @BeforeTest
    public void getProject() {
        project = LogInBase.project;
        repoId = launcherService.addGitRepo(
                project.getId(),
                ConfigHelper.getGithubProperties().getUrl() + "/" + REPO_NAME,
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken(),
                GitProvider.GITHUB.toString()
        );

        IntegrationManager.addIntegration(project.getId(), Tool.BROWSER_STACK);
        IntegrationManager.addIntegration(project.getId(), Tool.LAMBDA_TEST);
        IntegrationManager.addIntegration(project.getId(), Tool.SAUCE_LABS);
        IntegrationManager.addIntegration(project.getId(), Tool.SLACK);
        IntegrationManager.addIntegration(project.getId(), Tool.TEAMS_WEBHOOK);
    }

    @AfterTest
    public void deleteRepo() {
        launcherService.deleteGitRepoById(project.getId(), repoId);
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1855", "ZTP-1861", "ZTP-1862", "ZTP-1863", "ZTP-1864", "ZTP-1865", "ZTP-1866", "ZTP-1867", "ZTP-1868"})
    public void _addNewLauncherInputFieldsAndDockerImagesTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.CREATION);

        String acceptableLauncherName = "Latin letters, numbers(ZTP-1861) and use spaces";
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);

        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(REPO_NAME)
                .clickOnRepository()
                .clickAddNewLauncherBtn();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(addLauncherPage.getSelectedLauncherForm().getSelectedBranchSection().getBranchValue()
                                             .isEmpty(),
                "Branch should be empty");

        addLauncherPage.clickBranch();

        softAssert.assertTrue(addLauncherPage.isBranchFieldActive(), "Branch field should be active!");
        addLauncherPage.clickLaunchNameInput();
        softAssert.assertTrue(addLauncherPage.isLauncherNameFieldActive(), "Launcher name field should be active!");
        addLauncherPage.clickDockerImage();
        softAssert.assertTrue(addLauncherPage.isDockerImageFieldActive(), "Docker image field should be active!");
        addLauncherPage.clickLaunchCommandField();
        softAssert.assertTrue(addLauncherPage.isLaunchCommandFieldActive(), "Launch command  field should be active!");
        addLauncherPage
                .typeLauncherName(acceptableLauncherName)
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        Lists.newArrayList(DockerImageEnum.values()).forEach(dockerImage -> {
            addLauncherPage.clickLaunchCommandField();
            addLauncherPage.findAndChooseDockerImage(dockerImage.getDockerImage());// ZTP-1863

            softAssert.assertEquals(addLauncherPage.getDockerImageValue(), dockerImage.getDockerImage(),
                    "Docker image is not as expected!");
            pause(1);
            softAssert.assertEquals(addLauncherPage.getLaunchCommandValue(), dockerImage.getLaunchCommand(),
                    "Launch command is not as expected!");
        });
        String customLaunchCommand = "User can enter Latin_letters, numbers(ZTP-1868) and use spaces in 'Launch Command' field";
        addLauncherPage.typeLaunchCommand(customLaunchCommand);
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection()
                                               .getLaunchCommand(), customLaunchCommand,
                "Custom launch command is not as expected!");
        softAssert.assertEquals(addLauncherPage.getSelectedLauncherForm()
                                               .getSelectedLauncherName(), acceptableLauncherName,
                "Created launcher name is not as expected!");
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1897", "ZTP-1898", "ZTP-1899", "ZTP-3681"})
    public void addNotificationChannels() {
        WebDriver webDriver = super.getDriver();

        String launcherName = "notification channel checks " + RandomStringUtils.randomAlphabetic(5);
        String email = "email123@gjod.com";
        String invalidEmail = "invalidEmail";
        String twoEmails = "email123@gjod.com, email1423@gjod.com";
        String slackChannel = "slackChannel 123";
        String msTeamChannel = "msTeamChannel 123";
        String twoSlackChannel = "slackChannel 123, slackChannel 1";
        String twoMsTeamChannel = "msTeamChannel 123, msTeamChannel 1";

        LauncherPage launcherPage = LauncherPage.openPageDirectly(webDriver, project);
        AddOrEditLauncherPage addLauncherPage =
                launcherPage.chooseRepo(REPO_NAME)
                            .clickOnRepository()
                            .clickAddNewLauncherBtn()
                            .typeLauncherName(launcherName)
                            .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                            .findBranchAndChoose("main")
                            .expandChannels()
                            .typeEmail(email)
                            .typeSlackChannel(slackChannel)
                            .typeMSTeamsChannel(msTeamChannel);

        addLauncherPage.submitLauncherAdding();
        NotificationChannelsSection notificationChannelsSection = addLauncherPage.getSelectedLauncherForm()
                                                                                 .getNotificationChannelsSection();

        Assert.assertTrue(
                notificationChannelsSection.getSlackChannels().contains(slackChannel),
                "Expected slack channel was not found!"
        );
        Assert.assertTrue(
                notificationChannelsSection.getEmails().contains(email),
                "Expected email was not found!"
        );
        Assert.assertTrue(
                notificationChannelsSection.getMsTeamsChannels().contains(msTeamChannel),
                "Expected msTeams channel was not found!"
        );

        addLauncherPage.clickChangeDefaultsButton();
        addLauncherPage.typeEmail(invalidEmail);

        Assert.assertEquals(
                notificationChannelsSection.getInputFieldErrorMessage(notificationChannelsSection.getEmail()),
                invalidEmail + " is not a valid email",
                "Error message should not be visible!"
        );
        Assert.assertTrue(
                notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getEmail()),
                "Error message should not be visible when entering correct email!"
        );
        Assert.assertFalse(
                addLauncherPage.isSaveButtonClickable(),
                "Add button should be disabled when entering invalid email!"
        );

        addLauncherPage.typeSlackChannel(twoSlackChannel);
        Assert.assertFalse(
                notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getSlackChannel()),
                "Error message should not be visible when entering two slack channels!"
        );

        addLauncherPage.typeMSTeamsChannel(twoMsTeamChannel);
        Assert.assertFalse(
                notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getTeamsChannel()),
                "Error message should not be visible when entering two MsTeams channels!"
        );

        addLauncherPage.typeEmail(twoEmails);
        super.pause(1);

        Assert.assertFalse(
                notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getEmail()),
                "Error message should not be visible when entering two emails!"
        );

        addLauncherPage.clickSaveButton();

        Assert.assertTrue(
                notificationChannelsSection.getSlackChannels().contains(twoSlackChannel),
                "Expected slack channels were not found!"
        );
        Assert.assertTrue(
                notificationChannelsSection.getEmails().contains(twoEmails),
                "Expected emails were not found!"
        );
        Assert.assertTrue(
                notificationChannelsSection.getMsTeamsChannels().contains(twoMsTeamChannel),
                "Expected msTeams channels were not found!"
        );
        Assert.assertTrue(
                addLauncherPage.getSelectedLauncherForm().getFooterSection().isLaunchButtonClickable(),
                "Launch button should be active when saving launch with valid notification channels!"
        );
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1900", "ZTP-1901", "ZTP-1902", "ZTP-1904"})
    public void selectTestingPlatform() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.CREATION);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(REPO_NAME)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("launcher to check testing platform selecting № ".concat(RandomStringUtils.randomNumeric(5)))
                .findBranchAndChoose(LauncherDataEnum.MAIN.get())
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .selectPlatform(LauncherDataEnum.LAMBDATEST.get());// ZTP-1901

        softAssert.assertEquals(addLauncherPage.getSelectedPlatform(), LauncherDataEnum.LAMBDATEST.get(), "Platform is not as expected!");

        addLauncherPage.selectPlatform(LauncherDataEnum.SAUCE_LABS.get());// ZTP-1902
        softAssert.assertEquals(addLauncherPage.getSelectedPlatform(), LauncherDataEnum.SAUCE_LABS.get(), "Platform is not as expected!");

        addLauncherPage.selectPlatform(LauncherDataEnum.ZEBRUNNER_SELENIUM_GRID.get());// ZTP-1900
        softAssert.assertEquals(addLauncherPage.getSelectedPlatform(), LauncherDataEnum.ZEBRUNNER_SELENIUM_GRID.get(),
                "Platform is not as expected!");

        addLauncherPage.selectPlatform(LauncherDataEnum.BROWSER_STACK.get());// ZTP-1901
        softAssert.assertEquals(addLauncherPage.getSelectedPlatform(), LauncherDataEnum.BROWSER_STACK.get(), "Platform is not as expected!");

        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection()
                                               .getTestingPlatformName().getText(),
                LauncherDataEnum.BROWSER_STACK.get()
                , "Testing platform is not as expected!");
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    public void operationSystemSelect() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.CREATION);

        SoftAssert softAssert = new SoftAssert();
        String launcherName = "os checks ".concat(RandomStringUtils.randomAlphabetic(5));
        String os = LauncherDataEnum.LINUX.get();
        String browser = LauncherDataEnum.FIREFOX.get();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(REPO_NAME)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName(launcherName)
                .findBranchAndChoose(LauncherDataEnum.MAIN.get())
                .findAndChooseDockerImage(LauncherDataEnum.MAVEN_3_8_11.get())
                .clickOperationSystem()
                .selectOS(os);
        softAssert.assertEquals(addLauncherPage.getSelectedPlatform(), LauncherDataEnum.ZEBRUNNER_SELENIUM_GRID.get(),
                "Platform is not as expected!");

        String browserVersion = addLauncherPage.openBrowserChoosingModal().getVersions(browser)
                                               .selectBrowserVersionByOrder(0);

        addLauncherPage.submitLauncherAdding();
        softAssert.assertEquals(addLauncherPage.getSelectedBrowser(), (browser + " " + browserVersion), "Selected browser is not as expected!");
        softAssert.assertEquals(addLauncherPage.getSelectedOS(), os, "Selected OS is not as expected!");

        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1848"})
    public void addAndDeleteLauncher() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.CREATION);

        SoftAssert softAssert = new SoftAssert();
        String launcherName = "Launcher № ".concat(RandomStringUtils.randomNumeric(5));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        LauncherWeb launcher = LauncherWeb.builder()
                                          .launchName(launcherName)
                                          .branch(LauncherDataEnum.MAIN.get())
                                          .dockerImage(LauncherDataEnum.MAVEN_3_8_11.get())
                                          .build();

        launcherPage
                .chooseRepo(REPO_NAME)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .fillLaunchingPageByLauncher(launcher)
                .submitLauncherAdding();

        softAssert.assertTrue(launcherPage.chooseRepo(REPO_NAME).isLauncherPresentInList(launcherName),
                String.format("Launcher with name %s is not present in list", launcherName));

        Optional<LauncherItem> launcherItem = launcherPage.chooseRepo(REPO_NAME).getLauncherWithName(launcherName);

        if (launcherItem.isPresent()) {
            launcherItem.get().getRootExtendedElement().click();
        } else {

            softAssert.assertAll(String.format("We cannot get launcher with name %s", launcherName));
        }

        launcherPage.clickDeleteLauncherBtn();

        softAssert.assertEquals(launcherPage.getPopUp(), "Launcher was successfully deleted", "Popup is not as expected!");
        softAssert.assertFalse(launcherPage.chooseRepo(REPO_NAME).isLauncherPresentInList(launcherName),
                String.format("Launcher with name %s shouldn't be in list after deleting!", launcherName));
        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-3659")
    @Test(groups = "min_acceptance")
    public void operationSystemFieldIsNotClearedAfterAddingCustomCapability() {
        Artifact.attachReferenceToTest("ZEB-5712", "https://solvd.atlassian.net/browse/ZEB-5712");
        Label.attachToTest("bug", "ZEB-5712");

        CustomVariable envVariable = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        CustomVariable capability = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        String launcherName = "Launcher № " + RandomStringUtils.randomNumeric(5);

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        LauncherWeb launcher = new LauncherWeb().setLaunchName(launcherName)
                                                .setBranch("main")
                                                .setDockerImage("maven:3.8-openjdk-11")
                                                .setOs("Linux")
                                                .setBrowser("Chrome")
                                                .setBrowserVersion("110.0");

        AddOrEditLauncherPage addOrEditLauncherPage =
                launcherPage.chooseRepo(REPO_NAME)
                            .clickOnRepository()
                            .clickAddNewLauncherBtn()
                            .fillLaunchingPageByLauncher(launcher);

        Assert.assertEquals(addOrEditLauncherPage.getSelectedOS(), launcher.getOs(), "Os is not as expected!");
        Assert.assertEquals(addOrEditLauncherPage.getSelectedBrowser(), launcher.getBrowserAndVersion(), "Browser is not as expected!");

        addOrEditLauncherPage.createNewEnvVariable(envVariable);
        addOrEditLauncherPage.createNewCapability(capability);

        Assert.assertEquals(
                addOrEditLauncherPage.getSelectedOS(), launcher.getOs(),
                "Os is not as expected after adding capability and env variable!"
        );
        Assert.assertEquals(
                addOrEditLauncherPage.getSelectedBrowser(), launcher.getBrowserAndVersion(),
                "Browser is not as expected after adding capability and env variable!"
        );

        addOrEditLauncherPage.submitLauncherAdding();
        addOrEditLauncherPage = new AddOrEditLauncherPage(getDriver());

        Assert.assertEquals(
                addOrEditLauncherPage.getSelectedOS(), launcher.getOs(),
                "Os is not as expected after saving launcher!"
        );
        Assert.assertEquals(
                addOrEditLauncherPage.getSelectedBrowser(), launcher.getBrowserAndVersion(),
                "Browser is not as expected after saving launcher!"
        );

        addOrEditLauncherPage.clickChangeDefaultsButton();

        Assert.assertEquals(
                addOrEditLauncherPage.getSelectedOS(), launcher.getOs(),
                "Os is not as expected after click on 'Change defaults' button!"
        );
        Assert.assertEquals(
                addOrEditLauncherPage.getSelectedBrowser(), launcher.getBrowserAndVersion(),
                "Browser is not as expected after click on 'Change defaults' button!"
        );
    }
}
