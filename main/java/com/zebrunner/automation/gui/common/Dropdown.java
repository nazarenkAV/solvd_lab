package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class Dropdown extends AbstractUIObject {
    public static final String ROOT_LOCATOR = "//div[@class='dropdown__items']";

    @FindBy(xpath = ".//span[@class='dropdown__item-title']")
    private List<Element> dropdownItems;

    public Dropdown(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_LOCATOR));
    }

    public List<Element> getDropdownItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROOT_LOCATOR + "//li"), 0), 3);
        return dropdownItems;
    }

    public Element findItem(String item) {
        log.info("Searching dropdown item " + item);
        return getDropdownItems().stream()
                .filter(dropdownItem -> dropdownItem.getText().equalsIgnoreCase(item))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Dropdown item '%s' was not found!", item)));

    }

    @Getter
    @AllArgsConstructor
    public enum DropdownItemsEnum {
        COPY_LINE("Copy line"),
        COPY_PERMALINK("Copy permalink"),
        COPY_LINK("Copy link"),
        RESEND("Resend"),
        REVOKE("Revoke"),

        DEFAULT_STATUS("Status"),
        STATUS_FAILED("Failed"),
        STATUS_FAILED_NO_LINKED_ISSUE("Failed (no linked issue)"),
        STATUS_SKIPPED("Skipped"),
        STATUS_PASSED("Passed"),
        STATUS_ABORTED("Aborted"),
        STATUS_IN_PROGRESS("In Progress"),

        TEST_CASE_STATE_UPDATED("Updated"),
        TEST_CASE_STATE_DEPRECATED("Deprecated"),

        TEST_CASES_SETTING_TRASH_BIN("Trash bin"),
        TEST_CASES_SETTING_IMPORT("Import"),

        ENVIRONMENT("Environment"),
        LAUNCH_DATE("Launch date"),
        REVIEWED("Reviewed"),
        LOCALE("Locale"),

        MARK_AS_FAILED("Mark as Failed"),
        MARK_AS_PASSED("Mark as Passed"),
        DOWNLOAD_ARTIFACT("Download artifacts"),

        YES("Yes"),
        NO("No"),
        ANY("Any"),

        ON("On"),
        ON_OR_AFTER("On or after"),
        ON_OR_BEFORE("On or before"),
        BETWEEN("Between (Inclusive)");

        private final String itemValue;

        public static Dropdown.DropdownItemsEnum[] getStatues() {
            return Arrays.stream(DropdownItemsEnum.values())
                    .filter(enumValue -> enumValue.name().startsWith("STATUS_"))
                    .toArray(DropdownItemsEnum[]::new);
        }
    }
}
