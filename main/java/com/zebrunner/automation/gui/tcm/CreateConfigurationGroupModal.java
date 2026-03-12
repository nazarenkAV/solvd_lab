package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CreateConfigurationGroupModal extends AbstractModal<CreateConfigurationGroupModal> {
    @FindBy(id = "name")
    private ExtendedWebElement titleInput;

    public CreateConfigurationGroupModal(WebDriver driver) {
        super(driver);
        super.setBy(By.xpath("//div[@aria-describedby='modal-dialog-content'][form]"));
    }

    public CreateConfigurationGroupModal inputTitle(String title) {
        titleInput.type(title);
        return this;
    }

}
