package com.zebrunner.automation.gui.project;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ProjectLogoLoadModal extends AbstractModal {
    public static final String MODAL_NAME = "Upload image";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = ".//button[text()='Upload']")
    protected Element uploadButton;

    public ProjectLogoLoadModal(WebDriver driver) {
        super(driver);
    }

    public ProjectLogoLoadModal(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public static ProjectLogoLoadModal modalInstance(WebDriver driver) {
        LOGGER.info("Attempt to open '{}' modal window", MODAL_NAME);
        ProjectLogoLoadModal projectLogoLoadModal = new ProjectLogoLoadModal(driver);
        projectLogoLoadModal.pause(3);
        return projectLogoLoadModal;
    }

    public void clickUpload() {
        uploadButton.getRootExtendedElement().isPresent();
        uploadButton.click();
    }

}
