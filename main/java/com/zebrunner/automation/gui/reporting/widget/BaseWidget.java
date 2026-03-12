package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.utils.R;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.List;

@Getter
public class BaseWidget extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String ROOT_ELEMENT = "div[contains(@class,'grid-stack-item ui-draggable')]";

    private final String FAILURES_INDEX = "[4]";

    @FindBy(xpath = ".//div[@class='panel-heading']")
    private ExtendedWebElement widgetTitle;

    @FindBy(xpath = ".//div[@class='panel-heading']//button")
    private ExtendedWebElement cardKebabMenu;

    @FindBy(xpath = "//span[text()='Send by email']")
    private Element sendByEmailButton;

    @FindBy(xpath = "//span[text()='Edit widget']")
    private Element editWidgetButton;

    @FindBy(xpath = "//span[text()='Delete']")
    private Element removeFromDashboardButton;

    @FindBy(xpath = "//button[text()='Delete']")
    private Element deleteButton;

    @FindBy(xpath = ".//a[text()='anonymous']")
    private Element anonymous;

    @FindBy(xpath = ".//td" + FAILURES_INDEX)
    private List<Element> failuresCount;

    public BaseWidget(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTitle() {
        return widgetTitle.getText().trim().replace("\nmore_vert", "");
    }

    public SendByEmailWindow openSendByEmailWindow() {
        widgetTitle.scrollTo();
        widgetTitle.hover();
        pause(1);
        cardKebabMenu.click();
        pause(1);
        sendByEmailButton.click();
        return new SendByEmailWindow(getDriver());
    }

    public EditWidgetWindow openEditWidgetWindow() {
        widgetTitle.scrollTo();
        widgetTitle.hover();
        cardKebabMenu.click();
        pause(1);
        editWidgetButton.clickUsingJS();
        return new EditWidgetWindow(getDriver());
    }

    public void editWidget(String name, String description) {
        EditWidgetWindow widgetWindow = openEditWidgetWindow();
        widgetWindow
                .clickNext()
                .typeName(name)
                .typeDescription(description)
                .submitModal();
    }

    public void removeWidget() {
        R.CONFIG.put("auto_screenshot", String.valueOf(false), true);
        widgetTitle.hover();
        cardKebabMenu.click();
        removeFromDashboardButton.clickUsingJS();
        deleteButton.click();
    }

    public void clickOnActonMenu() {
        widgetTitle.hover();
        cardKebabMenu.click();
    }

    public void closeActonMenu() {
        LOGGER.info("Closing action menu ...");
        WebElement body = driver.findElement(By.tagName("body"));
        body.click();
    }

    public Boolean isSendByEmailActive() {
        LOGGER.info("Checking the button 'Send by email'...");
        return sendByEmailButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public Boolean isEditWidgetActive() {
        LOGGER.info("Checking the button 'Edit dashboard'...");
        return editWidgetButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public Boolean isRemoveFromDashboardActive() {
        LOGGER.info("Checking the button 'Remove from dashboard'...");
        return removeFromDashboardButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public void moveWidget(int xOffset, int yOffset) {
        //To drag and drop element by xOffset pixel offset In horizontal and yOffset pixel offset In Vertical direction.
        // new Actions(getDriver()).dragAndDropBy(widgetTitle.getElement(), xOffset, yOffset).build() .perform();
        new Actions(getDriver()).clickAndHold(widgetTitle.getElement())
                .pause(Duration.ofSeconds(5))
                .moveByOffset(getDriver().manage().window().getSize().getWidth() / 2, yOffset)
                .release().build().perform();
    }

    public WidgetsPage toPersonalWidget() {
        anonymous.click();
        return new WidgetsPage(getDriver());
    }

    public WidgetsPage clickFailuresCount() {
        failuresCount.get(0).click();
        return new WidgetsPage(getDriver());
    }
}
