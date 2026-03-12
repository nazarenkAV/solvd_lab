package com.zebrunner.automation.gui.landing;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

//div[@class='header-wrapper']
public class LandingHeader extends AbstractUIObject {

    @FindBy(xpath = ".//div[@class='logo']")
    private ExtendedWebElement zebrunnerLogo;

    @FindBy(xpath = ".//a[@aria-label='home']")
    private ExtendedWebElement homePage;

    @FindBy(xpath = ".//div[text()='Products']")
    private ExtendedWebElement productsButton;

    @FindBy(xpath = ".//a[text()='Pricing']")
    private ExtendedWebElement pricingButton;

    @FindBy(xpath = ".//div[text()='Resources']")
    private ExtendedWebElement resourcesButton;

    @FindBy(xpath = ".//a[text()='Company']")
    private ExtendedWebElement companyButton;

    @FindBy(xpath = ".//a[@aria-current='page']")
    private Element activePage;

    @FindBy(xpath = ".//div[text()='Book a demo']")
    private Element btnBookDemo;

    @FindBy(xpath = ".//div[text()='Try for free']")
    private Element btnTryForFree;

    public LandingHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getActiveSection() {
        return activePage.getText();
    }

    public RequestDemoPage clickBookDemo() {
        btnBookDemo.click();
        return new RequestDemoPage(getDriver());
    }

    public RegisterPage clickTryItForFree() {
        btnTryForFree.click();
        return new RegisterPage(getDriver());
    }

    public MainLandingPage toHomePage() {
        homePage.click();
        return new MainLandingPage(getDriver());
    }

    public AboutUsPage clickCompany() {
        companyButton.click();
        return new AboutUsPage(getDriver());
    }

    public PricingPage clickPricing() {
        pricingButton.click();
        return new PricingPage(getDriver());
    }
}
