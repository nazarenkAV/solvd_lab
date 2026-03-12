package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.common.NavigationMenu;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.automation.gui.integration.SettingsPageR;
import com.zebrunner.automation.gui.launcher.TestRunsLauncher;
import com.zebrunner.automation.gui.project.DeleteProjectModal;
import com.zebrunner.automation.gui.project.ProcessProjectModal;
import com.zebrunner.automation.gui.project.ProjectCard;
import com.zebrunner.automation.gui.project.ProjectsMenu;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.reporting.dashboard.MainDashboardsPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.milestone.MilestonePage;
import com.zebrunner.automation.legacy.BreadcrumbsEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.util.LocalStorageManager;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Maintainer("obabich")
@Slf4j
public class ProjectPageElementsTest extends LogInBase {
    private final List<String> projectKeys = new ArrayList<>();
    private String projectKey = "";
    private Project createdProject;

    @BeforeClass
    public void getProjectKey() {
        createdProject = LogInBase.project;
    }

    @AfterMethod(onlyForGroups = "list-of-projects")
    public void deleteProjects() {
        projectKeys.forEach(projectV1Service::deleteProjectByKey);
    }

    @AfterMethod()
    public void testsSeparator(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 5, "GMT+3:00");
    }

    @AfterMethod(onlyForGroups = "add-edit-search")
    public void deleteCreatedProject() {
        projectV1Service.deleteProjectByKey(projectKey);
    }

    @Test(groups = "min_acceptance")
    public void projectsMenuInNavigationBarTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        SoftAssert softAssert = new SoftAssert();
        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), createdProject);

        softAssert.assertEquals(automationLaunchesPage.getNavigationMenu()
                                                      .getProjectKey(), createdProject.getTrimmedProjectKey(),
                "Project key differs expected");
        softAssert.assertTrue(automationLaunchesPage.getNavigationMenu().isProjectPhotoPresent());
        softAssert.assertAll();
    }

    @Test
    public void projectsMenuInHeaderTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), createdProject);
        ProjectsMenu projectsMenu = automationLaunchesPage.getHeader().openProjectsWindow();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsMenu.areProjectsPresent(), "Can't find projects in project menu");
        softAssert.assertTrue(projectsMenu.isViewAllProjectsButtonPresent(), "View All Projects Button is not present");
        softAssert.assertTrue(projectsMenu.isCreateNewProjectButtonPresent(), "Create New Project Button is not resent");

        ProcessProjectModal createProjectWindow = projectsMenu.openNewProjectWindow();
        softAssert.assertTrue(createProjectWindow.isCreateProjectModalPresent(), "Project creation window should present");
        createProjectWindow.getClose().click();

        softAssert.assertAll();
    }

    @Test
    public void projectPageElementsPresenceTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(projectsPage.getTitle(),
                ProjectsPage.PAGE_NAME, "Projects page title differs expected");
        softAssert.assertTrue(projectsPage.isSearchFieldPresent(), "Can't find search field");
        softAssert.assertTrue(projectsPage.isNewProjectButtonPresent(), "Can't find new project button");

        ProcessProjectModal createProjectWindow = projectsPage.openNewProjectModal();
        softAssert.assertTrue(createProjectWindow.isCreateProjectModalPresent(), "Can't find project creation window");
        createProjectWindow.getClose().click();
        softAssert.assertTrue(projectsPage.getHeader().isUIObjectPresent(), "Can't find header");

        PaginationR pagination = projectsPage.getPagination();
        softAssert.assertEquals(pagination.getNumberOfItemsOnThePage(), projectsPage.getNumberOfProjectCards(),
                "Number of project cards differs to pagination info");
        softAssert.assertAll();
    }

    @Test
    public void createNewProjectModalElementsPresence() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProcessProjectModal createProjectModal = projectsPage.openNewProjectModal();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(createProjectModal.getHeader()
                                                  .getText(), "Create project", "Modal title is not as expected!");
        softAssert.assertTrue(createProjectModal.isProjectLogoPresent(), "Can't find project logo");
        softAssert.assertTrue(createProjectModal.isProjectNameFieldPresent(), "Can't find project name field");
        softAssert.assertTrue(createProjectModal.isProjectPublic(), "By default project should be public");
        softAssert.assertTrue(createProjectModal.isAccessDropdownPresent(), "Can't find project visibility switcher");

        createProjectModal.changeProjectAccess();
        softAssert.assertFalse(createProjectModal.isProjectPublic(), "After clicking switcher project should become private");
        softAssert.assertTrue(createProjectModal.isKeyFieldPresent(), "Can't fin project key field");
        softAssert.assertFalse(createProjectModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "Create button should be inactive because of empty fields");
        softAssert.assertTrue(createProjectModal.getClose()
                                                .isStateMatches(Condition.VISIBLE_AND_CLICKABLE), "Can't find close button");
        softAssert.assertTrue(createProjectModal.getCancelButton()
                                                .isStateMatches(Condition.VISIBLE_AND_CLICKABLE), "Can't find cancel button");
        createProjectModal.getClose().click();
        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey("ZTP-835")
    public void searchFieldTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(projectsPage.isSearchFieldPresent(), "Can't find search field");

        projectsPage.search(projectName);
        projectsPage.getProjectCards()
                    .forEach(card -> softAssert.assertTrue(card.getProjectName().contains(projectName),
                            "All project titles should contain string from search field, card: "
                                    + card.getProjectName() + ", search field: " + projectName));
        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Can't find project after search field filter");

        projectsPage.search(RandomStringUtils.randomAlphabetic(7));
        softAssert.assertTrue(projectsPage.getEmptyPlaceholder()
                                          .isUIObjectPresent(), "No project content is not as expected!");
        softAssert.assertEquals(projectsPage.getEmptyPlaceholder()
                                            .getEmptyPlaceHolderTitle(), MessageEnum.NO_RESULT_MESSAGE.getDescription(),
                "No project content text isn't equals to expected");
        softAssert.assertTrue(projectsPage.getEmptyPlaceholder()
                                          .isEmptyPlaceholderImagePresent(), "No project content icon isn't present");

        projectsPage.search(projectKey);
        projectsPage.getProjectCards()
                    .forEach(card -> softAssert.assertTrue(card.getKey().contains(projectKey),
                            "All project titles should contain string from search field, card: "
                                    + card.getKey() + ", search field: " + projectKey));
        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Can't find project after searching by projectKey");
        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey({"ZTP-838", "ZTP-837", "ZTP-839", "ZTP-5734"})
    public void projectSortingTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        List<ProjectCard> projectCards = projectsPage.getProjectCards();

        List<String> defaultUiNames = projectCards.stream().map(ProjectCard::getProjectName)
                                                  .collect(Collectors.toList());

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(defaultUiNames, projectsPage.getDefaultSortedAllProjectsName(),
                "Default sorting(alphabetical by name) is not as expected!");//ZTP-839 Verify default sorting of projects is alphabetical

        //by name ASC(A-Z)
        projectsPage.sortByName();
        List<String> uiNames = projectCards.stream().map(ProjectCard::getProjectName).collect(Collectors.toList());
        List<String> sortedNames = projectCards.stream().map(ProjectCard::getProjectName)
                                               .sorted(Comparator.comparing(String::trim, String.CASE_INSENSITIVE_ORDER))
                                               .collect(Collectors.toList());
        softAssert.assertEquals(uiNames, sortedNames, "Sorting by name is not as expected !"); //ZTP-5734

        //by name (reverse order (DESC(Z-A)))
        projectsPage.sortByName();
        uiNames = projectCards.stream().map(ProjectCard::getProjectName).collect(Collectors.toList());
        sortedNames = projectCards.stream().map(ProjectCard::getProjectName)
                                  .sorted(Comparator.comparing(String::trim, String.CASE_INSENSITIVE_ORDER).reversed())
                                  .collect(Collectors.toList());
        softAssert.assertEquals(uiNames, sortedNames, "Sorting by name(reverse order) is not as expected!");//ZTP-5734

        projectsPage.sortByName();
        defaultUiNames = projectCards.stream().map(ProjectCard::getProjectName).collect(Collectors.toList());
        softAssert.assertEquals(defaultUiNames, projectsPage.getDefaultSortedAllProjectsName(),
                "After sorting name again the projects name should be sorted by default !"); //ZTP-5734

        //by key ASC(A-Z)
        projectsPage.sortByKey();
        projectCards = projectsPage.getProjectCards();
        List<String> uiKeys = projectCards.stream().map(ProjectCard::getKey).collect(Collectors.toList());
        List<String> sortedKeys = projectCards.stream().map(ProjectCard::getKey)
                                              .sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        softAssert.assertEquals(uiKeys, sortedKeys, "Sorting by key is not as expected!");//ZTP-837

        projectsPage.sortByKey();//by key (reverse order (DESC(Z-A)))
        projectCards = projectsPage.getProjectCards();
        uiKeys = projectCards.stream().map(ProjectCard::getKey).collect(Collectors.toList());
        sortedKeys = projectCards.stream().map(ProjectCard::getKey)
                                 .sorted(String::compareToIgnoreCase)
                                 .sorted(Collections.reverseOrder())
                                 .collect(Collectors.toList());
        softAssert.assertEquals(uiKeys, sortedKeys, "Sorting by key(reverse order) is not as expected!");//ZTP-837

        //by default
        projectsPage.sortByKey();
        defaultUiNames = projectCards.stream().map(ProjectCard::getProjectName).collect(Collectors.toList());
        softAssert.assertEquals(defaultUiNames, projectsPage.getDefaultSortedAllProjectsName(),
                "After sorting key again the projects name should be sorted by default !"); //ZTP-837

        //by createdAt
        projectsPage.sortByCreated();
        projectCards = projectsPage.getProjectCards();
        List<Date> uiCreatedDates = projectCards.stream().map(ProjectCard::getCreatedDate).collect(Collectors.toList());
        List<Date> sortedCreatedDates = projectCards.stream().map(ProjectCard::getCreatedDate)
                                                    .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        softAssert.assertEquals(uiCreatedDates, sortedCreatedDates, "Sorting by created works is not as expected!");//ZTP-838

        //by createdAt reverse order
        projectsPage.sortByCreated();
        projectCards = projectsPage.getProjectCards();
        uiCreatedDates = projectCards.stream().map(ProjectCard::getCreatedDate).collect(Collectors.toList());
        sortedCreatedDates = projectCards.stream().map(ProjectCard::getCreatedDate)
                                         .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        softAssert.assertEquals(uiCreatedDates, sortedCreatedDates, "Sorting by created(reverse order) works is not as expected!");//ZTP-838

        //by default
        projectsPage.sortByCreated();
        defaultUiNames = projectCards.stream().map(ProjectCard::getProjectName).collect(Collectors.toList());
        softAssert.assertEquals(defaultUiNames, projectsPage.getDefaultSortedAllProjectsName(),
                "After sorting created again the projects name should be sorted by default !"); //ZTP-837

        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-842", "ZTP-844"})
    public void userRemainsOnTheSameProjectWhenSwitchingBetweenProjectScopePagesTest() {
        WebDriver webDriver = super.getDriver();
        String urlContainingPattern = String.format("/projects/%s/", createdProject.getKey());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, createdProject);

        Assert.assertEquals(
                automationLaunchesPage.getNavigationMenu().getProjectKey(), createdProject.getTrimmedProjectKey(),
                "Project key differs expected (Automation launches page)"
        );

        MilestonePage milestonePage = automationLaunchesPage.getNavigationMenu().toMilestonePage();
        Assert.assertEquals(
                milestonePage.getNavigationMenu().getProjectKey(), createdProject.getTrimmedProjectKey(),
                "Project key differs expected(Milestone page)"
        );
        Assert.assertTrue(
                webDriver.getCurrentUrl().contains(urlContainingPattern),
                "Current URL doesn't contain project key " + createdProject.getKey() + "( on Milestones page)"
        );

        MembersPageR membersPage = milestonePage.getNavigationMenu().toMembersPageR();
        Assert.assertEquals(
                membersPage.getNavigationMenu().getProjectKey(), createdProject.getTrimmedProjectKey(),
                "Project key differs expected(Members page)"
        );
        Assert.assertTrue(
                webDriver.getCurrentUrl().contains(urlContainingPattern),
                "Current URL doesn't contain project key " + createdProject.getKey() + "( on Members page)"
        );

        MainDashboardsPageR mainDashboardsPageR = membersPage.getNavigationMenu().toMainDashboardPage();
        Assert.assertEquals(
                mainDashboardsPageR.getNavigationMenu().getProjectKey(), createdProject.getTrimmedProjectKey(),
                "Project key differs expected(Dashboards page)"
        );
        Assert.assertTrue(
                webDriver.getCurrentUrl().contains(urlContainingPattern),
                "Current URL doesn't contain project key " + createdProject.getKey() + "( on Dashboards page)"
        );

        automationLaunchesPage = membersPage.getNavigationMenu().toTestRunsPage();
        Assert.assertEquals(
                automationLaunchesPage.getNavigationMenu().getProjectKey(), createdProject.getTrimmedProjectKey(),
                "Project key differs expected(Launches page)"
        );
        Assert.assertTrue(
                webDriver.getCurrentUrl().contains(urlContainingPattern),
                "Current URL doesn't contain project key " + createdProject.getKey() + " (on Launches page)"
        );
    }

    @Test()
    @TestCaseKey({"ZTP-843"})
    public void userCanReturnToProjectScopePagesViaHeaderTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.PROJECTS);

        SoftAssert softAssert = new SoftAssert();
        UsersPageR usersPage = UsersPageR.openPageDirectly(getDriver());
        softAssert.assertTrue(getDriver().findElements(By.xpath(NavigationMenu.NAVIGATION_MENU_XPATH)).isEmpty(),
                "Navigation menu shouldn't be present on Users page!");
        ProjectsPage projectsPage = usersPage.getHeader().openProjectsWindow().toProjectsPage();
        softAssert.assertTrue(projectsPage.isPageOpened(), "Project page wasn't opened from header on Users page!");

        SettingsPageR settingsPage = SettingsPageR.openPageDirectly(getDriver());
        softAssert.assertTrue(getDriver().findElements(By.xpath(NavigationMenu.NAVIGATION_MENU_XPATH)).isEmpty(),
                "Navigation menu shouldn't be present on Settings page!");
        projectsPage = settingsPage.getHeader().openProjectsWindow().toProjectsPage();
        softAssert.assertTrue(projectsPage.isPageOpened(), "Project page wasn't opened from header on Settings page!");

        projectsPage.getProjectCardByProjectKey(createdProject.getKey()).getRootExtendedElement().click();
        softAssert.assertTrue(getDriver().findElements(By.xpath(NavigationMenu.NAVIGATION_MENU_XPATH)).isEmpty(),
                "Navigation menu should be present after clicking on project card!");

        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey("ZTP-5736")
    @Maintainer("Gmamaladze")
    public void verifyUserCanOpenMembersPageViaProjectSettingsDropdown() {
        SoftAssert softAssert = new SoftAssert();

        final String expectedMember = StringUtil.getExpectedAuthor(userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser()
                                                                                                                     .getUsername())) + " (me)";
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = "Automation".concat(RandomStringUtils.randomAlphabetic(5));

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        MembersPageR membersPage = projectsPage
                .getProjectCardByProjectKey(projectKey).toMembersPageR();

        softAssert.assertTrue(membersPage.isPageOpened(), "Member page should be opened !");
        softAssert.assertTrue(membersPage.isMemberPresent(expectedMember), "Member is not as expected !");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5739")
    @Maintainer("Gmamaladze")
    public void verifyUserCanDeleteProject() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);
        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage = projectsPage.getProjectCardByProjectKey(projectKey)
                                   .clickDeleteCard()
                                   .deleteProject();

        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.PROJECT_DELETE.getDescription(projectName)),
                "Project delete message is not as specified !");
        softAssert.assertFalse(projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Project should be deleted !");

        ProjectsMenu projectsMenu = projectsPage.getHeader().openProjectsWindow();

        softAssert.assertFalse(projectsMenu.isProjectPresent(projectName, projectKey), "Deleted project should not be present on projects menu !");

        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey("ZTP-5738")
    @Maintainer("Gmamaladze")
    public void verifyNumberOfProjectsInTheGrid() {
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        String actualNumberOfProjects = projectsPage.getNumberOfProjectCards();
        String projectsCountMessage = projectsPage.getProjectsCountMessages();

        Pattern pattern = Pattern.compile("\\b(\\d+)\\b");

        Matcher matcher = pattern.matcher(projectsCountMessage);
        if (matcher.find()) {
            String matchedNumberStr = matcher.group(1);

            Assert.assertEquals(actualNumberOfProjects, matchedNumberStr, "Project count mismatch !");
        } else {
            throw new IllegalStateException("No number found in the string.");
        }
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey("ZTP-5740")
    @Maintainer("Gmamaladze")
    public void verifyUserCanCancelProjectDelete() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        DeleteProjectModal deleteProjectModal = projectsPage.getProjectCardByProjectKey(projectKey)
                                                            .clickDeleteCard();

        deleteProjectModal.clickCancel();
        softAssert.assertFalse(deleteProjectModal.isModalOpened(),
                "Delete modal should be closed, after clicking 'Cancel' button !");

        deleteProjectModal = projectsPage.getProjectCardByProjectKey(projectKey)
                                         .clickDeleteCard();

        deleteProjectModal.clickClose();
        softAssert.assertFalse(deleteProjectModal.isModalOpened(),
                "Delete modal should be closed, after clicking 'X' button !");

//        deleteProjectModal = projectsPage.getProjectCardByProjectKey(projectKey)
//                .clickDeleteCard();
//        softAssert.assertTrue(deleteProjectModal.isModalOpened(),
//                "Delete modal should be opened !");
//
//        Actions actions = new Actions(getDriver());
//        actions.sendKeys(Keys.ESCAPE).build().perform();
//
//        softAssert.assertFalse(deleteProjectModal.isModalOpened(),
//                "Delete modal should be closed, after clicking 'ESC' key !");

        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @TestCaseKey({"ZTP-5742", "ZTP-1824"})
    @Maintainer("Gmamaladze")
    public void verifyUserCanNavigateToProjectPageViaBreadCrumbs() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);

        new LocalStorageManager(getDriver()).setItem(LocalStorageKey.ZBR_AUTH, iamService.login(UsersEnum.MAIN_ADMIN.getUser()));

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);

        softAssert.assertTrue(automationLaunchesPage.getBreadcrumbs()
                                                    .isBreadcrumbPresent(BreadcrumbsEnum.PROJECTS.getBreadcrumb()),
                "Breadcrumb 'Projects' should be present on launches page !");

        automationLaunchesPage.clickBreadcrumb(BreadcrumbsEnum.PROJECTS.getBreadcrumb());

        ProjectsPage projectsPage = new ProjectsPage(getDriver());

        softAssert.assertTrue(projectsPage.isPageOpened(),
                "Project page is not opened after clicking breadcrumb in launches page !");
        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Project should present on list !");

        TestRunsLauncher testRunsLauncher = AutomationLaunchesPage.
                openPageDirectly(getDriver(), projectKey)
                .toLaunchesPage();

        softAssert.assertTrue(testRunsLauncher.getBreadcrumbs()
                                              .isBreadcrumbPresent(BreadcrumbsEnum.PROJECTS.getBreadcrumb()),
                "Breadcrumb 'Projects' should be present on launcher page !");

        testRunsLauncher.clickBreadcrumb(BreadcrumbsEnum.PROJECTS.getBreadcrumb());

        softAssert.assertTrue(projectsPage.isPageOpened(),
                "Project page is not opened after clicking breadcrumb in launcher page !");
        softAssert.assertTrue(projectsPage.isProjectWithNameAndKeyExists(projectName, projectKey),
                "Project should present on list !");

        softAssert.assertAll();
    }

    @Test(groups = "list-of-projects")
    @TestCaseKey({"ZTP-841", "ZTP-1229"})
    @Maintainer("Gmamaladze")
    public void verifyUserCanSeeListOfThreeLastSelectedProjects() {
        SoftAssert softAssert = new SoftAssert();

        Map<String, String> projectMap = new HashMap<>();

        AutomationLaunchesPage automationLaunchesPage = null;
        String lastProjectName = null;

        for (int i = 1; i <= 3; i++) {
            String projectName = "Automation" + i + " " + RandomStringUtils.randomAlphabetic(5);
            String projectKey = ("au" + (char) ('a' + i) + RandomStringUtils.randomAlphabetic(2)).toUpperCase();

            projectMap.put(projectName, projectKey);
            projectV1Service.createProject(projectName, projectKey);
            projectKeys.add(projectKey);

            LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());
            localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(LogInBase.notProjectMember));

            automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
            lastProjectName = projectName;
            pause(3);
        }

        ProjectsMenu projectsMenu = automationLaunchesPage
                .getHeader()
                .openProjectsWindow();

        for (Map.Entry<String, String> entry : projectMap.entrySet()) {
            String projectName = entry.getKey();

            softAssert.assertTrue(projectsMenu.isProjectPresent(projectName),
                    String.format("Project %s should be in list !", projectName));
        }

        softAssert.assertEquals(projectsMenu.getProjectItems().get(0).getProjectName(), lastProjectName,
                "Active project should be first in list !");

        softAssert.assertAll();
    }

    @Test(groups = "add-edit-search")
    @Maintainer("Gmamaladze")
    public void verifyEditedProjectKey() {
        SoftAssert softAssert = new SoftAssert();

        projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        String projectName = RandomStringUtils.randomAlphabetic(6);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.createProject(projectName, projectKey);
        projectsPage = ProjectsPage.openPageDirectly(getDriver());

        String newProjectKey = ("but".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectName = RandomStringUtils.randomAlphabetic(3);
        projectsPage.editCertainProjectByKey(projectKey)
                    .typeProjectName(projectName)
                    .typeProjectKey(newProjectKey)
                    .submitModal();

        projectKey = newProjectKey;

        ProjectsMenu projectsMenu = projectsPage.getHeader().openProjectsWindow();

        softAssert.assertTrue(projectsMenu.isProjectPresent(projectName, projectKey),
                "Project should present on project menu !");

        projectsMenu.getProjectByKey(newProjectKey)
                    .ifPresent(projectItem -> projectItem.getRootExtendedElement().click());

        AutomationLaunchesPage automationLaunchesPage = new AutomationLaunchesPage(getDriver());

        final String URL = automationLaunchesPage.getCurrentUrl();

        softAssert.assertTrue(URL.contains(projectKey), "Page Url should contain project key !");
        softAssert.assertTrue(automationLaunchesPage.getBreadcrumbs()
                                                    .isBreadcrumbPresent(projectKey), "Breadcrumb is not as excepted !");

        softAssert.assertAll();
    }
}