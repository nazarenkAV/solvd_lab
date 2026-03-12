package com.zebrunner.automation.gui.tcm.testsuite;

import com.zebrunner.automation.gui.common.InputContainer;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CreateOrEditSuiteModal extends AbstractModal<CreateOrEditSuiteModal> {
    public static final String TITLE_FOR_CREATION = "Create suite";
    public static final String TITLE_FOR_EDITION = "Edit Suite";

    @FindBy(xpath = ".//label[@for='name']" + InputContainer.ROOT_XPATH)
    private InputContainer suiteNameInput;

    @FindBy(xpath = ".//label[@for='description']" + InputContainer.ROOT_XPATH)
    private InputContainer descriptionInput;

    @FindBy(xpath = ".//*[text()='Parent suite *']" + InputContainer.ROOT_XPATH)
    protected InputContainer parentSuiteInput;

    public CreateOrEditSuiteModal(WebDriver driver) {
        super(driver);
    }

    public CreateOrEditSuiteModal inputName(String name) {
        suiteNameInput.getInput().sendKeys(name);
        return this;
    }

    public CreateOrEditSuiteModal inputDescription(String name) {
        descriptionInput.getInput().sendKeys(name);
        return this;
    }

    public SelectSuiteListBoxMenu clickSelectSuite() {
        parentSuiteInput.click();
        return new SelectSuiteListBoxMenu(getDriver());
    }

    public CreateOrEditSuiteModal selectSuite(String suiteName) {
        SelectSuiteListBoxMenu selectSuiteListBox = clickSelectSuite();
        selectSuiteListBox.findItem(suiteName)
                .click();
        return this;
    }

    public void typeParentSuite(String parentSuiteName) {
        parentSuiteInput.input(parentSuiteName);
    }
}
