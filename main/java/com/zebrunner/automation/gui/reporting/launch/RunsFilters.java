package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.Calendar;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.FilterSemiWindow;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class RunsFilters extends AbstractUIObject {

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(),'Browser')]")
    private Element browserFilterButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(),'Platform')]")
    private Element platformFilterButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(),'Environment')]")
    private Element environmentFilterButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(),'Locale')]")
    private Element localeFilterButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(),'Date')]")
    private Element dateFilterButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(),'Reviewed')]")
    private Element reviewedFilterButton;

    @FindBy(xpath = ".//button[contains(@class,'launch-filters__add-filter-button')]")
    private Element addFilterButton;

    /**
     * List of visible filter options.
     */
    @FindBy(xpath = "//div[@class='dropdown__items']//li")
    private List<ExtendedWebElement> filterOptionsList;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='RESET']")
    private Element resetButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='SAVE']")
    private Element saveButton;

    @FindBy(xpath = ".//button[contains(@class,'launches-filter__active-filter')]")
    private Element activeFilter;

    @FindBy(xpath = "//div[@role='presentation' and not (@aria-hidden)]")
    private FilterSemiWindow semiWindow;

    @FindBy(xpath = "//*[@d='" + SvgPaths.CALENDAR + "']")
    private ExtendedWebElement calendarLogo;

    @FindBy(xpath = "//*[contains(@class, 'MuiInputAdornment-outlined')]//*[local-name()='svg' and @data-testid='CalendarIcon']")
    private ExtendedWebElement calendarLogoForBetween;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Saved filters']")
    private Element showAllSavedFilters;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown dropdown;

    @FindBy(xpath = Calendar.ROOT_LOCATOR_FOR_FILTERS)
    private Calendar calendar;

    @FindBy(xpath = ExtendedCalendar.ROOT_LOCATOR)
    private ExtendedCalendar extendedCalendar;

    public RunsFilters(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isAddFilterButtonPresent() {
        return addFilterButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isResetButtonPresent() {
        return resetButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isSaveButtonPresent() {
        return saveButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public void selectPlatform(String platform) {
        platformFilterButton.waitUntil(Condition.VISIBLE).click();
        getFilterParameterBy(platform).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void clickEnvironmentFilterButton() {
        environmentFilterButton.click();
    }

    public void selectEnvItem(String env) {
        environmentFilterButton.click();
        dropdown.findItem(env).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectEnv(String env) {
        addFilterButton.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.ENVIRONMENT.getItemValue()).click();
        ComponentUtil.pressEscape(getDriver());
        selectEnvItem(env);
    }

    public void selectReviewType(String review) {
        reviewedFilterButton.click();
        dropdown.findItem(review).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectReview(String review) {
        addFilterButton.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.REVIEWED.getItemValue()).click();
        ComponentUtil.pressEscape(getDriver());
        selectReviewType(review);
    }

    public void inputDate(LocalDate localDate) {
        calendarLogo.click();
        calendar.selectDate(localDate);
    }

    public void selectDateType(Dropdown.DropdownItemsEnum dateType) {
        dateFilterButton.waitUntil(Condition.CLICKABLE);
        dateFilterButton.click();
        dropdown.findItem(dateType.getItemValue()).click();
    }

    public void selectAndTypeDate(Dropdown.DropdownItemsEnum dateType, LocalDate localDate) {
        selectDateType(dateType);
        inputDate(localDate);
        pause(1);
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectBetweenDateType(LocalDate startDate, LocalDate endDate) {
        selectDateType(Dropdown.DropdownItemsEnum.BETWEEN);
        calendarLogoForBetween.click();
        extendedCalendar.selectBetweenDate(startDate, endDate);
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectLaunchesDate() {
        addFilterButton.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.LAUNCH_DATE.getItemValue()).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public FilterSemiWindow clickSaveFilter() {
        saveButton.waitUntil(Condition.VISIBLE).click();
        return semiWindow;
    }

    public void selectLocale() {
        addFilterButton.click();
        dropdown.findItem(Dropdown.DropdownItemsEnum.LOCALE.getItemValue()).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectLocaleType(Locale locale) {
        localeFilterButton.waitUntil(Condition.CLICKABLE);
        localeFilterButton.click();
        dropdown.findItem(locale.toString()).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void clickResetFilter() {
        resetButton.waitUntil(Condition.VISIBLE).click();
    }

    public boolean isActiveFilterNameVisible() {
        return activeFilter.isStateMatches(Condition.VISIBLE);
    }

    public FilterSemiWindow toSavedFilters() {
        showAllSavedFilters.click();
        return semiWindow;
    }

    /**
     * Searching filter parameter by parameter's name
     */
    public ExtendedWebElement getFilterParameterBy(String parameterName) {
        return WaitUtil.waitElementAppearedInListByCondition(
                filterOptionsList,
                filterParameter -> filterParameter.getText().trim().equalsIgnoreCase(parameterName),
                "Filter parameter with name " + parameterName + " was found",
                "Filter parameter with name " + " was not found"
        );
    }

    public void selectBrowser(String browser) {
        browserFilterButton.click();
        getFilterParameterBy(browser).click();
        ComponentUtil.pressEscape(getDriver());
    }

}
