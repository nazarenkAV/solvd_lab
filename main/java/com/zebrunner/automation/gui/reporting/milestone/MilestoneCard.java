package com.zebrunner.automation.gui.reporting.milestone;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class MilestoneCard extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String DIVIDER_XPATH = "//div[contains(@class,'milestone-dates__container-divider')]";

    @FindBy(xpath = ".//div[@data-title]//div[@class='attribute-label__content']")
    private Element milestoneName;

    @FindBy(xpath = ".//div[@data-title='Name']//*[local-name()='svg']")
    private Element flagImg;

    @FindBy(xpath = ".//div[@data-title='Dates']//*[local-name()='svg']")
    private Element calendarIcon;

    @FindBy(xpath = "." + DIVIDER_XPATH
            + "//preceding-sibling::*[contains(@class,'_disabled') "
            + "or contains(@class,'milestone-dates__container-text')]")
    private Element startDateInfo;

    @FindBy(xpath = "." + DIVIDER_XPATH
            + "//following-sibling::*[contains(@class,'_disabled') "
            + "or contains(@class,'milestone-dates__container-text')]")
    private Element dueDateInfo;

    @FindBy(xpath = "." + DIVIDER_XPATH)
    private Element dividerImg;

    @FindBy(xpath = ".//input[contains(@class,'PrivateSwitchBase-input css-1m9pwf3')]")
    private ExtendedWebElement checkboxComplete;

    @FindBy(xpath = ".//span[@class='milestone-start__text']")
    private Element checkboxCompleteElement;

    @FindBy(xpath = ".//div[contains(@class,'milestones-description')]")
    private Element description;

    @FindBy(xpath = ".//div[@class='milestone-menu-button']//button")
    private Element threeDots;

    @FindBy(xpath = "//span[text()='Edit']")
    private Element edit; // on dropdown

    @FindBy(xpath = "//span[text()='Delete']")
    private Element delete; // on dropdown

    public MilestoneCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void delete() {
        LOGGER.info("Deleting milestone with name " + getTitle());
        threeDots.click();
        MilestoneModal milestoneModal = new MilestoneModal(getDriver());
        milestoneModal.clickDeleteButton();
        milestoneModal.clickDeleteButton();
        LOGGER.info("Deleted");
    }

    public String getTitle() {
        return milestoneName.getText();
    }

    public boolean isFlagImgPresent() {
        return flagImg.isStateMatches(Condition.VISIBLE);
    }

    public boolean isCalendarIconPresent() {
        return calendarIcon.isStateMatches(Condition.VISIBLE);
    }

    public String getStartDateInfo() {
        return startDateInfo.getText();
    }

    public String getDueDateInfo() {
        return dueDateInfo.getText();
    }

    public LocalDate getDueDateInLocalDateFormat() {
        String dateStr = getDueDateInfo();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

    public String getCheckboxCompleteLabel() {
        return checkboxCompleteElement.getText().trim();
    }

    public String getDescription() {
        return description.getText().trim();
    }

    public boolean isDividerImgPresent() {
        return dividerImg.isStateMatches(Condition.VISIBLE);
    }

    public boolean isCheckboxActive() {
        return Boolean.parseBoolean(checkboxComplete.getAttribute("checked"));
    }

    public boolean isEditButtonPresent() {
        return threeDots.isStateMatches(Condition.CLICKABLE);
    }

    public void clickThreeDots() {
        threeDots.click();
    }

    public MilestoneModal edit() {
        // editButton.click();
        threeDots.click();
        edit.click();
        waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(AbstractModal.ROOT_LOCATOR)), 10);
        MilestoneModal milestoneModal = new MilestoneModal(getDriver());

        return milestoneModal;
    }

    public void clickCheckBox() {
        checkboxComplete.click();
    }

    public boolean isDescriptionPresent() {
        return description.isStateMatches(Condition.VISIBLE);
    }

    public boolean isStartDatePresent() {
        return startDateInfo.isStateMatches(Condition.VISIBLE);
    }

    public boolean isDueDatePresent() {
        return dueDateInfo.isStateMatches(Condition.VISIBLE);
    }

    public boolean isDeleteButtonActive() {
        return delete.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isEditActive() {
        return edit.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }
}

