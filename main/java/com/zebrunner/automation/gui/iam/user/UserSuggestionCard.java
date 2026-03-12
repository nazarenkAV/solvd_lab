package com.zebrunner.automation.gui.iam.user;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class UserSuggestionCard extends AbstractUIObject {

    @FindBy(xpath = ".//*[contains(text(), '(read-only)')]")
    private ExtendedWebElement readOnlyLabel;

    @FindBy(xpath = ".//span[@class = 'option-title']")
    private ExtendedWebElement username;


    public UserSuggestionCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isReadOnlyLabelPresent() {
       return readOnlyLabel.isElementPresent(3);
    }

    public String getUsername() {
        return username.getText();
    }

}
