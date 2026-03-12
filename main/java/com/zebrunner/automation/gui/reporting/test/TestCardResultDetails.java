package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.reporting.launch.FailureTagModal;
import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestCardResultDetails extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[contains(@class, 'test-details__wrapper')]";

    @FindBy(xpath = ".//*[@class = 'test-failure-tag__wrapper']/button")
    private ExtendedWebElement failureTagButton;

    @FindBy(xpath = ".//*[contains(@class, 'Zbr-copy-to-clipboard')]")
    private ExtendedWebElement copyStackTraceButton;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public TestCardResultDetails(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public FailureTagModal getFailureTagModal() {
        failureTagButton.click();
        return new FailureTagModal(getDriver());
    }

    public String getColorOfFailureTagButton() {
        String color = failureTagButton.getElement().getCssValue("background-color");
        return ColorUtil.getHexColorFromString(color);
    }

    public String getFailureTagText() {
        return failureTagButton.getText();
    }

    public String hoverCopyStackTraceButtonAndGetToolTipText() {
        copyStackTraceButton.hover();
        return tooltip.getTooltipText();
    }

    public String clickCopyStackTraceButtonAndGetToolTipText() {
        copyStackTraceButton.click();
        return tooltip.getTextFromTooltipDirectly();
    }
}
