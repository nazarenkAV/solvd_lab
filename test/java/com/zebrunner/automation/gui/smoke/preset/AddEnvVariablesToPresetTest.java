package com.zebrunner.automation.gui.smoke.preset;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.CustomCapabilitiesSection;
import com.zebrunner.automation.gui.launcher.CustomVariableAddingForm;
import com.zebrunner.automation.gui.launcher.EnvVariablesSection;
import com.zebrunner.automation.gui.launcher.ExecutionEnvSection;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.launcher.NotificationChannelsSection;
import com.zebrunner.automation.gui.launcher.SchedulesSection;
import com.zebrunner.automation.gui.launcher.SelectedLauncherForm;
import com.zebrunner.automation.gui.launcher.preset.CreatePresetModal;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
@Maintainer("obabich")
public class AddEnvVariablesToPresetTest extends LogInBase {
    private final String REPO_NAME = "dikazak/carina-demo";
    private final String LAUNCHER_NAME = "Env variables test";
    private AddOrEditLauncherPage addOrEditLauncherPage;
    private Project project;
    private Long projectId;
    private String createdPresetName;
    private Launcher createdLauncher;
    private Long createdRepoId;

    @BeforeClass
    public void createLauncher() {
        project = LogInBase.project;
        projectId = LogInBase.project.getId();
        createdRepoId = LogInBase.repositoryId;

        createdLauncher = launcherService.addDefaultApiTestsLauncher(projectId, createdRepoId, LAUNCHER_NAME, "api");
    }

    @BeforeMethod(onlyForGroups = "launcher and preset pre-created")
    public void createPreset() {
        createdPresetName = "Pre-created preset ".concat(RandomStringUtils.randomNumeric(5));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        // launcherPage.waitUntilStalenessOfPage();

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


    // ========================================= Tests ================================================ //

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3337"})
    public void userCanAddAndDeleteIntegerEnvVariable() {
        CustomVariable integerVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.INTEGER);
        integerVar.setDefaultValue(1 + RandomStringUtils.randomNumeric(5));

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                             .createNewEnvVariable(integerVar);

        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getEnvVariablesValues()
                                                   .contains(integerVar.getName().concat("=")
                                                                       .concat(integerVar.getDefaultValue())),
                "Env variable is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm envVar = addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                                                               .getEnvVariableWithName(integerVar.getName());

        softAssert = envVar.assertCreatedVariable(softAssert, integerVar);
        envVar.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInEnvVariablesList(integerVar.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3338", "ZTP-3346"})
    public void userCanAddAndDeleteStringEnvVariableTest() {
        CustomVariable stringVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        stringVar.setDefaultValue("PRIORITY=>P0||P1 latin letters numbers and use spaces");

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                             .createNewEnvVariable(stringVar);

        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getEnvVariablesValues()
                                                   .contains(stringVar.getName().concat("=")
                                                                      .concat(stringVar.getDefaultValue())),
                "Env variable is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm envVar = addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                                                               .getEnvVariableWithName(stringVar.getName());

        softAssert = envVar.assertCreatedVariable(softAssert, stringVar);
        envVar.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInEnvVariablesList(stringVar.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3345"})
    public void userCanAddAndDeleteBooleanEnvVariableTest() {
        CustomVariable stringVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        stringVar.setDefaultValue("false");

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                             .createNewEnvVariable(stringVar);

        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getEnvVariablesValues()
                                                   .contains(stringVar.getName().concat("=")
                                                                      .concat(stringVar.getDefaultValue())),
                "Env variable is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm envVar = addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                                                               .getEnvVariableWithName(stringVar.getName());

        softAssert = envVar.assertCreatedVariable(softAssert, stringVar);
        envVar.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInEnvVariablesList(stringVar.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3347 "})
    public void userCanAddAndDeleteChoiceEnvVariableTest() {
        List<String> listOfValues = List.of("value1", "value2", "Latin letters, numbers(123) and spaces");

        CustomVariable choiceVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.CHOICE);
        choiceVar.setDefaultValue(null);
        choiceVar.setValues(listOfValues);

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                             .createNewEnvVariableNameLast(choiceVar);// !!! when bug https://solvd.atlassian.net/browse/ZEB-5723
        // will be fixed should be replaced on createNewEnvVariable()
        choiceVar.setDefaultValue(listOfValues.get(0));//by default, we use the first value of list
        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getEnvVariablesValues()
                                                   .contains(choiceVar.getName().concat("=")
                                                                      .concat(choiceVar.getDefaultValue())),
                "Env variable is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm envVar = addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                                                               .getEnvVariableWithName(choiceVar.getName());

        softAssert = envVar.assertCreatedVariable(softAssert, choiceVar);
        envVar.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInEnvVariablesList(choiceVar.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    public void addedToLauncherEnvVariablesAppearInPresetTest() {
        CustomVariable integerVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.INTEGER);
        integerVar.setDefaultValue(1 + RandomStringUtils.randomNumeric(5));
        CustomVariable booleanVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        booleanVar.setDefaultValue("false");

        ArrayList<CustomVariable> launcherVariables = new ArrayList(Arrays.asList(
                CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING),
                booleanVar, integerVar,
                CustomVariable.getRandomChoiceVariable()));

        String launcherName = "Pre-created launcher ".concat(RandomStringUtils.randomNumeric(5));
        Launcher launcher = launcherService.addDefaultApiTestsLauncher(projectId, createdRepoId, launcherName, "api");

        createdPresetName = "Pre-created preset ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        // launcherPage.waitUntilStalenessOfPage();

        launcherPage.getLauncherWithName(REPO_NAME, launcherName)
                    .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                            () -> Assert.fail("No launcher with name " + launcherName));

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        CreatePresetModal createPresetModal = addOrEditLauncherPage.clickSavePresetButton();
        createPresetModal
                .typePresetName(createdPresetName)
                .submitModal();
        launcherPage.waitUntilStalenessOfPage();
        launcherPage.getPresetWithName(REPO_NAME, launcherName, createdPresetName)
                    .ifPresentOrElse(presetItem -> presetItem.getRootExtendedElement().click(),
                            () -> {
                                softAssert.fail("Unable to find preset with name " + createdPresetName);
                                softAssert.assertAll();
                            });

        CustomVariable presetStringVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);

        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                             .createNewEnvVariable(presetStringVar);
        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getEnvVariablesValues()
                                                   .contains(presetStringVar.getName().concat("=")
                                                                            .concat(presetStringVar.getDefaultValue())),
                "Env variable is not as created!");
        addOrEditLauncherPage.getLauncherWithName(REPO_NAME, launcherName)
                             .ifPresentOrElse(launcherItem -> launcherItem.clickOnLauncherName(),
                                     () -> Assert.fail("No launcher with name " + launcherName));
        addOrEditLauncherPage.clickChangeDefaultsButton();

        launcherVariables.add(presetStringVar);
        launcherVariables.forEach(customVariable -> addOrEditLauncherPage.getSelectedLauncherForm()
                                                                         .getEnvVariablesSection()
                                                                         .createNewEnvVariableNameLast(customVariable));// !!! when bug https://solvd.atlassian.net/browse/ZEB-5723
        // will be fixed should be replaced on createNewEnvVariable()
        addOrEditLauncherPage.clickSaveButton();
        pause(3);
        addOrEditLauncherPage.getPresetWithName(REPO_NAME, launcherName, createdPresetName)
                             .ifPresentOrElse(presetItem -> presetItem.getRootExtendedElement().click(),
                                     () -> {
                                         softAssert.fail("Unable to find preset with name " + createdPresetName);
                                         softAssert.assertAll();
                                     });

        addOrEditLauncherPage.clickEditPresetButton();
        launcherVariables.forEach(customVariable ->
        {
            softAssert.assertTrue(addOrEditLauncherPage.isVariablePresentInEnvVariablesList(customVariable.getName()),
                    String.format("Env variable '%s' should be in preset variables!", customVariable.getName()));
            addOrEditLauncherPage.getEnvVariableWithName(customVariable.getName())
                                 .assertCreatedVariable(softAssert, customVariable);
        });
        launcherService.deleteLauncher(projectId, createdRepoId, launcher.getId());
        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-6164")
    @Test(groups = "launcher and preset pre-created")
    public void verifyInstanceTypeIsNotChangingAfterDeleteOrAddingEnvironmentOrCustomCapability() {
        Artifact.attachReferenceToTest("ZEB-6828", "https://solvd.atlassian.net/browse/ZEB-6828");

        String instanceType = "Large (4 CPU, 4 GB RAM)";

        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.findAndChooseExecutionInstanceType(instanceType);

        SelectedLauncherForm launcherForm = addOrEditLauncherPage.getSelectedLauncherForm();
        ExecutionEnvSection executionEnvSection = launcherForm.getExecutionEnvSection();

        CustomVariableAddingForm customVariableAddingForm = addOrEditLauncherPage.createNewEnvironmentVariable();
        Assert.assertEquals(
                executionEnvSection.getInstanceType(), instanceType,
                "Instance type is not as expected after adding environment variable!"
        );

        customVariableAddingForm.deleteVariable();
        Assert.assertEquals(
                executionEnvSection.getInstanceType(), instanceType,
                "Instance type is not as expected after deleting environment variable!"
        );

        customVariableAddingForm = launcherForm.getCustomCapabilitiesSection().createNewCapability();
        Assert.assertEquals(
                executionEnvSection.getInstanceType(), instanceType,
                "Instance type is not as expected after adding custom capability!"
        );

        customVariableAddingForm.deleteVariable();
        Assert.assertEquals(
                executionEnvSection.getInstanceType(), instanceType,
                "Instance type is not as expected after deleting custom capability!"
        );
    }

    @TestCaseKey("ZTP-6165")
    @Test(groups = "launcher and preset pre-created")
    public void verifyScheduleIsNotDisappearAfterDeleteOrAddingEnvironmentOrCustomCapability() {
        Artifact.attachReferenceToTest("ZEB-6828", "https://solvd.atlassian.net/browse/ZEB-6828");

        addOrEditLauncherPage.clickEditPresetButton();

        SelectedLauncherForm launcherForm = addOrEditLauncherPage.getSelectedLauncherForm();
        SchedulesSection schedulesSection = launcherForm.getSchedulesSection();

        schedulesSection.clickAddScheduleButton();

        CustomVariableAddingForm customVariableAddingForm = addOrEditLauncherPage.createNewEnvironmentVariable();
        super.pause(3);
        Assert.assertEquals(
                schedulesSection.getScheduleItems().size(), 1,
                "Schedule should not disappear after adding environment variable"
        );

        customVariableAddingForm.deleteVariable();
        super.pause(3);

        Assert.assertEquals(
                schedulesSection.getScheduleItems().size(), 1,
                "Schedule should not disappear after deleting environment variable"
        );

        customVariableAddingForm = launcherForm.getCustomCapabilitiesSection().createNewCapability();
        super.pause(3);

        Assert.assertEquals(
                schedulesSection.getScheduleItems().size(), 1,
                "Schedule should not disappear after adding custom capability"
        );

        customVariableAddingForm.deleteVariable();
        super.pause(3);

        Assert.assertEquals(
                schedulesSection.getScheduleItems().size(), 1,
                "Schedule should not disappear after deleting custom capability"
        );
    }

    @Test(groups = "launcher and preset pre-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-6212", "ZTP-6214", "ZTP-6219"})
    public void verifyUserCanChangeEnvironmentVariableAndCustomCapabilityDefaultValueWithArrows() {
        SoftAssert softAssert = new SoftAssert();

        addOrEditLauncherPage.clickEditPresetButton();

        BiConsumer<String, CustomVariableAddingForm> verifyIncreaseAndDecrease = (section, form) -> {
            log.info("Verifying section {}", section);

            String type = "INTEGER";
            form.selectType(type);

            softAssert.assertEquals(form.getVarType(), type, "Type is not as excepted !"); //ZTP-6219
            // User can select 'Integer values type' on Environment variables


            String initialDefaultValue = form.getDefaultValue();
            form.clickIntegerValueIncreasingArrow();
            softAssert.assertEquals(form.getDefaultValue(), String.valueOf(Integer.parseInt(initialDefaultValue) + 1),
                    section + " default value is not as expected after increasing!");

            form.clickIntegerValueDecreaseArrow();
            softAssert.assertEquals(form.getDefaultValue(), initialDefaultValue,
                    section + " default value is not as expected after decreasing!");

            form.deleteVariable();
        };

        CustomVariableAddingForm environmentVariableForm = addOrEditLauncherPage.createNewEnvironmentVariable();
        verifyIncreaseAndDecrease.accept(EnvVariablesSection.ENVIRONMENT_VARIABLE_SECTION, environmentVariableForm);

        CustomVariableAddingForm customCapabilityForm = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                             .getCustomCapabilitiesSection()
                                                                             .createNewCapability();
        verifyIncreaseAndDecrease.accept(CustomCapabilitiesSection.CUSTOM_CAPABILITY_SECTION, customCapabilityForm);

        softAssert.assertAll();
    }

    @Test(groups = "launcher and preset pre-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-6218", "ZTP-6224"})
    public void verifyUserCanEnterLatinLettersNumbersAndSpacesInEnvironmentVariableAndCustomCapabilityDefaultValueFields() {
        SoftAssert softAssert = new SoftAssert();

        addOrEditLauncherPage.clickEditPresetButton();

        BiConsumer<String, CustomVariableAddingForm> verifyDefaultValueInputField = (section, form) -> {
            log.info("Verifying section {}", section);

            form.typeVariableName(RandomStringUtils.randomAlphabetic(3));

            form.typeDefaultValue(RandomStringUtils.randomAlphabetic(3) + "  " +
                    RandomStringUtils.randomNumeric(3));

            pause(1);

            softAssert.assertFalse(form.isDefaultValueInputErrorMessagePresent(),
                    "No error message should be present bellow " + section + " Default value input !");

            form.deleteVariable();
        };

        CustomVariableAddingForm environmentVariableForm = addOrEditLauncherPage.createNewEnvironmentVariable();
        verifyDefaultValueInputField.accept(EnvVariablesSection.ENVIRONMENT_VARIABLE_SECTION, environmentVariableForm);

        CustomVariableAddingForm customCapabilityForm = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                             .getCustomCapabilitiesSection()
                                                                             .createNewCapability();
        verifyDefaultValueInputField.accept(CustomCapabilitiesSection.CUSTOM_CAPABILITY_SECTION, customCapabilityForm);

        softAssert.assertAll();
    }

    @Test(groups = "launcher and preset pre-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-6220", "ZTP-6221"})
    public void verifyUserCanChangeEnvironmentVariableAndCustomCapabilityDefaultValueViaKeyBoard() {
        SoftAssert softAssert = new SoftAssert();

        addOrEditLauncherPage.clickEditPresetButton();

        BiConsumer<String, CustomVariableAddingForm> verifyInputChangeViaKeyBoard = (section, form) -> {
            log.info("Verifying section {}", section);

            form.selectType("INTEGER");

            String randomNumber = RandomStringUtils.randomNumeric(3);
            form.typeDefaultValue(randomNumber);

            softAssert.assertEquals(form.getDefaultValue(), String.valueOf(Integer.parseInt(randomNumber)),
                    section + " default value is not as excepted !");

            form.deleteVariable();
        };

        CustomVariableAddingForm environmentVariableForm = addOrEditLauncherPage.createNewEnvironmentVariable();
        verifyInputChangeViaKeyBoard.accept(EnvVariablesSection.ENVIRONMENT_VARIABLE_SECTION, environmentVariableForm);

        CustomVariableAddingForm customCapabilityForm = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                             .getCustomCapabilitiesSection()
                                                                             .createNewCapability();
        verifyInputChangeViaKeyBoard.accept(CustomCapabilitiesSection.CUSTOM_CAPABILITY_SECTION, customCapabilityForm);

        softAssert.assertAll();
    }

    @Test(groups = "launcher and preset pre-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-6227", "ZTP-6222", "ZTP-6225", "ZTP-6223", "ZTP-6226"})
    public void verifyRequiredFieldsAndUserCantSaveWithEmptyTitleOrEnvironmentVariableOrCustomCapabilityFields() {
        SoftAssert softAssert = new SoftAssert();

        addOrEditLauncherPage.clickEditPresetButton();

        BiConsumer<String, CustomVariableAddingForm> verifyInputChangeViaKeyBoard = (section, form) -> {
            log.info("Verifying section {}", section);

            softAssert.assertTrue(form.getDefaultValueLabelText().contains("*"), section +
                    " default value label should contain '*' symbol to identify as required field !");
            softAssert.assertTrue(form.getVariableNameLabelText().contains("*"), section +
                    " variable name label should contain '*' symbol to identify as required field !");
            softAssert.assertTrue(form.getTypeLabelText().contains("*"), section +
                    " type label should contain '*' symbol to identify as required field !");
            softAssert.assertFalse(addOrEditLauncherPage.isSaveButtonClickable(),
                    "Save button shouldn't be clickable when " + section + " is empty !");

            form.deleteVariable();
        };

        CustomVariableAddingForm environmentVariableForm = addOrEditLauncherPage.createNewEnvironmentVariable();
        verifyInputChangeViaKeyBoard.accept(EnvVariablesSection.ENVIRONMENT_VARIABLE_SECTION, environmentVariableForm);

        CustomVariableAddingForm customCapabilityForm = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                             .getCustomCapabilitiesSection()
                                                                             .createNewCapability();
        verifyInputChangeViaKeyBoard.accept(CustomCapabilitiesSection.CUSTOM_CAPABILITY_SECTION, customCapabilityForm);

        addOrEditLauncherPage.clearLauncherInput();

        softAssert.assertFalse(addOrEditLauncherPage.isSaveButtonClickable(),
                "Save button shouldn't be clickable when title is empty !");

        softAssert.assertAll();
    }

    @Test(groups = "launcher and preset pre-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-6243")
    public void verifyUserCanExpandCollapseNotificationChannels() {
        SoftAssert softAssert = new SoftAssert();

        addOrEditLauncherPage.clickEditPresetButton();

        NotificationChannelsSection notificationChannelsSection = addOrEditLauncherPage.getSelectedLauncherForm()
                                                                                       .getNotificationChannelsSection();

        notificationChannelsSection.expandChannel();

        softAssert.assertTrue(notificationChannelsSection.isEmailsFieldVisible(),
                "Notification channel should be expanded  and email field should be visible !");

        notificationChannelsSection.collapseChannel();

        softAssert.assertFalse(notificationChannelsSection.isEmailsFieldVisible(),
                "Notification channel should be collapsed and email field shouldn't be visible !");

        softAssert.assertAll();
    }
}
