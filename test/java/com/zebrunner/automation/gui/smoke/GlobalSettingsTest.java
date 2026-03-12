package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.gui.common.AboutZebrunnerModal;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.util.PageUtil;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlobalSettingsTest extends LogInBase {

    @Test
    @TestCaseKey("ZTP-701")
    @Maintainer("Gmamaladze")
    public void verifyUserCanLogOutViaAccountSettingsDropdownInHeader() {
        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());

        LoginPage loginPage = projectsPage.getHeader().logout();
        Assert.assertTrue(loginPage.isPageOpened(), "Login in page should be opened !");
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-4224", "ZTP-4227", "ZTP-4225"})
    public void aboutZebrunnerSectionTest() {
        Project project = LogInBase.project;
        WebDriver webDriver = super.getDriver();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(webDriver);

        for (int i = 0; i < 3; i++) {
            projectsPage.getHeader().openMenu();
            AboutZebrunnerModal aboutZebrunnerModal = projectsPage.getHeader().clickAboutZebrunnerButton();

            Assert.assertTrue(aboutZebrunnerModal.isModalOpened(), "Modal should be opened !");
            Assert.assertEquals(
                    aboutZebrunnerModal.getModalTitleText(), AboutZebrunnerModal.MODAL_TITLE,
                    "Modal title is not as excepted !"
            );

            if (i == 0) {
                aboutZebrunnerModal.clickOkButton();
            } else if (i == 1) {
                aboutZebrunnerModal.clickClose();
            } else {
                PageUtil.guaranteedToHideDropDownList(webDriver);
            }

            Assert.assertFalse(aboutZebrunnerModal.isModalOpened(), "Modal should be closed !");
            Assert.assertTrue(projectsPage.isPageOpened(), "Project page should be opened !");
        }

        PageUtil.guaranteedToHideDropDownList(webDriver);

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(webDriver, project.getKey());

        AboutZebrunnerModal aboutZebrunnerModal = testCasesPage.getHeader().openAboutZebrunnerModal();

        Assert.assertTrue(aboutZebrunnerModal.isModalOpened(), "Modal should be opened from testcase page as well !");
        Assert.assertEquals(
                aboutZebrunnerModal.getModalTitleText(), AboutZebrunnerModal.MODAL_TITLE,
                "Modal title is not as excepted after opening it from testcase page !"
        );
    }

}
