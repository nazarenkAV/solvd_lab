package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TrashBinTestCasePreview extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[@class = 'run-right-sidebar']";

    @FindBy(xpath = TabsWrapper.ROOT_XPATH)
    private TabsWrapper tabsWrapper;

    public TrashBinTestCasePreview(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public SidebarGeneralTab openGeneralTab() {
        tabsWrapper.clickTab(TabsWrapper.Tabs.GENERAL);
        return new SidebarGeneralTab(getDriver());
    }
}
