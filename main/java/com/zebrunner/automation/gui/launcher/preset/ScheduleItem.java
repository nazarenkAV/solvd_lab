package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

@Getter
public class ScheduleItem extends AbstractUIObject {
    @FindBy(xpath = ".//input[@id='cron']")
    private Element cronInput;
    @FindBy(xpath = ".//label[@for='cron']//parent::div[contains(@class,'input-container _with-label')]//span[contains(@class,'input-message-animation item-enter-done')]")
    private Element cronInputError;
    @FindBy(xpath = ".//div[@id='select-Timezone']")
    private Element selectedTimezone;
    @FindBy(xpath = "//ul[@role='listbox']//li")
    private List<Element> timezoneList;
    @FindBy(xpath = ".//button[contains(@class,'MuiButton-disableElevation')]")
    private Element deleteBtn;

    public ScheduleItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void clickTimeZone() {
        selectedTimezone.click();
    }

    public String selectAnyTimeZone() {
        clickTimeZone();
        pause(1);
        Optional<Element> timezone = timezoneList.stream().findAny();
        if (timezone.isPresent()) {
            String timeZoneValue = timezone.get().getText();
            timezone.get().click();
            return timeZoneValue;
        } else
            return "Time zones not appeared!";
    }

    public String getCronErrorMessage() {
        cronInputError.waitUntil(Condition.VISIBLE);
        return cronInputError.getText();
    }

    public void typeCronExpression(String cron) {
        cronInput.sendKeys(cron);
    }

    public void clickDelete() {
        deleteBtn.click();
        pause(1);
    }
}
