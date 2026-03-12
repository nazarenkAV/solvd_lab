package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SharedStepsCard extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[@class='shared-steps-list-container']/div";

    @FindBy(xpath = ".//span[@class='shared-step__name']/span")
    private ExtendedWebElement cardTitle;

    @FindBy(xpath = ".//div[@aria-label='Edit']")
    private ExtendedWebElement editButton;

    @FindBy(xpath = ".//div[@aria-label='Delete']")
    private ExtendedWebElement deleteButton;

    @FindBy(xpath = ".//span[@class='shared-step__counter shared-step__counter--steps']")
    private Element stepsCount;

    @FindBy(xpath = ".//span[@class='shared-step__counter']")
    private Element attachmentCount;

    @FindBy(xpath = ExpandedSharedStepItem.ROOT_XPATH)
    private List<ExpandedSharedStepItem> expandedSharedSteps;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public SharedStepsCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isExpanded(){
        return this.getRootExtendedElement().getAttribute("class").contains("Mui-expanded");
    }

    public String cardTitle() {
        return cardTitle.getText();
    }

    public ExpandedSharedStepItem clickTitle() {
        cardTitle.click();
        return new ExpandedSharedStepItem(getDriver(), getDriver());
    }

    public DeleteSharedStepsModal delete() {
        cardTitle.hover();
        deleteButton.click();
        return new DeleteSharedStepsModal(getDriver());
    }

    public SharedStepsModal edit() {
        cardTitle.hover();
        editButton.click();
        return new SharedStepsModal(getDriver());
    }

    public boolean isEditButtonClickable(){
        cardTitle.hover();
        return editButton.isClickable();
    }

    public boolean isDeleteButtonClickable(){
        cardTitle.hover();
        return deleteButton.isClickable();
    }

    public String getNameTooltip(){
        cardTitle.hover();
        String text = tooltip.getTooltipText();
        stepsCount.hover();//remove tooltip visibility by hovering over another element
        return text;
    }

    public String getEditButtonTooltip(){
        editButton.hover();
        String text = tooltip.getTooltipText();
        stepsCount.hover();//remove tooltip visibility by hovering over another element
        return text;
    }

    public String getDeleteButtonTooltip(){
        deleteButton.hover();
        String text = tooltip.getTooltipText();
        stepsCount.hover();//remove tooltip visibility by hovering over another element
        return text;
    }

    public int getStepsCount() {
        String numberOnly = stepsCount.getText().replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);
    }

    public int getAttachmentCount() {
        String numberOnly = attachmentCount.getText().replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);
    }

    public ExpandedSharedStepItem expandSharedStep() {
        if (!isExpanded()) {
            clickTitle();
        }
        pause(1);
        return new ExpandedSharedStepItem(getDriver(), getDriver());
    }

    public List<TestCaseStep> getSteps() {
        List<TestCaseStep> steps = new ArrayList<>();
        for (ExpandedSharedStepItem item : expandedSharedSteps) {
            TestCaseStep step = new TestCaseStep(item.getActionText(), item.getExpectedResultText());
            step.setRelativePosition(item.getCount());
            steps.add(step);
        }
        return steps;
    }
}
