package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestCaseSideBarView extends AbstractTestCasePreview<TestCaseSideBarView> {

    public static final String ROOT_XPATH = "//*[contains(@class,'test-cases-preview MuiBox-root')]";

    @FindBy(xpath = ".//div[@aria-label='Switch to modal view']//button")
    private Element toModalViewButton;

    public TestCaseSideBarView(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public TestCaseSideBarView(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    @Override
    public <T extends AbstractGeneralTab<?>> T openGeneralTab() {
        tabs().clickTab(TabsWrapper.Tabs.GENERAL);
        return (T) new SidebarGeneralTab(getDriver());
    }

}
