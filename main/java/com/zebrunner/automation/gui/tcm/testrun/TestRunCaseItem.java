package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.automation.gui.common.ListBoxMenu;
import com.zebrunner.automation.gui.tcm.DeleteModal;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.tcm.testcase.EditTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.AbstractTestCasePreview;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseModalView;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseSideBarView;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.NoSuchElementException;

public class TestRunCaseItem extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[contains(@class, 'run-case') and contains(@class, 'MuiBox-root')]";

    @FindBy(xpath = ".//span[contains(@class, 'run-case__title')]")
    private ExtendedWebElement titleLabel;

    @FindBy(xpath = ".//span[contains(@class, 'ZbrCheckbox')]")
    private ExtendedWebElement checkbox;

    @FindBy(xpath = ".//span[@class='assignee-select-option__name']")
    private ExtendedWebElement assignedUsername;

    @FindBy(xpath = ".//div[contains(@class,'assignee-select-placeholder')]")
    private ExtendedWebElement assignedOptions;

    @FindBy(xpath = ".//div[contains(@class, 'run-case__status')]")
    private ExtendedWebElement statusPlaceholder;

    @FindBy(xpath = ".//span[@aria-label='Edit']")
    @CaseInsensitiveXPath
    private Element editButton;

    @FindBy(xpath = ".//span[@aria-label='Delete']")
    @CaseInsensitiveXPath
    private Element deleteButton;

    public TestRunCaseItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getCaseTitle() {
        return titleLabel.getText();
    }

    public void clickCheckbox() {
        checkbox.click();
    }

    public String getAssignedUsername() {
        assignedOptions.hover();
        pause(1);
        return assignedUsername.getText();
    }

    public boolean isSelected() {
        return checkbox.getAttribute("class").contains("Mui-checked");
    }

    public EditTestCaseModal openEditTestCaseModal() {
        titleLabel.hover();
        editButton.clickByActions();

        return new EditTestCaseModal(getDriver());
    }

    public DeleteModal openDeleteModal() {
        titleLabel.hover();
        deleteButton.clickByActions();

        return new DeleteModal(getDriver());
    }

    public void selectUserForAssign(String username) {
        assignedOptions.hover();
        assignedOptions.click();

        new ListBoxMenu(getDriver()).findItem(username).click();
    }

    public void selectStatus(RerunModal.TestRunStatuses testRunStatus) {
        statusPlaceholder.click();

        new ListBoxMenu(getDriver()).findItem(testRunStatus.getValue()).click();
    }

    public String getStatus() {
        return statusPlaceholder.getText();
    }

    public <T extends AbstractTestCasePreview<?>> T clickTestCase() {
        this.click();

        AbstractTestCasePreview<?> preview = new TestCaseModalView(getDriver());
        if (preview.isPresent(7)) {
            return (T) preview;
        }

        AbstractTestCasePreview<?> sidebar = new TestCaseSideBarView(getDriver());
        sidebar.setBy(By.xpath("//div[@class='run-right-sidebar__container']"));
        if (sidebar.isPresent(7)) {
            return (T) sidebar;
        }

        throw new NoSuchElementException("Neither Test case modal preview nor side bar menu was opened!");
    }
}
