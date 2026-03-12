package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.automation.gui.landing.DocumentationPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.util.PageUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Maintainer("obabich")
public class DocumentationPageTest extends LogInBase {

    @Test(groups = {"min_acceptance"})
    public void toDocumentationFromTenantTest() {
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        DocumentationPage documentationPage = projectsPage.getHeader().toDocumentationPage();
        documentationPage.assertPageOpened();

        Assert.assertTrue(documentationPage.isHeaderPresent(), "Header on documentation page should present");
        Assert.assertTrue(documentationPage.isSidebarPresent(), "Sidebar on documentation page should present");

        String expectedActiveSection = "Introduction";
        String expectedOverviewTitle = "Meet Zebrunner";

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(documentationPage.getActiveSessionText(), expectedActiveSection, "Active session text is not as expected");
        softAssert.assertEquals(documentationPage.getOverviewTitle(), expectedOverviewTitle, "Overview title is not as expected");

        PageUtil.toOtherTab(getDriver());
        softAssert.assertAll();
    }
}
