package com.zebrunner.automation.gui.smoke.preset;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.ExecutionEnvSection;
import com.zebrunner.automation.gui.launcher.LauncherItem;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.launcher.NotificationChannelsSection;
import com.zebrunner.automation.gui.launcher.SchedulesSection;
import com.zebrunner.automation.gui.launcher.preset.CreatePresetModal;
import com.zebrunner.automation.gui.launcher.preset.PresetItem;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Slf4j
public class PresetsTest extends LogInBase {

    private static final String REPO_NAME = "dikazak/carina-demo";
    private static final String LAUNCHER_NAME = "Carina API launcher";
    private static final String QUARTZ_CRONE_EXPRESSION_CORRECT = "0 0 0 ? * * 2061";
    private static final String QUARTZ_CRONE_EXPRESSION_INVALID = " 0 0 123333 1 1 ? *";

    private Project project;
    private Long createdRepoId;
    private Launcher createdLauncher;

    @BeforeClass
    public void createLauncher() {
        project = LogInBase.project;

        createdRepoId = LogInBase.repositoryId;
        createdLauncher = launcherService.addDefaultApiTestsLauncher(project.getId(), createdRepoId, LAUNCHER_NAME, "api");
    }

    @AfterClass
    public void deleteCreatedLauncher() {
        launcherService.deleteLauncher(project.getId(), createdRepoId, createdLauncher.getId());
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-3267", "ZTP-3269", "ZTP-3270", "ZTP-3280", "ZTP-3288", "ZTP-3306"})
    public void createPresetWithNameOnlyAndDeleteIt() {
        String presetName = "Latin characters, numeric values and spaces(for removing) ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();//ZTP-3267
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(createPresetModal.getModalTitle()
                                                 .getText(), CreatePresetModal.MODAL_NAME, "Modal title is not as expected!");
        createPresetModal
                .typePresetName(presetName)
                .submitModal();//ZTP-3270
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.CREATE_PRESET_POPUP.getDescription(),
                "Popup about creating preset is not as expected!");//ZTP-3280

        Optional<PresetItem> createdPreset = launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName);
        createdPreset.ifPresentOrElse(presetItem -> presetItem.getRootExtendedElement().click(),
                () -> {
                    softAssert.fail("Unable to find preset with name " + presetName);
                    softAssert.assertAll();
                });
        NotificationChannelsSection notificationChannelsSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                                       .getNotificationChannelsSection();
        softAssert.assertTrue(notificationChannelsSection.isExpandNotificationChannelButtonClickable(),
                "Expand notification channels button should be clickable!");

        addOrEditLauncherPage.clickDeletePresetButton().clickDelete();
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.DELETE_PRESET_POPUP.getDescription(),
                "Popup about deleting is not as expected!");

        softAssert.assertFalse(launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName).isPresent(),
                "Removed preset shouldn't be present in presets list!");
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-3814", "ZTP-3273", "ZTP-3274", "ZTP-3275", "ZTP-3276", "ZTP-3277"})
    public void presetScheduling() {
        String presetName = "To test adding schedules ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();
        SoftAssert softAssert = new SoftAssert();

        createPresetModal.clickScheduleCheckbox();
        softAssert.assertTrue(createPresetModal.isScheduleCheckboxActive(), "Checkbox should be active after clicking on it!");
        softAssert.assertTrue(createPresetModal.getAddScheduleBtn().isStateMatches(Condition.CLICKABLE),
                "'Add schedule' button should be activated after clicking on schedule checkbox!");

        createPresetModal.getScheduleItems().get(0).typeCronExpression(QUARTZ_CRONE_EXPRESSION_INVALID);
        softAssert.assertEquals(createPresetModal.getScheduleItems().get(0).getCronErrorMessage(),
                MessageEnum.ENTER_VALID_EXPRESSION.getDescription(),
                "Error message is not as expected!"); //ZTP-3814

        createPresetModal.getScheduleItems().get(0).typeCronExpression(QUARTZ_CRONE_EXPRESSION_CORRECT);//ZTP-3276
        softAssert.assertFalse(createPresetModal.getScheduleItems().get(0).getCronInputError()
                                                .isStateMatches(Condition.VISIBLE),
                "Error message shouldn't appears when entering valid cron expression!");

        String expectedTimeZone = createPresetModal.getScheduleItems().get(0).selectAnyTimeZone(); //ZTP-3273
        softAssert.assertEquals(createPresetModal.getScheduleItems().get(0).getSelectedTimezone().getText(),
                expectedTimeZone, "Selected timezone is not as expected!");

        createPresetModal.addSchedules(4);
        softAssert.assertEquals(createPresetModal.getScheduleItems().size(), 5, "Numbers of schedules should be 5");
        softAssert.assertFalse(createPresetModal.getAddScheduleBtn().isStateMatches(Condition.VISIBLE),
                "'Add schedule' button should be invisible after adding 5 schedules!");//ZTP-3274

        createPresetModal.getScheduleItems().stream().findAny().get().clickDelete();//ZTP-3275
        softAssert.assertEquals(createPresetModal.getScheduleItems()
                                                 .size(), 4, "Numbers of schedules should be 4 after deleting one");

        createPresetModal
                .typePresetName(presetName)
                .clickCancel();//ZTP-3277
        softAssert.assertFalse(createPresetModal.getNameInput().isStateMatches(Condition.VISIBLE),
                "Create preset modal shouldn't be visible after clicking on 'Cancel'");

        softAssert.assertFalse(launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName).isPresent(),
                "Preset shouldn't be saved in presets after clicking 'Cancel' button!");
        softAssert.assertAll();
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-3278", "ZTP-3279"})
    public void createPresetModalMainElementsPresence() {
        String presetName = "for checking 'Create preset' modal ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(createPresetModal.getModalTitle()
                                                 .getText(), CreatePresetModal.MODAL_NAME, "Modal title is not as expected!");
        softAssert.assertFalse(createPresetModal.isScheduleCheckboxActive(), "By default checkbox should be disabled!");
        softAssert.assertFalse(createPresetModal.getAddScheduleBtn().isStateMatches(Condition.CLICKABLE),
                "'Add schedule' button should be disabled while schedule checkbox disabled!");

        softAssert.assertEquals(createPresetModal.getModalAlertText()
                                                 .getText(), MessageEnum.CREATE_PRESET_MODAL_ALERT_TEXT.getDescription(),
                "Alert message is not as expected");//ZTP-3278

        createPresetModal.getClose().click(); //ZTP-3279
        softAssert.assertFalse(createPresetModal.getNameInput().isStateMatches(Condition.VISIBLE),
                "Create preset modal shouldn't be visible after clicking on 'Close'");

        softAssert.assertFalse(launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName).isPresent(),
                "Preset shouldn't be saved in presets after clicking 'Cancel' button!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-3296", "ZTP-3298", "ZTP-3301", "ZTP-3302"})
    public void switchOnSwitchOffAllSchedulesOnPreset() {
        String presetName = "To test actions with schedules ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();
        SoftAssert softAssert = new SoftAssert();
        createPresetModal.clickScheduleCheckbox();
        createPresetModal.changeFirstScheduleItemCron(QUARTZ_CRONE_EXPRESSION_CORRECT);//need to do it to increase default schedule interval

        for (int i = 0; i < 4; i++) {
            createPresetModal.addSchedule(QUARTZ_CRONE_EXPRESSION_CORRECT);
        }

        createPresetModal
                .typePresetName(presetName)
                .submitModal();
        SchedulesSection schedulesSection = addOrEditLauncherPage.getSelectedLauncherForm().getSchedulesSection();

        schedulesSection.waitUntil(ExpectedConditions.visibilityOf(schedulesSection.getRootExtendedElement()
                                                                                   .getElement()), 5);
        softAssert.assertEquals(schedulesSection.getSavedSchedules()
                                                .size(), 1, "By default user can see only one schedule!");
        schedulesSection.clickMore();
        softAssert.assertEquals(schedulesSection.getSavedSchedules()
                                                .size(), 5, "User should see all schedules after clicking on 'More' button!");
        schedulesSection.clickLess();
        softAssert.assertEquals(schedulesSection.getSavedSchedules()
                                                .size(), 1, "User should see only one schedule after clicking on 'Less' button!");
        schedulesSection.clickMore();

        schedulesSection.clickScheduleCheckbox();//ZTP-3299, 3296
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.SCHEDULES_WAS_SUCCESSFULLY_PAUSED.getDescription(),
                "Popup message(about pausing schedules) is not as expected!");//ZTP-3302

        schedulesSection.getSavedSchedules().forEach(sch ->
                softAssert.assertTrue(sch.isPaused(), "Schedule should be paused on clicking on OFF toggle!"));//ZTP-3302

        Optional<PresetItem> presetItem = addOrEditLauncherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName);
        presetItem.
                ifPresentOrElse(obtainedPreset -> {
                            obtainedPreset.getRootExtendedElement().click();

                            softAssert.assertFalse(obtainedPreset.isScheduleIconPresent(),
                                    "Schedule icon should be invisible after clicking on OFF toggle!");

                            schedulesSection.clickScheduleCheckbox();//ZTP-3296
                            softAssert.assertTrue(
                                    addOrEditLauncherPage.waitIsPopUpMessageAppear(MessageEnum.SCHEDULES_WAS_SUCCESSFULLY_RESUMED.getDescription()),
                                    "Popup message(about resuming schedules) is not as expected!");//ZTP-3301

                            schedulesSection.getSavedSchedules().forEach(sch ->
                                    softAssert.assertTrue(sch.isResumed(), "Schedule should be paused on clicking on ON toggle!"));//ZTP-3301

                            Optional<PresetItem> presetItem1 = addOrEditLauncherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName);
                            presetItem1.ifPresent(presetItem2 -> presetItem2.getRootExtendedElement().click());
                            softAssert.assertTrue(presetItem1.get().isScheduleIconPresent(),
                                    "Schedule icon should be visible after clicking on ON toggle!");

                            if (schedulesSection.isSchedulesCheckboxChecked()) {
                                log.info("Deactivating schedules...");
                                schedulesSection.clickScheduleCheckbox();
                                pause(3);// to pause all schedules
                            }
                            softAssert.assertAll();
                        },
                        () -> {
                            softAssert.fail("No preset with name " + presetName + " for launcher " + LAUNCHER_NAME + " in repo " + REPO_NAME);
                            softAssert.assertAll();
                        });
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-3300", "ZTP-3303", "ZTP-3304", "ZTP-3299"})
    public void pauseAndResumeScheduleOnPreset() {
        WebDriver webDriver = super.getDriver();
        String presetName = "Switch on/off schedules " + RandomStringUtils.randomNumeric(3);

        LauncherPage launcherPage = LauncherPage.openPageDirectly(webDriver, project);
        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(
                            launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME)
                    );

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(webDriver);
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();

        createPresetModal.clickScheduleCheckbox();
        createPresetModal.changeFirstScheduleItemCron(QUARTZ_CRONE_EXPRESSION_CORRECT);//need to do it to increase default schedule interval

        for (int i = 0; i < 4; i++) {
            createPresetModal.addSchedule(QUARTZ_CRONE_EXPRESSION_CORRECT);
        }

        createPresetModal.typePresetName(presetName).submitModal();
        SchedulesSection schedulesSection = addOrEditLauncherPage.getSelectedLauncherForm().getSchedulesSection();

        schedulesSection.clickMore();
        schedulesSection.waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(schedulesSection.getSAVED_SCHEDULES_XPATH()), 1), 5);

        schedulesSection.getSavedSchedules().get(1).clickPause();
        addOrEditLauncherPage.waitPopupDisappears();

        schedulesSection.getSavedSchedules().get(2).clickPause();
        addOrEditLauncherPage.waitPopupDisappears();

        schedulesSection.getSavedSchedules().get(1).clickResume();
        addOrEditLauncherPage.waitPopupDisappears();

        schedulesSection.getSavedSchedules().get(2).clickResume();

        if (schedulesSection.isSchedulesCheckboxChecked()) {
            schedulesSection.clickScheduleCheckbox();

            super.pause(3);
        }
    }

    @Test
    public void userCanLaunchPreset() {
        WebDriver webDriver = super.getDriver();
        String presetName = "Preset to check launch " + RandomStringUtils.randomNumeric(3);

        LauncherPage launcherPage = LauncherPage.openPageDirectly(webDriver, project);
        LauncherItem launcher = launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                                            .orElseThrow(() -> new AssertionError("Could not find launcher with name '" + LAUNCHER_NAME + "'"));
        launcher.getRootExtendedElement().click();

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(webDriver);
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();
        createPresetModal.typePresetName(presetName)
                         .submitModal();
        addOrEditLauncherPage.waitPopupDisappears();

        super.pause(5);

        AutomationLaunchesPage automationLaunchesPage = addOrEditLauncherPage.clickLaunch();
        automationLaunchesPage.assertPageOpened(Duration.ofSeconds(5));
        Assert.assertTrue(automationLaunchesPage.isCertainLaunchAppears(presetName), String.format("Launch with name '%s' should appear on Launches page!", presetName));
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    @TestCaseKey({"ZTP-3293", "ZTP-3294", "ZTP-3295", "ZTP-6507"})
    public void verificationActiveAndInActiveFieldsAfterCreatingPreset() {
        WebDriver webDriver = super.getDriver();
        String presetName = "verification active and inactive fields " + RandomStringUtils.randomNumeric(3);

        LauncherPage launcherPage = LauncherPage.openPageDirectly(webDriver, project);

        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .map(AbstractUIObject::getRootExtendedElement)
                    .ifPresentOrElse(
                            ExtendedWebElement::click,
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME)
                    );

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(webDriver);
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();

        Assert.assertEquals(
                createPresetModal.getModalTitle().getText(), CreatePresetModal.MODAL_NAME,
                "Modal title is not as expected!"
        );

        createPresetModal.typePresetName(presetName).submitModal();
        Assert.assertEquals(
                addOrEditLauncherPage.getPopUp(), "Preset was successfully created",
                "Popup about creating preset is not as expected!"
        );

        launcherPage.getPresetWithName(REPO_NAME, LAUNCHER_NAME, presetName)
                    .map(AbstractUIObject::getRootExtendedElement)
                    .ifPresentOrElse(
                            ExtendedWebElement::click,
                            () -> Assert.fail("Unable to find preset with name " + presetName)
                    );

        NotificationChannelsSection notificationChannels = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                                .getNotificationChannelsSection();
        Assert.assertTrue(
                notificationChannels.isExpandNotificationChannelButtonClickable(),
                "Expand notification channels button should be clickable!"
        );

        notificationChannels.expandChannel();
        Assert.assertTrue(notificationChannels.isEmailsFieldVisible(), "Emails input field should be disabled!");
        Assert.assertTrue(notificationChannels.isEmailsFieldDisabled(), "Emails input field should be disabled!");

        Assert.assertTrue(
                addOrEditLauncherPage.getSelectedLauncherForm().getSelectedBranchSection().isBranchFieldDisabled(),
                "Branch should be disabled!"
        );
        Assert.assertTrue(
                addOrEditLauncherPage.getSelectedLauncherForm()
                                     .getTestingPlatformSection()
                                     .isSelectedTestingPlatformDisabled(),
                "Selected testing platform should be disabled!"
        );
        ExecutionEnvSection executionEnvSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                       .getExecutionEnvSection();
        Assert.assertFalse(executionEnvSection.isLaunchCommandVisible(), "By default launch command should be invisible!");
        Assert.assertFalse(executionEnvSection.isDockerImageVisible(), "By default launch command should be invisible!");

        executionEnvSection.clickExpandButton();
        Assert.assertTrue(executionEnvSection.isLaunchCommandVisible(), "After click on expand button launch command should be visible!");
        Assert.assertFalse(executionEnvSection.isLaunchCommandClickable(), "After click on expand button launch command should be not clickable!");

        Assert.assertTrue(executionEnvSection.isDockerImageVisible(), "After click on expand docker image should be visible!");
        Assert.assertFalse(executionEnvSection.isDockerImageClickable(), "After click on expand docker image should be not clickable!");

        executionEnvSection.clickExpandButton();
        Assert.assertFalse(executionEnvSection.isLaunchCommandVisible(), "After click on expand button launch command should be invisible!");
        Assert.assertFalse(executionEnvSection.isDockerImageVisible(), "After click on expand docker image should be invisible!");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-6228", "ZTP-6229"})
    public void verifyUserCanCancelCreateLauncherPresetViaEscAndViaCancelButton() {
        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        AddOrEditLauncherPage addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());

        for (int i = 0; i < 2; i++) {
            CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();

            if (i == 0) {
                createPresetModal.clickCancel();
                softAssert.assertFalse(createPresetModal.isModalOpened(),
                        "Modal should be closed after clicking 'Cancel' button !");
            } else {
                PageUtil.guaranteedToHideDropDownList(getDriver());
                softAssert.assertFalse(createPresetModal.isModalOpened(),
                        "Modal should be closed after clicking 'Esc' key !");
            }
        }

        softAssert.assertAll();
    }
}
