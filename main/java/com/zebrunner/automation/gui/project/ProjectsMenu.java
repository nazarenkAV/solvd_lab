package com.zebrunner.automation.gui.project;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
@Slf4j
public class ProjectsMenu extends AbstractUIObject {

    @FindBy(xpath = ProjectItem.ROOT_XPATH)
    private List<ProjectItem> projectItems;

    @FindBy(xpath = ".//li[text()='View all Projects']")
    private Element viewAllProjectsButton;

    @FindBy(xpath = ".//li[text()='Create a Project']")
    private Element createNewProjectButton;

    @FindBy(xpath = ".//*[text() = 'Recent']")
    private ExtendedWebElement recentLabel;

    @FindBy(xpath = ".//*[text() = 'Starred']")
    private ExtendedWebElement starredLabel;

    public ProjectsMenu(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//ul[@role='menu']"));
    }

    public Optional<ProjectItem> getProjectByKey(String projectKey) {
        log.info("Trying to find project by key " + projectKey);
        return projectItems.stream()
                .filter(projectItem ->
                        projectItem.getProjectKey().equalsIgnoreCase(projectKey))
                .findFirst();
    }

    public boolean isCreateNewProjectButtonPresent() {
        return createNewProjectButton.isStateMatches(Condition.VISIBLE);
    }

    public boolean isViewAllProjectsButtonPresent() {
        return viewAllProjectsButton.isStateMatches(Condition.VISIBLE);
    }

    // TODO check logic
    public boolean areProjectsPresent() {
        log.info("Waiting for projectTitles list to load...");
        WaitUtil.waitCheckListIsNotEmpty(projectItems);
        if (projectItems.size() < 1) {
            return false;
        }

        return projectItems.stream().map(ProjectItem::getProjectName).count() == projectItems.stream().map(ProjectItem::getProjectKey).count() &&
                projectItems.stream().map(ProjectItem::getProjectName).count() == projectItems.stream().map(ProjectItem::getProjectImg).count();
    }

    public ProjectsPage toProjectsPage() {
        pause(2);
        viewAllProjectsButton.click();
        return ProjectsPage.getInstance(getDriver());
    }

    public ProcessProjectModal openNewProjectWindow() {
        createNewProjectButton.click();
        pause(4);
        return new ProcessProjectModal(getDriver());
    }

    public boolean isProjectPresent(String projectName) {
        log.info("Checking if project with name " + projectName + " is present");
        return projectItems.stream()
                .anyMatch(projectItem ->
                        projectItem.getProjectName().equals(projectName));
    }

    public boolean isProjectPresent(String projectName, String projectKey) {
        log.info("Checking if project with name " + projectName + " and with key " + projectKey + " is present");
        return projectItems.stream()
                .anyMatch(projectItem ->
                        projectItem.getProjectName().equals(projectName) && projectItem.getProjectKey().equalsIgnoreCase(projectKey));

    }

    public List<ProjectItem> getProjectItems() {
        return projectItems;
    }

    public boolean isStarredLabelPresent() {
        return starredLabel.isElementPresent(3);
    }

    public boolean isRecentLabelPresent() {
        return recentLabel.isElementPresent(3);
    }

    public boolean isProjectMenuOpened() {
        return viewAllProjectsButton.isStateMatches(Condition.PRESENT) &&
                createNewProjectButton.isStateMatches(Condition.PRESENT) &&
                recentLabel.isElementPresent(5);
    }
}