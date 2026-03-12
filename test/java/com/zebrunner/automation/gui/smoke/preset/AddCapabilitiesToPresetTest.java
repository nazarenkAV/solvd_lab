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

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.CustomVariableAddingForm;
import com.zebrunner.automation.gui.launcher.LauncherItem;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
@Maintainer("obabich")
public class AddCapabilitiesToPresetTest extends LogInBase {
    private final String REPO_NAME = "dikazak/carina-demo";
    private final String LAUNCHER_NAME = "Capabilities test";
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

        createdLauncher = launcherService.addDefaultApiTestsLauncher(projectId, createdRepoId, LAUNCHER_NAME, "api");
    }

    @BeforeMethod(onlyForGroups = "launcher and preset pre-created")
    public void createPreset() {
        createdPresetName = "Pre-created preset ".concat(RandomStringUtils.randomNumeric(3));
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        launcherPage.waitUntilStalenessOfPage();

        launcherPage.getLauncherWithName(REPO_NAME, LAUNCHER_NAME)
                .ifPresentOrElse(LauncherItem::clickOnLauncherName,
                        () -> Assert.fail("No launcher with name " + LAUNCHER_NAME));

        addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        addOrEditLauncherPage.savePreset(createdPresetName);

        SoftAssert softAssert = new SoftAssert();
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

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3463", "ZTP-3464", "ZTP-3466"})
    public void userCanAddAndDeleteCapabilityWithIntegerType() {
        CustomVariable integerVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.INTEGER);
        integerVar.setDefaultValue(1 + RandomStringUtils.randomNumeric(5));

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .createNewCapability(integerVar);

        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getCapabilityVariablesValues()
                        .contains(integerVar.getName().concat("=").concat(integerVar.getDefaultValue())),
                "Capability is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm capability = addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                                                                   .getCapabilityWithName(integerVar.getName());

        softAssert = capability.assertCreatedVariable(softAssert, integerVar);
        capability.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInCapabilitiesList(integerVar.getName()),
                "Capability shouldn't be present in variables!");
        softAssert.assertAll();
    }

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3468", "ZTP-3471", "ZTP-3472", "ZTP-3473"})
    public void userCanAddAndDeleteCapabilityWithStringType() {
        CustomVariable stringVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        stringVar.setDefaultValue("PRIORITY=>P0||P1 latin letters, numbers and use spaces");

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .createNewCapability(stringVar);

        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getCapabilityVariablesValues()
                        .contains(stringVar.getName().concat("=").concat(stringVar.getDefaultValue())),
                "Capability is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm capability = addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .getCapabilityWithName(stringVar.getName());

        softAssert = capability.assertCreatedVariable(softAssert, stringVar);
        capability.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInCapabilitiesList(stringVar.getName()),
                "Capability shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3467", "ZTP-3469"})
    public void userCanAddAndDeleteCapabilityWithBooleanType() {
        CustomVariable stringVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        stringVar.setDefaultValue("false");

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm var = addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .createNewCapability(stringVar);

        var.clickTrue();// ZTP-3469 User can select False/True in 'Boolean Type' Custom capabilities
        softAssert.assertTrue(var.isActiveTrue(), "True should be active after clicking on 'True' checkbox!");

        var.clickFalse();
        softAssert.assertTrue(var.isActiveFalse(), "False should be active after clicking on 'False' checkbox!");
        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getCapabilityVariablesValues()
                        .contains(stringVar.getName().concat("=").concat(stringVar.getDefaultValue())),
                "Capability is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm capability = addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .getCapabilityWithName(stringVar.getName());

        softAssert = capability.assertCreatedVariable(softAssert, stringVar);
        capability.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInCapabilitiesList(stringVar.getName()),
                "Capability shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"launcher and preset pre-created", "min_acceptance"})
    @TestCaseKey({"ZTP-3465", "ZTP-3473"})
    public void userCanAddAndDeleteCapabilityWithChoiceType() {
        List<String> listOfValues = List.of("value1", "value2", "Latin letters, numbers(123) and spaces");

        CustomVariable choiceVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.CHOICE);
        choiceVar.setDefaultValue(listOfValues.get(0));
        choiceVar.setValues(listOfValues);

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .createNewCapability(choiceVar);//ZEB-3473

        choiceVar.setDefaultValue(listOfValues.get(0));//by default, we use the first value of list
        addOrEditLauncherPage.clickSaveButton();

        softAssert.assertTrue(addOrEditLauncherPage.getCapabilityVariablesValues()
                        .contains(choiceVar.getName().concat("=").concat(choiceVar.getDefaultValue())),
                "Capability is not as created!");

        addOrEditLauncherPage.clickEditPresetButton();
        CustomVariableAddingForm capability = addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .getCapabilityWithName(choiceVar.getName());

        softAssert = capability.assertCreatedVariable(softAssert, choiceVar);
        capability.deleteVariable();

        pause(1);
        softAssert.assertFalse(addOrEditLauncherPage.isVariablePresentInCapabilitiesList(choiceVar.getName()),
                "Capability shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    public void addedToLauncherCapabilitiesAppearInPreset() {
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
        launcherPage.waitUntilStalenessOfPage();

        launcherPage.getLauncherWithName(REPO_NAME, launcherName)
                .ifPresentOrElse(launcherItem -> launcherItem.getRootExtendedElement().click(),
                        () -> Assert.fail("No launcher with name " + launcherName));

        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());
        addOrEditLauncherPage.savePreset(createdPresetName);

        launcherPage.waitUntilStalenessOfPage();
        launcherPage.getPresetWithName(REPO_NAME, launcherName, createdPresetName)
                .ifPresentOrElse(presetItem -> presetItem.getRootExtendedElement().click(),
                        () -> {
                            softAssert.fail("Unable to find preset with name " + createdPresetName);
                            softAssert.assertAll();
                        });

        CustomVariable presetStringVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);

        addOrEditLauncherPage.clickEditPresetButton();
        addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .createNewCapability(presetStringVar);
        addOrEditLauncherPage.clickSaveButton();

        addOrEditLauncherPage.getLauncherWithName(REPO_NAME, launcherName)
                .ifPresentOrElse(launcherItem -> launcherItem.clickOnLauncherName(),
                        () -> Assert.fail("No launcher with name " + launcherName));
        addOrEditLauncherPage.clickChangeDefaultsButton();

        launcherVariables.add(presetStringVar);
        launcherVariables.forEach(customVariable -> addOrEditLauncherPage.getSelectedLauncherForm().getCustomCapabilitiesSection()
                .createNewCapability(customVariable));
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
            softAssert.assertTrue(addOrEditLauncherPage.isVariablePresentInCapabilitiesList(customVariable.getName()),
                    String.format("Capability '%s' should be in preset variables!", customVariable.getName()));
            addOrEditLauncherPage.getCapabilityWithName(customVariable.getName())
                    .assertCreatedVariable(softAssert, customVariable);
        });
        launcherService.deleteLauncher(projectId, createdRepoId, launcher.getId());
        softAssert.assertAll();
    }

}
