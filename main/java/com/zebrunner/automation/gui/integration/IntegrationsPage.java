package com.zebrunner.automation.gui.integration;

import com.zebrunner.automation.legacy.IntegrationsEnum;
import com.zebrunner.automation.gui.common.TenantBasePage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Getter
public class IntegrationsPage extends TenantBasePage {

    public static final String PAGE_NAME = "Integrations";

    private final String CATEGORY_TABS_ROOT = "//div[contains(@class,'integrations-category-tabs__tab')]";

    @FindBy(xpath = CATEGORY_TABS_ROOT)
    private List<ExtendedWebElement> intCategoryTabs;

    @FindBy(xpath = IntegrationsGrid.ROOT_XPATH)
    private IntegrationsGrid integrationsGrid;

    public IntegrationsPage(WebDriver driver) {
        super(driver);
        setPageURL("/settings/integrations");
        setUiLoadedMarker(integrationsGrid);
    }

    public static IntegrationsPage openPageDirectly(WebDriver driver) {

        IntegrationsPage integrationsPage = new IntegrationsPage(driver);
        integrationsPage.open();

        integrationsPage.assertPageOpened();
        return integrationsPage;
    }

    public ExtendedWebElement findCategoryTab(String tabName) {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(CATEGORY_TABS_ROOT), 0),
                15);

        return intCategoryTabs.stream()
                .filter(tab -> tab.getText().equalsIgnoreCase(tabName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Tab " + tabName + " was not found!"));
    }


    public CertainIntegrationPage to(IntegrationsEnum integration) {
        integrationsGrid
                .findIntegrationCard(integration.getName())
                .orElseThrow(
                        () -> new NoSuchElementException("Integration " + integration.getName() + " was not found!")
                )
                .click();
        return new CertainIntegrationPage(getDriver());
    }
}
