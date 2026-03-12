package com.zebrunner.automation.gui.launcher;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.util.WaitUtil;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BrowserVersions extends AbstractUIObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FindBy(xpath = ".//li[contains(@class,'browser-select-modal__browser-version')]")
    private List<ExtendedWebElement> browserVersionList;

    public BrowserVersions(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void selectBrowserVersion(String version) {
        LOGGER.info("Selecting browser version");

        ExtendedWebElement foundBrowserVersion = WaitUtil.waitElementAppearedInListByCondition(browserVersionList,
                vr -> vr.getText().equalsIgnoreCase(version),
                "Browser version " + version + " was found",
                "Browser version " + version + " was not found");
        chooseVersion(foundBrowserVersion);
    }

    public String selectBrowserVersionByOrder(int numberInOrderFromTop) {
        LOGGER.info("Loading versions ....");
        WaitUtil.waitCheckListIsNotEmpty(browserVersionList);
        LOGGER.info("Size " + browserVersionList.size());
        ExtendedWebElement vr = browserVersionList.get(numberInOrderFromTop);
        String selectedVersion = vr.getText();
        chooseVersion(vr);
        return selectedVersion;
    }

    public String selectRandomBrowserVersion() {
        LOGGER.info("Loading versions ....");
        WaitUtil.waitCheckListIsNotEmpty(browserVersionList);
        LOGGER.info("Size " + browserVersionList.size());

        int index = new Random().nextInt(browserVersionList.size());
        ExtendedWebElement vr = browserVersionList.get(index);
        String selectedVersion = vr.getText();
        chooseVersion(vr);
        return selectedVersion;
    }

    private void chooseVersion(ExtendedWebElement version) {
        LOGGER.info("Selected version is {}", version.getText());
        if (!version.getAttribute("value").contains("_active")) {
            version.click();
        } else {
            LOGGER.warn("Version {} already chose", version.getText());
        }
    }

    public List<String> getBrowserVersions() {
        pause(3);
        return browserVersionList.stream().map(ExtendedWebElement::getText)
                .collect(Collectors.toList());

    }
}
