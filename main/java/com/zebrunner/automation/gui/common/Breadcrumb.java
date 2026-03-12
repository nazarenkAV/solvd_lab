package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@Slf4j
public class Breadcrumb extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//nav[contains(@class,'ZbrBreadcrumbs')]";

    @FindBy(xpath = ".//li")
    private List<ExtendedWebElement> breadcrumbs;

    public Breadcrumb(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isBreadcrumbPresentOnPage() {
        return waitUntil(ExpectedConditions.elementToBeClickable(By.xpath(ROOT_LOCATOR)), 3);
    }

    public void clickBreadcrumb(String breadcrumbName) {
        log.info("Loading breadcrumbs.... ");
        pause(2);
        for (ExtendedWebElement breadcrumb : breadcrumbs) {
            log.info(breadcrumb.getText());
            if (breadcrumb.getText().equalsIgnoreCase(breadcrumbName)) {
                breadcrumb.click();
                break;
            }
        }
    }

    public boolean isBreadcrumbPresent(String breadcrumbName) {
        log.info("Loading breadcrumbs.... ");
        pause(2);

        for (ExtendedWebElement breadcrumb : breadcrumbs) {
            String currentBreadcrumbName = breadcrumb.getText();
            log.info("Found breadcrumb: " + currentBreadcrumbName);

            if (currentBreadcrumbName.equalsIgnoreCase(breadcrumbName)) {
                log.info("Breadcrumb '" + breadcrumbName + "' is present.");
                return true;
            }
        }

        log.info("Breadcrumb '" + breadcrumbName + "' is not present.");
        return false;
    }
}
