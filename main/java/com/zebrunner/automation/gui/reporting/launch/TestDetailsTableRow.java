package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestDetailsTableRow extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[contains(@class, 'test-details__table-accordion')]";
    @FindBy(xpath = ROOT_XPATH)
    private ExtendedWebElement testDetailsTable;

    @FindBy(xpath = ".//*[@class = 'test-details__group-name']")
    private ExtendedWebElement tableName;

    @FindBy(xpath = ".//*[@class = 'test-details__group-count']")
    private ExtendedWebElement testCount;

    public TestDetailsTableRow(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTableNameText() {
        return tableName.getText();
    }

    public void clickOnTable() {
        tableName.click();
    }

    public String getTestCountText() {
        return testCount.getText();
    }
}