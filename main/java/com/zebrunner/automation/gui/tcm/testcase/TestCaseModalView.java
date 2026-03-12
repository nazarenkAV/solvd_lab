package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class TestCaseModalView extends AbstractTestCasePreview<TestCaseModalView> {
    public static final String ROOT_XPATH = "//*[@role='dialog']";

    @FindBy(xpath = ".//div[@aria-label='Switch to sidebar view']//button")
    private Element toSidebarViewButton;

    public TestCaseModalView(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    @Override
    public <T extends AbstractGeneralTab<?>> T openGeneralTab() {
        tabs().clickTab(TabsWrapper.Tabs.GENERAL);
        return (T) new ModalGeneralTab(getDriver());
    }

}
