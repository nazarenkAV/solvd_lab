package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class AbstractModalHeader extends AbstractUIObject {

    @FindBy(xpath = ".//div[@class='modal-header__inner']//h4")
    private ExtendedWebElement modalTitle;

    public AbstractModalHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTitleText() {
        return modalTitle.getText();
    }

}
