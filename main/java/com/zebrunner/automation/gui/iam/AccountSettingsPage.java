package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.common.TenantBasePage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class AccountSettingsPage extends TenantBasePage {
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/profile";

    @FindBy(id = "simple-tab-1")
    private ExtendedWebElement apiAccessTabButton;

    @FindBy(id = "url")
    private ExtendedWebElement serviceUrl;

    @FindBy(xpath = "//*[@id='firstName']")
    private Element firstNameInput;

    @FindBy(xpath = "//*[@id='lastName']")
    private Element lastNameInput;

    @FindBy(xpath = "//div[contains(@class, 'profile-form__select')]/div/div")
    private ExtendedWebElement homepageDropdown;

    @FindBy(xpath = "//div[@class='profile-form__controls']/button[text()='Save']")
    private Element saveButton;

    @FindBy(xpath = "//h1[contains(@class, 'page-header')]")
    private ExtendedWebElement uiLoadedMarker;

    public AccountSettingsPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static AccountSettingsPage openPageDirectly(WebDriver driver) {
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage(driver);
        accountSettingsPage.openURL(String.format(PAGE_URL));
        accountSettingsPage.assertPageOpened();
        return accountSettingsPage;
    }

    public String getNameOfApiAccessTabButton() {
        return apiAccessTabButton.getText();
    }

    public boolean isApiAccessTabButtonPresent() {
        return apiAccessTabButton.isPresent();
    }

    public ApiAccessTab openApiAccessTab() {
        apiAccessTabButton.click();
        return new ApiAccessTab(getDriver());
    }

    public String getServiceUrl() {
        return serviceUrl.getAttribute("value");
    }

    public boolean isServiceUrlPresent() {
        return serviceUrl.isElementPresent();
    }

    public boolean isServiceUrlDisabled() {
        return !serviceUrl.getAttribute("disabled").isEmpty();
    }

    public void typeFirstName(String firstName) {
        firstNameInput.sendKeys(firstName);
    }

    public void typeLastName(String lastName) {
        lastNameInput.sendKeys(lastName);
    }

    public void typeFullName(String firstName, String lastName) {
        WaitUtil.waitComponentByCondition(firstNameInput, inputField
                -> inputField.isStateMatches(Condition.VISIBLE_AND_CLICKABLE));
        typeFirstName(firstName);
        typeLastName(lastName);
    }

    public boolean isSaveButtonActiveAndClickable() {
        return saveButton.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public void clickSave() {
        saveButton.click();
    }

    public String getCurrentHomepageText() {
        return homepageDropdown.getText();
    }

    public void clickHomepageDropdown() {
        homepageDropdown.click();
    }

    public void chooseHomepage(String text) {
        clickHomepageDropdown();
        new ListBoxMenu(getDriver()).clickItem(text);
    }


    public int getHomepageItemCount() {
        return new ListBoxMenu(getDriver()).getCount();
    }

    public enum HomepageDropdownEnum {
        PROJECTS_DIRECTORY("Projects directory"),
        TEST_CASES("Test cases (last visited project)"),
        AUTOMATION_LAUNCHES("Automation launches (last visited project)");
        private final String data;

        HomepageDropdownEnum(String data) {
            this.data = data;
        }

        public String get() {
            return data;
        }
    }
}
