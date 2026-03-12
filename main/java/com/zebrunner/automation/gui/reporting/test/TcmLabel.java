package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.tcm.TcmLabelPreview;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Slf4j
public class TcmLabel extends AbstractUIObject {
    public static final String ROOT_XPATH = "//*[contains(@class, 'tcm-label _link')]";

    @FindBy(xpath = ".]//div[contains(@class,'tcm-label__item-icon')]")
    private Element icon;
    @FindBy(xpath = ".//div[contains(@class,'tcm-label__item-value')]")
    private Element value;
    @FindBy(xpath = ".//div[contains(@class,'tcm-label__item-indicator')]")
    private Element indicator;// only for Testrail

    public TcmLabel(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTextValue() {
        return value.getText();
    }

    public TcmLabelPreview clickOnTcmLabel() {
        this.getRootExtendedElement().click();
        return new TcmLabelPreview(driver);
    }

    public TcmLabelPreview clickOnTcmLabelAndWaitIntegration() {

        Wait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofMinutes(1))
                .pollingEvery(Duration.ofSeconds(10))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .ignoring(org.openqa.selenium.NoSuchElementException.class);

        AtomicInteger i = new AtomicInteger(0);

        wait.until((drv) -> {
            log.info("Attempt " + i.getAndIncrement());

            TcmLabelPreview finalTcmLabelPreview = this.clickOnTcmLabel();

            boolean isPresent = finalTcmLabelPreview.getTcmPreviewContent().isPresent(5);

            if (!isPresent) {
                finalTcmLabelPreview.close();
                return null;
            }
            return true;
        });

        return new TcmLabelPreview(driver);
    }
}
