package com.zebrunner.automation.gui.reporting.milestone;

import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
@Getter
public class DeleteMilestoneModal extends AbstractModal<DeleteMilestoneModal> {
    public static final String MODAL_NAME = "Delete milestone?";

    public DeleteMilestoneModal(WebDriver driver) {
        super(driver);
    }

    public String getTitleText() {
        return super.getHeader().getTitleText();
    }
}
