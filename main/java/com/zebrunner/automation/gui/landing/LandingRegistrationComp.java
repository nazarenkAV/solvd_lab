package com.zebrunner.automation.gui.landing;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

//div[@name='registration']
public class LandingRegistrationComp extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(xpath = ".//input[@name='name']")
    private ExtendedWebElement orgNameInput;

    @FindBy(xpath = ".//input[@name='ownerName']")
    private ExtendedWebElement ownerNameInput;

    @FindBy(xpath = ".//input[@name='email']")
    private ExtendedWebElement emailInput;

    @FindBy(xpath = ".//label[@for='form-subscribe-checkbox']")
    private ExtendedWebElement newsCheckbox;

    @FindBy(xpath = ".//button[@type='submit' and text()='Create Free Workspace']")
    private ExtendedWebElement submitButton;

    public LandingRegistrationComp(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void createFreeWorkspace(String orgName, String ownerName, String email) {
        orgNameInput.type(orgName);
        ownerNameInput.type(ownerName);
        emailInput.type(email);
        newsCheckbox.click();
        submitButton.click();
        LOGGER.info("Created new tenant with name " + orgName + " for email " + email);
    }
}
