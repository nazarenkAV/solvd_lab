package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.carina.webdriver.locator.FindAny;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@Getter
@Slf4j
public class TestingPlatformSection extends AbstractUIObject {
    @FindBy(xpath = ".//div[contains(@class,'provider-capability-select__input')]//div[@role='presentation']")
    private Element os;

    @FindBy(xpath = ".//button[contains(@class,'provider-capability-select__input')]")
    private Element browser;

    @FindBy(xpath = ".//span[@class='selected-system-label-text']")
    private Element testingPlatformName;

    @FindBy(xpath = ".//span[contains(text(),'Choose device')]//following-sibling::div")
    private Element deviceInput;

    @FindAny({ @FindBy(xpath = ".//div[contains(@class,'dropdown-with-icon__button no-animate')]"),
            @FindBy(xpath = ".//div[contains(@class,'selected-system-label') and not(contains(@class,'arrow'))]") })
    private Element testingPlatformButton;// should have this element to check that element disabled(only it has value _disabled in class)

    @FindBy(xpath = ".//input[@id='var']")
    private Element customVarsInput;

    @FindBy(xpath = ".//button[contains(@class,'custom-vars-view__input-icon')]")
    private Element viewCustomVarsButton;

    @FindBy(xpath = "//li[@role='menuitem']")
    private List<Element> testingPlatformsList;

    public TestingPlatformSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getCapabilityVariables() {
        return customVarsInput.getAttributeValue("value");
    }

    public CustomCapabilitiesModal openCustomCapabilitiesModal() {
        viewCustomVarsButton.click();
        pause(1);
        return new CustomCapabilitiesModal(getDriver());
    }

    public TestingPlatformSection selectPlatform(String platformName) throws NoSuchElementException {
        log.info("Trying to select platform with name: {}", platformName);
        PageUtil.guaranteedToHideDropDownList(getDriver());
        testingPlatformName.click();
        WaitUtil.waitElementAppearedInListByCondition(testingPlatformsList,
                        platform -> platform.getText().equalsIgnoreCase(platformName),
                        "Platform with name " + platformName + " was found",
                        "There are no platform with name " + platformName)
                .click();
        return this;
    }

    public String getSelectedTestingPlatform() {
        pause(1);
        return testingPlatformName.getText();
    }

    public boolean isSelectedTestingPlatformDisabled() {
        log.info("Checking that testing platform disabled....");
        testingPlatformButton.waitUntil(Condition.VISIBLE);
        return testingPlatformButton.getRootExtendedElement().getAttribute("class").contains("_disabled");
    }

    public OperationSystemWindow clickOperationSystem() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        os.waitUntil(Condition.VISIBLE);
        os.click();
        return new OperationSystemWindow(getDriver());
    }

    public String getSelectedOS() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Getting selected os.....");
        os.waitUntil(Condition.VISIBLE);
        pause(1);
        return os.getText();
    }

    public BrowsersModal openBrowserChoosingModal() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        browser.click();
        log.info("browser clicked....");

        if (!waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(BrowsersModal.rootXpath)),3)){
            log.info("Browser modal is not visible");
            browser.click();
        } else  {
            log.info("Browser modal is visible");
        }
        return BrowsersModal.openModal(getDriver());
    } //problem with ff browser, cannot open browser modal from first click

    public String getSelectedBrowser() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Getting selected browser.....");
        browser.waitUntil(Condition.VISIBLE);
        return browser.getText();
    }

    public String getSelectedDevice() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Getting selected device.....");
        return deviceInput.getText();
    }

    public AndroidDeviceModal openDeviceChoosingModal() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        deviceInput.click();
        return AndroidDeviceModal.openModal(getDriver());
    }
}
