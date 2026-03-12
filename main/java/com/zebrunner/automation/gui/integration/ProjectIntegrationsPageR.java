package com.zebrunner.automation.gui.integration;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ProjectIntegrationsPageR extends TenantProjectBasePage {

    public static final String PAGE_NAME = "Integrations";
    public static final String URL_MATCHER =
            "https://.+\\.zebrunner\\..+/projects/.+/integrations(/|\\z)";

    public static final String PAGE_URL =
            ConfigHelper.getTenantUrl() + "/projects/%s/integrations/";

    @FindBy(xpath = "//div[@class='integration-card ']")
    private List<ExtendedWebElement> activeIntegrations;

    @FindBy(xpath = "//div[@class='integration-card _disable']")
    private List<ExtendedWebElement> disableIntegrations;

    @FindBy(xpath = "//div[contains(@class,'category-container')]/div")
    private List<ExtendedWebElement> totalIntegrations;

    @FindBy(xpath = "//p[@class='integrations__content-category-title' ]")
    private ExtendedWebElement uiLoadedMarker;

    public ProjectIntegrationsPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static ProjectIntegrationsPageR getPageInstance(WebDriver driver) {
        return new ProjectIntegrationsPageR(driver);
    }

    @Override
    public boolean isPageOpened() {
        boolean isUrlMatches = waitUntil(ExpectedConditions.urlMatches(URL_MATCHER), DEFAULT_EXPLICIT_TIMEOUT);
        return isUrlMatches && super.isPageOpened();
    }

    public ProjectIntegrationsPageR openPageDirectly(String projectKey) {
        this.openURL(String.format(PAGE_URL, projectKey));
        assertPageOpened();
        return this;
    }

    public List<String> getActiveIntegrations() {
        log.debug("Waiting for integrations list to load...");
        WaitUtil.waitCheckListIsNotEmpty(activeIntegrations);
        return activeIntegrations.stream().map(ExtendedWebElement::getText).collect(Collectors.toList());
    }

    public List<String> getDisableIntegrations() {
        List<String> integrationsStr = new ArrayList<>();
        log.debug("Waiting for disabled integrations list to load...");
        WaitUtil.waitCheckListIsNotEmpty(disableIntegrations);
        return disableIntegrations.stream().map(ExtendedWebElement::getText).collect(Collectors.toList());
    }

    public List<String> getTotalIntegrations() {
        log.debug("Waiting for total integrations list to load...");
        WaitUtil.waitCheckListIsNotEmpty(totalIntegrations);
        return totalIntegrations.stream().map(ExtendedWebElement::getText).collect(Collectors.toList());
    }

}
