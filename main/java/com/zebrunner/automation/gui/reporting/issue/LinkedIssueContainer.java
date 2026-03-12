package com.zebrunner.automation.gui.reporting.issue;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class LinkedIssueContainer extends AbstractUIObject {

    @FindBy(xpath = ".//*[contains(@class, 'linked-issue-chip')]//span[@class='text']")
    private Element linkedIssueText;

    @FindBy(xpath = ".//*[@class='linked-issue-editable__button']")
    private Element editLinkedIssueButton;

    @FindBy(xpath = JiraIssuePreview.ROOT_XPATH)
    private JiraIssuePreview jiraIssuePreview;

    public LinkedIssueContainer(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getLinkedIssueValue() {
        return linkedIssueText.getText();
    }

    public JiraIssuePreview clickLinkedIssue() {
        linkedIssueText.click();
        return jiraIssuePreview;
    }

}
