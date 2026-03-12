package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;

import groovy.util.logging.Slf4j;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;

@Getter
@Slf4j
public class DeleteLauncherAlertModal extends AbstractModal<DeleteLauncherAlertModal> {

    public static final String MODAL_NAME = "Delete launch?";
    public static final Duration LOADING_TIME = Duration.ofSeconds(4);

    @FindBy(xpath = ".//*[text()='Delete']")
    protected Element deleteModalButton;

    @FindBy(xpath = ".//button[contains(@class, 'main-modal__close-icon')][2]")
    protected Element closeModalButton;

    @FindBy(xpath = "//*[@id = 'modal-header']//h4")
    private Element modalTitle;

    public DeleteLauncherAlertModal(WebDriver driver) {
        super(driver);
    }

    public LauncherPage acceptDeletingAlert() {
        deleteModalButton.click();
        return LauncherPage.openPage(getDriver());
    }

    public void closeAlert() {
        closeModalButton.click();
    }

    public boolean isModalOpened() {
        return getRootExtendedElement().isPresent(5);
    }

    @Override
    public String getModalTitleText() {
        return this.modalTitle.getText();
    }
}
