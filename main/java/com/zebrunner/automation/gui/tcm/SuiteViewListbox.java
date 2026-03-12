package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class SuiteViewListbox extends AbstractUIObject {
    public static final String ROOT_LOCATOR = "//*[@role = 'listbox']";

    @FindBy(xpath = ".//li")
    private List<SuiteViewItem> listBoxItems;

    public SuiteViewListbox(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ListBoxMenu.ROOT_LOCATOR));
    }

    public List<SuiteViewItem> getListBoxItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROOT_LOCATOR + "//li"), 0), 3);
        return listBoxItems;
    }

    public SuiteViewItem findItem(SuiteViewTypes item) {
        return getOptionalItem(item)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("ListBox item '%s' was not found!", item.getViewType())));

    }

    public void clickItem(SuiteViewTypes item) {
        findItem(item).click();
    }

    public Optional<SuiteViewItem> getOptionalItem(SuiteViewTypes item) {
        log.info("Searching listBox item " + item);
        return getListBoxItems().stream()
                .filter(listBoxItem -> listBoxItem.getItemTitle().equalsIgnoreCase(item.getViewType()))
                .findFirst();
    }

    @Getter
    @AllArgsConstructor
    public enum SuiteViewTypes {
        TREE_VIEW("Tree view", "Browse all suites and cases in a tree view"),
        SUITE_VIEW("Suite view", "Browse cases belonging to selected suite only"),
        SUITE_WITH_SUB_SUITES("Suite with sub-suites view", "Browse cases belonging to selected suite and its child suites"),
        ;

        private final String viewType;
        private final String viewDescription;
    }
}
