package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
@Getter
public class CreateWidgetModal extends AbstractModal<CreateWidgetModal> {
    public static final String TITLE_PAGE_1 = "New widget: choose template (1/3)";
    public static final String TITLE_PAGE_2 = "New widget: set parameters (2/3)";
    public static final String TITLE_PAGE_3 = "New widget: preview and save (3/3)";

    @FindBy(xpath = WidgetCard.ROOT_XPATH)
    private List<WidgetCard> widgetCards;

    @FindBy(xpath = ".//div[@class='buttons-wrapper']//md-icon[@aria-label='autorenew']/ancestor::button")
    private Element templatesButton;

    @FindBy(xpath = ".//button[contains(text(),'Back')]")
    private Element backButton;

    @FindBy(xpath = ".//input[@id='name']")
    private Element nameField;

    @FindBy(xpath = ".//textarea[@id='description']")
    private Element textAreaField;

    public CreateWidgetModal(WebDriver driver) {
        super(driver);
    }

    public List<WidgetCard> getWidgetTemplateCards() {
        log.debug("Waiting for widgetCard list to load...");
        WaitUtil.waitCheckListIsNotEmpty(widgetCards);
        return widgetCards;
    }

    public CreateWidgetModal typeName(String widgetName) {
        nameField.waitUntil(Condition.VISIBLE).sendKeys(widgetName);
        return this;
    }

    public CreateWidgetModal typeDescription(String widgetDescription) {
        textAreaField.waitUntil(Condition.VISIBLE).sendKeys(widgetDescription);
        return this;
    }

    public CreateWidgetModal choseWidgetTemplate(String title) {
        log.info("Waiting for widgetCard list to load...");
        pause(1);
        WaitUtil.waitElementAppearedInListByCondition(widgetCards,
                        widgetCard -> widgetCard.getTitle().toLowerCase().contains(title.toLowerCase()),
                        "Widget template with name " + title + " was found",
                        "Can't find widget template with name " + title)
                .clickTitle();
        return this;
    }

    public boolean isNextButtonActive() {
        log.info("Checking 'Next' button ...");
        return nextButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isBackButtonActive() {
        log.info("Checking 'Back' button ...");
        return backButton.isStateMatches(Condition.CLICKABLE);
    }
}
