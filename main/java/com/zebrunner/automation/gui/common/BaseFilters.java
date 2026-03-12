package com.zebrunner.automation.gui.common;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public abstract class BaseFilters extends AbstractUIObject {

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    protected Dropdown dropdown;

    @FindBy(xpath = Calendar.ROOT_LOCATOR_FOR_FILTERS)
    protected Calendar calendar;

    @FindBy(xpath = Menu.ROOT_LOCATOR)
    protected Menu menu;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='RESET']")
    protected Element resetButton;

    @FindBy(xpath = "//*[@class = 'dropdown__date-input-wrapper']")
    protected ExtendedWebElement calendarInput;

    @FindBy(xpath = "//*[contains(@class, 'dropdown__search')]//input[@type = 'search']")
    private ExtendedWebElement dropDownSearchTextField;

    public BaseFilters(WebDriver driver) {
        super(driver);
    }

    public BaseFilters(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void searchDropDownItem(String dropdownItem) {
        dropDownSearchTextField.type(dropdownItem);
    }

    public void clickResetFilter() {
        resetButton.waitUntil(Condition.VISIBLE).click();
    }

}
