package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.tcm.sharedstep.AddSharedStepModal;
import com.zebrunner.automation.gui.tcm.sharedstep.StepItem;
import com.zebrunner.automation.api.tcm.domain.SharedStepsBunch;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

public class EditStepsModal extends AbstractModal<EditStepsModal> {

    public static final String MODAL_NAME = "Edit steps";

    @FindBy(xpath = ".//div[@class='test-case-regular-step']")
    private List<StepItem> stepItems;

    @FindBy(xpath = ".//*[text()='Step']//parent::button")
    private ExtendedWebElement addStepButton;

    @FindBy(xpath = ".//*[text()='Shared step']//parent::button")
    private ExtendedWebElement addSharedStepButton;

    public EditStepsModal(WebDriver driver) {
        super(driver);
    }

    public StepItem findStepByAction(String action) {
        return stepItems.stream()
                .filter(step -> step.getAction().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Step with action " + action + " not found"));
    }

    public EditStepsModal addSharedStep(SharedStepsBunch step){
        addSharedStepButton.click();
        AddSharedStepModal addSharedStepModal = new AddSharedStepModal(getDriver());
        pause(3);

        addSharedStepModal.selectSharedStep(step.getName());
        addSharedStepModal.submitModal();
        return this;
    }

    public StepItem addSteps(List<TestCaseStep> stepsList, boolean isAddButtonShouldBeClickedForFirstStep) {
        for (int i = 0; i < stepsList.size(); i++) {
            if (i > 0 || isAddButtonShouldBeClickedForFirstStep) {
                addStepButton.click();
            }
            StepItem newItem = stepItems.get(stepItems.size() - 1);
            newItem.fillStep(stepsList.get(i));
        }
        return stepItems.get(stepItems.size() - 1);
    }
}
