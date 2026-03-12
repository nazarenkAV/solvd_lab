package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.gui.common.ZbrAutocompleteInput;
import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class AddSharedStepModal extends AbstractModal<AddSharedStepModal> {
    public static final String MODAL_NAME = "Add shared step";

    @FindBy(xpath = ".//*[text()='Source *']" + ZbrAutocompleteInput.PARENT_ROOT_XPATH)
    protected ZbrAutocompleteInput sourceInput;

    public AddSharedStepModal(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//*[text()='Add shared step']//ancestor::div[@class='styled-modal']"));
    }

    public AddSharedStepModal selectSharedStep(String sharedStepName) {
        sourceInput.selectValue(sharedStepName);
        return this;
    }
}
