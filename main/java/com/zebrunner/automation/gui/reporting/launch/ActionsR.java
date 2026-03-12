package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.launcher.DeleteLauncherAlertModal;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class ActionsR extends AbstractUIObject {

    @FindBy(xpath = ".//span[text()='Review' or text()='Reviewed' ]/parent::button")
    private Element review;

    @FindBy(xpath = ".//div[@aria-label='Share']/parent::button")
    private Element shareTestRun;

    @FindBy(xpath = "//div[@id='share-popover']")
    private ShareLaunchForm shareLaunchForm;

    @FindBy(xpath = ".//*[@d = '" + SvgPaths.ABORT + "']//ancestor::button")
    private ExtendedWebElement abortButton;

    @FindBy(xpath = "//button[contains(text(),'Abort')]")
    private ExtendedWebElement abortButtonFromPopUp;

    @FindBy(xpath = ".//*[@d = 'M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z']//ancestor::button")
    private ExtendedWebElement relaunchButton;

    @FindBy(xpath = ".//button[contains(@class, 'icon info-dark')]")
    private Element settings;

    @FindBy(xpath = "//ul[@role='menu']//li")
    private List<Element> testRunSettings;

    @FindBy(xpath = AssignToMilestoneModalR.ROOT_LOCATOR)
    private AssignToMilestoneModalR abstractModal;

    @FindBy(xpath = Menu.ROOT_LOCATOR)
    private Menu menu;

    public ActionsR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public ReviewModalR openReviewModal() {
        review.click();
        return ReviewModalR.getInstance(getDriver());
    }

    public ShareLaunchForm openShareFormModal() {
        shareTestRun.click();
        return shareLaunchForm;
    }

    public void hideLabels() {
        this.clickCertainTestRunResultSetting(Settings.HIDE_LABELS);
    }

    public void showLabels() {
        this.clickCertainTestRunResultSetting(Settings.SHOW_LABELS);
    }

    public AssignToMilestoneModalR assignToMilestone() {
        this.clickCertainTestRunResultSetting(Settings.ASSIGN_TO_MILESTONE);
        return abstractModal;
    }

    public DeleteLauncherAlertModal clickDelete() {
        this.clickCertainTestRunResultSetting(Settings.DELETE);
        return new DeleteLauncherAlertModal(getDriver());
    }

    public boolean isTestRunReviewed() {
        String colour = review.getElement().getCssValue("border-top-color");

        return Color.fromString(colour).asHex().equals("#44c480");
    }

    private void clickCertainTestRunResultSetting(Settings runResultSettings) throws NoSuchElementException {
        settings.waitUntil(Condition.CLICKABLE).click();
        WaitUtil.waitElementAppearedInListByCondition(
                        testRunSettings,
                        settings -> settings.getText().equalsIgnoreCase(runResultSettings.getName()),
                        "Test run settings with name " + runResultSettings + " was found",
                        "There are no run settings with name: " + runResultSettings.getName()
                )
                .click();
        settings.isDisappear();
    }

    private Menu openLaunchMenu() {
        relaunchButton.click();
        return menu;
    }

    public void openAndSelectLaunchAction(Menu.MenuItemEnum menuItemEnum) {
        this.openLaunchMenu()
            .findItem(menuItemEnum.getItemValue())
            .click();
    }

    public boolean isSettingsButtonPresent() {
        return settings.isStateMatches(Condition.PRESENT);
    }

    public void clickAbortButton() {
        abortButton.click();
        abortButtonFromPopUp.click();
    }

    public boolean isAbortButtonPresent() {
        return abortButton.isElementPresent(3);
    }

    @Getter
    @RequiredArgsConstructor
    private enum Settings {

        SHOW_LABELS("Show labels"),
        HIDE_LABELS("Hide labels"),
        ASSIGN_TO_MILESTONE("Assign to Milestone"),
        DELETE("Delete");

        private final String name;

    }

}
