package com.zebrunner.automation.gui.integration;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantBasePage;
import com.zebrunner.automation.gui.iam.UsersPageR;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.automation.gui.iam.GroupsAndPermissionPage;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Slf4j
@Getter
public class SettingsPageR extends TenantBasePage {

    public static final String PAGE_NAME = "Settings";
    public static final String URL_MATCHER =
            "https://.+\\.zebrunner\\..+/settings(/|\\z)";
    public static final String PAGE_URL =
            ConfigHelper.getTenantUrl() + "/settings";

    @FindBy(xpath = "//div[contains(@class,'settings-header__logo')]")
    private ExtendedWebElement companyLogo;

    @FindBy(xpath = "//div[@class='settings-content__buttons']/a[contains(@href, 'sso')]")
    private Element ssoConfiguration;

    @FindBy(xpath = "//div[@class='settings-cards__buttons']/a[contains(@href, 'users')]")
    private Element users;

    @FindBy(xpath = "//div[@class='settings-cards__buttons']/a[contains(@href, 'user-groups')]")
    private Element groupsAndPermissions;

    @FindBy(xpath = "//div[contains(@class,'settings-header__logo')]")
    private ExtendedWebElement uiLoadedMarker;

    public SettingsPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static SettingsPageR getPageInstance(WebDriver driver) {
        return new SettingsPageR(driver);
    }

    @Override
    public boolean isPageOpened() {
        boolean isUrlMatches = waitUntil(ExpectedConditions.urlMatches(URL_MATCHER), DEFAULT_EXPLICIT_TIMEOUT);
        return isUrlMatches && super.isPageOpened();
    }

    public static SettingsPageR openPageDirectly(WebDriver driver) {
        SettingsPageR settingsPageR = new SettingsPageR(driver);
        settingsPageR.openURL(PAGE_URL);
        settingsPageR.assertPageOpened();
        return settingsPageR;
    }

    public UsersPageR toUsersPage() {
        users.click();
        return new UsersPageR(getDriver());
    }

    public GroupsAndPermissionPage toGroupsAndPermissionsPage() {
        groupsAndPermissions.click();
        return new GroupsAndPermissionPage(driver);
    }
}

