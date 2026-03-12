package com.zebrunner.automation.gui.tcm.testrun.testcase;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class ModalBaseSuiteItem extends AbstractUIObject {
    public static final String ANCESTOR_XPATH = "div[@data-index]";

    @FindBy(xpath = ".//span[@class='select-test-cases-modal__suite-title']")
    private ExtendedWebElement title;

    @FindBy(xpath = ".//input[contains(@class, 'PrivateSwitchBase-input')]")
    private ExtendedWebElement checkbox;

    @FindBy(xpath = ".//span[@class='select-test-cases-modal__suite-counts']")
    private ExtendedWebElement testCasesCount;

    @FindBy(xpath = ".//*[name()='svg' and @class='select-test-cases-modal__arrow']")
    private ExtendedWebElement arrowIcon;

    public ModalBaseSuiteItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getSuiteName() {
        return title.getText();
    }

    public TestCasesPanel select() {
        if (!checkbox.isChecked()) {
            log.info("Selecting test suite " + getSuiteName());
            checkbox.click();
        }

        return new TestCasesPanel(getDriver());
    }

    public TestCasesPanel clickOnName() {
        log.info("Clicking on suite " + title.getText());
        title.click();
        return new TestCasesPanel(getDriver());
    }
}
