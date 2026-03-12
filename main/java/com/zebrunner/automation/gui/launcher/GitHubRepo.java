package com.zebrunner.automation.gui.launcher;

import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Getter
public class GitHubRepo extends AbstractUIObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(xpath = ".//span[@class='repository-list__item-link-text']")
    private Element repoName;
    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Add']")
    private Element addButton;

    @FindBy(xpath = ".//*[@class='repository-list__item-name']//*[name()='svg']")
    private Element toGitHubRepoIcon;

    public GitHubRepo(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void clickAddRepo() {
        LOGGER.info("Adding GitHub repository....");
        addButton.click();
        // Button cannot click without waiting
        pause(2);
    }

    public void clickToGitHubRepo() {
        LOGGER.info("Redirect to GitHub repository....");

        toGitHubRepoIcon.click();
//        waitUntil(Condition.DISAPPEAR);
        pause(3);
    }
}
