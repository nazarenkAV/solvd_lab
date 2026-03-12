package com.zebrunner.automation.gui.tcm;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;

public class DeleteModal extends AbstractModal<DeleteModal> {

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Delete']")
    private ExtendedWebElement deleteButton;

    public DeleteModal(WebDriver driver) {
        super(driver);
        super.setBy(By.xpath("//div[@aria-describedby='modal-dialog-content'][.//h4[starts-with(text(), 'Delete')]]"));
    }

    public void clickDeleteButton() {
        deleteButton.click();
    }

}
