package com.zebrunner.automation.gui.integration;

import com.zebrunner.automation.gui.common.RadioWrapper;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EditIntegrationModal extends AbstractModal<EditIntegrationModal> {

    public static final String MODAL_NAME = "Edit integration";

    public static final String INTEGRATION_IS_CONNECTED = "Integration is connected.";

    @FindBy(xpath = "//*[text()='Enable for all projects']" + RadioWrapper.ROOT_XPATH)
    private RadioWrapper enableForAllProjectWrapper;

    @FindBy(xpath = ".//button[text()='Test']")
    private ExtendedWebElement testButton;

    @FindBy(xpath = "//div[@class='integration-details-test-message__message-text']")
    private ExtendedWebElement connectMessage;

    public EditIntegrationModal(WebDriver driver) {
        super(driver);
    }

    public void clickTestIntegration() {
        testButton.click();
    }

    public String getConnectMessageText() {
        return connectMessage.getText();

    }

    public String waitSuccessMessage() {
        waitUntil(ExpectedConditions.textToBe(connectMessage.getBy(), INTEGRATION_IS_CONNECTED), 15);
        return connectMessage.getText();
    }

    public boolean isEnabledForAllProjects() {
        return enableForAllProjectWrapper.isChecked();
    }
}
