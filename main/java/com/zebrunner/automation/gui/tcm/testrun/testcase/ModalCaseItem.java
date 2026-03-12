package com.zebrunner.automation.gui.tcm.testrun.testcase;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class ModalCaseItem extends AbstractUIObject {
    public static final String ANCESTOR_XPATH = ".//div[@data-index]";

    @FindBy(xpath = ".//input[contains(@class, 'PrivateSwitchBase-input')]")
    private ExtendedWebElement checkbox;

    @FindBy(xpath = ".//span[contains(@class, 'select-test-cases-modal__case-title')]")
    private ExtendedWebElement title;

    @FindBy(xpath = ".//span[@class='draft-label']")
    private ExtendedWebElement draftLabel;

    public ModalCaseItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTestCaseName() {
        return title.getText();
    }

    public void select() {
        if (!checkbox.isChecked()) {
            log.info("Selecting test case " + getTestCaseName());
            checkbox.click();
        }
    }

    public boolean isCaseDeprecated() {
        return title.getAttribute("class").contains("deprecated");
    }

    public boolean isCaseDraft() {
        return draftLabel.isPresent(3);
    }
}
