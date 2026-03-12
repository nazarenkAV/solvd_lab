package com.zebrunner.automation.gui.reporting.dashboard;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
@Getter
public class NewDashboardModal extends AbstractModal {
    public static final String MODAL_NAME = "New dashboard";

    @FindBy(xpath = " //input[@id='name']")
    private Element dashboardNameInput;

    public NewDashboardModal(WebDriver driver) {
        super(driver);
    }
}
