package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
@Getter
public class WebhooksModal extends AbstractModal<WebhooksModal> {
    public static final String MODAL_NAME = "Webhooks";
    public static final String EDIT_WEBHOOK_MODAL_TITLE = "Edit webhook";
    public static final String MODAL_NAME_ON_CREATION = "Create webhook";
    public static final String EXPECTED_PLACEHOLDER_FOR_EMPTY_MODAL = "Created webhooks will be listed here";

    @FindBy(xpath = "." + WebhookCard.ROOT_XPATH)
    private List<WebhookCard> webhookCards;

    @FindBy(xpath = "//div[@class='modal-header__title']")
    private Element modalTitle;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[text()='create']//parent::button")
    protected Element createButton;

    @FindBy(xpath = ".//div[@class='webhooks-placeholder__text']")
    protected Element webhooksPlaceholderText;

    @FindBy(xpath = ".//div[@class='webhooks-placeholder__icon']")
    protected Element webhooksPlaceholderIcon;

    @FindBy(id = "name")
    private Element inputWebhookName;

    @FindBy(id = "base64SecretKey")
    private Element inputSecretKey;

    @FindBy(xpath = ".//div[contains(@class, 'tooltip Zbr-copy-to-clipboard')]")
    private Element copyUrlButton; // present only on edit webhook modal

    @FindBy(xpath = ".//button[@aria-label='Toggle password visibility']")
    private Element togglePasswordVisibilityButton; // on edit modal

    @FindBy(xpath = ".//div[@class='webhook-warning__text']")
    private Element warningMessage; // on edit modal and secret key exists

    @FindBy(xpath = ".//span[@role='presentation']")
    private Element buttonChangeSecretKey; // on edit modal and secret key exists

    @FindBy(xpath = ".//div[@class='secret-key-wrapper']/button")
    private Element cancelChangingSecretKeyButton; // on edit, if clicked change secret key button

    @FindBy(id = "link")
    private Element urlInput; // on edit modal

    @FindBy(xpath = "//span[@class='input-message-animation item-enter-done']")
    private Element errorTitleMessage;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public WebhooksModal(WebDriver driver) {
        super(driver);
    }

    public static WebhooksModal getModalInstance(WebDriver driver) {
        return new WebhooksModal(driver);
    }

    public WebhooksModal clickCreateButton() {
        createButton.click();
        return this;
    }

    public String getPlaceholderText() {
        return webhooksPlaceholderText.getText();
    }

    public String getBackgroundCreateButtonColor() {
        pause(1);
        return ColorUtil.getHexColorFromString(createButton.getElement().getCssValue("background-color"));
    }

    public boolean isPlaceholderIconPresent() {
        return webhooksPlaceholderIcon.isStateMatches(Condition.VISIBLE);
    }

    public boolean isInputWebhookNamePresent() {
        return inputWebhookName.isStateMatches(Condition.VISIBLE);
    }

    public boolean isSecretKeyInputPresent() {
        return inputSecretKey.isStateMatches(Condition.VISIBLE);
    }

    public boolean isSaveButtonActive() {
        return submitButton.isStateMatches(Condition.CLICKABLE);
    }

    public WebhooksModal clickCancelCreationWebhookButton() {
        cancelButton.click();
        return this;
    }

    public void hoverCreateButton() {
        createButton.hover();
    }

    public String getModalTitleText() {
        return modalTitle.getText();
    }

    public WebhooksModal clickSaveButton() {
        submitButton.click();
        return this;
    }

    public void typeWebhookName(String name) {
        inputWebhookName.sendKeys(name);
    }

    public void typeWebhookSecretKey(String key) {
        inputSecretKey.sendKeys(key);
    }

    public boolean isWebhookPresent(String webhookName) {
        if (this.getPlaceholderText().equals(EXPECTED_PLACEHOLDER_FOR_EMPTY_MODAL)) {
            log.info("Can't find any of webhooks");
            return false;
        }

        log.debug("Waiting for webhook cards list to load...");
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(webhookCards,
                card -> card.getWebhookNameText().equals(webhookName));
    }

    public WebhookCard getCertainWebhookCard(String webhookName) {
        if (this.getPlaceholderText().equals(EXPECTED_PLACEHOLDER_FOR_EMPTY_MODAL)) {
            throw new RuntimeException("Can't find webhook cards");
        }

        return WaitUtil.waitElementAppearedInListByCondition(webhookCards,
                card -> card.getWebhookNameText().equalsIgnoreCase(webhookName),
                "Found webhook card with name " + webhookName,
                "Not found webhook with name " + webhookName);
    }

    public WebhookCard createWebhookCardOnlyWithName(String name) {
        clickCreateButton();
        typeWebhookName(name);
        clickSaveButton();

        return getCertainWebhookCard(name);
    }

    public WebhookCard createWebhookWithNameAndKey(String name, String key) {
        clickCreateButton();
        typeWebhookName(name);
        typeWebhookSecretKey(key);
        clickSaveButton();

        return getCertainWebhookCard(name);
    }

    public String copyUrlButtonClickAndGetTooltip() {
        copyUrlButton.click();
        //waitUntil(ExpectedConditions.textToBePresentInElement(tooltip.getRootExtendedElement().getElement(), "Copied!"), 2);
        //pause(1); // need to use pause directly because of short time of switching tooltip text
        return tooltip.getTextFromTooltipDirectly();
    }

    public String copyUrlButtonHoverAndGetTooltip() {
        copyUrlButton.hover();
        return tooltip.getTooltipText();
    }

    public void togglePasswordVisibility() {
        togglePasswordVisibilityButton.click();
    }

    public String getCurrentVisibilityOfSecretKey() {
        return inputSecretKey.getAttributeValue("type");
    }

    public String getTextFromWarningMessage() {
        return warningMessage.getText();
    }

    public String getEnteredNameFromEditModal() {
        return inputWebhookName.getAttributeValue("value");
    }

    public void clickChangeSecretKey() {
        buttonChangeSecretKey.click();
    }

    public boolean isCancelChangingSecretKeyButtonPresent() {
        return cancelChangingSecretKeyButton.isStateMatches(Condition.VISIBLE);
    }

    public boolean isTogglePasswordVisibilityButtonPresent() {
        return togglePasswordVisibilityButton.isStateMatches(Condition.VISIBLE);
    }

    public String getUrlFromEditModal() {
        return urlInput.getAttributeValue("value");
    }

    public boolean isErrorTitleMessagePresent() {
        return errorTitleMessage.isStateMatches(Condition.VISIBLE);
    }

    public String getErrorTitleMessage() {
        return errorTitleMessage.getText();
    }
}
