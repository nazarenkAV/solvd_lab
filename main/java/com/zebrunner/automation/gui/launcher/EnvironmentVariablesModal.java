package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

public class EnvironmentVariablesModal extends AbstractModal<EnvironmentVariablesModal> {
    public static final String MODAL_NAME = "Environment variables";

    @FindBy(xpath = ".//div[@class='capabilities-edit-modal__row']")
    private List<CapabilitiesEditModalRaw> existingEnvVariables;

    public EnvironmentVariablesModal(WebDriver driver) {
        super(driver);
    }

    public Optional<CapabilitiesEditModalRaw> getEnvVariableWithName(String name) {
        return existingEnvVariables.stream()
                .filter(var -> var.getNameOfCapability().equalsIgnoreCase(name)).findFirst();
    }
}
