package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.common.SelectWrapperMenu;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.CornerClickerUtil;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//div[@class='test-run-group-row test-details__header-actions _default']
@Getter
public class ActionsBlockR extends AbstractUIObject {

    @CaseInsensitiveXPath(text = true)
    @FindBy(xpath = ".//input[@type='checkbox']")
    private ExtendedWebElement checkbox;

    @FindBy(xpath = ".//div[@class='launch-details-actions__filters']//input[@placeholder = 'Search tests']")
    private ExtendedWebElement searchField;

    @FindBy(xpath = ".//*[text()='Group by']/ancestor::div[contains(@class,'select-wrapper')]")
    private Element groupBy;

    @FindBy(xpath = ".//*[text() = 'Labels']/parent::button")
    private Element sortByLabels;

    @FindBy(xpath = ".//*[text() = 'Execution order']/ancestor::div[contains(@class, 'select-wrapper')]")
    private Element executionOrder;

    @FindBy(xpath = ".//*[text() = 'Status']/parent::button")
    private Element status;

    @FindBy(xpath = ".//button[text()='Reset']")
    private Element resetFiltersButton;

    @FindBy(xpath = ".//*[text()='Test case state']//ancestor::button")
    private Element testCaseState;

    @FindBy(xpath = ".//*[text()='Labels']//ancestor::button")
    private Element labels;

    @FindBy(xpath = ".//*[text() = 'Failure tag']/parent::button")
    private Element failureTagButton;

    @FindBy(xpath = "//div[@role='presentation']")
    private ExtendedWebElement clickCatcher;

    @FindBy(xpath = "//*[@value='reset']")
    private ExtendedWebElement clearSelectionButton;

    @FindBy(xpath = PaginationR.ROOT_XPATH)
    private PaginationR pagination;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown dropdown;

    @FindBy(xpath = SelectWrapperMenu.ROOT_LOCATOR)
    private SelectWrapperMenu selectWrapperMenu;

    @FindBy(xpath = Menu.ROOT_LOCATOR)
    private Menu menu;

    @FindBy(xpath = "//*[contains(@class, 'bulk-actions')]")
    private BulkActionSection bulkActionSection;

    public ActionsBlockR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isCheckboxPresent() {
        return checkbox.isPresent();
    }

    public boolean isSearchFieldPresent() {
        return isElementPresent(searchField);
    }

    public boolean isGroupByPresent() {
        return groupBy.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public boolean isSortByLabels() {
        return sortByLabels.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public boolean isResetButtonPresent() {
        return resetFiltersButton.isStateMatches(Condition.PRESENT);
    }

    private boolean isElementPresent(ExtendedWebElement element) {
        return element.isVisible(2) && element.isClickable(2);
    }

    public void filterByFailed() {
        dropdown.findItem(Dropdown.DropdownItemsEnum.STATUS_FAILED.getItemValue()).click();
        ComponentUtil.closeAnyMenuOrModal(getDriver());
        pause(1);

    }

    public ActionsBlockR openFilteringByStatus() {
        status.click();
        return this;
    }

    public void openStatusSettings() {
        status.click();
    }

    public void clickResetButton() {
        resetFiltersButton.click();
    }

    public void clickCheckbox() {
        checkbox.click();
    }

    public boolean isCheckboxSelected() {
        return checkbox.isChecked();
    }

    public void hideDropdownMenu() {
        if (clickCatcher.isPresent()) {
            CornerClickerUtil.clickToCorner(true, true, getDriver(), clickCatcher);
        }
    }

    private SelectWrapperMenu openGroupBySetting() {
        groupBy.click();
        return selectWrapperMenu;
    }

    private SelectWrapperMenu openSortingSettings() {
        executionOrder.click();
        return selectWrapperMenu;
    }

    private Menu openFailureTagSettings() {
        failureTagButton.click();
        return menu;
    }

    private Dropdown openTestCaseStateSettings() {
        testCaseState.click();
        return dropdown;
    }

    private Dropdown openLabelSettings() {
        labels.click();
        return dropdown;
    }

    public void openSelectFailureTagAndClose(Menu.MenuItemEnum menuItemEnum) {
        openFailureTagSettings()
                .findItem(menuItemEnum.getItemValue())
                .click();
        closeSelectBox();
    }

    public void openAndSelectLabel(String label) {
        openLabelSettings()
                .findItem(label)
                .click();
        closeSelectBox();
    }

    public void openAndSelectTestCaseState(Dropdown.DropdownItemsEnum testCaseState) {
        openTestCaseStateSettings()
                .findItem(testCaseState.getItemValue())
                .click();
        closeSelectBox();
    }

    public void openAndSelectGroup(SelectWrapperMenu.WrapperItemEnum wrapperItemEnum) {
        openGroupBySetting()
                .findItem(wrapperItemEnum.getItemValue())
                .click();
    }

    public void selectSingleStatusAndClose(Dropdown.DropdownItemsEnum status) {
        openStatusSettings();
        selectStatus(status);
        closeSelectBox();
    }

    public void openAndSelectSort(SelectWrapperMenu.WrapperItemEnum wrapperItemEnum) {
        openSortingSettings()
                .findItem(wrapperItemEnum.getItemValue())
                .click();
    }

    public void selectStatus(Dropdown.DropdownItemsEnum dropdownItemsEnum) {
        dropdown.findItem(dropdownItemsEnum.getItemValue())
                .click();
    }

    public boolean isSearchTextFieldClean() {
        return searchField.getAttribute("value").equals("");
    }

    public boolean isFailureTagSelected(Menu.MenuItemEnum menuItemEnum) {
        return failureTagButton.getText().contains(menuItemEnum.getItemCapitalCase());
    }

    public boolean isGroupSelected(SelectWrapperMenu.WrapperItemEnum wrapperItemEnum) {
        return groupBy.getText().split("\n")[0].equals(
                wrapperItemEnum.getItemValue());
    }

    public boolean isStatusSelected(Dropdown.DropdownItemsEnum dropdownItemsEnum) {
        return status.getText().contains(dropdownItemsEnum.getItemValue());
    }

    public boolean isSortSelected(SelectWrapperMenu.WrapperItemEnum wrapperItemEnum) {
        return executionOrder.getText().equals(wrapperItemEnum.getItemValue());
    }

    public void closeSelectBox() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
    }

    public void searchTest(String testName) {
        searchField.type(testName);
        pause(1);
    }

    public void clickClearSelectionButton() {
        clearSelectionButton.click();
        PageUtil.guaranteedToHideDropDownList(getDriver());
    }

    public boolean isBulkActionSectionPresent() {
        return bulkActionSection.getRootExtendedElement().isVisible(3);
    }
}