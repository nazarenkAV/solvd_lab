package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.gui.reporting.test.CustomLabel;
import com.zebrunner.automation.gui.reporting.test.TcmLabel;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.TestCardStatuesEnum;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Getter
public class ResultTestMethodCardR extends Element {

    @FindBy(xpath = ".//div[contains(@class,'custom-label   _main')]")
    public Element labelIcon;

    @FindBy(xpath = "//*[@d='" + SvgPaths.ARTIFACT + "']/ancestor::div[contains(@class,'custom-label   _main')]")
    public Element artifactIcon;

    @FindBy(xpath = "//*[@d='" + SvgPaths.LABEL + "']/ancestor::div[contains(@class,'custom-label   _main')]//div[@class = 'custom-label__key']")
    public ExtendedWebElement labelsTotalNumber;

    @FindBy(xpath = "//*[@d='" + SvgPaths.ARTIFACT + "']/ancestor::div[contains(@class,'custom-label   _main')]//div[@class = 'custom-label__key']")
    public ExtendedWebElement artifactsTotalNumber;

    @FindBy(xpath = ".//div[contains(@class, 'test-card ')]")
    private ExtendedWebElement cardElements;

    @FindBy(xpath = ".//span[contains(@class,'Checkbox')]")
    private ExtendedWebElement checkbox;

    @Getter(AccessLevel.NONE)
    @FindBy(xpath = ".//div[contains(@class, 'test-card__col-settings')]//button")
    private Element settingsButton;

    @Getter(AccessLevel.NONE)
    @FindBy(xpath = "//ul[@role='menu']")
    private CardMenuR cardMenuR;

    @FindBy(xpath = ".//*[@class = 'test-failure-tag__wrapper']/button")
    private ExtendedWebElement failureTagButton;

    @FindBy(xpath = ".//*[text()='Link issue']//parent::button")
    private Element linkIssueButton;

    @FindBy(xpath = ".//div[contains(@class, '_title-text')]")
    private Element cardTitle;

    @FindBy(xpath = ".//div[contains(@class, 'stability')]")
    private ExtendedWebElement stability;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.DURATION + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    private ExtendedWebElement duration;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.TEST_MAINTAINER + "']/ancestor::div[contains(@class,'attribute-label')]//div[@class='attribute-label__content']")
    private ExtendedWebElement testMaintainer;

    @FindBy(xpath = ".//div[contains(@class, 'sessions')]")
    private ExtendedWebElement testSessionInfoRef;

    @FindBy(xpath = ".//div[@class='test-card__label-items']//div[@class='custom-label__value']")
    private List<Element> elements;

    @FindBy(xpath = ".//div[contains(@class,'Zbr-stacktrace test-card__stacktrace _expandable')]")
    private Element errorStacktracePreview;

    @FindBy(xpath = ".//div[@class='stacktrace-wrapper _expanded']")
    private Element expandedStacktraceBody;

    @FindBy(xpath = ".//div[@class='stacktrace-text']")
    private Element errorFullStacktrace;

    @FindBy(xpath = TcmLabel.ROOT_XPATH)
    private List<TcmLabel> tcmLabels;

    @FindBy(xpath = "//div[contains(@class,'custom-label') and not (contains(@class,'main'))]//div[@class='custom-label__text-content']")
    private List<CustomLabel> customLabels;

    @FindBy(xpath = "//a[contains(@class,'custom-label') and (contains(@class,'link'))]")
    private List<CustomLabel> artifactReferenceLabels;

    @FindBy(xpath = ".//*[contains(@class, 'linked-issue-editable__container')]")
    private ExtendedWebElement linkedIssue;

    @FindBy(xpath = ".//*[@class = 'test-card__col-selection']//input")
    private ExtendedWebElement cardCheckbox;

    @FindBy(xpath = "//button[@class = 'linked-issue-editable__button']")
    private ExtendedWebElement linkedIssueEditButton;

    @FindBy(xpath = ".//*[contains(@class, 'Zbr-copy-to-clipboard')]")
    private ExtendedWebElement copyStackTraceButton;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public ResultTestMethodCardR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public CardMenuR getCardMenuR() {
        settingsButton.waitUntil(Condition.CLICKABLE);
        settingsButton.click();
        return cardMenuR;
    }

    public boolean isErrorStacktracePresent() {
        // used isClickable because isVisible works bad
        return errorStacktracePreview.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isCheckboxPresent() {
        return checkbox.isPresent(4) && checkbox.isVisible(4);
    }

    public String getLinkedIssueText() {
        return linkedIssue.getText();
    }

    public boolean isLinkedIssuePresent() {
        return linkedIssue.isElementPresent(2);
    }

    public void expandErrorStackTrace() {
        errorStacktracePreview.click();
        pause(1);
    }

    public boolean isStackTraceExpanded() {
        return expandedStacktraceBody.isStateMatches(Condition.PRESENT);
    }

    public String getStackTracePreview() {
        if (errorStacktracePreview.isStateMatches(Condition.PRESENT)) {
            expandErrorStackTrace();
        }
        return errorFullStacktrace.getText();
    }

    public boolean isDurationPresent() {
        return duration.isVisible();
    }

    public String getDurationText() {
        return duration.getText();
    }

    public String getTestMaintainer() {
        return testMaintainer.getText().trim();
    }

    public UserInfoTooltip hoverTestMaintainer() {
        testMaintainer.hover();
        return new UserInfoTooltip(getDriver());
    }

    public void clickTestSessionInfoRef() {
        pause(1);
        testSessionInfoRef.click();
    }

    public Boolean isLabelsVisible() {
        WaitUtil.waitCheckListIsNotEmpty(elements);
        if (elements.isEmpty()) {
            return false;
        } else {
            return elements.get(0).isStateMatches(Condition.VISIBLE);
        }
    }

    public String getLeftCardBorderColor() {
        String color = cardElements.getElement().getCssValue("border-left-color");
        return Color.fromString(color).asHex();
    }

    public void waitLeftCardBorderColor(ColorEnum color) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(5)).until(
                    ExpectedConditions.attributeContains(
                            cardElements.getElement(),
                            "border-left-color",
                            Color.fromString(color.getHexColor()).asRgba()));
        } catch (NoSuchElementException | TimeoutException ignore) {
        }
    }

    public String getStatusByLeftBorderColor() {
        return TestCardStatuesEnum.getColourStatus(getLeftCardBorderColor());
    }

    public TestCardStatuesEnum getEnumStatusByLeftBorderColor() {
        return TestCardStatuesEnum.getEnumStatusFromColor(getLeftCardBorderColor());
    }

    public boolean isStabilityPresent() {
        return stability.isPresent();
    }

    public void clickOnCard() {
        this.getRootExtendedElement().click();
    }

    public void clickCardCheckbox() {
        cardCheckbox.click();
    }

    public boolean isCardCheckboxChecked() {
        return cardCheckbox.isChecked();
    }

    public void linkIssueViaTestCard(String issueId) {
        getLinkIssueButton().click();
        LinkIssueModal linkIssueModal = new LinkIssueModal(getDriver());
        linkIssueModal.openAndSelectBugTracker(Menu.MenuItemEnum.BUG_TUCKER_JIRA);
        linkIssueModal.linkIssue(issueId);
    }

    public void clickLinkIssue() {
        linkIssueButton.click();
    }

    public void clickLinkedIssueButton() {
        linkedIssue.click();
    }

    public FailureTagModal getFailureTagModal() {
        failureTagButton.click();
        return new FailureTagModal(getDriver());
    }

    public String getFailureTagText() {
        return failureTagButton.getText();
    }

    public boolean isFailureTagButtonPresent() {
        return failureTagButton.isElementPresent(3);
    }

    public String getCardTitleText() {
        return cardTitle.getText();
    }

    public boolean isDefaultTagSelected() {
        return getFailureTagText().equals(FailureTagModal.UNCATEGORIZED_TAG);
    }

    public void hoverCard() {
        cardTitle.hover();
    }

    public void selectDefaultTag() {
        FailureTagModal failureTagModal = getFailureTagModal();
        failureTagModal.clickUncategorizedTagButton();
        failureTagModal.clickSaveButton();
    }

    public LinkIssueModal clickLinkIssueEditButton() {
        linkedIssueEditButton.click();
        return new LinkIssueModal(getDriver());
    }

    public void expandCollapseLabels() {
        labelIcon.click();
    }

    public void expandArtifacts() {
        artifactIcon.click();
    }

    public String getLabelsTotalNumber() {
        return labelsTotalNumber.getText();
    }

    public String getArtifactsTotalNumber() {
        return artifactsTotalNumber.getText();
    }

    public String hoverCopyStackTraceButtonAndGetToolTipText() {
        copyStackTraceButton.hover();
        return tooltip.getTooltipText();
    }

    public String clickCopyStackTraceButtonAndGetToolTipText() {
        copyStackTraceButton.click();
        return tooltip.getTextFromTooltipDirectly();
    }

    public boolean isTcmLabelVisible(String label) {
        for (TcmLabel tcmLabel : tcmLabels) {
            if (tcmLabel.getTextValue().equalsIgnoreCase(label)) {
                return tcmLabel.getRootExtendedElement().isVisible(3);
            }
        }
        throw new NoSuchElementException("Label '" + label + "' not found");
    }

    public Optional<TcmLabel> findTcmLabel(String labelValue) {
        return tcmLabels.stream()
                        .filter(artifactRef -> artifactRef.getTextValue().equalsIgnoreCase(labelValue))
                        .findFirst();
    }

    public TcmLabel _findTcmLabel(String labelValue) {
        return tcmLabels.stream()
                        .filter(artifactRef -> artifactRef.getTextValue().equalsIgnoreCase(labelValue))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(String.format("TCM label '%s' was not found!", labelValue)));
    }

}