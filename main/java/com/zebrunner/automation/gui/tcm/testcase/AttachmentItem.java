package com.zebrunner.automation.gui.tcm.testcase;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.io.File;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.util.FileUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

public class AttachmentItem extends AbstractUIObject {

    public static final String ROOT = ".//*[contains(@class,'attachment-item ')]";

    @FindBy(xpath = ".//img")
    private ExtendedWebElement img;

    @FindBy(xpath = ".//p[@class='attachment-item__name']")
    private ExtendedWebElement imgName;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.TRASH_BIN + "']")
    private ExtendedWebElement deleteIcon;

    @FindBy(xpath = ".//*[@d='M12.9 6.694A4.993 4.993 0 0 0 3.567 5.36 3.996 3.996 0 0 0 0 9.334c0 2.206 1.793 4 4 4h8.667A3.335 3.335 0 0 0 16 10a3.317 3.317 0 0 0-3.1-3.306ZM12.667 12H4a2.666 2.666 0 0 1-2.667-2.666 2.649 2.649 0 0 1 2.374-2.647l.713-.073.333-.634A3.646 3.646 0 0 1 8 4a3.669 3.669 0 0 1 3.593 2.954l.2 1 1.02.073c1.04.067 1.854.94 1.854 1.973 0 1.1-.9 2-2 2ZM7.033 6.667h1.934v2h1.7L8 11.334 5.333 8.667h1.7v-2Z']")
    private ExtendedWebElement downloadIcon;

    public AttachmentItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getImgPath() {
        return img.getAttribute("src");
    }

    public File getImgFile() {
        return FileUtils.getFileFromURL(getImgPath())
                        .orElseThrow(() -> new RuntimeException("File is not present by url!"));
    }

    public String hoverAndGetAttachmentName() {
        imgName.hover();
        super.pause(1);

        return imgName.getText();
    }

    public void delete() {
        deleteIcon.hover();
        super.pause(1);
        deleteIcon.clickByActions();
    }

    public void hoverAndDownload() {
        downloadIcon.hover();
        super.pause(1);
        downloadIcon.clickByActions();
    }

}
