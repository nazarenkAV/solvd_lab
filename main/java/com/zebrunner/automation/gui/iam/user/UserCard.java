package com.zebrunner.automation.gui.iam.user;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class UserCard extends AbstractUIObject {
    @FindBy(xpath = ".//i[text()='person']/ancestor::span")
    private ExtendedWebElement id;

    @FindBy(xpath = ".//div[@class='users-table__col _username']/span[@class = 'users-table__content']")
    private ExtendedWebElement username;

    @FindBy(xpath = ".//div[@class='users-table__col _email']/span")
    private ExtendedWebElement email;

    @FindBy(xpath = ".//div[@class='users-table__col _fullname']//span")
    private ExtendedWebElement userFullName;

    @FindBy(xpath = ".//div[@data-title='Group']/span")
    private ExtendedWebElement group;

    @FindBy(xpath = ".//div[@data-title='Source']/span")
    private ExtendedWebElement source;

    @FindBy(xpath = ".//div[@data-title='Registration/ last activity']//div[@name='userCreated']")
    private ExtendedWebElement userCreated;

    @FindBy(xpath = ".//div[@data-title='Registration/ last activity']//span")
    private ExtendedWebElement lastActive;

    @FindBy(xpath = ".//div[@data-title='Status']/span")
    private ExtendedWebElement status;

    @FindBy(xpath = ".//*[@class = 'users-table__col _menu']//button")
    private ExtendedWebElement settings;

    @FindBy(xpath = ".//div[@class='users-table__col _username']/span[contains(@class, 'users-table__content-additional')]")
    private ExtendedWebElement readOnlyUserLabel;

    public UserCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getUserName() {
        return username.getText().trim();
    }

    public String getEmail() {
        return email.getText();
    }

    public String getStatus() {
        return status.getText().trim();
    }

    public UserCardMenu openUserCardMenu() {
        settings.click();
        return new UserCardMenu(getDriver());
    }

    public void deactivateUser() {
        UserStatusConfirmationModal deactivateStatusModal = openUserCardMenu().clickDeactivateButton();
        deactivateStatusModal.deactivate();
    }

    public String getUserFullName() {
        return userFullName.getText();
    }

    public boolean isReadOnlyUserLabelPresent() {
        return readOnlyUserLabel.isElementPresent(3);
    }
}
