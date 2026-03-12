package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class CardMenuR extends AbstractUIObject {

    @FindBy(xpath = ".//span[text()='Mark as Passed']")
    private Element markAsPassed;

    @FindBy(xpath = ".//span[text()='Mark as Failed']")
    private Element markAsFailed;

    @FindBy(xpath = ".//span[text()='Link issue']")
    private Element linkIssue;

    public CardMenuR(WebDriver driver) {
        super(driver);
    }

    public CardMenuR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

}