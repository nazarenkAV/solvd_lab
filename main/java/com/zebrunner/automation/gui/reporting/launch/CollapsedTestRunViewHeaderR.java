package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

@Slf4j
@Getter
public class CollapsedTestRunViewHeaderR extends LaunchHeader {
    public static final String ELEMENT_NAME = "Collapsed header";

    @FindBy(xpath = ".//*[@d='" + SvgPaths.MILESTONE
            + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    protected Element milestone;

    @FindBy(xpath = ".//div[@class='attribute-label _maxWidth-270px']")
    protected Element stateAndTimeFromStart;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.DURATION
            + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    protected Element durationTime;

    @FindBy(xpath = ".")
    private ResultStatus status;

    @FindBy(xpath = ".//div[contains(@class, 'labels-group _dropdown')]")
    private Element cardConfigurationDropDownButton;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')]")
    private Element launchedBy;

    public CollapsedTestRunViewHeaderR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CollapsedTestRunViewHeaderR assertCollapsedTestRunViewElementPresence(SoftAssert softAssert) {
        log.info("Checking collapsed test details...");
        softAssert.assertTrue(isMilestonePresent(), "Milestone should be present on collapsed header!");
        //  softAssert.assertTrue(isEnvPresent(), "Env should be present on collapsed header!");
        softAssert.assertTrue(isLaunchStatisticsPresent(), "Run statistic should be present on collapsed header!");
        //  softAssert.assertEquals(getPlatform().getPlatformType(), PlatformTypeR.ND, "Platform should be present on collapsed header!");

        softAssert.assertTrue(isLaunchTimePresent(), "Time should be present on collapsed header!");
        softAssert.assertTrue(isDurationPresent(), "Duration should be present on collapsed header!");
        softAssert.assertFalse(isPassRateChartPresent(), "Pass rate chart should not be present on collapsed header!");
        return this;
    }

    public boolean isMilestonePresent() {
        return milestone.isStateMatches(Condition.VISIBLE);
    }

    public String getMilestoneName() {
        return milestone.getText();
    }

    public boolean isDurationPresent() {
        log.info("Checking for a presence of duration...");
        return durationTime.isStateMatches(Condition.VISIBLE);
    }

    public boolean isLaunchTimePresent() {
        log.info("Checking for a presence of launch time...");
        return stateAndTimeFromStart.isStateMatches(Condition.VISIBLE);
    }

    public CollapsedLaunchCardConfiguration openConfiguration() {
        cardConfigurationDropDownButton.click();
        return new CollapsedLaunchCardConfiguration(getDriver());
    }

    public UserInfoTooltip hoverUsername() {
        launchedBy.hover();
        return new UserInfoTooltip(getDriver());
    }
}