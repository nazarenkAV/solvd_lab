package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.TooltipEnum;
import com.zebrunner.automation.gui.project.ProjectsMenu;
import com.zebrunner.automation.gui.project.ProjectCard;
import com.zebrunner.automation.gui.project.ProjectItem;
import com.zebrunner.automation.gui.project.ResizedProjectCard;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Maintainer("Gmamaladze")
@Slf4j
public class StarredProjectsTest extends LogInBase {

    private final List<String> projectKeys = new ArrayList<>();

    @AfterMethod(alwaysRun = true)
    public void deleteProjects() {
        projectKeys.forEach(projectV1Service::deleteProjectByKey);
        projectKeys.clear();
    }

    @AfterMethod()
    public void testsSeparator(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 5, "GMT+3:00");
    }

    @AfterMethod(alwaysRun = true, groups = "resize")
    public void returnWindowToDefaultSize() {
        getDriver().manage().window().maximize();
    }


    // -------------------------------------- Test ------------------------------------ //

    @Test()
    @TestCaseKey({"ZTP-5720", "ZTP-5721", "ZTP-5722", "ZTP-5730", "ZTP-5725"})
    @Maintainer("Gmamaladze")
    public void verifyStarredProjectsAtTheProjectGrid() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        String projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        projectKeys.add(projectKey);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        softAssert.assertTrue(projectsPage.getProjectCards().stream().allMatch(ProjectCard::isProjectStarPresent),
                "Project stars should present !"); //ZTP-5720
        softAssert.assertTrue(projectsPage.getProjectCards().stream().allMatch(ProjectCard::isProjectStarClickable),
                "Project stars is not clickable !");

        ProjectCard projectCard = projectsPage.getProjectCardByProjectKey(projectKey);
        projectCard.clickProjectStar();
        projectCard.hover(); // Used for to move cursor from star to get properly color from project

        softAssert.assertEquals(projectCard.getColorFromStar(), ColorEnum.STARRED.getHexColor(),
                "Color of starred is not as excepted !"); //ZTP-5721

        projectCard.clickProjectStar();
        projectCard.hover();

        softAssert.assertEquals(projectCard.getColorFromStar(), ColorEnum.NO_STARRED.getHexColor(),
                "Color of no starred is not as excepted !"); //ZTP-5722

        projectCard.clickProjectStar();
        ProjectsPage.openPageDirectly(getDriver());

        List<String> defaultUiNames = projectsPage.getProjectCards().stream().map(ProjectCard::getProjectName).collect(Collectors.toList());

        //ZTP-5725, ZTP-5730
        softAssert.assertEquals(defaultUiNames, projectsPage.getDefaultSortedAllProjectsName(), "Sort is not as excepted !");

        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey({"ZTP-5727", "ZTP-5728", "ZTP-5729", "ZTP-5731"})
    @Maintainer("Gmamaladze")
    public void verifyStarredProjectsAtTheProjectDropDown() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        String projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        projectKeys.add(projectKey);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        ProjectsMenu projectsMenu = automationLaunchesPage
                .getHeader()
                .openProjectsWindow();
        ProjectItem projectItem = projectsMenu.getProjectByKey(projectKey)
                .orElseThrow(() -> new NoSuchElementException("Unable to find with key " + projectKey));

        softAssert.assertTrue(projectsMenu.getProjectItems().stream().allMatch(ProjectItem::isProjectStarClickable),
                "Project stars is not clickable !");

        projectItem.clickProjectStar();
        projectItem.hover(); // Used for to move cursor from star to get properly color from project

        softAssert.assertEquals(projectItem.getColorFromStar(), ColorEnum.STARRED.getHexColor(),
                "Color of starred is not as excepted !"); //ZTP-5727

        projectItem.clickProjectStar();
        projectItem.hover();

        softAssert.assertEquals(projectItem.getColorFromStar(), ColorEnum.NO_STARRED.getHexColor(),
                "Color of non starred is not as excepted !"); //ZTP-5728

        projectItem.clickProjectStar();

        ProjectsPage projectsPage = projectsMenu.toProjectsPage();

        softAssert.assertFalse(projectsMenu.isProjectMenuOpened(), "Project menu should be closed !");
        softAssert.assertTrue(projectsPage.getStarredProjectCards().get(0).isProjectStarred(),
                "Project should be starred on project grid !"); //ZTP-5729

        projectsMenu = projectsPage.getHeader().openProjectsWindow();

        //ZTP-5731
        softAssert.assertTrue(projectsMenu.isStarredLabelPresent(), "Starred label should be present !");
        softAssert.assertTrue(projectsMenu.isRecentLabelPresent(), "Recent label should be present !");

        projectItem = projectsMenu.getProjectByKey(projectKey)
                .orElseThrow(() -> new NoSuchElementException("Unable to find with key " + projectKey));

        projectItem.clickProjectStar();
        projectsPage = projectsMenu.toProjectsPage();

        softAssert.assertFalse(projectsMenu.isProjectMenuOpened(), "Project menu should be closed !");
        softAssert.assertFalse(projectsPage.getProjectCardByProjectKey(projectKey).isProjectStarred(),
                "Project should not be starred !"); //ZTP-5729

        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey("ZTP-5723")
    @Maintainer("Gmamaladze")
    public void verificationStarredTooltips() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        String projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        projectKeys.add(projectKey);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProjectCard projectCard = projectsPage.getProjectCardByProjectKey(projectKey);

        softAssert.assertEquals(projectCard.getStarToolTip(), TooltipEnum.TOOLTIP_ADD_TO_STARRED.getToolTipMessage());

        projectCard.clickProjectStar();

        softAssert.assertEquals(projectCard.getStarToolTip(), TooltipEnum.TOOLTIP_REMOVE_FROM_STARRED.getToolTipMessage());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), projectKey);
        ProjectsMenu projectsMenu = automationLaunchesPage
                .getHeader()
                .openProjectsWindow();
        ProjectItem projectItem = projectsMenu.getProjectByKey(projectKey)
                .orElseThrow(() -> new NoSuchElementException("Unable to find with key " + projectKey));

        softAssert.assertEquals(projectItem.getStarToolTip(), TooltipEnum.TOOLTIP_REMOVE_FROM_STARRED.getToolTipMessage());

        projectItem.clickProjectStar();

        softAssert.assertEquals(projectItem.getStarToolTip(), TooltipEnum.TOOLTIP_ADD_TO_STARRED.getToolTipMessage());

        softAssert.assertAll();
    }

    @Test(priority = 7, groups = "resize")
    @TestCaseKey("ZTP-5732")
    @Maintainer("Gmamaladze")
    public void verifyStarredNonStarredProjectsForScreensLest768Px() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        String projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        projectKeys.add(projectKey);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        projectsPage.getProjectCardByProjectKey(projectKey).clickProjectStar();
        ProjectsPage.openPageDirectly(getDriver());

        getDriver().manage().window().setSize(new Dimension(700, 700));

        List<ResizedProjectCard> resizedProjectCards = projectsPage.getResizedProjectCards();

        softAssert.assertTrue(resizedProjectCards.stream().allMatch(ResizedProjectCard::isProjectStarPresent),
                "Project stars should present !");
        softAssert.assertTrue(resizedProjectCards.stream().allMatch(ResizedProjectCard::isProjectStarredLabelPresent),
                "Project starred label should present !");
        softAssert.assertTrue(resizedProjectCards.stream().anyMatch(resizedProjectCard ->
                        resizedProjectCard.getColorFromStar().equalsIgnoreCase(ColorEnum.STARRED.getHexColor())),
                "Starred project color is not as excepted !");
        softAssert.assertTrue(resizedProjectCards.stream().anyMatch(resizedProjectCard ->
                        resizedProjectCard.getColorFromStar().equalsIgnoreCase(ColorEnum.NO_STARRED.getHexColor())),
                "No starred project color is not as excepted !");

        softAssert.assertAll();
    }

    @Test(priority = 5)
    @TestCaseKey("ZTP-5733")
    @Maintainer("Gmamaladze")
    public void verifyDifferentUsersHaveDifferentStarredProjects() {
        SoftAssert softAssert = new SoftAssert();

        String projectName = "Automation ".concat(RandomStringUtils.randomAlphabetic(5));
        String projectKey = ("aut".concat(RandomStringUtils.randomAlphabetic(3))).toUpperCase();
        projectV1Service.createProject(projectName, projectKey);
        projectKeys.add(projectKey);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        ProjectCard projectCard = projectsPage.getProjectCardByProjectKey(projectKey);

        projectCard.clickProjectStar();

        projectCard.hover(); // Used for to move cursor from star to get properly color from project
        softAssert.assertTrue(projectCard.isProjectStarred(), "Project should be starred !");

        WebDriver userDriver = getDriver("user");
        LoginPage loginPage = new LoginPage(userDriver);
        loginPage.openURL(ConfigHelper.getTenantUrl() + "/signin");
        loginPage.isPageOpened();

        loginPage.login(LogInBase.notProjectMember);

        projectsPage = new ProjectsPage(userDriver);
        projectCard = projectsPage.getProjectCardByProjectKey(projectKey);

        softAssert.assertFalse(projectCard.isProjectStarred(), "Different user Project card should not be starred !");

        softAssert.assertAll();
    }
}
