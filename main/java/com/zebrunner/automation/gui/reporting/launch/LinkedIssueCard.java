package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class LinkedIssueCard extends AbstractUIObject {

    public final static String ROOT_XPATH = "//*[@class = 'issue-list__item-wrapper']";

    @FindBy(xpath = ".//div[@class = 'issue-list__item-col _id']")
    private ExtendedWebElement issueTicketId;

    @FindBy(xpath = ".//button[contains(@class, 'issue-list__item-col _button')]")
    private ExtendedWebElement linkUnlinkIssueButton;

    @FindBy(xpath = ".//div[@class ='issue-list__item-col _title']")
    private ExtendedWebElement issueTicketTitle;

    @FindBy(xpath = ".//div[@class ='issue-list__item-col _name']")
    private ExtendedWebElement assignedPerson;

    @FindBy(xpath = ".//div[@class = 'issue-list__item-col _date']")
    private ExtendedWebElement cardDate;

    public LinkedIssueCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getIssueTicketIdText() {
        return issueTicketId.getText();
    }

    public void clickLinkUnlinkIssueButton() {
        linkUnlinkIssueButton.click();
    }

    public String getIssueTicketTitleText() {
        return issueTicketTitle.getText();
    }

    public String getColorOfLinkUnlinkButton() {
        String color = linkUnlinkIssueButton.getElement().getCssValue("color");
        return ColorUtil.getHexColorFromString(color);
    }

    public String getCardDate() {
        return cardDate.getText();
    }

    public String getTitleOfLinkUnlinkButton() {
        return linkUnlinkIssueButton.getAttribute("title");
    }

    public String getAssignedPerson() {
        return assignedPerson.getText();
    }

    public UserInfoTooltip hoverAssignedPerson() {
        assignedPerson.hover();
        return new UserInfoTooltip(getDriver());
    }
}
