package com.zebrunner.automation.gui.integration;

import com.zebrunner.automation.gui.common.TenantBasePage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
@Getter
public class CertainIntegrationPage extends TenantBasePage {


    @FindBy(xpath = IntegrationConfig.ROOT_XPATH)
    private List<IntegrationConfig> integrationConfigs;

    @FindBy(xpath = "//div[@class='integration-content']")
    private ExtendedWebElement pageContent;


    public CertainIntegrationPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(pageContent);
    }


}
