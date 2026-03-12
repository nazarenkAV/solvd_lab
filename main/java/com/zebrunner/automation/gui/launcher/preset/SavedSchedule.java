package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class SavedSchedule extends AbstractUIObject {
    @FindBy(xpath = ".//div[text()='Cron']/following-sibling::div[@class='schedule-value']")
    private Element cron;

    @FindBy(xpath = ".//div[text()='Timezone']/following-sibling::div[@class='schedule-value']")
    private Element timezone;

    @FindBy(xpath = ".//span[@aria-label='Resume schedule']")
    private Element resumeBtn;

    @FindBy(xpath = ".//span[@aria-label='Pause schedule']")
    private Element pauseBtn;

    public SavedSchedule(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getCron() {
        return cron.getText();
    }

    public String getTimezone() {
        return timezone.getText();
    }

    public void clickResume() {
        resumeBtn.click();
        resumeBtn.getRootExtendedElement().waitUntilElementDisappear(5);
    }

    public void clickPause() {
        pauseBtn.click();
        pauseBtn.getRootExtendedElement().waitUntilElementDisappear(5);
    }

    public boolean isPaused() {
        return !pauseBtn.isStateMatches(Condition.VISIBLE);
    }

    public boolean isResumed() {
        return !resumeBtn.isStateMatches(Condition.VISIBLE);
    }
}
