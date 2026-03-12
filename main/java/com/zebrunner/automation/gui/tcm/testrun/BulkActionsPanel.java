package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class BulkActionsPanel extends AbstractUIObject {

    public static final String ROOT_XPATH = "//div[contains(@class, 'bulk-actions')]";

    @CaseInsensitiveXPath
    @FindBy(xpath = "//*[contains(text(), '%s')]/parent::button")
    private ExtendedWebElement actionButton;

    public BulkActionsPanel(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isBulkActionExist(TestRunsBulkActionsEnum action) {
        return format(actionButton, action.getItemValue()).isPresent(1);
    }

    @Getter
    @AllArgsConstructor
    public enum TestRunsBulkActionsEnum {
        DELETE("Delete"),
        CLOSE("Close"),
        ASSIGN_TO_MILESTONE("Assign to milestone");

        private final String itemValue;
    }
}
