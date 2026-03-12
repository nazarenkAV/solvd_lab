package com.zebrunner.automation.gui.tcm.testrun;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import com.zebrunner.automation.gui.common.BaseFilters;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;

@Getter
public class TestRunsFilterBlock extends BaseFilters {

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[contains(text(), '%s')]")
    private ExtendedWebElement filterItem;

    public TestRunsFilterBlock(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void selectFilterItem(TestRunsFiltersEnum filterName, String filterValue) {
        format(filterItem, filterName).click();
        menu.findItem(filterValue).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectMilestoneFilter(String milestoneName) {
        format(filterItem, TestRunsFiltersEnum.MILESTONE).click();
        menu.findItemDirectlyByName(milestoneName).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public void selectConfigurationFilter(String configurationName, String optionName) {
        format(filterItem, TestRunsFiltersEnum.CONFIGURATION).click();
        menu.findItem(configurationName).click();

        menu.findItem(optionName).click();
        ComponentUtil.pressEscape(getDriver());
    }

    public boolean isFilterButtonPresent(TestRunsFiltersEnum filterName) {
        return format(filterItem, filterName).isPresent(2);
    }

    public boolean isConfigurationAppearInList(String configName) {
        format(filterItem, TestRunsFiltersEnum.CONFIGURATION).click();
        List<Element> appearedMenuOptions = menu.getMenuItem();

        return appearedMenuOptions.stream()
                .map(Element::getText)
                .anyMatch(option -> option.equals(configName));
    }

    @Getter
    @AllArgsConstructor
    public enum TestRunsFiltersEnum {
        MILESTONE("Milestone"),
        ENVIRONMENT("Environment"),
        CREATED("Created"),
        CONFIGURATION("Configuration");


        private final String itemValue;
    }
}
