package com.zebrunner.automation.gui.tcm.testsuite;

import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.tcm.testcase.CreateTestCaseModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class SuiteItemActions extends AbstractUIObject {

    public final static String ROOT_XPATH = ".//div[contains(@class,'repository-virtuoso-item-suite__actions')]";

    @FindBy(xpath = ".//button[@aria-label='Create a sub-suite or case']")
    private ExtendedWebElement createSubSuiteOrCaseBtn;

    @FindBy(xpath = ".//button[@aria-label='Edit']")
    private ExtendedWebElement editBtn;

    @FindBy(xpath = ".//button[@aria-label='Delete']")
    private ExtendedWebElement deleteBtn;

    @FindBy(xpath = ".//div[@aria-label='Copy link']//button")
    private ExtendedWebElement copyLinkBtn;

    @FindBy(xpath = ".//div[@aria-label='Copy ID']//button")
    private ExtendedWebElement copyIdBtn;

    public SuiteItemActions(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    @Getter
    @AllArgsConstructor
    public enum MenuItemsEnum {
        CREATE_SUB_SUITE("Create sub-suite"),
        CREATE_CASE("Create case"),
        ;
        private String name;
    }

    public CreateOrEditSuiteModal clickCreateSubSuiteOrCaseButtonAndSelectCreateSubSuite() {
        createSubSuiteOrCaseBtn.hover();
        createSubSuiteOrCaseBtn.click();

        Menu menu = new Menu(getDriver());
        menu.findItem(MenuItemsEnum.CREATE_SUB_SUITE.getName())
                .click();
        return new CreateOrEditSuiteModal(getDriver());
    }

    public CreateTestCaseModal clickCreateSubSuiteOrCaseButtonAndSelectCreateCase() {
        createSubSuiteOrCaseBtn.hover();
        createSubSuiteOrCaseBtn.click();

        Menu menu = new Menu(getDriver());
        menu.findItem(MenuItemsEnum.CREATE_CASE.getName())
                .click();
        return new CreateTestCaseModal(getDriver());
    }

    public void clickCopyLink() {
        copyLinkBtn.click();
    }

    public void clickCopyId() {
        copyIdBtn.click();
    }

    public void clickEdit() {
        editBtn.click();
    }
}
