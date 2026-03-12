package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Optional;

public class StartedR extends AbstractUIObject {

    @FindBy(xpath = ".//div[@class='time']//span")
    private Element status;

    @FindBy(xpath = ".//div[@class='time']//time")
    private Element timeAgo;

    @FindBy(xpath = ".//duration//span[@aria-hidden='false']")
    private Element duration;

    public StartedR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<Element> getStatus() {
        return Optional.of(status);
    }

    public Optional<Element> getTimeAgo() {
        return Optional.of(timeAgo);
    }

    public Optional<Element> getDuration() {
        return Optional.of(duration);
    }
}
