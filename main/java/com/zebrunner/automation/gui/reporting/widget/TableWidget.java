package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class TableWidget extends BaseWidget {
    @FindBy(xpath = ".//thead//th[not(contains(@class,'ng-hide'))]")
    private List<ExtendedWebElement> headerElements;

    @FindBy(xpath = ".//tbody//tr")
    private List<TableRow> tableRows;

    public TableWidget(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public List<String> getHeader() {
        return headerElements.stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    public List<TableRow> getTableRows() {
        return tableRows;
    }
}
