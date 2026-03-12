package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.ZbrTimeInput;
import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class AddExecutionResultModal extends AbstractModal<AddExecutionResultModal> {

    @FindBy(xpath = ".//button[contains(@class, 'repository-case-modal-attachments-button')]")
    private ExtendedWebElement addAttachmentButton;

    @FindBy(xpath = ".//*[@class='repository-case-modal-attachments']//input")
    private ExtendedWebElement attachmentInput;

    @FindBy(xpath = ".//label[text()='Type']/following-sibling::div")
    private ExtendedWebElement typeStatusButton;

    @FindBy(xpath = ".//label[text()='Result status']/following-sibling::div")
    private ExtendedWebElement resultStatusButton;

    @Getter
    @FindBy(xpath = ZbrTimeInput.ROOT_XPATH)
    private ZbrTimeInput elapsedTime;

    public AddExecutionResultModal(WebDriver driver) {
        super(driver);
    }

    public AddExecutionResultModal addAttachment(String filePath) {
        attachmentInput.attachFile(filePath);
        return this;
    }

    public ListBoxMenu getListExecutionTypes() {
        typeStatusButton.click();
        return new ListBoxMenu(getDriver());
    }

    public AddExecutionResultModal selectExecutionType(ExecutionTypesEnum automationType) {
        getListExecutionTypes().clickItem(automationType.getValue());
        return this;
    }

    public String getSelectedExecutionType() {
        return typeStatusButton.getText();
    }

    public ListBoxMenu clickResultStatuses() {
        resultStatusButton.click();
        return new ListBoxMenu(getDriver());
    }

    public String getCurrentElapsedTimeValue() {
        return getElapsedTime().getCurrentTime();
    }

    public void clickAddAttachmentButton() {
        addAttachmentButton.click();
    }

    @Getter
    @AllArgsConstructor
    public enum ExecutionTypesEnum {
        MANUAL("Manual"),
        AUTOMATED("Automated");

        private final String value;
    }

}
