package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
@Getter
public class RevokeUserModal extends AbstractModal<RevokeUserModal> {

    public static final String MODAL_NAME = "Revoke invitation?";

    @FindBy(xpath = ".//button[contains(text(),'Cancel')]")
    private Element cancelButton;

    @FindBy(xpath = ".//button[contains(text(),'Revoke')]")
    private Element revokeButton;

    @FindBy(xpath = ".//span[contains(text(),'Revoke invitation?')]")
    private ExtendedWebElement uiLoadedMarker;

    public RevokeUserModal(WebDriver driver) {
        super(driver);
    }

    public static RevokeUserModal getInstance(WebDriver driver) {
        return new RevokeUserModal(driver);
    }

    public void clickRevoke() {
        revokeButton.click();
    }

    public boolean isRevokeButtonActive() {
        return revokeButton.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

}
