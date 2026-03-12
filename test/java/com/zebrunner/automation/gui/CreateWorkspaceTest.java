package com.zebrunner.automation.gui;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

import com.zebrunner.agent.core.registrar.CurrentTest;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.landing.RegisterPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.EmailManager;
import com.zebrunner.carina.core.AbstractTest;
import com.zebrunner.carina.utils.R;

public class CreateWorkspaceTest extends AbstractTest {

    private final EmailManager emailManager = EmailManager.primaryInstance;

    private final String tenantEmail = this.getTenantEmail();
    private final String tenantName = "zbrautomation" + RandomStringUtils.randomNumeric(5);

    private final User tenantAdmin =
            new User().setUsername(ConfigHelper.getUserProperties().getAdmin().getUsername())
                      .setPassword(ConfigHelper.getUserProperties().getAdmin().getPassword());

    private String getTenantEmail() {
        String emailAccountUsername = ConfigHelper.getEmailAccountProperties().getUsername();

        String[] emailParts = emailAccountUsername.split("@");

        return emailParts[0] + "+" + RandomStringUtils.randomNumeric(5) + "@" + emailParts[1];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void createWorkspace_ShouldCreateWorkspace_AfterTenantRegistration() {
        if (!this.isProductionEnv()) {
            CurrentTest.revertRegistration();
            return;
        }

        R.CONFIG.put("explicit_timeout", String.valueOf(240), true);

        WebDriver webDriver = super.getDriver();

        String tenantOwnerName = "QA automation";
        RegisterPage registerPage = new RegisterPage(webDriver);

        registerPage.open();
        registerPage.closeCookie();
        registerPage.createWorkspace(tenantOwnerName, tenantEmail, tenantName);
        Assert.assertEquals(registerPage.getTitle(), registerPage.getExpectedPageTitle(), "Page title is not as expected!");

        String invitationLink = emailManager.pollWorkspaceReadyInvitationLink();
        registerPage.openURL(invitationLink);

        LoginPage loginPage = new LoginPage(webDriver);
        Assert.assertTrue(loginPage.isPageOpened(), "Could not open login page after workspace creation");

        loginPage.loginFirstTime(tenantAdmin.getUsername(), tenantAdmin.getPassword());
        Assert.assertTrue(webDriver.getCurrentUrl().contains("/projects"), "Page url is not as expected!");
    }

    private boolean isProductionEnv() {
        return APIContextManager.TENANT_URL.contains("zebrunner.com");
    }

    @Test(dependsOnMethods = "createWorkspace_ShouldCreateWorkspace_AfterTenantRegistration")
    public void login_ShouldLogin_AfterUserCreation() {
        WebDriver webDriver = super.getDriver(UUID.randomUUID().toString());
        String tenantUrl = "https://" + tenantName + ".zebrunner.com";

        LoginPage loginPage = LoginPage.openPageByUrl(webDriver, tenantUrl + "/signin");
        Assert.assertTrue(webDriver.getCurrentUrl().contains(tenantName), "Login page url doesn't contain tenant name");

        ProjectsPage projectsPage = loginPage.login(tenantAdmin.getUsername(), tenantAdmin.getPassword());
        Assert.assertTrue(projectsPage.getHeader().isVisible(), "Projects page header should be visible after login");
    }

}
