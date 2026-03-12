package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.Condition;
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
public class ScreenshotsView extends AbstractUIObject {

    @FindBy(xpath = ".//div[(@class='pswp__item') and (@aria-hidden='false')]//img")
    private Element bigScreenshot;

    @FindBy(xpath = ".//button[@title='Previous']")
    private Element previousBtn;

    @FindBy(xpath = ".//button[@title='Next']")
    private Element nextBtn;

    @FindBy(xpath = ".//a[@class='pswp__button pswp__button--download-button']")
    private Element downloadBtn;

    @FindBy(xpath = ".//div[@class='pswp__button pswp__button--hide-thumbnails-button']")
    private Element hideScreenListBtn;

    @FindBy(xpath = ".//button[@class='pswp__button pswp__button--close']")
    private Element closeBtn;

    @FindBy(xpath = ".//div[@class='pswp__bulletsIndicator pswp__hide-on-close']")
    private AllScreenshotsPanel allScreenshotsPanel;

    public ScreenshotsView(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }
    //    public ScreenshotsView(WebDriver driver) {
    //        super(driver);
    //    }

    public BufferedImage getBigScreenshot() throws IOException {
        // Get the value of the src attribute
        bigScreenshot.waitUntil(Condition.VISIBLE);
        String srcValue = bigScreenshot.getAttributeValue("src");

        // Load the remote image directly from the URL
        URL imageUrl = new URL(srcValue);
        BufferedImage remoteImage = ImageIO.read(imageUrl);

        return remoteImage;
    }

    public void clickHideListOfScreenshots() {
        hideScreenListBtn.click();
    }

    public String getBigScreenshotSrc() {
        bigScreenshot.waitUntil(Condition.VISIBLE);
        pause(3);
        return bigScreenshot.getAttributeValue("src");
    }

    public void closeScreenshotsView() {
        closeBtn.click();
    }

    public Boolean isBigScreenshotPresent() {
        return (Boolean) ((JavascriptExecutor) getDriver()).executeScript(
                "return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" "
                        + "&& arguments[0].naturalWidth > 0", bigScreenshot.getElement());
    }
}
