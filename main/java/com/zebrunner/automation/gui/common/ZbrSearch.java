package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Getter
public class ZbrSearch extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[contains(@class,'ZbrSearchField')]";

    @FindBy(xpath = ".//input")
    private Element searchField;

    @FindBy(xpath = ".//input/following-sibling::div/*[local-name()='svg']")
    private Element clearButton;

    public ZbrSearch(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[contains(@class,'zbr-autocomplete') and contains(@class, 'MuiPopper-root')]"));
    }

    public boolean isSearchClickable() {
        return searchField.isClickable(4);
    }

    public void search(String text) {
        waitUntil(ExpectedConditions.elementToBeClickable(searchField), 3);
        searchField.sendKeys(text);
    }

    public String getSearchValue() {
        return searchField.getAttributeValue("value");
    }

    public ZbrSearch clearSearch() {
        clearButton.click();
        return this;
    }

}
