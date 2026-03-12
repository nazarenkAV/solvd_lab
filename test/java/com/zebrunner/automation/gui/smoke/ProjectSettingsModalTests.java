package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.util.PictureGeneratorUtil;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import com.zebrunner.automation.gui.iam.AddMemberModal;
import com.zebrunner.automation.gui.project.ProcessProjectModal;
import com.zebrunner.automation.gui.project.ProjectCard;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.project.ProjectsPage;

@Maintainer("azarouski")
public class ProjectSettingsModalTests extends LogInBase {

    private User createdUser;
    private String projectKey = "";

    @AfterMethod(onlyForGroups = "add-edit-search")
    public void deleteCreatedProject() {
        projectV1Service.deleteProjectByKey(projectKey);
    }


    @BeforeClass()
    public void createUser() {
        createdUser = userService.addRandomUserToTenant();
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedUser() {
        userService.deleteUserById(createdUser.getId());
    }

    @AfterMethod()
    public void testsSeparator(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 5, "GMT+3:00");
    }

    @Test(groups = "add-edit-search")
    public void projectSettingsModalMainElementsPresenceTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.EDITING);

        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        AutomationLaunchesPage runsPage = projectsPage.createProject(projectName, projectKey);

        projectsPage = runsPage
                .getHeader()
                .openProjectsWindow()
                .toProjectsPage();
        ProjectCard projectCard = projectsPage.getProjectCardByProjectKey(projectKey);
        ProcessProjectModal editWindow = projectCard.editCard();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(editWindow.getHeader().getModalTitle()
                                          .getText(), "Edit project", "Title of modal window is not as expected.");
        softAssert.assertTrue(editWindow.isProjectLogoPresent(), "Can't find project logo");
        softAssert.assertTrue(editWindow.isProjectNameFieldPresent(), "Can't find project name field");
        softAssert.assertTrue(editWindow.isAccessDropdownPresent(), "Can't find project visibility switcher");
        softAssert.assertTrue(editWindow.isProjectPublic(), "By default project should be public");

        editWindow.changeProjectAccess();

        softAssert.assertTrue(editWindow.isKeyFieldPresent(), "Can't fin project key field");
        softAssert.assertFalse(editWindow.isProjectPublic(), "After clicking switcher project should become private");
        softAssert.assertTrue(editWindow.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Create button should be inactive because of empty fields");
        softAssert.assertTrue(editWindow.getClose()
                                        .isStateMatches(Condition.VISIBLE_AND_CLICKABLE), "Can't find close button");
        softAssert.assertTrue(editWindow.getCancelButton()
                                        .isStateMatches(Condition.VISIBLE_AND_CLICKABLE), "Can't find cancel button");
        softAssert.assertTrue(editWindow.isSelectLeadMenuPresent(), "Can't find lead selection menu");
        //  softAssert.assertTrue(editWindow.isProjectMembersButtonPresent(), "Can't find project members button");

        editWindow.getClose().click();
        softAssert.assertAll();
    }

    @Test(groups = {"add-edit-search", "min_acceptance"})
    public void editProjectTest() {
        WebDriver webDriver = super.getDriver();

        String projectName = "Automation" + RandomStringUtils.randomAlphabetic(5);
        projectKey = "aut" + RandomStringUtils.randomAlphabetic(3);
        projectKey = projectKey.toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);

        AutomationLaunchesPage launchesPage = projectsPage.createProject(projectName, projectKey);
        super.pause(4);

        projectsPage = launchesPage.getHeader()
                                   .openProjectsWindow()
                                   .toProjectsPage();
        ProjectCard projectCard = projectsPage.getProjectCardByProjectKey(projectKey);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectCard.isProjectPublic(), "New project should be public by default");

        String oldKey = projectKey;

        String changeName = "Change" + RandomStringUtils.randomAlphabetic(5);
        projectKey = "ch" + RandomStringUtils.randomAlphabetic(3);
        projectKey = projectKey.toUpperCase();

        projectCard.editCard()
                   .typeProjectName(changeName)
                   .typeProjectKey(projectKey)
                   .changeProjectAccess()
                   .submitModal();

        softAssert.assertTrue(
                projectsPage.isProjectWithNameAndKeyExists(changeName, projectKey),
                "Can't find edited project with name: " + changeName + ", and key: " + projectKey
        );
        softAssert.assertFalse(
                projectsPage.isProjectWithNameAndKeyExists(projectName, oldKey),
                String.format("Cant find project with '%s' name and '%s' key", projectName, oldKey)
        );
        ProjectCard updatedCard = projectsPage.getProjectCardByProjectKey(projectKey);

        softAssert.assertFalse(updatedCard.isProjectPublic(), "Updated project should be private");
        softAssert.assertAll();
    }

    @Test(description = "'Project settings' popup", groups = "add-edit-search")
    @TestCaseKey({"ZTP-845", "ZTP-846", "ZTP-847", "ZTP-849", "ZTP-855", "ZTP-5735"})
    public void editProjectNameTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.EDITING);

        projectKey = ("AUT".concat(RandomStringUtils.randomAlphanumeric(3))).toUpperCase();

        String minShortProjectName = RandomStringUtils.randomAlphanumeric(1);
        String usualProjectName = RandomStringUtils.randomAlphanumeric(5);
        String maxLongProjectName = RandomStringUtils.randomAlphabetic(29)
                + " "
                + RandomStringUtils.randomNumeric(10);
        String allowedSpecialCharactersProjectName = "T,.-!?&()[]";

        String testProjectName = "Test" + RandomStringUtils.randomAlphanumeric(10);
        String testProjectKey = "T" + RandomStringUtils.randomAlphabetic(3);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(usualProjectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(testProjectName, testProjectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey)
                                                           .typeProjectName(testProjectName)
                                                           .submitModal();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.PROJECT_WITH_SUCH_NAME_EXISTS.getDescription()),
                "Warning message about duplicate project name is not as specified.");//ZTP-849

        editProjectModal.getClose().click();
        projectsPage.getProjectCardByProjectKey(projectKey)
                    .editCard()
                    .typeProjectName(minShortProjectName)
                    .submitModal();
        projectsPage = ProjectsPage.getInstance(getDriver());

        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(minShortProjectName, projectKey),
                "Project with name: " + minShortProjectName + " and key: " + projectKey + " was not found.");

        editProjectModal = projectsPage
                .getProjectCardByProjectKey(projectKey)
                .editCard();

        editProjectModal.typeProjectName("");

        softAssert.assertFalse(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be inactive. Edit project is impossible with empty name !");

        editProjectModal.typeProjectName(maxLongProjectName + "T");

        softAssert.assertEquals(editProjectModal.getProjectName()
                                                .length(), 40, "User can't use name more than 40 characters");

        editProjectModal.typeProjectName(maxLongProjectName)
                        .submitModal();

        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(maxLongProjectName, projectKey),
                "Project with name: " + maxLongProjectName + " and key: " + projectKey + " was not found.");

        editProjectModal = projectsPage
                .getProjectCardByProjectKey(projectKey)
                .editCard()
                .typeProjectName(allowedSpecialCharactersProjectName);

        softAssert.assertTrue(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be active. Special characters should be allowed !");

        editProjectModal.submitModal();

        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(allowedSpecialCharactersProjectName, projectKey),
                "Project with name: " + allowedSpecialCharactersProjectName + " and key: " + projectKey + " was not found.");

        projectV1Service.deleteProjectByKey(testProjectKey);
        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-848", "ZTP-855"})
    public void editProjectNameWithSpecialSymbolsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.EDITING);

        SoftAssert softAssert = new SoftAssert();

        projectKey = ("AUT".concat(RandomStringUtils.randomAlphanumeric(3))).toUpperCase();

        String projectName = RandomStringUtils.randomAlphanumeric(5);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);
        projectsPage = ProjectsPage.openPageDirectly(getDriver());

        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey);

        String notAllowedSpecialCharacters = "\"#$%'*+/:;<=>@\\^_`{|}~";
        for (char c : notAllowedSpecialCharacters.toCharArray()) {
            projectName = RandomStringUtils.randomAlphabetic(3) + c;
            editProjectModal.typeProjectName(projectName);

            softAssert.assertFalse(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                    "'Save' button must be locked. (Special symbol in name)");
            softAssert.assertEquals(editProjectModal.getNameErrorMessage(),
                    MessageEnum.ONLY_LETTERS_AND_DIGITS_WARNING_MESSAGE.getDescription(),
                    "Warning message about changing project key is not as specified.");
        }

        String allowedSpecialCharacters = " .,-!?&()[]";
        projectName = RandomStringUtils.randomAlphabetic(3) + allowedSpecialCharacters;
        editProjectModal.typeProjectName(projectName);

        softAssert.assertTrue(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be active.");

        softAssert.assertAll();
    }

    @Test(description = "'Project settings' popup", groups = "add-edit-search")
    @TestCaseKey({"ZTP-850", "ZTP-852", "ZTP-853", "ZTP-855", "ZTP-5737"})
    public void editProjectKeyTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.EDITING);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();

        String minShortProjectKey = RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphanumeric(2).toUpperCase();
        String maxLongProjectKey = RandomStringUtils.randomAlphanumeric(5).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase();

        String testProjectName = RandomStringUtils.randomAlphanumeric(10);
        String otherProjectName = RandomStringUtils.randomAlphanumeric(10);
        String otherProjectKey = "O" + RandomStringUtils.randomAlphabetic(3).toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(testProjectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(otherProjectName, otherProjectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.isPageOpened();
        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey)
                                                           .typeProjectKey(otherProjectKey)
                                                           .submitModal();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(
                        MessageEnum.PROJECT_WITH_SUCH_KEY_EXISTS.getDescription()),
                "Popup text not equal to specified.");
        softAssert.assertEquals(editProjectModal.getKeyErrorMessage(), MessageEnum.PROJECT_WITH_GIVEN_KEY_EXISTS.getDescription(),
                "Error message about duplicate project key is not as specified.");

        editProjectModal.getClose().click();
        editProjectModal = projectsPage.editCertainProjectByKey(projectKey);
        editProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(2));

        softAssert.assertFalse(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Wrong 'Key' length)");

        editProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(7));

        softAssert.assertEquals(editProjectModal.getKey()
                                                .length(), 6, "User cannot use a key that has more than 6 characters");

        //        editProjectModal.submitModal();
        editProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(3) +
                RandomStringUtils.random(1, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~") +
                RandomStringUtils.randomAlphanumeric(1));

        softAssert.assertFalse(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Special symbol in key)");//ZTP-853
        softAssert.assertEquals(editProjectModal.getKeyErrorMessage(), MessageEnum.KEY_EDIT_ERROR.getDescription(),
                "Warning message about changing project key is not as specified.");

        editProjectModal.typeProjectKey(RandomStringUtils.randomAlphabetic(3) + " " + RandomStringUtils.randomAlphanumeric(1));

        softAssert.assertFalse(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Space in key)");
        softAssert.assertEquals(editProjectModal.getKeyErrorMessage(), MessageEnum.KEY_EDIT_ERROR.getDescription(),
                "Warning message about changing project key is not as specified.");

        editProjectModal.typeProjectKey(RandomStringUtils.randomNumeric(6));

        softAssert.assertFalse(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button must be locked. (Numbers in key)");
        softAssert.assertEquals(editProjectModal.getKeyErrorMessage(), MessageEnum.KEY_EDIT_ERROR.getDescription(),
                "Warning message about changing project key is not as specified.");

        editProjectModal.typeProjectKey(minShortProjectKey);

        softAssert.assertTrue(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button should be clickable after typing key with 3 characters !");

        editProjectModal.submitModal();
        projectsPage = ProjectsPage.getInstance(getDriver());

        softAssert.assertTrue(projectsPage.isProjectWithKeyExists(minShortProjectKey),
                "Project with key: " + minShortProjectKey + " was not found.");

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        editProjectModal = projectsPage.editCertainProjectByKey(minShortProjectKey);
        editProjectModal.typeProjectKey(maxLongProjectKey);

        softAssert.assertTrue(editProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "'Save' button should be clickable after typing key with 6 characters !");

        editProjectModal.submitModal();

        softAssert.assertTrue(projectsPage.isProjectWithKeyExists(maxLongProjectKey),
                "Project with key: " + maxLongProjectKey + " was not found.");

        projectV1Service.deleteProjectByKey(otherProjectKey);
        projectKey = maxLongProjectKey;
        softAssert.assertAll();
    }

    @TestCaseKey({"ZTP-859", "ZTP-860"})
    @Test(groups = {"add-edit-search", "min_acceptance", "user-was-created"})
    public void editProjectLeadTest() {
        WebDriver webDriver = super.getDriver();

        projectKey = "aut" + RandomStringUtils.randomAlphabetic(3);
        projectKey = projectKey.toUpperCase();
        String projectName = "Automation" + RandomStringUtils.randomAlphabetic(5);

        String testUserName = ConfigHelper.getUserProperties().getTestUser().getUsername();
        String existingUsername = createdUser.getUsername();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);
        projectsPage.createProject(projectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(webDriver);
        projectsPage.assertPageOpened();

        projectsPage.getProjectCardByProjectKey(projectKey)
                    .editCard();

        MembersPageR membersPage = MembersPageR.openPageDirectly(webDriver, projectKey);
        AddMemberModal addMemberWindow = membersPage.openAddMemberModal()
                                                    .typeUsernameWithSuggestionChoosing(testUserName)
                                                    .typeUsernameWithSuggestionChoosing(existingUsername);
        addMemberWindow.getModalTitle().click();
        addMemberWindow.chooseRoleByName("Manager");
        addMemberWindow.getAddButton().click();
        addMemberWindow.getAddButton().waitUntil(Condition.DISAPPEAR);

        projectsPage = ProjectsPage.openPageDirectly(webDriver);

        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey);
        editProjectModal.setLeadToTheProject(testUserName, false);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(
                editProjectModal.getSelectedLead(), testUserName,
                "Project lead is not as we select!"
        );

        editProjectModal.submitModal();
        softAssert.assertEquals(
                projectsPage.getProjectCardByProjectKey(projectKey).getLead(), testUserName,
                "Project lead was not set to " + testUserName
        );

        projectsPage = ProjectsPage.openPageDirectly(webDriver);
        editProjectModal = projectsPage.editCertainProjectByKey(projectKey)
                                       .setLeadToTheProject(existingUsername, false);

        softAssert.assertEquals(
                editProjectModal.getSelectedLead(), existingUsername,
                "Project lead is not as we select!"
        );

        editProjectModal.submitModal();
        projectsPage = ProjectsPage.getInstance(webDriver);
        projectsPage.assertPageOpened();

        softAssert.assertEquals(
                projectsPage.getProjectCardByProjectKey(projectKey).getLead(), existingUsername,
                "Project lead was not set to " + existingUsername
        );
        softAssert.assertAll();
    }

    @Test(description = "'Project settings' popup", groups = "add-edit-search")
    @TestCaseKey("ZTP-858")
    public void editProjectPrivacyTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.EDITING);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey)
                                                           .changeProjectAccess();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(editProjectModal.isProjectPublic(),
                "Project switcher was not switched to private, as expected.");

        editProjectModal.submitModal();
        softAssert.assertFalse(projectsPage.getProjectCardByProjectKey(projectKey).isProjectPublic(),
                "Project is not private, as expected.");

        editProjectModal = projectsPage.editCertainProjectByKey(projectKey)
                                       .changeProjectAccess();
        softAssert.assertTrue(editProjectModal.isProjectPublic(),
                "Project switcher was not switched to public, as expected.");

        editProjectModal.submitModal();
        softAssert.assertTrue(projectsPage.getProjectCardByProjectKey(projectKey).isProjectPublic(),
                "Project is not public, as expected.");
        softAssert.assertAll();
    }

    @Test(description = "'Project settings' popup", groups = "add-edit-search")
    @TestCaseKey({"ZTP-856", "ZTP-857"})
    public void editProjectLogoTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);
        Label.attachToTest(TestLabelsConstant.PROJECTS, TestLabelsConstant.EDITING);

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey);

        PictureGeneratorUtil.deleteInDefaultPath();
        editProjectModal.uploadLogo(PictureGeneratorUtil.generateByWeight(2))
                        .clickUpload();
        editProjectModal.submitModal();

        PictureGeneratorUtil.deleteInDefaultPath();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(projectsPage.getProjectCardByProjectKey(projectKey).isLogoDefault(),
                "Project logo was not changed as expected.");//ZTP-856 Verify that it is possible to change a project logo

        String logoId = projectsPage.getProjectCardByProjectKey(projectKey).getLogoLink();
        editProjectModal = projectsPage.editCertainProjectByKey(projectKey);
        editProjectModal.uploadLogo(PictureGeneratorUtil.generateByWeight(5.3));

        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.POPUP_WRONG_FILE_SIZE_OR_EXTENSION.getDescription()),
                "Popup message is not as expected or not exist.");
        softAssert.assertFalse(editProjectModal.getSubmitButton()
                                               .isStateMatches(Condition.CLICKABLE));//ZTP-857 Verify that it is impossible to change a project logo if the photo is >2mb

        editProjectModal.getCancelButton().click();

        softAssert.assertEquals(projectsPage.getProjectCardByProjectKey(projectKey).getLogoLink(), logoId,
                "Project logo should not change.");

        PictureGeneratorUtil.deleteInDefaultPath();
        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey("ZTP-5745")
    @Maintainer("Gmamaladze")
    public void cancelProjectEditingTest() {
        SoftAssert softAssert = new SoftAssert();

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal editProjectModal = projectsPage.editCertainProjectByKey(projectKey);

        editProjectModal.clickCancel();
        softAssert.assertFalse(editProjectModal.isCreateProjectModalPresent(),
                "Edit project modal should be closed, after clicking 'Cancel' button !");

        editProjectModal = projectsPage.editCertainProjectByKey(projectKey);
        editProjectModal.clickCloseButton();
        softAssert.assertFalse(editProjectModal.isCreateProjectModalPresent(),
                "Edit project modal should be closed, after clicking 'X' button !");

        editProjectModal = projectsPage.editCertainProjectByKey(projectKey);

        softAssert.assertTrue(editProjectModal.isCreateProjectModalPresent(), "Edit project modal should be opened !");

        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).build().perform();

        softAssert.assertFalse(editProjectModal.isCreateProjectModalPresent(),
                "Edit project modal should be closed, after clicking 'ESC' key !");

        softAssert.assertAll();
    }
}