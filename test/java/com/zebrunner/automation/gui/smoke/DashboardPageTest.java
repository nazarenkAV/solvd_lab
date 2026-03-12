package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.gui.reporting.dashboard.DashboardCardR;
import com.zebrunner.automation.gui.reporting.dashboard.MainDashboardsPageR;
import com.zebrunner.automation.gui.reporting.widget.WidgetsPage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;

@Maintainer("obabich")
public class DashboardPageTest extends LogInBase {
    private Project project;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    private String title;

    @Test(groups = "min_acceptance")
    @TestCaseKey({"ZTP-1919", "ZTP-920", "ZTP-962", "ZTP-943", "ZTP-936"})
    public void addDashboard() {
        WebDriver webDriver = super.getDriver();

        String widgetTemplateName = "PASS RATE (BAR)";
        String widgetName = "Widget " + RandomStringUtils.randomAlphabetic(6);
        title = "Dashboard Name " + RandomStringUtils.randomAlphabetic(6);

        AutomationLaunchesPage launchesPageTest = AutomationLaunchesPage.openPageDirectly(webDriver, project.getKey());
        MainDashboardsPageR mainDashboardsPage = launchesPageTest.getNavigationMenu().toMainDashboardPage();

        mainDashboardsPage.assertPageOpened();
        Assert.assertEquals(
                mainDashboardsPage.getNavigationMenu().getProjectKey(), project.getTrimmedProjectKey(),
                "Project key differs expected(Dashboards page)"
        );

        WidgetsPage dashboardPage = mainDashboardsPage.addDashboard(title);
        Assert.assertEquals(
                mainDashboardsPage.getPopUp(), "Dashboard was successfully created",
                "Message is not as expected!"
        );

        dashboardPage.isPageOpened();
        Assert.assertEquals(dashboardPage.getTitle(), title, "Title is not as expected!");

        dashboardPage.addNewWidgetFromTemplates(widgetName, widgetTemplateName);
        Assert.assertTrue(dashboardPage.isSendByEmailButtonPresent(), "Send by email should be present on page!");

        mainDashboardsPage = MainDashboardsPageR.openPageDirectly(webDriver, project.getKey());
        Assert.assertTrue(mainDashboardsPage.isDashboardPresentOnMainPage(title), "Can't find newly created dashboard");

        DashboardCardR dashboardCard = mainDashboardsPage.getDashboardByName(title);
        Assert.assertEquals(
                dashboardCard.getCreatedDate(),
                OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                "Create date is not as expected!"
        );
        Assert.assertTrue(dashboardCard.isEditButtonPresent(), "Button edit is not present on this dashboard!");
        Assert.assertTrue(dashboardCard.isDeleteButtonPresent(), "Button delete is not present on this dashboard!");
    }

    @Test(groups = "min_acceptance")
    public void editDashboard() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.DASHBOARDS);
        Label.attachToTest(TestLabelsConstant.DASHBOARDS, TestLabelsConstant.EDITING);

        SoftAssert softAssert = new SoftAssert();
        title = "Dashboard Name ".concat(RandomStringUtils.randomAlphabetic(6));
        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        WidgetsPage dashboardPage = mainDashboardsPage.addDashboard(title);
        title = "New ".concat(title);

        dashboardPage.editDashboard(title);
        softAssert.assertEquals(mainDashboardsPage.getPopUp(),
                MessageEnum.DASHBOARD_UPDATED.getDescription(), "Message is not as expected!");

        softAssert.assertEquals(dashboardPage.getTitle(), title, "Title is not as expected!");

        mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        softAssert.assertTrue(mainDashboardsPage.isPageOpened());
        softAssert.assertTrue(mainDashboardsPage.isDashboardPresentOnMainPage(title),
                "Can't find newly created dashboard");
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    @TestCaseKey("ZTP-937")
    public void searchDashboard() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.DASHBOARDS);

        title = "Dashboard Name ".concat(RandomStringUtils.randomAlphabetic(6));
        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        WidgetsPage dashboardPage = mainDashboardsPage.addDashboard(title);
        dashboardPage.assertPageOpened();

        mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());

        Assert.assertEquals(mainDashboardsPage.searchDashboard(title).get(0).getDashboardName(), title,
                "Can't find necessary dashboard!");
    }

    @Test(groups = "min_acceptance")
    public void deleteDashboard() {
        WebDriver webDriver = super.getDriver();
        title = "Dashboard Name " + RandomStringUtils.randomAlphabetic(6);

        MainDashboardsPageR dashboardsPage = MainDashboardsPageR.openPageDirectly(webDriver, project.getKey());
        WidgetsPage dashboardPage = dashboardsPage.addDashboard(title);
        dashboardPage.assertPageOpened();

        dashboardsPage = MainDashboardsPageR.openPageDirectly(webDriver, project.getKey());
        dashboardsPage.assertPageOpened();
        dashboardsPage.deleteDashboard(title);

        Assert.assertEquals(dashboardsPage.getPopUp(), "Dashboard has been deleted", "Message is not as expected!");
        Assert.assertFalse(dashboardsPage.isDashboardPresentOnMainPage(title), "Dashboard with name " + title + " was not deleted!");
    }

    @Test(groups = "min_acceptance")
    public void checkGeneralDashboard() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.DASHBOARDS);

        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        DashboardCardR dashboardCard = mainDashboardsPage.getDashboardByName("General");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(dashboardCard.isCreatedDatePresent(), "Create date is present!");
        softAssert.assertFalse(dashboardCard.isEditButtonPresent(), "Button edit is not present on this dashboard!");
        softAssert.assertFalse(dashboardCard.isDeleteButtonPresent(), "Button delete is not present on this dashboard!");
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    public void checkMainDashboardPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.DASHBOARDS);

        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(mainDashboardsPage.getTitle(), mainDashboardsPage.PAGE_NAME, "Title is not as expected!");
        softAssert.assertTrue(mainDashboardsPage.isSearchPresentAndClickable(), "Search is not visible or clickable");
        softAssert.assertTrue(mainDashboardsPage.isAddDashboardButtonPresentAndClickable(), "AddDashboard button is not visible or clickable");
        softAssert.assertEquals(mainDashboardsPage.getColumnTitleForDashboardName().toLowerCase(),
                "Dashboard name".toLowerCase(),
                "Colon name is not as expected!");
        softAssert.assertEquals(mainDashboardsPage.getColumnTitleForCreationDate()
                                                  .toLowerCase(), "Creation date".toLowerCase(),
                "Colon creation date is not as expected!");
        softAssert.assertAll();
    }
}
