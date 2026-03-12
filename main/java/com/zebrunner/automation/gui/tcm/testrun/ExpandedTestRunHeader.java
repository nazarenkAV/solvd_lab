package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ExpandedTestRunHeader extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[contains(@class, 'run-dashboard__full-container')]";

    @FindBy(xpath = ".//*[@d='" + SvgPaths.MILESTONE
            + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    private ExtendedWebElement milestoneLabel;

    @FindBy(xpath = ".//div[@class='system-label__key']")
    private ExtendedWebElement configurationGroup;

    @FindBy(xpath = ".//div[@class='system-label__value']")
    private ExtendedWebElement configurationOption;

    @FindBy(xpath = ".//div[contains(@class, 'run-dashboard__description')]")
    private ExtendedWebElement descriptionLabel;

    @FindBy(xpath = ".//div[@class='run-dashboard__description-container']//span/following-sibling::span/a[text()='show more']")
    private ExtendedWebElement showMoreButton; // show full description

    @FindBy(xpath = ".//span[contains(@class, 'zbr-on-hover-user-card__children')]")
    private ExtendedWebElement createdUsernameLabel;

    public ExpandedTestRunHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getMilestoneName() {
        return milestoneLabel.getText();
    }

    public String getDescription() {
        if (showMoreButton.isPresent(3)) {
            showMoreButton.click();
            return descriptionLabel.getText().replace("show less", "").trim();
        }

        return descriptionLabel.getText();
    }

    public String getConfigurationGroupName() {
        return configurationGroup.getText();
    }

    public String getConfigurationOptionName() {
        return configurationOption.getText().replace(":", "").trim();
    }

    public void hoverUsername() {
        createdUsernameLabel.hover();
    }
}
