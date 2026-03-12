package com.zebrunner.automation.gui.reporting.test;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.reporting.issue.LinkedIssueContainer;
import com.zebrunner.automation.gui.reporting.launch.LinkIssueModal;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class TestHeader extends AbstractUIObject {

    @FindBy(xpath = ".//div[contains(@class,'stability-indicator__text')]")
    private Element stability;

    @FindBy(xpath = ".//div[contains(@class,'custom-label') and (contains(@class,'main'))]//div[@class='custom-label__text-content']")
    private CustomLabel mainCustomLabel;

    @FindBy(xpath = ".//div[contains(@class,'custom-label') and not (contains(@class,'main'))]//div[@class='custom-label__text-content']")
    private List<CustomLabel> customLabels;

    @FindBy(xpath = ".//div[contains(@class,'tcm-label _link')]")
    private List<TcmLabel> tcmLabels;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.LABEL
            + "']/ancestor::div[contains(@class,'custom-label   _main')]")
    protected Element labelIcon;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.ARTIFACT
            + "']/ancestor::div[contains(@class,'custom-label   _main')]")
    protected Element artifactIcon;

    @FindBy(xpath = ".//a[contains(@class,'custom-label') and (contains(@class,'link'))]")
    private List<CustomLabel> artifactReferenceLabels;

    @FindBy(xpath = "//*[text()='Link issue']")
    private Element linkIssueBtn;

    @FindBy(xpath = "//div[contains(@class, 'linked-issue-editable__container')]")
    private LinkedIssueContainer linkedIssue;

    public TestHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getLeftCardBorderColor() {
        String color = this.getRootExtendedElement().getElement().getCssValue("border-left-color");
        return Color.fromString(color).asHex();
    }

    public boolean isArtifactsExpanded() {
        artifactIcon.waitUntil(Condition.VISIBLE);
        return artifactIcon.getElement().getAttribute("class").contains("_expanded");
    }

    public void expandArtifacts() {
        if (!isArtifactsExpanded()) {
            artifactIcon.click();
            pause(1);
        }
    }

    public void collapseArtifacts() {
        if (isArtifactsExpanded()) {
            artifactIcon.click();
            pause(1);
        }
    }

    public Optional<CustomLabel> findArtifactReferenceByName(String referenceName) {
        return artifactReferenceLabels.stream()
                                      .filter(artifactRef -> artifactRef.getTextKey().equalsIgnoreCase(referenceName))
                                      .findFirst();
    }

    public boolean isLabelsExpanded() {
        labelIcon.waitUntil(Condition.VISIBLE);
        return labelIcon.getElement().getAttribute("class").contains("_expanded");
    }

    public void expandLabels() {
        if (!isLabelsExpanded()) {
            labelIcon.click();
            pause(1);
        }
    }

    public void collapseLabels() {
        if (isLabelsExpanded()) {
            labelIcon.click();
            pause(1);
        }
    }

    public Optional<CustomLabel> findLabelsByKey(String labelKey) {
        return customLabels.stream()
                           .filter(artifactRef -> artifactRef.getTextKey().equalsIgnoreCase(labelKey))
                           .findFirst();
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

    public List<TcmLabel> getTcmLabels() {
        WaitUtil.waitCheckListIsNotEmpty(tcmLabels);
        return tcmLabels;
    }

    public LinkIssueModal clickLinkIssueButton() {
        linkIssueBtn.click();
        return new LinkIssueModal(driver);

    }

    public String getLinkedIssueValue() {
        return linkedIssue.getLinkedIssueValue();

    }

    public boolean isLinkedIssuePresent() {
        return linkedIssue.isUIObjectPresent(3);
    }

}
