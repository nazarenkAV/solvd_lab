package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import java.util.List;

@Slf4j
@Getter
public class ExpandedTestRunViewHeaderR extends LaunchHeader {
    @FindBy(xpath = "(.//button[contains(@class, 'launch-card__label-with-dropdown')])[1]")
    private ExtendedWebElement runAttachments;

    @FindBy(xpath = ".//div[contains(@class, 'labels-group')]")
    private LaunchCardConfiguration launchCardConfiguration;

    @FindBy(xpath = "//ul[@role='menu']//div[@class='custom-label__text-content']")
    private List<ExtendedWebElement> links;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')]")
    private Element launchedBy;

    public ExpandedTestRunViewHeaderR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public ExpandedTestRunViewHeaderR assertExpandedTestRunVDetailsElementsPresence(SoftAssert softAssert) {
        log.info("Checking expanded test run details...");
        softAssert.assertTrue(isMilestonePresent(), "Milestone should be present on expanded test run details!");
        ///  softAssert.assertTrue(isEnvPresent(), "Env should be present on expanded header!");
        softAssert.assertTrue(isStatisticsChartPresent(), "Statistics chart should be present on expanded header!");
        softAssert.assertTrue(isPassRateChartPresent(), "Pass rate chart should be present on expanded header!");

        softAssert.assertTrue(isLabelIconPresent(), "Labels should be present on expanded header!");
        softAssert.assertTrue(isArtifactIconPresent(), "Artifacts should be present on expanded header!");
        softAssert.assertTrue(isLinksPresent(), "Links should be present on expanded header!");

        softAssert.assertFalse(isLaunchStatisticsPresent(), "Run statistic should not be present on expanded header!");
        //  softAssert.assertEquals(getPlatform().getPlatformType(), PlatformTypeR.ND, "Platform should be present on expanded header!");

        softAssert.assertTrue(isLaunchTimePresent(), "Time should be present on expanded header!");
        softAssert.assertTrue(isDurationPresent(), "Duration should be present on expanded header!");
        return this;
    }

    public boolean isLinkPresentInList(String linkName) {
        log.info("Waiting links.....");
        pause(2);
        for (ExtendedWebElement el : links) {
            log.info("Link with name {} :", el.getText());
            if (el.getText().equalsIgnoreCase(linkName)) {
                log.info("Link with name {} was found!", linkName);
                return true;
            }
        }
        log.info("Link with name {} was not found!", linkName);
        return false;
    }

    public boolean isLinksPresent() {
        log.info("Checking run links ...");
        return runAttachments.isPresent(3);
    }

    public void clickLinks() {
        log.info("Getting run links...");
        ComponentUtil.pressEscape(getDriver());
        pause(2);
        runAttachments.click(2);
    }

    public PlatformR getPlatform() {
        return launchCardConfiguration.getPlatform();
    }

    public Browser getBrowser() {
        return launchCardConfiguration.getBrowser();
    }

    public UserInfoTooltip hoverUsername() {
        launchedBy.hover();
        return new UserInfoTooltip(getDriver());
    }
}