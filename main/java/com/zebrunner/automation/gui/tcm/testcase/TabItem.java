package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TabItem extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//*[contains(@class,'test-cases-preview-tab') and not (contains(@class,'MuiTabs-root'))]";

    @FindBy(xpath = ".//*[@class='tab-label']")
    public Element name;

    public TabItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTabName() {
        return name.getText();
    }

    public boolean isTabSelected() {
        return Boolean.parseBoolean(this.getAttribute("aria-selected"));
    }
}
