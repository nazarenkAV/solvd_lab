package com.zebrunner.automation.gui.reporting.widget;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
@Getter
public class SendByEmailWindow extends AbstractModal {
    public static final String MODAL_TITLE = "Send widget";
    public static final String SHARE_LAUNCH_RESULT_MODAL_TITLE = "Share launch results";

    @FindBy(xpath = ".//*[@class='modal-header__inner']//h4")
    protected Element modalTitle;

    @FindBy(xpath = ".//input[@id='subject']")
    private ExtendedWebElement nameInput;

    @FindBy(xpath = ".//textarea[@id='text']")
    private ExtendedWebElement descriptionInput;

    @FindBy(xpath = ".//input[@placeholder='Start typing to search']")
    private Element addEmailField;

    @FindBy(xpath = ".//*[@class='modal-controls__inner']//button[contains(text(),'Send')]")
    private ExtendedWebElement sendButton;

    @FindBy(xpath = "//span[contains(@class, 'multiselect-autocomplete-option__text')]")
    private List<ExtendedWebElement> emailSuggestionsList;

    @FindBy(xpath = ".//button[contains(@class, 'main-modal__close-icon')][2]")
    protected Element closeModalButton;

    public SendByEmailWindow(WebDriver driver) {
        super(driver);
    }

    public String getNameInputValue() {
        return nameInput.getAttribute("value");
    }

    public String getDescription() {
        return descriptionInput.getText();
    }

    public boolean isAddEmailFieldVisible() {
        return addEmailField.isStateMatches(Condition.VISIBLE);
    }

    public boolean isSendButtonPresent() {
        return sendButton.isClickable(2);
    }

    public void fillingEmailAndSubmit(String email) {
        log.info("Filling out the form.....");
        addEmailField.sendKeys(email, false, true);
        addEmailField.getRootExtendedElement().sendKeys(Keys.ESCAPE);
        getModalTitle().click();
        pause(3);
        sendButton.click();
    }

    public void fillEmailsAndSend(List<String> emailList) {
        for (String email : emailList) {
            getAddEmailField().sendKeys(email, false, true);
        }
        getAddEmailField().getRootExtendedElement().sendKeys(Keys.ESCAPE);
        getModalTitle().click();
        getSendButton().click();
    }

    public void closeModal() {
        closeModalButton.click();
    }
}
