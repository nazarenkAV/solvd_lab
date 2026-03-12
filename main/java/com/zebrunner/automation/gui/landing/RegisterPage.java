package com.zebrunner.automation.gui.landing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

import com.zebrunner.automation.legacy.APIContextManager;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

public class RegisterPage extends LandingBasePage {


    public static final String PAGE_NAME = "Register";

    private final String RANGES_LIST_XPATH = "//ul[@class='list']//li[ @class='option']";

    @FindBy(xpath = "//input[@name='ownerName']")
    private Element ownerNameInput;

    @FindBy(xpath = "//input[@name='email']")
    private Element emailInput;

    @FindBy(xpath = "//input[@name='name']")
    private Element companyNameInput;

    @FindBy(xpath = "//div[contains(@class,'company-size')]")
    private Element companySize;

    @FindBy(xpath = RANGES_LIST_XPATH)
    private List<Element> ranges;

    @FindBy(xpath = "//div[@id='start-button']")
    private Element btnSubmit;

    @FindBy(xpath = "//div[@id='cookiescript_close']")
    private ExtendedWebElement closeCookieBtn;

    public RegisterPage(WebDriver driver) {
        super(driver);
        super.setPageAbsoluteURL(APIContextManager.LANDING_URL + "/register?plan=professional&product=TP");
    }

    public String getExpectedPageTitle() {
        return PAGE_NAME;
    }

    public void createWorkspace(String name, String email, String companyName) {
        ownerNameInput.sendKeys(name);
        emailInput.sendKeys(email);
        companyNameInput.sendKeys(companyName);
        this.selectCompanySize();

        btnSubmit.click();
    }

    public void selectCompanySize() {
        companySize.click();

        WaitUtil.waitNotEmptyListOfElements(getDriver(), RANGES_LIST_XPATH);

        Element range = StreamUtils.findFirst(ranges, existingRange -> existingRange.getText().contains("50+"))
                                   .orElseThrow(() -> new NoSuchElementException("Couldn't select company size '50+'"));

        range.click();
    }

    public void closeCookie() {
        if (closeCookieBtn.isClickable(15)) {
            closeCookieBtn.click();
        }
    }

}
