package com.zebrunner.automation.gui.launcher;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Optional;

@Getter
@Slf4j
public class LauncherTree extends AbstractUIObject {
    private final String LAUNCHER_ITEM_LOCATOR = ".//li[@class='launcher-tree__item-launcher']";

    @FindBy(xpath = ".//div[contains(@class,'launcher-tree__repo-name')]")
    private Element repository;

    @FindBy(xpath = LAUNCHER_ITEM_LOCATOR)
    private List<LauncherItem> launcherItems;

    @FindBy(xpath = ".//span[text()='Add new launcher']//ancestor::button")
    private Element addNewLauncherButton;

    public LauncherTree(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isLauncherPresentInList(String launcherName) {
        log.info("Checking is launcher with name: {} of repo: {} is present", launcherName, repository.getText());
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(launcherItems,
                launcher -> launcher.getLauncherName().equalsIgnoreCase(launcherName));
    }

    public Optional<LauncherItem> getLauncherWithName(String launcherName) {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(LAUNCHER_ITEM_LOCATOR), 1), 5);
        return launcherItems.stream()
                .filter(launcherItem -> {
                    log.debug("Getting launcher with name {}", launcherName);
                    return launcherItem.getLauncherName().equalsIgnoreCase(launcherName);
                })
                .findFirst();
    }

    public LauncherTree clickOnRepository() {
        repository.click();
        return this;
    }

    public AddOrEditLauncherPage clickAddNewLauncherBtn() {
        addNewLauncherButton.click();
        return new AddOrEditLauncherPage(getDriver());
    }
}
