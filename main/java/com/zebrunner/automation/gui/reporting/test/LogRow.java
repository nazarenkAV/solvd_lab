package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

import java.util.Optional;

@Slf4j
@Getter
public class LogRow extends AbstractUIObject {
    public static final String YELLOW_BACKGROUND_COLOR_OF_ACTIVE_LOG = "#feffe4";

    @FindBy(xpath = ".//div[contains(@class, '_visuals') and contains(@class, 'table-col')]//img")
    private Element screenshot;

    @FindBy(xpath = ".//div[contains(@class, '_time') and contains(@class, 'table-col')]")
    private Element timestamp;

    @FindBy(xpath = ".//div[contains(@class,'test-details__tab-table-col _status')]//span")
    private Element level;

    @FindBy(xpath = ".//div[contains(@class,'log_message')]")
    private Element message;

    @FindBy(xpath = ".//span[@class='show-more']")
    private Element showMore;

    @FindBy(xpath = ".//span[@class='show-less']")
    private Element showLess;

    @FindBy(xpath = ".//div[@class='test-details__tab-table-col _menu']//button")
    private ExtendedWebElement menu;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown dropdown;

    public LogRow(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<Element> getScreenshot() {
        return WaitUtil.waitComponentByCondition(screenshot, (s -> s.isStateMatches(Condition.PRESENT)));
    }

    public String getTimestamp() {
        return timestamp.getText();
    }

    public String getLevel() {
        return level.getText();
    }

    public String getMessage() {
        return getMessage(true);
    }

    public String getMessage(boolean isShowMoreShouldBeExpanded) {
        if (isShowMoreShouldBeExpanded && showMore.isStateMatches(Condition.VISIBLE)) {
            showMore.click();
            pause(2);
        }
        return message.getText();
    }

    public void clickOnScreenshot() {
        screenshot.click();
    }

    public String getScreenshotSrc() {
        return screenshot.getAttributeValue("src");
    }

    public String getBackGroundColor() {
        log.info("Getting background color ...");
        String color = Color.fromString(this.getRootExtendedElement().getElement()
                .getCssValue("background-color")).asHex();
        log.info(" HEX-color code is " + color);
        return color;
    }

    public LogRow expandCollapseLogMessage() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].scrollIntoView(false);", this.getRootExtendedElement().getElement());
        pause(1);
        js.executeScript("arguments[0].click();", showMore.getElement());
        return this;
    }

    public LogRow clickShowLess() {
        if (showLess.isStateMatches(Condition.PRESENT)) {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].scrollIntoView(false);", this.getRootExtendedElement().getElement());
            pause(1);
            js.executeScript("arguments[0].click();", showLess.getElement());
            log.info("'Show less' is clicked");
        }
        return this;
    }

    public Dropdown openSettings() {
        this.getRootExtendedElement().click();
        menu.click();
        return dropdown;
    }

    public void openSettingsAndSelect(Dropdown.DropdownItemsEnum dropdownItem) {
        openSettings()
                .findItem(dropdownItem.getItemValue())
                .click();
    }
}
