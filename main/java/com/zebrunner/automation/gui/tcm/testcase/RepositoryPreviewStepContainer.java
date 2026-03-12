package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class RepositoryPreviewStepContainer extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[@class='repository-preview-step__container']";

    @FindBy(xpath = ".//span[contains(@class,'counter')]")
    private Element counter;

    @FindBy(xpath = ".//*[text()='Action']//parent::div[@class='repository-preview-step__field']//p")
    private Element action;

    @FindBy(xpath = ".//*[text()='Expected result']//parent::div[@class='repository-preview-step__field']//p")
    private Element expectedResult;

    public RepositoryPreviewStepContainer(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getActionValue() {
        return action.getText();
    }

    public String getExpectedResultValue() {
        return expectedResult.getText();
    }

    public int getCounterValue() {
        return Integer.parseInt(counter.getText());
    }
}
