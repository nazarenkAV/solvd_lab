package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class FilterCard extends AbstractUIObject {

    @FindBy(xpath = ".//button[contains(@class, 'name-value')]")
    private ExtendedWebElement filterName;

    @FindBy(xpath = ".//*[contains(@class,'zbr-on-hover-user-card__children')]")
    private ExtendedWebElement owner;

    @FindBy(xpath = ".//*[@class='saved-searches__list-col star']/button//*[local-name()='svg']")
    private ExtendedWebElement starButton;

    @FindBy(xpath = ".//div[@class='saved-searches__list-col menu']//button")
    private Element cardSettings;

    @FindBy(xpath = ".//button[@class='saved-searches__clear md-icon-button _default-md-style md-button md-ink-ripple']")
    private ExtendedWebElement closeSaving;

    @FindBy(xpath = "//ul[@role='menu']")
    protected FilterCardSettings settings;

    @FindBy(xpath = ".//div[contains(@class,'filter-status-tooltip-icon')]")
    private ExtendedWebElement statusTooltip;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public FilterCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getStarColor() {
        pause(3);
        String color = starButton.getElement().getCssValue("fill");
        return ColorUtil.getHexColorFromString(color);
    }

    public String getFilterStatusTooltip() {
        statusTooltip.hover();
        return tooltip.getTooltipText();
    }

    public String getFilterName() {
        return filterName.getText();
    }

    public FilterCardSettings openSettings() {
        cardSettings.click();
        return settings;
    }

    public void clickFilterName() {
        filterName.click();
    }

    public void clickFavouriteButton(){
        starButton.click();
    }

    public UserInfoTooltip hoverFilterOwner(){
        owner.hover();
        return new UserInfoTooltip(getDriver());
    }
}
