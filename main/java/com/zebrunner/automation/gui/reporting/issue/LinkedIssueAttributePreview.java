package com.zebrunner.automation.gui.reporting.issue;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class LinkedIssueAttributePreview extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[@class='linked-issue-preview__attribute']";

    @FindBy(xpath = ".//span[@class='linked-issue-preview__field-label']")
    public Element attributeName;

    @FindBy(xpath = ".//div[@class='linked-issue-preview__attribute-value']")
    public Element attributeValue;

    public LinkedIssueAttributePreview(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getAttributeNameText() {
        return attributeName.getText();
    }

    public String getAttributeValueText() {
        return attributeValue.getText();
    }
}
