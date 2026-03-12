package com.zebrunner.automation.gui.tcm.testrun;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.List;
import java.util.NoSuchElementException;

import com.zebrunner.automation.gui.common.ZbrAutocompleteInput;
import com.zebrunner.automation.gui.tcm.ConfigGroup;
import com.zebrunner.automation.gui.tcm.CreateConfigurationGroupModal;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.tcm.testrun.testcase.SelectTestCasesModal;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class CreateTestRunPage extends TenantProjectBasePage {

    @FindBy(id = "title")
    private Element titleInput;
    @FindBy(id = "description")
    private ExtendedWebElement descriptionInput;

    @FindBy(xpath = "//div[text()='Environment']//parent::" + ZbrAutocompleteInput.ROOT_XPATH)
    private ZbrAutocompleteInput environments;
    @FindBy(xpath = "//div[text()='Milestone']//parent::" + ZbrAutocompleteInput.ROOT_XPATH)
    private ZbrAutocompleteInput milestones;

    @FindBy(xpath = "//button[contains(@class, 'testing-config__add-button')]")
    private ExtendedWebElement addConfigurationButton;

    @FindBy(xpath = "//span[contains(text(),'Add cases')]")
    private ExtendedWebElement addTestCasesButton;

    @FindBy(xpath = "//div[@class='test-cases-selected__info']//b")
    private ExtendedWebElement linkedTestCasesInfo;

    @FindBy(xpath = "//span[contains(text(),'change selection')]")
    private ExtendedWebElement changeLinkedTestCasesButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[contains(text(),'CANCEL')]")
    private ExtendedWebElement cancelButton;
    @CaseInsensitiveXPath
    @FindBy(xpath = "//button[contains(text(),'CREATE')]")
    private ExtendedWebElement createButton;

    public CreateTestRunPage(WebDriver driver) {
        super(driver);
        super.setUiLoadedMarker(titleInput);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getTitleInputValue() {
        return titleInput.getAttribute("value");
    }

    public String getDescriptionInputText() {
        return descriptionInput.getText();
    }

    public String getLinkedTestCasesNumber() {
        return linkedTestCasesInfo.getText().replaceAll("\\D", "");
    }

    public CreateTestRunPage selectEnvironment(String environmentName) {
        environments.selectValue(environmentName);
        return this;
    }

    public CreateTestRunPage selectMilestone(String milestoneName) {
        milestones.selectValue(milestoneName);
        return this;
    }

    public boolean isAddConfigurationButtonClickable() {
        return addConfigurationButton.isClickable(3);
    }

    public boolean isAddTestCasesButtonVisible() {
        return addTestCasesButton.isVisible(3);
    }

    public boolean isChangeLinkedTestCasesButtonVisible() {
        return changeLinkedTestCasesButton.isVisible(3);
    }

    public boolean isCreateButtonClickable() {
        return createButton.isClickable(3);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CreateTestRunPage inputTitle(String title) {
        titleInput.waitUntil(Condition.VISIBLE).sendKeys(title);
        return this;
    }

    public void inputDescription(String description) {
        descriptionInput.type(description);
    }

    public TestRunPage clickCreateButton() {
        createButton.click();
        return new TestRunPage(super.getDriver());
    }

    public TestRunsGridPage clickCancelButton() {
        cancelButton.click();
        super.pause(2);

        return new TestRunsGridPage(super.getDriver());
    }

    public SelectConfigurationDialog clickAddConfigurationButton() {
        addConfigurationButton.click();

        return new SelectConfigurationDialog(super.getDriver());
    }

    public SelectTestCasesModal clickAddTestCaseButton() {
        addTestCasesButton.click();
        return new SelectTestCasesModal(getDriver());
    }

    public SelectTestCasesModal clickChangeLinkedTestCasesButton() {
        changeLinkedTestCasesButton.click();

        return new SelectTestCasesModal(getDriver());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class SelectConfigurationDialog extends AbstractUIObject {

        @FindBy(xpath = ".//div[@class='create-run-select-conf-modal__group-list']/div")
        private List<ConfigGroup> configGroups;

        @FindBy(xpath = ".//button[contains(@class, 'main-modal__close-icon')]")
        private ExtendedWebElement closeButton;
        @FindBy(xpath = ".//div[@class='create-run-select-conf-modal__group-list']//button")
        private ExtendedWebElement addConfigurationGroupButton;
        @FindBy(xpath = ".//button[text()='Cancel']")
        private ExtendedWebElement cancelButton;
        @CaseInsensitiveXPath
        @FindBy(xpath = ".//button[contains(text(),'DONE')]")
        private ExtendedWebElement doneButton;

        public SelectConfigurationDialog(WebDriver driver) {
            super(driver);
            super.setBy(By.xpath("//div[@aria-describedby='modal-dialog-content'][./div[@class='create-run-select-conf-modal']]"));
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public ConfigGroup selectGroup(String groupName) {
            ConfigGroup group = this.getGroup(groupName);

            return group.clickNameLabel();
        }

        public ConfigGroup getGroup(String groupName) {
            WaitUtil.waitElementAppearedInListByCondition(
                    configGroups,
                    configGroup -> configGroup.getGroupName().equalsIgnoreCase(groupName),
                    "Group with name " + groupName + " was found",
                    "Group with name " + groupName + " was not found"
            );

            return StreamUtils.findFirst(
                                      configGroups,
                                      group -> group.getGroupName().equalsIgnoreCase(groupName)
                              )
                              .orElseThrow(() -> new NoSuchElementException("Group '" + groupName + "' not found"));
        }

        public boolean isOpened() {
            return super.isUIObjectPresent(10);
        }

        public boolean isAddConfigurationGroupButtonClickable() {
            return addConfigurationGroupButton.isClickable(3);
        }

        public boolean isGroupExist(String groupName) {
            return WaitUtil.waitAndCheckElementAppearedInListByCondition(
                    configGroups,
                    configGroup -> configGroup.getGroupName().trim().equalsIgnoreCase(groupName)
            );
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public void clickCloseButton() {
            closeButton.click();
        }

        public void clickCancelButton() {
            cancelButton.click();
        }

        public CreateTestRunPage clickDoneButton() {
            doneButton.click();
            return new CreateTestRunPage(getDriver());
        }

        public CreateConfigurationGroupModal clickCreateConfigurationGroup() {
            addConfigurationGroupButton.click();

            return new CreateConfigurationGroupModal(super.getDriver());
        }

        public void createNewConfigGroupAndOption(String newGroupName, String newOptionName) {
            this.createNewConfigGroup(newGroupName);
            this.createNewOptionInGroup(newGroupName, newOptionName);
        }

        public void createNewConfigGroup(String groupName) {
            addConfigurationGroupButton.click();

            CreateConfigurationGroupModal createGroupModal = new CreateConfigurationGroupModal(getDriver());
            createGroupModal.inputTitle(groupName);

            createGroupModal.submitModal();
        }

        private void createNewOptionInGroup(String groupName, String newOptionName) {
            this.selectGroup(groupName)
                .createOption(newOptionName);
        }

    }

}
