package com.zebrunner.automation.gui.tcm;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.annotation.TestLabel;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Label;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.api.tcm.domain.SharedStepsBunch;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestCasePriority;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunSettings;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.gui.reporting.launch.ResultTestMethodCardR;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.gui.reporting.test.TestDetailsPageR;
import com.zebrunner.automation.gui.tcm.testcase.AbstractTestCasePreview;
import com.zebrunner.automation.gui.tcm.testcase.AttachmentItem;
import com.zebrunner.automation.gui.tcm.testcase.AttachmentsTab;
import com.zebrunner.automation.gui.tcm.testcase.CloneTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.DedicatedTestCasePage;
import com.zebrunner.automation.gui.tcm.testcase.EditStepsModal;
import com.zebrunner.automation.gui.tcm.testcase.EditTestCaseModal;
import com.zebrunner.automation.gui.tcm.testcase.ExecutionsTab;
import com.zebrunner.automation.gui.tcm.testcase.ModalGeneralTab;
import com.zebrunner.automation.gui.tcm.testcase.PropertiesTab;
import com.zebrunner.automation.gui.tcm.testcase.RepositoryCaseItem;
import com.zebrunner.automation.gui.tcm.testcase.RepositoryPreviewStepContainer;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseModalView;
import com.zebrunner.automation.gui.tcm.testcase.TestCaseSideBarView;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinPage;
import com.zebrunner.automation.gui.tcm.testcase.TrashBinTestCaseRightSideBar;
import com.zebrunner.automation.gui.tcm.testrun.TestRunPage;
import com.zebrunner.automation.gui.tcm.testrun.TestRunsGridPage;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.legacy.TcmType;
import com.zebrunner.automation.util.FileUtils;
import com.zebrunner.automation.util.PageUtil;

@Slf4j
@Maintainer("obabich")
@TestLabel(name = "Feature", value = "Dedicated page")
public class DedicatedTestCasePageTest extends TcmLogInBase {

    private final static String IMAGES_ZEB_PNG = "src/test/resources/images/zeb.png";

    private Project project;
    private TestSuite testSuite_1;
    private TestCase testCaseForSuite_1;

    private List<TestCase> allTestCases;
    private TestRun testRun;

    private Launch launch;


    @BeforeClass
    public void getProjectKey() {
        project = super.getCreatedProject();

        testSuite_1 = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        testCaseForSuite_1 = tcmService.createTestCase(project.getId(), testSuite_1.getId());

        allTestCases = tcmService.createTestCases(project.getId(), testSuite_1.getId(), 2);

        testRun = tcmService.createTestRun(project.getId(), allTestCases, TestRun.createWithRandomName());

        TestRunSettings testRunSettings = tcmService.getTestRunSettings(project.getId());

        tcmService.addTestRunResults(project.getId(), testRun, allTestCases, testRunSettings, "Failed");

    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedData() {
        tcmService.deleteTestSuite(project.getId(), testSuite_1.getId());
        if (launch != null) {
            testRunService.deleteLaunch(project.getId(), launch.getId());
        }
    }

    @Test
    @TestCaseKey({"ZTP-5024"})
    public void userIsAbleToBrowseTestCaseOnDedicatedPageTest() {
        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        TestCaseSideBarView sideBarView = testCasesPage
                .findTestCase(testCaseForSuite_1.getTitle())
                .clickTestCase()
                .toSideBarView();

        DedicatedTestCasePage dedicatedTestCasePage = sideBarView.clickTestCaseKeyAndSwitchTab();
        softAssert.assertTrue(dedicatedTestCasePage.isPageOpened(),
                "Dedicated test case page is not opened when attempting to open via side bar view!");

        testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());

        TestCaseModalView modalView = testCasesPage
                .findTestCase(testCaseForSuite_1.getTitle())
                .clickTestCase()
                .toModalView();

        dedicatedTestCasePage = modalView.clickTestCaseKeyAndSwitchTab();
        softAssert.assertTrue(dedicatedTestCasePage.isPageOpened(),
                "Dedicated test case page is not opened when attempting to open via modal view!");

        testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        dedicatedTestCasePage = testCasesPage.findTestCase(testCaseForSuite_1.getTitle())
                                             .openCaseInNewTab();

        PageUtil.toOtherTab(getDriver());

        softAssert.assertTrue(dedicatedTestCasePage.isPageOpened(),
                "Dedicated test case page is not opened when clicking on test case key in repository ite !");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5032"})
    public void userIsAbleToOpenDedicatedPageWithUrlTest() {
        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        DedicatedTestCasePage dedicatedTestCasePage = testCasesPage.findTestCase(testCaseForSuite_1.getTitle())
                                                                   .openCaseInNewTab();

        PageUtil.toOtherTab(getDriver());

        softAssert.assertTrue(dedicatedTestCasePage.isPageOpened(),
                "Dedicated test case page is not opened when clicking on test case key in repository ite !");

        String url = dedicatedTestCasePage.getDriver().getCurrentUrl();

        testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.assertPageOpened();
        softAssert.assertFalse(dedicatedTestCasePage.isPageOpened(7),
                "Dedicated test case page shouldn't be opened when opened Test case page!");
        getDriver().get(url);

        softAssert.assertTrue(dedicatedTestCasePage.isPageOpened(),
                "Dedicated test case page is not opened when opening by URL !");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5026"})
    public void userIsAbleToDeleteTestCaseFromDedicatedPageTest() {
        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite_1.getId());
        SoftAssert softAssert = new SoftAssert();

        DedicatedTestCasePage dedicatedTestCasePage = DedicatedTestCasePage.openPageDirectly(getDriver(), project.getKey(), testCase.getId());
        dedicatedTestCasePage.openDeleteTestCaseModal()
                             .clickDelete();
        softAssert.assertEquals(dedicatedTestCasePage.getPopUp(),
                String.format("Case \"%s\" was successfully deleted", testCase.getTitle()),
                "Toast message is not as expected!");

        TestCasesPage testCasesPage = new TestCasesPage(getDriver());
        testCasesPage.assertPageOpened();
        testCasesPage.searchTestCase(testCase.getKey());

        softAssert.assertFalse(testCasesPage.isTestCasePresent(testCase.getKey()), "Deleted test case shouldn't be present in repository");
        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5154")
    public void userIsAbleToEditTestCaseFromDedicatedPageWithEditTestCaseModalTest() {
        WebDriver webDriver = super.getDriver();
        TestCase testCase = tcmService.createTestCase(
                project.getId(), testSuite_1.getId(), new TestCase("Case" + RandomStringUtils.randomNumeric(15))
        );

        String newTitle = "New title for " + testCase.getKey();
        String newDescription = "New description for " + testCase.getKey();
        String newPostConditions = "New post conditions for " + testCase.getKey();
        String newPreConditions = "New pre conditions for " + testCase.getKey();

        DedicatedTestCasePage testCasePage = DedicatedTestCasePage.openPageDirectly(webDriver, project.getKey(), testCase.getId());

        EditTestCaseModal editTestCaseModal = testCasePage.openEditTestCaseModal();
        editTestCaseModal.inputTitle(newTitle)
                         .inputDescription(newDescription)
                         .inputPreConditions(newPreConditions)
                         .inputPostConditions(newPostConditions)
                         .submitModal();

        Assert.assertEquals(
                testCasePage.getPopUp(), "Case \""+ newTitle + "\" was successfully edited",
                "Toast message is not as expected!"
        );

        Assert.assertEquals(testCasePage.getTestCaseTitle(), newTitle, "Title is not as expected!(Dedicated page)");
        Assert.assertEquals(testCasePage.getPreConditions(), newPreConditions, "Pre conditions are not as expected!(Dedicated page)");
        Assert.assertEquals(testCasePage.getPostConditions(), newPostConditions, "Post conditions are not as expected!(Dedicated page)");
        Assert.assertEquals(testCasePage.getDescriptionTextValue(), newDescription, "Description is not as expected!(Dedicated page)");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(webDriver, project.getKey());
        testCasesPage.searchTestCase(testCase.getKey());

        RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCase.getKey());

        Assert.assertEquals(caseItem.getTestCaseTitleValue(), newTitle, "Title is not as expected!(Modal view)");

        TestCaseModalView modalView = caseItem.clickTestCase().toModalView();
        ModalGeneralTab modalGeneralTab = modalView.openGeneralTab();

        Assert.assertEquals(modalGeneralTab.getPreconditions(), newPreConditions, "Pre conditions are not as expected!(Modal view)");
        Assert.assertEquals(modalGeneralTab.getPostConditions(), newPostConditions, "Post conditions are not as expected!(Modal view)");
        Assert.assertEquals(modalGeneralTab.getDescriptionText(), newDescription, "Description is not as expected!(Modal view)");
    }

    @Test
    @TestCaseKey({"ZTP-5027"})
    public void userIsAbleToEditTestCaseFieldsFromDedicatedPageTest() {
        SoftAssert softAssert = new SoftAssert();

        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite_1.getId(),
                new TestCase("Case" + RandomStringUtils.randomNumeric(15)));

        String newDescription = "New description for ".concat(testCase.getKey());
        String newPostConditions = "New post conditions for ".concat(testCase.getKey());
        String newPreConditions = "New pre conditions for ".concat(testCase.getKey());
        String newPriority = TestCasePriority.Priority.LOW.getPriority();

        DedicatedTestCasePage dedicatedTestCasePage = DedicatedTestCasePage.openPageDirectly(getDriver(), project.getKey(), testCase.getId());
        dedicatedTestCasePage
                .inputDescription(newDescription)
                .inputPreConditions(newPreConditions)
                .inputPostConditions(newPostConditions)
                .selectPriority(newPriority);

        softAssert.assertEquals(dedicatedTestCasePage.getPreConditions(), newPreConditions, "Pre conditions are not as expected!(Dedicated page)");
        softAssert.assertEquals(dedicatedTestCasePage.getPostConditions(), newPostConditions, "Post conditions are not as expected!(Dedicated page)");
        softAssert.assertEquals(dedicatedTestCasePage.getDescriptionTextValue(), newDescription, "Description is not as expected!(Dedicated page)");
        softAssert.assertEquals(dedicatedTestCasePage.getPriority(), newPriority, "Priority is not as expected!(Dedicated page)");

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.searchTestCase(testCase.getKey());

        RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCase.getKey());

        TestCaseModalView modalView = caseItem.clickTestCase().toModalView();
        ModalGeneralTab modalGeneralTab = modalView.openGeneralTab();

        softAssert.assertEquals(modalGeneralTab.getPreconditions(), newPreConditions, "Pre conditions are not as expected!(Modal view)");
        softAssert.assertEquals(modalGeneralTab.getPostConditions(), newPostConditions, "Post conditions are not as expected!(Modal view)");
        softAssert.assertEquals(modalGeneralTab.getDescriptionText(), newDescription, "Description is not as expected!(Modal view)");

        PropertiesTab propertiesTab = modalView.openPropertiesTab();
        softAssert.assertEquals(propertiesTab.getPriorityValue(), newPriority, "Priority is not as expected!(Modal view)");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5151"})
    public void userIsAbleToEditTestCaseStepsFromDedicatedPageTest() {
        SoftAssert softAssert = new SoftAssert();

        TestCaseStep testCaseStep = TestCaseStep.withRandomActionAndExpectedResult();
        SharedStepsBunch shared = tcmService.createSharedStep(project.getId(), SharedStepsBunch.generateRandom(1));

        TestCase testCase = new TestCase("Case" + RandomStringUtils.randomNumeric(15));
        testCase.addStep(TestCase.Step.regular(testCaseStep));

        testCase = tcmService.createTestCase(project.getId(), testSuite_1.getId(), testCase);

        String newAction = "Updated action ".concat(testCase.getKey());
        String newExpectedResult = "Updated expected result ".concat(testCase.getKey());
        TestCaseStep updatedStep = new TestCaseStep(newAction, newExpectedResult);

        TestCaseStep newStep = TestCaseStep.withRandomActionAndExpectedResult();

        List<TestCaseStep> expectedSteps = List.of(updatedStep, shared.getSteps().get(0), newStep);

        DedicatedTestCasePage dedicatedTestCasePage = DedicatedTestCasePage.openPageDirectly(getDriver(), project.getKey(), testCase.getId());
        EditStepsModal editStepsModal = dedicatedTestCasePage.openEditStepsModal();
        editStepsModal.
                findStepByAction(testCaseStep.getAction())
                .fillStep(updatedStep);

        editStepsModal.addSharedStep(shared);

        editStepsModal.addSteps(List.of(newStep), true);
        editStepsModal.submitModal();

        softAssert.assertEquals(dedicatedTestCasePage.getPopUp(),
                String.format("Case \"%s\" was successfully edited", testCase.getTitle()),
                "Toast message is not as expected!");

        pause(3);
        TriConsumer<List<RepositoryPreviewStepContainer>, TestCaseStep, String> verifyStep = (existingSteps, s, verificationPlace) -> {
            log.info("Verifying step with action: " + s.getAction());

            existingSteps.stream()
                         .filter(step -> step.getActionValue().equals(s.getAction()))
                         .findFirst()
                         .ifPresentOrElse(step ->
                                         softAssert.assertEquals(step.getExpectedResultValue(), s.getExpectedResult(),
                                                 "Expected result is not as expected!" + verificationPlace),
                                 () -> softAssert.fail("Step not found with action: " + s.getAction() + verificationPlace)
                         );
        };

        List<RepositoryPreviewStepContainer> steps = dedicatedTestCasePage.getSteps();
        expectedSteps.forEach(s -> verifyStep.accept(steps, s, "(Dedicated page)"));


        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        testCasesPage.searchTestCase(testCase.getKey());

        RepositoryCaseItem caseItem = testCasesPage.findTestCase(testCase.getKey());

        TestCaseModalView modalView = caseItem.clickTestCase().toModalView();
        ModalGeneralTab modalGeneralTab = modalView.openGeneralTab();

        List<RepositoryPreviewStepContainer> modalSteps = modalGeneralTab.getSteps();
        expectedSteps.forEach(s -> verifyStep.accept(modalSteps, s, "(Modal view)"));

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5025")
    public void userIsAbleToCloneTestCaseFromDedicatedPageTest() {
        WebDriver webDriver = super.getDriver();

        TestCase testCase = tcmService.createTestCase(
                project.getId(), testSuite_1.getId(), new TestCase("Case" + RandomStringUtils.randomNumeric(15))
        );
        String newTitle = "Cloned " + UUID.randomUUID() + " from suite " + testSuite_1.getTitle();

        DedicatedTestCasePage dedicatedTestCasePage = DedicatedTestCasePage.openPageDirectly(webDriver, project.getKey(), testCase.getId());
        CloneTestCaseModal cloneTestCaseModal = dedicatedTestCasePage.openCloneTestCaseModal();

        int numberTabsBeforeClone = PageUtil.getNumberOfOpenedWindows(webDriver);

        cloneTestCaseModal.inputTitle(newTitle).clickClone();
        cloneTestCaseModal.waitUntilElementDisappear(15);

        int numberTabsAfterClone = PageUtil.getNumberOfOpenedWindows(webDriver);
        Assert.assertEquals(numberTabsAfterClone - numberTabsBeforeClone, 1, "Cloned test case is not opened in new tab!");

        PageUtil.toOtherTab(webDriver);

        dedicatedTestCasePage = new DedicatedTestCasePage(webDriver);
        Assert.assertEquals(dedicatedTestCasePage.getTestCaseTitle(), newTitle, "Title is not as expected!");
        Assert.assertNotEquals(dedicatedTestCasePage.getTestCaseKeyTextValue(), testCase.getKey(), "Test case key should be different from original test case key!");
    }

    @Test
    @TestCaseKey({"ZTP-5108"})
    public void userIsAbleToOpenDedicatedPageFromTestRunTest() {
        SoftAssert softAssert = new SoftAssert();

        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        TestRunPage testRunPage = testRunsGridPage.searchAndOpenTestRun(testRun.getTitle());

        TestCase testCase = allTestCases.get(0);
        AbstractTestCasePreview<?> modal = testRunPage.getTestSuite(testSuite_1.getTitle())
                                                      .getTestCase(testCase.getTitle())
                                                      .clickTestCase();

        DedicatedTestCasePage dedicatedTestCasePage = modal.clickTestCaseKeyAndSwitchTab();
        dedicatedTestCasePage.assertPageOpened();

        softAssert.assertEquals(dedicatedTestCasePage.getTestCaseTitle(), testCase.getTitle(),
                "Title is not as expected!");
        softAssert.assertEquals(dedicatedTestCasePage.getTestCaseKeyTextValue(), testCase.getKey(),
                "Test case key is not as expected!");

        softAssert.assertAll();

    }


    @Test
    @TestCaseKey({"ZTP-5148", "ZTP-5150"})
    public void userIsAbleToOpenDedicatedPageFromTcmPreviewTest() {
        SoftAssert softAssert = new SoftAssert();
        Label label = new Label(TcmType.ZEBRUNNER.getLabelKey(), testCaseForSuite_1.getKey());

        launch = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        TestExecution test = testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        apiHelperService.addLabelsToTest(launch.getId(), test.getId(), List.of(label));
        testService.finishTestAsResult(launch.getId(), test.getId(), "PASSED");
        testRunService.finishTestRun(launch.getId());

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId());

        ResultTestMethodCardR testCard = testRunResultPage.getCertainTest(test.getName());

        TcmLabelPreview tcmLabelPreview = testCard
                ._findTcmLabel(label.getValue())
                .clickOnTcmLabelAndWaitIntegration();

        verifyTabs.accept(softAssert, getDriver(), tcmLabelPreview);
        verifyDedicatedPageOpening.accept(softAssert, testCaseForSuite_1, getDriver());


        TestDetailsPageR testDetailsPageR = new TestDetailsPageR(getDriver())
                .openPageDirectly(project.getKey(), launch.getId(), test.getId());

        tcmLabelPreview = testDetailsPageR.getTestHeader()
                                          ._findTcmLabel(label.getValue())
                                          .clickOnTcmLabel();

        verifyTabs.accept(softAssert, getDriver(), tcmLabelPreview);
        verifyDedicatedPageOpening.accept(softAssert, testCaseForSuite_1, getDriver());

        softAssert.assertAll();
    }

    TriConsumer<SoftAssert, TestCase, WebDriver> verifyDedicatedPageOpening = (softAssert, testCase, driver) -> {

        DedicatedTestCasePage dedicatedTestCasePage = new DedicatedTestCasePage(getDriver());
        dedicatedTestCasePage.assertPageOpened();

        softAssert.assertEquals(dedicatedTestCasePage.getTestCaseTitle(), testCase.getTitle(),
                "Title is not as expected!");
        softAssert.assertEquals(dedicatedTestCasePage.getTestCaseKeyTextValue(), testCase.getKey(),
                "Test case key is not as expected!");
    };

    TriConsumer<SoftAssert, WebDriver, TcmLabelPreview> verifyTabs = (softAssert, driver, tcmLabelPreview) -> {
        int tabsBeforeClick = PageUtil.getNumberOfOpenedWindows(getDriver());

        tcmLabelPreview.clickCaseTitleLink();

        int tabsAfterClick = PageUtil.getNumberOfOpenedWindows(getDriver());

        softAssert.assertEquals(tabsAfterClick - tabsBeforeClick, 1, "Test case is not opened in new tab!");

        PageUtil.toOtherTab(getDriver());
    };


    @Test
    @TestCaseKey({"ZTP-5283", "ZTP-5757"})
    public void userIsAbleToOpenDedicatedPageWithCmdClickOnKeyAndUserInfoTooltipAppearanceTest() {
        User mainAdmin = usersService.getUserByUsername(MAIN_ADMIN.getUsername());
        TestCase testCase = allTestCases.get(0);

        SoftAssert softAssert = new SoftAssert();

        TestCasesPage testCasesPage = TestCasesPage.openPageDirectly(getDriver(), project.getKey());
        int tabsBeforeClick = PageUtil.getNumberOfOpenedWindows(getDriver());

        testCasesPage
                .findTestCase(testCase.getTitle())
                .holdCmdAndClickTestCaseKey();

        int tabsAfterClick = PageUtil.getNumberOfOpenedWindows(getDriver());

        softAssert.assertEquals(tabsAfterClick - tabsBeforeClick, 1, "Test case is not opened in new tab!");

        PageUtil.toOtherTab(getDriver());

        verifyDedicatedPageOpening.accept(softAssert, testCase, getDriver());

        DedicatedTestCasePage dedicatedTestCasePage = new DedicatedTestCasePage(getDriver());

        ///  Properties tab
        UserInfoTooltip userInfoTooltip = dedicatedTestCasePage
                .getPropertiesTab()
                .hoverAuthorLabel();

        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering Author on  Properties tab");

        userInfoTooltip.verifyUserInfoTooltip(softAssert, mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On Properties tab");

        ///  Executions tab
        ExecutionsTab executionsTab = dedicatedTestCasePage.openExecutionsTab();

        userInfoTooltip = executionsTab
                .getLastExecution()
                .hoverExecutedBy();
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering user on Executions tab");

        userInfoTooltip.verifyUserInfoTooltip(softAssert, mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On Executions tab");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-5118", "ZTP-5119", "ZTP-5120"})
    public void userIsAbleAddDownloadDeleteAttachmentFromDedicatedPageTest() {
        WebDriver webDriver = super.getDriver();

        File file = new File(IMAGES_ZEB_PNG);
        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite_1.getId());

        DedicatedTestCasePage dedicatedTestCasePage = DedicatedTestCasePage.openPageDirectly(webDriver, project.getKey(), testCase.getId());
        AttachmentsTab attachmentsTab = dedicatedTestCasePage.getAttachmentsTab();
        Assert.assertTrue(attachmentsTab.isEmptyPlaceholderPresent(), "Empty placeholder is not present");

        attachmentsTab.hover();
        Assert.assertEquals(
                attachmentsTab.getEmptyPlaceholderColor(), "#f5f5f5",
                "Color of empty placeholder is not as expected after hover!"
        );

        dedicatedTestCasePage.addAttachment(IMAGES_ZEB_PNG);
        AttachmentItem attachmentItem = attachmentsTab.getAttachment(file.getName());
        File act = attachmentItem.getImgFile();

        Assert.assertEquals(act.length(), file.length(), "File length is not as for added file(for sidebar!)");

        attachmentItem.hoverAndDownload();
        URL url = FileUtils.getFileUrl(webDriver, file.getName());

        file = FileUtils.waitFile(url, 30)
                        .orElseThrow(() -> new AssertionError("The file was not downloaded!"));
        Assert.assertEquals(file.length(), file.length(), "File length is not as for added file");

        attachmentItem.delete();
        super.pause(2);

        AttachmentItem attachment = dedicatedTestCasePage.getAttachmentsTab()
                                                         .getOptionalAttachment(IMAGES_ZEB_PNG)
                                                         .orElse(null);
        Assert.assertNull(attachment, "Deleted file shouldn't be present in repository");
    }

    @Test
    @TestCaseKey({"ZTP-5036", "ZTP-5039"})
    public void userIsAbleOpenDedicatedPageFromTrashBinAndFieldsShouldBeReadOnlyPageTest() {
        TestCase testCase = tcmService.createTestCase(project.getId(), testSuite_1.getId());
        tcmService.deleteTestCase(project.getId(), testCase.getId());

        TrashBinPage trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        trashBinPage.findTestCase(testCase.getTitle()).click();

        TrashBinTestCaseRightSideBar trashBinTestCaseRightSideBar = new TrashBinTestCaseRightSideBar(getDriver());

        DedicatedTestCasePage dedicatedTestCasePage = trashBinTestCaseRightSideBar.clickTestCaseKeyAndSwitchTab();
        dedicatedTestCasePage.assertPageOpened();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(dedicatedTestCasePage.getDeletedCaseInfo(), DedicatedTestCasePage.DELETED_CASE_INFO,
                "Deleted case info is not as expected after opening via side bar tab!");

        //ZTP-5039 User is not able to edit test case dedicated page of the deleted test case
        softAssert.assertTrue(dedicatedTestCasePage.getTitleInput().isReadOnly(), "Title input shouldn't be active!");
        softAssert.assertFalse(dedicatedTestCasePage.getSuiteInput().isPresent(2), "Suite field shouldn't present!");
        softAssert.assertTrue(dedicatedTestCasePage.getDescriptionInput()
                                                   .isReadOnly(), "Description field shouldn't be active!");
        softAssert.assertTrue(dedicatedTestCasePage.getPreConditionsInput()
                                                   .isReadOnly(), "Pre-conditions field shouldn't be active!");
        softAssert.assertTrue(dedicatedTestCasePage.getPostConditionsInput()
                                                   .isReadOnly(), "Post-conditions field shouldn't be active!");

        PropertiesTab propertiesTab = dedicatedTestCasePage.getPropertiesTab();

        softAssert.assertTrue(propertiesTab.isPriorityReadOnly(), "Priority field shouldn't be active!");
        softAssert.assertTrue(propertiesTab.isAutomationStateReadOnly(), "Automation state field shouldn't be active!");
        softAssert.assertTrue(propertiesTab.isDeprecatedReadOnly(), "Deprecated field shouldn't be active!");
        softAssert.assertTrue(propertiesTab.isDraftReadOnly(), "Draft field shouldn't be active!");

        trashBinPage = TrashBinPage.openPageDirectly(getDriver(), project.getKey());
        dedicatedTestCasePage = trashBinPage
                .findTestCase(testCase.getTitle())
                .openCaseInNewTab();

        PageUtil.toOtherTab(getDriver());

        softAssert.assertTrue(dedicatedTestCasePage.isPageOpened(), "Deleted case page is not opened when opening in new tab!");
        softAssert.assertEquals(dedicatedTestCasePage.getDeletedCaseInfo(), DedicatedTestCasePage.DELETED_CASE_INFO,
                "Deleted case info is not as expected after opening in new tab!");

        softAssert.assertAll();
    }
}
