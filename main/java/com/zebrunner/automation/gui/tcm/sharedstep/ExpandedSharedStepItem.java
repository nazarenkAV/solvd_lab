package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import static java.lang.Integer.parseInt;

public class ExpandedSharedStepItem extends AbstractUIObject {
    public static final String ROOT_XPATH = ".//div[@class='shared-step__step']";

    @FindBy(xpath = ".//span[text()='Action']/following-sibling::div[@class='shared-step__step-content']//p")
    private Element action;

    @FindBy(xpath = ".//span[text()='Expected result']/following-sibling::div[@class='shared-step__step-content']//p")
    private Element expectedResult;

    @FindBy(xpath = ".//span[@class='shared-step__step-count']")
    private Element count;

    public ExpandedSharedStepItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getActionText() {
        return action.getText();
    }

    public String getExpectedResultText() {
        return expectedResult.getText();
    }

    public Integer getCount() {
        return parseInt(count.getText());
    }
}
