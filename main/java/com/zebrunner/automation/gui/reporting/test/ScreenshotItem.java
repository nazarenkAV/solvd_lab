package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Getter
public class ScreenshotItem extends AbstractUIObject {

    @FindBy(xpath = ".//img")
    private Element screenshotImg;

    @FindBy(xpath = ".//div[@class='pswp__bulletText']")
    private Element screenshotText;

    public ScreenshotItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public BufferedImage getSmallScreenshot() throws IOException {
        // Get the value of the src attribute
        String srcValue = screenshotImg.getAttributeValue("src");

        // Load the remote image directly from the URL
        URL imageUrl = new URL(srcValue);
        BufferedImage remoteImage = ImageIO.read(imageUrl);

        return remoteImage;
    }

    public String getSmallImgSrc() {
        String srcValue = screenshotImg.getAttributeValue("src");
        return srcValue;
    }

    public Boolean isImagePresent() {
        return (Boolean) ((JavascriptExecutor) getDriver()).executeScript(
                "return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" "
                        + "&& arguments[0].naturalWidth > 0", screenshotImg.getElement());
    }
}
