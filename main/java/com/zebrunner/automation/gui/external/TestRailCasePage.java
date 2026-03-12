package com.zebrunner.automation.gui.external;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.PageOpeningStrategy;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestRailCasePage extends AbstractPage {

    @FindBy(xpath = "//*[@data-testid = 'testCaseContentHeaderTitle']")
    private ExtendedWebElement testCaseTitle;

    public TestRailCasePage(WebDriver driver) {
        super(driver);
        setPageOpeningStrategy(PageOpeningStrategy.BY_ELEMENT);
        setUiLoadedMarker(testCaseTitle);
    }

    public String getTestCaseTitleText() {
        return testCaseTitle.getText();
    }
}
