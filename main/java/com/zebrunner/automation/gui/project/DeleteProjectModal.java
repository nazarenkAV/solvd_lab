package com.zebrunner.automation.gui.project;

import com.zebrunner.automation.gui.common.AbstractModal;

import org.openqa.selenium.WebDriver;

public class DeleteProjectModal extends AbstractModal<DeleteProjectModal> {


    public DeleteProjectModal(WebDriver driver) {
        super(driver);
    }

    public ProjectsPage deleteProject() {
        deleteButton.click();
        return new ProjectsPage(getDriver());
    }
}
