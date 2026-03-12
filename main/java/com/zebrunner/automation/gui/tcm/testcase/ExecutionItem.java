package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.legacy.TestRunStatusEnumRgb;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ExecutionItem extends AbstractUIObject {

    public static final String ROOT = ".//*[@class='execution']";

    @FindBy(xpath = ".//div[@class='execution__status']")
    private ExtendedWebElement status;

    @FindBy(xpath = ".//div[@class='execution__type']//div")
    private ExtendedWebElement executionType;

    @FindBy(xpath = ".//div[@class='execution__date']")
    private ExtendedWebElement executionDate;

    @FindBy(xpath = ".//div[@class='execution__date']//span[contains(@class,'user-card')]")
    private ExtendedWebElement executedBy;

    @FindBy(xpath = ".//div[@class='execution__elapsed-time']")
    private ExtendedWebElement executionElapsedTime;

    @FindBy(xpath = AttachmentItem.ROOT)
    private List<AttachmentItem> attachments;

    public ExecutionItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getExecutionTypeValue() {
        return executionType.getAttribute("aria-label").replace("Type:", "").trim();
    }

    public String getExecutionByValue() {
        return executedBy.getText();
    }

    public List<AttachmentItem> getAttachments() {
        return attachments;
    }

    public UserInfoTooltip hoverExecutedBy() {
        executedBy.hover();
        pause(1);
        return new UserInfoTooltip(getDriver());
    }

    public String getExecutionStatus() {
        return TestRunStatusEnumRgb.fromRgb(status.getCssValue("background-color")).getStatus();
    }

    public String getExecutionElapsedTimeText() {
        return executionElapsedTime.getText();
    }

    public boolean isElapsedTimePresent() {
        return executionElapsedTime.isPresent(1);
    }
}
