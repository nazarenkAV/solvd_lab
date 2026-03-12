package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.InputContainer;
import com.zebrunner.automation.gui.common.ZbrAutocomplete;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ImportTestCasesModal extends AbstractModal<ImportTestCasesModal> {
    public static final String MODAL_TITLE = "Create test case";

    @FindBy(xpath = ".//*[text()='Source *']" + ZbrAutocomplete.PARENT_ROOT_XPATH)
    protected ZbrAutocomplete sourceInput;

    @FindBy(xpath = ".//*[text()='Target suite *']" + InputContainer.ROOT_XPATH)
    protected InputContainer targetSuiteInput;

    @FindBy(xpath = ".//div[@class='file-upload-wrapper']//input")
    protected ExtendedWebElement fileInput;

    public ImportTestCasesModal(WebDriver driver) {
        super(driver);
    }


    public SelectSuiteListBoxMenu clickTargetSuite() {
        targetSuiteInput.click();
        return new SelectSuiteListBoxMenu(getDriver());
    }

    public void typeTargetSuite(String parentSuiteName) {
        targetSuiteInput.input(parentSuiteName);
    }

    public ImportTestCasesModal selectTargetSuite(String parentSuiteName) {
        SelectSuiteListBoxMenu listBoxMenu = clickTargetSuite();
        listBoxMenu.clickItem(parentSuiteName);
        return this;
    }


    public ImportTestCasesModal addFile(String filePath) {
        fileInput.attachFile(filePath);
        return this;
    }

}
