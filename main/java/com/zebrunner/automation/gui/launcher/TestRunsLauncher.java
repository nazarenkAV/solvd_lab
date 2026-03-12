package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.launcher.domain.LauncherWeb;
import com.zebrunner.automation.api.tcm.domain.Repository;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.reporting.TestRunsPageR;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

//className = "md-dialog-content"
public class TestRunsLauncher extends TenantProjectBasePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String URL = ConfigHelper.getTenantUrl() + "/projects/DEF/launchers/761"; //FIXME Change URL
    public static final String PAGE_NAME = "Launchers";

    @FindBy(xpath = "//div[contains(@class,'launcher-tree__repo-name ng-binding')]")
    private List<Element> repositoryList;

    @FindBy(xpath = ".//div[@class='list-arrow-item__content']//div[contains(@class,'launcher-tree__item-launcher-name')]")
    private List<Element> launchersList;

    @FindBy(xpath = "//button[@class='md-raised btn-w-xs md-primary _pull-right _small md-button md-ink-ripple']")
    private Element launchSuiteButton;

    public TestRunsLauncher(WebDriver driver) {
        super(driver);
    }

    public static TestRunsLauncher openPageDirectly(WebDriver driver) {
        LOGGER.info("Attempt to go to the page '{}' directly. Its URL: '{}'", PAGE_NAME, URL);
        TestRunsLauncher testRunsLauncher = new TestRunsLauncher(driver);
        testRunsLauncher.openURL(URL);
        testRunsLauncher.pause(4);
        return testRunsLauncher;
    }

    public static TestRunsLauncher openPage(WebDriver driver) {
        LOGGER.info("Attempt to go to the page '{}'", PAGE_NAME);
        TestRunsLauncher testRunsLauncher = new TestRunsLauncher(driver);
        testRunsLauncher.pause(4);
        return testRunsLauncher;
    }

    public TestRunsPageR launchLauncher(LauncherWeb launcher) throws NoSuchElementException {
        Repository suiteRepo = launcher.getSuite().getRefersToRepo();

        WaitUtil.waitElementAppearedInListByCondition(repositoryList,
                        repository -> repository.getText().trim().equals(
                                suiteRepo.getTitle()),
                        "Repository with name " + suiteRepo.getTitle() + " was found",
                        "There are no repository with name: " + suiteRepo.getTitle())
                .click();

        WaitUtil.waitElementAppearedInListByCondition(launchersList,
                        suiteElement -> suiteElement.getText()
                                .trim()
                                .equals(launcher.getLaunchName()),
                        "Launcher  with name: " + launcher.getLaunchName() + " was found",
                        "There are no launcher  with name: " + launcher.getLaunchName())
                .click();
        pause(4);
        launch();
        return TestRunsPageR.getPageInstance(getDriver());
    }

    public TestRunsPageR launch() {
        scrollSuiteToLaunchButton();
        launchSuiteButton.click();
        return TestRunsPageR.getPageInstance(getDriver());
    }

    private TestRunsLauncher scrollSuiteToLaunchButton() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].scrollIntoView();", launchSuiteButton.getElement());
        return this;
    }

}
