package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.BaseFilters;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.NoSuchElementException;

@Slf4j
public class TestRunPageFiltersBlock extends BaseFilters {

    @FindBy(xpath = ".//*[contains(text(), '%s')]")
    private ExtendedWebElement filterItem;

    public TestRunPageFiltersBlock(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void selectFilterItem(TestRunFilterEnum filterName, String filterValue) {
        format(filterItem, filterName.getItemValue()).click();
        menu.findItem(filterValue).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectDeprecatedFilterItem(String filterValue) {
        format(filterItem, TestRunFilterEnum.DEPRECATED.getItemValue()).click();

        String itemXPath = Menu.ROOT_LOCATOR + String.format("//li//span[text()='%s']", filterValue);
        WebElement menuItem = getDriver().findElement(By.xpath(itemXPath));
        if (menuItem == null) {
            throw new NoSuchElementException(String.format("Menu item '%s' was not found!", filterValue));
        }
        menuItem.click();

        ComponentUtil.pressEscape(getDriver());
    }

    @Getter
    @AllArgsConstructor
    public enum TestRunFilterEnum {
        RESULT("Result"),
        ASSIGNEE("Assignee"),
        AUTHOR("Author"),
        AUTOMATION_STATE("Automation State"),
        CREATED("Created"),
        DEPRECATED("Deprecated"),
        DRAFT("Draft"),
        PRIORITY("Priority"),
        STEPS_TO_REPRODUCE("Steps to reproduce"),
        UPDATED("Updated");

        private final String itemValue;
    }
}
