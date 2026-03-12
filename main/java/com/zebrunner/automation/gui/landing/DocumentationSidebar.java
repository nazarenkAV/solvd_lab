package com.zebrunner.automation.gui.landing;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//div[@class='sidebar']
public class DocumentationSidebar extends AbstractUIObject {

    @FindBy(xpath = "//li[@class='md-nav__item md-nav__item--active']")
    private Element activeSection;

    public DocumentationSidebar(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getActiveSection() {
        return activeSection.getText();
    }
}
