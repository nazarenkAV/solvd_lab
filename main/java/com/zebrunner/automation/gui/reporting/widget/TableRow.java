package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TableRow extends AbstractUIObject {
    @FindBy(xpath = ".//td[not(contains(@class,'ng-hide'))]")
    private List<ExtendedWebElement> elements;

    public TableRow(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }
}
