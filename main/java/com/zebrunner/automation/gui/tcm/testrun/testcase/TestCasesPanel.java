package com.zebrunner.automation.gui.tcm.testrun.testcase;

import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

public class TestCasesPanel extends AbstractUIObject {
    public static final String ROOT_XPATH = "//div[@class='select-test-cases-modal__case-list']";

    @FindBy(xpath = ROOT_XPATH + "//input[contains(@class, 'PrivateSwitchBase-input')]")
    private ExtendedWebElement checkbox;

    @FindBy(xpath = ROOT_XPATH + "//h4")
    private ExtendedWebElement title;

    @FindBy(xpath = ModalCaseItem.ANCESTOR_XPATH)
    private List<ModalCaseItem> testCases;

    public TestCasesPanel(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public List<ModalCaseItem> getTestCases() {
        WaitUtil.waitCheckListIsNotEmpty(testCases);

        return testCases;
    }

    public void selectTestCase(String testCaseName) {
        testCases = getTestCases();

        testCases.stream()
                .filter(testCase -> testCase.getTestCaseName().equalsIgnoreCase(testCaseName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test case '" + testCaseName + "' not found"))
                .select();
    }
}
