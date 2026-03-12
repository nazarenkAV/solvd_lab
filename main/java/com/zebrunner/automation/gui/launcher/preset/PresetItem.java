package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Getter
@Slf4j
public class PresetItem extends AbstractUIObject {
    @FindBy(xpath = ".//div[contains(@class,'launcher-tree__item-preset-text')]")
    private Element presetName;

    @FindBy(xpath = ".//*[@class='launcher-tree__item-preset-icon']")
    private Element scheduleIcon;

    public PresetItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getPresetName() {
        return presetName.getText().trim();
    }

    public boolean isScheduleIconPresent() {
        log.info("Checking schedule icon ...");
        waitUntil(ExpectedConditions.visibilityOf(scheduleIcon.getElement()), 3);
        return scheduleIcon.isStateMatches(Condition.VISIBLE);
    }
}
