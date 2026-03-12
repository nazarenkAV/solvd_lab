package com.zebrunner.automation.gui.tcm;

import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Slf4j
public class ConfigGroup extends AbstractUIObject {

    public final static String ROOT_XPATH = ".//div[@class='create-run-select-conf-modal__group-list']";

    @FindBy(xpath = ".//span[@class = 'testing-config__group-name']")
    private ExtendedWebElement nameLabel;
    @FindBy(xpath = ".//*[@d='" + SvgPaths.PENCIL + "']")
    private ExtendedWebElement editButton;
    @FindBy(xpath = ".//*[@d='" + SvgPaths.TRASH_BIN + "']")
    private ExtendedWebElement deleteIcon;
    @FindBy(xpath = OptionGroup.ROOT_XPATH)
    private List<OptionGroup> options;
    @FindBy(xpath = ".//div[@aria-expanded]")
    private ExtendedWebElement expandButton;
    @CaseInsensitiveXPath
    @FindBy(xpath = ".//*[text()='Add option']//parent::button")
    private ExtendedWebElement addOptionButton;
    @FindBy(xpath = ".//input[@placeholder='Enter value']")
    private ExtendedWebElement newOptionNameInput;
    @FindBy(xpath = ".//span[contains(@class, 'config-applicable-label')]")
    private ExtendedWebElement applicableLabel;
    @FindBy(xpath = ".//button[contains(@class, 'config-applicable-label _active')]")
    private ExtendedWebElement activeApplicableLabel;

    public ConfigGroup(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getGroupName() {
        return nameLabel.getText();
    }

    public String getApplicableLabelText() {
        return applicableLabel.getText();
    }

    public String getActiveApplicableLabelText() {
        return activeApplicableLabel.getText();
    }

    public void selectOption(String optionName) {
        OptionGroup option = this.getOption(optionName);

        option.select();
    }

    public OptionGroup getOption(String optionName) {
        WaitUtil.waitElementAppearedInListByCondition(
                options,
                option -> option.getOptionName().equalsIgnoreCase(optionName),
                "Option with name " + optionName + " was found",
                "Option with name " + optionName + " was not found"
        );

        return StreamUtils.findFirst(
                                  options,
                                  option -> option.getOptionName().equalsIgnoreCase(optionName)
                          )
                          .orElseThrow(() -> new NoSuchElementException("Option '" + optionName + "' not found"));
    }

    public boolean isOptionExist(String optionName) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(
                options,
                configGroup -> configGroup.getOptionName().trim().equalsIgnoreCase(optionName)
        );
    }

    public boolean isAddOptionButtonClickable() {
        return addOptionButton.isClickable(3);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ConfigGroup clickNameLabel() {
        if (!this.isExpanded()) {
            log.info("Expanding group options...");

            nameLabel.click();
        }
        return this;
    }

    private boolean isExpanded() {
        return Boolean.parseBoolean(expandButton.getAttribute("aria-expanded"));
    }

    public EditConfigurationModal clickEditButton() {
        nameLabel.hover();
        editButton.clickByActions();

        return new EditConfigurationModal(getDriver());
    }

    public DeleteModal clickDeleteIcon() {
        nameLabel.hover();
        deleteIcon.clickByActions();

        return new DeleteModal(getDriver());
    }

    public ConfigGroup clickAddOptionButton() {
        addOptionButton.click();
        return this;
    }

    public void clickApplicableLabel() {
        applicableLabel.click();
    }

    public void clickActiveApplicableLabel() {
        activeApplicableLabel.click();
    }

    public void createOption(String optionName) {
        Actions actions = new Actions(getDriver());

        addOptionButton.click();
        newOptionNameInput.type(optionName);

        actions.keyDown(Keys.ENTER).perform();
        actions.keyUp(Keys.ENTER).perform();
    }

    public void inputOptionName(String optionName) {
        newOptionNameInput.type(optionName);
    }

}
