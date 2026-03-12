package com.zebrunner.automation.gui.smoke.preset;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.collections.Lists;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.ExecutionEnvSection;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.launcher.NotificationChannelsSection;
import com.zebrunner.automation.gui.launcher.SchedulesSection;
import com.zebrunner.automation.gui.launcher.preset.CreatePresetModal;
import com.zebrunner.automation.gui.launcher.preset.PresetItem;
import com.zebrunner.automation.gui.launcher.preset.ScheduleItem;
import com.zebrunner.automation.legacy.DockerImageEnum;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.legacy.LauncherDataEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
public class UpdatePresetTest extends LogInBase {
    private final String REPO_NAME = "dikazak/carina-demo";
    private final String LAUNCHER_NAME = "Carina API tests";
    private final String QUARTZ_CRON_EXPRESSION_CORRECT = "0 0 12 1 1 ? *";
    private final String QUARTZ_CRON_EXPRESSION_INVALID = " 0 0 123333 1 1 ? *";
    private AddOrEditLauncherPage addOrEditLauncherPage;
    private Project project;
    private Long projectId;
    private String createdPresetName;
    private Launcher createdLauncher;
    private Long createdRepoId;

    @BeforeClass
    public void createLauncher() {
        project = LogInBase.project;
        projectId = project.getId();

        createdRepoId = LogInBase.repositoryId;


        IntegrationManager.addIntegration(project.getId(), Tool.BROWSER_STACK);
        IntegrationManager.addIntegration(project.getId(), Tool.LAMBDA_TEST);
        IntegrationManager.addIntegration(project.getId(), Tool.SAUCE_LABS);
        IntegrationManager.addIntegration(project.getId(), Tool.SLACK);
        IntegrationManager.addIntegration(project.getId(), Tool.TEAMS_WEBHOOK);
        IntegrationManager.addIntegration(project.getId(), Tool.ZEBRUNNER_DEVICE_FARM);

        createdLauncher = launcherService.addDefaultApiTestsLauncher(projectId, createdRepoId, LAUNCHER_NAME, "api");
    }

    @BeforeMethod
    public void createPreset() {
        createdPresetName = "Pre-created preset ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);

        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();//ZTP-3267
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(createPresetModal.getModalTitle()
                                                 .getText(), CreatePresetModal.MODAL_NAME, "Modal title is not as expected!");
        createPresetModal
                .typePresetName(createdPresetName)
                .submitModal();
        launcherPage.waitUntilStalenessOfPage();
        launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, createdPresetName)
                    .ifPresentOrElse(presetItem -> presetItem.getRootExtendedElement().click(),
                            () -> {
                                softAssert.fail("Unable to find preset with name " + createdPresetName);
                                softAssert.assertAll();
                            });
    }

    @AfterClass
    public void deleteCreatedLauncher() {
        launcherService.deleteLauncher(projectId, createdRepoId, createdLauncher.getId());
    }

    @Test
    @TestCaseKey({"ZTP-3319", "ZTP-3320", "ZTP-3321", "ZTP-3289"})
    public void editNameAndGitBranchInPreset() {
        String newPresetName = "Latin characters, numeric values and spaces ".concat(RandomStringUtils.randomNumeric(3));

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(
                addOrEditLauncherPage.getSelectedLauncherForm().getFooterSection().isEditButtonClickable(),
                "'Edit' preset button should be visible and clickable after preset creation!");//ZTP-3319
        addOrEditLauncherPage.clickEditPresetButton();//ZTP-3289
        addOrEditLauncherPage.typePresetName(newPresetName);
        softAssert.assertTrue(
                addOrEditLauncherPage.getSelectedLauncherForm().getFooterSection().isSaveButtonClickable(),
                "'Save' preset button should be clickable after inputting valid preset name!");//ZTP-3320
        String selectedBranch = addOrEditLauncherPage.getSelectedLauncherForm().getSelectedBranchSection()
                                                     .selectAnyBranchAndClickOnIt();
        softAssert.assertEquals(addOrEditLauncherPage.getSelectedLauncherForm().getSelectedBranchSection()
                                                     .getBranchValue(),
                selectedBranch,
                "Branch is not as selected!");//ZTP-3321

        addOrEditLauncherPage.clickSaveButton();
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.UPDATE_PRESET_POPUP.getDescription(),
                "Popup about updating is not as expected!");

        LauncherPage launcherPage = LauncherPage.openPage(getDriver());
        Optional<PresetItem> updatedPreset = launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, newPresetName);

        if (updatedPreset.isPresent()) {
            updatedPreset.get().getRootExtendedElement().click();
            softAssert.assertEquals(launcherPage.getSelectedLauncherForm().getSelectedPresetTitle()
                                                .getText(), newPresetName,
                    "Updated preset name is not as expected!");
            softAssert.assertEquals(addOrEditLauncherPage.getSelectedLauncherForm().getSelectedBranchSection()
                                                         .getBranchValue(),
                    selectedBranch, "Preset branch is not as saved!");
        } else {
            softAssert.fail("Updated preset should be in presets list!");
        }
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-3350", "ZTP-3351", "ZTP-3352", "ZTP-3353", "ZTP-3354", "ZTP-3355", "ZTP-3356", "ZTP-3357", "ZTP-3331"})
    public void editDockerImageInPreset() {
        String customLaunchCmd = "my cmd dd=frt && ; fkldl kpkasl${jffmf} !@#$%&*()'+,-./:;<=>?[]^_`{|}";
        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        List<DockerImageEnum> images = Lists.newArrayList(DockerImageEnum.values());
        DockerImageEnum lastSelectedDockerImage = images.get(images.size() - 1);
        images.forEach(image -> {
                    log.info("Checking image " + image.getDockerImage());
                    ExecutionEnvSection executionEnvSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                                   .getExecutionEnvSection();
                    if (image.getDockerImage().equalsIgnoreCase(createdLauncher.getConfig().getDockerImage())) {

                        softAssert.assertEquals(executionEnvSection.getDockerImage(), createdLauncher.getConfig()
                                                                                                     .getDockerImage(),
                                "Docker image is not as selected!");
                        softAssert.assertEquals(executionEnvSection.getLaunchCommand(), createdLauncher.getConfig()
                                                                                                       .getLaunchCommand(),
                                "Launch command is not as selected!");
                    } else {
                        executionEnvSection.findAndChooseDockerImage(image.getDockerImage());
                        softAssert.assertEquals(executionEnvSection.getDockerImage(), image.getDockerImage(), "Docker image is not as expected!");
                        softAssert.assertEquals(executionEnvSection.getLaunchCommand(), image.getLaunchCommand(), "Launch command is not as expected!");
                    }//ZTP-335,ZTP-3351,ZTP-3352,ZTP-3353,ZTP-3354,ZTP-3355,ZTP-3356,ZTP-3357
                }
        );

        addOrEditLauncherPage.clickSaveButton();
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.UPDATE_PRESET_POPUP.getDescription(),
                "Popup about updating is not as expected!");

        LauncherPage launcherPage = LauncherPage.openPage(getDriver());
        Optional<PresetItem> updatedPreset = launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, createdPresetName);

        if (updatedPreset.isPresent()) {
            updatedPreset.get().getRootExtendedElement().click();
            ExecutionEnvSection executionEnvSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                           .getExecutionEnvSection();
            softAssert.assertEquals(executionEnvSection.getDockerImage(),
                    lastSelectedDockerImage.getDockerImage(),
                    "Docker image is not as saved!");
            softAssert.assertFalse(executionEnvSection.isDockerImageClickable(),
                    "Docker image shouldn't be clickable on saved preset!");
            softAssert.assertEquals(executionEnvSection.getLaunchCommand(), lastSelectedDockerImage.getLaunchCommand(),
                    "Launch command is not as saved!");
            softAssert.assertFalse(executionEnvSection.isLaunchCommandClickable(),
                    "Launch command shouldn't be clickable on saved preset!");
            addOrEditLauncherPage.clickEditPresetButton();
            executionEnvSection.typeLaunchCommand(customLaunchCmd);//ZTP-3331 User can enter any Launch Command
            addOrEditLauncherPage.clickSaveButton();

        } else {
            softAssert.fail("Unable to find updated preset in presets list!");
        }
        updatedPreset = launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, createdPresetName);

        if (updatedPreset.isPresent()) {
            updatedPreset.get().getRootExtendedElement().click();
            ExecutionEnvSection executionEnvSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                           .getExecutionEnvSection();
            softAssert.assertEquals(executionEnvSection.getLaunchCommand(), customLaunchCmd,
                    "Custom launch command is not as saved!");
            softAssert.assertFalse(executionEnvSection.isLaunchCommandClickable(),
                    "Custom launch shouldn't be clickable on saved preset!");
            softAssert.assertEquals(executionEnvSection.getDockerImage(),
                    lastSelectedDockerImage.getDockerImage(),
                    "Docker image is not as saved after setting custom launch command!");
        } else {
            softAssert.fail("Unable to find updated preset in presets list after setting custom launch command!");
        }
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-3322", "ZTP-3323", "ZTP-3324", "ZTP-3325", "ZTP-3326", "ZTP-3327", "ZTP-3328", "ZTP-3282"})
    public void addSchedulingToPreCreatedPreset() {
        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        SchedulesSection schedulesSection = addOrEditLauncherPage.getSelectedLauncherForm().getSchedulesSection();
        schedulesSection.clickAddScheduleButton();//3326
        ScheduleItem scheduleItem = schedulesSection.getScheduleItems().get(0);

        scheduleItem.typeCronExpression(QUARTZ_CRON_EXPRESSION_INVALID);
        softAssert.assertEquals(scheduleItem.getCronErrorMessage(),
                MessageEnum.ENTER_VALID_EXPRESSION.getDescription(),
                "Error message is not as expected!");//"ZTP-3282"

        scheduleItem.typeCronExpression(QUARTZ_CRON_EXPRESSION_CORRECT);//ZTP-3322
        softAssert.assertFalse(scheduleItem.getCronInputError().isStateMatches(Condition.VISIBLE),
                "Error message shouldn't appears when entering valid cron expression!");

        String expectedTimeZone = scheduleItem.selectAnyTimeZone(); //ZTP-3323
        softAssert.assertEquals(scheduleItem.getSelectedTimezone().getText(),
                expectedTimeZone, "Selected timezone is not as expected!");

        schedulesSection.addSchedules(4);//ZTP-3327
        softAssert.assertEquals(schedulesSection.getScheduleItems().size(), 5, "Numbers of schedules should be 5");
        softAssert.assertFalse(schedulesSection.getAddScheduleBtn().isStateMatches(Condition.VISIBLE),
                "'Add schedule' button should be invisible after adding 5 schedules!");//ZTP-3328

        schedulesSection.getScheduleItems().stream().findAny().get().clickDelete();//ZTP-3324
        softAssert.assertEquals(schedulesSection.getScheduleItems()
                                                .size(), 4, "Numbers of schedules should be 4 after deleting one");
        pause(1);

        schedulesSection.getScheduleItems().stream().findAny().get().clickDelete();//ZTP-3325
        softAssert.assertEquals(schedulesSection.getScheduleItems()
                                                .size(), 3, "Numbers of schedules should be 4 after deleting one");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-3691")
    public void createScheduleButtonShouldBeDisabledWhenSettingInvalidCron() {
        Artifact.attachReferenceToTest("ZEB-5726", "https://solvd.atlassian.net/browse/ZEB-5726");

        addOrEditLauncherPage.clickEditPresetButton();

        SchedulesSection schedulesSection = addOrEditLauncherPage.getSelectedLauncherForm().getSchedulesSection();
        schedulesSection.clickAddScheduleButton();

        ScheduleItem scheduleItem = schedulesSection.getScheduleItems().get(0);
        scheduleItem.typeCronExpression(QUARTZ_CRON_EXPRESSION_INVALID);

        Assert.assertEquals(
                scheduleItem.getCronErrorMessage(), "Please enter valid expression",
                "Error message is not as expected!"
        );
        Assert.assertFalse(addOrEditLauncherPage.isSaveButtonClickable(), "'Save' button should be disabled when setting cron with invalid format!");
    }

    @Test
    @TestCaseKey({"ZTP-3358", "ZTP-3359", "ZTP-3360", "ZTP-3361", "ZTP-3362"})
    public void userCanSelectAnyTestingPlatform() {
        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        List<String> testingPlatforms = Lists.newArrayList(LauncherDataEnum.SAUCE_LABS.get(),//"ZTP-3360"
                LauncherDataEnum.BROWSER_STACK.get(),//"ZTP-3359"
                LauncherDataEnum.ZEBRUNNER_SELENIUM_GRID.get(),//"ZTP-3358"
                LauncherDataEnum.LAMBDATEST.get(),//"ZTP-3362"
                LauncherDataEnum.M_CLOUD.get());//"ZTP-3361"

        testingPlatforms.forEach(testingPlatform -> {
            addOrEditLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().selectPlatform(testingPlatform);
            softAssert.assertEquals(addOrEditLauncherPage.getSelectedLauncherForm().getTestingPlatformSection()
                                                         .getSelectedTestingPlatform(),
                    testingPlatform,
                    "Testing platform is not as selected!");
        });

        addOrEditLauncherPage.clickSaveButton();
        softAssert.assertEquals(addOrEditLauncherPage.getSelectedLauncherForm().getTestingPlatformSection()
                                                     .getSelectedTestingPlatform(),
                testingPlatforms.get(testingPlatforms.size() - 1),
                "Testing platform is not as selected after saving preset!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-3294", "ZTP-3474", "ZTP-3475", "ZTP-3476"})
    public void userCanAddNotificationChannels() {
        SoftAssert softAssert = new SoftAssert();
        NotificationChannelsSection notificationChannelsSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                                       .getNotificationChannelsSection();

        softAssert.assertTrue(notificationChannelsSection.isExpandNotificationChannelButtonClickable(),
                "Expand notification channel button should be clickable!");
        notificationChannelsSection.expandChannel();//ZTP-3294 Verify that Notification channels button is clickable
        softAssert.assertTrue(notificationChannelsSection.getEmail().isStateMatches(Condition.VISIBLE),
                "Emails should be visible after clocking on expand button!");
        softAssert.assertTrue(notificationChannelsSection.getSlackChannel().isStateMatches(Condition.VISIBLE),
                "Slack channels should be visible after clocking on expand button!");
        softAssert.assertTrue(notificationChannelsSection.getTeamsChannel().isStateMatches(Condition.VISIBLE),
                "MsTeams channels be visible after clocking on expand button!");

        addOrEditLauncherPage.clickEditPresetButton();
        String email = "updatedEmail123@gjod.com";
        String twoEmails = "updatedEmail123@gjod.com, updatedEmail123@gjod.com";
        String invalidEmail = "invalid";
        String slackChannel = "updatedSlackChannel 123";
        String msTeamChannel = "updatedMsTeamChannel 123";

        notificationChannelsSection = addOrEditLauncherPage.getSelectedLauncherForm().getNotificationChannelsSection();
        notificationChannelsSection.typeEmail(invalidEmail);
        softAssert.assertEquals(notificationChannelsSection.getInputFieldErrorMessage(notificationChannelsSection.getEmail()),
                invalidEmail + " is not a valid email",
                "Error message should not be visible!");

        notificationChannelsSection.typeEmail(email);
        pause(1);
        softAssert.assertFalse(notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getEmail()),
                "Error message should not be visible when entering correct email!");
        notificationChannelsSection.typeEmail(twoEmails);
        pause(1);
        softAssert.assertFalse(notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getEmail()),
                "Error message should not be visible when entering correct email!");

        notificationChannelsSection.typeMSTeamsChannel(msTeamChannel);
        softAssert.assertFalse(notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getTeamsChannel()),
                "Error message should not be visible when entering correct MSTeams channel!");

        notificationChannelsSection.typeSlackChannel(slackChannel);
        softAssert.assertFalse(notificationChannelsSection.isInputFieldErrorMessagePresent(notificationChannelsSection.getSlackChannel()),
                "Error message should not be visible when entering correct Slack channel!");

        addOrEditLauncherPage.clickSaveButton();

        notificationChannelsSection = addOrEditLauncherPage.getSelectedLauncherForm().getNotificationChannelsSection();
        softAssert.assertEquals(notificationChannelsSection.getMsTeamsChannels(), msTeamChannel,
                "Obtained MsTeams channel is not as inputted!");

        softAssert.assertEquals(notificationChannelsSection.getEmails(), twoEmails,
                "Obtained email is not as inputted!");

        softAssert.assertEquals(notificationChannelsSection.getSlackChannels(), slackChannel,
                "Obtained Slack channel is not as inputted!");

        softAssert.assertTrue(notificationChannelsSection.isSlackFieldDisabled(),
                "Slack channels input field should be disabled!");
        softAssert.assertTrue(notificationChannelsSection.isMsTeamsFieldDisabled(),
                "Ms Teams channels input field should be disabled!");
        softAssert.assertTrue(notificationChannelsSection.isEmailsFieldDisabled(),
                "Emails input field should be disabled!");
        softAssert.assertAll();
    }

}
