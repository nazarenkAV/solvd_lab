package com.zebrunner.automation.gui.tcm.repository;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class RepositoryLeftPanel extends AbstractUIObject {

    @FindBy(xpath = ".//button[@class='repository-left-pane-icon']")
    private ExtendedWebElement paneIcon;
    @FindBy(xpath = ".//div[@aria-label='Expand all']")
    private ExtendedWebElement expandButton;

    public RepositoryLeftPanel(WebDriver driver) {
        super(driver);
        super.setBy(By.xpath("//div[@class='repository-left-pane ']"));
    }

    public void clickPaneIcon() {
        paneIcon.click();
    }

    public void expandAll() {
        expandButton.click();
    }

}
