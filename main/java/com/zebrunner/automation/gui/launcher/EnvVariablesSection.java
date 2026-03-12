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
public class EnvVariablesSection extends AbstractUIObject {

    public static final String ENVIRONMENT_VARIABLE_SECTION = "Environment variable";

    @FindBy(xpath = "//*[text()='Add variable']//ancestor::button")
    private Element addVariableButton;

    @FindBy(xpath = "//section[contains(@class,'_env-variables')]//div[@class='custom-vars-add__row']")
    private List<EnvVarsAdd> envVariablesList;

    public EnvVariablesSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CustomVariableAddingForm createNewEnvironmentVariable() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
                addVariableButton.getElement());
        pause(1);
        addVariableButton.click();
        return envVariablesList.get(envVariablesList.size() - 1);
    }

    public CustomVariableAddingForm createNewEnvVariable(CustomVariable var) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        ///   ComponentUtil.scrollToElementCenter(getDriver(), addVariableButton.getElement());
        addVariableButton.click();
        return envVariablesList.get(envVariablesList.size() - 1)
                .fillCustomVariable(var);
    }

    public CustomVariableAddingForm createNewEnvVariableNameLast(CustomVariable var) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        ///   ComponentUtil.scrollToElementCenter(getDriver(), addVariableButton.getElement());
        addVariableButton.click();
        return envVariablesList.get(envVariablesList.size() - 1)
                .fillCustomVariableNameLast(var);
    }

    public boolean isVariablePresentInEnvVariablesList(String varName) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Trying to find env variable in vars list ....");
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(envVariablesList,
                var -> var.getVariableName().equalsIgnoreCase(varName)
        );
    }

    public CustomVariableAddingForm getEnvVariableWithName(String variableName) {
        log.info("Trying to find env variable form with variable name {}", variableName);
        PageUtil.guaranteedToHideDropDownList(getDriver());
        return WaitUtil.waitElementAppearedInListByCondition(envVariablesList,
                var -> var.getVariableName().trim().equalsIgnoreCase(variableName),
                "Variable with name " + variableName + " was found",
                "Variable with name " + variableName + " was not found");
    }
}
