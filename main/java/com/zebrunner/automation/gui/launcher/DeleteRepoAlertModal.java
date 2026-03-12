package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;

public class DeleteRepoAlertModal extends AbstractModal<DeleteRepoAlertModal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String ALERT_NAME = "Delete repository alert";
    public static final Duration LOADING_TIME = Duration.ofSeconds(5);


    private DeleteRepoAlertModal(WebDriver driver) {
        super(driver);
    }

    public static DeleteRepoAlertModal openAlert(WebDriver driver) {
        LOGGER.info("Attempt to go to the page '{}'", ALERT_NAME);
        DeleteRepoAlertModal deleteLauncherAlertModal = new DeleteRepoAlertModal(driver);
        deleteLauncherAlertModal.pause(LOADING_TIME.toSeconds());
        return deleteLauncherAlertModal;
    }
}
