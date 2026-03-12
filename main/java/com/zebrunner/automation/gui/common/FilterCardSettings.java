package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//md-menu-content[@class='fixed-md-menu-content']
public class FilterCardSettings extends AbstractUIObject {

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//span[text()='Delete']")
    private Element delete;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//span[text()='Make public']")
    private ExtendedWebElement makePublic;


    public FilterCardSettings(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void delete() {
        delete.click();
    }

    public void clickMakePublic() {
        makePublic.click();
    }
}
