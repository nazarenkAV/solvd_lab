package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.project.ProjectsMenu;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.integration.SettingsPageR;
import com.zebrunner.automation.gui.reporting.TestRunsPageR;
import com.zebrunner.automation.gui.landing.DocumentationPage;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Set;

//header
public class TenantHeader extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(className = "app-header__logo-icon")
    private ExtendedWebElement logoImage;

    @FindBy(xpath = "//div[@class='projects-dropdown']//button")
    private Element showProjectButton;

    //4 buttons on the left of a header
    @FindBy(xpath = ".//a[contains(@href, '/documentation')]")
    private Element helpButton;

    @FindBy(xpath = ".//md-list-item[contains(@class,'nav-item')]//i[text()='account_balance_wallet']")
    private Element walletButton;

    @FindBy(xpath = ".//a[contains(@href, 'settings')]")
    private Element toSettingsPage;

    @FindBy(xpath = ".//button[contains(@class,'header-user__button')]")
    private Element membersButton;

    //header buttons sub buttons
    @FindBy(xpath = "//md-menu-content[@class='nav-sublist']//span[contains(text(),'Documentation')]")
    private Element helpDocumentation;

    @FindBy(xpath = "//md-menu-content[@class='nav-sublist']//span[contains(text(),'Billing')]")
    private Element walletBilling;

    @FindBy(xpath = "//md-menu-content[@class='nav-sublist']//span[contains(text(),'Subscription')]")
    private Element walletSubscription;

    @FindBy(xpath = "//md-menu-content[@class='nav-sublist']//span[contains(text(),'Account settings')]")
    private Element memberAccountSettings;

    @FindBy(xpath = "//*[@role='menuitem']//span[text()='Logout']")
    private Element memberLogout;

    @FindBy(xpath = "//*[@class='dropdown__item-title'][text() = 'About Zebrunner']")
    private Element aboutZebrunner;

    public TenantHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public ProjectsMenu openProjectsWindow() {
        showProjectButton.click();
        return new ProjectsMenu(getDriver());
    }

    public ProjectsPage toProjectsPage() {
        showProjectButton.click();
        return new ProjectsMenu(getDriver()).toProjectsPage();
    }

    public DocumentationPage toDocumentationPage() {
        helpButton.click();
        Set<String> windows = driver.getWindowHandles();
        String firstTab = driver.getWindowHandle();
        for (String window : windows) {
            if (!window.equals(firstTab)) {
                LOGGER.info("Switching to new tab");
                driver.switchTo().window(window);
            }
        }
        return DocumentationPage.openPage(getDriver());
    }

    public SettingsPageR toSettingsPage() {
        toSettingsPage.click();
        return SettingsPageR.getPageInstance(getDriver());
    }

    public void toWalletDropdown() {
        // trying to close dropdown list of wallet that (can be) opened
        this.getRootExtendedElement().click();
        walletButton.click();
    }

    public Boolean isBillingPresentInDropdown() {
        return walletBilling.isStateMatches(Condition.PRESENT);
    }

    public Boolean isSubscriptionPresentInDropdown() {
        return walletSubscription.isStateMatches(Condition.PRESENT);
    }

    public Boolean isBillingPresentInHeader() {
        return walletButton.isStateMatches(Condition.PRESENT);
    }

    public Boolean isButtonToSettingsPagePresent() {
        return toSettingsPage.isStateMatches(Condition.PRESENT);
    }

    public Boolean isHelpButtonPresentInHeader() {
        return helpButton.isStateMatches(Condition.PRESENT);
    }

    public Boolean isAccountAndProfileButtonPresentInHeader() {
        return membersButton.isStateMatches(Condition.PRESENT);
    }

    public TestRunsPageR reLoginAngular(String username, String password) {
        membersButton.click();
        memberLogout.click();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        return new TestRunsPageR(getDriver());
    }

    public LoginPage logout() {
        membersButton.waitUntil(Condition.CLICKABLE);
        membersButton.click();
        memberLogout.click();
        return new LoginPage(driver);
    }

    public AboutZebrunnerModal openAboutZebrunnerModal() {
        membersButton.waitUntil(Condition.CLICKABLE);
        membersButton.click();
        aboutZebrunner.click();
        return new AboutZebrunnerModal(getDriver());
    }

    public void openMenu() {
        membersButton.waitUntil(Condition.CLICKABLE);
        membersButton.click();
    }

    public AboutZebrunnerModal clickAboutZebrunnerButton() {
        aboutZebrunner.click();
        return new AboutZebrunnerModal(getDriver());
    }
}
