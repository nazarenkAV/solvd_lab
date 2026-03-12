package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class DeleteSharedStepsModal extends AbstractModal<DeleteSharedStepsModal> {
    @FindBy(xpath = ".//*[@class='modal-header__inner']/h4")
    private Element modalTitle;

    @FindBy(xpath = ".//div[contains(@class, 'modal-text-container')]")
    private Element modalBodyText;

    @FindBy(xpath = ".//button[text()='Delete']")
    private Element deleteButton;


    public DeleteSharedStepsModal(WebDriver driver) {
        super(driver);
    }

    public String getModalBodyText() {
        return modalBodyText.getText();
    }

    public boolean isModalOpened() {
        return getRootExtendedElement().isPresent(5);
    }

    public void close() {
        close.click();
    }

    public void delete() {
        deleteButton.click();
    }

}

