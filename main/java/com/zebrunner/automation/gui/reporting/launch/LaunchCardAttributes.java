package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
@Slf4j
public class LaunchCardAttributes extends AbstractUIObject {

    @FindBy(xpath = ".//*[@d='" + SvgPaths.MILESTONE
            + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    protected Element milestone;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.MILESTONE + "']")
    protected ExtendedWebElement milestoneIcon;

    @FindBy(xpath = ".//div[@class='attribute-label _maxWidth-270px']")
    protected Element stateAndTimeFromStart;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.DURATION + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    protected Element duration;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.DURATION + "']")
    protected ExtendedWebElement durationIcon;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')]")
    protected Element launchedBy;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public LaunchCardAttributes(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isLaunchFinished() {
        return duration.isStateMatches(Condition.PRESENT);
    }

    public String getMilestoneName() {
        log.info("Getting milestone name...");
        return milestone.getText();
    }

    public String getTime() {
        log.info("Getting time ...");
        return stateAndTimeFromStart.getText();
    }

    public String getDurationTime() {
        log.info("Getting duration ...");
        return duration.getText();
    }

    public UserInfoTooltip hoverUsername() {
        launchedBy.hover();
        return new UserInfoTooltip(getDriver());
    }

    public String hoverLaunchedTimeAndGetToolTipValue() {
        stateAndTimeFromStart.hover();
        pause(1);
        return tooltip.getTooltipText();
    }

    public String hoverDurationIconAndGetToolTipValue() {
        durationIcon.hover();
        pause(1);
        return tooltip.getTooltipText();
    }

    public String hoverMilestoneIconAndGetToolTipValue() {
        milestoneIcon.hover();
        pause(1);
        return tooltip.getTooltipText();
    }
}
