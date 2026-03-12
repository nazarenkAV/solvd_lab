package com.zebrunner.automation.gui.tcm;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
@Getter
public class WysiwygInputContainer extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//*[contains(@class,'wysiwyg-container')]";
    public static final String PARENT_ROOT_LOCATOR = "//parent::div" + ROOT_LOCATOR;

    @FindBy(xpath = ".//div[contains(@class,'ProseMirror toastui-editor-contents')]")
    private ExtendedWebElement input;

    @FindBy(xpath = "." + DefaultEditorToolbar.ROOT_LOCATOR)
    private DefaultEditorToolbar defaultEditorToolbar;

    public WysiwygInputContainer(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void input(String text) {
        input.type(text);
    }

    public String getInputValue() {
        return input.getText();
    }
}