package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ConfirmCancelOfTestCaseCreationModal extends AbstractModal<ConfirmCancelOfTestCaseCreationModal> {
    public static final String MODAL_TITLE = "Your data won’t be saved";

    @FindBy(xpath = ".//div[@class='modal-header__wrapper']")
    protected Element titleModal;

    @FindBy(xpath = ".//button[text()='Confirm']")
    @CaseInsensitiveXPath
    protected Element confirmButton;


    public ConfirmCancelOfTestCaseCreationModal(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[@aria-describedby='child-modal-description']"));
    }

    public void clickConfirm() {
        confirmButton.click();
    }

    public boolean isVisible() {
        return this.confirmButton.isVisible(3) && MODAL_TITLE.equalsIgnoreCase(this.getModalTitleText());
    }

    public String getTitleModal() {
        return titleModal.getText();
    }
}