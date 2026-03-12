package com.zebrunner.automation.gui.tcm.repository;

import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class SuiteSelectItem extends Element {
    public static final String ROOT_LOCATOR = ".//li[contains(@class,'suite-select__menu-item')]";
    public static final String NEW_TOP_LEVEL_SUITE = "New top-level suite";

    @FindBy(xpath = ".//p[@class='suite-select__render-value']")
    private Element suiteName;

    @FindBy(xpath = ".//span[@class='new-option__label']")
    private Element newOptionLabel;

    @FindBy(xpath = ".//span[@class='suite-select__suite-path']")
    private Element suitePath;

    public SuiteSelectItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getSuiteNameValue() {
        return suiteName.getText();
    }

    public String getSuitePathValue() {
        return suitePath.getText();
    }

    public String getNewOptionLabelValue() {
        return newOptionLabel.getText();
    }
}
