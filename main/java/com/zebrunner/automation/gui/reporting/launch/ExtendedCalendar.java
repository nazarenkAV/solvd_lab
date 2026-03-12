package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.Calendar;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class ExtendedCalendar extends Calendar {
    public static final String ROOT_LOCATOR = "//div[contains(@class,'date-range-picker__container')]";

    @FindBy(xpath = ".//span[@class='rdrYearPicker']//select")
    private ExtendedWebElement yearPickerDropdown;

    @FindBy(xpath = ".//span[@class='rdrMonthPicker']//select")
    private ExtendedWebElement monthPickerDropdown;

    @FindBy(xpath = ".//span[@class='rdrMonthPicker']//select/option[@value='%s']")
    private ExtendedWebElement selectMonthValue;

    @FindBy(xpath = ".//div[@class='date-range-picker__controls']/button")
    private ExtendedWebElement okButton;

    @FindBy(xpath = ".//button[not(contains(@class, 'rdrDayPassive')) and @type='button' and span[@class='rdrDayNumber']]")
    private List<ExtendedWebElement> listDays;

    public ExtendedCalendar(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void selectDateCustom(LocalDate date) {
        selectYearValue(date.getYear());
        selectMonthValue(date.getMonthValue());
        selectDayValue(date.getDayOfMonth());
    }

    public void selectBetweenDate(LocalDate startDate, LocalDate endDate) {
        selectDateCustom(startDate);
        selectDateCustom(endDate);
        okButton.click();
    }

    public void clickOkButton(){
        okButton.click();
    }

    public void selectYearValue(int yearValue) {
        yearPickerDropdown.select(String.valueOf(yearValue));
    }

    public void selectMonthValue(int monthValue) {
        monthPickerDropdown.click();
        selectMonthValue.format(monthValue-1).click();
    }

    public void selectDayValue(int dayToSelect) {
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

}
