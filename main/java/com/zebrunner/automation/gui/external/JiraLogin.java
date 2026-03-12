package com.zebrunner.automation.gui.external;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class JiraLogin extends AbstractPage {

    @FindBy(xpath = "//span[@aria-label = 'Jira']")
    private ExtendedWebElement jiraLoginTitle;

    public JiraLogin(WebDriver driver) {
        super(driver);
    }

    public String getUrl() {
        return this.getDriver().getCurrentUrl();
    }

    public boolean isJiraLoginTitlePresent() {
        return jiraLoginTitle.isElementPresent();
    }
}
