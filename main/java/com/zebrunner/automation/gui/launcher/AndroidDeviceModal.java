package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AndroidDeviceModal extends AbstractPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String MODAL_NAME = "Devices";

    @FindBy(xpath = "//div[@class='device-select-modal__item-name']")
    private List<ExtendedWebElement> deviceList;

    @FindBy(xpath = "//div[@class='device-select-modal__item-platform']")
    private List<ExtendedWebElement> redroidVersionList;

    public AndroidDeviceModal(WebDriver driver) {
        super(driver);
    }

    public static AndroidDeviceModal openModal(WebDriver driver) {
        LOGGER.info("Attempt to open '{}' modal window", MODAL_NAME);
        AndroidDeviceModal browsersModal = new AndroidDeviceModal(driver);
        browsersModal.pause(3);
        return browsersModal;
    }

    public void selectDevice(String deviceName) {
        LOGGER.info("Loading devices.....");
        pause(1);
        ArrayList<ExtendedWebElement> devices = new ArrayList<>(deviceList);

        for (ExtendedWebElement device : devices) {
            LOGGER.debug("Device is " + device.getText());
            if (device.getText().equalsIgnoreCase(deviceName)) {
                LOGGER.info("Device " + device.getText() + " was found!");
                device.click();
                break;
            }
        }
    }

    public void selectVersion(String deviseVersion) {
        LOGGER.info("Loading versions.....");
        pause(1);
        ArrayList<ExtendedWebElement> versions = new ArrayList<>(redroidVersionList);
        for (ExtendedWebElement version : versions) {
            LOGGER.debug("Version is " + version.getText());
            if (version.getText().contains(deviseVersion)) {
                LOGGER.info("Version " + version.getText() + " was found!");
                version.click();
                break;
            }
        }
    }

    public List<String> getRedroidVersions() {
        pause(3);
        return redroidVersionList.stream()
                .map(ExtendedWebElement::getText)
                .collect(Collectors.toList());
    }

    public String selectRandomDevice() {
        LOGGER.info("Loading devices ....");
        WaitUtil.waitCheckListIsNotEmpty(deviceList);
        LOGGER.info("Size " + deviceList.size());

        int index = new Random().nextInt(deviceList.size());
        ExtendedWebElement vr = deviceList.get(index);
        String selectedDevise = vr.getText();
        selectDevice(selectedDevise);
        return selectedDevise;
    }
}
