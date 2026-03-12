package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class TabWysiwygContainer extends AbstractUIObject {

    public static final String ROOT_XPATH = "//parent::*[contains(@class,'tab-wysiwyg') and contains(@class,'root')]";

    @FindBy(xpath = ".//div[@class='tab-wysiwyg-title']")
    private Element tabWysiwygTitle;

    @FindBy(xpath = ".//div[contains(@class,'wysiwyg-viewer-container')]")
    private ExtendedWebElement wysiwygViewerContainer;

    @FindBy(xpath = ".//div[@class='toastui-editor-contents']")
    private Element content;

    @FindBy(xpath = WysiwygInputContainer.ROOT_LOCATOR)
    private WysiwygInputContainer wysiwygInput;

    @FindBy(xpath = ".//*[text()='Cancel']//parent::button")
    private Element cancelButton;

    @FindBy(xpath = ".//*[text()='Save']//parent::button")
    private Element saveButton;


    public TabWysiwygContainer(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public TabWysiwygContainer input(String text) {
        if (!content.isVisible(3)) {
            this.click();
        }
        content.clickIfPresent(3);
        wysiwygInput.input(text);
        return this;
    }

    public String getTitleValue() {
        return tabWysiwygTitle.getText();
    }

    public String getContentValue() {
        return content.getText();
    }

    public void clickSaveButton() {
        saveButton.click();
    }

    public void clickCancelButton() {
        cancelButton.click();
    }

    public boolean isReadOnly() {
        return wysiwygViewerContainer.getAttribute("class").contains("_read-only");
    }
}
