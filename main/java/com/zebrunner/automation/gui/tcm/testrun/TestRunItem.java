package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TestRunItem extends AbstractUIObject {

    @FindBy(xpath = ".//div[contains(@class, 'runs-item__checkbox')]//input")
    private ExtendedWebElement checkBox;

    @FindBy(xpath = ".//div[@class='runs-item__title']")
    private ExtendedWebElement titleName;

    @FindBy(xpath = ".//a[@class='runs-item__test-plan']")
    private ExtendedWebElement testPlan;

    @FindBy(xpath = ".//div[@class='runs-item__environment']")
    private ExtendedWebElement environmentLabel;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.MILESTONE
            + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    private ExtendedWebElement milestoneLabel;

    @FindBy(xpath = ".//span[contains(@class, 'zbr-on-hover-user-card__children')]")
    private ExtendedWebElement usernameLabel;

    @FindBy(xpath = ConfigurationLabel.ROOT_XPATH)
    private List<ConfigurationLabel> configurations;

    @FindBy(xpath = ".//span[@class='statistics-percentage']")
    private ExtendedWebElement percentageStatistics;

    @FindBy(xpath = ".//span[@class='statistics-count']")
    private ExtendedWebElement countStatistics;

    @FindBy(xpath = ".//div[@class='runs-item__menu']//button")
    private ExtendedWebElement menuButton;

    @FindBy(xpath = Dropdown.ROOT_LOCATOR)
    private Dropdown menu;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public TestRunItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void clickCheckBox() {
        checkBox.click();
    }

    public boolean isCheckBoxClicked() {
        return checkBox.isChecked();
    }

    public String getTestRunName() {
        return titleName.getText();
    }

    public String getEnvironmentLabel() {
        return environmentLabel.getText();
    }

    public String getMilestoneName() {
        return milestoneLabel.getText();
    }

    public List<ConfigurationLabel> getConfigurations() {
        return configurations;
    }

    public String getPercentageStatistic() {
        return percentageStatistics.getText();
    }

    public String getCountStatistic() {
        return countStatistics.getText();
    }

    public String getNameTestPlan() {
        return testPlan.getText();
    }

    public Dropdown clickMenuButton() {
        menuButton.click();
        return menu;
    }

    public void clickMenuAction(TestRunMenuActions action) {
        menu.findItem(action.getValue()).click();
    }

    public void openMenuAndSelectAction(TestRunMenuActions action) {
        clickMenuButton();
        clickMenuAction(action);
    }

    public TestRunPage clickTestRunItem() {
        this.getRootExtendedElement().click();
        return TestRunPage.getPageInstance(getDriver());
    }

    public void hoverUsername() {
        usernameLabel.hover();
    }

    public String hoverTestRunNameAndGetTooltipText() {
        titleName.hover();

        return tooltip.getTooltipText();
    }

    public String getTooltipTextFromCheckbox() {
        checkBox.hover();
        return tooltip.getTooltipText();
    }

    @Getter
    @AllArgsConstructor
    public enum TestRunMenuActions {
        ASSIGN_TO_MILESTONE("Assign to milestone"),
        COPY_ID("Copy ID"),
        CLOSE("Close"),
        EDIT("Edit"),
        RERUN("Rerun"),
        DELETE("Delete");

        private final String value;
    }
}
