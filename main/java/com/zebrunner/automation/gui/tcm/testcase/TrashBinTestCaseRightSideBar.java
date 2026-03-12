package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public class TrashBinTestCaseRightSideBar extends AbstractUIObject {

    public static final String ROOT_XPATH = "//div[@class='run-right-sidebar']";

    @FindBy(xpath = ".//a[@class='header-case-link']")
    private Element headerCaseLink;


    public TrashBinTestCaseRightSideBar(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public DedicatedTestCasePage clickTestCaseKeyAndSwitchTab() {
        int tabsBeforeClick = PageUtil.getNumberOfOpenedWindows(getDriver());

        headerCaseLink.hover();
        headerCaseLink.click();

        int tabsAfterClick = PageUtil.getNumberOfOpenedWindows(getDriver());
        Assert.assertEquals(tabsAfterClick - tabsBeforeClick, 1, "Test case is not opened in new tab!");

        PageUtil.toOtherTab(getDriver());

        return new DedicatedTestCasePage(getDriver());
    }
}
