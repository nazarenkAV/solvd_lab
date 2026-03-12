package com.zebrunner.automation.gui.common;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.carina.webdriver.helper.IPageActionsHelper;
import com.zebrunner.carina.webdriver.helper.IPageDataHelper;

@Slf4j
@Getter
@Deprecated
public abstract class AbstractModal<T extends AbstractModal> extends AbstractUIObject implements IPageDataHelper, IPageActionsHelper {

    public static final String ROOT_LOCATOR =  "//*[@role='dialog']";

    @FindBy(xpath = ".//*[@id='modal-header']")
    protected AbstractModalHeader header;

    @FindBy(xpath = ".//*[@class='modal-header__title']")
    protected Element modalTitle;

    @FindBy(xpath = ".//button[contains(@class, 'tertiary')]")
    protected Element close;

    @FindBy(xpath = ".//button[text()='Cancel']")
    protected Element cancelButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Save' or text()='Create' or text()='Submit' or text()='Add']")
    protected Element submitButton;

    @FindBy(xpath = ".//button[text()='Delete']")
    protected Element deleteButton;

    @FindBy(xpath = ".//button[text()='Next']")
    protected Element nextButton;

    public AbstractModal(WebDriver driver) {
        super(driver, driver);
        setBy(By.xpath(AbstractModal.ROOT_LOCATOR));
        setName(this.getClass().getSimpleName());
    }

    public AbstractModal(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getModalTitleText() {
        return modalTitle.getText();
    }

    public T clickNext() {
        nextButton.waitUntil(Condition.CLICKABLE);
        nextButton.click();
        return (T) this;
    }

    public T clickCancel() {
        cancelButton.click();
        return (T) this;
    }

    public T clickDelete() {
        deleteButton.click();
        return (T) this;
    }

    public T submitModal() {
        submitButton.getRootExtendedElement().isPresent();
        submitButton.click();
        return (T) this;
    }

    public T clickClose() {
        close.click();
        return (T) this;
    }

    public boolean isModalOpened() {
        return this.isUIObjectPresent(10);
    }

}
