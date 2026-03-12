package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.launcher.preset.PresetItem;
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
public class LauncherItem extends AbstractUIObject {
    private final String PRESET_ITEM_LOCATOR = ".//div[contains(@class,'launcher-tree__item-preset-name')]";
    @FindBy(xpath = ".//div[contains(@class,'launcher-tree__item-launcher-name')]")
    private Element launcherNameXpath;

    @FindBy(xpath = ".//button[contains(@class,'launcher-tree__repo-expand')]")
    private Element expandBtn;

    @FindBy(xpath = PRESET_ITEM_LOCATOR)
    private List<PresetItem> presetList;

    public LauncherItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isPresetPresentInList(String presetName) {
        log.info("Checking is preset with name: {} of repo: {} is present", presetName, launcherNameXpath.getText());
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(presetList,
                preset -> preset.getPresetName().equalsIgnoreCase(presetName));
    }

    public Optional<PresetItem> getPresetWithName(String presetName) {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(PRESET_ITEM_LOCATOR), 1), 5);
        return presetList.stream()
                .filter(preset -> {
                    log.info("Getting preset with name {} in launcher {} ", presetName, getLauncherName());
                    return preset.getPresetName().equalsIgnoreCase(presetName);
                })
                .findFirst();
    }

    public String getLauncherName() {
        return launcherNameXpath.getText().trim();
    }

    public void clickOnLauncherName() {
        launcherNameXpath.click();
    }
}
