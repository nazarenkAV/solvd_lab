package com.zebrunner.automation.gui.smoke;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.BreadcrumbsEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.reporting.milestone.MilestoneCard;
import com.zebrunner.automation.gui.reporting.milestone.MilestonePage;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.project.ProjectsPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.Milestone;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Maintainer("akhivyk")
public class MilestonesGrid extends LogInBase {
    private final List<Milestone> CREATED_MILESTONES = new ArrayList<>();
    String milestoneName;
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

    @AfterMethod(onlyForGroups = "milestones-created")
    public void deleteCreatedMilestones() {
        CREATED_MILESTONES.forEach(milestone -> super.apiHelperService.deleteMilestone(project.getId(), milestone.getId()));
        CREATED_MILESTONES.clear();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1823", "ZTP-1480", "ZTP-1486", "ZTP-1487"})
    public void testUserCanNavigateToMilestonePageUsingMenuAndBackToProjectGridViaBreadcrumb() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        SoftAssert softAssert = new SoftAssert();
        projectsPage.toCertainProject(project.getKey());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.getPageInstance(getDriver());
        softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Launches page isn't opened");

        automationLaunchesPage.clickBreadcrumb(BreadcrumbsEnum.LAUNCHES.getBreadcrumb());
        softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Launches page isn't opened after switching via breadcrumb"); // ZTP-1487 Verify user can return to test run grid of the project via breadcrumbs

        MilestonePage milestonePage = automationLaunchesPage.getNavigationMenu().toMilestonePage();
        softAssert.assertTrue(milestonePage.isPageOpened(), "Milestone page isn't opened"); // ZTP-1480 Verify user can open project's milestones via sidebar

        milestonePage.clickBreadcrumb(BreadcrumbsEnum.PROJECTS.getBreadcrumb());
        softAssert.assertTrue(projectsPage.isPageOpened(), "Project page isn't opened"); // ZTP-1486 Verify user can return to project grid via breadcrumbs

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1483", "ZTP-1485"})
    public void testUserCanMarkMilestoneAsCompletedAndBack() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        SoftAssert softAssert = new SoftAssert();

        ProjectsPage projectsPage = ProjectsPage.openPageDirectly(getDriver());
        projectsPage.toCertainProject(project.getKey());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.getPageInstance(getDriver());
        softAssert.assertTrue(automationLaunchesPage.isPageOpened(), "Launches page isn't opened");

        MilestonePage milestonePage = automationLaunchesPage.getNavigationMenu().toMilestonePage();
        milestonePage.openAddMilestoneModal()
                .typeMilestoneName(milestoneName)
                .submitModal();
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "Created milestone isn't in the list");

        softAssert.assertTrue(milestonePage.isFilteringSelectedByOpenMilestones(),
                "Filtering by 'open' isn't preselected");
        softAssert.assertTrue(milestonePage.isPresentedListIncludesOnlyOpenedMilestones(),
                "Select filtering by 'open' but not only opened milestones in the list"); // ZTP-1485 Verify filtering by 'Open' is pre-selected when opening the grid

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneName);
        milestoneCard.clickCheckBox();
        softAssert.assertFalse(milestonePage.isMilestonePresent(milestoneName),
                "Milestone marked as completed present in opened milestone list");

        milestonePage.showCompleteMilestones();
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                "Milestone marked as completed isn't present in completed milestone list"); // ZTP-1483 Verify user can uncheck a milestone as 'Completed'

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1481", "ZTP-1484"})
    public void testUserCanApplyFilteringByOpenAndCompleted() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        milestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(10));
        String completedMilestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(5));
        String openedMilestoneName = "Milestone ".concat(RandomStringUtils.randomAlphabetic(12));
        SoftAssert softAssert = new SoftAssert();
        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);

        milestonePage.createMilestoneOnlyWithTitle(milestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName), "First created milestone isn't in the list");

        milestonePage.createMilestoneOnlyWithTitle(completedMilestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(completedMilestoneName), "Second created milestone isn't in the list");

        milestonePage.createMilestoneOnlyWithTitle(openedMilestoneName);
        softAssert.assertTrue(milestonePage.isMilestonePresent(openedMilestoneName), "Third created milestone isn't in the list");

        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(completedMilestoneName);
        milestoneCard.clickCheckBox();
        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_UPDATED.getDescription(completedMilestoneName),
                "Popup isn't equals to expected");

        milestonePage.showOpenMilestones();
        softAssert.assertTrue(milestonePage.isMilestonePresent(milestoneName),
                "First milestone created and marked as opened isn't present in milestone list sorted by 'open'");
        softAssert.assertTrue(milestonePage.isMilestonePresent(openedMilestoneName),
                "Third milestone created and marked as opened isn't present in milestone list sorted by 'open'");

        List<MilestoneCard> presentedMilestonesWithOpenFiltering = milestonePage.getMilestoneCards();

        presentedMilestonesWithOpenFiltering.forEach(card -> {
            softAssert.assertFalse(card.isCheckboxActive(), "Selected filtering by 'open' but not only opened milestones in the list"); // ZTP-1481 Verify user can apply filtering by 'Open'
        });

        milestonePage.showCompleteMilestones();
        softAssert.assertTrue(milestonePage.isMilestonePresent(completedMilestoneName),
                "Second milestone created and marked as completed isn't present in milestone list sorted by 'completed'");

        List<MilestoneCard> presentedMilestonesWithCompletedFiltering = milestonePage.getMilestoneCards();

        presentedMilestonesWithCompletedFiltering.forEach(card -> {
            softAssert.assertTrue(card.isCheckboxActive(), "Selected filtering by 'completed' but not only completed milestones in the list"); // ZTP-1484 Verify user can apply filtering by 'Completed'
        });

        softAssert.assertAll();
    }

    @Test(groups = "milestones-created")
    @TestCaseKey({"ZTP-1489", "ZTP-1490"})
    public void sortingMilestonesOnlyWithDueDateOrWithoutDates() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        int numbersOfMilestonesToGenerate = 3;
        List<Milestone> createdMilestonesWithDueDate = generateMilestones(numbersOfMilestonesToGenerate, true);
        CREATED_MILESTONES.addAll(createdMilestonesWithDueDate);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(milestonePage.isPresentedMilestonesWithDueDateSortedByAsc(),
                "Presented milestones isn't sorted by asc"); // ZTP-1489 Verify default sorting of the grid is by 'Due date' column (ASC)

        createdMilestonesWithDueDate.forEach(a -> super.apiHelperService.deleteMilestone(project.getId(), a.getId()));

        List<Milestone> createdMilestoneWithoutDates = generateMilestones(numbersOfMilestonesToGenerate, false);
        CREATED_MILESTONES.addAll(createdMilestoneWithoutDates);

        MilestonePage milestonePageUpdate = MilestonePage.openPageDirectly(getDriver(), project);
        Collections.reverse(createdMilestoneWithoutDates);

        softAssert.assertTrue(milestonePageUpdate.isMilestoneCardsWithoutDatesSortedByCreationOrder(createdMilestoneWithoutDates),
                "Expected order of created milestones via api isn't equals to displayed on page"); // ZTP-1490 Verify if milestone's 'Due date' isn't specified, the sorting is by creation date (DESC)

        createdMilestoneWithoutDates.forEach(a -> super.apiHelperService.deleteMilestone(project.getId(), a.getId()));

        softAssert.assertAll();
    }

    @Test(groups = "milestones-created")
    @TestCaseKey({"ZTP-1491", "ZTP-1492"})
    public void verifySortingMilestonesRestructuredAfterEditingDueDate() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        int numbersOfMilestonesToGenerate = 3;
        List<Milestone> createdMilestonesWithDueDate = generateMilestones(numbersOfMilestonesToGenerate, true);
        CREATED_MILESTONES.addAll(createdMilestonesWithDueDate);
        SoftAssert softAssert = new SoftAssert();

        List<Milestone> createdMilestoneWithoutDates = generateMilestones(numbersOfMilestonesToGenerate, false);
        Collections.reverse(createdMilestoneWithoutDates);
        CREATED_MILESTONES.addAll(createdMilestoneWithoutDates);

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);

        softAssert.assertTrue(milestonePage.isPresentedMilestonesWithDueDateSortedByAsc(),
                "Presented milestones with due date isn't sorted by asc");
        softAssert.assertTrue(milestonePage.isMilestoneCardsWithoutDatesSortedByCreationOrder(createdMilestoneWithoutDates),
                "Expected order of created milestones without dates via api isn't equals to displayed on page");

        String milestoneNameForEditingDueDate = findMilestoneWithLatestDueDate(createdMilestonesWithDueDate).getName();
        MilestoneCard milestoneCard = milestonePage.getCertainMilestoneCard(milestoneNameForEditingDueDate);
        int indexOfMilestoneBeforeEditingDueDate = milestonePage.getIndexOfMilestoneOnPage(milestoneNameForEditingDueDate);

        LocalDate newDueDate = milestoneCard.getDueDateInLocalDateFormat()
                .minusYears(6);

        milestoneCard.edit()
                .inputDueDate(newDueDate)
                .typeDescription("edited milestone")
                .submitModal();

        softAssert.assertEquals(milestonePage.getPopUp(), MessageEnum.MILESTONE_UPDATED.getDescription(milestoneNameForEditingDueDate),
                "Popup message is not as expected!");

        milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        int indexOfMilestoneAfterChangingDueDate = milestonePage.getIndexOfMilestoneOnPage(milestoneNameForEditingDueDate);
        boolean isPreviousIndexGreaterThanCurrent = indexOfMilestoneBeforeEditingDueDate > indexOfMilestoneAfterChangingDueDate;

        softAssert.assertTrue(isPreviousIndexGreaterThanCurrent,
                "Milestone after changing due date has the same position in grid"); // ZTP-1491 Verify if user edits milestone's due date, its sorting on the grid is restructured

        softAssert.assertTrue(milestonePage.isPresentedMilestonesWithDueDateSortedByAsc(),
                "Presented milestones with due date isn't sorted by asc after editing due date in milestone");
        softAssert.assertTrue(milestonePage.isMilestoneCardsWithoutDatesSortedByCreationOrder(createdMilestoneWithoutDates),
                "Expected order of created milestones without dates via api isn't equals to displayed on page after editing due date in milestone");

        softAssert.assertTrue(milestonePage.isMilestonesWithoutDatesPlacedAfterMilestonesWithDueDate(),
                "Milestones with due date isn't first in milestone grid"); // ZTP-1492 Verify milestones with 'due date' are shown first in the grid

        softAssert.assertAll();
    }

    @Test(groups = "milestones-created")
    @TestCaseKey("ZTP-1488")
    public void milestonePaginationTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.MILESTONES);

        int numbersOfMilestonesToGenerate = 13;
        List<Milestone> generatedMilestones = generateMilestones(numbersOfMilestonesToGenerate, false);
        CREATED_MILESTONES.addAll(generatedMilestones);
        SoftAssert softAssert = new SoftAssert();

        MilestonePage milestonePage = MilestonePage.openPageDirectly(getDriver(), project);
        PaginationR pagination = milestonePage.getPagination();

        softAssert.assertEquals(milestonePage.getMilestoneCards().size(), 10,
                "Preselected type of pagination isn't 10");

        String nameOfFirstMilestone = milestonePage.getMilestoneCards().get(0).getName();

        pagination.clickToNextPagePagination();
        softAssert.assertFalse(milestonePage.isMilestonePresent(nameOfFirstMilestone),
                "Milestone from first page present on the second pagination page");

        pagination.selectTwentyFiveItems();

        boolean isMoreThanTenMilestonesPresentOnPage = ((milestonePage.getMilestoneCards().size() > 10) && milestonePage.getMilestoneCards().size() <= 25);
        softAssert.assertTrue(isMoreThanTenMilestonesPresentOnPage,
                "Twenty five items pagination per page isn't displayed as expected");

        softAssert.assertAll();
    }

    private List<Milestone> generateMilestones(int numbersOfMilestonesToGenerate, boolean isGenerateMilestoneWithRandomDueDate) {
        List<Milestone> createdMilestones = new ArrayList<>();
        Random random = new Random();
        OffsetDateTime dueDate = null;
        Milestone milestone = null;

        for (int i = 0; i < numbersOfMilestonesToGenerate; i++) {
            milestoneName = i + ". Milestone".concat(RandomStringUtils.randomAlphabetic(5));

            if (isGenerateMilestoneWithRandomDueDate) {
                int monthsToAdd = random.nextInt(11) - 5;
                int daysToAdd = random.nextInt(21) - 10;
                OffsetDateTime currentDateTime = OffsetDateTime.now();
                dueDate = currentDateTime
                        .plusMonths(monthsToAdd)
                        .plusDays(daysToAdd);

                milestone = Milestone.createMilestoneWithTitleAndDueDate(milestoneName, Instant.from(dueDate));
            } else {
                milestone = Milestone.createMilestoneWithTitle(milestoneName);
                ;
            }

            milestone = super.apiHelperService.createMilestone(project.getId(), milestone);
            createdMilestones.add(milestone);
        }

        return createdMilestones;
    }

    private Milestone findMilestoneWithLatestDueDate(List<Milestone> milestones) {
        Milestone latestDueDateMilestone = null;

        for (Milestone milestone : milestones) {
            Instant milestoneDueDate = milestone.getDueDate();

            if (latestDueDateMilestone == null || milestoneDueDate.isAfter(latestDueDateMilestone.getDueDate())) {
                latestDueDateMilestone = milestone;
            }
        }

        return latestDueDateMilestone;
    }
}
