package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
@Slf4j
public class PropertiesTab extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[@role='tabpanel' and contains(@id,'Properties')]";
    public static final String TAB_CONTENT_CONTENT = ".//*[contains(@class,'tab-content__content')]";
    public static final String TAB_CONTENT_COLUMN_CONTENT = ".//*[contains(@class,'tab-content__column-content')]";
    public static final String TAB_CONTENT_AUTHOR = ".//*[contains(@class,'tab-content-author')]";
    private static final String INPUT_ELEMENT = ".//input";

    @CaseInsensitiveXPath
    @FindBy(xpath = "//*[text()='%s']//ancestor::*[contains(@class,'test-case-tab-content__column')]")
    private ExtendedWebElement tabContent;


    public PropertiesTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public boolean isTabPresent(String tabName) {
        log.info("Verifying tab " + tabName);
        return format(tabContent, tabName).isPresent(3);
    }

    public boolean isTabClickable(String tabName) {
        log.info("Verifying tab " + tabName);
        return format(tabContent, tabName)
                .findExtendedWebElement(By.xpath(TAB_CONTENT_CONTENT))
                .isClickable(3);
    }

    public ExtendedWebElement getTab(TabContentNames tabName) {
        return format(tabContent, tabName.getUiName());
    }

    @Getter
    public enum TabContentNames {

        AUTHOR("Author"),
        CREATED_ON("Created on"),
        PRIORITY("Priority"),
        AUTOMATION_STATE("Automation State"),
        DEPRECATED("Deprecated"),
        DRAFT("Draft"),
        ;

        private final String uiName;

        TabContentNames(String tabName) {
            this.uiName = tabName;
        }
    }

    public String getAuthorValue() {
        return format(tabContent, TabContentNames.AUTHOR.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_CONTENT + "//p")).getText();
    }

    public UserInfoTooltip hoverAuthorLabel() {
        format(tabContent, TabContentNames.AUTHOR.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_AUTHOR + "//p")).hover();

        pause(1);
        return new UserInfoTooltip(getDriver());
    }

    public String getCreatedOnValue() {
        return format(tabContent, TabContentNames.CREATED_ON.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_CONTENT + "//p")).getText();
    }

    public String getPriorityValue() {
        return format(tabContent, TabContentNames.PRIORITY.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_COLUMN_CONTENT + "//input"))
                .getAttribute("value");
    }

    public void selectPriority(String priority) {
        format(tabContent, TabContentNames.PRIORITY.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_COLUMN_CONTENT + "//input"))
                .click(5);

        ListBoxMenu listBoxMenu = new ListBoxMenu(getDriver());
        listBoxMenu.clickItem(priority);
    }

    public String getAutomationStateValue() {
        return format(tabContent, TabContentNames.AUTOMATION_STATE.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_CONTENT + "//input"))
                .getAttribute("value");
    }

    public String getDeprecatedValue() {
        return format(tabContent, TabContentNames.DEPRECATED.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_CONTENT + "//input"))
                .getAttribute("value");
    }

    public String getDraftValue() {
        return format(tabContent, TabContentNames.DRAFT.getUiName())
                .findExtendedWebElement(By.xpath(TAB_CONTENT_CONTENT + "//input"))
                .getAttribute("value");
    }

    public boolean isPriorityReadOnly() {
        return format(tabContent, TabContentNames.PRIORITY.getUiName())
                .findExtendedWebElement(By.xpath(INPUT_ELEMENT), 2) == null;

    }

    public boolean isAutomationStateReadOnly() {
        return format(tabContent, TabContentNames.AUTOMATION_STATE.getUiName())
                .findExtendedWebElement(By.xpath(INPUT_ELEMENT), 2) == null;
    }

    public boolean isDeprecatedReadOnly() {
        return format(tabContent, TabContentNames.DEPRECATED.getUiName())
                .findExtendedWebElement(By.xpath(INPUT_ELEMENT), 2) == null;
    }

    public boolean isDraftReadOnly() {
        return format(tabContent, TabContentNames.DRAFT.getUiName())
                .findExtendedWebElement(By.xpath(INPUT_ELEMENT), 2) == null;
    }
}
