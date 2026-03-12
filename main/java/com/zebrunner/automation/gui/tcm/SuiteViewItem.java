package com.zebrunner.automation.gui.tcm;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class SuiteViewItem extends AbstractUIObject {

    @FindBy(xpath = "//*[name()='svg' and @class='ZbrSelect-Icon']")
    private ExtendedWebElement icon;

    @FindBy(xpath = ".//p[contains(@class,'ItemTitle')]")
    private ExtendedWebElement title;

    @FindBy(xpath = ".//p[contains(@class,'ItemSubtitle')]")
    private ExtendedWebElement description;

    public SuiteViewItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getItemTitle() {
        return title.getText();
    }

    public String getItemDescription() {
        return description.getText();
    }

    public boolean isIconPresent() {
        return icon.isVisible(3);
    }
}
