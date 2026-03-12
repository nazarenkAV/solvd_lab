package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
@Slf4j
public class CustomCapabilitiesSection extends AbstractUIObject {

    public static final String CUSTOM_CAPABILITY_SECTION = "Custom capability";

    @FindBy(xpath = "//div[@class='custom-vars-add__row']")
    private CustomVariableAddingForm variable;

    @FindBy(xpath = "//section[contains(@class,'_custom-capabilities')]//div[@class='custom-vars-add__row']")
    private List<CustomVariableAddingForm> customCapabilitiesList;

    @FindBy(xpath = "//*[text()='Add capability']//ancestor::button")
    private Element addCapabilityButton;

    public CustomCapabilitiesSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CustomVariableAddingForm createNewCapability() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
                addCapabilityButton.getElement());
        pause(1);
        addCapabilityButton.click();
        return customCapabilitiesList.get(customCapabilitiesList.size() - 1);
    }

    public CustomVariableAddingForm createNewCapability(CustomVariable var) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        //   ComponentUtil.scrollToElementCenter(getDriver(), addVariableButton.getElement());
        addCapabilityButton.click();
        return customCapabilitiesList.get(customCapabilitiesList.size() - 1)
                .fillCustomVariable(var);
    }

    public boolean isVariablePresentInCapabilitiesList(String varName) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Trying to find capability in vars list ....");
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(customCapabilitiesList,
                var -> var.getVariableName().equalsIgnoreCase(varName)
        );
    }

    public CustomVariableAddingForm getCapabilityWithName(String variableName) {
        log.info("Trying to find env variable form with variable name {}", variableName);
        PageUtil.guaranteedToHideDropDownList(getDriver());
        return WaitUtil.waitElementAppearedInListByCondition(customCapabilitiesList,
                var -> var.getVariableName().trim().equalsIgnoreCase(variableName),
                "Variable with name " + variableName + " was found",
                "Variable with name " + variableName + " was not found");
    }
}
