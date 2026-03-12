package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

//div[@class='saved-searches']
public class FilterSemiWindow extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//input[@placeholder='Search items']")
    private ExtendedWebElement searchFilterField;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//input[@id='Name of filter to save']")
    private ExtendedWebElement filterNameInput;

    @FindBy(xpath = ".//div[@class='saved-searches__list-body']/div[@class='saved-searches__list-row']")
    private List<FilterCard> filterCardList;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Save']")
    private ExtendedWebElement saveButton;

    @FindBy(xpath = ".//div[@class='close-button']")
    private ExtendedWebElement closeSemiWindow;

    public FilterSemiWindow(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isFavouritedFilterFirstInList(String filterName) {
        pause(2);
        return filterCardList.get(0).getFilterName().equalsIgnoreCase(filterName);
    }

    public boolean isSearchFieldEmpty() {
        return searchFilterField.getText().isEmpty();
    }

    public void saveFilter(String name) {
        waitUntil(ExpectedConditions.elementToBeClickable(filterNameInput.getElement()), 2);
        filterNameInput.type(name, 2);
        waitUntil(ExpectedConditions.elementToBeClickable(saveButton.getElement()), 2);
        saveButton.click();
    }

    public boolean isCloseButtonPresent() {
        return closeSemiWindow.isElementPresent(7);
    }

    public void closeSemiWindow() {
        WaitUtil.waitComponentByCondition(closeSemiWindow, ExtendedWebElement::isClickable);
        closeSemiWindow.click();
    }

    public boolean isFilterPresentInList(String filterName) {
        LOGGER.info("Checking is filter present...");
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(filterCardList,
                filterCard -> filterCard.getFilterName().equalsIgnoreCase(filterName));
    }

    public FilterCard getFilterCard(String filterName) {
        LOGGER.info("Waiting for filters to load...");
        return WaitUtil.waitElementAppearedInListByCondition(filterCardList,
                filterCard -> filterCard.getFilterName().equalsIgnoreCase(filterName),
                "Filter with name " + filterName + " was found",
                "Filter with name " + filterName + " was not found");
    }

    public FilterCard searchFilter(String name) {
        LOGGER.info("Waiting for filters to load...");
        pause(3);
        searchFilterField.type(name);
        WaitUtil.waitCheckListIsNotEmpty(filterCardList);
        return filterCardList.get(0);
    }

    public List<FilterCard> getFilterCardList() {
        pause(2);
        return filterCardList;
    }

    public long getFilterCardNameCount(String filterName) {
        return getFilterCardList().stream()
                .filter(filterCard -> filterCard.getFilterName().equalsIgnoreCase(filterName)).count();
    }

}
