package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.tcm.AccordionContainer;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class SidebarGeneralTab extends AbstractGeneralTab<SidebarGeneralTab> {

    public static final String ROOT_XPATH = ".//div[@role='tabpanel' and contains(@id,'General')]";

    @FindBy(xpath = "//p[contains(@class, 'title') and text() = 'Pre-conditions']")
    private ExtendedWebElement preconditionsTitleExpander;

    @FindBy(xpath = "//div[contains(@class, 'editor-contents')]/p[text()='Pre-conditions']")
    private ExtendedWebElement preconditionsExpectedInput;

    @FindBy(xpath = ".//div[text()='Post-conditions']" + AccordionContainer.ROOT_XPATH)
    private AccordionContainer postConditionsInput;

    public SidebarGeneralTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public boolean isPreconditionsInputActive() {
        preconditionsTitleExpander.click();
        return preconditionsExpectedInput.isClickable(2);
    }
}
