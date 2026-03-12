package com.zebrunner.automation.gui;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Slf4j
@Getter
public class PopUp extends Element {

    public static final String POPUP_XPATH = "//div[@class='Toastify']";

    @FindBy(xpath = ".//div[contains(@class, 'Toastify__toast-body')]")
    private ExtendedWebElement popupMessage;

    public PopUp(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public PopUp waitUntilAppear() {
        boolean isFound = super.waitUntil(ExpectedConditions.visibilityOf(getRootExtendedElement().getElement()), 10);

        if (!isFound) {
            log.error("Popup is not found!");

            return null;
        } else {
            log.info("Popup is found!");

            return this;
        }
    }

}
