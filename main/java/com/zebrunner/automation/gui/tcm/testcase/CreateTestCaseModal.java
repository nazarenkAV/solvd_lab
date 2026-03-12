package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.InputContainer;
import com.zebrunner.automation.gui.tcm.WysiwygInputContainer;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.tcm.repository.SelectSuiteListBoxMenu;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class CreateTestCaseModal extends AbstractModal<CreateTestCaseModal> {
    public static final String MODAL_TITLE = "Create test case";

    @FindBy(xpath = ".//label[@for='name']" + InputContainer.ROOT_XPATH)
    protected InputContainer testCaseNameInput;

    @FindBy(xpath = ".//*[text()='Description']" + WysiwygInputContainer.PARENT_ROOT_LOCATOR)
    protected WysiwygInputContainer descriptionInput;

    @FindBy(xpath = ".//*[text()='Pre-conditions']" + WysiwygInputContainer.PARENT_ROOT_LOCATOR)
    protected WysiwygInputContainer preConditionsInput;

    @FindBy(xpath = ".//*[text()='Post-conditions']" + WysiwygInputContainer.PARENT_ROOT_LOCATOR)
    protected WysiwygInputContainer postConditionsInput;

    @FindBy(xpath = ".//*[text()='Parent suite *']" + InputContainer.ROOT_XPATH)
    protected InputContainer parentSuiteInput;

    @FindBy(xpath = ".//*[@class='repository-case-modal-attachments']//input")
    protected ExtendedWebElement attachmentsInput;

    public CreateTestCaseModal(WebDriver driver) {
        super(driver);
    }

    public CreateTestCaseModal inputTitle(String title) {
        testCaseNameInput.input(title);
        return this;
    }

    public CreateTestCaseModal inputDescription(String description) {
        descriptionInput.input(description);
        return this;
    }

    public CreateTestCaseModal inputPreConditions(String preConditions) {
        preConditionsInput.input(preConditions);
        return this;
    }

    public CreateTestCaseModal inputPostConditions(String postConditions) {
        postConditionsInput.input(postConditions);
        return this;
    }

    public SelectSuiteListBoxMenu clickParentSuite() {
        parentSuiteInput.click();
        return new SelectSuiteListBoxMenu(getDriver());
    }

    public CreateTestCaseModal selectParentSuite(String parentSuiteName) {
        SelectSuiteListBoxMenu listBoxMenu = clickParentSuite();
        listBoxMenu.clickItem(parentSuiteName);
        return this;
    }

    public InputContainer selectParentSuite(String parentSuiteName, String pathContaining) {
        SelectSuiteListBoxMenu listBoxMenu = clickParentSuite();
        listBoxMenu.clickItem(parentSuiteName, pathContaining);
        return parentSuiteInput;
    }

    public CreateTestCaseModal addAttachment(String filePath) {
        attachmentsInput.attachFile(filePath);
        return this;
    }

    public void typeParentSuite(String parentSuiteName) {
        parentSuiteInput.input(parentSuiteName);
    }
}
