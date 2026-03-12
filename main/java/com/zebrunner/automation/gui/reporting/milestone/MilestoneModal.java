package com.zebrunner.automation.gui.reporting.milestone;

import com.zebrunner.automation.gui.common.Calendar;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;

@Slf4j
@Getter
public class MilestoneModal extends AbstractModal<MilestoneModal> {
    public static final String MODAL_NAME = "Create milestone";

    @FindBy(xpath = ".//input[@id='Start date']//ancestor::div[contains(@class,'MuiInputBase-adornedEnd')]//*[local-name()='svg']")
    private Element clearStartDate;

    @FindBy(xpath = ".//input[@id='Due date']//ancestor::div[contains(@class,'MuiInputBase-adornedEnd')]//*[local-name()='svg']")
    private Element clearDueDate;

    @FindBy(id = "name")
    private Element milestoneNameInput;

    @FindBy(xpath = ".//input[@id='Start date']")
    private Element startDateInput;

    @FindBy(xpath = ".//input[@id='Due date']")
    private Element dueDateInput;

    @FindBy(xpath = "//textarea[@id='description']")
    private Element milestoneDescription;

    @FindBy(xpath = ".//h2[@class='modal-header__title ng-binding _warning']")
    private Element deleteWarningTitle;

    @FindBy(xpath = "//span[@class='input-message-animation item-enter-done']")
    private Element errorMessage;

    @FindBy(xpath = "//div[@class='MuiCalendarPicker-root css-1brzq0m']")
    private Calendar calendar;

    public MilestoneModal(WebDriver driver) {
        super(driver);
    }

    public void selectStartDate(String date, String day) {
        log.debug("Selecting start date " + date + " " + day);
        startDateInput.click();
        //        Month month = calendar.getMonth(date);
        //        month.selectDay(day);
    }

    public boolean isDateClickable(LocalDate date) {
        if (!calendar.isYearInList(date.getYear())) {
            log.info("Unable to select year " + date.getYear());
            return false;
        } else {
            calendar.selectYear(date.getYear());
            if (!calendar.isMonthSelected(date.getMonthValue())) {
                log.info("Unable to select month + " + date.getMonthValue());
                return false;
            } else {
                for (ExtendedWebElement day : calendar.getListDays()) {
                    String iteratedDayText = day.getText();

                    if (iteratedDayText.equals(Integer.toString(date.getDayOfMonth()))) {
                        return day.isClickable(3);
                    }
                }
            }
        }

        return false;
    }

    public boolean isDayClickableInStartDateCalendar(LocalDate startDate) {
        startDateInput.click();
        isDateClickable(startDate);

        return false;
    }

    public boolean isDayClickableInDueDateCalendar(LocalDate endDate) {
        dueDateInput.click();
        isDateClickable(endDate);

        return false;
    }

    public void selectEndDate(String date, String day) {
        log.debug("Selecting start date " + date + " " + day);
        dueDateInput.click();
        //        Month month = calendar.getMonth(date);
        //        month.selectDay(day);
    }

    public MilestoneModal typeMilestoneName(String milestoneName) {
        milestoneNameInput.sendKeys(milestoneName);
        return this;
    }

    public MilestoneModal typeDescription(String description) {
        milestoneDescription.sendKeys(description);
        return this;
    }

    public String getErrorMessageText() {
        return errorMessage.getText();
    }

    public void clickClearStartDate() {
        clearStartDate.click();
    }

    public void clickDueDateInput() {
        dueDateInput.click();
    }

    public void clickClearDueDate() {
        clearDueDate.click();
    }

    public void clickDeleteButton() {
        deleteButton.click();
    }

    public boolean isMilestoneNameInputPresent() {
        return milestoneNameInput.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isStartDateFieldPresent() {
        return startDateInput.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isDueDateFieldPresent() {
        return dueDateInput.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isMilestoneDescriptionFieldPresent() {
        return milestoneDescription.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isDeleteButtonActive() {
        return deleteButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isCancelButtonActive() {
        return cancelButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isDeleteWarningTitlePresent() {
        return deleteWarningTitle.isStateMatches(Condition.VISIBLE);
    }

    public boolean isErrorMessagePresent() {
        return errorMessage.isStateMatches(Condition.VISIBLE);
    }

    public boolean isSaveButtonActive() {
        return getSubmitButton().isStateMatches(Condition.CLICKABLE);
    }

    public MilestoneModal inputStartDate(LocalDate date) {
        startDateInput.click();
        calendar.selectDate(date);
        return this;
    }

    public MilestoneModal inputDueDate(LocalDate date) {
        dueDateInput.click();
        calendar.selectDate(date);
        return this;
    }

    public String getEnteredTitleText() {
        return milestoneNameInput.getAttributeValue("value");
    }

    public String getEnteredDescriptionText() {
        return milestoneDescription.getText();
    }
}
