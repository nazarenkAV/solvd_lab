package com.zebrunner.automation.gui.reporting.dashboard;

import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
@Getter
public class DeleteDashboardModal extends AbstractModal {
    public static final String MODAL_NAME = "Delete dashboard";

    public DeleteDashboardModal(WebDriver driver) {
        super(driver);
    }
}
