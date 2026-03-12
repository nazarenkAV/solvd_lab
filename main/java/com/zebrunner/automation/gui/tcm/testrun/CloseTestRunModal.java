package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CloseTestRunModal extends AbstractModal<CloseTestRunModal> {
    public static final String MODAL_TITLE = "Close test run?";

    @FindBy(xpath = ".//div[@class='modal-header__inner']//h4")
    private ExtendedWebElement modalTitle;

    @FindBy(xpath = ".//button[contains(text(),'Close')]")
    private ExtendedWebElement closeButton;

    @FindBy(xpath = ".//button[contains(@class, 'tertiary')]")
    private ExtendedWebElement crossButton;

    public CloseTestRunModal(WebDriver driver) {
        super(driver);
    }

    public boolean isModalOpened() {
        return modalTitle.isVisible(3);
    }

    public void clickCloseButton() {
        closeButton.click();
    }

    public void clickCrossButton() {
        crossButton.click();
    }
}
