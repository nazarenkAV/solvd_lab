package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.PictureGeneratorUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.automation.gui.common.NavigationMenu;
import com.zebrunner.automation.gui.iam.MemberCard;
import com.zebrunner.automation.gui.project.ProcessProjectModal;
import com.zebrunner.automation.gui.project.ProjectCard;
import com.zebrunner.automation.gui.project.ProjectLogoLoadModal;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.project.ProjectsPage;

@Maintainer("obabich")
public class CreateProjectModalTests extends LogInBase {

    private String projectKey = "";

    @AfterMethod(onlyForGroups = "add-edit-search")
    public void deleteCreatedProject() {
        projectV1Service.deleteProjectByKey(projectKey);
    }

    @AfterMethod()
    public void testsSeparator(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 5, "GMT+3:00");
    }


    // ------------------------------------------- Tests ------------------------------------------------------

    @TestCaseKey({"ZTP-866", "ZTP-5741", "ZTP-5744"})
    @Test(groups = {"add-edit-search", "min_acceptance"})
    public void newProjectCreationFromProjectPageTest() {
        WebDriver webDriver = super.getDriver();

        String projectName = "40 " + RandomStringUtils.randomAlphabetic(37);
        projectKey = "aut" + RandomStringUtils.randomAlphabetic(3);
        projectKey = projectKey.toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);
        ProcessProjectModal createProjectModal = projectsPage.openNewProjectModal()
                                                             .typeProjectName(projectName);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of one empty field"
        );
        softAssert.assertTrue(
                createProjectModal.getCancelButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "Cancel button should be active"
        );
        softAssert.assertTrue(
                createProjectModal.isProjectPublic(),
                "Public-Private toggle should be switched on to 'Public' state by default!"
        );

        createProjectModal.typeProjectKey(projectKey);
        createProjectModal.typeProjectName("");
        super.pause(2);

        softAssert.assertFalse(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Project creation with empty project name is impossible !"
        );

        createProjectModal.typeProjectName(projectName);

        softAssert.assertTrue(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "All fields are filled, save button should be active"
        );

        createProjectModal.typeProjectName(RandomStringUtils.randomAlphabetic(1));
        softAssert.assertTrue(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "All fields are filled(project name 1 symbol), save button should be active"
        );

        createProjectModal.typeProjectName(projectName);
        softAssert.assertTrue(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "All fields are filled(project name 40 symbols), save button should be active"
        );
        createProjectModal.submitModal();

        softAssert.assertTrue(
                projectsPage.waitIsPopUpMessageAppear("Project “" + projectName + "” was successfully created"),
                "Popup message is not as expected!"
        );

        projectsPage = ProjectsPage.openPageDirectly(webDriver);

        softAssert.assertTrue(
                projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Can't find project card with name " + projectName + ", and key " + projectKey
        );
        softAssert.assertAll();
    }

    @Test(groups = {"add-edit-search", "min_acceptance"})
    @TestCaseKey({"ZTP-861", "ZTP-864", "ZTP-863", "ZTP-867", "ZTP-870", "ZTP-871"})
    public void newProjectCreationFromHeaderTest() {
        WebDriver webDriver = super.getDriver();

        String projectName = "Latin letters numerals and spaces"
                + RandomStringUtils.randomAlphabetic(2)
                + RandomStringUtils.randomNumeric(3);
        String maxProjectName = RandomStringUtils.randomAlphabetic(40);
        projectKey = "aut" + RandomStringUtils.randomAlphabetic(3);
        projectKey = projectKey.toUpperCase();
        String lessThanMinimumProjectKey = "AB";
        String maxProjectKey = "ABCDFG";

        SoftAssert softAssert = new SoftAssert();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);
        ProcessProjectModal createProjectModal = projectsPage.getHeader()
                                                             .openProjectsWindow()
                                                             .openNewProjectWindow();

        createProjectModal.typeProjectName(maxProjectName + "1");

        softAssert.assertEquals(
                createProjectModal.getProjectName().length(), 40,
                "User can't use name more than 40 characters"
        );
        softAssert.assertTrue(createProjectModal.isNameLabelContainsAsterisk(), "'Name' label should contain asterisk");
        softAssert.assertTrue(createProjectModal.isKeyLabelContainsAsterisk(), "'Key' label should contain asterisk");

        createProjectModal.typeProjectName(projectName);

        softAssert.assertTrue(
                createProjectModal.isProjectPublic(),
                "Public-Private toggle should be switched on to 'Public' state by default!"
        );
        softAssert.assertFalse(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of one empty field"
        );

        createProjectModal.typeProjectKey(lessThanMinimumProjectKey);

        softAssert.assertFalse(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button should be inactive, because key can't be less than 3"
        );

        createProjectModal.typeProjectKey(maxProjectKey + "A");

        softAssert.assertEquals(
                createProjectModal.getKey().length(), 6,
                "User can't use key more than 6 characters"
        );

        createProjectModal.typeProjectKey(projectKey);

        softAssert.assertTrue(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "All fields are filled, save button should be active"
        );

        createProjectModal.submitModal();
        softAssert.assertTrue(
                projectsPage.waitIsPopUpMessageAppear("Project “" + projectName + "” was successfully created"),
                "Popup message is not as expected!"
        );

        NavigationMenu navigationMenu = new AutomationLaunchesPage(webDriver).getNavigationMenu();

        String trimmedProjectKey = StringUtil.trimProjectKey(projectKey);
        softAssert.assertTrue(
                navigationMenu.waitUntilProjectKeyToBE(trimmedProjectKey),
                "Navigation bar should have new key " + projectKey
        );

        projectsPage.open();

        softAssert.assertTrue(
                projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Can't find project card with name " + projectName + ", and key " + projectKey
        );
        softAssert.assertAll();
    }

    @TestCaseKey("ZTP-877")
    @Test(priority = 6, groups = {"add-edit-search", "min_acceptance"})
    public void createNewPrivateProjectTest() {
        WebDriver webDriver = super.getDriver();

        String projectName = "Automation " + RandomStringUtils.randomAlphabetic(5);
        projectKey = "aut" + RandomStringUtils.randomAlphabetic(3);
        projectKey = projectKey.toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);
        ProcessProjectModal createProjectModal = projectsPage.openNewProjectModal()
                                                             .typeProjectName(projectName);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(
                createProjectModal.isProjectPublic(),
                "Public-Private toggle should be switched on to 'Public' state by default!"
        );
        softAssert.assertFalse(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Save button should be inactive because of one empty field"
        );
        createProjectModal.typeProjectKey(projectKey);

        createProjectModal.changeProjectAccess();
        softAssert.assertTrue(
                createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "All fields are filled, save button should be active"
        );
        softAssert.assertFalse(
                createProjectModal.isProjectPublic(),
                "Public-Private toggle should be switched on to 'Private' state after changing project visibility!"
        );

        createProjectModal.submitModal();
        softAssert.assertTrue(
                projectsPage.waitIsPopUpMessageAppear("Project “" + projectName + "” was successfully created"),
                "Popup message is not as expected!"
        );
        NavigationMenu navigationMenu = new AutomationLaunchesPage(webDriver).getNavigationMenu();

        String trimmedProjectKey = StringUtil.trimProjectKey(projectKey);
        softAssert.assertTrue(
                navigationMenu.waitUntilProjectKeyToBE(trimmedProjectKey),
                "Navigation bar should have new key " + projectKey
        );
        projectsPage.open();

        softAssert.assertTrue(
                projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Can't find project card with name " + projectName + ", and key " + projectKey
        );
        softAssert.assertFalse(
                projectsPage.getProjectCardByProjectKey(projectKey).isProjectPublic(),
                "Project with key " + projectKey + " should be private!"
        );

        String driverName = "login as not project member";
        webDriver = super.getDriver(driverName);
        LoginPage loginPage = LoginPage.openPageDirectly(webDriver);
        loginPage.login(LogInBase.notProjectMember);
        super.pause(2);

        ProjectsPage projectsPageAsNotProjectMember = ProjectsPage.openPageDirectly(webDriver);
        softAssert.assertFalse(
                projectsPageAsNotProjectMember.isProjectWithNameAndKeyExists(projectName, projectKey),
                "A non-member can't see the private project on UI."
        );
        super.quitDriver(driverName);
        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey({"ZTP-872", "ZTP-873", "ZTP-862"})
    public void projectCreationWithInvalidKeyTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.CREATION);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = ("Automation ".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();

        String otherProjectKey = "O" + RandomStringUtils.randomAlphabetic(3).toUpperCase();
        projectV1Service.createProject(RandomStringUtils.randomAlphanumeric(10), otherProjectKey);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal processProjectModal = projectsPage.openNewProjectModal()
                                                              .typeProjectName(projectName)
                                                              .typeProjectKey(otherProjectKey)
                                                              .submitModal();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(
                        MessageEnum.PROJECT_WITH_SUCH_KEY_EXISTS.getDescription()),
                "Popup text not equal to specified.");
        softAssert.assertEquals(processProjectModal.getKeyErrorMessage(), MessageEnum.PROJECT_WITH_GIVEN_KEY_EXISTS.getDescription(),
                "Warning message about duplicate key is not as specified.");//ZTP-872
        softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Duplicate key)");

        processProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(2));
        softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Wrong 'Key' length 2)");

        projectV1Service.deleteProjectByKey(otherProjectKey);

        processProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(3) +
                RandomStringUtils.random(1, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~") +
                RandomStringUtils.randomAlphanumeric(1));
        softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Special symbol in key)");
        softAssert.assertEquals(processProjectModal.getKeyErrorMessage(), MessageEnum.KEY_EDIT_ERROR.getDescription(),
                "Warning message about changing project key is not as specified.");//ZTP-873

        processProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(3) + " " + RandomStringUtils.randomAlphanumeric(1));
        softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Space in key)");
        softAssert.assertEquals(processProjectModal.getKeyErrorMessage(), MessageEnum.KEY_EDIT_ERROR.getDescription(),
                "Warning message about changing project key is not as specified.");

        processProjectModal.typeProjectKey(RandomStringUtils.randomNumeric(6));
        softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Numbers in key)");
        softAssert.assertEquals(processProjectModal.getKeyErrorMessage(), MessageEnum.KEY_EDIT_ERROR.getDescription(),
                "Warning message about changing project key is not as specified.");

        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey({"ZTP-868", "ZTP-865", "ZTP-862"})
    public void projectCreationWithInvalidProjectNameTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.CREATION);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();

        String otherProjectName = RandomStringUtils.randomAlphanumeric(10);
        String otherProjectKey = projectV1Service.createProject(otherProjectName, RandomStringUtils.randomAlphabetic(3)
                                                                                                   .toUpperCase());

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal processProjectModal = projectsPage.openNewProjectModal()
                                                              .typeProjectName(otherProjectName)
                                                              .typeProjectKey(projectKey)
                                                              .submitModal();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(
                        MessageEnum.PROJECT_WITH_SUCH_NAME_EXISTS.getDescription()),
                "Popup text not equal to specified.");
        softAssert.assertEquals(processProjectModal.getNameErrorMessage(),
                MessageEnum.PROJECT_WITH_GIVEN_NAME_EXISTS.getDescription(),
                "Warning message about duplicate name is not as specified.");//ZTP-868
        softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Duplicate name)");

        //ZTP-865
        String notAllowedSpecialCharacters = "\"#$%'*+/:;<=>@\\^_`{|}~";
        for (char c : notAllowedSpecialCharacters.toCharArray()) {
            String projectName = RandomStringUtils.randomAlphabetic(3) + c;
            processProjectModal.typeProjectName(projectName);

            softAssert.assertFalse(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                    "'Create' button must be locked. (Special symbol in name)");
            softAssert.assertEquals(processProjectModal.getNameErrorMessage(),
                    MessageEnum.ONLY_LETTERS_AND_DIGITS_WARNING_MESSAGE.getDescription(),
                    "Warning message about changing project name is not as specified.");
        }

        String allowedSpecialCharacters = " .,-!?&()[]";
        String projectName = RandomStringUtils.randomAlphabetic(3) + allowedSpecialCharacters;
        processProjectModal.typeProjectName(projectName);

        softAssert.assertTrue(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Create' button must be active.");

        projectKey = otherProjectKey;
        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey({"ZTP-869", "ZTP-862"})
    public void projectCreationWithValidProjectKeysTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.CREATION);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = ("Automation ".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();

        String minShortProjectKey = RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphanumeric(2).toUpperCase();
        String maxLongProjectKey = RandomStringUtils.randomAlphanumeric(5).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal processProjectModal = projectsPage.openNewProjectModal()
                                                              .typeProjectName(projectName);

        SoftAssert softAssert = new SoftAssert();
        processProjectModal.typeProjectKey(minShortProjectKey);
        softAssert.assertTrue(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be unlocked.User can enter 3 symbols to the KEY field");

        processProjectModal.typeProjectKey(maxLongProjectKey);
        softAssert.assertTrue(processProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be unlocked.User can enter 3 symbols to the KEY field");
        processProjectModal.submitModal();
        pause(2);
        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        softAssert.assertTrue(projectsPage.isProjectWithKeyExists(maxLongProjectKey),
                "Project with key: " + maxLongProjectKey + " was not found.");
        projectKey = maxLongProjectKey;

        ProjectCard projectCard = projectsPage.getProjectCardByProjectKey(projectKey);

        final String expectedCreator = StringUtil.getExpectedAuthor(userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser()
                                                                                                                      .getUsername())) + " (me)";

        MemberCard memberCard = projectCard.toMembersPageR().getMemberByName(expectedCreator);
        softAssert.assertEquals(memberCard.getRole(), RoleEnum.ADMINISTRATOR.getName(),
                "The creator of the project should be automatically added as project Admin.");//ZTP-878

        softAssert.assertAll();
    }

    @Test(description = "'Create project' modal", groups = "add-edit-search")
    @TestCaseKey({"ZTP-876", "ZTP-5743", "ZTP-875"})
    public void addProjectLogoTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal createProjectModal = projectsPage
                .getHeader()
                .openProjectsWindow()
                .openNewProjectWindow()
                .typeProjectName(projectName)
                .typeProjectKey(projectKey);

        PictureGeneratorUtil.deleteInDefaultPath();

        ProjectLogoLoadModal logoLoadWindow = createProjectModal
                .uploadLogo(PictureGeneratorUtil.generateByWeight(5.3));

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.POPUP_WRONG_FILE_SIZE_OR_EXTENSION.getDescription()),
                "Popup message is not as expected or not exist.");//ZTP-876 Verify user can’t add a logo to the project if the photo is >5mb

        PictureGeneratorUtil.deleteInDefaultPath();
        logoLoadWindow = createProjectModal.uploadLogo(PictureGeneratorUtil.generateByWeight(2));
        logoLoadWindow.clickUpload();

        String logoLinkOnModal = createProjectModal.getLogoLink();
        createProjectModal.submitModal();
        PictureGeneratorUtil.deleteInDefaultPath();

        pause(2);
        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        softAssert.assertFalse(projectsPage.getProjectCardByProjectKey(projectKey).isLogoDefault(),
                "Project logo was not changed as expected.");

        String logoLinkOnProjectCard = projectsPage.getProjectCardByProjectKey(projectKey).getLogoLink();
        softAssert.assertNotNull(logoLinkOnModal, "Project logo link shouldn't be null.");
        softAssert.assertEquals(logoLinkOnProjectCard, logoLinkOnModal, "Project logo link is not as on 'Create project' modal.");
        PictureGeneratorUtil.deleteInDefaultPath();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        String logoLinkOnSidebar = automationLaunchesPage.getNavigationMenu().getSidebarProjectImgLink();
        softAssert.assertEquals(logoLinkOnSidebar, logoLinkOnModal, "Sidebar image link is not as on 'Create project' modal.");

        automationLaunchesPage
                .getHeader()
                .openProjectsWindow()
                .getProjectByKey(projectKey)
                .ifPresentOrElse(project ->
                                softAssert.assertEquals(project.getLogoLink(), logoLinkOnModal,
                                        "Project image link from header is not as on 'Create project' modal."),
                        () -> softAssert.fail(
                                "Unable to find project with key " + projectKey + " on header"));
        //ZTP-875 Verify project logo can be viewed on sidebar and header

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5746")
    @Maintainer("Gammaaldze")
    public void cancelProjectCreationTest() {
        SoftAssert softAssert = new SoftAssert();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal createProjectModal = projectsPage
                .openNewProjectModal();

        createProjectModal.clickCancel();
        softAssert.assertFalse(createProjectModal.isCreateProjectModalPresent(),
                "Create project modal should be closed, after clicking 'Cancel' button !");

        projectsPage.openNewProjectModal();

        createProjectModal.clickCloseButton();
        softAssert.assertFalse(createProjectModal.isCreateProjectModalPresent(),
                "Create project modal should be closed, after clicking 'X' button !");

        projectsPage.openNewProjectModal();

        softAssert.assertTrue(createProjectModal.isCreateProjectModalPresent(), "Create project modal should be opened !");

        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).build().perform();

        softAssert.assertFalse(createProjectModal.isCreateProjectModalPresent(),
                "Create project modal should be closed, after clicking 'ESC' key !");

        softAssert.assertAll();
    }
}