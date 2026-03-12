package com.zebrunner.automation.gui.tcm;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class DefaultEditorToolbar extends AbstractUIObject {

    public static final String ROOT_LOCATOR = "//*[@class = 'toastui-editor-defaultUI-toolbar']";

    @FindBy(xpath = ".//*[contains(@class, 'heading')]")
    private ExtendedWebElement headingButton;

    @FindBy(xpath = ".//*[contains(@class, 'bold')]")
    private ExtendedWebElement boldButton;

    @FindBy(xpath = ".//*[contains(@class, 'italic')]")
    private ExtendedWebElement italicButton;

    @FindBy(xpath = ".//*[contains(@class, 'strike')]")
    private ExtendedWebElement strikeButton;

    @FindBy(xpath = ".//*[contains(@class, 'bullet-list')]")
    private ExtendedWebElement unorderedListButton;

    @FindBy(xpath = ".//*[contains(@class, 'ordered-list')]")
    private ExtendedWebElement orderedListButton;

    @FindBy(xpath = ".//*[@class = 'toastui-editor-popup-body']")
    private WysiwygInputEditor wysiwygInputEditor;

    public DefaultEditorToolbar(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void clickBoldButton() {
        boldButton.click();
    }

    public void clickItalicButton() {
        italicButton.click();
    }

    public void clickStrikeButton() {
        strikeButton.click();
    }

    public void clickOrderedListButton() {
        orderedListButton.click();
    }

    public void clickUnOrderedListButton() {
        unorderedListButton.click();
    }

    public WysiwygInputEditor openHeadingEditor() {
        headingButton.click();
        return wysiwygInputEditor;
    }

    public void chooseHeading(WysiwygInputEditor.DropdownItemsEnum heading) {
        openHeadingEditor()
                .findItem(heading.getItemValue())
                .click();
    }
}
