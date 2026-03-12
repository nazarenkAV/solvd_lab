package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

//div[contains(@class,'MuiTablePagination-root')]
public class PaginationR extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String ROOT_XPATH= "//div[contains(@class,'MuiTablePagination-root')]";

    @FindBy(xpath = ".//*[@data-testid='LastPageIcon']")
    private ExtendedWebElement toLastPagePagination;

    @FindBy(xpath = ".//button[@aria-label='Go to next page']")
    private ExtendedWebElement toNextPagePagination;

    @FindBy(xpath = ".//button[@aria-label='Go to previous page']")
    private ExtendedWebElement toPreviousPagePagination;

    @FindBy(xpath = ".//button[@aria-label='Go to first page']")
    private ExtendedWebElement toFirstPagePagination;

    @FindBy(xpath = ".//p[@class='MuiTablePagination-displayedRows css-1chpzqh']")
    private ExtendedWebElement pages;

    @FindBy(xpath = ".//div[contains(@class, 'MuiTablePagination-input')]")
    private ExtendedWebElement paginationLimitMenu;

    @FindBy(xpath = "//li[@data-value='10']")
    private ExtendedWebElement paginationToTenItems;

    @FindBy(xpath = "//li[@data-value='25']")
    private ExtendedWebElement paginationToTwentyFiveItems;

    @FindBy(xpath = "//li[@data-value='50']")
    private ExtendedWebElement paginationToFiftyItems;

    @FindBy(xpath = "//li[@data-value='100']")
    private ExtendedWebElement paginationToOneHundredItems;

    public PaginationR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getPages() {
        String pagesStr = pages.getText().trim();
        LOGGER.info("Pagination is: " + pagesStr);
        return pagesStr;
    }

    public String getNumberOfItemsOnThePage() {
        String[] arr = getPages().split(" of ");
        return arr[1];
    }

    public boolean isFullPaginationPresent() {
        return toFirstPagePagination.isElementPresent() && toLastPagePagination.isElementPresent() && toNextPagePagination.isElementPresent() &&
                toPreviousPagePagination.isElementPresent() && pages.isElementPresent();
    }

    public boolean isPaginationPresent() {
        return paginationLimitMenu.isPresent(1);
    }

    public void clickToPaginationLimit() {
        paginationLimitMenu.click();
    }

    public void selectTwentyFiveItems() {
        clickToPaginationLimit();
        paginationToTwentyFiveItems.click();
    }

    public void selectTenItems() {
        clickToPaginationLimit();
        paginationToTenItems.click();
    }

    public boolean isPreviousPagePaginationButtonClickable() {
        return toPreviousPagePagination.isClickable();
    }

    public boolean isNextPagePaginationButtonClickable() {
        return toNextPagePagination.isClickable();
    }

    public void clickToPreviousPagePagination(){
        toPreviousPagePagination.click();
    }

    public void clickToNextPagePagination(){
        toNextPagePagination.click();
    }

    public void clickToLastPagePagination() {
        toLastPagePagination.click();
    }

    public void clickToFirstPagePagination() {
        toFirstPagePagination.click();
    }
}
