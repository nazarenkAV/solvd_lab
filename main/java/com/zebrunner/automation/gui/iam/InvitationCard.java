package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//div[@class='invitations-table__row ng-scope']
public class InvitationCard extends AbstractUIObject {

    @FindBy(xpath = ".//i[text()='person']/ancestor::span")
    private ExtendedWebElement id;

    @FindBy(xpath = ".//span[@name='userPhoto']")
    private ExtendedWebElement userPhoto;

    @FindBy(xpath = ".//div[@data-title='Email']/span")
    private ExtendedWebElement email;

    @FindBy(xpath = ".//div[@data-title='Group']/span")
    private ExtendedWebElement group;

    @FindBy(xpath = ".//div[@data-title='Source']/span")
    private ExtendedWebElement source;

    @FindBy(xpath = ".//div[@data-title='Date']//span[@class='users-table__content-additional ng-binding']")
    private ExtendedWebElement createdBy;

    @FindBy(xpath = ".//div[@data-title='Date']//span[@class='invitations-table__content ng-binding']")
    private ExtendedWebElement creationDate;

    @FindBy(xpath = ".//div[@data-title='Source']/span")
    private ExtendedWebElement status;

    @FindBy(xpath = ".//div[@class='invitations-table__col invitations-menu']//button")
    private ExtendedWebElement settings;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown dropdownRoot;

    public InvitationCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getEmail() {
        return email.getText().trim();
    }

    public void clickSettings() {
        settings.click();
    }

    public boolean isSettingsButtonPresent() {
        return settings.isElementPresent();
    }

    public void clickCopyLinkButton() {
        dropdownRoot.findItem(Dropdown.DropdownItemsEnum.COPY_LINK.getItemValue()).click();
    }

    public void clickResendButton() {
        dropdownRoot.findItem(Dropdown.DropdownItemsEnum.RESEND.getItemValue()).click();
    }

    public void resend(){
        settings.click();
        clickResendButton();
    }

    public RevokeUserModal openRevokeUserModal() {
        pause(3);
        dropdownRoot.findItem(Dropdown.DropdownItemsEnum.REVOKE.getItemValue()).click();
        return RevokeUserModal.getInstance(getDriver());
    }

    public void revoke(){
        settings.click();
        RevokeUserModal revokeUserModal = openRevokeUserModal();
        revokeUserModal.clickRevoke();
    }
}
