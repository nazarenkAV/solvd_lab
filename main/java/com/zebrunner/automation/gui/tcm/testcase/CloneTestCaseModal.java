package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.InputContainer;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CloneTestCaseModal extends AbstractModal<CloneTestCaseModal> {
    public static final String MODAL_TITLE = "Clone test case";

    @FindBy(xpath = ".//*[text()='Parent suite *']" + InputContainer.ROOT_XPATH)
    private InputContainer parentSuiteInput;

    @FindBy(xpath = ".//label[@for='name']" + InputContainer.ROOT_XPATH)
    private InputContainer testCaseNameInput;

    @FindBy(xpath = ".//button[text()='Clone']")
    protected Element cloneButton;


    public CloneTestCaseModal(WebDriver driver) {
        super(driver);
    }

    public SelectSuiteListBoxMenu clickParentSuite() {
        parentSuiteInput.click();
        return new SelectSuiteListBoxMenu(getDriver());
    }

    public CloneTestCaseModal selectParentSuite(String parentSuiteName) {
        SelectSuiteListBoxMenu listBoxMenu = clickParentSuite();
        listBoxMenu.clickItem(parentSuiteName);
        return this;
    }

    public CloneTestCaseModal inputTitle(String title) {
        testCaseNameInput.input(title);
        return this;
    }

    public void clickClone() {
        cloneButton.click();
    }

    public void typeParentSuite(String parentSuiteName) {
        parentSuiteInput.input(parentSuiteName);
    }
}