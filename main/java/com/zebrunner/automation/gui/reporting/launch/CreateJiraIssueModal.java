package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.common.Calendar;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.legacy.RandomUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
@Getter
public class CreateJiraIssueModal extends AbstractModal<CreateJiraIssueModal> {

    public static final String MODAL_TITLE = "Create issue";

    private static final String SUGGESTION_XPATH = "//li[contains(@class, 'option')]";

    @FindBy(id = "jiraProject")
    private Element jiraProjectListBox;

    @FindBy(id = "IssueType")
    private Element issueTypeListBox;

    @FindBy(id = "description")
    private ExtendedWebElement descriptionInput;

    @FindBy(id = "summary")
    private ExtendedWebElement summaryInput;

    @FindBy(xpath = "//*[text()='More fields']//parent::button")
    private ExtendedWebElement moreFieldsButton;

    @FindBy(xpath = "//*[text()='Less fields']//parent::button")
    private ExtendedWebElement lessFieldsButton;

    @FindBy(xpath = "//*[@class = 'jira-issue__container _flex']")
    private ExtendedWebElement additionalElementsPopUP;

    @FindBy(id = "Time stamp")
    private ExtendedWebElement timeStampInput;

    @FindBy(xpath = "//*[@id = 'Time stamp']/parent::div//*[local-name()='svg']")
    private ExtendedWebElement timeStampCalendarLogo;

    @FindBy(xpath = "//*[@placeholder = 'Numbers']")
    private ExtendedWebElement numbersInput;

    @FindBy(xpath = Calendar.ROOT_LOCATOR)
    private Calendar calendar;

    @FindBy(xpath = "//button[text() = 'Create and link']")
    private ExtendedWebElement createAndLinkButton;

    @FindBy(xpath = "//*[text() = 'People']/parent::div//input")
    private ExtendedWebElement peopleInput;

    @FindBy(xpath = "//*[text() = 'People']/parent::div//span[contains(@class,'label')]")
    private List<ExtendedWebElement> selectedPeople;

    @FindBy(xpath = "//*[text() = 'Reporter']/parent::div//input")
    private ExtendedWebElement reporterInput;

    @FindBy(xpath = SUGGESTION_XPATH)
    private List<ExtendedWebElement> suggestions;

    @FindBy(xpath = "//*[text() = 'Flagged']/parent::div")
    private ExtendedWebElement flaggedInput;

    @FindBy(id = "mui-component-select-priority")
    private ExtendedWebElement priorityInput;

    @FindBy(xpath = "//*[text() = 'Labels']/parent::div//input")
    private ExtendedWebElement labelsInput;

    @FindBy(xpath = "//*[text() = 'Labels']/parent::div//span[contains(@class,'label')]")
    private List<ExtendedWebElement> selectedLabels;

    @FindBy(id = "assignee")
    private ExtendedWebElement assigneeInput;

    public CreateJiraIssueModal(WebDriver driver) {
        super(driver);
    }

    public void openJiraProjectListBox() {
        jiraProjectListBox.click();
    }

    public void openIssueTypeListBox() {
        issueTypeListBox.click();
    }

    public void selectJiraProject(String jiraProject) {
        new ListBoxMenu(getDriver()).clickItem(jiraProject);
    }

    public void openAndSelectJiraProject(String jiraProject) {
        openJiraProjectListBox();
        selectJiraProject(jiraProject);
    }

    public void selectIssueType(String issueType) {
        new ListBoxMenu(getDriver()).clickItem(issueType);
    }

    public void openAndSelectIssueType(String issueType) {
        openIssueTypeListBox();
        selectIssueType(issueType);
    }

    public boolean isSummaryInputFieldPresent() {
        return summaryInput.isElementPresent();
    }

    public String getTextOfDescriptionInputField() {
        return descriptionInput.getText();
    }

    public String getSummaryText() {
        return summaryInput.getAttribute("value");
    }

    public String getIssueTypeText() {
        return issueTypeListBox.getText();
    }

    public boolean isJiraProjectPresentInList(String jiraProject) {
        return new ListBoxMenu(getDriver()).isItemPresentInList(jiraProject);
    }

    public boolean isMoreFieldsButtonPresent() {
        return moreFieldsButton.isElementPresent();
    }

    public boolean isLessFieldsButtonPresent() {
        return lessFieldsButton.isElementPresent();
    }

    public void clickMoreFieldsButton() {
        moreFieldsButton.click();
    }

    public void clickLessFieldsButton() {
        lessFieldsButton.click();
    }

    public void clickTimeStampCalendarLogo() {
        timeStampInput.click();
    }

    public String getSelectedTimeStampDate() {
        return timeStampInput.getAttribute("value");
    }

    public void typeSummary(String summary) {
        summaryInput.type(summary);
    }

    public void typeDescription(String description) {
        descriptionInput.type(description);
    }

    public void typeNumber(String number) {
        numbersInput.type(String.valueOf(number));
    }

    public String getNumber() {
        return numbersInput.getAttribute("value");
    }

    public boolean isCreateAndLinkButtonClickable() {
        return createAndLinkButton.isClickable(2);
    }

    public void fillAlMandatoryFields(String jiraProject, String jiraIssue, String summary) {
        openAndSelectJiraProject(jiraProject);
        openAndSelectIssueType(jiraIssue);
        typeSummary(summary);
    }

    public String selectAnyPersonStartsWith(String text) {
        peopleInput.type(text);
        WaitUtil.waitNotEmptyListOfElements(getDriver(), SUGGESTION_XPATH);

        int randomIndex = RandomUtil.generateRandomNumber(0, suggestions.size() - 1);
        String name = suggestions.get(randomIndex).getText();
        suggestions.get(randomIndex).click();
        return name;
    }

    public boolean isPersonSelected(String person) {
        boolean condition = false;
        for (ExtendedWebElement e : selectedPeople) {
            if (e.getText().equals(person)) {
                condition = true;
                break;
            }
        }
        return condition;
    }

    public String selectAnyReporterStartWith(String text) {
        reporterInput.type(text);
        WaitUtil.waitNotEmptyListOfElements(getDriver(), SUGGESTION_XPATH);

        int randomIndex = RandomUtil.generateRandomNumber(0, suggestions.size() - 1);
        String reporter = suggestions.get(randomIndex).getText();
        suggestions.get(randomIndex).click();
        return reporter;
    }

    public String getReporter() {
        return reporterInput.getAttribute("value");
    }

    public boolean isAdditionalElementsPopUP() {
        return additionalElementsPopUP.isVisible(2);
    }

    public void openAndSelectPriority(String priority) {
        priorityInput.click();
        new ListBoxMenu(getDriver()).clickItem(priority);
    }

    public String getPriority() {
        return priorityInput.getText();
    }

    public void openAndSelectFlag(String flag) {
        flaggedInput.click();
        new ListBoxMenu(getDriver()).clickItem(flag);
    }

    public String getFlag() {
        String input = flaggedInput.getText();
        return input.replace("Flagged\n", "");
    }

    public void closeFlagSelectBox() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
    }

    public String selectAnyLabelStartsWith(String text) {
        labelsInput.type(text);
        WaitUtil.waitNotEmptyListOfElements(getDriver(), SUGGESTION_XPATH);

        int randomIndex = RandomUtil.generateRandomNumber(0, suggestions.size() - 1);
        String label = suggestions.get(randomIndex).getText();
        suggestions.get(randomIndex).click();
        return label;
    }

    public boolean isLabelSelected(String label) {
        boolean condition = false;
        for (ExtendedWebElement e : selectedLabels) {
            if (e.getText().equals(label)) {
                condition = true;
                break;
            }
        }
        return condition;
    }

    public String selectAnyAssigneeStartWith(String text) {
        assigneeInput.type(text);
        WaitUtil.waitNotEmptyListOfElements(getDriver(), SUGGESTION_XPATH);

        int randomIndex = RandomUtil.generateRandomNumber(0, suggestions.size() - 1);
        String name = suggestions.get(randomIndex).getText();
        suggestions.get(randomIndex).click();
        return name;
    }

    public String getAssignedPerson() {
        return assigneeInput.getAttribute("value");
    }
}