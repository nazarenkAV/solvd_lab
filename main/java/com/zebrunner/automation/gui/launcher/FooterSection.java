package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
@Slf4j
public class FooterSection extends AbstractUIObject {

    @FindBy(xpath = "//button[contains(text(),'ADD')]")
    private Element addButton;
    @FindBy(xpath = ".//button[text()='Delete launcher']")
    private Element deleteLauncherBtn;
    @FindBy(xpath = ".//*[text()='Launch another']//parent::*//input")
    private Element launchAnotherCheckbox;
    @FindBy(xpath = ".//button[text()='Save preset']")
    private Element savePresetBtn;
    @FindBy(xpath = ".//button[text()='Save']")
    private Element saveBtn;//when try to edit preset
    @FindBy(xpath = "//button[text()='Delete preset']")
    private Element deletePresetBtn;
    @FindBy(xpath = "//*[text()='Edit']//parent::button")
    private Element editPresetBtn;
    @FindBy(xpath = ".//button[text()='Launch']")
    private Element launchBtn;

    public FooterSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void clickEditButton() {
        editPresetBtn.click();
    }

    public boolean isEditButtonClickable() {
        editPresetBtn.waitUntil(Condition.VISIBLE);
        return editPresetBtn.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isLaunchButtonClickable() {
        launchBtn.waitUntil(Condition.VISIBLE);
        return launchBtn.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isSaveButtonClickable() {
        saveBtn.waitUntil(Condition.VISIBLE);
        return saveBtn.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isSavePresetButtonClickable() {
        savePresetBtn.waitUntil(Condition.VISIBLE);
        return savePresetBtn.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isAddButtonClickable() {
        addButton.waitUntil(Condition.VISIBLE);
        return addButton.isStateMatches(Condition.CLICKABLE);
    }

    public void clickLaunchButton() {
        launchBtn.waitUntil(Condition.VISIBLE);
        launchBtn.click();
        launchBtn.getRootExtendedElement().waitUntilElementDisappear(5);
    }

    public void clickAddButton() {
        log.info("Clicking 'Add' button...");
        addButton.waitUntil(Condition.CLICKABLE);
        addButton.click();
    }

    public void clickSavePresetButton() {
        log.info("Clicking 'Save preset' button...");
        savePresetBtn.waitUntil(Condition.CLICKABLE);
        savePresetBtn.click();
    }
}
