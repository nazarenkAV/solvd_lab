package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.launcher.domain.CustomVariable;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.utils.R;
import com.zebrunner.carina.utils.commons.SpecialKeywords;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Slf4j
@Getter
public class CustomVariableAddingForm extends AbstractUIObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String DEFAULT_VALUE_INPUT_XPATH = ".//input[starts-with(@id, 'text') or starts-with(@id, 'number') or starts-with(@id, 'choice')]";
    private final String VARIABLE_NAME_INPUT_XPATH = ".//input[starts-with(@id, 'name')]";

    @FindBy(xpath = VARIABLE_NAME_INPUT_XPATH + "/ancestor::*/div[@class = 'custom-vars-add__col _name']//label")
    private Element variableNameLabel;

    @FindBy(xpath = VARIABLE_NAME_INPUT_XPATH)
    private Element variableNameInput;

    @FindBy(xpath = VARIABLE_NAME_INPUT_XPATH + "/ancestor::*/div[@class = 'custom-vars-add__col _name']//span[contains(@class, 'input-message-animation')]")
    private Element variableNameInputErrorMessage;

    @FindBy(xpath = ".//label[contains(text(), 'Type')]")
    private Element typeLabel;

    @FindBy(xpath = ".//div[@id= 'select-undefined']")
    private Element typeInput;

    @FindBy(xpath = "//ul[contains(@class,'css-r8u8y9')]//li")
    private List<Element> typesList;

    @FindBy(xpath = DEFAULT_VALUE_INPUT_XPATH + "/ancestor::*/div[@class = 'custom-vars-add__col _input']//label")
    private Element defaultValueLabel;

    @FindBy(xpath = DEFAULT_VALUE_INPUT_XPATH)
    private Element defaultValueInput;

    @FindBy(xpath = DEFAULT_VALUE_INPUT_XPATH + "/ancestor::*/div[@class = 'custom-vars-add__col _input']//span[contains(@class, 'input-message-animation')]")
    private Element defaultValueInputErrorMessage;

    @FindBy(xpath = ".//button[contains(@class, 'choice-input-icon')]")
    private Element choiceInputIcon;

    @FindBy(xpath = ".//button[contains(@class,'MuiButton-disableElevation button tertiary icon')]")
    private Element deleteVariable;

    @FindBy(xpath = ".//input[@value='true']/ancestor::span")
    private Element trueButton;

    @FindBy(xpath = ".//input[@value='false']/ancestor::span")
    private Element falseButton;

    @FindBy(xpath = "//*[contains(@class, 'input__up-button')]")
    private Element integerValueIncreasingArrow;

    @FindBy(xpath = "//*[contains(@class, 'input__down-button')]")
    private Element integerValueDecreasingArrow;

    public CustomVariableAddingForm(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CustomVariableAddingForm typeVariableName(String variableName) {
        clickVariableNameInput();
        this.variableNameInput.sendKeys(variableName);
        return this;
    }

    public CustomVariableAddingForm typeDefaultValue(String defaultValue) {
        defaultValueInput.sendKeys(defaultValue);
        return this;
    }

    public CustomVariableAddingForm openChoiceModalAndTypeValue(String choiceValue) {
        choiceInputIcon.click();
        ChoiceValuesModal choiceValuesModal = ChoiceValuesModal.openModal(getDriver())
                .typeValue(choiceValue);
        choiceValuesModal.save();
        return this;
    }

    public CustomVariableAddingForm selectType(String typeName) throws NoSuchElementException {
        LOGGER.info("Trying to choose variable type with name: {}", typeName);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
                typeInput.getElement());
        typeInput.click();

        WaitUtil.waitElementAppearedInListByCondition(typesList,

                        type -> {
                            log.info(type.getText());
                            return type.getText().equalsIgnoreCase(typeName);
                        },
                        "Found variable type with name " + typeName,
                        "There are no variable type with name " + typeName
                )
                .click();
        return this;
    }

    public CustomVariableAddingForm clickVariableNameInput() {
        variableNameInput.click();
        return this;
    }

    public void deleteVariable() {
        deleteVariable.click();
    }

    public String getVariableName() {
        pause(2);
        return variableNameInput.getAttributeValue("value");
    }

    public String getDefaultValue() {
        pause(2);
        return defaultValueInput.getAttributeValue("value");
    }

    public String getVarType() {
        return typeInput.getText();
    }

    public void clickTrue() {
        trueButton.click();
        LOGGER.info("True was selected!");
    }

    public void clickFalse() {
        falseButton.click();
        LOGGER.info("False was selected!");
    }

    public boolean isActiveTrue() {
        pause(2);
        LOGGER.info("Checking true switcher...");
        return trueButton.getAttributeValue("class").contains("Mui-checked");
    }

    public boolean isActiveFalse() {
        pause(2);
        LOGGER.info("Checking false switcher...");
        return falseButton.getAttributeValue("class").contains("Mui-checked");
    }

    public CustomVariableAddingForm fillCustomVariable(CustomVariable var) {
        if (!(var.getName() == null)) {
            typeVariableName(var.getName());
        }
        if (!(var.getType() == null)) {
            selectType(var.getType().name());
        }
        if (!(var.getDefaultValue() == null)) {
            if (var.getType().equals(CustomVariable.Type.CHOICE)) {
                openChoiceModalAndTypeValue(var.getDefaultValue());
            } else if (var.getType().equals(CustomVariable.Type.BOOLEAN)) {
                if ("true".equalsIgnoreCase(var.getDefaultValue())) {
                    clickTrue();
                } else if ("false".equalsIgnoreCase(var.getDefaultValue())) {
                    clickFalse();
                }
            } else
                typeDefaultValue(var.getDefaultValue());
        }

        if (!(var.getValues() == null) && var.getType().equals(CustomVariable.Type.CHOICE)) {
            choiceInputIcon.click();
            ChoiceValuesModal choiceValuesModal = ChoiceValuesModal.openModal(getDriver());
            var.getValues().forEach(val -> choiceValuesModal.typeValue(val + "\n"));
            choiceValuesModal.save();
        }
        if (R.CONFIG.get(SpecialKeywords.CAPABILITIES + ".browserName").equalsIgnoreCase(Browser.FIREFOX.browserName())) {
            LOGGER.info("Browser firefox");
            variableNameInput.click(); // we should do id to submit default value on firefox browser
        }
        return this;
    }

    public CustomVariableAddingForm fillCustomVariableNameLast(CustomVariable var) {//because bug ZEB-5723
        if (!(var.getType() == null)) {
            selectType(var.getType().name());
        }
        if (!(var.getDefaultValue() == null)) {
            if (var.getType().equals(CustomVariable.Type.CHOICE)) {
                openChoiceModalAndTypeValue(var.getDefaultValue());
            } else if (var.getType().equals(CustomVariable.Type.BOOLEAN)) {
                if ("true".equalsIgnoreCase(var.getDefaultValue())) {
                    clickTrue();
                } else if ("false".equalsIgnoreCase(var.getDefaultValue())) {
                    clickFalse();
                }
            } else
                typeDefaultValue(var.getDefaultValue());
        }

        if (!(var.getValues() == null) && var.getType().equals(CustomVariable.Type.CHOICE)) {
            choiceInputIcon.click();
            ChoiceValuesModal choiceValuesModal = ChoiceValuesModal.openModal(getDriver());
            var.getValues().forEach(val -> choiceValuesModal.typeValue(val + "\n"));
            choiceValuesModal.save();
        }
        if (!(var.getName() == null)) {
            typeVariableName(var.getName());
        }
        if (R.CONFIG.get(SpecialKeywords.CAPABILITIES + ".browserName").equalsIgnoreCase(Browser.FIREFOX.browserName())) {
            LOGGER.info("Browser firefox");
            variableNameInput.click(); // we should do id to submit default value on firefox browser
        }
        return this;
    }

    public SoftAssert assertCreatedVariable(SoftAssert softAssert, CustomVariable var) {
        softAssert.assertEquals(getVarType(), var.getType().name(), "Var type is not as expected!");
        softAssert.assertEquals(getVariableName(), var.getName(), "Var name is not as expected!");

        if (CustomVariable.Type.BOOLEAN.equals(var.getType())) {
            log.info("Checking default value for boolean variable....");
            softAssert.assertEquals(String.valueOf(isActiveTrue()), var.getDefaultValue(), "Boolean var default value is not as expected!");
        } else {
            log.info("Checking default value for " + var.getType().name() + " variable....");
            softAssert.assertEquals(getDefaultValue(), var.getDefaultValue(), "Var default value is not as expected!");
        }
        return softAssert;
    }

    public void clickIntegerValueDecreaseArrow() {
        integerValueDecreasingArrow.click();
    }

    public void clickIntegerValueIncreasingArrow() {
        integerValueIncreasingArrow.click();
    }

    public String getVariableNameInputErrorMessage() {
        return variableNameInputErrorMessage.getText();
    }

    public String getDefaultValueInputErrorMessage() {
        return defaultValueInputErrorMessage.getText();
    }

    public boolean isDefaultValueInputErrorMessagePresent() {
        return defaultValueInputErrorMessage.isElementPresent(3);
    }

    public boolean isVariableNameInputErrorMessagePresent() {
        return variableNameInputErrorMessage.isElementPresent(3);
    }

    public String getVariableNameLabelText() {
        return variableNameLabel.getText();
    }

    public String getDefaultValueLabelText() {
        return defaultValueLabel.getText();
    }

    public String getTypeLabelText() {
        return typeLabel.getText();
    }
}
