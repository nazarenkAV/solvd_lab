package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class BrowsersModal extends AbstractPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String rootXpath = "//div[@class='browser-select-modal__content modal-content']";
    public static final String MODAL_NAME = "Browser";

    @FindBy(xpath = "//div[@class='browser-select-modal__browser-name']")
    private List<ExtendedWebElement> browserList;

    @FindBy(xpath = rootXpath)
    private Element root;

    @FindBy(xpath = "//div[@class='browser-select-modal__browser-list-expander-btn-icon']")
    private ExtendedWebElement expander;

    @FindBy(xpath = ".//ul[@class='browser-select-modal__browser-versions _no-limit']")
    private List<BrowserVersions> versionsList;

    public BrowsersModal(WebDriver driver) {
        super(driver);
    }

    public static BrowsersModal openModal(WebDriver driver) {
        LOGGER.info("Attempt to open '{}' modal window", MODAL_NAME);
        BrowsersModal browsersModal = new BrowsersModal(driver);
        browsersModal.pause(3);
        return browsersModal;
    }

    public int getBrowserId(String browserName) {
        LOGGER.info("Loading browsers.....");
        waitUntil(ExpectedConditions
                .numberOfElementsToBeMoreThan(By.xpath("//div[@class='browser-select-modal__browser-name']"), 0), 7);
        ArrayList<ExtendedWebElement> l = new ArrayList<>(browserList);
        for (ExtendedWebElement br : l) {
            LOGGER.info("Browser is " + br.getText());
            if (br.getText().equalsIgnoreCase(browserName)) {
                LOGGER.info("Browser " + br.getText() + " was found!");
                int i = l.indexOf(br);
                LOGGER.info("Index of browser " + browserName + " is " + i);
                return i;
            }
        }
        throw new RuntimeException("Browser " + browserName + " was not found!");
    }

    public BrowserVersions getVersions(String browser) {
        expander.clickIfPresent();
        int browserId = getBrowserId(browser);
        WaitUtil.waitCheckListIsNotEmpty(versionsList);
        List<BrowserVersions> versions = Collections.unmodifiableList(versionsList);
        return versions.get(browserId);
    }
}
