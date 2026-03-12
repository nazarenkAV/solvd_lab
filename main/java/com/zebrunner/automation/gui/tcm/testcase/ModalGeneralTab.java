package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.tcm.TabWysiwygContainer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ModalGeneralTab extends AbstractGeneralTab<ModalGeneralTab> {

    public static final String ROOT_XPATH = ".//div[@role='tabpanel' and contains(@class,'modal') and contains(@id,'General')]";

    @FindBy(xpath = ".//div[text()='Pre-conditions']" + TabWysiwygContainer.ROOT_XPATH)
    private TabWysiwygContainer preconditionsInput;

    @FindBy(xpath = ".//div[text()='Post-conditions']" + TabWysiwygContainer.ROOT_XPATH)
    private TabWysiwygContainer postConditionsInput;

    @FindBy(xpath = RepositoryPreviewStepContainer.ROOT_XPATH)
    protected List<RepositoryPreviewStepContainer> steps;


    public ModalGeneralTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public ModalGeneralTab inputAndSavePreconditions(String preconditions) {
        preconditionsInput
                .input(preconditions)
                .clickSaveButton();
        return this;
    }

    public String getPreconditions(){
        return preconditionsInput.getContentValue();
    }

    public String getPostConditions(){
        return postConditionsInput.getContentValue();
    }

}
