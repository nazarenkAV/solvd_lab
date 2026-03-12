package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CaseStep extends AbstractUIObject {
    @FindBy(xpath = ".//*[@class='tcm-label-preview__step-content _expected']")
    private Element action;

    @FindBy(xpath = ".//*[@class='tcm-label-preview__step-content _result']")
    private Element expectedResult;

    public CaseStep(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getActionText() {
        return action.getText();
    }

    public String getExpectedResultText() {
        return expectedResult.getText();
    }
}
