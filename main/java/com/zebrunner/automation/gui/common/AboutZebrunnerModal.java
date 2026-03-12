package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class AboutZebrunnerModal extends AbstractModal<AboutZebrunnerModal> {

    public static final String MODAL_TITLE = "About Zebrunner";

    @FindBy(xpath = "//button[text() = 'OK']")
    private ExtendedWebElement okButton;

    public AboutZebrunnerModal(WebDriver driver) {
        super(driver);
    }

    public void clickOkButton() {
        okButton.click();
    }
}
