package com.zebrunner.automation.gui.tcm.testrun;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.common.NavigationMenu;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.NavigationMenuPopover;
import com.zebrunner.automation.gui.tcm.DeleteModal;
import com.zebrunner.automation.gui.tcm.EditConfigurationModal;
import com.zebrunner.automation.gui.tcm.ConfigGroup;
import com.zebrunner.automation.gui.tcm.CreateConfigurationGroupModal;
import com.zebrunner.automation.gui.tcm.OptionGroup;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.tcm.testrun.testcase.ModalSuiteItem;
import com.zebrunner.automation.gui.tcm.testrun.testcase.SelectTestCasesModal;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;
import com.zebrunner.automation.api.tcm.domain.Environment;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Milestone;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.tcm.domain.TestPlan;
import com.zebrunner.automation.api.tcm.domain.ConfigurationGroup;
import com.zebrunner.automation.api.tcm.domain.ConfigurationOption;
import com.zebrunner.automation.api.tcm.domain.TestRunConfiguration;

@Slf4j
@Maintainer("akhivyk")
@TestLabel(name = TestLabelsConstant.GROUP, value = TestLabelsConstant.TEST_RUNS)
public class TestRunsGridPageTest extends TcmLogInBase {

    private static final int TEST_RUN_TITLE_CHARACTERS_LIMIT = 255;
    private static final int TEST_RUN_DESCRIPTION_CHARACTERS_LIMIT = 2047;
    private static final String EXPECTED_PLACEHOLDER_TITLE = "There are no test runs yet";
    private static final String EXPECTED_PLACEHOLDER_DESCRIPTION = "Validate your product increment by creating test runs" +
            "\nand distributing work among team members.";

    private Project project;
    private Project emptyProject;
    private Environment environment;
    private Milestone milestone;
    private String testRunName;
    private TestSuite testSuite;
    private TestSuite subSuite;
    private TestCase testCase;
    private ConfigurationGroup configGroup;
    private ConfigurationOption configOption;

    @BeforeClass
    public void preparation() {
        String milestoneName = "Milestone_" + RandomStringUtils.randomAlphabetic(5);
        project = super.getCreatedProject();
        emptyProject = super.getEmptyProject();

        environment = environmentService.createEnvironment(project.getId(), Environment.createRandom());
        milestone = apiHelperService.createMilestone(project.getId(), Milestone.createMilestoneWithTitle(milestoneName));

        testCase = new TestCase("Test case № ".concat(RandomStringUtils.randomNumeric(7)));
        testSuite = tcmService.createTestSuite(project.getId(), new TestSuite("Suite for automation " + RandomStringUtils.randomNumeric(5)));
        testCase = tcmService.createTestCase(project.getId(), testSuite.getId(), testCase);

        subSuite = tcmService.createTestSuite(project.getId(), new TestSuite("SubSuite " + RandomStringUtils.randomAlphabetic(3), testSuite.getId()));
        TestCase subSuiteTestCase = new TestCase("Test case № ".concat(RandomStringUtils.randomNumeric(7)));
        subSuiteTestCase.setDraft(true);
        tcmService.createTestCase(project.getId(), subSuite.getId(), subSuiteTestCase);

        configGroup = apiHelperService.createConfigurationGroup(project.getId(), ConfigurationGroup.createRandom());
        configOption = apiHelperService.createConfigurationOption(project.getId(), configGroup.getId(), ConfigurationOption.generateRandom());
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedData() {
        environmentService.deleteEnvironment(project.getId(), environment.getId());
        apiHelperService.deleteMilestone(project.getId(), milestone.getId());
        tcmService.deleteTestSuite(project.getId(), testSuite.getId());
    }

    @Test
    @TestCaseKey({"ZTP-2867", "ZTP-2866"})
    public void _verifyUserIsAbleToOpenTestRunPageAndPlaceholderPresentWhenNoTestRuns() {
        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), emptyProject);
        automationLaunchesPage.getNavigationMenu().to(NavigationMenu.NavigationMenuItem.TESTING_ACTIVITIES,
                NavigationMenuPopover.PopoverItems.TEST_RUNS);

        TestRunsGridPage testRunsGridPage = new TestRunsGridPage(getDriver());

        // ZTP-2866 User is able to open the 'Test runs' page
        softAssert.assertTrue(testRunsGridPage.isPageOpened(), "Test runs page isn't opened via navigation menu");

        softAssert.assertTrue(testRunsGridPage.getTestRunsList().isEmpty(), "Test run list isn't empty");
        softAssert.assertTrue(testRunsGridPage.getEmptyPlaceholder().isEmptyPlaceholderImagePresent(),
                "Placeholder img isn't present on empty test runs page!");
        softAssert.assertTrue(testRunsGridPage.getEmptyPlaceholder().isEmptyPlaceholderTitlePresent(),
                "Placeholder title isn't present on empty test runs page!");
        softAssert.assertTrue(testRunsGridPage.getEmptyPlaceholder().isEmptyPlaceholderDescriptionPresent(),
                "Placeholder description isn't present on empty test runs page!");
        softAssert.assertTrue(testRunsGridPage.getEmptyPlaceholder().isCreateFirstTestRunButtonPresent(),
                "Placeholder button isn't present on empty test runs page!"); // ZTP-2867 Verify the placeholder if there are no test runs

        softAssert.assertEquals(testRunsGridPage.getEmptyPlaceholder()
                                                .getEmptyPlaceHolderTitle(), EXPECTED_PLACEHOLDER_TITLE,
                "Placeholder title isn't equals to expected!");
        softAssert.assertEquals(testRunsGridPage.getEmptyPlaceholder()
                                                .getEmptyPlaceHolderDescription(), EXPECTED_PLACEHOLDER_DESCRIPTION,
                "Placeholder description isn't equals to expected!");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2868", "ZTP-5464"})
    public void verifyUserIsAbleToOpenCreateTestRunPageAndClose() {
        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project);
        automationLaunchesPage.getNavigationMenu().to(NavigationMenu.NavigationMenuItem.TESTING_ACTIVITIES,
                NavigationMenuPopover.PopoverItems.TEST_RUNS);

        TestRunsGridPage testRunsGridPage = new TestRunsGridPage(getDriver());
        softAssert.assertTrue(testRunsGridPage.isPageOpened(), "Test runs page isn't opened via navigation menu");

        softAssert.assertTrue(testRunsGridPage.isCreateTestRunButtonClickable(), "Create test run button isn't clickable");

        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        softAssert.assertTrue(createTestRunPage.isPageOpened(), "Create test run page isn't opened"); // ZTP-2868 User is able to open the 'Create test run' page

        testRunsGridPage = createTestRunPage.clickCancelButton();
        softAssert.assertTrue(testRunsGridPage.isPageOpened(), "Test runs grid page isn't opened after" +
                " canceling create test run"); // ZTP-5464 User is able to close the 'Create test run' page

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5887")
    public void verifyUserIsAbleToCreateTestRunWithOnlyRequiredFields() {
        SoftAssert softAssert = new SoftAssert();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        softAssert.assertTrue(testRunsGridPage.isPageOpened(), "Test runs page isn't opened");

        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        softAssert.assertTrue(createTestRunPage.isPageOpened(), "Create test run page isn't opened");

        testRunName = "testRun_" + RandomStringUtils.randomAlphabetic(5);
        createTestRunPage.inputTitle(testRunName);

        softAssert.assertTrue(createTestRunPage.isCreateButtonClickable(), "Create test run button isn't clickable");

        TestRunPage testRunPage = createTestRunPage.clickCreateButton();
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_RUN_CREATED.getDescription(), "Popup message isn't equals to expected");

        testRunPage.clickBreadcrumb(TestRunsGridPage.PAGE_TITLE);

        softAssert.assertTrue(testRunsGridPage.isTestRunExist(testRunName), "Created test run isn't " +
                "present in all test runs grid"); // ZTP-5887 User is able to 'Create test run'

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-2869")
    public void verifyTitleRestrictions() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        String testRunNameWith255Characters = RandomStringUtils.randomAlphabetic(255);
        String testRunNameWithSpecialCharacters = "# " + "@ " + "( " + ") " + "* " + ">" + "! " + "' " + "` " + "~";
        String testRunNameWithCyrillicCharacters = "ТестРан_" + RandomStringUtils.randomNumeric(5);
        String testRunNameWithOnlySpaces = "   ";

        // 255 characters
        createTestRunPage.inputTitle(testRunNameWith255Characters);

        softAssert.assertTrue(createTestRunPage.isCreateButtonClickable(), "Create test run button isn't clickable");

        TestRunPage testRunPage = createTestRunPage.clickCreateButton();
        softAssert.assertTrue(testRunPage.isPageOpened(),
                "Direct test run page isn't opened after creating test run with name " + testRunNameWith255Characters);

        testRunsGridPage = testRunPage.backToTestRunsGrid();

        softAssert.assertTrue(testRunsGridPage.isTestRunExist(testRunNameWith255Characters),
                "Test run with 255 char in name isn't present in all test runs grid");

        // special characters including spaces
        createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        createTestRunPage.inputTitle(testRunNameWithSpecialCharacters);

        softAssert.assertTrue(createTestRunPage.isCreateButtonClickable(), "Create test run button isn't clickable");

        testRunPage = createTestRunPage.clickCreateButton();
        softAssert.assertTrue(testRunPage.isPageOpened(),
                "Direct test run page isn't opened after creating test run with name " + testRunNameWithSpecialCharacters);

        testRunsGridPage = testRunPage.backToTestRunsGrid();

        softAssert.assertTrue(testRunsGridPage.isTestRunExist(testRunNameWithSpecialCharacters),
                "Test run with special char in name isn't present in all test runs grid");

        // cyrillic characters
        createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        createTestRunPage.inputTitle(testRunNameWithCyrillicCharacters);

        softAssert.assertTrue(createTestRunPage.isCreateButtonClickable(), "Create test run button isn't clickable");

        testRunPage = createTestRunPage.clickCreateButton();
        softAssert.assertTrue(testRunPage.isPageOpened(),
                "Direct test run page isn't opened after creating test run with name " + testRunNameWithCyrillicCharacters);

        testRunsGridPage = testRunPage.backToTestRunsGrid();

        softAssert.assertTrue(testRunsGridPage.isTestRunExist(testRunNameWithCyrillicCharacters),
                "Test run with cyrillic char in name isn't present in all test runs grid");

        // unable leave title empty
        createTestRunPage = testRunsGridPage.clickCreateTestRunButton();
        createTestRunPage.inputTitle("");

        softAssert.assertFalse(createTestRunPage.isCreateButtonClickable(), "Create test run button " +
                "is clickable with empty title");

        // unable title with only spaces
        createTestRunPage.inputTitle(testRunNameWithOnlySpaces);

        softAssert.assertFalse(createTestRunPage.isCreateButtonClickable(), "Create test run button " +
                "is clickable with only spaces");

        testRunsGridPage = createTestRunPage.clickCancelButton();
        softAssert.assertFalse(testRunsGridPage.isTestRunExist(testRunNameWithOnlySpaces),
                "Test run with only spaces exist in grid");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2870", "ZTP-2871"})
    public void verifyMaxCharacterLimitsInTitleAndDescription() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        String testRunNameWith256Characters = RandomStringUtils.randomAlphabetic(256);
        String testRunDescription = "testRunDesc_" + RandomStringUtils.randomAlphabetic(2035);

        createTestRunPage.inputTitle(testRunNameWith256Characters);

        softAssert.assertEquals(createTestRunPage.getTitleInputValue().length(), TEST_RUN_TITLE_CHARACTERS_LIMIT,
                "Entered length of title more than 255 characters");

        createTestRunPage.inputDescription(testRunDescription);

        TestRunPage testRunPage = createTestRunPage.clickCreateButton();
        softAssert.assertEquals(testRunPage.getName().length(), TEST_RUN_TITLE_CHARACTERS_LIMIT,
                "Length of created test run name isn't equals to limit"); // ZTP-2870 user can't enter >255 symbols in the 'Title' field
        softAssert.assertEquals(testRunPage.getExpandedHeader().getDescription()
                                           .length(), TEST_RUN_DESCRIPTION_CHARACTERS_LIMIT,
                "Length of created test run description isn't equals to limit");

        testRunsGridPage = testRunPage.backToTestRunsGrid();
        createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        testRunDescription = RandomStringUtils.randomAlphabetic(2048);
        testRunName = "testRun_" + RandomStringUtils.randomAlphabetic(7);

        createTestRunPage.inputTitle(testRunName)
                         .inputDescription(testRunDescription);

        softAssert.assertEquals(createTestRunPage.getDescriptionInputText()
                                                 .length(), TEST_RUN_DESCRIPTION_CHARACTERS_LIMIT,
                "Entered length of description more than 2047 characters");

        testRunPage = createTestRunPage.clickCreateButton();

        // ZTP-2871 User is able to fill in the 'Description' field
        softAssert.assertEquals(testRunPage.getExpandedHeader().getDescription()
                                           .length(), TEST_RUN_DESCRIPTION_CHARACTERS_LIMIT,
                "Length of create test run description isn't equals to limit after inputting more than 2047 characters");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2872", "ZTP-2873"})
    public void verifyUserIsAbleToSelectEnvironmentAndMilestoneWhenCreateTestRun() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        testRunName = "testRun_" + RandomStringUtils.randomAlphabetic(5);
        createTestRunPage.inputTitle(testRunName);

        createTestRunPage.selectEnvironment(environment.getName());
        createTestRunPage.selectMilestone(milestone.getName());

        TestRunPage testRunPage = createTestRunPage.clickCreateButton();

        softAssert.assertTrue(testRunPage.getEnvironmentName().equalsIgnoreCase(environment.getKey()),
                "Environment of created test run isn't equals to expected on direct test run page"); // ZTP-2872 User is able to select the 'Environment'
        softAssert.assertEquals(testRunPage.getExpandedHeader().getMilestoneName(), milestone.getName(),
                "Milestone of created test run isn't equals to expected on direct test run page"); // ZTP-2873 User is able to select the 'Milestone'

        TestRunItem testRunItem = testRunPage.backToTestRunsGrid()
                                             .getTestRunItem(testRunName);
        softAssert.assertTrue(testRunItem.getEnvironmentLabel().equalsIgnoreCase(environment.getKey()),
                "Environment of created test run isn't equals to expected on all test run grid page");
        softAssert.assertEquals(testRunItem.getMilestoneName(), milestone.getName(),
                "Milestone of created test run isn't equals to expected on all test run grid page");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2900", "ZTP-5462", "ZTP-2905", "ZTP-2902", "ZTP-2906"})
    public void verifyUserIsAbleToCreateConfigurationGroupWithOptionAndSelect() {
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(getDriver());

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        testRunName = "Test Run " + RandomStringUtils.randomAlphabetic(5);
        createTestRunPage.inputTitle(testRunName);
        softAssert.assertTrue(createTestRunPage.isAddConfigurationButtonClickable(), "Add configuration button isn't clickable!");

        CreateTestRunPage.SelectConfigurationDialog selectConfigurationDialog = createTestRunPage.clickAddConfigurationButton();
        // ZTP-2900
        Assert.assertTrue(selectConfigurationDialog.isOpened(), "Select configuration dialog isn't opened!");
        Assert.assertTrue(selectConfigurationDialog.isAddConfigurationGroupButtonClickable(), "Create config group button isn't clickable!");

        CreateConfigurationGroupModal createConfigGroupModal = selectConfigurationDialog.clickCreateConfigurationGroup();
        Assert.assertTrue(createConfigGroupModal.isModalOpened(), "Create config group modal isn't opened!");

        String groupName = "Group " + RandomStringUtils.randomAlphabetic(5);
        createConfigGroupModal.inputTitle(groupName)
                              .submitModal();

        // ZTP-5462
        softAssert.assertTrue(
                selectConfigurationDialog.isGroupExist(groupName),
                "Created group isn't present in the list of all existing configurations!"
        );

        ConfigGroup configGroup = selectConfigurationDialog.selectGroup(groupName);
        softAssert.assertTrue(configGroup.isAddOptionButtonClickable(), "Add option button isn't clickable!");

        String optionName = "Option " + RandomStringUtils.randomAlphabetic(3);
        configGroup.clickAddOptionButton()
                   .inputOptionName(optionName);

        actions.keyDown(Keys.ENTER).perform();
        actions.keyUp(Keys.ENTER).perform();

        softAssert.assertTrue(configGroup.isOptionExist(optionName), "Created option isn't present in group options!"); // ZTP-2905 User is able to 'Add option'
        configGroup.selectOption(optionName); // ZTP-2906 User is able to select a new option

        TestRunPage testRunPage = selectConfigurationDialog.clickDoneButton()
                                                           .clickCreateButton();
        softAssert.assertEquals(testRunPage.getExpandedHeader().getConfigurationGroupName(), groupName,
                "Configuration group name isn't equals to expected on certain test run page");
        softAssert.assertEquals(testRunPage.getExpandedHeader().getConfigurationOptionName(), optionName,
                "Configuration option name isn't equals to expected on certain test run page"); // ZTP-2902 User is able to 'Add configuration group'

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5463", "ZTP-5465"})
    public void verifyUserIsAbleToCancelCreatingAndDeletingConfigurationGroup() {
        WebDriver webDriver = getDriver();
        Actions actions = new Actions(webDriver);

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(webDriver, project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        CreateTestRunPage.SelectConfigurationDialog selectConfigurationDialog = createTestRunPage.clickAddConfigurationButton();
        Assert.assertTrue(selectConfigurationDialog.isOpened(), "Configuration modal isn't opened!");

        CreateConfigurationGroupModal createConfigGroupModal = selectConfigurationDialog.clickCreateConfigurationGroup();
        Assert.assertTrue(createConfigGroupModal.isModalOpened(), "Create config group modal isn't opened!");

        String groupName = "Configuration " + RandomStringUtils.randomAlphabetic(5);
        createConfigGroupModal.inputTitle(groupName)
                              .clickCancel();

        Assert.assertFalse(
                createConfigGroupModal.getRootExtendedElement().isVisible(5),
                "Create config modal isn't closed via 'Cancel' button!"
        );

        createConfigGroupModal = selectConfigurationDialog.clickCreateConfigurationGroup();
        createConfigGroupModal.inputTitle(groupName);

        actions.keyDown(Keys.ESCAPE).perform();
        actions.keyUp(Keys.ESCAPE).perform();

        Assert.assertFalse(
                createConfigGroupModal.getRootExtendedElement().isVisible(5),
                "Create config modal isn't closed via 'Escape' button!"
        );

        createConfigGroupModal = selectConfigurationDialog.clickCreateConfigurationGroup();
        createConfigGroupModal.inputTitle(groupName)
                              .clickClose();

        Assert.assertFalse(
                createConfigGroupModal.getRootExtendedElement().isVisible(5),
                "Create config modal isn't closed via 'X' button!"
        );
        // ZTP-5463
        Assert.assertFalse(
                selectConfigurationDialog.isGroupExist(groupName),
                "Group present in all config groups list after canceling creation"
        );

        createConfigGroupModal = selectConfigurationDialog.clickCreateConfigurationGroup();
        createConfigGroupModal.inputTitle(groupName)
                              .submitModal();

        Assert.assertTrue(
                selectConfigurationDialog.isGroupExist(groupName),
                "Created group isn't present in all config groups list after creation"
        );

        ConfigGroup configGroup = selectConfigurationDialog.getGroup(groupName);
        DeleteModal deleteModal = configGroup.clickDeleteIcon();

        actions.keyDown(Keys.ESCAPE).perform();
        actions.keyUp(Keys.ESCAPE).perform();

        Assert.assertFalse(
                deleteModal.getRootExtendedElement().isVisible(5),
                "Delete group modal isn't closed via 'Escape' button!"
        );

        configGroup = selectConfigurationDialog.getGroup(groupName);
        deleteModal = configGroup.clickDeleteIcon()
                                 .clickCancel();

        Assert.assertFalse(
                deleteModal.getRootExtendedElement().isVisible(5),
                "Delete group modal isn't closed via 'Cancel' button!"
        );

        configGroup = selectConfigurationDialog.getGroup(groupName);
        deleteModal = configGroup.clickDeleteIcon()
                                 .clickClose();

        Assert.assertFalse(
                deleteModal.getRootExtendedElement().isVisible(5),
                "Delete group modal isn't closed via 'X' button!"
        );
        // ZTP-5465
        Assert.assertTrue(
                selectConfigurationDialog.isGroupExist(groupName),
                "Group isn't present in all config groups list after canceling deleting"
        );
    }

    @Test
    @TestCaseKey({"ZTP-5466", "ZTP-2907", "ZTP-2903"})
    public void verifyUserIsAbleToDeleteConfigOptionAndGroup() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        String groupName = "Configuration " + RandomStringUtils.randomAlphabetic(5);
        String optionName = "Option " + RandomStringUtils.randomAlphabetic(3);

        CreateTestRunPage.SelectConfigurationDialog configurationModal = createTestRunPage.clickAddConfigurationButton();
        configurationModal.createNewConfigGroupAndOption(groupName, optionName);

        softAssert.assertTrue(configurationModal.isGroupExist(groupName), "Config group isn't created");

        ConfigGroup configGroup = configurationModal.getGroup(groupName);
        OptionGroup option = configGroup.getOption(optionName);

        DeleteModal deleteModal = option.clickDeleteIcon();
        softAssert.assertTrue(deleteModal.isModalOpened(),
                "Delete modal isn't opened after clicking delete icon on option");

        deleteModal.clickCancel();
        softAssert.assertTrue(configGroup.isOptionExist(optionName),
                "Option isn't present in config group after canceling deleting"); // ZTP-5466 User is able to cancel 'Delete' option

        configGroup.getOption(optionName)
                   .clickDeleteIcon()
                   .clickDelete();
        pause(1);
        softAssert.assertFalse(configurationModal.getGroup(groupName).isOptionExist(optionName),
                "Option is still exist after deleting"); // ZTP-2907 User is able to Delete option

        configGroup.clickDeleteIcon()
                   .clickDelete();
        pause(1);
        softAssert.assertFalse(configurationModal.isGroupExist(groupName),
                "Group is still exist after deleting"); // ZTP-2903 User is able to 'Delete configuration group'

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2904", "ZTP-5467"})
    public void verifyUserIsAbleToEditConfigurationGroup() {
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(getDriver());
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        CreateTestRunPage.SelectConfigurationDialog configurationModal = createTestRunPage.clickAddConfigurationButton();
        softAssert.assertTrue(configurationModal.isOpened(), "Configuration modal isn't opened!");

        String groupName = "Group " + RandomStringUtils.randomAlphabetic(5);
        configurationModal.createNewConfigGroup(groupName);

        softAssert.assertTrue(configurationModal.isGroupExist(groupName), "Group isn't created!");

        ConfigGroup configGroup = configurationModal.getGroup(groupName);
        EditConfigurationModal editModal = configGroup.clickEditButton();

        softAssert.assertTrue(editModal.isModalVisible(),
                "Edit config group modal isn't opened!");

        editModal.clickCancel();
        softAssert.assertFalse(editModal.isModalVisible(),
                "Delete group modal isn't closed via 'Cancel' button!");

        editModal = configGroup.clickEditButton();
        editModal.clickClose();
        softAssert.assertFalse(editModal.isModalVisible(),
                "Delete group modal isn't closed via 'X' button!");

        editModal = configGroup.clickEditButton();

        actions.keyDown(Keys.ESCAPE).perform();
        actions.keyUp(Keys.ESCAPE).perform();

        softAssert.assertFalse(editModal.isModalVisible(),
                "Delete group modal isn't closed via 'Escape' button!"); // ZTP-5467 user can cancel 'Edit configuration group'

        String newGroupName = "Updated Group " + RandomStringUtils.randomAlphabetic(5);
        editModal = configGroup.clickEditButton();
        editModal.inputTitle(newGroupName);

        softAssert.assertTrue(editModal.isSaveButtonClickable(),
                "Save button isn't clickable on edit group modal after editing title");

        editModal.submitModal();
        softAssert.assertTrue(configurationModal.isGroupExist(newGroupName),
                "Group with updated name isn't found in all config list!");
        softAssert.assertFalse(configurationModal.isGroupExist(groupName),
                "Group with previous name still exist in all config list"); // ZTP-2904 user can 'Edit configuration group'

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2908", "ZTP-5468"})
    public void userIsAbleToEditConfigurationGroupOption() {
        WebDriver webDriver = super.getDriver();
        Actions actions = new Actions(webDriver);

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(webDriver, project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        CreateTestRunPage.SelectConfigurationDialog selectConfigurationDialog = createTestRunPage.clickAddConfigurationButton();
        Assert.assertTrue(selectConfigurationDialog.isOpened(), "Configuration modal isn't opened!");

        String groupName = "Group " + RandomStringUtils.randomAlphabetic(5);
        String optionName = "Option " + RandomStringUtils.randomAlphabetic(3);
        selectConfigurationDialog.createNewConfigGroupAndOption(groupName, optionName);

        Assert.assertTrue(selectConfigurationDialog.isGroupExist(groupName), "Group isn't created!");
        Assert.assertTrue(
                selectConfigurationDialog.getGroup(groupName).isOptionExist(optionName),
                "Option in created group isn't present!"
        );

        OptionGroup option = selectConfigurationDialog.getGroup(groupName).getOption(optionName);
        EditConfigurationModal editModal = option.clickEditButton();

        Assert.assertTrue(editModal.isModalVisible(), "Delete option modal isn't opened!");
        editModal.clickCancel();

        Assert.assertFalse(editModal.isModalVisible(), "Delete option modal isn't closed via 'Cancel' button!");

        option = selectConfigurationDialog.getGroup(groupName).getOption(optionName);
        editModal = option.clickEditButton();
        editModal.clickClose();

        Assert.assertFalse(editModal.isModalVisible(), "Delete option modal isn't closed via 'X' button!");

        option = selectConfigurationDialog.getGroup(groupName).getOption(optionName);
        editModal = option.clickEditButton();

        actions.keyDown(Keys.ESCAPE).perform();
        actions.keyUp(Keys.ESCAPE).perform();

        // ZTP-5468 User is able to cancel Edit option
        Assert.assertFalse(editModal.isModalVisible(), "Delete option modal isn't closed via 'Escape' button!");

        String newOptionName = "Updated Option " + RandomStringUtils.randomAlphabetic(3);
        option = selectConfigurationDialog.getGroup(groupName).getOption(optionName);

        editModal = option.clickEditButton();
        editModal.inputTitle(newOptionName);

        Assert.assertTrue(editModal.isSaveButtonClickable(), "Save button isn't clickable on edit option modal after editing title");
        editModal.submitModal();

        ConfigGroup configGroup = selectConfigurationDialog.getGroup(groupName);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(configGroup.isOptionExist(newOptionName), "Option with updated name isn't found in the list of all options!");
        // ZTP-2908 User is able to Edit option
        softAssert.assertFalse(configGroup.isOptionExist(optionName), "Option with previous name still exist in the list of all options!");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2917", "ZTP-5475"})
    public void verifyMakeNotApplicableInscriptionAppearsAfterSelectingOption() {
        WebDriver webDriver = getDriver();
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(webDriver, project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        CreateTestRunPage.SelectConfigurationDialog selectConfigurationDialog = createTestRunPage.clickAddConfigurationButton();
        Assert.assertTrue(selectConfigurationDialog.isOpened(), "Configuration modal isn't opened!");

        String groupName = "Group " + RandomStringUtils.randomAlphabetic(5);
        String optionName = "Option " + RandomStringUtils.randomAlphabetic(3);
        selectConfigurationDialog.createNewConfigGroupAndOption(groupName, optionName);

        Assert.assertTrue(selectConfigurationDialog.isGroupExist(groupName), "Group isn't created!");
        Assert.assertTrue(
                selectConfigurationDialog.getGroup(groupName).isOptionExist(optionName),
                "Option in created group isn't present!"
        );

        ConfigGroup configGroup = selectConfigurationDialog.selectGroup(groupName);
        softAssert.assertEquals(
                configGroup.getApplicableLabelText(),
                "not applicable",
                "Default value of applicable label isn't equals to expected"
        );
        softAssert.assertAll();

        configGroup.selectOption(optionName);
        Assert.assertTrue(configGroup.getOption(optionName).isSelected(), "Option isn't selected after clicking!");

        // ZTP-2917
        softAssert.assertEquals(
                configGroup.getActiveApplicableLabelText(),
                "make not applicable",
                "Value of applicable label isn't equals to expected after selecting option"
        );

        configGroup.clickActiveApplicableLabel();

        softAssert.assertEquals(
                configGroup.getApplicableLabelText(),
                "not applicable",
                "Value of applicable label isn't equals to expected after clicking 'make not applicable' button"
        );
        // ZTP-5475
        softAssert.assertFalse(
                configGroup.getOption(optionName).isSelected(),
                "Option is still selected after clicking 'make not applicable' button"
        );
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2919", "ZTP-2920"})
    public void verifyUserIsAbleToOpenAddTestCaseModalAndSearchTestCases() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        String keyword = "case";
        SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
        softAssert.assertTrue(selectTestCasesModal.isOpened(), "Test case modal isn't opened!"); // ZTP-2919 User is able to open the 'Select test cases' modal window

        List<ModalSuiteItem> foundSuites = selectTestCasesModal.searchTestSuitesWithTestCaseWithName(keyword);
        for (int i = 0; i < foundSuites.size(); i++) {
            selectTestCasesModal.expandTestSuites();

            foundSuites.get(i).clickOnName().getTestCases().forEach(obtainedCase -> {
                softAssert.assertTrue(obtainedCase.getTestCaseName().toLowerCase()
                                                  .contains(keyword), "Founded test case " +
                        obtainedCase.getTestCaseName() + "  title isn't contains keyword " + keyword);
            }); // ZTP-2920 User is able to use the 'Search' field
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2922", "ZTP-2923", "ZTP-2926"})
    public void verifyUserIsAbleToUseCheckBoxesForTestSuites() {
        SoftAssert softAssert = new SoftAssert();
        String expectedCountCases = "2";
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
        selectTestCasesModal.selectTestSuite(testSuite.getTitle());

        softAssert.assertEquals(selectTestCasesModal.getCountSelectedCases(), expectedCountCases,
                "Count of selected cases after selecting all test suite isn't equals to expected on test case modal");

        createTestRunPage = selectTestCasesModal.clickDoneButton();
        softAssert.assertEquals(createTestRunPage.getLinkedTestCasesNumber(), expectedCountCases,
                "Count of selected cases after selecting all test suite isn't equals to expected on create test run page"); // ZTP-2922 User is able to use checkboxes for Test suites

        softAssert.assertTrue(createTestRunPage.isChangeLinkedTestCasesButtonVisible(),
                "Change selection test cases button isn't visible after linking test cases");
        softAssert.assertFalse(createTestRunPage.isAddTestCasesButtonVisible(),
                "Add test cases button still visible after linking test cases");

        selectTestCasesModal = createTestRunPage.clickChangeLinkedTestCasesButton(); // ZTP-2926 User is able to click on the "change selection" button
        createTestRunPage = selectTestCasesModal.clickClearSelectionButton()
                                                .clickDoneButton();

        softAssert.assertFalse(createTestRunPage.isChangeLinkedTestCasesButtonVisible(),
                "Change selection test cases button still present after clearing selection test cases");
        softAssert.assertTrue(createTestRunPage.isAddTestCasesButtonVisible(),
                "Add test cases button isn't visible after clearing selection test cases");

        selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();
        selectTestCasesModal.clickOnTestSuite(testSuite.getTitle())
                            .selectTestCase(testCase.getTitle());

        softAssert.assertEquals(selectTestCasesModal.getCountSelectedCases(), "1",
                "Count of selected cases after selecting test case isn't equals to expected on test case modal");
        createTestRunPage = selectTestCasesModal.clickDoneButton();

        //  ZTP-2923 User is able to use checkboxes for Test cases
        softAssert.assertEquals(createTestRunPage.getLinkedTestCasesNumber(), "1",
                "Count of selected cases after selecting test case isn't equals to expected on create test run page");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2924", "ZTP-2921"})
    public void verifyUserIsAbleToExpandAndCollapseTestSuitesAndUseFilters() {
        SoftAssert softAssert = new SoftAssert();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        SelectTestCasesModal selectTestCasesModal = createTestRunPage.clickAddTestCaseButton();

        selectTestCasesModal.expandTestSuites();
        softAssert.assertTrue(selectTestCasesModal.isTestSuiteVisible(testSuite.getTitle()),
                "Test suite with name " + testSuite.getTitle() + " isn't visible after expanding test suites");
        softAssert.assertTrue(selectTestCasesModal.isTestSuiteVisible(subSuite.getTitle()),
                "Sub suite with name " + subSuite.getTitle() + " isn't visible after expanding test suites");

        selectTestCasesModal.collapseTestSuites(); // ZTP-2924 User is able to expand and collapse Test suites

        softAssert.assertTrue(selectTestCasesModal.isTestSuiteVisible(testSuite.getTitle()),
                "Main test suite with name " + testSuite.getTitle() + " isn't visible after collapsing test suites");
        softAssert.assertFalse(selectTestCasesModal.isTestSuiteVisible(subSuite.getTitle()),
                "Sub suite with name " + subSuite.getTitle() + " is still visible after collapsing test suites");

        selectTestCasesModal.getFilters().selectFilterItem(TestCaseFilterBlock.TestCaseFiltersEnum.DRAFT, "Yes");
        List<ModalSuiteItem> foundSuites = selectTestCasesModal.getTestSuites();
        for (int i = 0; i < foundSuites.size(); i++) {
            selectTestCasesModal.expandTestSuites();

            foundSuites.get(i).clickOnName().getTestCases().forEach(obtainedCase -> {
                softAssert.assertTrue(obtainedCase.isCaseDraft(), "Founded test case " +
                        obtainedCase.getTestCaseName() + "  isn't marked as draft");
            });
        } // ZTP-2921 User is able to use filters

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-2933", "ZTP-2928", "ZTP-5488"})
    public void verifyUserIsAbleToOpen3DotMenuAndCloseTestRun() {
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(getDriver());

        testRunName = "Test run " + RandomStringUtils.randomAlphabetic(5);
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        CreateTestRunPage createTestRunPage = testRunsGridPage.clickCreateTestRunButton();

        TestRunPage testRunPage = createTestRunPage.inputTitle(testRunName)
                                                   .clickCreateButton();
        softAssert.assertEquals(testRunPage.getPopUp(), MessageEnum.TEST_RUN_CREATED.getDescription(),
                "Popup message isn't equals to expected");

        testRunsGridPage = testRunPage.backToTestRunsGrid();


        for (int i = 0; i < 3; i++) {
            TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRunName);
            testRunPage = testRunItem.clickTestRunItem();

            // ZTP-2933 User is able to open test run
            softAssert.assertTrue(testRunPage.isPageOpened(), "Test run page isn't opened from test run grid page");

            CloseTestRunModal closeTestRunModal = testRunPage.clickCloseButton();
            softAssert.assertTrue(closeTestRunModal.isModalOpened(),
                    "Close test run modal isn't opened after clicking 'Close' button on test run page");

            if (i == 0) {
                closeTestRunModal.clickCancel();
            } else if (i == 1) {
                closeTestRunModal.clickCrossButton();
            } else {
                actions.keyDown(Keys.ESCAPE).perform();
                actions.keyUp(Keys.ESCAPE).perform();
            }

            // ZTP-5488 User is able to cancel closing the test run
            softAssert.assertFalse(closeTestRunModal.isModalOpened(),
                    "Close test run modal still open after canceling");
            testRunsGridPage = testRunPage.backToTestRunsGrid()
                                          .clickOpenedTestRuns();
        }

        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRunName);
        testRunPage = testRunItem.clickTestRunItem();
        CloseTestRunModal closeTestRunModal = testRunPage.clickCloseButton();

        closeTestRunModal.clickCloseButton();
        testRunsGridPage = testRunPage.backToTestRunsGrid()
                                      .clickOpenedTestRuns();
        softAssert.assertFalse(testRunsGridPage.isTestRunExist(testRunName),
                "Test run " + testRunName + " is still present in opened test runs list after closing on test run page");

        testRunsGridPage = testRunsGridPage.clickClosedTestRuns();
        // ZTP-2928 User is able to close test run
        softAssert.assertTrue(testRunsGridPage.isTestRunExist(testRunName),
                "Test run " + testRunName + " isn't present in closed test runs list after closing on test run page");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5898")
    public void userIsAbleToSelectTestRunsViaShift() {
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(getDriver());
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);

        tcmService.createTestRuns(project.getId(), 4);
        List<TestRunItem> displayedTestRunsOnPage = testRunsGridPage.getTestRunItems();

        actions.keyDown(Keys.SHIFT).perform();

        displayedTestRunsOnPage.get(0)
                               .clickCheckBox();
        displayedTestRunsOnPage.get(displayedTestRunsOnPage.size() - 1)
                               .clickCheckBox();

        actions.keyUp(Keys.SHIFT).perform();

        displayedTestRunsOnPage = testRunsGridPage.getTestRunsList();
        for (TestRunItem testRunItem : displayedTestRunsOnPage) {
            softAssert.assertTrue(testRunItem.isCheckBoxClicked(), "Test run item " + testRunItem.getTestRunName()
                    + " isn't selected via shift");
        }

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5920")
    public void verifyTooltipAppearsWhenHoverCursorOverTheLongTestRunName() {
        String longTestRunName = RandomStringUtils.randomAlphabetic(200);

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        testRunsGridPage.clickCreateTestRunButton()
                        .inputTitle(longTestRunName)
                        .clickCreateButton()
                        .backToTestRunsGrid();

        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(longTestRunName);
        Assert.assertEquals(testRunItem.hoverTestRunNameAndGetTooltipText(), longTestRunName,
                "Text from tooltip isn't equals to expected");
    }

    @Test
    @TestCaseKey("ZTP-5918")
    public void verifyUserIsNotAbleToEditTestRunIncludedInTestPlan() {
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestPlan testPlan = new TestPlan("TestPlan " + RandomStringUtils.randomAlphabetic(4));

        TestRunConfiguration testRunConfiguration = new TestRunConfiguration();
        testRunConfiguration.setGroupId(configGroup.getId());
        testRunConfiguration.setOptionId(configOption.getId());

        testPlan.setConfigurations(Collections.singletonList(testRunConfiguration));
        testPlan = tcmService.createTestPlan(project.getId(), testPlan);

        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testPlan.getTitle());
        softAssert.assertEquals(testRunItem.getTooltipTextFromCheckbox(),
                "Bulk actions cannot be performed on this test run as it is part of a test plan. " +
                        "To modify such runs, please access and make changes from the test plan directly.",
                "Tooltip text on checkbox isn't equals to expected");

        // ZTP-5918 User is not able to edit Test runs that are part of the test plan
        TestRunPage testRunPage = testRunItem.clickTestRunItem();
        softAssert.assertTrue(testRunPage.isPageOpened(), "Test plan page isn't opened");

        Dropdown testPlanMenu = testRunPage.open3DotMenu();
        softAssert.assertFalse(testPlanMenu.getDropdownItems().stream()
                                           .anyMatch(option -> option.getText().equalsIgnoreCase("Edit")),
                "User can edit test plan on test run page");

        softAssert.assertAll();
    }
}
