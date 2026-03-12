package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class StatisticsR extends Element {

    @FindBy(xpath = ".//div[contains(@class, 'launch-statistics__label _success-border')]")
    private Element passed;

    @FindBy(xpath = ".//*[contains(@class, 'launch-statistics__label _failed-border')]")
    private Element failedAndKnownIssues;

    @FindBy(xpath = ".//*[contains(@class, 'launch-statistics__label _skipped-border')]")
    private Element skipped;

    public StatisticsR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getFailedIssues() {
        String failedAndKnown = failedAndKnownIssues.getText();
        String[] failed = failedAndKnown.split("\\D");
        return failed[0];
    }

    public String getKnownIssues() {
        String failedAndKnown = failedAndKnownIssues.getText();
        String[] known = failedAndKnown.split("\\D");
        return known[1];
    }

}