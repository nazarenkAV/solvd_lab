package com.zebrunner.automation.gui.integration;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class IntegrationConfig extends AbstractUIObject {

    public static final String ROOT_XPATH = "//div[@class='integration-content__config']";

    @FindBy(xpath = ".//div[@data-title='URL']")
    private ExtendedWebElement urlElement;

    @FindBy(xpath = ".//div[@data-title='Projects']")
    private ExtendedWebElement projectsElement;

    @FindBy(xpath = ".//div[@data-title='Enabled']//span")
    private ExtendedWebElement enabledElement;

    @FindBy(xpath = ".//div[@aria-label='Enable' or @aria-label='Disable']")
    private ExtendedWebElement enableDisableButton;

    @FindBy(xpath = ".//div[@aria-label='Edit']")
    private ExtendedWebElement editButton;


    public IntegrationConfig(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getUrl() {
        return urlElement.getText();
    }

    public String getProjects() {
        return projectsElement.getText();
    }

    public boolean isEnabledIntegration() {
        return enabledElement.getText().equalsIgnoreCase("enabled");
    }

    public EditIntegrationModal clickEdit() {
        editButton.click();
        return new EditIntegrationModal(getDriver());
    }
}
