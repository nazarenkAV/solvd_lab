package com.zebrunner.automation.gui.tcm;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;

@Getter
public class EditConfigurationModal extends AbstractModal<EditConfigurationModal> {

    @FindBy(id = "name")
    private Element titleInput;

    public EditConfigurationModal(WebDriver driver) {
        super(driver);
        super.setBy(By.xpath("//div[@aria-describedby='modal-dialog-content'][form]"));
    }

    public EditConfigurationModal inputTitle(String title) {
        titleInput.sendKeys(title);
        return this;
    }

    public boolean isSaveButtonClickable() {
        return submitButton.isClickable(3);
    }

    public boolean isModalVisible() {
        return super.getRootExtendedElement().isVisible(5);
    }

}
