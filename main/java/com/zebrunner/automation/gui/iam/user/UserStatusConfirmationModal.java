package com.zebrunner.automation.gui.iam.user;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class UserStatusConfirmationModal extends AbstractModal<UserStatusConfirmationModal> {
    public static final String DEACTIVATE_USER_MODAL_TITLE = "Deactivate user?";
    public static final String ACTIVATE_USER_MODAL_TITLE = "Activate user?";

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text() = 'Deactivate']")
    private ExtendedWebElement deactivateButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text() = 'Activate']")
    private ExtendedWebElement activateButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text() = 'Confirm']")
    private ExtendedWebElement confirmButton;

    public UserStatusConfirmationModal(WebDriver driver) {
        super(driver);
    }

    public void deactivate() {
        deactivateButton.click();
    }

    public void activate() {
        activateButton.click();
    }

    public void confirm() {
        confirmButton.click();
    }
}
