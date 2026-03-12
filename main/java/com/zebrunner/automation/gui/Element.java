package com.zebrunner.automation.gui;

import com.zebrunner.automation.util.KeyCombinations;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.not;

@Slf4j
public class Element extends AbstractUIObject {
    private static final Duration TIME_TO_LOAD_ELEMENT = Duration.ofSeconds(3);
    private static final Duration WAITING_TIME = Duration.ofSeconds(10);

    public Element(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public void click() {
        waitUntil(ExpectedConditions.visibilityOf(super.getElement()), 5);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
                super.getElement());
        pause(1);
        super.click();
    }

    public void doubleClick() {
        super.doubleClick(TIME_TO_LOAD_ELEMENT.toSeconds());
    }

    public void clickUsingJS() {
        waitUntil(ExpectedConditions.elementToBeClickable(super.getElement()), TIME_TO_LOAD_ELEMENT.toSeconds());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", super.getElement());
    }

    public void sendKeys(String text) {
        sendKeys(text, true, false);
    }

    public void sendKeys(String text, Boolean clearBefore, Boolean autoSubmit) {
        waitUntil(ExpectedConditions.visibilityOf(super.getElement()), 5);
        if (clearBefore) {
            this.click();
            KeyCombinations.CTRL_A.createAction(getDriver()).perform();
            super.sendKeys(Keys.DELETE);
        }

        if (autoSubmit)
            text += '\n';

        type(text);
    }

    public void sendSecretKeys(String secretText) {
        WebElement inputFieldElement = super.getElement();
        ExtendedWebElement inputField = this;
        Actions action = new Actions(getDriver());
        action.click(inputFieldElement).click(inputFieldElement).click(inputFieldElement).perform();
        inputField.sendKeys(Keys.DELETE);
        inputField.type(secretText);
    }

    public String getText() {
        String text = "";
        try {
           text =  super.getText();
            log.info("We get element with text " + text);
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            text = String.format("Element '%s' not found! ", super.getBy());
            log.error(text);
        }
        return  text;
    }

    public Boolean isStateMatches(Condition condition) {
        //  pause(TIME_TO_LOAD_ELEMENT.toSeconds());
        boolean state = false;
        switch (condition) {
        case CLICKABLE:
            state = isClickable();
            break;
        case NOT_CLICKABLE:
            state = !isClickable();
            break;
        case VISIBLE:
            state = isVisible();
            break;
        case NON_VISIBLE:
            state = !isVisible();
            break;
        case PRESENT:
            state = isPresent();
            break;
        case DISAPPEAR:
            state = isDisappear();
            break;
        case PRESENT_AND_CLICKABLE:
            state = isPresent() && isClickable();
            break;
        case VISIBLE_AND_CLICKABLE:
            state = isVisible() && isClickable();
            break;
        default:
            throw new RuntimeException("There are no state handler " + condition + " for " + this.getType());
        }
        return state;
    }

    public Element waitUntil(Condition condition) {
        ExpectedCondition<?> conditions;
        switch (condition) {
        case CLICKABLE:
            conditions = ExpectedConditions.elementToBeClickable(super.getElement());
            break;
        case NOT_CLICKABLE:
            conditions = not(ExpectedConditions.elementToBeClickable(super.getElement()));
            break;
        case VISIBLE:
            conditions = ExpectedConditions.visibilityOf(super.getElement());
            break;
        case NON_VISIBLE:
            conditions = not(ExpectedConditions.visibilityOf(super.getElement()));
            break;
        case PRESENT:
            conditions = ExpectedConditions.presenceOfElementLocated(super.getBy());
            break;
        case DISAPPEAR:
            conditions = ExpectedConditions.stalenessOf(super.getElement());
            break;
        case PRESENT_AND_CLICKABLE:
            conditions = ExpectedConditions.and(
                    ExpectedConditions.presenceOfElementLocated(super.getBy()),
                    ExpectedConditions.elementToBeClickable(super.getElement()));
            break;
        case VISIBLE_AND_CLICKABLE:
            conditions = ExpectedConditions.and(
                    ExpectedConditions.visibilityOf(super.getElement()),
                    ExpectedConditions.elementToBeClickable(super.getElement()));
            break;
        default:
            throw new RuntimeException("There are no waiting state handler " + condition + " for " + this.getType());
        }
        waitUntil(conditions, WAITING_TIME.toSeconds());
        return this;
    }

    public boolean isDisappear() {
        return super.waitUntilElementDisappear(TIME_TO_LOAD_ELEMENT.toSeconds());
    }

    public boolean isClickable() {
        return super.isClickable(TIME_TO_LOAD_ELEMENT.toSeconds());
    }

    public boolean isPresent() {
        return super.isPresent(TIME_TO_LOAD_ELEMENT.toSeconds());
    }

    public boolean isVisible() {
        return super.isVisible(TIME_TO_LOAD_ELEMENT.toSeconds());
    }

    public String getAttributeValue(String attribute) {
        return super.getAttribute(attribute).trim();
    }

    public void hover() {
        waitUntil(ExpectedConditions.visibilityOf(super.getElement()), 5);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
                super.getElement());
        pause(1);
        super.hover();
    }

}
