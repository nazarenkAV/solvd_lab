package com.zebrunner.automation.gui.reporting.issue;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

@Getter
@Slf4j
public class JiraIssuePreview extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[@class='linked-issue-preview-wrapper']";

    @FindBy(xpath = ".//div[@class='linked-issue-preview__title-link']")
    private Element linkedIssueTitle;

    @FindBy(xpath = ".//div[@class='linked-issue-preview__header']//button[contains(@class, 'MuiButton-root')]")
    private Element closeButton;

    @FindBy(xpath = ".//*[@class='linked-issue-editable__button']")
    private Element editLinkedIssueButton;

    @FindBy(xpath = LinkedIssueAttributePreview.ROOT_XPATH)
    private List<LinkedIssueAttributePreview> attributePreviewList;

    public JiraIssuePreview(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTitle() {
        linkedIssueTitle.waitUntil(Condition.VISIBLE);
        return linkedIssueTitle.getText();
    }

    public Optional<LinkedIssueAttributePreview> findAttributeWithName(String attributeName) {
        log.info("Finding an attribute by name "+ attributeName);
        return attributePreviewList.stream()
                .filter(attribute ->
                        attribute.getAttributeNameText().equalsIgnoreCase(attributeName))
                .findFirst();
    }

    public void close() {
        closeButton.click();
        closeButton.waitUntil(Condition.DISAPPEAR);
        log.info("Jira issue preview is closed!");
    }
}
