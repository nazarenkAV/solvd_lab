package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.ZbrAutocomplete;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
@Slf4j
public class CapabilitiesEditModalRaw extends AbstractUIObject {
    @FindBy(xpath = ".//div[@class='capabilities-edit-modal__capability-name']")
    private Element capabilityName;

    @FindBy(xpath = ".//input")
    private Element capabilityValueInput;

    @FindBy(xpath = ".//input[@value='true']/ancestor::span")
    private Element trueCheckbox;// only for boolean type

    @FindBy(xpath = ".//input[@value='false']/ancestor::span")
    private Element falseCheckbox;// only for boolean type

    @FindBy(xpath = "//*[contains(@class,'MuiAutocomplete-listbox')]//li")
    private List<Element> choiceValueList;

    public CapabilitiesEditModalRaw(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getNameOfCapability() {
        return capabilityName.getText();
    }

    public String getValueOfCapability() {
        return capabilityValueInput.getAttributeValue("value");
    }

    public List<String> getChoiceValues() {
        capabilityValueInput.click();
        pause(1);
        ZbrAutocomplete zbrAutocomplete = new ZbrAutocomplete(getDriver());
        zbrAutocomplete.getOptionList();
        return zbrAutocomplete.getOptionList();
    }

    public void typeCapabilityValue(String newValue) {
        capabilityValueInput.sendKeys(newValue);
    }

    public boolean isActiveTrue() {
        log.info("Checking true switcher...");
        return trueCheckbox.getAttributeValue("class").contains("Mui-checked");
    }

    public boolean isActiveFalse() {
        log.info("Checking false switcher...");
        return falseCheckbox.getAttributeValue("class").contains("Mui-checked");
    }

    public CapabilitiesEditModalRaw selectChoiceValueFromList(String value) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        capabilityValueInput.click();
        ZbrAutocomplete zbrAutocomplete = new ZbrAutocomplete(getDriver());
        zbrAutocomplete.getOption(value)
                .get()
                .click();
        return this;
    }
}
