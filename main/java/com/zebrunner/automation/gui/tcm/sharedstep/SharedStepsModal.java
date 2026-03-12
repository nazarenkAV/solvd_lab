package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;


public class SharedStepsModal extends AbstractModal<SharedStepsModal> {
    public static final String MODAL_NAME = "Create shared step";

    @FindBy(xpath = ".//input[@id='name']")
    private ExtendedWebElement titleInput;

    @FindBy(xpath = ".//div[@class='test-case-regular-step']")
    private List<StepItem> sharedStepItems;

    @FindBy(xpath = ".//button[text()='Save' or text()='Create']")
    protected Element submitButton;

    @FindBy(xpath = ".//button[contains(@class, 'tertiary')]")
    protected Element close;

    @FindBy(xpath = ".//button[contains(@class, 'shared-steps__add-button')]")
    private Element addNewStepButton;

    public SharedStepsModal(WebDriver driver) {
        super(driver);
    }

    public boolean isModalOpened(){
        return getRootExtendedElement().isPresent(5);
    }

    public void close(){
        close.click();
    }

    public boolean isCreateButtonPresent() {
        return addNewStepButton.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public boolean isSubmitButtonPresent() {
        return submitButton.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public void typeTitle(String title){
        titleInput.type(title);
    }

    public void submit(){
        submitButton.click();
    }

    public StepItem addSteps(List<TestCaseStep> stepsList, boolean isAddButtonShouldBeClickedForFirstStep) {
        for (int i = 0; i < stepsList.size(); i++) {
            if (i > 0 || isAddButtonShouldBeClickedForFirstStep) {
                addNewStepButton.click();
            }
            StepItem newItem = sharedStepItems.get(sharedStepItems.size() - 1);
            newItem.fillStep(stepsList.get(i));
        }
        return sharedStepItems.get(sharedStepItems.size() - 1);
    }
}
