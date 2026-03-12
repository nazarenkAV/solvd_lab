package com.zebrunner.automation.gui.reporting.test.session;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Optional;

public class Session extends AbstractUIObject {

    @FindBy(xpath = ".//div[@id='panel1bh-header']")
    private Header header;

    @FindBy(xpath = ".//div[@id='panel1bh-content']")
    private Content content;

    public Session(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void expandSessionIfNot() {
        if (!content.isExpanded()) {
            header.click();
            pause(2);
        }
    }

    public String getSessionId() {
        return header.getSessionId();
    }

    public Optional<Element> getVideo() {
        return content.getVideo();
    }

    public Optional<Element> getSessionLogs() {
        return content.getSessionLogs();
    }

}
