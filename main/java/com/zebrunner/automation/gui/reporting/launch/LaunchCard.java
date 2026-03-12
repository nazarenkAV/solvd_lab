package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import lombok.Getter;

import java.time.Duration;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class LaunchCard extends AbstractUIObject {

    public static final Duration TEST_RUN_FINISH_WAITING_TIME = Duration.ofSeconds(1500);
    public static final Duration TEST_RUN_FINISH_INTERVAL_WAITING_TIME = Duration.ofSeconds(5);

    @FindBy(xpath = ".")
    protected StatusR status;

    @FindBy(xpath = ".//div[contains(@class, 'launches-item__checkbox' )]//input")
    protected ExtendedWebElement checkBox;

    @FindBy(xpath = ".//div[contains(@class, 'launches-item__checkbox')]//span[contains(@class, 'MuiCircularProgress-root')]")
    protected ExtendedWebElement checkBoxInProgress;

    @FindBy(xpath = ".//div[@class='launches-item__attributes']")
    protected LaunchCardAttributes launchCardAttributes;

    @FindBy(xpath = ".//div[@class='launches-item__configuration-options']")
    protected LaunchCardConfiguration launchCardConfiguration;

    @FindBy(xpath = ".//div[@class='launches-item__title']")
    protected ExtendedWebElement titleName;

    @FindBy(xpath = ".//div[contains(@class, 'launches-item__reviewed')]//button")
    protected Element reviewedBadge;
    @FindBy(xpath = ".//div[contains(@class, 'comments')]/button")
    protected Element commentsButton;//not such icon now
    @FindBy(xpath = ".//div[@class='environment-label']")
    protected Element environment;

    @FindBy(xpath = ".//div[contains(@class, 'launches-item__statistics')]")
    protected StatisticsR statistics;

    @FindBy(xpath = ".//div[contains(@class, 'launches-item__menu' )]//button")
    private ExtendedWebElement menuButton;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public LaunchCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isCheckBoxSelected() {
        return Boolean.parseBoolean(checkBox.getAttribute("value"));
    }

    public boolean isCheckBoxPresent(int timeInSec) {
        return checkBox.isPresent(timeInSec);
    }

    public MenuContent clickMenu() {
        menuButton.click();
        return new Menu(getDriver()).getMenuContent();
    }

    public boolean waitForCheckBoxInProgress() {
        return waitUntil(ExpectedConditions.visibilityOf(checkBoxInProgress.getElement()), 800);
    }

    public void clickCheckbox() {
        checkBox.click();
    }

    public void clickCheckboxInProgress() {
        checkBoxInProgress.click();
    }

    public void markAsReviewed(String comment) {
        clickMenu().getMarkAsReviewed().click();

        MarkAsReviewedModal markAsReviewedModal = new MarkAsReviewedModal(getDriver());
        markAsReviewedModal.getMessageForReview().sendKeys(comment, false, false);
        markAsReviewedModal.getSubmitButton().click();
    }

    public String getCardName() {
        return titleName.getText();
    }

    public boolean isLaunchFinished() {
        return launchCardAttributes.isLaunchFinished();
    }

    public LaunchCard waitFinish() {
        return waitFinish(TEST_RUN_FINISH_WAITING_TIME, TEST_RUN_FINISH_INTERVAL_WAITING_TIME);
    }

    public LaunchCard waitFinish(Duration waitingTime, Duration interval) {
        return WaitUtil.waitElementMatchByCondition(
                this,
                LaunchCard::isLaunchFinished,
                waitingTime, interval,
                "Test run finished",
                "Test run card not finished -  timeout exceeded"
        );
    }

    public void assignMilestone(String milestoneName) {
        clickMenu().getAssignToMilestone().click();
        AssignToMilestoneModalR assignToMilestoneModalR = new AssignToMilestoneModalR(getDriver());
        assignToMilestoneModalR.chooseMilestoneAndAssign(milestoneName);
    }

    public boolean isReviewedBadgePresent() {
        return reviewedBadge.isStateMatches(Condition.VISIBLE);
    }

    public String getEnvTextValue() {
        return environment.getText();
    }

    public Element getDuration() {
        return launchCardAttributes.getDuration();
    }

    public Element getStateAndTimeFromStart() {
        return launchCardAttributes.getStateAndTimeFromStart();
    }

    public Element getMilestone() {
        return launchCardAttributes.getMilestone();
    }

    public PlatformR getPlatform() {
        return launchCardConfiguration.getPlatform();
    }

    public Browser getBrowser() {
        return launchCardConfiguration.getBrowser();
    }

    public Element getLocale() {
        return launchCardConfiguration.getLocale();
    }

    public TestRunResultPageR toTests() {
        this.getRootExtendedElement().click();
        return TestRunResultPageR.getPageInstance(driver);
    }

    public boolean isMilestonePresent() {
        return getMilestone().isUIObjectPresent(3);
    }

    public String getNameOfAssignedMilestone() {
        return getMilestone().getText();
    }

    public String hoverEnvironmentAndGetToolTipValue() {
        environment.hover();
        pause(1);
        return tooltip.getTooltipText();
    }

}
