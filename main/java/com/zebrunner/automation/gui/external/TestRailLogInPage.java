package com.zebrunner.automation.gui.external;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.PageOpeningStrategy;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestRailLogInPage extends AbstractPage {

    @FindBy(xpath = "//*[contains(@class, 'loginpage-logo')]")
    private ExtendedWebElement loginPageLogo;

    @FindBy(id = "name")
    private ExtendedWebElement emailTextField;

    @FindBy(id = "password")
    private ExtendedWebElement passwordTextField;

    @FindBy(id = "button_primary")
    private ExtendedWebElement loginButton;

    public TestRailLogInPage(WebDriver driver) {
        super(driver);
        setPageOpeningStrategy(PageOpeningStrategy.BY_ELEMENT);
        setUiLoadedMarker(loginPageLogo);
    }

    public void typeEmail(String email) {
        emailTextField.type(email);
    }

    public void typePassword(String password) {
        passwordTextField.type(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }
}
