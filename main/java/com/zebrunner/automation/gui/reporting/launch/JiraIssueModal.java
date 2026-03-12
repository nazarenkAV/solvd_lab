package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.external.JiraLogin;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class JiraIssueModal extends AbstractUIObject {

    @FindBy(xpath = "//*[@class = 'linked-issue-preview__title-link']/a")
    private ExtendedWebElement jiraIssueTitleLink;

    public JiraIssueModal(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[contains(@class,'linked-issue-preview')]"));
    }

    public String getJiraIssueTitleLink() {
        return jiraIssueTitleLink.getAttribute("href");
    }

    public JiraLogin clickJiraIssueTitleLink() {
        jiraIssueTitleLink.click();
        return new JiraLogin(getDriver());
    }
}
