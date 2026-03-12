package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class FailureTagModal extends AbstractUIObject {

    public static final String UNCATEGORIZED_TAG = "UNCATEGORIZED";

    @FindBy(xpath = ".//*[@class = 'modal-popover-footer']/button")
    private ExtendedWebElement saveButton;

    @FindBy(xpath = ".//button[text() = 'UNCATEGORIZED']")
    private ExtendedWebElement uncategorizedTagButton;

    @FindBy(xpath = ".//button[text() = 'BUSINESS ISSUE']")
    private ExtendedWebElement businessIssueTagButton;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')][1]")
    private ExtendedWebElement assignedBy;

    public FailureTagModal(WebDriver driver) {
        super(driver);
        super.setBy(By.xpath("//div[contains(@class,'MuiPopover-paper')]"));
    }

    public void clickSaveButton() {
        saveButton.click();
    }

    public void clickUncategorizedTagButton() {
        uncategorizedTagButton.click();
    }

    public void clickBusinessIssueTagButton() {
        businessIssueTagButton.click();
    }

    public UserInfoTooltip hoverFirstAssigner(){
        assignedBy.hover();
        return new UserInfoTooltip(getDriver());
    }

}