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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Deprecated
public class SelectWrapperMenu extends AbstractUIObject {
    public static final String ROOT_LOCATOR = "//div[contains(@class,'select-wrapper__menu')]";

    @FindBy(xpath = ".//li")
    private List<Element> wrapperItems;

    public SelectWrapperMenu(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public List<Element> getWrapperItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROOT_LOCATOR + "//li"), 0), 3);
        return wrapperItems;
    }

    public Element findItem(String item) {
        log.info("Searching wrapper item " + item);
        return getWrapperItems().stream()
                .filter(wrapperItem -> wrapperItem.getText().equalsIgnoreCase(item))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Wrapper item '%s' was not found!", item)));

    }

    @Getter
    @AllArgsConstructor
    public enum WrapperItemEnum {
        DEFAULT_GROUP("Default (none)"),
        GROUP_FILE("File"),
        GROUP_DIRECTORY("Directory"),
        GROUP_LABEL("Label"),
        GROUP_FAILURE("Failure"),
        GROUP_FAILURE_TAG("Failure tag"),
        GROUP_MAINTAINER("Maintainer"),
        CUSTOM_TEST_GROUP("Test group"),

        DEFAULT_SORT("Execution order"),
        SORT_STATUS("Status"),
        SORT_FAILURE_TAG("Failure tag"),
        SORT_FASTEST_FIRST("Fastest first"),
        SORT_SLOWEST_FIRST("Slowest first"),
        SORT_NAME("Name");

        private final String itemValue;

        public static SelectWrapperMenu.WrapperItemEnum[] getGroups() {
            return Arrays.stream(WrapperItemEnum.values())
                    .filter(enumValue -> enumValue.name().startsWith("GROUP_"))
                    .toArray(WrapperItemEnum[]::new);
        }
    }
}