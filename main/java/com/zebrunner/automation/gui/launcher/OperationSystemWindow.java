package com.zebrunner.automation.gui.launcher;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class OperationSystemWindow extends AbstractUIObject {

    @FindBy(xpath = "//div[@class='select-platform-modal__platform-name']")
    private List<Element> osList;

    @FindBy(xpath = "//*[contains(@class,'select-platform-modal__section _native')]//div[contains(@class,'select-platform-modal__platform-name')]")
    private List<Element> nativeOsList;

    @FindBy(xpath = "//*[contains(@class,'select-platform-modal__section _web')]//div[contains(@class,'select-platform-modal__platform-name')]")
    private List<Element> webOsList;

    public OperationSystemWindow(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[@class='styled-modal']"));
    }

    public AddOrEditLauncherPage selectOS(String osName) throws NoSuchElementException {
        log.info("Trying to select OS with name: {}", osName);
        Element foundOS = WaitUtil.waitElementAppearedInListByCondition(osList,
                os -> os.getText().equalsIgnoreCase(osName),
                "OS with name " + osName + " was found",
                "OS with name " + osName + " was not found");

        foundOS.click();
        return AddOrEditLauncherPage.openPage(getDriver());
    }

    public AddOrEditLauncherPage selectNativeOS(String nativeOsName) throws NoSuchElementException {
        log.info("Trying to select native OS with name: {}", nativeOsName);
        Element foundOS = WaitUtil.waitElementAppearedInListByCondition(nativeOsList,
                os -> os.getText().equalsIgnoreCase(nativeOsName),
                "Native OS with name " + nativeOsName + " was found",
                "Native OS with name " + nativeOsName + " was not found");

        foundOS.click();
        return AddOrEditLauncherPage.openPage(getDriver());
    }

    public AddOrEditLauncherPage selectWebOS(String webOsName) throws NoSuchElementException {
        log.info("Trying to select native OS with name: {}", webOsName);
        Element foundOS = WaitUtil.waitElementAppearedInListByCondition(webOsList,
                os -> os.getText().equalsIgnoreCase(webOsName),
                "Native OS with name " + webOsName + " was found",
                "Native OS with name " + webOsName + " was not found");

        foundOS.click();
        return AddOrEditLauncherPage.openPage(getDriver());
    }

    public AddOrEditLauncherPage selectOS(String osName, String osType) {
        log.info("OS is {}, type of OS is {}", osName, osType);
        if (osType != null) {
            switch (osType) {
            case "Web":
                selectWebOS(osName);
                break;
            case "Native":
                selectNativeOS(osName);
                break;
            default:
                selectOS(osName);
                break;
            }
        } else {
            log.info("As the OS type is not defined, we will select the OS from the full list.");
            selectOS(osName);
        }
        return AddOrEditLauncherPage.openPage(getDriver());
    }

    public String selectRandomWebOSWhichContainsPartOfText(String partOfText) throws NoSuchElementException {
        log.info("Trying to select native OS with part of text: {}", partOfText);

        List<Element> filteredList = webOsList.stream()
                .filter(os -> os.getText().contains(partOfText))
                .collect(Collectors.toList());

        int index = new Random().nextInt(filteredList.size());
        Element selectedOs = filteredList.get(index);
        String selectedOsName = selectedOs.getText();
        log.info("Random OS is {}", selectedOs.getText());
        selectedOs.click();
        return selectedOsName;
    }
}
