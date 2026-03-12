package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
@Getter
public class AbortModal extends AbstractModal {

    @FindBy(xpath = ".//button[text()='Abort']")
    private Element abortButton;

    public AbortModal(WebDriver driver) {
        super(driver);
    }

    public void clickAbortModalButton(){
        abortButton.click();
    }
}
