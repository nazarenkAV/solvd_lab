package com.zebrunner.automation.gui.smoke;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.automation.gui.integration.SettingsPageR;
import com.zebrunner.automation.gui.project.ProjectsPage;

@Maintainer("obabich")
public class SettingsPageTest extends LogInBase {

    @TestCaseKey("ZTP-1505")
    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void elementsPresenceOnSettingsPageTest() {
        WebDriver webDriver = super.getDriver();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);

        SettingsPageR settingsPage = projectsPage.getHeader().toSettingsPage();
        settingsPage.assertPageOpened();
        Assert.assertEquals(settingsPage.getTitle(), SettingsPageR.PAGE_NAME);

        Assert.assertTrue(settingsPage.getCompanyLogo().isPresent(10), "Can't find company logo");
        Assert.assertTrue(settingsPage.getUsers().isStateMatches(Condition.VISIBLE), "Can't find users button");
        Assert.assertTrue(
                settingsPage.getGroupsAndPermissions().isStateMatches(Condition.VISIBLE),
                "Can't find groups and permissions button"
        );

        UsersPageR usersPage = settingsPage.toUsersPage();
        usersPage.assertPageOpened();
        Assert.assertEquals(webDriver.getWindowHandles().size(), 1, "Users page should be open in the same tab!");
        Assert.assertNotNull(usersPage.getUserCards(), "Users page should contain list of all existing users!");
    }

}
