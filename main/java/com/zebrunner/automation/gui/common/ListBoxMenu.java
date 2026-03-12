package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class ListBoxMenu extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//*[@role = 'listbox']";

    @FindBy(xpath = ".//li")
    private List<Element> listBoxItems;

    public ListBoxMenu(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_LOCATOR));
    }

    public List<Element> getItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROOT_LOCATOR + "//li"), 0), 3);
        return listBoxItems;
    }

    public Element findItem(String item) {
        log.info("Searching listBox item " + item);
        return getItems().stream()
                .filter(listBoxItem -> listBoxItem.getText().equalsIgnoreCase(item))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("ListBox item '%s' was not found!", item)));

    }

    public void clickItem(String item) {
        findItem(item).click();
    }

    public boolean isItemPresentInList(String item) {
        return findItem(item).isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public int getCount() {
        return listBoxItems.size();
    }
}