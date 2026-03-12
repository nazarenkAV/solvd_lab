package com.zebrunner.automation.gui;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.carina.core.IAbstractTest;

public class DemoTest implements IAbstractTest {

    public static final String TEST_NAME = "Zebrunner Web tests - login_ShouldOpenLoginPage";

    @Test
    public void login_ShouldOpenLoginPage() {
        WebDriver webDriver = this.getDriver();

        LoginPage loginPage = LoginPage.openPageDirectly(webDriver);
        loginPage.assertPageOpened();

        Artifact.attachReferenceToTest("https://zebrunner.com", "landing");
        Artifact.attachReferenceToTestRun("https://zebrunner.com", "landing");

        throw new AssertionError("This test must be failed");
    }

}
