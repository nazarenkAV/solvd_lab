package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Tooltip extends AbstractUIObject {
    public static final String ROOT_LOCATOR = "//div[@role='tooltip']";

    @FindBy(xpath = ".")
    private Element tooltip;

    public Tooltip(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Tooltip(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_LOCATOR));
    }

    public String getTooltipText() {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(this.getRootExtendedElement().getBy()), 5);
        return tooltip.getText();
    }

    public String getTextFromTooltipDirectly() {
        return tooltip.getText();
    }
}
