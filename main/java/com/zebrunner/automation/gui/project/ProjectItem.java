package com.zebrunner.automation.gui.project;

import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

@Getter
public class ProjectItem extends AbstractUIObject {
    public static final String ROOT_XPATH = "//*[@class='projects-dropdown__item']";

    @FindBy(xpath = ".//div[@class='projects-dropdown__item-image']//img")
    private Element projectImg;

    @FindBy(xpath = ".//div[@class='projects-dropdown__item-name']")
    private Element projectName;

    @FindBy(xpath = ".//div[@class='projects-dropdown__item-key']")
    private Element projectKey;

    @FindBy(xpath = ".//*[contains(@class, 'project-star')]")
    private ExtendedWebElement projectStar;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public ProjectItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getNameValue() {
        return projectName.getText().trim();
    }

    public boolean isLogoPresent() {
        return projectImg.isStateMatches(Condition.PRESENT);
    }

    public String getLogoLink() {
        return projectImg.getAttributeValue("src");
    }

    public String getProjectName() {
        return projectName.getText();
    }

    public String getProjectKey() {
        return projectKey.getText();
    }

    public Element getProjectImg() {
        return projectImg;
    }

    public void clickProjectStar() {
        projectStar.click();
    }

    public boolean isProjectStarClickable() {
        return projectStar.isClickable(3);
    }

    public String getColorFromStar() {
        String color = projectStar.getElement().findElement(By.tagName("svg")).getCssValue("fill");
        return Color.fromString(color).asHex();
    }

    public String getStarToolTip() {
        projectStar.hover();
        return tooltip.getTooltipText();
    }
}
