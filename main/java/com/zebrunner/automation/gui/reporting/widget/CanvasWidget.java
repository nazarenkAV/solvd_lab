package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CanvasWidget extends BaseWidget {
    @FindBy(xpath = ".//canvas")
    private ExtendedWebElement widgetBody;

    public CanvasWidget(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isWidgetVisible() {
        return widgetBody.isVisible(2);
    }
}
