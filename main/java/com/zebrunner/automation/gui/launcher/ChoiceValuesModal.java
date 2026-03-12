package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ChoiceValuesModal extends AbstractUIObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String ROOT_LOCATOR = "//div[@id='modal-dialog-content']";

    public static final String MODAL_NAME = "Choice values";

    @FindBy(xpath = ".//textarea[@class='ace_text-input']")
    private Element textInputArea;

    @FindBy(xpath = ".//button[contains(text(),'Save')]")
    private Element saveButton;

    public ChoiceValuesModal(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_LOCATOR));
    }

    public static ChoiceValuesModal openModal(WebDriver driver) {
        LOGGER.info("Attempt to open '{}' modal window", MODAL_NAME);
        ChoiceValuesModal choiceValuesModal = new ChoiceValuesModal(driver);
        choiceValuesModal.pause(1);
        return choiceValuesModal;
    }

    public ChoiceValuesModal typeValue(String value) {
        LOGGER.info("Typing value ..." + value);
        textInputArea.type(value);
        return this;
    }

    public void save() {
        saveButton.click();
        saveButton.getRootExtendedElement().waitUntilElementDisappear(3);
    }
}
