package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.gui.common.FilterSemiWindow;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.util.VideoDelimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FilterAccessibilityTest extends LogInBase {
    private final String env = "NEW";
    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private String userName;

    @BeforeClass
    public void getProjectAndAddJiraIntegration() {
        project = LogInBase.project;
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    @AfterMethod(onlyForGroups = "user-was-created", alwaysRun = true)
    public void deleteCreatedUser() {
        userService.getUserId(userName)
                .ifPresent(userId -> userService.deleteUserById(userId));
    }

    @Test(groups = "user-was-created")
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1806", "ZTP-1185"})
    public void publicPrivateFilterVisibilityTest() {
        long trId = apiHelperService.startTR(project.getKey());
        testRunIdList.add(trId);
        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        testRunIdList.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectEnv(env);

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String publicFilter = "publicFilter".concat(RandomStringUtils.randomAlphabetic(6));
        filterSemiWindow.saveFilter(publicFilter);

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(publicFilter),
                String.format("Filter with name '%s' should present in list", publicFilter));

        filterSemiWindow.getFilterCard(publicFilter).openSettings().clickMakePublic();//ZTP-1806 - Changing the availability (visibility) of the filter
        filterSemiWindow.closeSemiWindow();

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectEnv(env);

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String privateFilter = "privateFilter".concat(RandomStringUtils.randomAlphabetic(6));
        filterSemiWindow.saveFilter(privateFilter);

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(privateFilter),
                String.format("Filter with name '%s' should present in list", privateFilter));

        filterSemiWindow.closeSemiWindow();
        automationLaunchesPage.getHeader().logout();

        User user = userService.generateRandomUser();
        userService.create(user);
        userName = user.getUsername();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(user.getUsername(), user.getPassword());

        softAssert.assertTrue(automationLaunchesPage.getHeader().isUIObjectPresent(), "Tenant header should be present after login!");

        AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(publicFilter),
                String.format("Public filter with name ‘%s’ should be visible for all users", publicFilter)); //ZTP-1185 - User is able to make saved filter public/private
        softAssert.assertFalse(
                filterSemiWindow.isFilterPresentInList(privateFilter),
                String.format("Private filter with name ‘%s’ shouldn’t be visible for another users", privateFilter));

        filterSemiWindow.closeSemiWindow();

        softAssert.assertAll();
    }
}
