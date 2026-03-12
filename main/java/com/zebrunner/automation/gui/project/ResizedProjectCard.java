package com.zebrunner.automation.gui.project;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class ResizedProjectCard extends AbstractUIObject {

    public final static String RESIZED_CARD_ROOT_XPATH = "//*[contains(@class, 'project-card MuiBox-root')]";

    @FindBy(xpath = ".//p[text() = 'Starred']")
    private ExtendedWebElement starredLabel;

    @FindBy(xpath = ".//*[contains(@class, 'project-star')]")
    private ExtendedWebElement projectStar;

    public ResizedProjectCard(WebDriver driver) {
        super(driver);
    }

    public ResizedProjectCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isProjectStarPresent() {
        return projectStar.isElementPresent(3);
    }

    public boolean isProjectStarredLabelPresent() {
        return starredLabel.isElementPresent(3);
    }

    public String getColorFromStar() {
        String color = projectStar.getElement().findElement(By.tagName("svg")).getCssValue("fill");
        return Color.fromString(color).asHex();
    }
}
