package com.zebrunner.automation.gui.smoke;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.test.TestCardResultDetails;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.TooltipEnum;
import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Slf4j
@Maintainer("Gmamaladze")
public class TestCardActions extends LogInBase {

    private final List<Long> testRunIdList = new ArrayList<>();
    private Project project;
    private TestClassLaunchDataStorage testClassLaunchDataStorage;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        testRunIdList.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    //----------------------------------------------Test---------------------------------------------------------

    @TestCaseKey("ZTP-1259")
    @Test(groups = "min_acceptance")
    public void labelAndArtifactCount() {
        WebDriver webDriver = super.getDriver();

        List<Label> labels = List.of(
                new Label("Platform", "Zebrunner"),
                new Label("Framework", "Carina"),
                new Label("TCM", "Zebrunner"),
                new Label("Java", "Carina"),
                new Label("Platform", "Jira")
        );
        List<ArtifactReference> artifacts = List.of(
                new ArtifactReference("Zebrunner", "https://zebrunner.com/documentation/"),
                new ArtifactReference("Google", "https://www.google.com/"),
                new ArtifactReference("Solvd", "https://www.solvd.com/"),
                new ArtifactReference("Carina", "https://zebrunner.github.io/carina/"),
                new ArtifactReference("Java", "https://www.java.com/en/")
        );

        // Formatting and sorting labels as String in one line. for instance "Platform: Zebrunner"
        List<String> formattedLabels = StreamUtils.mapToStream(labels, label -> label.getKey() + ": " + label.getValue())
                                                  .sorted()
                                                  .collect(Collectors.toList());

        //Sorted and created new list for artifact names
        List<String> artifactNames = StreamUtils.mapToStream(artifacts, ArtifactReference::getName)
                                                .sorted()
                                                .collect(Collectors.toList());

        Launch launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        TestExecution testExecution = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        apiHelperService.addLabelsToTest(launch.getId(), testExecution.getId(), labels);
        apiHelperService.addArtReferencesToTest(launch.getId(), testExecution.getId(), artifacts);
        testService.finishTestAsResult(launch.getId(), testExecution.getId(), "PASSED");
        testRunService.finishLaunch(launch.getId());
        testRunIdList.add(launch.getId());

        TestRunResultPageR launchPage = new TestRunResultPageR(webDriver).openPageDirectly(project.getKey(), launch.getId());
        ResultTestMethodCardR testCard = launchPage.getCertainTest(testExecution.getName());

        Assert.assertEquals(
                testCard.getLabelsTotalNumber(), String.valueOf(labels.size()),
                "Quantity of labels is not as expected !"
        );

        testCard.expandCollapseLabels();

        // Took actual labels from card, format them and sort
        List<String> actualLabels = StreamUtils.mapToStream(
                                                       testCard.getCustomLabels(),
                                                       label -> label.getKey().getText() + label.getValue().getText()
                                               )
                                               .sorted()
                                               .collect(Collectors.toList());
        Assert.assertFalse(actualLabels.isEmpty(), "Labels list is empty !");

        for (int i = 0; i < actualLabels.size(); i++) {
            Assert.assertEquals(actualLabels.get(i), formattedLabels.get(i), "Label is not as expected !");
        }

        testCard.expandCollapseLabels();
        Assert.assertTrue(testCard.getCustomLabels().isEmpty(), "Labels is not collapsed !");
        Assert.assertEquals(testCard.getArtifactsTotalNumber(), String.valueOf(artifacts.size()), "Quantity of artifacts is not as expected !");

        testCard.expandArtifacts();

        // Took actual artifact from card, format them and sort
        List<String> actualArtifactNames = StreamUtils.mapToList(
                testCard.getArtifactReferenceLabels(), artifact -> artifact.getKey().getText()
        );

        Assert.assertFalse(actualArtifactNames.isEmpty(), "Artifact list is empty !");
        for (int i = 0; i < actualArtifactNames.size(); i++) {
            Assert.assertEquals(actualArtifactNames.get(i), artifactNames.get(i), "Artifact is not as expected !");
        }
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-4623", "ZTP-4625"})
    public void verifyTooltipsAppearAfterClickingOrHoveringOnCopyStackTraceButton() {
        com.zebrunner.agent.core.registrar.Label.attachToTest(TestLabelsConstant.BUG, "ZEB-6872");

        SoftAssert softAssert = new SoftAssert();

        testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        testRunIdList.add(testClassLaunchDataStorage.getLaunch().getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        ResultTestMethodCardR failedTestCard = testRunResultPage.getFailedTestCards().get(0);

        String toolTipText = failedTestCard.hoverCopyStackTraceButtonAndGetToolTipText();

        softAssert.assertEquals(toolTipText, TooltipEnum.TOOLTIP_COPY_STACK_TRACE_BUTTON_HOVER,
                "Tooltip text is not as expected after hovering copy stacktrace button on test card !");

        toolTipText = failedTestCard.clickCopyStackTraceButtonAndGetToolTipText();

        softAssert.assertEquals(toolTipText, TooltipEnum.TOOLTIP_COPY_CLICKED,
                "Tooltip text is not as expected after clicking copy stacktrace button on test card !");

        failedTestCard.clickOnCard();

        TestDetailsPageR testDetailsPage = new TestDetailsPageR(getDriver());
        testDetailsPage.assertPageOpened();

        TestCardResultDetails testCardResultDetails = testDetailsPage.getTestCardResultDetails();

        toolTipText = testCardResultDetails.hoverCopyStackTraceButtonAndGetToolTipText();

        softAssert.assertEquals(toolTipText, TooltipEnum.TOOLTIP_COPY_STACK_TRACE_BUTTON_HOVER,
                "Tooltip text is not as expected after hovering copy stacktrace button on test details page card !");

        toolTipText = testCardResultDetails.clickCopyStackTraceButtonAndGetToolTipText();

        softAssert.assertEquals(toolTipText, TooltipEnum.TOOLTIP_COPY_CLICKED,
                "Tooltip text is not as expected after clicking copy stacktrace button on test details page card !");

        softAssert.assertAll();
    }
}