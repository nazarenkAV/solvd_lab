package com.zebrunner.automation.gui.iam;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

//.//div[@class='users-groups__item ng-scope layout-column']
public class UserGroupCard extends AbstractUIObject {

    @FindBy(xpath = ".//p[@class='user-groups__content-card-header-title']/b")
    private ExtendedWebElement groupName;

    @FindBy(css = ".input[type='text']")
    private ExtendedWebElement addUserInputField;

    @FindBy(xpath = ".//div[contains(@class,'css-1wrur9i')]/span")
    private List<ExtendedWebElement> userNames;

    @FindBy(xpath = ".//button[@class='md-icon-button md-button md-ink-ripple']")
    private ExtendedWebElement editGroupMenu;

    public UserGroupCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getGroupName() {
        waitUntil(ExpectedConditions.visibilityOf(groupName.getElement()), 1);
        return groupName.getText();
    }

    public boolean isUserPresentInGroup(String fullNameOrUsername) {
        for (ExtendedWebElement element : userNames) {
            if (element.getText().equals(fullNameOrUsername)) {
                return true;
            }
        }
        return false;
    }

}
