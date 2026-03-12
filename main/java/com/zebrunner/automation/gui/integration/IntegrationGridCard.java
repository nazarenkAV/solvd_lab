package com.zebrunner.automation.gui.integration;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class IntegrationGridCard extends AbstractUIObject {

    public static final String ROOT_XPATH = "//a[contains(@class,'new-integration-card')]";

    @FindBy(xpath = ".//h4[@class='new-integration-card__name']")
    private ExtendedWebElement integrationName;


    public IntegrationGridCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getNameValue() {
        return integrationName.getText();
    }

}
