package com.zebrunner.automation.gui.tcm.testcase;

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
import java.util.Optional;

@Slf4j
public class TabsWrapper extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[@class='test-cases-preview-tabs-wrapper']";

    @FindBy(xpath = TabItem.ROOT_XPATH)
    private List<TabItem> tabs;

    public TabsWrapper(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<TabItem> getTab(Tabs tabName) {
        return getTabs().stream().filter(t -> t.getTabName()
                        .equalsIgnoreCase(tabName.getTabName()))
                .findFirst();

    }

    public TabsWrapper clickTab(Tabs tabName) {
        log.info("Navigating to tab " + tabName);
        getTab(tabName)
                .orElseThrow(() -> new NoSuchElementException("Value is not present with text " + tabName.getTabName()))
                .click();
        return this;
    }

    public List<TabItem> getTabs() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(TabItem.ROOT_XPATH), 0), 7);
        return tabs;
    }

    @Getter
    @AllArgsConstructor
    public enum Tabs {
        GENERAL("General"),
        PROPERTIES("Properties"),
        ATTACHMENTS("Attachments"),
        EXECUTIONS("Executions"),
        CHANGE_LOG("Change log"),
        ;
        private final String tabName;

    }
}
