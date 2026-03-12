package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class DeleteTestRunModal extends AbstractUIObject {

    private static final String ROOT_XPATH = "//div[@aria-describedby='modal-dialog-content']";

    @FindBy(xpath =  ".//div[contains(@class, 'modal-text-container')]")
    private Element modalBodyText;

    @FindBy(xpath = ".//button[text()='Delete']")
    private Element deleteButton;

    public DeleteTestRunModal(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public void clickDelete(){
        deleteButton.click();
    }

}
