package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Instant;
import java.time.ZoneId;

@Getter
public class WebhookCard extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[@class='webhook-item']";
    public static final String NEVER_TRIGGERED = "Has never been triggered";

    @FindBy(xpath = ".//div[@class='webhook-item__name']")
    private ExtendedWebElement webhookName;

    @FindBy(xpath = ".//div[@class='webhook-item__url']")
    private ExtendedWebElement webhookUrl;

    @FindBy(xpath = ".//div[@class='webhook-item__col _time']")
    private ExtendedWebElement webhookLastTimeTrigger;

    @FindBy(xpath = ".//div[@aria-label='Edit webhook']")
    private ExtendedWebElement webhookEditButton;

    @FindBy(xpath = ".//div[@aria-label='Delete webhook']")
    private ExtendedWebElement webhookDeleteButton;

    @FindBy(xpath = ".//div[@class='webhook-item__col _author']/p")
    private ExtendedWebElement webhookAuthor;

    @FindBy(xpath = ".//div[@class='webhook-item__image']")
    private ExtendedWebElement authorImg;

    @FindBy(xpath = ".//div[contains(@class, 'webhook-item__name')]/div[contains(@class, 'tooltip Zbr-copy-to-clipboard')]")
    private ExtendedWebElement copyWebhookNameButton;

    @FindBy(xpath = ".//div[contains(@class, 'webhook-item__url')]/div[contains(@class, 'tooltip Zbr-copy-to-clipboard')]")
    private ExtendedWebElement copyUrlButton;

    @FindBy(xpath = ".//div[@aria-label='Delete webhook']")
    private ExtendedWebElement deleteButton;

    @FindBy(xpath = "//*[@class='webhook-item__key']")
    private ExtendedWebElement secretKeyImg;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public WebhookCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getWebhookNameText() {
        return webhookName.getText();
    }

    public String getWebhookUrlText() {
        return webhookUrl.getText();
    }

    public String getWebhookLastTimeTriggerText() {
        return webhookLastTimeTrigger.getText();
    }

    public String getWebhookAuthorName() {
        return webhookAuthor.getText();
    }

    public boolean isEditButtonPresent() {
        return webhookEditButton.isPresent(5);
    }

    public boolean isDeleteButtonPresent() {
        return webhookDeleteButton.isPresent(5);
    }

    public String copyNameButtonHoverAndGetTooltip() {
        copyWebhookNameButton.hover();
        copyWebhookNameButton.hover();
        return getTooltip();
    }

    public String copyUrlButtonHoverAndGetTooltip() {
        copyUrlButton.hover();
        return getTooltip();
    }

    public String editButtonHoverAndGetTooltip() {
        webhookEditButton.hover();
        return getTooltip();
    }

    public String deleteButtonHoverAndGetTooltip() {
        webhookDeleteButton.hover();
        return getTooltip();
    }

    public String authorImgHoverAndGetTooltip() {
        authorImg.hover();
        return getTooltip();
    }

    public void clickCopyNameButton() {
        copyWebhookNameButton.hover();
        copyWebhookNameButton.hover();

        copyWebhookNameButton.click();
    }

    public void clickCopyUrlButton() {
        copyUrlButton.hover();
        copyUrlButton.click();
    }

    public String copyNameButtonClickAndGetTooltip() {
        clickCopyNameButton();
        //waitUntil(ExpectedConditions.textToBePresentInElement(tooltip.getRootExtendedElement().getElement(), "Copied!"), 2);
        //pause(1); // need to use pause directly because of short time of switching tooltip text
        return tooltip.getTextFromTooltipDirectly();
    }

    public String copyUrlButtonClickAndGetTooltip() {
        clickCopyUrlButton();
        //waitUntil(ExpectedConditions.textToBePresentInElement(tooltip.getRootExtendedElement().getElement(), "Copied!"), 2);
        //pause(1); // need to use pause directly because of short time of switching tooltip text
        return tooltip.getTextFromTooltipDirectly();
    }

    public void clickEditButton() {
        webhookEditButton.click();
    }

    public void clickDeleteWebhookButton() {
        deleteButton.click();
    }

    public String getTooltip() {
        pause(3);
        return tooltip.getTooltipText();
    }

    public boolean isSecretKeyPresent() {
        waitUntil(ExpectedConditions.visibilityOf(secretKeyImg.getElement()), 5);
        return secretKeyImg.isPresent();
    }

    public Instant getWebhookLastTimeTriggerConvertedToZone(String timeZone) {
        String timeTriggered = getWebhookLastTimeTriggerText();
        return DateUtil.convertToInstant(timeTriggered.substring(timeTriggered.indexOf("\n") + 1),
                ZoneId.of(timeZone));
    }
}
