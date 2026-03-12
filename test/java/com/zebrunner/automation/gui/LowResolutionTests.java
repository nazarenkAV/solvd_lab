package com.zebrunner.automation.gui;

import lombok.extern.slf4j.Slf4j;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.TestGroups;
import com.zebrunner.automation.config.UserProperties;
import com.zebrunner.automation.gui.common.NavigationMenu;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.milestone.MilestonePage;
import com.zebrunner.automation.gui.smoke.LogInBase;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.gui.tcm.testrun.TestRunsGridPage;
import com.zebrunner.automation.util.LocalStorageManager;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class LowResolutionTests extends LogInBase {

    private final int SCREEN_WIDTH_IN_PX = 590;
    private final int SCREEN_HEIGHT_IN_PX = 590;

    @BeforeMethod
    public void setLowResolution() {
        WebDriver webDriver = super.getDriver();

        webDriver.manage()
                 .window()
                 .setSize(new Dimension(SCREEN_WIDTH_IN_PX, SCREEN_HEIGHT_IN_PX));
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void verifyUserCanLoginWithLowScreenResolution() {
        WebDriver webDriver = super.getDriver();

        LocalStorageManager localStorageManager = new LocalStorageManager(webDriver);
        localStorageManager.clear();

        this.assertResolution(webDriver);

        LoginPage loginPage = LoginPage.openPageDirectly(webDriver);
        loginPage.assertPageOpened();

        UserProperties.Admin admin = ConfigHelper.getUserProperties().getAdmin();
        loginPage.login(admin.getUsername(), admin.getPassword());

        AutomationLaunchesPage launchesPage = new AutomationLaunchesPage(webDriver);
        Assert.assertTrue(launchesPage.getHeader().isUIObjectPresent(), "Tenant header should be present after login!");
    }

    @Test(groups = TestGroups.MINIMAL_ACCEPTANCE)
    public void verifyUserCanNavigateInProjectWithLowScreenResolution() {
        WebDriver webDriver = super.getDriver();

        this.assertResolution(webDriver);

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, LogInBase.project.getKey());
        Assert.assertTrue(launchesPage.getHeader().isUIObjectPresent(), "Tenant header should be present after login!");

        NavigationMenu navigationMenu = launchesPage.openMobileNavigationMenu();
        navigationMenu.assertElementPresent();

        navigationMenu.click(NavigationMenu.NavigationMenuItem.TEST_REPOSITORY);

        TestCasesPage testCasesPage = new TestCasesPage(webDriver);
        testCasesPage.closeMobileNavigationMenu();
        Assert.assertTrue(testCasesPage.isPageOpened(), "Test cases page should be opened!");

        navigationMenu = launchesPage.openMobileNavigationMenu();
        navigationMenu.click(NavigationMenu.NavigationMenuItem.AUTOMATION);

        AutomationLaunchesPage automationLaunchesPage = new AutomationLaunchesPage(webDriver);
        testCasesPage.closeMobileNavigationMenu();
        Assert.assertTrue(automationLaunchesPage.isPageOpened(), "Automation launches page should be opened!");

        navigationMenu = launchesPage.openMobileNavigationMenu();
        navigationMenu.click(NavigationMenu.NavigationMenuItem.TESTING_ACTIVITIES);

        TestRunsGridPage testRunsGridPage = new TestRunsGridPage(webDriver);
        testRunsGridPage.closeMobileNavigationMenu();
        Assert.assertTrue(testRunsGridPage.getUiLoadedMarker().isVisible(), "Test runs page should be opened!");

        navigationMenu = launchesPage.openMobileNavigationMenu();
        MilestonePage milestonePage = navigationMenu.toMilestonePage();
        milestonePage.closeMobileNavigationMenu();
        Assert.assertTrue(milestonePage.isPageOpened(), "Milestones page should be opened!");
    }

    private void assertResolution(WebDriver driver) {
        Dimension windowSize = driver.manage().window().getSize();

        Assert.assertEquals(windowSize.getWidth(), SCREEN_WIDTH_IN_PX, "Window width is not as expected");
        Assert.assertEquals(windowSize.getHeight(), SCREEN_HEIGHT_IN_PX, "Window height is not as expected");
    }

}
