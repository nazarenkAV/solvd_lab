package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;

import static org.reflections.Reflections.log;

public class WysiwygInputEditor extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//*[@class = 'toastui-editor-popup-body']";

    @FindBy(xpath = ".//li")
    private List<Element> dropdownItems;

    public WysiwygInputEditor(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
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
        // =================== Heading ========================
        HEADING_1("Heading 1"),
        HEADING_2("Heading 2"),
        HEADING_3("Heading 3"),
        HEADING_4("Heading 4"),
        HEADING_5("Heading 5"),
        HEADING_6("Heading 6"),
        HEADING_PARAGRAPH("Paragraph");


        private final String itemValue;
    }
}
