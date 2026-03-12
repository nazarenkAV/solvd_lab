package com.zebrunner.automation.gui.smoke.launcher;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.CustomCapabilitiesModal;
import com.zebrunner.automation.gui.launcher.CustomVariableAddingForm;
import com.zebrunner.automation.gui.launcher.EnvironmentVariablesModal;
import com.zebrunner.automation.gui.launcher.ExecutionEnvSection;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.legacy.DockerImageEnum;
import com.zebrunner.automation.legacy.InstanceTypeEnum;
import com.zebrunner.automation.legacy.LauncherDataEnum;
import com.zebrunner.automation.legacy.RepoTypeEnum;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
@Maintainer("obabich")
public class LauncherVariablesTest extends LogInBase {

    private final String publicRepo = "dikazak/public-repository";
    private Project project;

    @BeforeTest()
    public void createProject() {
        project = LogInBase.project;
        launcherService.addGitRepo(
                project.getId(),
                ConfigHelper.getGithubProperties().getUrl() + "/" + publicRepo,
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken(),
                GitProvider.GITHUB.toString()
        );
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1872", "ZTP-1873", "ZTP-1874", "ZTP-1882"})
    public void addAndDeleteChoiceEnvVariable() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_CHOICE);

        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for choice env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.CHOICE);

        CustomVariableAddingForm capabilityVar = addLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                                                                .createNewEnvVariableNameLast(
                        var);// !!! when bug https://solvd.atlassian.net/browse/ZEB-5723 will be fixed should be replaced on createNewEnvVariable()
        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getEnvVariableWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }

        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.getEnvVariableWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey({"ZTP-1879"})
    public void editChoiceEnvVariableWithListOfValue() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_CHOICE);

        List<String> listOfValues = List.of("value1=1", "value2", "value3");
        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.CHOICE);
        var.setDefaultValue(null);
        var.setValues(listOfValues);

        CustomVariableAddingForm capabilityVar = addLauncherPage.getSelectedLauncherForm().getEnvVariablesSection()
                .createNewEnvVariableNameLast(var);// !!! when bug https://solvd.atlassian.net/browse/ZEB-5723
        // will be fixed should be replaced on createNewEnvVariable()

        var.setDefaultValue(listOfValues.get(0));//by default, we use the first value of list

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        String envVars = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().getEnvVariables();
        softAssert.assertEquals(envVars, var.getName().concat("=").concat(var.getDefaultValue()), "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getEnvVariableWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
            softAssert.assertEquals(modal.getEnvVariableWithName(var.getName()).get().getChoiceValues(), var.getValues(),
                    "List existing values of capability is not as expected!");
            String newDefaultValue = listOfValues.get(listOfValues.size() - 1);
            var.setDefaultValue(newDefaultValue);
            modal.getEnvVariableWithName(var.getName()).get().selectChoiceValueFromList(newDefaultValue);
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.submitModal();

        envVars = addLauncherPage.getEnvVariablesValues();
        softAssert.assertEquals(envVars, var.getName().concat("=").concat(var.getDefaultValue()), "After updating env vars is not as created!");

        addLauncherPage.clickChangeDefaultsButton();
        softAssert.assertEquals(addLauncherPage.getEnvVariableWithName(var.getName()).getDefaultValue(), var.getDefaultValue(),
                "After updating default value of capability is not as expected!");

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1879"})
    public void editChoiceCapabilityVariableWithListOfValue() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.CAPABILITIES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_CHOICE);

        List<String> listOfValues = List.of("value ".concat(RandomStringUtils.randomNumeric(3)),
                "value ".concat(RandomStringUtils.randomNumeric(3)),
                "value ".concat(RandomStringUtils.randomNumeric(3)));
        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.CHOICE);
        var.setDefaultValue(null);
        var.setValues(listOfValues);

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewCapability(var);
        var.setDefaultValue(listOfValues.get(0));//by default, we use the first value of list

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        CustomCapabilitiesModal modal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getCapabilityWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getCapabilityWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
            softAssert.assertEquals(modal.getCapabilityWithName(var.getName()).get().getChoiceValues(), var.getValues(),
                    "List existing values of capability is not as expected!");
            String newDefaultValue = listOfValues.get(listOfValues.size() - 1);
            var.setDefaultValue(newDefaultValue);
            modal.getCapabilityWithName(var.getName()).get().selectChoiceValueFromList(newDefaultValue);
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.submitModal();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "After updating env vars is not as created!");

        addLauncherPage.clickChangeDefaultsButton();
        softAssert.assertEquals(addLauncherPage.getCapabilityWithName(var.getName()).getDefaultValue(), var.getDefaultValue(),
                "After updating default value of capability is not as expected!");

        softAssert.assertAll();
    }

    @Test()
    @Maintainer("obabich")
    @TestCaseKey({"ZTP-1888", "ZTP-1887", "ZTP-1886", "ZTP-1896", "ZTP-3494"})
    public void addAndDeleteChoiceCapability() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.CAPABILITIES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_CHOICE);
        Label.attachToTest(TestLabelsConstant.BUG, "ZEB-5723");

        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for choice capability".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.CHOICE);

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewCapability(var);

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in capability variables! ");

        softAssert.assertTrue(addLauncherPage.isAddButtonClickable(),
                "'Add' button should be clickable after adding choice capability should be clickable!");//ZTP-3494
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Capability is not as created!");

        CustomCapabilitiesModal modal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).isPresent(), "Capability should be present on modal!");

        if (modal.getCapabilityWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getCapabilityWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in the list of variables! ");
        addLauncherPage
                .getCapabilityWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability shouldn't be present in the list of variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1891", "ZTP-1887", "ZTP-1886", "ZTP-1894", "ZTP-1895"})
    @Maintainer("obabich")
    public void addAndDeleteStringCapability() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.CAPABILITIES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_STRING);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string capability ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewCapability(var);

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in capability variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Capability is not as created!");

        CustomCapabilitiesModal modal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).isPresent(), "Capability should be present on modal!");

        if (modal.getCapabilityWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getCapabilityWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in the list of variables! ");
        addLauncherPage.getCapabilityWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability shouldn't be present in the list of variables after deleting! ");
        softAssert.assertAll();
    }

    @Test()
    @Maintainer("obabich")
    @TestCaseKey({"ZTP-1872", "ZTP-1873", "ZTP-1877", "ZTP-1880", "ZTP-1881", "ZTP-3481"})
    public void addAndDeleteStringEnvVariableWithUnderscoreSymbol() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_STRING);
        Label.attachToTest(TestLabelsConstant.BUG, "ZEB-5752");
        Artifact.attachReferenceToTest("ZEB-5752", "https://solvd.atlassian.net/browse/ZEB-5752");

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        var.setDefaultValue(var.getDefaultValue() + "_env_With_underscore_");
        var.setName(var.getName() + "_env_With_underscore_");

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewEnvVariable(var);

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getEnvVariableWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage
                .getEnvVariableWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1889", "ZTP-1886", "ZTP-1887"})
    @Maintainer("obabich")
    public void addAndDeleteIntegerCapability() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.CAPABILITIES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_INTEGER);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string capability ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.INTEGER);
        var.setDefaultValue(1 + RandomStringUtils.randomNumeric(5));

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewCapability(var);

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in capability variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Capability is not as created!");

        CustomCapabilitiesModal modal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).isPresent(), "Capability should be present on modal!");

        if (modal.getCapabilityWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getCapabilityWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }

        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in the list of variables! ");
        addLauncherPage
                .getCapabilityWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability shouldn't be present in the list of variables after deleting! ");
        softAssert.assertAll();
    }

    @Test()
    @Maintainer("obabich")
    @TestCaseKey({"ZTP-1872", "ZTP-1873", "ZTP-1875"})
    public void addAndDeleteIntegerEnvVariable() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_INTEGER);

        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.INTEGER);
        var.setDefaultValue(1 + RandomStringUtils.randomNumeric(3));

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewEnvVariable(var);

        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getEnvVariableWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }

        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage
                .getEnvVariableWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1872", "ZTP-1873", "ZTP-1876"})
    public void addAndDeleteBooleanEnvVariable() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_BOOLEAN);

        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        var.setDefaultValue("false");

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewEnvVariable(var);
        softAssert.assertEquals(capabilityVar.getVarType(), var.getType().name(), "Type is not as expected!");

        softAssert.assertTrue(capabilityVar.isActiveFalse(), "False should be active!");
        softAssert.assertFalse(capabilityVar.isActiveTrue(), "True should not be active!");

        softAssert.assertEquals(capabilityVar.getVariableName(), var.getName(), "Var name is not as expected!");
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).get().isActiveFalse(),
                    "False should be active!");
            softAssert.assertFalse(modal.getEnvVariableWithName(var.getName()).get().isActiveTrue(),
                    "True should be inactive!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }

        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage
                .getEnvVariableWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1887", "ZTP-1886", "ZTP-1890"})
    @Maintainer("obabich")
    public void addAndDeleteBooleanCapability() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.CAPABILITIES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_BOOLEAN);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string capability ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        var.setDefaultValue("false");

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewCapability(var);
        softAssert.assertEquals(capabilityVar.getVarType(), var.getType().name(), "Type is not as expected!");
        softAssert.assertTrue(capabilityVar.isActiveFalse(), "False should be active!");
        softAssert.assertFalse(capabilityVar.isActiveTrue(), "True should not be active!");
        softAssert.assertEquals(capabilityVar.getVariableName(), var.getName(), "Var name is not as expected!");
        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in capability variables! ");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Capability is not as created!");

        CustomCapabilitiesModal modal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).isPresent(), "Capability should be present on modal!");

        if (modal.getCapabilityWithName(var.getName()).isPresent()) {
            softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).get().isActiveFalse(),
                    "False should be active!");
            softAssert.assertFalse(modal.getCapabilityWithName(var.getName()).get().isActiveTrue(),
                    "True should be inactive!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.clickCancel();
        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability should be present in the list of variables! ");
        addLauncherPage
                .getCapabilityWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Capability shouldn't be present in the list of variables after deleting! ");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1878"})
    @Maintainer("obabich")
    public void editBooleanEnvVariable() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_BOOLEAN);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for boolean capability ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        var.setDefaultValue(null);

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewEnvVariable(var);
        softAssert.assertEquals(capabilityVar.getVarType(), CustomVariable.Type.BOOLEAN.name(), "Type as expected!");
        softAssert.assertTrue(capabilityVar.isActiveTrue(), "True should be active by default!");//by default true is active

        capabilityVar.clickFalse();// ZTP-1878
        softAssert.assertTrue(capabilityVar.isActiveFalse(), "False should be active after selecting false!");
        softAssert.assertFalse(capabilityVar.isActiveTrue(), "True should be inactive after selecting false!");

        capabilityVar.clickTrue();// ZTP-1878
        softAssert.assertTrue(capabilityVar.isActiveTrue(), "True should be active after selecting true!");
        softAssert.assertFalse(capabilityVar.isActiveFalse(), "False should not be active after selecting true!");
        var.setDefaultValue("true");

        softAssert.assertTrue(addLauncherPage.isAddButtonClickable(), "Add button should be active!");
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Variable with name " + var.getName() + " was not found!");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).get().isActiveTrue(),
                    "True should be active on modal!");
            softAssert.assertFalse(modal.getEnvVariableWithName(var.getName()).get().isActiveFalse(),
                    "False should be inactive on modal!");

            modal.getEnvVariableWithName(var.getName()).get().getFalseCheckbox().click();
            pause(1);
            softAssert.assertFalse(modal.getEnvVariableWithName(var.getName()).get().isActiveTrue(),
                    "True should be inactive on modal after selecting false!");
            softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).get().isActiveFalse(),
                    "False should be active on modal after selecting false!");
            var.setDefaultValue("false");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.submitModal();

        String envVarsAfterEditing = addLauncherPage.getEnvVariablesValues();
        softAssert.assertEquals(envVarsAfterEditing, var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as expected after updating!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1892"})
    public void editBooleanCapability() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.CAPABILITIES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_BOOLEAN);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for boolean capability ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.BOOLEAN);
        var.setDefaultValue(null);

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewCapability(var);
        softAssert.assertEquals(capabilityVar.getVarType(), CustomVariable.Type.BOOLEAN.name(), "Type as expected!");
        softAssert.assertTrue(capabilityVar.isActiveTrue(), "True should be active by default!");//by default true is active

        capabilityVar.clickFalse();// ZTP-1892
        softAssert.assertTrue(capabilityVar.isActiveFalse(), "False should be active after selecting false!");
        softAssert.assertFalse(capabilityVar.isActiveTrue(), "True should be inactive after selecting false!");

        capabilityVar.clickTrue();// ZTP-1892
        softAssert.assertTrue(capabilityVar.isActiveTrue(), "True should be active after selecting true!");
        softAssert.assertFalse(capabilityVar.isActiveFalse(), "False should not be active after selecting true!");
        var.setDefaultValue("true");

        softAssert.assertTrue(addLauncherPage.isAddButtonClickable(), "Add button should be active!");
        softAssert.assertTrue(addLauncherPage.isVariablePresentInCapabilitiesList(var.getName()),
                "Variable with name " + var.getName() + " was not found!");
        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");

        CustomCapabilitiesModal modal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getCapabilityWithName(var.getName()).isPresent()) {
            softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).get().isActiveTrue(),
                    "True should be active on modal!");
            softAssert.assertFalse(modal.getCapabilityWithName(var.getName()).get().isActiveFalse(),
                    "False should be inactive on modal!");

            modal.getCapabilityWithName(var.getName()).get().getFalseCheckbox().click();
            pause(1);
            softAssert.assertFalse(modal.getCapabilityWithName(var.getName()).get().isActiveTrue(),
                    "True should be inactive on modal after selecting false!");
            softAssert.assertTrue(modal.getCapabilityWithName(var.getName()).get().isActiveFalse(),
                    "False should be active on modal after selecting false!");
            var.setDefaultValue("false");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.submitModal();

        String envVarsAfterEditing = addLauncherPage.getCapabilityVariablesValues();
        softAssert.assertEquals(envVarsAfterEditing, var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as expected after updating!");

        softAssert.assertAll();
    }

    @Test()
    @Maintainer("obabich")
    public void addStringEnvVariableWithSightEqualsInValue() throws NoSuchElementException {
        Artifact.attachReferenceToTest("ZEB-6081", "https://solvd.atlassian.net/browse/ZEB-6081");
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHERS);
        Label.attachToTest(TestLabelsConstant.VARIABLES, TestLabelsConstant.ENV_VARIABLES);
        Label.attachToTest(TestLabelsConstant.VARIABLES_TYPE, TestLabelsConstant.VARIABLES_TYPE_STRING);

        SoftAssert softAssert = new SoftAssert();
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        var.setDefaultValue("PRIORITY=>P0||P1");

        CustomVariableAddingForm capabilityVar = addLauncherPage.createNewEnvVariable(var);
        softAssert = capabilityVar.assertCreatedVariable(softAssert, var);
        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        addLauncherPage.submitLauncherAdding();

        String envVars = addLauncherPage.getEnvVariablesValues();
        softAssert.assertEquals(envVars, var.getName().concat("=").concat(var.getDefaultValue()), "Env vars is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        softAssert.assertTrue(modal.getEnvVariableWithName(var.getName()).isPresent(), "Env vars should be present in modal!");

        if (modal.getEnvVariableWithName(var.getName()).isPresent()) {
            softAssert.assertEquals(modal.getEnvVariableWithName(var.getName()).get().getValueOfCapability(), var.getDefaultValue(),
                    "Default value of capability is not as expected!");
        } else {
            softAssert.fail("Unable to find variable with name " + var.getName());
        }
        modal.getModalTitle().click();
        modal.clickCancel();
        pause(1);
        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created after opening modal with variables!");

        addLauncherPage.clickChangeDefaultsButton();

        softAssert.assertTrue(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable should be present in env variables! ");
        CustomVariableAddingForm customVariableAddingForm = addLauncherPage.getEnvVariableWithName(var.getName());
        softAssert = customVariableAddingForm.assertCreatedVariable(softAssert, var);
        addLauncherPage
                .getEnvVariableWithName(var.getName())
                .deleteVariable();

        pause(1);
        softAssert.assertFalse(addLauncherPage.isVariablePresentInEnvVariablesList(var.getName()),
                "Env variable shouldn't be present in variables! ");
        softAssert.assertAll();
    }

    @Test()
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-3678")
    public void checkEnvAndCapabilityVariablesRemainUnchangedOnCancel() {
        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        CustomVariable var = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        var.setName("envValueName");
        var.setDefaultValue("envValue");
        addLauncherPage.createNewEnvVariable(var);

        CustomVariable capVar = CustomVariable.getRandomCustomVariable(CustomVariable.Type.STRING);
        capVar.setName("customValueName");
        capVar.setDefaultValue("customValue1");
        addLauncherPage.createNewCapability(capVar);

        addLauncherPage.submitLauncherAdding();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Env vars is not as created!");
        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), capVar.getName().concat("=").concat(capVar.getDefaultValue()),
                "Cap var is not as created!");

        EnvironmentVariablesModal modal = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().openEnvironmentVariableModal();
        modal.getEnvVariableWithName(var.getName()).get().typeCapabilityValue("newEnvValue");
        modal.clickCancel();

        softAssert.assertEquals(addLauncherPage.getEnvVariablesValues(), var.getName().concat("=").concat(var.getDefaultValue()),
                "Environment variable is not as expected after canceling changes.");

        CustomCapabilitiesModal capModal = addLauncherPage.getSelectedLauncherForm().getTestingPlatformSection().openCustomCapabilitiesModal();
        capModal.getCapabilityWithName(capVar.getName()).get().typeCapabilityValue("newCapabilityValue");
        capModal.clickCancel();

        softAssert.assertEquals(addLauncherPage.getCapabilityVariablesValues(), capVar.getName().concat("=").concat(capVar.getDefaultValue()),
                "Capability variable is not as expected after canceling changes.");

        softAssert.assertAll();
    }

    @Test()
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-4533")
    public void instanceTypeAndDockerImageSelectionFieldsAreChangedAfterChangingTest() {
        SoftAssert softAssert = new SoftAssert();

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn()
                .typeLauncherName("Launcher for string env variable ".concat(RandomStringUtils.randomAlphabetic(5)))
                .findAndChooseDockerImage(DockerImageEnum.GRADLE_7_2_11.getDockerImage())
                .findBranchAndChoose(LauncherDataEnum.MAIN.get());

        addLauncherPage.submitLauncherAdding();

        ExecutionEnvSection executionEnvSection = addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().clickExpandButton();
        executionEnvSection.clickExpandButton();
        softAssert.assertEquals(executionEnvSection.getDockerImage(), DockerImageEnum.GRADLE_7_2_11.getDockerImage(),
                "Before changing docker image. It was not as expected!");
        softAssert.assertEquals(executionEnvSection.getInstanceType(), InstanceTypeEnum.MEDIUM.getValue(),
                "Before changing instance type. It was not as expected!");
        addLauncherPage.findAndChooseDockerImage(DockerImageEnum.MICROSOFT_PLAYWRIGHT.getDockerImage());
        addLauncherPage.findAndChooseExecutionInstanceType(InstanceTypeEnum.LARGE.getValue());

        executionEnvSection.clickExpandButton();
        softAssert.assertFalse(addLauncherPage.getSelectedLauncherForm().getExecutionEnvSection().isLaunchCommandVisible(),
                "Fields are not collapsed after clicking 'Less' button");

        executionEnvSection.clickExpandButton();
        softAssert.assertTrue(executionEnvSection.isLaunchCommandVisible(),
                "Fields are not expanded after clicking 'More' button");
        softAssert.assertEquals(executionEnvSection.getDockerImage(), DockerImageEnum.MICROSOFT_PLAYWRIGHT.getDockerImage(),
                "After changing docker image. It was not as expected!");
        softAssert.assertEquals(executionEnvSection.getInstanceType(), InstanceTypeEnum.LARGE.getValue(),
                "After changing instance type. It was not as expected!");

        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey("ZTP-6163")
    @Maintainer("Gmamaladze")
    public void verifyOperatingSystemFieldIsNotDisappearAfterDeletingEnvironmentOrCustomCapability() {
        Artifact.attachReferenceToTest("ZEB-6828", "https://solvd.atlassian.net/browse/ZEB-6828");

        SoftAssert softAssert = new SoftAssert();

        String os = LauncherDataEnum.LINUX.get();
        String browser = LauncherDataEnum.CHROME.get();
        String browserVersion = "latest";
        String expectedBrowserInputValue = browser + " " + browserVersion;

        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        AddOrEditLauncherPage addLauncherPage = launcherPage
                .chooseRepo(publicRepo)
                .clickOnRepository()
                .clickAddNewLauncherBtn();

        CustomVariableAddingForm environmentVariable = addLauncherPage.createNewEnvironmentVariable();
        CustomVariableAddingForm customCapability = addLauncherPage.getSelectedLauncherForm()
                .getCustomCapabilitiesSection().createNewCapability();

        addLauncherPage.clickOperationSystem().selectOS(os);
        addLauncherPage.chooseBrowserVersion(browser, browserVersion);

        environmentVariable.deleteVariable();

        softAssert.assertEquals(addLauncherPage.getSelectedOS(), LauncherDataEnum.LINUX.get(),
                "Os version is not as excepted after deleting environment variable !");
        softAssert.assertEquals(addLauncherPage.getSelectedBrowser(), expectedBrowserInputValue,
                "Browser input value is not as excepted after deleting environment variable !");

        customCapability.deleteVariable();

        softAssert.assertEquals(addLauncherPage.getSelectedOS(), LauncherDataEnum.LINUX.get(),
                "Os version is not as excepted after deleting custom Capability !");
        softAssert.assertEquals(addLauncherPage.getSelectedBrowser(), expectedBrowserInputValue,
                "Browser input value is not as excepted after deleting custom Capability !");

        softAssert.assertAll();
    }
}
