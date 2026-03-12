package com.zebrunner.automation.gui.reporting.test.session;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Optional;

public class Content extends AbstractUIObject {

    @FindBy(xpath = ".")
    private ExtendedWebElement content;

    @FindBy(xpath = ".//video")
    private Element video;

    @FindBy(xpath = ".//div[text()='Artifacts']")
    private Element artifacts;

    private Element platformIcon;

    private Element browserIcon;

    private Element browserVersion;

    private Element startedAt;

    private Element duration;

    @FindBy(xpath = ".//a[text()='Session logs']")
    private Element sessionLogs;

    public Content(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<Element> getVideo() {
        return WaitUtil.waitComponentByCondition(video, v -> v.isStateMatches(Condition.VISIBLE));
    }

    public boolean isExpanded() {
        return content.getAttribute("value").equals("false");
    }

    public Optional<Element> getSessionLogs() {
        artifacts.click();
        return WaitUtil.waitComponentByCondition(sessionLogs, sl -> sl.isStateMatches(Condition.CLICKABLE));
    }
}
