package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
@Slf4j
public class Calendar extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//div[contains(@class,'MuiPickersPopper-root')]";
    public static final String ROOT_LOCATOR_FOR_FILTERS = "//div[contains(@class,'dropdown__date-popper')]";

    @FindBy(xpath = ROOT_LOCATOR)
    private ExtendedWebElement calendar;

    @FindBy(xpath = "//button[contains(@class,'MuiPickersDay-dayWithMargin')]")
    private List<ExtendedWebElement> listDays;

    @FindBy(xpath = "//button[contains(@class, 'MuiPickersCalendarHeader-switchViewButton')]")
    private ExtendedWebElement buttonOpenYearsList;

    @FindBy(xpath = "//div[contains(@class, 'PrivatePickersYear-root')]")
    private List<ExtendedWebElement> listYears;

    @FindBy(xpath = "//button[@aria-label='Next month']")
    private ExtendedWebElement switchToNextMonthButton;

    @FindBy(xpath = "//button[@aria-label='Previous month']")
    private ExtendedWebElement switchToPreviousMonthButton;

    @FindBy(xpath = "//div[@class='MuiPickersCalendarHeader-label css-1v994a0']")
    private ExtendedWebElement currentDisplayedMonthAndYear;

    @FindBy(xpath = "//div[contains(@class, 'MuiDialogActions-root')]//span[contains(@class, 'MuiTouchRipple-root')]")
    private ExtendedWebElement todayButton;


    public Calendar(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void selectDate(LocalDate date) {
        selectYear(date.getYear());
        isMonthSelected(date.getMonthValue());
        selectDay(date.getDayOfMonth());
    }

    public void selectYear(int yearToSelect) {
        buttonOpenYearsList.click();
        WaitUtil.waitCheckListIsNotEmpty(listYears);
        for (ExtendedWebElement yearElement : listYears) {
            String yearText = yearElement.getText();
            if (yearText.equals(Integer.toString(yearToSelect))) {
                yearElement.click();
                return;
            }
        }

        throw new NoSuchElementException("Year " + yearToSelect + " isn't found in the list");
    }

    public boolean isMonthSelected(int monthToSelect) {
        String currentDisplayedMonth = currentDisplayedMonthAndYear.getText().replaceAll("\\d", "").trim();
        int numberOfDisplayedMonth = converterMonthToNumberOfMonth(currentDisplayedMonth);

        if (monthToSelect < numberOfDisplayedMonth) {
            int numberOfClicks = numberOfDisplayedMonth - monthToSelect;
            for (int i = 0; i < numberOfClicks; i++) {
                if (isPreviousMonthClickable()) {
                    log.info("Try to move to previous month");
                    switchToPreviousMonthButton.click();
                } else {
                    log.error("Unable to move to previous month");
                    return false;
                }
            }
        } else if (monthToSelect > numberOfDisplayedMonth) {
            int numberOfClicks = monthToSelect - numberOfDisplayedMonth;
            for (int i = 0; i < numberOfClicks; i++) {
                if (isNextMonthClickable()) {
                    log.info("Try to move to next month");
                    switchToNextMonthButton.click();
                } else {
                    log.error("Unable to move to next month");
                    return false;
                }
            }
        }

        currentDisplayedMonth = currentDisplayedMonthAndYear.getText().replaceAll("\\d", "").trim();
        numberOfDisplayedMonth = converterMonthToNumberOfMonth(currentDisplayedMonth);
        if (numberOfDisplayedMonth != monthToSelect) {
            throw new IllegalArgumentException("Selected month isn't equals to expected result");
        }

        return true;
    }

    public void selectDay(int dayToSelect) {
        WaitUtil.waitCheckListIsNotEmpty(listDays);
        for (ExtendedWebElement day : listDays) {
            String iteratedDayText = day.getText();

            if (iteratedDayText.equals(Integer.toString(dayToSelect))) {
                day.click();
                return;
            }
        }

        throw new NoSuchElementException("Day " + dayToSelect + " isn't found in the list");
    }

    public boolean isDayClickable(LocalDate date) {
        selectYear(date.getYear());
        isMonthSelected(date.getMonthValue());
        for (ExtendedWebElement day : listDays) {
            String iteratedDayText = day.getText();

            if (iteratedDayText.equals(Integer.toString(date.getDayOfMonth()))) {
                log.info("Day " + date.getDayOfMonth() + " found in the list!");
                return day.isClickable();
            }
        }

        log.error("Day " + date.getDayOfMonth() + " isn't found in the list");
        return false;
    }

    public boolean isYearInList(int year) {
        buttonOpenYearsList.click();
        String yearToString = Integer.toString(year);
        for (ExtendedWebElement yearElement : listYears) {
            String yearText = yearElement.getText();
            if (yearText.equals(yearToString)) {
                buttonOpenYearsList.click();
                log.info("Year " + yearToString + " found in the list!");
                return true;
            }
        }

        log.error("Year " + yearToString + " isn't found in the list");
        return false;
    }

    public boolean isNextMonthClickable() {
        boolean isClickable = switchToNextMonthButton.isClickable(3);
        log.info("Is switch to next month button clickable: " + isClickable);
        return isClickable;
    }

    public boolean isPreviousMonthClickable() {
        boolean isClickable = switchToPreviousMonthButton.isClickable(3);
        log.info("Is switch to previous month button clickable: " + isClickable);
        return isClickable;
    }

    public int converterMonthToNumberOfMonth(String month) {
        String[] months = {
                "january", "february", "march", "april", "may", "june",
                "july", "august", "september", "october", "november", "december"
        };

        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(month.trim())) {
                return i + 1;
            }
        }

        throw new IllegalArgumentException("Invalid entered month " + month);
    }

    public boolean isTodayButtonPresent() {
        return todayButton.isElementPresent();
    }

    public boolean isCalendarOpened() {
        return calendar.isElementPresent();
    }
}