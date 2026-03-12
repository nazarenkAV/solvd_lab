package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.iam.AccountSettingsPage;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.util.ComponentUtil;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

public class UserHomepageTest extends LogInBase {
    private List<String> userNames = new ArrayList<>();
    private Project project;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterClass(alwaysRun = true)
    public void deleteUsers() {
        userNames.forEach(userName -> {
            userService.getUserId(userName).ifPresent(userId -> userService.deleteUserById(userId));
        });
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-2855", "ZTP-2856", "ZTP-2857", "ZTP-2860", "ZTP-2859"})
    public void verifyHomepage() {
        User user = userService.generateRandomUser();
        User randomUser = userService.create(user);
        randomUser.setPassword(user.getPassword());
        userNames.add(randomUser.getUsername());

        SoftAssert softAssert = new SoftAssert();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.getHeader().logout();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(randomUser.getUsername(), randomUser.getPassword());

        pause(4);
        AccountSettingsPage accountSettingsPage = AccountSettingsPage.openPageDirectly(getDriver());

        softAssert.assertEquals(accountSettingsPage.getCurrentHomepageText(), AccountSettingsPage.HomepageDropdownEnum.PROJECTS_DIRECTORY.get(),
                "Homepage for new user is not as expected!");//ZTP-2855

        accountSettingsPage.clickHomepageDropdown();

        softAssert.assertEquals(accountSettingsPage.getHomepageItemCount(), 3, "There should be 3 homepages!");//ZTP-2858

        ComponentUtil.pressEscape(getDriver());

        accountSettingsPage.chooseHomepage(AccountSettingsPage.HomepageDropdownEnum.AUTOMATION_LAUNCHES.get());
        accountSettingsPage.clickSave();

        softAssert.assertEquals(accountSettingsPage.getCurrentHomepageText(), AccountSettingsPage.HomepageDropdownEnum.AUTOMATION_LAUNCHES.get(),
                "Updated homepage is not as expected!");//ZTP-2856
        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.HOMEPAGE_WAS_SUCCESSFULLY_UPDATED.getDescription()),
                "Popup text not equal to specified.");

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.toCertainProject(project.getKey());

        accountSettingsPage.getHeader().logout();
        loginPage.login(randomUser.getUsername(), randomUser.getPassword());

        AutomationLaunchesPage automationLaunchesPage = new AutomationLaunchesPage(getDriver());
        softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Automation Launches page is not opened after log in!");//ZTP-2857
        softAssert.assertTrue(automationLaunchesPage.getBreadcrumbs().isBreadcrumbPresent(project.getKey()), "Project is not as excepted!");//ZTP-2860

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-2861")
    public void verifyHomepageSelectionForTwoUsers() {
        User user = userService.generateRandomUser();
        User randomUser = userService.create(user);
        randomUser.setPassword(user.getPassword());
        userNames.add(randomUser.getUsername());

        User user2 = userService.generateRandomUser();
        User randomUser2 = userService.create(user2);
        randomUser2.setPassword(user2.getPassword());
        userNames.add(randomUser2.getUsername());

        SoftAssert softAssert = new SoftAssert();
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.getHeader().logout();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(randomUser.getUsername(), randomUser.getPassword());

        pause(4);
        AccountSettingsPage accountSettingsPage = AccountSettingsPage.openPageDirectly(getDriver());

        accountSettingsPage.chooseHomepage(AccountSettingsPage.HomepageDropdownEnum.AUTOMATION_LAUNCHES.get());
        accountSettingsPage.clickSave();

        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.HOMEPAGE_WAS_SUCCESSFULLY_UPDATED.getDescription()),
                "Popup text not equal to specified for first user.");
        softAssert.assertEquals(accountSettingsPage.getCurrentHomepageText(), AccountSettingsPage.HomepageDropdownEnum.AUTOMATION_LAUNCHES.get(),
                "Updated homepage is not as expected for first user!");

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.toCertainProject(project.getKey());

        accountSettingsPage.getHeader().logout();
        loginPage.login(randomUser2.getUsername(), randomUser2.getPassword());

        pause(4);
        accountSettingsPage = AccountSettingsPage.openPageDirectly(getDriver());

        accountSettingsPage.chooseHomepage(AccountSettingsPage.HomepageDropdownEnum.TEST_CASES.get());
        accountSettingsPage.clickSave();

        softAssert.assertTrue(projectsPage.waitIsPopUpMessageAppear(MessageEnum.HOMEPAGE_WAS_SUCCESSFULLY_UPDATED.getDescription()),
                "Popup text not equal to specified.");
        softAssert.assertEquals(accountSettingsPage.getCurrentHomepageText(), AccountSettingsPage.HomepageDropdownEnum.TEST_CASES.get(),
                "Updated homepage is not as expected!");

        projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.toCertainProject(project.getKey());

        accountSettingsPage.getHeader().logout();
        loginPage.login(randomUser.getUsername(), randomUser.getPassword());

        pause(4);
        AutomationLaunchesPage automationLaunchesPage = new AutomationLaunchesPage(getDriver());
        softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Automation Launches page is not opened!");
        softAssert.assertTrue(automationLaunchesPage.getBreadcrumbs().isBreadcrumbPresent(project.getKey()), "User 1 project is not as excepted!");

        accountSettingsPage.getHeader().logout();
        loginPage.login(randomUser2.getUsername(), randomUser2.getPassword());

        pause(4);
        TestCasesPage testCasesPage = new TestCasesPage(getDriver());
        softAssert.assertTrue(testCasesPage.isPageOpened(), "Test Cases page is not opened!");
        softAssert.assertTrue(testCasesPage.getBreadcrumbs().isBreadcrumbPresent(project.getKey()), "User 2 project is not as excepted!");

        softAssert.assertAll();
    }
}
