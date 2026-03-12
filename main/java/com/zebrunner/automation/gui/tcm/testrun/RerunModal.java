package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.ZbrCheckbox;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class RerunModal extends AbstractModal<RerunModal> {

    @FindBy(xpath = ".//div[contains(@class, 'run-select-tests-modal-body')]" + RerunOption.ROOT_XPATH)
    private List<RerunOption> options;

    @FindBy(xpath = ".//*[text()='Copy Assigned To']" + ZbrCheckbox.PARENT_ROOT_XPATH)
    private ZbrCheckbox copyAssignedToCheckbox;

    @FindBy(xpath = ".//button[text()='Ок']")
    @CaseInsensitiveXPath
    private ExtendedWebElement okButton;

    @FindBy(xpath = ".//button[contains(@class, 'tertiary')]")
    private ExtendedWebElement crossButton;

    public RerunModal(WebDriver driver) {
        super(driver);
    }

    public boolean isModalOpened() {
        return header.isPresent(3);
    }

    public void clickCrossButton() {
        crossButton.click();
    }

    public List<RerunOption> getExistingOptions() {
        return options;
    }

    public RerunOption getOption(String optionName) {
        return WaitUtil.waitElementAppearedInListByCondition(options,
                (option -> option.getOptionName().equalsIgnoreCase(optionName)),
                "Test status with name '" + optionName + "' was found in grid!",
                "Test status with name '" + optionName + "' not found in grid"
        );
    }

    public void selectOption(TestRunStatuses testRunStatus) {
        getOption(testRunStatus.getValue()).clickCheckbox();
    }

    public boolean isOptionSelected(TestRunStatuses testRunStatus) {
        return getOption(testRunStatus.getValue()).isOptionSelected();
    }

    public void clickCopyAssignedTo() {
        copyAssignedToCheckbox._click();
    }

    public CreateTestRunPage clickOkButton() {
        okButton.click();
        return new CreateTestRunPage(getDriver());
    }

    @Getter
    @AllArgsConstructor
    public enum TestRunStatuses {
        ALL("All"),
        UNTESTED("Untested"),
        PASSED("Passed"),
        FAILED("Failed"),
        SKIPPED("Skipped"),
        RETEST("Retest"),
        BLOCKED("Blocked"),
        INVALID("Invalid");

        private final String value;
    }
}
