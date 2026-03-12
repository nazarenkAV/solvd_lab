package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.helper.IPageDataHelper;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class TestRunResultCardSettingsR extends AbstractUIObject implements IPageDataHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(xpath = ".//li[contains(text(),'Mark as passed')]")
    private Element markAsPassedButton;

    @FindBy(xpath = ".//li[contains(text(),'Mark as failed')]")
    private Element markAsFailedButton;

    @FindBy(xpath = ".//button[contains(text(),'Link issue')]")
    private Element linkIssueButton;

    public TestRunResultCardSettingsR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void markAsPassed() {
        LOGGER.info("Trying to mark test {} as passed...", getTitle());
        markAsPassedButton.click();
        WaitUtil.waitForAlert(getDriver()).accept();
        pause(2); // wait until page reload
    }

    public void markAsFailed() {
        LOGGER.info("Trying to mark test {} as failed...", getTitle());
        markAsFailedButton.waitUntil(Condition.CLICKABLE);
        markAsFailedButton.click();
        WaitUtil.waitForAlert(getDriver()).accept();
        pause(2); // wait until page reload
    }


    public boolean isMarkAsPassedButtonPresent() {
        return markAsPassedButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isMarkAsFailedButtonPresent() {
        boolean present = markAsFailedButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
        markAsFailedButton.click();
        WaitUtil.waitForAlert(getDriver()).dismiss();
        pause(2); // wait until page reload
        return present;
    }
}
