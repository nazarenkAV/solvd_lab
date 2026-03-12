package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ReviewModalR extends AbstractUIObject {

    private static final String MODAL_NAME = "Review";
    @FindBy(xpath = ".//h2[@class='launch-review-popover__header-title']")
    private ExtendedWebElement titleOfWindow;

    @FindBy(xpath = ".//button[text()='Submit']")
    private Element submitButton;

    @FindBy(xpath = ".//div[@class='launch-review-form__text']")
    private ExtendedWebElement reviewFormText;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')]")
    private List<Element> reviewers;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')]")
    private Element reviewer;

    @FindBy(id = "outlined-multiline-static")
    private Element inputCommentField;

    @FindBy(xpath = ".//div[@class='launch-review-form__notifications']//input[@type='checkbox']")
    private Element postUpdateToNotificationChannelCheckbox;

    @FindBy(xpath = ".//button[contains(@class, 'button icon tertiary  css-1obwva')]")
    private ExtendedWebElement closeWindowField;

    public ReviewModalR(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[contains(@class,'MuiPopover-paper launch-review-popover')]"));
    }

    public static ReviewModalR getInstance(WebDriver driver) {
        log.info("Attempt to open '{}' modal window", MODAL_NAME);
        return new ReviewModalR(driver);
    }

    public ReviewModalR typeComment(String text) {
        log.info("Adding comments on {} ....", MODAL_NAME);
        inputCommentField.sendKeys(text, true, false);
        return this;
    }

    public void fillReviewAndMaskAsReviewed(String text) {
        reviewFormText.clickIfPresent(3);
        typeComment(text);
        submitButton.click();
    }

    public String getReviewText() {
        return reviewFormText.getText();
    }

    public List<String> getReviewers() {
        return reviewers.stream().map(Element::getText).collect(Collectors.toList());
    }

    public boolean isReviewModalOpened() {
        return titleOfWindow.isElementPresent(3);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }

    public UserInfoTooltip hoverReviewer() {
        reviewer.hover();
        return new UserInfoTooltip(getDriver());
    }
}
