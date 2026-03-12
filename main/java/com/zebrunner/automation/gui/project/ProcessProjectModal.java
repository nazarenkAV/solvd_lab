package com.zebrunner.automation.gui.project;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;

import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.gui.common.InputContainer;
import com.zebrunner.automation.gui.common.ZbrAutocomplete;
import com.zebrunner.automation.gui.common.ZbrAutocompleteInput;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Slf4j
@Getter
public class ProcessProjectModal extends AbstractModal<ProcessProjectModal> {

    @FindBy(xpath = ".//button[contains(@class,'main-modal__close-icon')]")
    protected Element close;

    @FindBy(xpath = ".//div[contains(@class, 'add-photo__image')]")
    private Element projectImg;

    @FindBy(xpath = ".//div[contains(@class, 'add-photo__image')]//img")
    private ExtendedWebElement customLogo;

    @FindBy(xpath = "//form[@id='formElem']")
    private ProjectLogoLoadModal imgModal;

    @FindBy(xpath = ".//input[@type='file']")
    private ExtendedWebElement projectImgUpload;

    @FindBy(xpath = ".//label[@for = 'name']")
    private ExtendedWebElement projectNameLabel;

    @FindBy(xpath = ".//label[@for = 'key']")
    private ExtendedWebElement projectKeyLabel;

    @FindBy(xpath = ".//label[@for='name']" + InputContainer.ROOT_XPATH)
    private InputContainer projectNameInput;

    @FindBy(xpath = ".//label[@for='key']" + InputContainer.ROOT_XPATH)
    private InputContainer projectKeyInput;

    @FindBy(xpath = "//div[text()='Lead']//parent::" + ZbrAutocompleteInput.ROOT_XPATH)
    private ZbrAutocompleteInput selectLeadMenu;

    @FindBy(xpath = ".//div[contains(@class, 'descriptive-dropdown__value')]")
    private Element accessDropdownValue;
    @FindBy(xpath = "//li[contains(@class, 'descriptive-dropdown__menu-item')]")
    private List<Element> accessDropdownItems;

    public ProcessProjectModal(WebDriver driver) {
        super(driver);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getLogoLink() {
        return customLogo.getAttribute("src");
    }

    public String getProjectName() {
        return projectNameInput.getValue();
    }

    public String getKey() {
        return projectKeyInput.getValue();
    }

    public String getSelectedLead() {
        return selectLeadMenu.getValue();
    }

    public String getNameErrorMessage() {
        return projectNameInput.getErrorMessageText();
    }

    public String getKeyErrorMessage() {
        super.pause(2);
        return projectKeyInput.getErrorMessageText();
    }

    public boolean isProjectLogoPresent() {
        return projectImg.isStateMatches(Condition.VISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isProjectPublic() {
        return "Public".equals(accessDropdownValue.getText());
    }

    public boolean isProjectNameFieldPresent() {
        return projectNameInput.getInput().isClickable(5);
    }

    public boolean isAccessDropdownPresent() {
        return accessDropdownValue.isUIObjectPresent();
    }

    public boolean isKeyFieldPresent() {
        return projectKeyInput.getInput().isClickable(5);
    }

    public boolean isCreateProjectModalPresent() {
        return close.isStateMatches(Condition.VISIBLE) &&
                projectImg.isStateMatches(Condition.VISIBLE) &&
                projectNameInput.isVisible(5) &&
                accessDropdownValue.isUIObjectPresent() &&
                projectKeyInput.isVisible(5) &&
                cancelButton.isStateMatches(Condition.VISIBLE) &&
                submitButton.isStateMatches(Condition.VISIBLE);
    }

    public boolean isNameLabelContainsAsterisk() {
        return projectNameLabel.getText().contains("*");
    }

    public boolean isKeyLabelContainsAsterisk() {
        return projectKeyLabel.getText().contains("*");
    }

    public boolean isSelectLeadMenuPresent() {
        return selectLeadMenu.isClickable(5) && selectLeadMenu.isVisible(5);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ProjectLogoLoadModal uploadLogo(String pathToImage) {
        projectImgUpload.attachFile(pathToImage);
        return imgModal;
    }

    public ProcessProjectModal changeProjectAccess() {
        accessDropdownValue.click();

        String newAccess = accessDropdownValue.getText().equals("Public")
                ? "Private"
                : "Public";
        Element newAccessDropdownValue = this.getAccessDropdownItemByDataValue(newAccess);

        newAccessDropdownValue.click();
        return this;
    }

    private Element getAccessDropdownItemByDataValue(String dataValue) {
        return StreamUtils.findFirst(accessDropdownItems, item -> dataValue.equals(item.getAttribute("data-value")))
                          .orElseThrow(() -> new NoSuchElementException("No access dropdown item found with attribute 'data-value=" + dataValue + "'"));
    }

    public ProcessProjectModal typeProjectName(String name) {
        projectNameInput.input(name);
        return this;

    }

    public ProcessProjectModal typeProjectKey(String key) {
        projectKeyInput.input(key);
        return this;
    }

    public ProcessProjectModal setLeadToTheProject(String name, boolean listOpened) {
        ZbrAutocomplete autocomplete = null;
        if (!listOpened) {
            autocomplete = selectLeadMenu.inputClick();
        }

        autocomplete.getOption(name)
                    .orElseThrow(() -> new NoSuchElementException("No elements found with name " + name))
                    .click();
        return this;
    }

    public void clickCloseButton() {
        this.close.click();
    }

}
