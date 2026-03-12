package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@Getter
@Slf4j
public abstract class AbstractTestCasePreview<T extends AbstractTestCasePreview> extends AbstractUIObject {

    @FindBy(xpath = ".//a[@class='header-case-link']")
    private Element headerCaseLink;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//div[contains(@aria-label, 'Switch') and contains(@aria-label, 'view')]//button")
    private Element switchViewButton;

    @FindBy(xpath = ".//*[@d = '" + SvgPaths.CLOSE_X_ICON +
            "']//ancestor::button")
    private ExtendedWebElement closeButton;

    @FindBy(xpath = TabsWrapper.ROOT_XPATH)
    private TabsWrapper tabsWrapper;

    @FindBy(xpath = ".//*[@d = '" + SvgPaths.THREE_DOTS + "']//ancestor::button")
    private ExtendedWebElement settingsButton;

    public AbstractTestCasePreview(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public AbstractTestCasePreview(WebDriver driver) {
        super(driver);
    }

    public String getCaseKey() {
        return headerCaseLink.getText();
    }

    public String getCaseLink() {
        return headerCaseLink.getAttributeValue("href");
    }

    public <T extends AbstractTestCasePreview<?>> T toModalView() {
        if (!(this instanceof TestCaseModalView)) {
            log.info("Switching to modal view....");
            this.switchViewButton.click();
            return (T) new TestCaseModalView(getDriver());
        } else {
            log.info("We are already using modal view!");
            return (T) this;
        }
    }

    public <T extends AbstractTestCasePreview<?>> T toSideBarView() {
        if (!(this instanceof TestCaseSideBarView)) {
            log.info("Switching to sidebar view....");
            this.switchViewButton.click();
            return (T) new TestCaseSideBarView(getDriver());
        } else {
            log.info("We are already using sidebar view!");
            return (T) this;
        }
    }

    public Dropdown clickSettings() {
        settingsButton.click();
        return new Dropdown(getDriver());
    }

    public TabsWrapper tabs() {
        return tabsWrapper;
    }

    public abstract <T extends AbstractGeneralTab<?>> T openGeneralTab();

    public PropertiesTab openPropertiesTab() {
        tabs().clickTab(TabsWrapper.Tabs.PROPERTIES);
        return new PropertiesTab(getDriver());
    }

    public AttachmentsTab openAttachmentsTab() {
        tabs().clickTab(TabsWrapper.Tabs.ATTACHMENTS);
        return new AttachmentsTab(getDriver());
    }

    public ExecutionsTab openExecutionsTab() {
        tabs().clickTab(TabsWrapper.Tabs.EXECUTIONS);
        return new ExecutionsTab(getDriver());
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