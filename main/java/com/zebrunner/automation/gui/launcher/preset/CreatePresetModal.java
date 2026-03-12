package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.automation.gui.common.ZbrSwitch;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@Slf4j
@Getter
public class CreatePresetModal extends AbstractModal<CreatePresetModal> {
    public static final String MODAL_NAME = "Create launcher preset";
    public static final String SCHEDULE_ITEM_XPATH = ".//div[@class='schedule-item']";

    @FindBy(xpath = ".//div[@class='modal-alert__text']")
    private Element modalAlertText;
    @FindBy(xpath = ".//input[@id='name']")
    private Element nameInput;
    @FindBy(xpath = ZbrSwitch.ROOT_XPATH)
    private ZbrSwitch scheduleCheckbox;
    @FindBy(xpath = SCHEDULE_ITEM_XPATH)
    private List<ScheduleItem> scheduleItems;
    @FindBy(xpath = ".//*[text()='Add schedule']//parent::button")
    private Element addScheduleBtn;

    public CreatePresetModal(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CreatePresetModal(WebDriver driver) {
        super(driver);
    }

    public static CreatePresetModal getModalInstance(WebDriver driver) {
        return new CreatePresetModal(driver);
    }

    public CreatePresetModal typePresetName(String presetName) {
        nameInput.sendKeys(presetName);
        return this;
    }

    public Boolean isScheduleCheckboxActive() {
        pause(1); //time for the switcher changes
        return scheduleCheckbox.isSwitched();
    }

    public CreatePresetModal clickScheduleCheckbox() {
        scheduleCheckbox.click();
        return this;
    }

    public CreatePresetModal addSchedules(Integer scheduleNumbers) {
        for (int i = 0; i < scheduleNumbers; i++) {
            addScheduleBtn.click();
            pause(1);
        }
        return this;
    }

    public ScheduleItem addSchedule() {
        addScheduleBtn.click();
        return scheduleItems.get(scheduleItems.size() - 1);
    }

    public CreatePresetModal addSchedule(String cron) {
        addSchedule().typeCronExpression(cron);
        return this;
    }

    public List<ScheduleItem> getScheduleItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(SCHEDULE_ITEM_XPATH), 0), 3);
        return scheduleItems;
    }

    public void changeFirstScheduleItemCron(String cron) {
        getScheduleItems().get(0).typeCronExpression(cron);
    }
}
