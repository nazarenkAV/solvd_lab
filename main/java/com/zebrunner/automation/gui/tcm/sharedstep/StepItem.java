package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.gui.tcm.WysiwygInputContainer;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class StepItem extends AbstractUIObject {

    @FindBy(xpath = ".//span[text()='Action']" + WysiwygInputContainer.PARENT_ROOT_LOCATOR)
    private WysiwygInputContainer actionInput;

    @FindBy(xpath = ".//span[text()='Expected result']" + WysiwygInputContainer.PARENT_ROOT_LOCATOR)
    private WysiwygInputContainer expectedResultInput;

    @FindBy(xpath = ".//button[@aria-label='Max size is 1000 mb']")
    private ExtendedWebElement imageButton;

    @FindBy(xpath = ".//button[@aria-label='Delete']")
    private ExtendedWebElement deleteButton;

    public StepItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public StepItem fillStep(TestCaseStep step) {
        actionInput.input(step.getAction());
        expectedResultInput.input(step.getExpectedResult());
        return this;
    }

    public String getAction() {
        return actionInput.getInputValue();
    }

    public String getExpectedResult() {
        return expectedResultInput.getInputValue();
    }

}
