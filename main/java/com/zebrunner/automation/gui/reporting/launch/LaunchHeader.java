package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.List;

import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class LaunchHeader extends AbstractUIObject {

    @FindBy(xpath = ".//*[@d='" + SvgPaths.LABEL + "']/ancestor::button[contains(@class,'launch-card__label')]")
    public ExtendedWebElement labelIcon;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.ARTIFACT + "']/ancestor::button[contains(@class,'launch-card__label')]")
    public ExtendedWebElement artifactIcon;

    @FindBy(xpath = "//*[@class = 'environment-label']")
    private ExtendedWebElement environment;

    @FindBy(xpath = ".//div[@class='launch-card__attributes']")
    private LaunchCardAttributes launchCardAttributes;

    @FindBy(xpath = ".//div[contains(@class,'chart-wrapper')]")
    private Element statsChart;

    @FindBy(xpath = ".//div[contains(@class,'launch-card__passrate-chart')]")
    private Element passRateChart;

    @FindBy(xpath = ".//div[@class='launch-statistics']")
    private ExtendedWebElement launchStatistics;

    @FindBy(xpath = "//ul[@role='menu']//div[@class='custom-label__text-content']")
    private List<ExtendedWebElement> labels;

    @FindBy(xpath = "//ul[@role='menu']//div[@class='custom-label__text-content']")
    private List<ExtendedWebElement> artifactReferences;

    public LaunchHeader(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getMilestoneName() {
        return launchCardAttributes.getMilestoneName();
    }

    public String getEnvironment() {
        return environment.getText();
    }

    public String getTime() {
        return launchCardAttributes.getTime();
    }

    public String getDuration() {
        return launchCardAttributes.getDurationTime();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isMilestonePresent() {
        return launchCardAttributes.getMilestone().isStateMatches(Condition.VISIBLE);
    }

    public boolean isLaunchTimePresent() {
        return launchCardAttributes.getStateAndTimeFromStart().isStateMatches(Condition.VISIBLE);
    }

    public boolean isDurationPresent() {
        return launchCardAttributes.getDuration().isStateMatches(Condition.VISIBLE);
    }

    public boolean isStatisticsChartPresent() {
        return statsChart.isStateMatches(Condition.VISIBLE);
    }

    public boolean isPassRateChartPresent() {
        return passRateChart.isStateMatches(Condition.VISIBLE);
    }

    public boolean isArtifactIconPresent() {
        return artifactIcon.isPresent(3);
    }

    public boolean isLabelIconPresent() {
        return labelIcon.isPresent(3);
    }

    public boolean isLaunchStatisticsPresent() {
        return launchStatistics.isVisible(3);
    }

    public boolean containsLabel(Label label) {
        return StreamUtils.mapToStream(labels, ExtendedWebElement::getText)

                          .filter(labelText -> labelText.contains(label.getKey()))
                          .anyMatch(labelText -> labelText.contains(label.getValue()));
    }

    public boolean containsArtifactReference(ArtifactReference artifactReference) {
        return StreamUtils.mapToStream(artifactReferences, ExtendedWebElement::getText)
                          .anyMatch(reference -> reference.contains(artifactReference.getName()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void clickLabelIcon() {
        labelIcon.click();
    }

    public void clickArtifactIcon() {
        artifactIcon.click();
    }

    public void clickLabels() {
        labelIcon.click(2);
    }

}
