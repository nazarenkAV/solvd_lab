package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.reporting.test.session.Session;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

@Getter
public class Artifacts extends AbstractUIObject {

    //    @FindBy(xpath = ".//td[contains(@class, '_status-info')]//button")
    //    private Element status;// moved to test header

    @FindBy(xpath = ".//td[contains(@class, '_issue')]//button")
    private Element linkIssue;

    @FindBy(xpath = ".//td[contains(@class, '_test-owner-data')]//span")
    private Element owner;

    @FindBy(xpath = ".//div[@class='sessions sessions-wrapper__scrollable']/div")
    private List<Session> sessions;

    public Artifacts(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<List<Session>> getSessions() {
        return WaitUtil.waitComponentList(sessions);
    }

    public Optional<Session> getSessionById(String sessionId) {
        return sessions.stream()
                .filter(session -> session.getSessionId().equals(sessionId))
                .findAny();
    }

    @Getter
    public enum Status {
        PASSED("Passed"), FAILED("Failed");

        private String name;

        Status(String name) {
            this.name = name;
        }
    }

}
