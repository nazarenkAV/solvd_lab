package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.automation.gui.integration.ProjectIntegrationsPageR;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Maintainer("obabich")
public class IntegrationsPageTest extends LogInBase {
    private final String EXPECTED_PAGE_TITLE = "Integrations";
    private String projectKey;

    @BeforeClass
    public void getProjectKey() {
        projectKey = LogInBase.project.getKey();
    }

    @Test(groups = {"min_acceptance"}, enabled = false)
    public void elementsPresenceTest() {
        ProjectIntegrationsPageR projectIntegrationsPage =
                new ProjectIntegrationsPageR(getDriver()).openPageDirectly(projectKey);
        projectIntegrationsPage.assertPageOpened();

        int expectedNumberOfActiveIntegrations = 21;
        int expectedNumberOfDisabledIntegrations = 3;
        int expectedTotalIntegrations = expectedNumberOfActiveIntegrations + expectedNumberOfDisabledIntegrations;

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(projectIntegrationsPage.getTitle(), EXPECTED_PAGE_TITLE, "Integration page title is not as expected!");
        softAssert.assertEquals(projectIntegrationsPage.getDisableIntegrations().size(), expectedNumberOfDisabledIntegrations,
                "Number of disabled integrations is not as expected!");
        softAssert.assertEquals(projectIntegrationsPage.getActiveIntegrations().size(), expectedNumberOfActiveIntegrations,
                "Number of active integrations is not as expected!");
        softAssert.assertEquals(projectIntegrationsPage.getTotalIntegrations().size(), expectedTotalIntegrations,
                "Number of total integrations is not as expected!");
        softAssert.assertAll();
    }
}
