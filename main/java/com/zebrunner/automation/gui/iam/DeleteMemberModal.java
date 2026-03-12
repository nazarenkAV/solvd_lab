package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;


public class DeleteMemberModal extends AbstractModal<DeleteMemberModal> {

    @FindBy(xpath = ".//div[contains(@class,'modal-text-container')]]")
    private ExtendedWebElement warningMessage; //should contain name of deleting member

    @FindBy(xpath = ".//button[text() = 'Delete']")
    private ExtendedWebElement deleteButton;

    public DeleteMemberModal(WebDriver driver) {
        super(driver);
    }

    public void delete() {
        deleteButton.click();
    }
}
