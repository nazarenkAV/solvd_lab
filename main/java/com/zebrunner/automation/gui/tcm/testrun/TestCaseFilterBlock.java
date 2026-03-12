package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.BaseFilters;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;

@Getter
public class TestCaseFilterBlock extends BaseFilters {

    @FindBy(xpath = "//span[text() = '%s']/parent::div[@class = 'add-filter-button__dropdown-option']/span[contains(@class , 'dropdown-option-type')]")
    private ExtendedWebElement filterType;

    public TestCaseFilterBlock(WebDriver driver) {
        super(driver);
    }

    public void selectFilterItem(TestCaseFilterBlock.TestCaseFiltersEnum filterName, String filterValue) {
        dropdown.findItem(filterName.getValue()).click();
        dropdown.findItem(filterValue).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectDeletedDate(Dropdown.DropdownItemsEnum dateFilter, LocalDate date) {
        dropdown.findItem(TestCaseFiltersEnum.DELETED.getValue()).click();
        dropdown.findItem(dateFilter.getItemValue()).click();
        calendarInput.click();
        calendar.selectDate(date);

        ComponentUtil.pressEscape(getDriver());
    }

    public TextFilterMenu openStepsToReproduce() {
        dropdown.findItem(TestCaseFiltersEnum.STEPS_TO_REPRODUCE.getValue()).click();
        return new TextFilterMenu(getDriver());
    }

    public String getFilterType(TestCaseFilterBlock.TestCaseFiltersEnum filterName) {
        return filterType.format(filterName.getValue()).getText();
    }

    public boolean isFilterTypeDisplayed(TestCaseFilterBlock.TestCaseFiltersEnum filterName) {
        return filterType.format(filterName.getValue()).isElementPresent(1);
    }

    @Getter
    @AllArgsConstructor
    public enum TestCaseFiltersEnum {
        AUTHOR("Author", "User"),
        AUTOMATION_STATE("Automation State", "Single-select"),
        CREATED("Created", "Date"),
        DEPRECATED("Deprecated", "Boolean"),
        DRAFT("Draft", "Boolean"),
        PRIORITY("Priority", "Single-select"),
        STEPS_TO_REPRODUCE("Steps to reproduce", "Steps"),
        UPDATED("Updated", "Date"),
        DELETED("Deleted", "Date"),
        DELETED_BY("Deleted by", "User");

        private final String value;
        private final String type;
    }

}
