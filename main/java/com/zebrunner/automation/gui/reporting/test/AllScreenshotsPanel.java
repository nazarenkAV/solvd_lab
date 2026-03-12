package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
public class AllScreenshotsPanel extends AbstractUIObject {
    @FindBy(xpath = ".//div[contains(@class,'pswp__bulletItem')]")
    private List<ScreenshotItem> screenshots;

    public AllScreenshotsPanel(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }
}
