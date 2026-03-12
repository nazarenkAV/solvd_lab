package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.gui.iam.AddTokenModal;
import com.zebrunner.automation.gui.iam.ApiAccessTab;
import com.zebrunner.automation.gui.iam.AccountSettingsPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Maintainer("akhivyk")
@TestLabel(name = "group", value = "account_and_profile")
public class AccountSettingTest extends LogInBase {

    private static final String API_ACCESS_TAB_BUTTON_NAME = "API Access";
    private static final String ADD_TOKEN_MODAL_TEXT = "Create token";

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-3686", "ZTP-3689"})
    public void verifyApiAccessTab() {
        Label.attachToTest(TestLabelsConstant.BUG, "ZEB-5756");
        Artifact.attachReferenceToTest("ZEB-5756", "https://solvd.atlassian.net/browse/ZEB-5756");

        AccountSettingsPage accountSettingsPage = AccountSettingsPage.openPageDirectly(getDriver());
        SoftAssert softAssert = new SoftAssert();
        String tokenName = "Token ".concat(RandomStringUtils.randomAlphabetic(5));

        softAssert.assertTrue(accountSettingsPage.isApiAccessTabButtonPresent(),
                "Api access form button isn't present");
        softAssert.assertEquals(accountSettingsPage.getNameOfApiAccessTabButton(), API_ACCESS_TAB_BUTTON_NAME,
                "Name of api access tab button isn't equals to expected");

        softAssert.assertTrue(accountSettingsPage.isServiceUrlPresent(),
                "Service url isn't present on account page");
        softAssert.assertTrue(accountSettingsPage.isServiceUrlDisabled(), "Service url isn't disabled");
        softAssert.assertEquals(accountSettingsPage.getServiceUrl(), ConfigHelper.getTenantUrl(),
                "Service url isn't equals to expected"); // ZTP-3689 Verify that Service URL is present

        ApiAccessTab apiAccessTab = accountSettingsPage.openApiAccessTab();
        softAssert.assertTrue(apiAccessTab.isAddTokenButtonPresent(),
                "Add token button isn't present after switching to api access tab"); // ZTP-3686 "API Access" tab is present and title is correct

        AddTokenModal addTokenModal = apiAccessTab.openAddTokenModal();
        softAssert.assertEquals(addTokenModal.getModalTitleText(), ADD_TOKEN_MODAL_TEXT,
                "Modal title text of add token modal isn't equals to expected");

        addTokenModal.inputTokenName(tokenName);
        addTokenModal.clickCheckboxAddExpirationDate();
        addTokenModal.clickCalendarLogo();

        softAssert.assertTrue(addTokenModal.getCalendar().isCalendarOpened(),
                "Calendar modal isn't opened");
        softAssert.assertTrue(addTokenModal.getCalendar().isTodayButtonPresent(),
                "Today button isn't present on calendar modal");

        softAssert.assertAll();
    }
}
