package com.zebrunner.automation.gui.reporting.test;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.reporting.test.session.Session;
import com.zebrunner.automation.legacy.Logs;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Components - get with optional<>  a
 * Pages - main logic with logging
 */
@Slf4j
@Getter
public class TestDetailsPageR extends TenantProjectBasePage implements IMobileUtils {

    public static final String URL_MATCHER =
            "https://.+\\.zebrunner\\..+/projects/.+/automation-launches/\\d{1,10}/tests/\\d{1,10}(\\?|\\z).*";

    public static final String PAGE_URL =
            ConfigHelper.getTenantUrl() + "/projects/%s/automation-launches/%d/tests/%d";

    @FindBy(xpath = "//div[@class='page-header test-details-header']")
    private TestDetailsPageHeader pageHeader;

    @Getter
    @FindBy(xpath = "//div[@class='test-execution-history']")
    private TestHistory testHistory;

    @FindBy(xpath = "//div[contains(@class,'test-details__wrapper')]")
    private TestHeader testHeader;

    @FindBy(xpath = "//div[@class='test-details__card-column _left']")
    private LogsTable logsTable;

    @FindBy(xpath = "//div[@class='test-details__card-column _right']")
    private Artifacts artifacts;

    @FindBy(xpath = "//div[contains(@class,'ZbrCard test-details__card')]")
    private ExtendedWebElement uiLoadedMarker;

    @FindBy(xpath = "//section[@class='pswp__scroll-wrap']")
    private ScreenshotsView screenshotsView;

    @FindBy(xpath = TestCardResultDetails.ROOT_XPATH)
    private TestCardResultDetails testCardResultDetails;

    @FindBy(xpath = "//span[contains(text(),'Screenshot')]")
    private ExtendedWebElement logWithScreenshot;

    public TestDetailsPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static TestDetailsPageR getPageInstance(WebDriver driver) {
        return new TestDetailsPageR(driver);
    }

    @Override
    public boolean isPageOpened() {
        boolean isUrlMatches = waitUntil(ExpectedConditions.urlMatches(URL_MATCHER), DEFAULT_EXPLICIT_TIMEOUT);
        return isUrlMatches && super.isPageOpened();
    }

    public TestDetailsPageR openPageDirectly(String projectKey, Long testRunId, Long testId) {
        this.openURL(String.format(PAGE_URL, projectKey, testRunId, testId));
        assertPageOpened();
        return this;
    }

    public void swipeToLogWithScreenshot() {
        swipe(logWithScreenshot, logsTable, Direction.UP);
    }

    public boolean isRandomScreenshotLogContainsImage() {
        final String screenshotMessage = "Screenshot is captured";

        if (!swipe(logWithScreenshot, logsTable, Direction.UP)) {
            log.error("There are no logs with screenshot message at all");
            return false;
        }

        Optional<List<LogRow>> logsWithScreenshots = logsTable.getLogsWithScreenshots();

        if (logsWithScreenshots.isEmpty()) {
            log.error("There are no logs with screenshot at all");
            return false;
        }

        Optional<LogRow> randomLogWithScreenshot = logsWithScreenshots.get().stream()
                                                                      .filter(log -> log.getMessage()
                                                                                        .equals(screenshotMessage))
                                                                      .findAny();

        if (randomLogWithScreenshot.isEmpty()) {
            log.error("There are no logs with screenshot message at all");
            return false;
        }

        Optional<Element> randomScreenshot = randomLogWithScreenshot.get().getScreenshot();

        if (randomScreenshot.isEmpty()) {
            log.error("There are no screenshot in log with screenshot message: {}", screenshotMessage);
            return false;
        }

        String screenshotLink = randomScreenshot.get().getAttributeValue("src");

        Response response = given()
                .contentType(ContentType.ANY)
                .when()
                .get(screenshotLink)
                .then()
                .extract().response();

        if (response.getStatusCode() != 200) {
            log.error("Cannot get screenshot. Expected status code: 200, but found: {}", response.getStatusCode());
            return false;
        }

        return true;
    }

    public boolean isLogMessagesPresent() {
        Optional<List<LogRow>> logs = logsTable.getLogs();
        log.info("LOGS  is present {} ", logs.isPresent());
        log.info("LOGS  is empty {} ", logs.isEmpty());
        return logs.isPresent();
    }

    public List<String> getSessionsIds() {
        Optional<List<Session>> foundSessions = artifacts.getSessions();
        if (foundSessions.isEmpty()) {
            throw new NoSuchElementException("There are no sessions at all");
        }

        return foundSessions.get().stream()
                            .map(Session::getSessionId)
                            .collect(Collectors.toList());
    }

    public boolean isVideoPresent(String sessionId) {
        Optional<Session> foundSession = artifacts.getSessionById(sessionId);

        if (foundSession.isEmpty()) {
            log.error("There are no session with id: {}", sessionId);
            return false;
        }

        Optional<Element> foundVideo = foundSession.get().getVideo();

        if (foundVideo.isEmpty()) {
            log.error("There are no video at all");
            return false;
        }

        String videoLink = foundVideo
                .get()
                .findExtendedWebElement(By.xpath(".//source"))
                .getAttribute("src");
        log.info("Videolink: {}", videoLink);

        return videoLink != null && !videoLink.isBlank();
    }

    public boolean isSessionLogsPresent(String sessionId) {

        Optional<Session> foundSession = artifacts.getSessionById(sessionId);

        if (foundSession.isEmpty()) {
            log.error("There are no session with id: {}", sessionId);
            return false;
        }

        if (foundSession.get().getSessionId().equalsIgnoreCase("Failed session")) {
            log.error("This is failed session!");
            return false;
        }

        Optional<Element> foundSessionLogs = foundSession.get().getSessionLogs();

        if (foundSessionLogs.isEmpty()) {
            log.error("There are no session logs at all");
            return false;
        }

        String sessionLogsLink = foundSessionLogs.get().getAttributeValue("href");
        log.info("Session logs link: {}", sessionLogsLink);
        Response response = given()
                .contentType(ContentType.ANY)
                .when()
                .get(sessionLogsLink)
                .then()
                .extract().response();

        if (response.getStatusCode() != 200) {
            log.error("Session logs getting response status expected: 200, but found: {}", response.getStatusCode());
            return false;
        }

        boolean isSessionIdInLogFile = Logs.checkLogStream(sessionLogsLink, "session");
        if (!isSessionIdInLogFile) {
            log.error("Session logs file is not contains 'session' word in text - maybe file is empty");
            return false;
        }

        return true;
    }

    public Artifacts getArtifacts() {
        waitUntil(ExpectedConditions.visibilityOf(artifacts.getRootExtendedElement().getElement()), 3);
        return artifacts;
    }

}
