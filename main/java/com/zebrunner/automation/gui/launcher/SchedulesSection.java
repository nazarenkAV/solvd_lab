package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.launcher.preset.SavedSchedule;
import com.zebrunner.automation.gui.launcher.preset.ScheduleItem;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@Getter
public class SchedulesSection extends AbstractUIObject {
    public final String SAVED_SCHEDULES_XPATH = ".//div[@class='schedule-container']";

    @FindBy(xpath = ".//input[contains(@class,'css-1m9pwf3')]//parent::span")
    private Element scheduleCheckbox;
    @FindBy(xpath = ".//div[@class='schedule-item']")
    private List<ScheduleItem> scheduleItems;

    @FindBy(xpath = SAVED_SCHEDULES_XPATH)
    private List<SavedSchedule> savedSchedules;
    @FindBy(xpath = "//*[text()='Add schedule']//parent::button")
    private Element addScheduleBtn;
    @FindBy(xpath = ".//*[text()='More']")
    private Element moreBtn;
    @FindBy(xpath = ".//*[text()='Less']")
    private Element lessBtn;

    public SchedulesSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }


    public SchedulesSection clickScheduleCheckbox() {
        scheduleCheckbox.click();
        return this;
    }

    public SchedulesSection clickAddScheduleButton() {
        addScheduleBtn.click();
        return this;
    }

    public SchedulesSection addSchedules(Integer scheduleNumbers) {
        for (int i = 0; i < scheduleNumbers; i++) {
            addScheduleBtn.click();
            pause(1);
        }
        return this;
    }

    public SchedulesSection clickMore() {
        moreBtn.click();
        moreBtn.getRootExtendedElement().waitUntilElementDisappear(3);
        pause(1);//time to page load schedules
        return this;
    }

    public SchedulesSection clickLess() {
        lessBtn.click();
        lessBtn.getRootExtendedElement().waitUntilElementDisappear(3);
        pause(1);//time to page load schedules
        return this;
    }

    public boolean isSchedulesCheckboxChecked() {
        return scheduleCheckbox.getAttributeValue("class").contains("Mui-checked");
    }

    public List<SavedSchedule> getSavedSchedules(){
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(SAVED_SCHEDULES_XPATH), 0), 3);
        return savedSchedules;
    }
}
