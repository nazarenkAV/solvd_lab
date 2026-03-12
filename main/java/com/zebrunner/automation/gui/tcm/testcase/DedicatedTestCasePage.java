package com.zebrunner.automation.gui.tcm.testcase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.List;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.common.ZbrTitleInput;
import com.zebrunner.automation.gui.tcm.AccordionContainer;
import com.zebrunner.automation.gui.tcm.TabWysiwygContainer;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Getter
public class DedicatedTestCasePage extends TenantProjectBasePage {

    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/test-cases/%s";

    public static final String DELETED_CASE_INFO = "This test case has been deleted from the test repository and is now located in the trash bin. Its data cannot be edited.\n" +
            "You have the option to either restore this test case or permanently purge it.";

    @FindBy(xpath = "//div[contains(@class,'test-case-view-header__key')]")
    private ExtendedWebElement caseKey;

    @FindBy(xpath = ZbrTitleInput.ROOT_XPATH)
    private ZbrTitleInput titleInput;

    @FindBy(xpath = "//div[contains(@class,'test-case-view-header__title')]//input")
    private Element caseName;

    @FindBy(xpath = ".//*[@d = '" + SvgPaths.THREE_DOTS + "']//ancestor::button")
    private ExtendedWebElement settingsButton;

    @FindBy(xpath = "//div[contains(@class,'suite-field-container')]//input")
    private ExtendedWebElement suiteInput;

    @FindBy(xpath = "//p[@class='deleted-test-case-info']")
    private ExtendedWebElement deletedCaseInfo;

    @FindBy(xpath = ".//*[text()='Description']" + TabWysiwygContainer.ROOT_XPATH)
    protected TabWysiwygContainer descriptionInput;

    @FindBy(xpath = ".//*[text()='Pre-conditions']" + AccordionContainer.ROOT_XPATH)
    protected TabWysiwygContainer preConditionsInput;

    @FindBy(xpath = ".//*[text()='Post-conditions']" + AccordionContainer.ROOT_XPATH)
    protected TabWysiwygContainer postConditionsInput;

    @FindBy(xpath = "//span[text()='Steps to reproduce']")
    private ExtendedWebElement stepsToReproduceContainer;

    @FindBy(xpath = "//button[@aria-label='Edit steps']")
    private ExtendedWebElement editStepsButton;

    @FindBy(xpath = RepositoryPreviewStepContainer.ROOT_XPATH)
    protected List<RepositoryPreviewStepContainer> steps;

    @FindBy(xpath = "//div[@class='properties-and-attachments-container']")
    public PropertiesTab propertiesTab;

    @FindBy(xpath = "//div[@class='execution']//parent::div[@class='executions']")
    public ExecutionsTab executionsTab;

    @FindBy(xpath = "//div[@class='test-case-view-tabs']")
    public TabsWrapper tabsWrapper;

    @FindBy(xpath = "//*[contains(text(),'Attachments')]//ancestor::*[contains(@class,'MuiAccordion-root') ]")
    public AttachmentsTab attachmentsTab;


    public DedicatedTestCasePage(WebDriver driver) {
        super(driver);
        super.setUiLoadedMarker(tabsWrapper);
    }

    public static DedicatedTestCasePage openPageDirectly(WebDriver webDriver, String projectKey, Long caseId) {
        DedicatedTestCasePage dedicatedTestCasePage = new DedicatedTestCasePage(webDriver);
        dedicatedTestCasePage.openURL(String.format(PAGE_URL, projectKey, caseId));
        dedicatedTestCasePage.assertPageOpened();

        return dedicatedTestCasePage;
    }

    public SelectSuiteListBoxMenu typeSuite(String parentSuiteName) {
        suiteInput.type(parentSuiteName);

        return new SelectSuiteListBoxMenu(super.getDriver());
    }

    public Dropdown clickSettings() {
        settingsButton.click();

        return new Dropdown(super.getDriver());
    }

    public EditTestCaseModal openEditTestCaseModal() {
        this.clickSettings()
            .findItem("Edit")
            .click();

        return new EditTestCaseModal(super.getDriver());
    }

    public DeleteTestCaseModal openDeleteTestCaseModal() {
        this.clickSettings()
            .findItem("Delete")
            .click();

        return new DeleteTestCaseModal(super.getDriver());
    }

    public CloneTestCaseModal openCloneTestCaseModal() {
        this.clickSettings()
            .findItem("Clone")
            .click();

        return new CloneTestCaseModal(super.getDriver());
    }

    public EditStepsModal openEditStepsModal() {
        stepsToReproduceContainer.hover();
        editStepsButton.click();

        return new EditStepsModal(super.getDriver());
    }

    public DedicatedTestCasePage inputPreConditions(String text) {
        preConditionsInput.input(text)
                          .clickSaveButton();
        return this;
    }

    public DedicatedTestCasePage inputPostConditions(String text) {
        postConditionsInput.input(text)
                           .clickSaveButton();
        return this;
    }

    public DedicatedTestCasePage inputDescription(String text) {
        descriptionInput.input(text)
                        .clickSaveButton();
        return this;
    }

    public String getTestCaseTitle() {
        return caseName.getAttribute("value");
    }

    public String getTestCaseKeyTextValue() {
        return caseKey.getText();
    }

    public String getPreConditions() {
        return preConditionsInput.getContentValue();
    }

    public String getPostConditions() {
        return postConditionsInput.getContentValue();
    }

    public String getDescriptionTextValue() {
        return descriptionInput.getContentValue();
    }

    public String getPriority() {
        return propertiesTab.getPriorityValue();
    }

    public void selectPriority(String priority) {
        propertiesTab.selectPriority(priority);
    }

    public ExecutionsTab openExecutionsTab() {
        tabsWrapper.clickTab(TabsWrapper.Tabs.EXECUTIONS);
        return executionsTab;
    }

    public void addAttachment(String filePath) {
        attachmentsTab.addAttachmentInput.attachFile(filePath);
    }

    public String getDeletedCaseInfo() {
        return deletedCaseInfo.getText();
    }

}
