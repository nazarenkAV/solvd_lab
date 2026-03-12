package com.zebrunner.automation.gui.integration;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Optional;

@Getter
public class IntegrationsGrid extends AbstractUIObject {

    public static final String ROOT_XPATH = "//div[@class='integrations-content__cards-grid']";

    @FindBy(xpath = IntegrationGridCard.ROOT_XPATH)
    private List<IntegrationGridCard> integrationGridCardList;

    public IntegrationsGrid(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public Optional<IntegrationGridCard> findIntegrationCard(String integrationName) {
        return getIntegrationCards().stream()
                .filter(card -> card.getNameValue().equalsIgnoreCase(integrationName))
                .findFirst();
    }

    private List<IntegrationGridCard> getIntegrationCards() {

        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(IntegrationGridCard.ROOT_XPATH), 0),
                15);

        return integrationGridCardList;
    }


}
