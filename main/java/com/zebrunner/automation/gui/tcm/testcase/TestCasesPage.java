package com.zebrunner.automation.gui.tcm.testcase;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.tcm.SuiteViewListbox;
import com.zebrunner.automation.gui.tcm.repository.BaseRepositoryItem;
import com.zebrunner.automation.gui.tcm.repository.RepositoryLeftPanel;
import com.zebrunner.automation.gui.tcm.repository.RepositoryList;
import com.zebrunner.automation.gui.tcm.testsuite.BaseSuiteItem;
import com.zebrunner.automation.gui.tcm.testsuite.TestSuitesTree;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;


@Getter
public class TestCasesPage extends TenantProjectBasePage implements IMobileUtils {

    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/test-cases";

    public static final String EMPTY_PLACEHOLDER_TITLE_WHEN_SEARCH = "No results were found matching your search";
    public static final String EMPTY_PLACEHOLDER_DESCRIPTION_WHEN_SEARCH = "Consider revising and trying again.";

    public static final String EMPTY_PLACEHOLDER_TITLE_FOR_EMPTY_REPO = "Test case repository is empty";
    public static final String EMPTY_PLACEHOLDER_DESCRIPTION_FOR_EMPTY_REPO =
            "Start describing your application workflows by creating a test suite\n" +
                    "and adding test cases to it.";

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='suite']/parent::button")
    private ExtendedWebElement createSuiteButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='case']/parent::button")
    private ExtendedWebElement createCaseButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Import']")
    private ExtendedWebElement importButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[text()='Create test suite']")
    private ExtendedWebElement createTestSuiteButton;

    @FindBy(xpath = "//div[@aria-label = 'Switch repository view']")
    private ExtendedWebElement switchRepoView;

    @FindBy(xpath = "//button[contains(@class,'info-dark')]")
    private ExtendedWebElement settingsButton;

    @FindBy(xpath = "//div[@class='repository-left-pane ']")
    private RepositoryLeftPanel leftActionExpandedPanel;

    @FindBy(xpath = "//div[@class='repository-left-pane _column']")
    private RepositoryLeftPanel leftActionCollapsedPanel;

    @FindBy(xpath = TestSuitesTree.ROOT_XPATH)
    private TestSuitesTree testSuiteTree;

    @FindBy(xpath = RepositoryList.ROOT_XPATH)
    private RepositoryList repository;

    @FindBy(xpath = "//h2[@class='field-settings-header__title']")
    private ExtendedWebElement uiLoadedMarker;

    public TestCasesPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static TestCasesPage openPageDirectly(WebDriver driver, String projectKey) {
        TestCasesPage testCasesPage = new TestCasesPage(driver);
        testCasesPage.openURL(String.format(PAGE_URL, projectKey));

        testCasesPage.assertPageOpened();

        return testCasesPage;
    }

    public boolean isSuiteTreeExpanded() {
        return testSuiteTree.isVisible(5);
    }

    public void expandSuitePanel() {
        if (!this.isSuiteTreeExpanded()) {
            leftActionCollapsedPanel.clickPaneIcon();
        }
    }

    public void collapseSuitePanel() {
        if (this.isSuiteTreeExpanded()) {
            leftActionExpandedPanel.clickPaneIcon();
        }
    }

    public void expandCases() {
        if (!isSuiteTreeExpanded()) {
            leftActionExpandedPanel.clickPaneIcon();
        }
        new RepositoryLeftPanel(getDriver())
                .expandAll();
    }

    public SuiteViewListbox clickSwitchRepoView() {
        switchRepoView.click();
        return new SuiteViewListbox(getDriver());
    }

    public RepositoryCaseItem getTestCaseWithSwipe(String caseNameOrKey) {
        RepositoryCaseItem searchedCase = repository.getTestCase(caseNameOrKey);
        if (swipe(searchedCase, repository, Direction.UP)) {
            return searchedCase;
        } else {
            return null;
        }
    }

    public RepositoryCaseItem findTestCase(String caseNameOrKey) {
        if (repository.getRepositoryItems().isEmpty()) {
            throw new NoSuchElementException("Repository list is empty!!!");
        }
        expandCases();
        return repository.findTestCase(caseNameOrKey);
    }


    public boolean isTestCasePresent(String caseNameOrKey) {
        RepositoryCaseItem repositoryCaseItem;
        try {
            repositoryCaseItem = findTestCase(caseNameOrKey);
        } catch (NoSuchElementException e) {
            return false;
        }
        return repositoryCaseItem.isPresent(3);
    }

    public RepositoryCaseItem getTestCaseWithSwipe(String caseNameOrKey, int swipeCount) {
        RepositoryCaseItem searchedCase = repository.getTestCase(caseNameOrKey);
        if (swipe(searchedCase, repository, swipeCount)) {
            return searchedCase;
        } else {
            return null;
        }
    }

    public BaseRepositoryItem getTestSuite(String suiteName, int swipeCount) {
        BaseRepositoryItem searchedSuite = repository.getSuite(suiteName);
        if (swipe(searchedSuite, repository, swipeCount)) {
            return searchedSuite;
        } else {
            return null;
        }
    }

    public BaseRepositoryItem getTestSuite(String caseNameOrKey, boolean isTreeViewEnabled) {
        if (isTreeViewEnabled) {
            this.clickSwitchRepoView().clickItem(SuiteViewListbox.SuiteViewTypes.TREE_VIEW);
        }

        BaseRepositoryItem searchedSuite = repository.getSuite(caseNameOrKey);

        if (swipe(searchedSuite, repository, Direction.UP)) {
            return searchedSuite;
        }

        if (swipe(searchedSuite, repository, Direction.DOWN)) {
            return searchedSuite;
        }

        return null;
    }

    public BaseSuiteItem findTestSuiteInSuiteTree(String suiteName) {
        expandSuitePanel();
        expandCases();
        return this.getTestSuiteTree().findTestSuite(suiteName);
    }

    public BaseRepositoryItem getTestSuite(String caseNameOrKey) {
        return getTestSuite(caseNameOrKey, true);
    }

    public boolean isCreateCaseButtonClickable() {
        return createCaseButton.isClickable(2);
    }

    public boolean isCreateSuiteButtonClickable() {
        return createSuiteButton.isClickable(2);
    }

    public boolean isCreateTestSuiteButtonClickable() {
        //for empty repository
        return createTestSuiteButton.isClickable(2);
    }

    public boolean isImportButtonClickable() {
        //for empty repository
        return importButton.isClickable(2);
    }

    public void selectSuiteFromSuiteTree(String suiteName) {
        testSuiteTree.selectSuite(suiteName);
    }

    public boolean isLeftSuiteTreeVisible() {
        return testSuiteTree.isVisible(5);
    }

    public void createTestCase(String caseTitle, String suiteName) {

        CreateTestCaseModal createTestCaseModal = clickCreateTestCaseBtn();
        createTestCaseModal
                .inputTitle(caseTitle)
                .selectParentSuite(suiteName)
                .submitModal();
    }

    public CreateTestCaseModal clickCreateTestCaseBtn() {
        createCaseButton.click();
        return new CreateTestCaseModal(getDriver());
    }

    public void searchTestCase(String searchString) {
        search.search(searchString);
        super.pause(4);
        super.waitInvisibilityOfLoader();
    }

    public TrashBinPage openTrashBinPage() {
        settingsButton.click();
        Dropdown dropdown = new Dropdown(getDriver());
        dropdown.findItem(Dropdown.DropdownItemsEnum.TEST_CASES_SETTING_TRASH_BIN.getItemValue()).click();
        return new TrashBinPage(getDriver());
    }

    public ImportTestCasesModal openImportModal() {
        settingsButton.click();
        Dropdown dropdown = new Dropdown(getDriver());
        dropdown.findItem(Dropdown.DropdownItemsEnum.TEST_CASES_SETTING_IMPORT.getItemValue()).click();
        return new ImportTestCasesModal(getDriver());
    }

}
