package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class CardUpdateModalR extends AbstractModal {

    @FindBy(xpath = ".//button[text()='Confirm']")
    protected Element submitButton;

    public CardUpdateModalR(WebDriver driver) {
        super(driver);
    }

    public void submit() {
        submitButton.click();
    }
}