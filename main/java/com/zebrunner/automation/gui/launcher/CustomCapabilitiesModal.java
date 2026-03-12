package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

@Getter
public class CustomCapabilitiesModal extends AbstractModal<CustomCapabilitiesModal> {
    public static final String MODAL_NAME = "Custom capabilities";

    @FindBy(xpath = ".//div[@class='capabilities-edit-modal__row']")
    private List<CapabilitiesEditModalRaw> existingEnvVariables;

    public CustomCapabilitiesModal(WebDriver driver) {
        super(driver);
    }

    public Optional<CapabilitiesEditModalRaw> getCapabilityWithName(String name) {
        return existingEnvVariables.stream()
                .filter(var -> var.getNameOfCapability().equalsIgnoreCase(name)).findFirst();
    }
}
