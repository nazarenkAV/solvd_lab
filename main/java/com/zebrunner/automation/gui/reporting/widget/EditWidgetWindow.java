package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EditWidgetWindow extends AbstractModal<EditWidgetWindow> {
    public static final String TITLE_PAGE_1 = "New widget: choose template (1/3)";
    public static final String TITLE_PAGE_2 = "New widget: set parameters (2/3)";
    @FindBy(xpath = ".//input[@id='name']")
    private Element nameField;

    @FindBy(xpath = ".//textarea[@id='description']")
    private Element description;

    public EditWidgetWindow(WebDriver driver) {
        super(driver);
    }

    public EditWidgetWindow typeName(String widgetName) {
        waitUntil(ExpectedConditions.visibilityOf(nameField.getElement()), 3);
        nameField.sendKeys(widgetName);
        return this;
    }

    public EditWidgetWindow typeDescription(String widgetDescription) {
        waitUntil(ExpectedConditions.visibilityOf(description.getElement()), 3);
        description.sendKeys(widgetDescription);
        return this;
    }
}
