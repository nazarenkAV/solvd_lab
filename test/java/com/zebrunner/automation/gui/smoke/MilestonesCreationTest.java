package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Artifact;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.reporting.milestone.DeleteMilestoneModal;
import com.zebrunner.automation.gui.reporting.milestone.MilestoneCard;
import com.zebrunner.automation.gui.reporting.milestone.MilestoneModal;
import com.zebrunner.automation.gui.reporting.launch.AssignToMilestoneModalR;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.milestone.MilestonePage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.util.PageUtil;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Maintainer("obabich")
@Slf4j
public class MilestonesCreationTest extends LogInBase {
    private DateTimeFormatter dtfForAssert = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final String NO_DUE_DATE_TEXT = "No due date";
    private final String NO_START_DATE_TEXT = "No start date";
    private final String EXPECTED_CHECKBOX_TEST = "Completed";
    private final String EXPECTED_ERROR_MESSAGE_UNDER_TITLE = "Name must start with letter or digit";
    private final int MAX_LIMIT_CHARACTERS_IN_TITLE = 32;
    private final int MAX_LIMIT_CHARACTERS_IN_DESCRIPTION = 500;
    private final List<String> ALLOWED_SYMBOLS_IN_TITLE = Arrays.asList(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomNumeric(4), ".", "-", "_");
    private final List<String> ONLY_ALLOWED_CHARACTERS_IN_TITLE = Arrays.asList(".", "-", "_");
    private final List<String> NOT_ALLOWED_SYMBOLS_IN_TITLE = Arrays.asList("#", "@", "(", ")", "*", ">", "!", "'", "`", "~");
    private final List<String> ALLOWED_CHARACTERS_IN_DESCRIPTION = Arrays.asList("описание", "desc", "192", "“", "[", "|", "]", "’", "~", "<", "!", "-", "@", "/", "$", "%", "^", "&", "#", "(", ")", "?", ">", ",", ".", "*", "\\");
    private String milestoneName;
    private Project project;
    private Long launchId;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterMethod
    public void deleteCreatedLaunch() {
        if (!(launchId == null)) {
            testRunService.deleteLaunch(project.getId(), launchId);
            launchId = null;
        }
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey("ZTP-1437")
    public void mainElementsPresenceOnPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(milestonePage.getTitle(), MilestonePage.PAGE_NAME);
        softAssert.assertTrue(milestonePage.isOpenButtonPresent(), "Can't find open button");
        softAssert.assertTrue(milestonePage.isCompleteButtonPresent(), "Can't find complete button");
        softAssert.assertTrue(milestonePage.isAddMilestoneButtonPresent(), "Can't find add milestone button");
        softAssert.assertTrue(milestonePage.isBreadCrumbsMenuPresent(), "Can't find breadcrumbs menu");

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1439"})
    public void addMilestoneModalElementsPresenceTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);

        SoftAssert softAssert = new SoftAssert();
        MilestoneModal milestoneModal = milestonePage.openAddMilestoneModal();

        softAssert.assertEquals(milestoneModal.getModalTitle()
                                              .getText(), MilestoneModal.MODAL_NAME, "Modal name is not as expected!");
        softAssert.assertTrue(milestoneModal.getCancelButton()
                                            .isStateMatches(Condition.VISIBLE), "Can't find close button");
        softAssert.assertTrue(milestoneModal.isMilestoneNameInputPresent(), "Can't find milestone title input");
        softAssert.assertTrue(milestoneModal.isStartDateFieldPresent(), "Can't find start date field");
        softAssert.assertTrue(milestoneModal.isDueDateFieldPresent(), "Can't find due date field");
        softAssert.assertTrue(milestoneModal.isMilestoneDescriptionFieldPresent(), "Can't find description field");
        softAssert.assertTrue(milestoneModal.getSubmitButton()
                                            .isStateMatches(Condition.PRESENT), "Can't find create button");
        softAssert.assertTrue(milestoneModal.isCancelButtonActive(), "Can't find cancel button");
        softAssert.assertFalse(milestoneModal.isDeleteButtonActive(), "Delete button should not be clickable");
        softAssert.assertFalse(milestoneModal.getSubmitButton().isStateMatches(Condition.CLICKABLE),
                "The 'Create' button should not be clickable while the milestone name is missing!");

        milestoneModal.typeMilestoneName("Milestone ".concat(RandomStringUtils.randomAlphabetic(10)));
        softAssert.assertTrue(milestoneModal.getSubmitButton()
                                            .isStateMatches(Condition.CLICKABLE), "Create button should be clickable!");
        milestoneModal.clickCancel();
        softAssert.assertFalse(milestoneModal.isMilestoneNameInputPresent(), "Milestone modal should disappeared after closing!");
        softAssert.assertAll();
    }

    @Test(groups = {"process", "min_acceptance"})
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1495", "ZTP-1482"})
    public void addCompletedMilestoneAndVerifyUnableToAssignItToLaunch() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String milestoneDescription = "Description ".concat(RandomStringUtils.randomAlphabetic(40));
        String launchName = "Launch to verify can't assign completed milestone ".concat(RandomStringUtils.randomAlphabetic(5));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1).plusDays(2);
        launchId = testRunService.startTestRunWithName(project.getKey(), launchName);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        MilestoneModal addWindow = milestonePage.openAddMilestoneModal();

        SoftAssert softAssert = new SoftAssert();
        addWindow.typeMilestoneName(milestoneName);
        softAssert.assertTrue(addWindow.getSubmitButton()
                                       .isStateMatches(Condition.PRESENT), "Can't find create button");

        addWindow.inputStartDate(startDate)
                 .inputDueDate(endDate)
                 .typeDescription(milestoneDescription)
                 .submitModal();
        softAssert.assertTrue(milestonePage.waitIsPopUpMessageAppear(MessageEnum.MILESTONE_CREATED.getDescription(milestoneName)),
                "Expected popup about successful created milestone not found");
        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneCard.getDescription(), milestoneDescription,
                "Milestone created for assign to launch with valid data in description isn't correct on milestone page");
        milestonePage.showOpenMilestones();
        pause(2);

        softAssert.assertEquals(milestoneCard.getTitle(), milestoneName, "Milestone name is not as expected!");
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), dtfForAssert.format(startDate), "Milestone start date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), dtfForAssert.format(endDate), "Milestone due date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDescription(), milestoneDescription, "Milestone description is not as expected!");
        softAssert.assertEquals(milestoneCard.getCheckboxCompleteLabel(), EXPECTED_CHECKBOX_TEST, "Text near checkbox is not as expected!");
        softAssert.assertTrue(milestoneCard.isFlagImgPresent(), "Can't find flag image");
        softAssert.assertTrue(milestoneCard.isCalendarIconPresent(), "Can't find calendar icon");
        softAssert.assertTrue(milestoneCard.isDividerImgPresent(), "Can't find divider between dates");
        softAssert.assertFalse(milestoneCard.isCheckboxActive(), "Checkbox should be unchecked");
        softAssert.assertTrue(milestoneCard.isEditButtonPresent(), "Can't find edit button");

        PaginationR cardsPagination = milestonePage.getPagination();
        softAssert.assertTrue(cardsPagination.isPaginationPresent(), "Pagination isn't present");

        milestoneCard.clickCheckBox();
        softAssert.assertFalse(milestonePage.isMilestonePresent(milestoneName),
                String.format("Milestone with name '%s' shouldn't be present on the page after getting status completed!", milestoneName));
        milestonePage.showCompleteMilestones();
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                String.format("Milestone with name '%s' should be present among completed milestones!", milestoneName));

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project);
        LaunchCard launchCard = automationLaunchesPage.getCertainTestRunCard(launchName, false);
        launchCard.clickMenu().getAssignToMilestone().click();
        AssignToMilestoneModalR assignToMilestoneModalR = new AssignToMilestoneModalR(getDriver());
        softAssert.assertFalse(assignToMilestoneModalR.isMilestoneChooseExists(milestoneName), "Completed milestone is in list!");

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1438", "ZTP-1461", "ZTP-1462", "ZTP-1454", "ZTP-1456"})
    public void createMilestoneWithoutDescriptionAndDeleteIt() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String milestoneIncorrectEndDateName = "Milestone with incorrect end date";
        String milestoneIncorrectStartDateName = "Milestone with incorrect start date";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1).plusDays(2);
        LocalDate incorrectEndDate = LocalDate.now().minusDays(2);
        LocalDate incorrectStartDate = LocalDate.now().plusMonths(1).plusDays(3);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        MilestoneModal milestoneModal = milestonePage.openAddMilestoneModal();

        milestoneModal.inputStartDate(startDate);
        softAssert.assertFalse(milestoneModal.isDayClickableInDueDateCalendar(incorrectEndDate), "Due date can't be earlier than start date");
        PageUtil.guaranteedToHideDropDownList(getDriver());

        milestoneModal.typeMilestoneName(milestoneIncorrectEndDateName).clickCancel();

        milestonePage.openAddMilestoneModal().inputDueDate(endDate);
        softAssert.assertFalse(milestoneModal.isDayClickableInStartDateCalendar(incorrectStartDate),
                "Start date can't be later than due date");
        milestoneModal.typeMilestoneName(milestoneIncorrectStartDateName).clickCancel();

        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneName)
                     .inputStartDate(startDate)
                     .inputDueDate(endDate)
                     .submitModal();

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneCard.getTitle(), milestoneName,
                "Created milestone for test creation without description - name isn't correct");
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), dtfForAssert.format(startDate), "Milestone start date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), dtfForAssert.format(endDate), "Milestone due date is not as expected!");
        softAssert.assertFalse(milestoneCard.isDescriptionPresent(), "Description should not present");
        softAssert.assertFalse(milestoneCard.isCheckboxActive(), "Checkbox should be inactive");
        softAssert.assertTrue(milestoneCard.isDividerImgPresent(), "Can't find divider img");

        milestoneCard.clickThreeDots();
        softAssert.assertTrue(milestoneCard.isDeleteButtonActive(), "'Delete' button should be active!");
        softAssert.assertTrue(milestoneCard.isEditActive(), "'Cancel' button should be active!");
        milestoneCard.getDelete().click();

        DeleteMilestoneModal deleteMilestoneModal = new DeleteMilestoneModal(getDriver());
        softAssert.assertEquals(deleteMilestoneModal.getTitleText(), deleteMilestoneModal.MODAL_NAME, "Modal title is not as expected!");
        deleteMilestoneModal.clickCancel();

        milestoneCard.clickThreeDots();
        milestoneCard.getDelete().click();
        softAssert.assertEquals(deleteMilestoneModal.getTitleText(), deleteMilestoneModal.MODAL_NAME, "Modal title is not as expected!");
        deleteMilestoneModal.clickDelete();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_DELETED.getDescription(milestoneName),
                "Popup message is not as expected!");
        softAssert.assertFalse(milestonePage.isMilestonePresent(milestoneName), "The deleted milestone should not be on the list!");
        softAssert.assertAll();
    }

    @Test()
    @TestCaseKey("ZTP-1453")
    public void createMilestoneWithoutDescriptionAndDueDate() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        LocalDate startDate = LocalDate.now();

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneName)
                     .inputStartDate(startDate)
                     .submitModal();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_CREATED.getDescription(milestoneName),
                "Expected popup about successful created milestone not found");

        milestonePage.showOpenMilestones();
        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneCard.getTitle(), milestoneName, "Created milestone for test creation without description and due date - name isn't correct");
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), dtfForAssert.format(startDate), "Milestone start date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), NO_DUE_DATE_TEXT, "Milestone due date is not as expected!");
        softAssert.assertFalse(milestoneCard.isDescriptionPresent(), "Description should not present");
        softAssert.assertFalse(milestoneCard.isCheckboxActive(), "Checkbox should be inactive");
        softAssert.assertTrue(milestoneCard.isDividerImgPresent(), "Can't find divider img");
        softAssert.assertAll();
    }

    @Test(groups = "process")
    @TestCaseKey("ZTP-1455")
    public void createMilestoneWithoutDescriptionAndStartDate() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        milestoneName = "Aim".concat(RandomStringUtils.randomAlphabetic(10));
        LocalDate endDate = LocalDate.now().plusMonths(1).plusDays(2);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneName)
                     .inputDueDate(endDate)
                     .submitModal();

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneCard.getTitle(), milestoneName, "Created milestone for test creation without description and start date - name isn't correct");
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), NO_START_DATE_TEXT, "Milestone start date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), dtfForAssert.format(endDate), "Milestone due date is not as expected!");
        softAssert.assertFalse(milestoneCard.isDescriptionPresent(), "Description should not present");
        softAssert.assertFalse(milestoneCard.isCheckboxActive(), "Checkbox should be inactive");
        softAssert.assertTrue(milestoneCard.isDividerImgPresent(), "Can't find divider img");
        softAssert.assertAll();
    }

    @Test(groups = "process")
    @TestCaseKey("ZTP-1452")
    public void createMilestoneWithoutDescriptionAndDates() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        milestoneName = "Aim".concat(RandomStringUtils.randomAlphabetic(10));

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneName)
                     .submitModal();
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Milestone without description and dates isn't in the list");

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), NO_START_DATE_TEXT, "Milestone start date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), NO_DUE_DATE_TEXT, "Milestone due date is not as expected!");
        softAssert.assertFalse(milestoneCard.isDescriptionPresent(), "Description should not present");
        softAssert.assertFalse(milestoneCard.isCheckboxActive(), "Checkbox should be inactive");
        softAssert.assertTrue(milestoneCard.isDividerImgPresent(), "Can't find divider img");
        softAssert.assertAll();
    }

    @Test(groups = {"process", "min_acceptance"})
    @TestCaseKey({"ZTP-794", "ZTP-1493", "ZTP-1494", "ZTP-3779"})
    public void createMilestoneWithoutDateAssignToLaunchAndVerifyOnlyOneMilestoneCanBeAssignedToLaunch() {
        WebDriver webDriver = super.getDriver();

        milestoneName = "Milestone " + RandomStringUtils.randomAlphabetic(10);
        String milestoneNameSecond = "Milestone " + RandomStringUtils.randomAlphabetic(10);
        String milestoneDescription = "Description " + RandomStringUtils.randomAlphabetic(40);

        String launchName = "Launch to check milestone attachment " + RandomStringUtils.randomAlphabetic(5);
        launchId = testRunService.startTestRunWithName(project.getKey(), launchName);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(webDriver, project);

        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneName)
                     .typeDescription(milestoneDescription)
                     .submitModal();

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        Assert.assertEquals(
                milestoneCard.getTitle(), milestoneName,
                "First milestone created for test creating milestone without dates and assign only to one launch - name isn't correct"
        );
        Assert.assertEquals(
                milestoneCard.getDescription(), milestoneDescription,
                "First milestone created for test creating milestone without dates and assign only to one launch - description isn't correct"
        );

        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneNameSecond)
                     .submitModal();
        Assert.assertEquals(
                milestonePage.getCertainMilestoneCard(milestoneNameSecond).getTitle(), milestoneNameSecond,
                "Second milestone created for test creating milestone without dates and assign only to one launch - name isn't correct"
        );

        Assert.assertEquals(milestoneCard.getStartDateInfo(), NO_START_DATE_TEXT, "Milestone start date is not as expected!");
        Assert.assertEquals(milestoneCard.getDueDateInfo(), NO_DUE_DATE_TEXT, "Milestone due date is not as expected!");
        Assert.assertEquals(milestoneCard.getDescription(), milestoneDescription, "Milestone description is not as expected!");
        Assert.assertFalse(milestoneCard.isCheckboxActive(), "Checkbox should be inactive");
        Assert.assertTrue(milestoneCard.isDividerImgPresent(), "Can't find divider img");

        AutomationLaunchesPage launchesPage = AutomationLaunchesPage.openPageDirectly(webDriver, project);
        LaunchCard launchCard = launchesPage.getCertainTestRunCard(launchName, false);
        launchCard.assignMilestone(milestoneName);
        Assert.assertEquals(launchCard.getMilestone().getText(), milestoneName, "Milestone name is not as expected!");

        launchCard.assignMilestone(milestoneNameSecond);
        Assert.assertEquals(launchCard.getMilestone().getText(), milestoneNameSecond, "Milestone name is not as expected!");
    }

    @Test
    @TestCaseKey({"ZTP-1440", "ZTP-1441", "ZTP-1442", "ZTP-1443", "ZTP-1444"})
    public void checkMilestoneTitleWithManyVariantsTest() {
        WebDriver webDriver = super.getDriver();

        milestoneName = RandomStringUtils.randomAlphabetic(2);
        String milestoneNameSecond = RandomStringUtils.randomAlphabetic(new Random().nextInt(29) + 3);
        String milestoneNameThird = RandomStringUtils.randomAlphabetic(32);
        String milestoneNameWithOneCharacter = RandomStringUtils.randomAlphabetic(1);
        String milestoneNameWithMoreThanThirtyTwoCharacters = "Milestone ".concat(RandomStringUtils.randomAlphabetic(33));

        MilestonePage milestonePage = MilestonePage.openPageDirectly(webDriver, project);
        MilestoneModal milestoneModal = milestonePage.openAddMilestoneModal();
        Assert.assertFalse(milestoneModal.isSaveButtonActive(), "Save button shouldn't be active with empty title");

        milestoneModal.typeMilestoneName(milestoneNameWithOneCharacter);
        Assert.assertFalse(milestoneModal.isSaveButtonActive(), "Save button shouldn't be active with title with one character");

        milestoneModal.typeMilestoneName(milestoneNameWithMoreThanThirtyTwoCharacters);
        int lengthEnteredMilestoneName = milestoneModal.getEnteredTitleText().length();
        Assert.assertEquals(
                lengthEnteredMilestoneName, MAX_LIMIT_CHARACTERS_IN_TITLE,
                "Save button shouldn't be active with title with more than thirty two characters"
        );

        milestoneModal.clickCancel();
        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneName)
                     .submitModal();
        Assert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone should be in the list");

        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneNameSecond)
                     .submitModal();
        Assert.assertTrue(milestonePage.isMilestonePresent(milestoneNameSecond), "Created second milestone should be in the list");

        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneNameThird)
                     .submitModal();
        Assert.assertTrue(milestonePage.isMilestonePresent(milestoneNameThird), "Created third milestone should be in the list");

        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(milestoneNameThird);
        Assert.assertEquals(
                milestoneModal.getErrorMessageText(), "Name already in use",
                "Error message doesn't belong to title error"
        );
        Assert.assertTrue(
                milestoneModal.isErrorMessagePresent(),
                "Error message should be appear when input duplicated name for milestone"
        );
    }

    @Test
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1448", "ZTP-1449", "ZTP-1450", "ZTP-1451"})
    public void checkMilestoneDescriptionForLengthAndAllowedCharactersTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String milestoneDescription = "Description ".concat(RandomStringUtils.randomAlphabetic(489));

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        MilestoneModal milestoneModal = milestonePage.openAddMilestoneModal();

        milestoneModal.typeMilestoneName(milestoneName)
                      .typeDescription(milestoneDescription);

        String enteredDescription = milestoneModal.getEnteredDescriptionText();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(milestoneModal.isErrorMessagePresent(),
                "Error message is present with valid data in description");
        milestoneModal.submitModal();

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                "Milestone with less than five hundred characters in description should be the list");
        softAssert.assertEquals(milestoneCard.getDescription(), enteredDescription,
                String.format("Description in milestone with name '%s' isn't expected", milestoneName));

        milestoneModal = milestonePage.openAddMilestoneModal();
        milestoneName = "Milestone with empty desc ".concat(RandomStringUtils.randomAlphabetic(5));

        milestoneModal.typeMilestoneName(milestoneName);
        enteredDescription = milestoneModal.getEnteredDescriptionText();
        softAssert.assertTrue(enteredDescription.isEmpty(),
                "Description is present on modal window in the milestone where description should be empty");
        milestoneModal.submitModal();
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                "Milestone with empty description isn't in the list");

        milestoneModal = milestonePage.openAddMilestoneModal();
        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        milestoneModal.typeMilestoneName(milestoneName)
                      .typeDescription(milestoneDescription);

        int enteredDescriptionLength = milestoneModal.getEnteredDescriptionText().length();
        softAssert.assertEquals(enteredDescriptionLength, MAX_LIMIT_CHARACTERS_IN_DESCRIPTION,
                "Entered description length is more than five hundred");

        milestoneModal.submitModal();
        softAssert.assertEquals(milestoneCard.getDescription().length(), MAX_LIMIT_CHARACTERS_IN_DESCRIPTION,
                "Description length after submitting modal isn' 500 length");

        milestoneDescription = String.join("", ALLOWED_CHARACTERS_IN_DESCRIPTION);
        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));

        milestoneModal = milestonePage.openAddMilestoneModal();
        milestoneModal.typeMilestoneName(milestoneName)
                      .typeDescription(milestoneDescription)
                      .submitModal();

        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                "Milestone with valid characters in description isn't in the list");

        MilestoneCard milestoneCardWithSpecialCharactersInDescription = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneCardWithSpecialCharactersInDescription.getDescription(), milestoneDescription,
                "Description with special characters in milestone isn't expected");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1445", "ZTP-1446", "ZTP-1447"})
    public void checkAllowedCharactersInMilestoneTitleTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.CREATION);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        MilestoneModal milestoneModal;
        String allowedSymbolsString = String.join("", ALLOWED_SYMBOLS_IN_TITLE)
                                            .concat(RandomStringUtils.randomAlphabetic(3));

        milestoneModal = milestonePage.openAddMilestoneModal();
        milestoneModal.typeMilestoneName(allowedSymbolsString);

        softAssert.assertTrue(milestoneModal.isSaveButtonActive(), "Save button inactive with valid name with symbols in name");
        softAssert.assertFalse(milestoneModal.isErrorMessagePresent(), "Error message present under title with valid symbols");

        milestoneModal.submitModal();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_CREATED.getDescription(allowedSymbolsString),
                "Popup message is not as expected!");
        softAssert.assertTrue(milestonePage.isMilestonePresent(allowedSymbolsString),
                "Milestone with valid characters in title isn't in the list"); // ZTP-1445 Verify user can enter Latin letters, numerals and some special chars to 'Title' field

        for (String symbol : NOT_ALLOWED_SYMBOLS_IN_TITLE) {
            milestoneModal = milestonePage.openAddMilestoneModal();

            milestoneName = RandomStringUtils.randomAlphabetic(5).concat(" ").concat(symbol);
            milestoneModal.typeMilestoneName(milestoneName);

            softAssert.assertFalse(milestoneModal.isSaveButtonActive(),
                    String.format("Save button active with invalid name with symbol '%s'", symbol));
            softAssert.assertEquals(milestoneModal.getErrorMessage()
                                                  .getText(), MessageEnum.MILESTONE_ERROR_MESSAGE_INVALID_CHARACTERS_IN_TITLE.getDescription(),
                    String.format("Error message isn't present under title with symbol '%s'", symbol));

            milestoneModal.clickCancel();
        } // ZTP-1446 Verify user can't use most special chars in 'Title' field

        milestoneModal = milestonePage.openAddMilestoneModal();
        String milestoneName = String.join("", ONLY_ALLOWED_CHARACTERS_IN_TITLE);

        milestoneModal.typeMilestoneName(milestoneName);
        softAssert.assertEquals(milestoneModal.getErrorMessageText(), EXPECTED_ERROR_MESSAGE_UNDER_TITLE,
                "Error message regarding special characters in title isn't present");
        softAssert.assertFalse(milestoneModal.isSaveButtonActive(), "Save button active with only special characters in title"); // ZTP-1447 Verify user can't enter only special chars to 'Title' field

        softAssert.assertAll();
    }

}