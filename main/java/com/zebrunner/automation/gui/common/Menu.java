package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class Menu extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//*[@role = 'menu']";

    @FindBy(xpath = ".//li")
    private List<Element> menuItem;

    @FindBy(xpath = ".//*[@class='name']")
    private List<Element> menuNameItems;

    public Menu(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Menu(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_LOCATOR));
    }

    public List<Element> getMenuItem() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROOT_LOCATOR + "//li"), 0), 3);
        return menuItem;
    }

    public List<Element> getMenuItemsDirectlyByName() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROOT_LOCATOR + "//span[@class='name']"), 0), 3);
        return menuNameItems;
    }

    public Element findItem(String item) {
        log.info("Searching menu item " + item);
        return getMenuItem().stream()
                .filter(menuItem -> menuItem.getText().equalsIgnoreCase(item))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Menu item '%s' was not found!", item)));

    }

    public Element findItemDirectlyByName(String itemName) {
        log.info("Searching menu item " + itemName);
        return getMenuItemsDirectlyByName().stream()
                .filter(menuItem -> menuItem.getText().equalsIgnoreCase(itemName))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Menu item with name '%s' was not found!", itemName)));
    }

    @Getter
    @AllArgsConstructor
    public enum MenuItemEnum {
        // --- Bug Trucker -- //
        BUG_TUCKER_JIRA("Jira"),
        BUG_TUCKER_GITHUB("github"),

        // -- Failure tag -- //
        UNCATEGORIZED("UNCATEGORIZED"),

        // -- Launch -- //
        RELAUNCH("Relaunch"),
        GO_TO_LAUNCHER("Go to launcher");

        private final String itemValue;

        public String getItemCapitalCase() {
            String[] words = itemValue.split(" ");
            StringBuilder formattedValue = new StringBuilder();

            for (String word : words) {
                if (formattedValue.length() > 0) {
                    formattedValue.append(" ");
                }
                formattedValue.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }

            return formattedValue.toString();
        }
    }
}
