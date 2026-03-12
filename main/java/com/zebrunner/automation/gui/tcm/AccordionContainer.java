package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class AccordionContainer extends AbstractUIObject {

    public static final String ROOT_XPATH = "//ancestor::*[contains(@class,'tab-content-accordion') and contains(@class,'root')]";

    @FindBy(xpath = ".//*[@class='accordion__title']")
    private Element tabWysiwygTitle;

    @FindBy(xpath = ".//div[@class='toastui-editor-contents']")
    private Element content;

    @FindBy(xpath = WysiwygInputContainer.ROOT_LOCATOR)
    private WysiwygInputContainer wysiwygInput;

    @FindBy(xpath = ".//*[text()='Cancel']//parent::button")
    private Element cancelButton;

    @FindBy(xpath = ".//*[text()='Save']//parent::button")
    private Element saveButton;


    public AccordionContainer(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }


    public String getTitleValue() {
        return tabWysiwygTitle.getText();
    }

    public String getContentValue() {
        return content.getText();
    }

    public AccordionContainer changeDescription(String description) {
        content.click();
        wysiwygInput.input(description);
        saveButton.click();
        return this;
    }

    public AccordionContainer input(String text) {
        if (!content.isVisible(3)) {
            this.click();
        }
        content.click();
        wysiwygInput.input(text);
        return this;
    }

    public void clickSaveButton() {
        saveButton.click();
    }

    public void clickCancelButton() {
        cancelButton.click();
    }
}
