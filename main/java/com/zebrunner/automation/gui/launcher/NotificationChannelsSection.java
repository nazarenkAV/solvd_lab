package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
@Slf4j
public class NotificationChannelsSection extends AbstractUIObject {
    private final String ERROR_MESSAGE_XPATH = ".//ancestor::div[contains(@class,'input-container _with-label')]//div[@class='input-messages-animation']//span";
    @FindBy(xpath = ".//button[contains(@class,'selected-launcher__button _expand')]")
    private Element expandCollapseBtn;

    @FindBy(id = "slackChannels")
    private Element slackChannel;

    @FindBy(id = "emails")
    private Element email;

    @FindBy(id = "teamsChannels")
    private Element teamsChannel;

    public NotificationChannelsSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getSlackChannels() {
        if (!slackChannel.isStateMatches(Condition.VISIBLE)) {
            expandCollapseBtn.click();
        }
        return slackChannel.getAttributeValue("value");
    }

    public String getEmails() {
        if (!email.isStateMatches(Condition.VISIBLE)) {
            expandCollapseBtn.click();
        }
        return email.getAttributeValue("value");
    }

    public String getMsTeamsChannels() {
        if (!teamsChannel.isStateMatches(Condition.VISIBLE)) {
            expandCollapseBtn.click();
        }
        return teamsChannel.getAttributeValue("value");
    }

    public NotificationChannelsSection typeSlackChannel(String slackChannelValue) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Adding slack channel...");
        if (!slackChannel.isStateMatches(Condition.VISIBLE)) {
            expandCollapseBtn.click();
        }
        ComponentUtil.scrollToElementCenter(getDriver(), slackChannel.getElement());
        slackChannel.sendKeys(slackChannelValue);
        return this;
    }

    public NotificationChannelsSection typeMSTeamsChannel(String MSTeamsChannelValue) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Adding MS Teams channel...");
        if (!teamsChannel.isStateMatches(Condition.VISIBLE)) {
            expandCollapseBtn.click();
        }
        ComponentUtil.scrollToElementCenter(getDriver(), teamsChannel.getElement());
        teamsChannel.sendSecretKeys(MSTeamsChannelValue);
        return this;
    }

    public NotificationChannelsSection typeEmail(String emailValue) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Adding email...");
        if (!email.isStateMatches(Condition.VISIBLE)) {
            expandCollapseBtn.click();
        }
        ComponentUtil.scrollToElementCenter(getDriver(), email.getElement());
        email.sendKeys(emailValue, true, true);
        return this;
    }

    public NotificationChannelsSection expandChannel() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        if (!isNotificationChannelExpanded()) {
            log.info("Expanding channels...");
            expandCollapseBtn.click();
        }
        return this;
    }

    public NotificationChannelsSection collapseChannel() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        if (isNotificationChannelExpanded()) {
            log.info("Collapsing channels...");
            expandCollapseBtn.click();
        }
        return this;
    }

    public String getInputFieldErrorMessage(Element element) {
        try {
            ExtendedWebElement el = element.getRootExtendedElement()
                    .findExtendedWebElement(
                            By.xpath(ERROR_MESSAGE_XPATH), 3);
            return el.getText();
        } catch (NoSuchElementException ignored) {
        }
        return "";
    }

    public boolean isInputFieldErrorMessagePresent(Element element) {
        ExtendedWebElement el = null;
        try {
            el = element.getRootExtendedElement()
                    .findExtendedWebElement(
                            By.xpath(ERROR_MESSAGE_XPATH), 3);
        } catch (NoSuchElementException ignored) {
        }
        if (!(el == null)) {
            return el.isPresent(5);
        } else
            return false;
    }

    public boolean isExpandNotificationChannelButtonClickable() {
        return expandCollapseBtn.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isEmailsFieldVisible() {
        email.waitUntil(Condition.VISIBLE);
        return email.isStateMatches(Condition.VISIBLE);
    }

    public boolean isEmailsFieldClickable() {
        return email.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isMsTeamsFieldVisible() {
        return teamsChannel.isStateMatches(Condition.VISIBLE);
    }

    public boolean isEmailsFieldDisabled() {
        return email
                .findExtendedWebElement(By.xpath(
                        ".//ancestor::section[@class='selected-launcher__section _notification-channels']//*[contains(@class,'selected-launcher__section-body-row')]"))
                .getAttribute("class").contains("_disabled");
    }

    public boolean isSlackFieldVisible() {
        return slackChannel.isStateMatches(Condition.VISIBLE);
    }

    public boolean isSlackFieldDisabled() {
        return slackChannel
                .findExtendedWebElement(By.xpath(
                        ".//ancestor::section[@class='selected-launcher__section _notification-channels']//*[contains(@class,'selected-launcher__section-body-row')]"))
                .getAttribute("class").contains("_disabled");
    }

    public boolean isMsTeamsFieldDisabled() {
        return teamsChannel
                .findExtendedWebElement(By.xpath(
                        ".//ancestor::section[@class='selected-launcher__section _notification-channels']//*[contains(@class,'selected-launcher__section-body-row')]"))
                .getAttribute("class").contains("_disabled");
    }

    public boolean isNotificationChannelExpanded() {
        return expandCollapseBtn.findElement(By.tagName("svg")).getAttribute("class").equals("_turn-up");
    }
}
