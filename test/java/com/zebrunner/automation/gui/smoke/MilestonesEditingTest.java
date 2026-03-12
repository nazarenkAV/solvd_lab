package com.zebrunner.automation.gui.smoke;

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
import java.util.NoSuchElementException;
import java.util.Random;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.gui.reporting.milestone.MilestoneCard;
import com.zebrunner.automation.gui.reporting.milestone.MilestoneModal;
import com.zebrunner.automation.gui.reporting.milestone.MilestonePage;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.automation.legacy.TestLabelsConstant;

@Maintainer("akhivyk")
public class MilestonesEditingTest extends LogInBase {
    private DateTimeFormatter dtfForAssert = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final String NO_DUE_DATE_TEXT = "No due date";
    private final String NO_START_DATE_TEXT = "No start date";
    private final int MAX_LIMIT_CHARACTERS_IN_TITLE = 32;
    private final int MAX_LIMIT_CHARACTERS_IN_DESCRIPTION = 500;
    private final List<String> ALLOWED_SYMBOLS_IN_TITLE = Arrays.asList(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomNumeric(4), ".", "-", "_");
    private final List<String> NOT_ALLOWED_SYMBOLS_IN_TITLE = Arrays.asList("#", "@", "(", ")", "*", ">", "!", "'", "`", "~");
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

    @Test(groups = {"process", "min_acceptance"})
    @TestCaseKey({"ZTP-1459", "ZTP-1460"})
    public void editMilestoneTest() throws NoSuchElementException {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.EDITING);

        String title = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String milestoneDescription = "Description ".concat(RandomStringUtils.randomAlphabetic(40));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1).plusDays(2);

        milestoneName = "Edited milestone ".concat(RandomStringUtils.randomAlphabetic(5));
        String milestoneDescriptionEdited = "Edited Description ".concat(RandomStringUtils.randomAlphabetic(40));
        LocalDate startDateEdited = LocalDate.now().minusDays(14);
        LocalDate endDateEdited = LocalDate.now().plusMonths(2).minusDays(8);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        milestonePage.openAddMilestoneModal()
                     .typeMilestoneName(title)
                     .inputStartDate(startDate)
                     .inputDueDate(endDate)
                     .typeDescription(milestoneDescription)
                     .submitModal();
        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(title);
        softAssert.assertEquals(milestoneCard.getTitle(), title,
                "Milestone created for edit test - name isn't correct on milestone page");
        softAssert.assertEquals(milestoneCard.getDescription(), milestoneDescription,
                "Milestone created for edit test - description isn't correct on milestone page");

        milestonePage.showOpenMilestones();
        pause(2);
        milestoneCard.edit().typeMilestoneName(milestoneName).clickCancel();
        softAssert.assertNotEquals(milestoneCard.getTitle(), milestoneName,
                "New milestone name shouldn't appear because editing canceled");

        milestoneCard.edit()
                     .typeMilestoneName(milestoneName)
                     .inputStartDate(startDateEdited)
                     .inputDueDate(endDateEdited)
                     .typeDescription(milestoneDescriptionEdited)
                     .submitModal();
        MilestoneCard milestoneEditedCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(milestoneEditedCard.getDescription(), milestoneDescriptionEdited,
                "After editing milestone, description isn't correct on milestone page");

        softAssert.assertFalse(milestonePage.isMilestonePresent(title),
                String.format("Milestone with old name '%s' shouldn't be in the milestone list!", title));
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                String.format("Milestone with edited name '%s' should be in the milestone list!", milestoneName));
        MilestoneCard editedCard = milestonePage.getCertainMilestoneCard(milestoneName);
        softAssert.assertEquals(editedCard.getStartDateInfo(), dtfForAssert.format(startDateEdited), "Milestone start date is not as expected!");
        softAssert.assertEquals(editedCard.getDueDateInfo(), dtfForAssert.format(endDateEdited), "Milestone start date is not as expected!");
        softAssert.assertEquals(editedCard.getDescription(), milestoneDescriptionEdited, "Milestone description is not as expected!");
        softAssert.assertFalse(editedCard.isCheckboxActive(), "Checkbox should be inactive");
        softAssert.assertTrue(editedCard.isDividerImgPresent(), "Can't find divider img");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1463", "ZTP-1464"})
    public void editMilestoneAndCheckTitleInputsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.EDITING);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String secondMilestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(12));

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();

        milestonePage.createMilestoneOnlyWithTitle(milestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone for editing isn't in the list");

        milestonePage.createMilestoneOnlyWithTitle(secondMilestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(secondMilestoneName),
                "Created milestone for checking already exists title isn't in the list");

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        MilestoneModal milestoneModal = milestoneCard.edit();

        milestoneModal.typeMilestoneName("");
        softAssert.assertFalse(milestoneModal.isSaveButtonActive(), "Milestone can be saved after editing with empty title");

        milestoneModal.typeMilestoneName(secondMilestoneName);
        softAssert.assertEquals(milestoneModal.getErrorMessageText(), MessageEnum.MILESTONE_ERROR_MESSAGE_IN_TITLE_NAME_ALREADY_IN_USE.getDescription(),
                "Error message isn't equals to expected");

        milestoneModal.typeMilestoneName(secondMilestoneName.toUpperCase());
        softAssert.assertEquals(milestoneModal.getErrorMessageText(), MessageEnum.MILESTONE_ERROR_MESSAGE_IN_TITLE_NAME_ALREADY_IN_USE.getDescription(),
                "Error message isn't equals to expected after inputted the same title but in upper case");

        secondMilestoneName = StringUtil.randomChangeLetterCase(secondMilestoneName);
        milestoneModal.typeMilestoneName(secondMilestoneName);

        softAssert.assertEquals(milestoneModal.getErrorMessageText(), MessageEnum.MILESTONE_ERROR_MESSAGE_IN_TITLE_NAME_ALREADY_IN_USE.getDescription(),
                "Error message isn't equals to expected after inputted the same title but in different letter case");
        softAssert.assertFalse(milestoneModal.isSaveButtonActive(), "Save button active with already existing milestone title");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-1465", "ZTP-1466", "ZTP-1467"})
    public void checkMilestoneTitleLimitsWhileEditingTest() {
        WebDriver webDriver = super.getDriver();

        milestoneName = "Milestone " + RandomStringUtils.randomAlphabetic(10);
        String milestoneNameWithTwoCharacters = RandomStringUtils.randomAlphabetic(2);
        String milestoneNameWithValidNumberCharacters = RandomStringUtils.randomAlphabetic(new Random().nextInt(29) + 3);
        String milestoneNameWithThirtyTwoCharacters = RandomStringUtils.randomAlphabetic(32);
        String milestoneNameWithOneCharacter = RandomStringUtils.randomAlphabetic(1);
        String milestoneNameWithMoreThanThirtyTwoCharacters = RandomStringUtils.randomAlphabetic(33);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(webDriver, project);

        milestonePage.createMilestoneOnlyWithTitle(milestoneName);
        Assert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone for editing isn't in the list");

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        MilestoneModal milestoneModal = milestoneCard.edit();

        milestoneModal.typeMilestoneName(milestoneNameWithTwoCharacters).submitModal();
        milestoneCard = milestonePage.getCertainMilestoneCard(milestoneNameWithTwoCharacters);
        Assert.assertEquals(
                milestoneCard.getTitle().length(), milestoneNameWithTwoCharacters.length(),
                "Expected length - 2, but found another count"
        );

        milestoneModal = milestoneCard.edit();
        milestoneModal.typeMilestoneName(milestoneNameWithValidNumberCharacters)
                      .submitModal();

        milestoneCard = milestonePage.getCertainMilestoneCard(milestoneNameWithValidNumberCharacters);
        Assert.assertEquals(
                milestoneCard.getTitle().length(), milestoneNameWithValidNumberCharacters.length(),
                "Expected length isn't equals to current displayed"
        );

        milestoneModal = milestoneCard.edit();
        milestoneModal.typeMilestoneName(milestoneNameWithThirtyTwoCharacters)
                      .submitModal();

        milestoneCard = milestonePage.getCertainMilestoneCard(milestoneNameWithThirtyTwoCharacters);
        Assert.assertEquals(
                milestoneCard.getTitle().length(), milestoneNameWithThirtyTwoCharacters.length(),
                "Expected length - 32, but found another count"
        );

        milestoneModal = milestoneCard.edit();
        milestoneModal.typeMilestoneName(milestoneNameWithOneCharacter);
        Assert.assertFalse(milestoneModal.isSaveButtonActive(), "Save button active with 1 character in title");

        milestoneModal.typeMilestoneName(milestoneNameWithMoreThanThirtyTwoCharacters);

        String enteredMilestoneName = milestoneModal.getEnteredTitleText();
        Assert.assertEquals(
                milestoneModal.getEnteredTitleText().length(), MAX_LIMIT_CHARACTERS_IN_TITLE,
                "Save button shouldn't be active with title with more than thirty two characters"
        );

        milestoneModal.submitModal();
        milestoneCard = milestonePage.getCertainMilestoneCard(enteredMilestoneName);

        Assert.assertEquals(
                milestoneCard.getTitle().length(), MAX_LIMIT_CHARACTERS_IN_TITLE,
                "Incorrect count of characters in title after saving milestone"
        );
    }

    @Test
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1468", "ZTP-1469"})
    public void testAllowedCharactersWhileEditingTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.EDITING);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        MilestoneModal milestoneModal;
        MilestoneCard milestoneCard;
        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));

        milestonePage.createMilestoneOnlyWithTitle(milestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone for editing isn't in the list");

        String allowedSymbolsString = String.join("", ALLOWED_SYMBOLS_IN_TITLE) + " " + RandomStringUtils.randomAlphabetic(3);

        milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        milestoneModal = milestoneCard.edit();
        milestoneModal.typeMilestoneName(allowedSymbolsString);

        softAssert.assertTrue(milestoneModal.isSaveButtonActive(), "Save button inactive with valid name with symbols in name");
        softAssert.assertFalse(milestoneModal.isErrorMessagePresent(), "Error message present under title with valid symbols");

        milestoneModal.submitModal();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_UPDATED.getDescription(allowedSymbolsString),
                "Popup message is not as expected!");
        softAssert.assertTrue(milestonePage.isMilestonePresent(allowedSymbolsString),
                "Milestone with valid characters in title isn't in the list"); // ZTP-1445 Verify user can enter Latin letters, numerals and some special chars to 'Title' field

        milestoneCard = milestonePage.getCertainMilestoneCard(allowedSymbolsString);
        for (String symbol : NOT_ALLOWED_SYMBOLS_IN_TITLE) {
            milestoneModal = milestoneCard.edit();

            milestoneName = RandomStringUtils.randomAlphabetic(5).concat(" ").concat(symbol);
            milestoneModal.typeMilestoneName(milestoneName);

            softAssert.assertFalse(milestoneModal.isSaveButtonActive(),
                    String.format("Save button active with invalid name with symbol '%s'", symbol));
            softAssert.assertEquals(milestoneModal.getErrorMessage()
                                                  .getText(), MessageEnum.MILESTONE_ERROR_MESSAGE_INVALID_CHARACTERS_IN_TITLE.getDescription(),
                    String.format("Error message isn't present under title with symbol '%s'", symbol));

            milestoneModal.clickCancel();
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1470", "ZTP-1471", "ZTP-1472"})
    public void testDescriptionWhileEditingMilestone() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.EDITING);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String milestoneDescription = "Description ".concat(RandomStringUtils.randomAlphabetic(30));

        milestonePage.createMilestoneWithTitleAndDescription(milestoneName, milestoneDescription);
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone for editing isn't in the list");

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        MilestoneModal milestoneModal = milestoneCard.edit();

        milestoneModal.typeDescription("");

        softAssert.assertTrue(milestoneModal.getEnteredDescriptionText().isEmpty(), "Description isn't empty");
        softAssert.assertFalse(milestoneModal.isErrorMessagePresent(), "Error message present under empty description");
        softAssert.assertTrue(milestoneModal.isSaveButtonActive(), "Save button inactive with empty description"); // ZTP-1470 Verify 'Description' field can be left empty
        milestoneModal.submitModal();

        milestoneModal = milestoneCard.edit();
        milestoneDescription = RandomStringUtils.randomAlphabetic(500);

        milestoneModal.typeDescription(milestoneDescription);
        softAssert.assertFalse(milestoneModal.isErrorMessagePresent(), "Error message present under valid data in description");
        softAssert.assertTrue(milestoneModal.isSaveButtonActive(), "Save button inactive with valid data in description"); // ZTP-1471 Verify user can enter up to 500 characters to 'Description field'

        milestoneModal.submitModal();
        softAssert.assertEquals(milestoneCard.getDescription(), milestoneDescription,
                "After saving edited milestone, description isn't expected");

        milestoneModal = milestoneCard.edit();
        milestoneDescription = RandomStringUtils.randomAlphabetic(501);

        milestoneModal.typeDescription(milestoneDescription);
        int enteredLengthDescription = milestoneModal.getEnteredDescriptionText().length();
        softAssert.assertEquals(enteredLengthDescription, MAX_LIMIT_CHARACTERS_IN_DESCRIPTION,
                "Length of entered description is more than 500");

        milestoneModal.submitModal();
        softAssert.assertEquals(milestoneCard.getDescription().length(), MAX_LIMIT_CHARACTERS_IN_DESCRIPTION,
                "After saving edited milestone description length is more than 500"); // ZTP-1472 Verify user can't enter >500 characters to 'Description' field

        softAssert.assertAll();
    }

    @Test
    @Maintainer("akhivyk")
    @TestCaseKey({"ZTP-1473", "ZTP-1474", "ZTP-1475", "ZTP-1476", "ZTP-1477"})
    public void testDatesInMilestoneWhileEditing() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);
        Label.attachToTest(TestLabelsConstant.MILESTONES, TestLabelsConstant.EDITING);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        SoftAssert softAssert = new SoftAssert();
        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1).plusDays(2);
        LocalDate incorrectStartDate = endDate.plusDays(5);
        LocalDate incorrectEndDate = startDate.minusDays(2);

        milestonePage.createMilestoneWithTitleAndDates(milestoneName, startDate, endDate);
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone for editing isn't in the list");

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        MilestoneModal milestoneModal = milestoneCard.edit();

        milestoneModal.clickClearStartDate();
        milestoneModal.clickClearDueDate();

        milestoneModal.submitModal();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_UPDATED.getDescription(milestoneName),
                "Popup message after clean due and start dates is not as expected!");
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), NO_START_DATE_TEXT, "Start date after editing isn't empty");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), NO_DUE_DATE_TEXT, "Due date after editing isn't empty"); // ZTP-1473 Verify 'Start/Due date' fields can be left empty

        milestoneModal = milestoneCard.edit();
        milestoneModal.inputStartDate(startDate);

        milestoneModal.submitModal();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_UPDATED.getDescription(milestoneName),
                "Popup message after updating start date is not as expected!");
        softAssert.assertEquals(milestoneCard.getStartDateInfo(), dtfForAssert.format(startDate), "Milestone start date is not as expected!"); // ZTP-1474 Verify user can select 'Start date'

        milestoneModal = milestoneCard.edit();
        milestoneModal.inputDueDate(endDate);

        milestoneModal.submitModal();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_UPDATED.getDescription(milestoneName),
                "Popup message after updating due date is not as expected!");
        softAssert.assertEquals(milestoneCard.getDueDateInfo(), dtfForAssert.format(endDate), "Milestone end date is not as expected!"); // ZTP-1476 Verify user can select 'Due date'

        milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        milestoneModal = milestoneCard.edit();
        softAssert.assertFalse(milestoneModal.isDayClickableInStartDateCalendar(incorrectStartDate),
                "Start date can be later than due date"); // ZTP-1475 Verify 'Start date' can't be later than 'Due date'
        milestoneModal.clickCancel();

        milestoneModal = milestoneCard.edit();
        softAssert.assertFalse(milestoneModal.isDayClickableInDueDateCalendar(incorrectEndDate),
                "End date can be earlier than start date"); // ZTP-1477 Verify 'Due date' can't be earlier than 'Start date'
        milestoneModal.clickDueDateInput();
        milestoneModal.clickCancel();

        softAssert.assertAll();

    }

}