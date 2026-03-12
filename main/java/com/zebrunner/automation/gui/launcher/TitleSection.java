package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.launcher.preset.WebhooksModal;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class TitleSection extends AbstractUIObject {
    @FindBy(xpath = ".//div[@class='section-title__webhooks']//button")
    private Element webhooksBtn;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public TitleSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String hoverAndGetTooltipValue() {
        webhooksBtn.hover();
        return tooltip.getTooltipText();
    }

    public WebhooksModal clickWebhookButton() {
        webhooksBtn.click();
        return new WebhooksModal(getDriver());
    }

    public String getWebhookBtnBackgroundColor() {
        return ColorUtil.getHexColorFromString(webhooksBtn.getElement().getCssValue("background-color"));
    }
}
