package com.zebrunner.automation.gui.tcm.repository;

import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class SelectSuiteListBoxMenu extends AbstractUIObject {

    @FindBy(xpath = SuiteSelectItem.ROOT_LOCATOR)
    private List<SuiteSelectItem> listBoxItems;

    public SelectSuiteListBoxMenu(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ListBoxMenu.ROOT_LOCATOR));
    }

    public List<SuiteSelectItem> getItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ListBoxMenu.ROOT_LOCATOR + "//li"), 0), 3);
        return listBoxItems;
    }


    public SuiteSelectItem findItem(String item) {
        log.info("Searching listBox item " + item);

        return getItems().stream()
                .filter(listBoxItem -> listBoxItem.getSuiteNameValue().equalsIgnoreCase(item))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("ListBox item '%s' was not found!", item)));

    }

    public SuiteSelectItem findItemWithNameAndPathContaining(String item, String pathContent) {
        log.info("Searching listBox item with suite name " + item + " and path containing " + pathContent);

        return getItems().stream()
                .filter(listBoxItem -> listBoxItem.getSuiteNameValue().equalsIgnoreCase(item) &&
                        listBoxItem.getSuitePathValue().contains(pathContent))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("ListBox item '%s' and path which contains '%s' was not found!", item, pathContent)));

    }

    public void clickItem(String item) {
        findItem(item)
                .click();
    }

    public void clickItem(String item, String pathContent) {
        findItemWithNameAndPathContaining(item, pathContent).click();
    }
}
