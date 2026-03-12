package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Getter
public class TestNavigation extends AbstractUIObject {

    public final static String ROOT_XPATH = "//*[@class = 'test-details__navigation']";
    public final static String NEXT_TEST_KEYBOARD_KEY = "k";
    public final static String PREVIOUS_TEST_KEYBOARD_KEY = "j";
    public final static String NEXT_NON_SUCCESSFULLY_TEST_KEY = "l";
    public final static String PREVIOUS_NON_SUCCESSFULLY_TEST_KEY = "h";

    @FindBy(xpath = ROOT_XPATH)
    private ExtendedWebElement testNavigation;

    @FindBy(xpath = ".//*[@class = 'Zbr-buttons-group']/button[@aria-label = 'Next (Type ‘k’)']")
    private ExtendedWebElement nextTestButton;

    @FindBy(xpath = ".//*[@class = 'Zbr-buttons-group']/button[@aria-label = 'Previous (Type ‘j’)']")
    private ExtendedWebElement previousTestButton;

    @FindBy(xpath = ".//*[@class = 'Zbr-buttons-group']/button[@aria-label = 'Next non-successful (Type ‘l’)']")
    private ExtendedWebElement nextNonSuccessfullyTestButton;

    @FindBy(xpath = ".//*[@class = 'Zbr-buttons-group']/button[@aria-label = 'Previous non-successful (Type ‘h’)']")
    private ExtendedWebElement previousNonSuccessfullyTestButton;

    @FindBy(xpath = ".//*[@class = 'test-details__navigation-text']")
    private ExtendedWebElement quantityOfTests;

    public TestNavigation(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isNextTestButtonClickable() {
        return nextTestButton.isClickable(3);
    }

    public boolean isNextNonSuccessfullyTestButtonClickable() {
        return nextNonSuccessfullyTestButton.isClickable(3);
    }

    public boolean isPreviousTestButtonClickable() {
        return previousTestButton.isClickable(3);
    }

    public boolean isPreviousNonSuccessfullyTestButtonClickable() {
        return previousNonSuccessfullyTestButton.isClickable(3);
    }

    public void clickNextTestButton() {
        waitUntil(ExpectedConditions.elementToBeClickable(nextTestButton.getElement()), 7);
        nextTestButton.click();
    }

    public void clickPreviousTestButton() {
        waitUntil(ExpectedConditions.elementToBeClickable(previousTestButton.getElement()), 7);
        previousTestButton.click();
    }

    public void clickNextNonSuccessfullyTestButton() {
        waitUntil(ExpectedConditions.elementToBeClickable(nextNonSuccessfullyTestButton.getElement()), 7);
        nextNonSuccessfullyTestButton.click();
    }

    public void clickPreviousNonSuccessfullyTestButton() {
        waitUntil(ExpectedConditions.elementToBeClickable(previousNonSuccessfullyTestButton.getElement()), 7);
        previousNonSuccessfullyTestButton.click();
    }

    public boolean isTestNavigationPresent() {
        return testNavigation.isElementPresent(3);
    }

    public String getQuantityOfTests() {
        return quantityOfTests.getText();
    }
}
