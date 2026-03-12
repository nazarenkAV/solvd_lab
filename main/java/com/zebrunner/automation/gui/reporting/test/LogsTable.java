package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.ComponentUtil;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Getter
public class LogsTable extends AbstractUIObject {

    @FindBy(xpath = ".//div[starts-with(@id, 'log-') and contains(@class, 'row')]")
    private List<LogRow> logRows;

    @FindBy(xpath = ".//div[contains(@class, '_visuals') and contains(@class, 'table-col')]//img//ancestor::div[starts-with(@id, 'log-') and contains(@class, 'row')]")
    private List<LogRow> logRowsWithScreenshots;

    @FindBy(xpath = ".//span[text()='Level']/ancestor::button")
    private Element filterLogLevel;

    @FindBy(xpath = "//ul[@aria-labelledby='level-menu-button']")
    private LogLevelsMenu logLevelsMenu;

    @FindBy(xpath = ".//div[@class='test-details__tab-table-row _empty']")
    private Element nologsText;

    public LogsTable(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<List<LogRow>> getLogs() {

        return WaitUtil.waitComponentList(logRows);
    }

    public Optional<List<LogRow>> getLogsWithScreenshots() {
        return WaitUtil.waitComponentList(logRowsWithScreenshots);
    }

    public void filterLogsBy(LogLevel logLevel) {
        log.info("Filtering log by " + logLevel + " level");
        ComponentUtil.closeAnyMenuOrModal(getDriver());
        filterLogLevel.click();
        logLevelsMenu.chooseLogLevel(logLevel);
        // Wait until filter menu by log level disappear
        pause(2);
    }

    public LogRow findLogWithMessageContaining(String partOfLogMessage) {
        log.info("Finding log with message containing " + partOfLogMessage);
        return getLogs()
                .orElseThrow(() -> new NoSuchElementException("No logs at all!"))
                .stream()
                .filter(logRow ->
                        logRow.getMessage(false).contains(partOfLogMessage))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No logs found with a message containing " + partOfLogMessage));

    }

}
