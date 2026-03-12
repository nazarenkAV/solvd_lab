package com.zebrunner.automation.gui.external;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class GitHubIssuePage extends AbstractPage {

    @FindBy(xpath = "//*[contains(@class, 'markdown-title')]")
    private ExtendedWebElement gitHubIssueTitle;

    public GitHubIssuePage(WebDriver driver) {
        super(driver);
    }

    public String getGitHubIssueTitle() {
        return gitHubIssueTitle.getText();
    }
}