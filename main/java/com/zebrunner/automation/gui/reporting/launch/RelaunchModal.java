package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class RelaunchModal extends AbstractModal<RelaunchModal> {

    public final String MODAL_TITLE = "Relaunch";

    @FindBy(xpath = ".//*[@class = 'modal-content__controls']//button[contains(text(), 'Relaunch')]")
    private ExtendedWebElement relaunchButton;

    public RelaunchModal(WebDriver driver) {
        super(driver);
    }

    public LauncherPage clickRelaunchButton() {
        relaunchButton.click();
        return new LauncherPage(getDriver());
    }
}
