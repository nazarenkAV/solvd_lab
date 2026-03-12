package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

//div[contains(@class,'zeb-page-header__actions')]
@Deprecated
public class Actions extends AbstractUIObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String REVIEW_BUTTON_COLOR_REVIEWED = "#44c480";

    @FindBy(xpath = ".//md-icon[@md-svg-icon='searchIcon']/following-sibling::span")
    private Element review;

    @FindBy(xpath = ".//button[@aria-label='Share test run']")
    private Element shareTestRun;

    @FindBy(xpath = ".//button[@aria-label='Test run rebuild']")
    private Element rebuild;

    @FindBy(xpath = ".//*[@aria-label='User settings']")
    private Element settings;

    @FindBy(xpath = "//md-menu-content[@class='fixed-md-menu-content test-details__menu-content']//button")
    private List<Element> testRunSettings;

    public Actions(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }


    public Actions hideLabels() {
        LOGGER.info("Hiding test labels");
        clickCertainTestRunResultSetting(Settings.HIDE_LABELS);
        return this;
    }

    public Actions showLabels() {
        LOGGER.info("Showing test labels");
        clickCertainTestRunResultSetting(Settings.SHOW_LABELS);
        return this;
    }

    public boolean isTestRunReviewed() {
        LOGGER.info("Checking is test run already reviewed");
        String color = "should be refactored";
        return Color.fromString(color).asHex().equals(REVIEW_BUTTON_COLOR_REVIEWED);
    }

    private void clickCertainTestRunResultSetting(Settings runResultSettings) throws NoSuchElementException {
        settings.waitUntil(Condition.CLICKABLE).click();
        WaitUtil.waitElementAppearedInListByCondition(testRunSettings,
                        settings -> settings.getText().equalsIgnoreCase(runResultSettings.getName()),
                        "Test run settings with name " + runResultSettings + " was found",
                        "There are no run settings with name: " + runResultSettings.getName())
                .click();
        settings.isDisappear();
    }

    private enum Settings {
        SHOW_LABELS("Show labels"), HIDE_LABELS("Hide labels"), ASSIGN_TO_MILESTONE("Assign to Milestone");

        private String name;

        Settings(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
