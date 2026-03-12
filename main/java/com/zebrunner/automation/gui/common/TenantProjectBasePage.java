package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Getter
public abstract class TenantProjectBasePage extends TenantBasePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //0 el = projects general page, 1 el = current project page
    @FindBy(xpath = Breadcrumb.ROOT_LOCATOR)
    protected Breadcrumb breadcrumbs;

    @FindBy(xpath = ZbrSearch.ROOT_XPATH)
    protected ZbrSearch search;

    @FindBy(xpath = "//button[contains(@class,'Zbr-reset-button')]")
    protected ExtendedWebElement resetButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='filter']/parent::button")
    protected ExtendedWebElement filterBtn;

    public TenantProjectBasePage(WebDriver driver) {
        super(driver);
    }

    public NavigationMenu getNavigationMenu() {
        return NavigationMenu.getInstance(getDriver());
    }

    public void clickBreadcrumb(String breadcrumbName) {
        breadcrumbs.clickBreadcrumb(breadcrumbName);
    }

    public void clickResetButton() {
        waitUntil(ExpectedConditions.visibilityOf(resetButton), 3);
        resetButton.click();
    }
}
