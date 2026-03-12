package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class WidgetCard extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[contains(@class,'selection-blocks') and @role='button']";
    @FindBy(xpath = ".//div[@class='selection-blocks__title']")
    private Element cardTitle;

    public WidgetCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void clickTitle() {
        cardTitle.click();
    }

    public String getTitle() {
        return cardTitle.getText();
    }
}
