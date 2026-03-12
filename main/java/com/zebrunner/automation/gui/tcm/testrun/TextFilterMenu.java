package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class TextFilterMenu extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[contains(@class, 'text-filter-menu')]";

    @FindBy(xpath = ".//label[text() = 'Contains']")
    private ExtendedWebElement containsLabel;

    @FindBy(xpath = ".//*[@placeholder = 'Enter your search query...']")
    private ExtendedWebElement textArea;

    @FindBy(xpath = ".//button[text() = 'Apply']")
    private ExtendedWebElement applyButton;

    public TextFilterMenu(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public boolean isContainsLabelPresent() {
        return containsLabel.isElementPresent(2);
    }

    public boolean isTextAreaActive() {
        return textArea.isClickable(2);
    }

    public boolean isApplyButtonClickable() {
        return applyButton.isClickable(2);
    }

    public void searchQuery(String query) {
        textArea.type(query);
    }

    public void clickApplyButton() {
        applyButton.click();
    }
}
