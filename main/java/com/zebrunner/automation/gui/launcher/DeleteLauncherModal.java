package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.WebDriver;

public class DeleteLauncherModal extends AbstractModal<DeleteLauncherModal> {

    public static final String MODAL_NAME = "Delete launch?";

    public DeleteLauncherModal(WebDriver driver) {
        super(driver);
    }

    public String getTitleText() {
        return super.getHeader().getTitleText();
    }
}
