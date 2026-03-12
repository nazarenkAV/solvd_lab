package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.launcher.DeleteLauncherAlertModal;
import com.zebrunner.automation.gui.launcher.DeleteLauncherModal;
import com.zebrunner.automation.gui.reporting.widget.SendByEmailWindow;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class BulkActionSection extends AbstractUIObject {

    @FindBy(xpath = ".//*[text()='Mark as Passed']//parent::button")
    private ExtendedWebElement markAsPassedButton;

    @FindBy(xpath = ".//*[text()='Mark as Failed']//parent::button")
    private ExtendedWebElement markAsFailedButton;

    @FindBy(xpath = ".//p[text() = '%s selected']")
    private ExtendedWebElement selectedTestNumber;

    @FindBy(xpath = ".//*[text()='Link issue']//parent::button")
    private ExtendedWebElement linkIssueButton;

    @FindBy(xpath = ".//*[text()='Delete']//parent::button")
    private Element bulkDeleteButton;

    @FindBy(xpath = ".//*[text()='Send as email']//parent::button")
    private Element bulkSendAsEmail;

    @FindBy(xpath = ".//*[text()='Assign to milestone']//parent::button")
    private Element bulkAssignToMilestone;

    @FindBy(xpath = ".//*[text()='Abort']//parent::button")
    private Element bulkAbortButton;

    @FindBy(xpath = ".//*[text()='Relaunch']//parent::button")
    private Element bulkRelaunch;

    @FindBy(xpath = ".//span[contains(@class, 'zbrClearSelectionText')]")
    private Element bulkClearSelection;

    @FindBy(xpath = ".//p")
    private Element bulkSelectedText;

    public BulkActionSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isMarkAsFailedClickable() {
        return markAsFailedButton.isClickable(2);
    }

    public boolean isMarkAsPassedClickable() {
        return markAsPassedButton.isClickable(2);
    }

    public boolean isLinkIssueButtonClickable() {
        return linkIssueButton.isClickable(2);
    }

    public boolean isSendAsEmailButtonPresent(int timeInSec) {
        return bulkSendAsEmail.isElementPresent(timeInSec);
    }

    public boolean isLinkIssueButtonPresent(int timeInSec) {
        return linkIssueButton.isElementPresent(timeInSec);
    }

    public boolean isAbortButtonPresent(int timeInSec) {
        return bulkSendAsEmail.isElementPresent(timeInSec);
    }

    public boolean isDeleteButtonPresent(int timeInSec) {
        return bulkDeleteButton.isElementPresent(timeInSec);
    }

    public boolean isAssignToMilestoneButtonPresent(int timeInSec) {
        return bulkAssignToMilestone.isElementPresent(timeInSec);
    }

    public boolean isRelaunchButtonPresent(int timeInSec) {
        return bulkRelaunch.isElementPresent(timeInSec);
    }

    public void clickMarkAsFailedButton() {
        markAsFailedButton.click();
    }

    public void clickMarkAsPassedButton() {
        markAsPassedButton.click();
    }

    public boolean isAmountOfSelectedTestsPresent(int number) {
        return selectedTestNumber.format(number).isElementPresent(3);
    }

    public LinkIssueModal clickLinkIssueButton() {
        linkIssueButton.click();
        return new LinkIssueModal(getDriver());
    }

    public AbortModal clickAbort() {
        bulkAbortButton.click();
        return new AbortModal(getDriver());
    }

    public void assignToMilestone(String milestoneName) {
        bulkAssignToMilestone.click();
        new AssignToMilestoneModalR(getDriver()).chooseMilestoneAndAssign(milestoneName);
    }

    public AssignToMilestoneModalR openAssignToMilestoneModal() {
        bulkAssignToMilestone.click();
        return new AssignToMilestoneModalR(getDriver());
    }

    public void unAssign() {
        bulkAssignToMilestone.click();
        new AssignToMilestoneModalR(getDriver()).unAssign();
    }

    public DeleteLauncherModal clickDelete() {
        bulkDeleteButton.click();
        return new DeleteLauncherModal(getDriver());
    }

    public void delete() {
        bulkDeleteButton.click();
        new DeleteLauncherAlertModal(getDriver()).acceptDeletingAlert();
    }

    public SendByEmailWindow clickBulkSendAsEmail() {
        bulkSendAsEmail.click();
        return new SendByEmailWindow(getDriver());
    }

    public void sendAsEmail(String email) {
        bulkSendAsEmail.click();
        new SendByEmailWindow(getDriver()).fillingEmailAndSubmit(email);
    }

    public void clearSelection() {
        bulkClearSelection.click();
    }

    public void relaunch() {
        bulkRelaunch.click();
        new RelaunchModal(getDriver()).clickRelaunchButton();
    }

    public RelaunchModal clickRelaunchButton() {
        bulkRelaunch.click();
        return new RelaunchModal(getDriver());
    }

    public String getSelectedCardsAmountText() {
        return bulkSelectedText.getText().replaceAll("\\s*\\|\\s*Clear\\s*selection", "");
    }
}
