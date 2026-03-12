package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//*[@class='md-sidenav-right test-sessions-sidenav ng-isolate-scope _md md-whiteframe-1dp']
public class ResultSessionWindowR extends AbstractUIObject {
    //    @FindBy(xpath = ".//button[contains(@class, 'sidenav__close')]")
    //    private ExtendedWebElement closeButton;

    @FindBy(xpath = "//v-accordion[@class='test-sessions__accordion ng-scope ng-isolate-scope']")
    private TestSessionR session;

    @FindBy(xpath = "//h2[@class='test-sessions-sidenav__title']")
    private ExtendedWebElement title;

    public ResultSessionWindowR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void closeWindow() {
        //  closeButton.click();
        PageUtil.guaranteedToHideDropDownList(getDriver());
        pause(3);
    }
}
