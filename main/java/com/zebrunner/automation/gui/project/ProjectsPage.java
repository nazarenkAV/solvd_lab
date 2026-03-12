package com.zebrunner.automation.gui.project;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.zebrunner.automation.api.reporting.service.ApiHelperServiceImpl;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantBasePage;
import com.zebrunner.automation.gui.common.ZbrSearch;
import com.zebrunner.automation.gui.reporting.TestRunsPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;

@Getter
public class ProjectsPage extends TenantBasePage {

    public static final String PAGE_NAME = "Projects";
    public static final String URL = ConfigHelper.getTenantUrl() + "/projects";

    @Getter
    @FindBy(xpath = "." + ProjectCard.CARD_ROOT_XPATH)
    private List<ProjectCard> projectCards;

    @FindBy(xpath = ResizedProjectCard.RESIZED_CARD_ROOT_XPATH)
    private List<ResizedProjectCard> resizedProjectCards;

    @FindBy(xpath = "//*[contains(@class, 'projects-info MuiBox-root')]")
    private ExtendedWebElement projectsCountMessage;

    @FindBy(xpath = ZbrSearch.ROOT_XPATH)
    private ZbrSearch search;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='project']//parent::button")
    private Element newProjectButton;

    @FindBy(xpath = ".//*[contains(@class,'table-header-cell')]//*[text()='key']")
    private ExtendedWebElement columnKeyHeader;

    @FindBy(xpath = ".//*[contains(@class,'table-header-cell')]//*[text()='created']")
    private ExtendedWebElement columnCreatedHeader;

    @FindBy(xpath = ".//*[contains(@class,'table-header-cell')]//*[text()='name']")
    private ExtendedWebElement columnNameHeader;

    public ProjectsPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(columnKeyHeader);
        setPageURL("/projects");
    }

    public static ProjectsPage getInstance(WebDriver driver) {
        return new ProjectsPage(driver);
    }

    public static ProjectsPage openPageDirectly(WebDriver driver) {
        ProjectsPage projectsPage = new ProjectsPage(driver);

        projectsPage.open();
        projectsPage.pause(1);
        projectsPage.assertPageOpened();

        return projectsPage;
    }

    public boolean isSearchFieldPresent() {
        return search.isVisible(5);
    }

    public boolean isNewProjectButtonClickable() {
        return newProjectButton.isClickable(3);
    }

    public boolean isNewProjectButtonPresent() {
        return newProjectButton.isStateMatches(Condition.VISIBLE);
    }

    public List<ResizedProjectCard> getResizedProjectCards() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), ResizedProjectCard.RESIZED_CARD_ROOT_XPATH);
        return resizedProjectCards;
    }

    public ProcessProjectModal openNewProjectModal() {
        newProjectButton.click();
        return new ProcessProjectModal(getDriver());
    }

    public String getNumberOfProjectCards() {
        WaitUtil.waitCheckListIsNotEmpty(projectCards);
        return String.valueOf(projectCards.size());
    }

    public TestRunsPageR toCertainProject(String projectKey) {
        ProjectCard foundProjectCard = WaitUtil.waitElementAppearedInListByCondition(projectCards,
                card -> card.getKey().equalsIgnoreCase(projectKey),
                "Project with key " + projectKey + " was found",
                "There are no project card with key: " + projectKey);

        return foundProjectCard
                .toTestRunsPage();
    }

    /**
     * Checking is project with name and key exists
     */
    public boolean isProjectWithNameAndKeyExists(String projectName, String projectKey) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(projectCards,
                projectCard -> projectCard.getProjectName().equalsIgnoreCase(projectName) &&
                        projectCard.getKey().equalsIgnoreCase(projectKey));
    }

    /**
     * Checking is project with key exists
     */
    public boolean isProjectWithKeyExists(String projectKey) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(projectCards,
                projectCard -> projectCard.getKey().equalsIgnoreCase(projectKey));
    }

    /**
     * Searching project by key and opening it's runs
     */
    @Deprecated
    public TestRunsPageR toProjectRunsByProjectKey(String projectKey) {
        ProjectCard foundedProjectCard = WaitUtil.waitElementAppearedInListByCondition(projectCards,
                projectCard -> projectCard.getKey().equalsIgnoreCase(projectKey),
                "Found project by the key " + projectKey,
                "Project with key " + projectKey + " was not found");

        return foundedProjectCard.toTestRunsPage();
    }

    /**
     * Getting project card by project key
     */
    public ProjectCard getProjectCardByProjectKey(String projectKey) {
        return WaitUtil.waitElementAppearedInListByCondition(projectCards,
                card -> card.getKey().equalsIgnoreCase(projectKey),
                "Project with key " + projectKey + " was found",
                "Project with key " + projectKey + " was not found");
    }

    /**
     * Find project by project key and open an editing project modal
     */
    public ProcessProjectModal editCertainProjectByKey(String projectKey) {
        ProjectCard projectCardToEdit = WaitUtil.waitElementAppearedInListByCondition(projectCards,
                projectCard -> projectCard.getKey().equalsIgnoreCase(projectKey),
                "Project with key " + projectKey + " was found",
                "There are no card with key: " + projectKey);
        return projectCardToEdit.editCard();
    }

    public AutomationLaunchesPage createProject(String name, String key) {
        openNewProjectModal().typeProjectName(name)
                .typeProjectKey(key)
                .getSubmitButton().click();
        AutomationLaunchesPage testRunsPage = new AutomationLaunchesPage(getDriver());
        testRunsPage.closeOnboardingModalIfExists();
        new ApiHelperServiceImpl().startTRWithCertainConfig(key, "TEST");
        return testRunsPage;
    }

    public void search(String projectName) {
        if (search.getClearButton().isStateMatches(Condition.CLICKABLE)) {
            search.clearSearch();
            pause(2);
        }
        search.search(projectName);
        pause(1);
    }

    public boolean isPossibleToSortByDate() {
        return columnCreatedHeader.isClickable(2);
    }

    public boolean isPossibleToSortByKey() {
        return columnKeyHeader.isClickable(2);
    }

    public void sortByKey() {
        columnKeyHeader.click();
        pause(2);
    }

    public void sortByCreated() {
        columnCreatedHeader.click();
        pause(2);
    }

    public void sortByName() {
        columnNameHeader.click();
        pause(2);
    }

    public String getProjectsCountMessages() {
        return projectsCountMessage.getText();
    }

    public List<ProjectCard> getStarredProjectCards() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), ProjectCard.CARD_ROOT_XPATH);
        return projectCards.stream()
                .filter(ProjectCard::isProjectStarred)
                .collect(Collectors.toList());
    }

    public List<ProjectCard> getNoStarredProjectCards() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), ProjectCard.CARD_ROOT_XPATH);
        return projectCards.stream()
                .filter(card -> !card.isProjectStarred())
                .collect(Collectors.toList());
    }

    public List<String> getDefaultSortedAllProjectsName() {
        List<String> starredProjectCardsNames = getStarredProjectCards().stream().map(ProjectCard::getProjectName).collect(Collectors.toList());
        List<String> simpleProjectsNames = getNoStarredProjectCards().stream().map(ProjectCard::getProjectName).collect(Collectors.toList());

        starredProjectCardsNames.sort(Comparator.comparing(String::trim, String.CASE_INSENSITIVE_ORDER));
        simpleProjectsNames.sort(Comparator.comparing(String::trim, String.CASE_INSENSITIVE_ORDER));

        List<String> defaultSortedAllProjectsNames = new ArrayList<>(starredProjectCardsNames);
        defaultSortedAllProjectsNames.addAll(simpleProjectsNames);

        return defaultSortedAllProjectsNames;
    }

}
