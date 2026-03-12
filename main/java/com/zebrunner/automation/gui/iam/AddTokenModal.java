package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.gui.common.Calendar;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class AddTokenModal extends AbstractModal<AddTokenModal> {

    @FindBy(id = "name")
    private ExtendedWebElement tokenName;

    @FindBy(xpath = "//input[contains(@class, 'PrivateSwitchBase-input')]")
    private ExtendedWebElement checkboxAddExpirationDate;

    @FindBy(xpath = "//button[@aria-label='Choose date']")
    private ExtendedWebElement calendarLogo;

    @FindBy(xpath = Calendar.ROOT_LOCATOR)
    private Calendar calendar;

    public AddTokenModal(WebDriver driver) {
        super(driver);
    }

    public void inputTokenName(String name) {
        tokenName.type(name);
    }

    public void clickCheckboxAddExpirationDate() {
        checkboxAddExpirationDate.click();
    }

    public void clickCalendarLogo() {
        calendarLogo.click();
    }

}
