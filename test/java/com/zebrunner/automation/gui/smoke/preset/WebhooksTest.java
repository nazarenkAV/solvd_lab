package com.zebrunner.automation.gui.smoke.preset;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.launcher.domain.Config;
import com.zebrunner.automation.api.launcher.domain.GetWebhookResponse;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.launcher.domain.Preset;
import com.zebrunner.automation.api.launcher.domain.TestExecutionResults;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.launcher.TitleSection;
import com.zebrunner.automation.gui.launcher.preset.WebhookCard;
import com.zebrunner.automation.gui.launcher.preset.WebhooksModal;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.legacy.TooltipEnum;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.automation.util.WebhooksUtil;
import com.zebrunner.automation.gui.smoke.LogInBase;

@Slf4j
@Maintainer("akhivyk")
public class WebhooksTest extends LogInBase {

    private final String LAUNCHER_NAME = "Webhook tests";
    private final String PRESET_NAME = "Preset ".concat(RandomStringUtils.randomNumeric(3));
    private final String WARNING_MESSAGE_BEFORE_CHANGING_SECRET_KEY = "If you've lost or forgotten this secret, you can change it, but be aware that any integrations using this secret will need to be updated.";
    private final String NAME_ERROR_MESSAGE = "Must be between 1 and 255 characters";
    private final File CONFIG_FILE = new File("src/test/resources/api/launcher_service/config.json");
    private String webhookName;
    private String webhookSecretKey;
    private AddOrEditLauncherPage addOrEditLauncherPage;
    private Project project;
    private Long projectId;
    private Launcher createdLauncher;
    private Preset createdPreset;
    private Long createdRepoId;
    private final Duration ALLOWED_DIFFERENCE_TRIGGERING = Duration.ofSeconds(30);

    @BeforeTest
    public void createLauncherAndPreset() {
        project = LogInBase.project;
        projectId = project.getId();

        createdRepoId = LogInBase.repositoryId;
        createdLauncher = launcherService.addDefaultApiTestsLauncher(projectId, createdRepoId, LAUNCHER_NAME, "helloWorld");

        Config config = JsonPath.from(CONFIG_FILE).getObject("", Config.class);
        config.setLaunchCommand("mvn clean test -Dsuite=helloWorld");
        Preset preset = new Preset(PRESET_NAME, config);

        createdPreset = launcherService.addPreset(projectId, createdRepoId, createdLauncher.getId(), preset);
    }

    @BeforeMethod
    public void getPreset() {
        LauncherPage launcherPage = LauncherPage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        addOrEditLauncherPage = AddOrEditLauncherPage.openPage(getDriver());

        launcherPage.getPresetWithName(PUBLIC_REPO_NAME, LAUNCHER_NAME, PRESET_NAME)
                .ifPresentOrElse(presetItem -> presetItem.getRootExtendedElement().click(),
                        () -> {
                            softAssert.fail("Unable to find preset with name " + PRESET_NAME);
                            softAssert.assertAll();
                        });
    }

    @AfterTest
    public void deleteCreatedLauncher() {
        launcherService.deleteLauncher(projectId, createdRepoId, createdLauncher.getId());
    }

    @Test(groups =  "min_acceptance")
    @TestCaseKey({"ZTP-3283", "ZTP-3284", "ZTP-3285", "ZTP-3286", "ZTP-4795", "ZTP-4796", "ZTP-4797", "ZTP-4798", "ZTP-4799"})
    public void _webhookModalMainElementsPresence() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();

        softAssert.assertEquals(titleSection.hoverAndGetTooltipValue(), "Webhooks",
                "Tooltip is not as expected!"); // ZTP-3283 Verify Webhook tooltip is present when hovering over it

        softAssert.assertEquals(titleSection.getWebhookBtnBackgroundColor(), ColorEnum.HOVER_ON_WEBHOOK_BUTTON.getHexColor(),
                "Background color is not as expected!"); // ZTP-3284 Verify webhook button is highlighted on hover

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        softAssert.assertEquals(webhooksModal.getModalTitleText(), WebhooksModal.MODAL_NAME,
                "Modal title is not as expected!"); // ZTP-3285 Verify webhooks modal is opened after click on the 'Webhook' button

        softAssert.assertEquals(webhooksModal.getPlaceholderText(), WebhooksModal.EXPECTED_PLACEHOLDER_FOR_EMPTY_MODAL,
                "Placeholder name isn't equals to expected!");
        softAssert.assertTrue(webhooksModal.isPlaceholderIconPresent(),
                "Placeholder icon isn't present"); // ZTP-4795 Verify placeholders present on empty webhooks modal

        webhooksModal.hoverCreateButton();
        softAssert.assertEquals(webhooksModal.getBackgroundCreateButtonColor(), ColorEnum.HOVER_ON_CREATE_BUTTON.getHexColor(),
                "Background color is not as expected!"); // ZTP-4796 Verify webhook create button highlighted on hover

        webhooksModal = webhooksModal.clickCreateButton();
        softAssert.assertEquals(webhooksModal.getModalTitleText(), WebhooksModal.MODAL_NAME_ON_CREATION,
                "Title on creation webhook modal isn't equals to expected"); // ZTP-4797 Verify webhook creation modal opened

        softAssert.assertTrue(webhooksModal.isInputWebhookNamePresent(),
                "Webhook input name isn't present");
        softAssert.assertTrue(webhooksModal.isSecretKeyInputPresent(),
                "Secret key input isn't present"); // ZTP-4798 Verify webhook creation inputs present on modal

        webhooksModal = webhooksModal.clickCancelCreationWebhookButton();
        softAssert.assertEquals(webhooksModal.getModalTitleText(), WebhooksModal.MODAL_NAME,
                "Modal title is not as expected after clicking cancel button on creation!"); // ZTP-4799 Verify user can back to webhook grid from creating modal by clicking Cancel button

        webhooksModal.clickClose();
        softAssert.assertFalse(webhooksModal.getModalTitle().isStateMatches(Condition.VISIBLE),
                "'Webhooks' modal should be closed after click on 'Close' button!"); // ZTP-3286 Verify webhooks modal is closed after click X button

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4800", "ZTP-4801", "ZTP-4802"})
    public void webhookCreationAndCardVerification() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        webhooksModal.clickCreateButton();

        softAssert.assertFalse(webhooksModal.isSaveButtonActive(),
                "Save button active with empty name"); // ZTP-4800 Verify webhook with empty name can't be created

        webhookName = "Webhook ".concat(RandomStringUtils.randomAlphabetic(7));
        webhooksModal.typeWebhookName(webhookName);

        webhooksModal = webhooksModal.clickSaveButton();
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.WEBHOOK_CREATED.getDescription(),
                "Popup message about creation webhook isn't expected");

        softAssert.assertTrue(webhooksModal.isWebhookPresent(webhookName),
                "Created webhook with name " + webhookName + " isn't in the list"); // ZTP-4801 Verify user can create webhook

        WebhookCard webhookCard = webhooksModal.getCertainWebhookCard(webhookName);
        User user = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        String expectedAuthor = StringUtil.getExpectedAuthor(user);

        // ZTP-4802 Verify main elements present on webhook card
        softAssert.assertEquals(webhookCard.getWebhookNameText(), webhookName,
                "Name of created webhook isn't equals to expected");
        softAssert.assertTrue(isWebhookLinkValid(webhookCard.getWebhookUrlText()), "Webhook link isn't have expected format");
        softAssert.assertEquals(webhookCard.getWebhookAuthorName(), expectedAuthor,
                "Author isn't equals to expected");
        softAssert.assertEquals(webhookCard.getWebhookLastTimeTriggerText(), WebhookCard.NEVER_TRIGGERED,
                "Last trigger time isn't equals to expected");
        softAssert.assertTrue(webhookCard.isEditButtonPresent(), "Edit webhook button isn't present");
        softAssert.assertTrue(webhookCard.isDeleteButtonPresent(), "Delete webhook button isn't present");
        softAssert.assertEquals(webhookCard.copyNameButtonHoverAndGetTooltip(), TooltipEnum.TOOLTIP_COPY_NAME_BUTTON_NOT_CLICKED.getToolTipMessage(),
                "Tooltip of copy name button isn't equals to expected");
        softAssert.assertEquals(webhookCard.copyUrlButtonHoverAndGetTooltip(), TooltipEnum.TOOLTIP_COPY_URL_BUTTON_NOT_CLICKED.getToolTipMessage(),
                "Tooltip of copy url button isn't equals to expected");
        softAssert.assertEquals(webhookCard.editButtonHoverAndGetTooltip(), TooltipEnum.TOOLTIP_EDIT_BUTTON.getToolTipMessage(),
                "Tooltip of edit webhook button isn't equals to expected");
        softAssert.assertEquals(webhookCard.deleteButtonHoverAndGetTooltip(), TooltipEnum.TOOLTIP_DELETE_BUTTON.getToolTipMessage(),
                "Tooltip of delete webhook button isn't equals to expected");
        softAssert.assertEquals(webhookCard.authorImgHoverAndGetTooltip(), expectedAuthor,
                "Tooltip from author img isn't equals to expected");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4803", "ZTP-4804", "ZTP-4805", "ZTP-4806"})
    public void webhookCardActionAndTooltipVerification() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        webhookName = "Webhook ".concat(RandomStringUtils.randomAlphabetic(7));

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        WebhookCard webhookCard = webhooksModal.createWebhookCardOnlyWithName(webhookName);
        String generatedUrl = webhookCard.getWebhookUrlText();

        softAssert.assertEquals(webhookCard.copyNameButtonClickAndGetTooltip(), TooltipEnum.TOOLTIP_COPY_CLICKED.getToolTipMessage(),
                "Tooltip of copy name button after clicking isn't as expected");

        softAssert.assertEquals(webhooksModal.getClipboardText(), webhookName,
                "Copied webhook name isn't equals to expected"); // ZTP-4804 Verify user can copy webhook name

        softAssert.assertEquals(webhookCard.copyUrlButtonClickAndGetTooltip(), TooltipEnum.TOOLTIP_COPY_CLICKED.getToolTipMessage(),
                "Tooltip of copy url button after clicking isn't as expected");

        softAssert.assertEquals(webhooksModal.getClipboardText(), generatedUrl,
                "Copied url isn't equals to expected"); // ZTP-4805 Verify user can copy webhook url

        webhookCard.clickEditButton();
        softAssert.assertEquals(webhooksModal.getModalTitleText(), WebhooksModal.EDIT_WEBHOOK_MODAL_TITLE,
                "Modal title isn't equals to expected after clicking edit button"); // ZTP-4803 Verify edit webhook button present

        softAssert.assertEquals(webhooksModal.copyUrlButtonHoverAndGetTooltip(), TooltipEnum.TOOLTIP_COPY_URL_BUTTON_NOT_CLICKED.getToolTipMessage(),
                "Tooltip of copy url button isn't equals to expected on edit webhook modal");

        softAssert.assertEquals(webhooksModal.copyUrlButtonClickAndGetTooltip(), TooltipEnum.TOOLTIP_COPY_CLICKED.getToolTipMessage(),
                "Tooltip of copy url button after clicking isn't equals to expected on edit webhook modal");

        softAssert.assertEquals(webhooksModal.getClipboardText(), generatedUrl,
                "Copied url isn't equals to expected");

        webhooksModal = webhooksModal.clickCancel();
        softAssert.assertEquals(webhooksModal.getModalTitleText(), WebhooksModal.MODAL_NAME,
                "Modal title isn't equals to expected after canceling edit webhook card");

        webhookCard = webhooksModal.getCertainWebhookCard(webhookName);
        webhookCard.clickDeleteWebhookButton();

        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.WEBHOOK_DELETED.getDescription(),
                "Popup after deleting webhook isn't equals to expected");
        softAssert.assertFalse(webhooksModal.isWebhookPresent(webhookName),
                "Deleted webhook shouldn't be in the list"); // ZTP-4806 Verify user can delete webhook

        softAssert.assertAll();
    }

    @Test(groups =  "min_acceptance")
    @TestCaseKey({"ZTP-4845", "ZTP-4846", "ZTP-4847", "ZTP-4848"})
    public void editWebhook() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        webhookName = "Webhook ".concat(RandomStringUtils.randomAlphabetic(7));

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        WebhookCard webhookCard = webhooksModal.createWebhookCardOnlyWithName(webhookName);
        String generatedLink = webhookCard.getWebhookUrlText();

        webhookCard.clickEditButton();
        softAssert.assertEquals(webhooksModal.getModalTitleText(), WebhooksModal.EDIT_WEBHOOK_MODAL_TITLE,
                "Modal title isn't equals to expected after clicking edit button");

        webhookName = "Webhook edited ".concat(RandomStringUtils.randomAlphabetic(10));
        webhookSecretKey = WebhooksUtil.generateSecretKey();

        webhooksModal.typeWebhookName(webhookName);
        webhooksModal.typeWebhookSecretKey(webhookSecretKey);

        softAssert.assertEquals(webhooksModal.getCurrentVisibilityOfSecretKey(), "password",
                "Secret key visibility isn't equals to default");

        webhooksModal.togglePasswordVisibility();
        softAssert.assertEquals(webhooksModal.getCurrentVisibilityOfSecretKey(), "text",
                "Secret key visibility isn't changed"); // ZTP-4848 Verify user can change visibility of secret key

        webhooksModal.clickSaveButton();
        softAssert.assertEquals(addOrEditLauncherPage.getPopUp(), MessageEnum.WEBHOOK_UPDATED.getDescription(),
                "Popup message about updating webhook isn't equals to expected");
        softAssert.assertTrue(webhooksModal.isWebhookPresent(webhookName),
                "Updated webhook isn't appear in grid"); // ZTP-4845 Verify user can edit webhook

        webhookCard = webhooksModal.getCertainWebhookCard(webhookName);
        softAssert.assertTrue(webhookCard.isSecretKeyPresent(),
                "Secret key icon isn't present after adding secret key"); // ZTP-4846 Verify secret key icon appear after adding secret key

        webhooksModal.getCertainWebhookCard(webhookName)
                .clickEditButton();
        softAssert.assertEquals(webhooksModal.getEnteredNameFromEditModal(), webhookName,
                "Edited name isn't equals to expected on edit modal");

        softAssert.assertEquals(webhooksModal.getUrlFromEditModal(), generatedLink,
                "Url from edit modal isn't equals to previously generated");
        softAssert.assertTrue(webhooksModal.getTextFromWarningMessage().contains(WARNING_MESSAGE_BEFORE_CHANGING_SECRET_KEY),
                "Warning message isn't equals to expected"); // ZTP-4847 Verify warning message present on edit modal if secret key exists

        webhooksModal.clickChangeSecretKey();
        softAssert.assertTrue(webhooksModal.isCancelChangingSecretKeyButtonPresent(),
                "Cancel changing secret key button isn't present");
        softAssert.assertTrue(webhooksModal.isTogglePasswordVisibilityButtonPresent(),
                "Toggle password visibility button isn't present");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4861", "ZTP-4863", "ZTP-4864"})
    public void verifyNameValidationsWhenCreateWebhook() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        String webhookNameWith1Character = RandomStringUtils.randomAlphabetic(1);
        String webhookNameWith255Characters = RandomStringUtils.randomAlphabetic(255);
        String webhookNameWith256Characters = RandomStringUtils.randomAlphabetic(256);

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        webhooksModal.clickCreateButton();

        webhooksModal.typeWebhookName("");
        softAssert.assertFalse(webhooksModal.isSaveButtonActive(),
                "Save button active with empty name"); // ZTP-4863 Verify webhook name can't be empty on creation webhook

        webhooksModal.typeWebhookName(webhookNameWith1Character);
        softAssert.assertTrue(webhooksModal.isSaveButtonActive(), "Save button inactive with 1 symbol in name");
        softAssert.assertFalse(webhooksModal.isErrorTitleMessagePresent(), "Error message present with 1 symbol in name");

        webhooksModal.clickSaveButton();
        softAssert.assertTrue(webhooksModal.isWebhookPresent(webhookNameWith1Character),
                "Webhook with 1 character in title isn't present in grid"); // ZTP-4864 Verify user can enter 1 character to name field on creation

        webhooksModal.clickCreateButton()
                .typeWebhookName(webhookNameWith256Characters);

        softAssert.assertFalse(webhooksModal.isSaveButtonActive(), "Save button active with 256 symbol in name");
        softAssert.assertTrue(webhooksModal.isErrorTitleMessagePresent(), "Error message isn't present with 256 symbol in name");
        softAssert.assertEquals(webhooksModal.getErrorTitleMessage(), NAME_ERROR_MESSAGE,
                "Error message isn't as expected"); // ZTP-4861 Verify user can't enter >255 characters to 'Name' field on creation

        webhooksModal.typeWebhookName(webhookNameWith255Characters);

        softAssert.assertTrue(webhooksModal.isSaveButtonActive(), "Save button inactive with 255 symbol in name");
        softAssert.assertFalse(webhooksModal.isErrorTitleMessagePresent(),
                "Error message present with 255 symbol in name"); // ZTP-4864 Verify user can enter 255 characters to name field on creation

        webhooksModal.clickSaveButton();
        softAssert.assertTrue(webhooksModal.isWebhookPresent(webhookNameWith255Characters),
                "Webhook with 255 characters in title isn't present in grid");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4862", "ZTP-4865", "ZTP-4866"})
    public void verifyNameValidationsWhenEditWebhook() {
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        webhookName = "Webhook " + RandomStringUtils.randomAlphabetic(10);
        String webhookNameWith1Character = RandomStringUtils.randomAlphabetic(1);
        String webhookNameWith255Characters = RandomStringUtils.randomAlphabetic(255);
        String webhookNameWith256Characters = RandomStringUtils.randomAlphabetic(256);

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        WebhookCard webhookCard = webhooksModal.createWebhookCardOnlyWithName(webhookName);

        webhookCard.clickEditButton();
        webhooksModal.typeWebhookName("");
        Assert.assertFalse(webhooksModal.isSaveButtonActive(), "Save button active with empty name on editing");

        webhooksModal.typeWebhookName(webhookNameWith1Character);
        Assert.assertTrue(webhooksModal.isSaveButtonActive(), "Save button inactive with 1 symbol in name on editing");
        Assert.assertFalse(webhooksModal.isErrorTitleMessagePresent(), "Error message present with 1 symbol in title on editing");

        webhooksModal.clickSaveButton();
        Assert.assertTrue(webhooksModal.isWebhookPresent(webhookNameWith1Character), "Webhook with 1 character in title isn't present in grid after editing");

        webhooksModal.getCertainWebhookCard(webhookNameWith1Character).clickEditButton();

        webhooksModal.typeWebhookName(webhookNameWith256Characters);
        Assert.assertFalse(webhooksModal.isSaveButtonActive(), "Save button active with 256 symbol in name on editing");
        Assert.assertTrue(webhooksModal.isErrorTitleMessagePresent(), "Error message isn't present with 256 symbol in name on editing");

        webhooksModal.typeWebhookName(webhookNameWith255Characters);
        Assert.assertTrue(webhooksModal.isSaveButtonActive(), "Save button inactive with 255 symbol in name on editing");
        Assert.assertFalse(webhooksModal.isErrorTitleMessagePresent(), "Error message present with 255 symbol in name on editing");

        webhooksModal.clickSaveButton();
        Assert.assertTrue(webhooksModal.isWebhookPresent(webhookNameWith255Characters), "Webhook with 255 character in title isn't present in grid after editing");
    }

    @Test(groups =  "min_acceptance")
    @TestCaseKey({"ZTP-4868", "ZTP-4869"})
    public void checkWebhookTriggerWithSecretKey() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        webhookName = "Webhook ".concat(RandomStringUtils.randomAlphabetic(7));
        webhookSecretKey = WebhooksUtil.generateSecretKey();
        String timeZone = (String) ((JavascriptExecutor) getDriver()).executeScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;");

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        WebhookCard webhookCard = webhooksModal.createWebhookWithNameAndKey(webhookName, webhookSecretKey);
        String generatedLink = webhookCard.getWebhookUrlText();

        Response response = apiHelperService.triggerWebhook(generatedLink, null,
                null, false); // ZTP-4869 Verify user can't trigger webhook with secret key without signing

        Assert.assertEquals(response.getStatusCode(), 400,
                "400 code should be when triggering without secret key and timestamp");

        String currentTimeInUTCString = DateUtil.formatTime(Instant.now(), ZoneOffset.UTC, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        ZoneId utcZone = ZoneId.of("UTC");
        Instant currentTimeUTC = Instant.now().atZone(utcZone).toInstant();

        response = apiHelperService.triggerWebhook(generatedLink, webhookSecretKey,
                currentTimeInUTCString, true);
        String resultLink = JsonPath.from(response.getBody().asString()).get("data.resultLink");

        Assert.assertEquals(response.getStatusCode(), 202,
                "Triggering webhook with sign isn't successful, response code isn't 202"); // ZTP-4868 Verify user can trigger webhook with secret key
        webhooksModal.clickClose();

        webhooksModal = titleSection.clickWebhookButton();
        String timeTriggered = webhooksModal.getCertainWebhookCard(webhookName).getWebhookLastTimeTriggerText();

        Instant actualTime = DateUtil.convertToInstant(timeTriggered.substring(timeTriggered.indexOf("\n") + 1),
                ZoneId.of(timeZone));

        softAssert.assertTrue(
                Math.abs(Duration.between(currentTimeUTC, actualTime).getSeconds()) <= ALLOWED_DIFFERENCE_TRIGGERING.getSeconds(),
                "Last time trigger isn't equals to expected"
        );

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project);
        LaunchCard testRunCard = automationLaunchesPage.waitLaunchAppearByName(createdPreset.getName()).waitFinish();

        softAssert.assertEquals(testRunCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.PASSED,
                "After triggering webhook test run isn't finish successfully");

        response = apiHelperService.getResultsAfterTriggeringWebhookFromResultLink(resultLink, true,
                webhookSecretKey, currentTimeInUTCString);
        GetWebhookResponse getWebhookResponse = response.as(GetWebhookResponse.class);
        TestExecutionResults testExecutionResults = TestExecutionResults.builder()
                .passed(1)
                .failed(0)
                .failedAsKnown(0)
                .skipped(0)
                .aborted(0)
                .inProgress(0)
                .total(1)
                .build();

        softAssert.assertEquals(getWebhookResponse.getData().isFinished(), true,
                "Triggered run via webhook isn't finished - value given by result link");
        softAssert.assertEquals(getWebhookResponse.getData().getStatus(), TestRunStatusEnumR.PASSED.value(),
                "Status of run isn't equals to expected - value given by result link");
        softAssert.assertEquals(getWebhookResponse.getData().getTestExecutions(), testExecutionResults,
                "Test execution result isn't equals to expected");

        softAssert.assertAll();
    }

    @Test(groups =  "min_acceptance")
    @TestCaseKey("ZTP-4886")
    public void checkWebhookTriggerWithoutSecretKey() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        webhookName = "Webhook ".concat(RandomStringUtils.randomAlphabetic(7));
        String timeZone = (String) ((JavascriptExecutor) getDriver()).executeScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;");

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        WebhookCard webhookCard = webhooksModal.createWebhookCardOnlyWithName(webhookName);
        String generatedLink = webhookCard.getWebhookUrlText();

        Response response = apiHelperService.triggerWebhook(generatedLink, null,
                null, false);

        Assert.assertEquals(response.getStatusCode(), 202,
                "Triggering webhook without secret key isn't successful, response code isn't 202");

        ZoneId utcZone = ZoneId.of("UTC");
        Instant currentTimeUTC = Instant.now().atZone(utcZone).toInstant();

        webhooksModal.clickClose();

        String resultLink = JsonPath.from(response.getBody().asString()).get("data.resultLink");

        webhooksModal = titleSection.clickWebhookButton();
        webhookCard = webhooksModal.getCertainWebhookCard(webhookName);

        Instant actualTime = webhookCard.getWebhookLastTimeTriggerConvertedToZone(timeZone);

        log.info("Current time UTC " + currentTimeUTC);
        log.info("Actual last triggered time from modal " + actualTime);

        softAssert.assertTrue(
                Math.abs(Duration.between(currentTimeUTC, actualTime).getSeconds())
                        <= ALLOWED_DIFFERENCE_TRIGGERING.getSeconds(),
                "Last time trigger isn't equals to expected"
        );

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project);
        LaunchCard testRunCard = automationLaunchesPage.waitLaunchAppearByName(createdPreset.getName()).waitFinish();

        softAssert.assertEquals(testRunCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.PASSED,
                "After triggering webhook test run isn't finish successfully");

        response = apiHelperService.getResultsAfterTriggeringWebhookFromResultLink(resultLink, false,
                null, null);
        GetWebhookResponse getWebhookResponse = response.as(GetWebhookResponse.class);
        TestExecutionResults testExecutionResults = TestExecutionResults.builder()
                .passed(1)
                .failed(0)
                .failedAsKnown(0)
                .skipped(0)
                .aborted(0)
                .inProgress(0)
                .total(1)
                .build();

        softAssert.assertEquals(getWebhookResponse.getData().isFinished(), true,
                "Triggered run via webhook isn't finished - value given by result link");
        softAssert.assertEquals(getWebhookResponse.getData().getStatus(), TestRunStatusEnumR.PASSED.value(),
                "Status of run isn't equals to expected - value given by result link");
        softAssert.assertEquals(getWebhookResponse.getData().getTestExecutions(), testExecutionResults,
                "Test execution result isn't equals to expected");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4888")
    public void verifyUnableToTriggerWebhookWithoutSigningAfterAddingSecretKey() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WEBHOOK);

        SoftAssert softAssert = new SoftAssert();
        TitleSection titleSection = addOrEditLauncherPage.getTitleSection();
        webhookName = "Webhook ".concat(RandomStringUtils.randomAlphabetic(7));

        WebhooksModal webhooksModal = titleSection.clickWebhookButton();
        WebhookCard webhookCard = webhooksModal.createWebhookCardOnlyWithName(webhookName);
        String generatedLink = webhookCard.getWebhookUrlText();

        Response response = apiHelperService.triggerWebhook(generatedLink, null,
                null, false);

        Assert.assertEquals(response.getStatusCode(), 202,
                "Triggering webhook without secret key isn't successful, response code isn't 202");

        webhookCard = webhooksModal.getCertainWebhookCard(webhookName);
        webhookCard.clickEditButton();

        webhookSecretKey = WebhooksUtil.generateSecretKey();
        webhooksModal.typeWebhookSecretKey(webhookSecretKey);
        webhooksModal.clickSaveButton();

        response = apiHelperService.triggerWebhook(generatedLink, null,
                null, false);

        Assert.assertEquals(response.getStatusCode(), 400,
                "400 code should be when triggering without secret key and timestamp");

        String errorCode = response.getBody().jsonPath().get("code");

        softAssert.assertEquals(errorCode, "LNR-2218",
                "Error code isn't equals to expected after triggering webhook without signing");

        softAssert.assertAll();
    }

    private boolean isWebhookLinkValid(String link) {
        log.info("Received link - " + link);
        String pattern = ConfigHelper.getTenantUrl() + "/webhooks/.+:trigger";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(link);
        return m.matches();
    }

}
