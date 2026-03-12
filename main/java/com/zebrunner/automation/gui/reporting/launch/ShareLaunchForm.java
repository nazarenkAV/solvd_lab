package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class ShareLaunchForm extends AbstractUIObject {

    @FindBy(xpath = ".//button[contains(@class, 'MuiButton-disableElevation button icon tertiary')]")
    private ExtendedWebElement closeButton;

    @FindBy(id = "message")
    private Element message;

    @FindBy(xpath = ".//input[@placeholder='Start typing to search']")
    private Element emailInput;

    @FindBy(xpath = ".//button[text()='Send']")
    private Element sendButton;

    @FindBy(xpath = ".//*[text()='copy url']//parent::button")
    private Element copyUrlButton;

    private ShareLaunchForm(WebDriver driver) {
        super(driver);
    }

    public ShareLaunchForm(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public static ShareLaunchForm getInstance(WebDriver driver) {
        return new ShareLaunchForm(driver);
    }

    public void addMessage(String email) {
        message.sendKeys(email);
    }

    public TestRunResultPageR send() {
        sendButton.click();
        return TestRunResultPageR.getPageInstance(getDriver());
    }

    public boolean isSendButtonActive() {
        return sendButton.isStateMatches(Condition.CLICKABLE);
    }

    public void clickCopyUrlButton() {
        copyUrlButton.click();
    }

}
