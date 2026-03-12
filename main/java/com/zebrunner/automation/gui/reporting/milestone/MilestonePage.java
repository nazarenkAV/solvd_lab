package com.zebrunner.automation.gui.reporting.milestone;

import com.zebrunner.automation.api.reporting.domain.Milestone;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.util.DateUtil;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
public class MilestonePage extends TenantProjectBasePage {

    public static final String PAGE_NAME = "Milestones";
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/milestones";
    private static final String NO_DUE_DATE = "No due date";
    private static final String NO_START_DATE = "No start date";

    @FindBy(xpath = "//div[@class='milestones-table__row']")
    private List<MilestoneCard> milestoneCards;

    @FindBy(xpath = "//button[contains(text(),'Open')]")
    private Element openButton;

    @FindBy(xpath = "//button[contains(text(),'Completed')]")
    private Element completedButton;

    @FindBy(xpath = "//span[contains(text(),'milestone')]/ancestor::button")
    private Element addMilestoneButton;

    @FindBy(xpath = "//span[@class='milestones-table__message-text']")
    private Element noMilestonesTitle;

    public MilestonePage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(addMilestoneButton.getRootExtendedElement());
    }

    public static MilestonePage getInstance(WebDriver driver) {
        return new MilestonePage(driver);
    }

    public static MilestonePage openPageDirectly(WebDriver driver, Project project) {
        MilestonePage milestonePage = new MilestonePage(driver);
        milestonePage.openURL(String.format(PAGE_URL, project.getKey()));
        milestonePage.assertPageOpened();
        return milestonePage;
    }

    public void deleteAllMilestones() {
        showAllMilestones();
        pause(2);
        if (!isNoMilestoneTitlePresent()) {
            log.info("Found milestones, deleting...");
            milestoneCards.forEach(MilestoneCard::delete);
        }
    }

    public boolean isMilestonePresent(String name) {
        if (isNoMilestoneTitlePresent()) {
            log.info("Can't find any of milestones");
            return false;
        }
        log.debug("Waiting for milestoneCards list to load...");
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(milestoneCards,
                card -> card.getTitle().equals(name));
    }

    public void showAllMilestones() {
        if (openButton.getAttributeValue("class").contains("_active")) {
            openButton.click();
        } else if (completedButton.getAttributeValue("class").contains("_active")) {
            completedButton.click();
        }
    }

    public MilestonePage showOpenMilestones() {
        if (!openButton.getAttributeValue("class").contains("_active")) {
            openButton.click();
        }
        return this;
    }

    public MilestonePage showCompleteMilestones() {
        if (!completedButton.getAttributeValue("class").contains("_active")) {
            completedButton.click();
        }
        pause(3);
        return this;
    }

    public void deleteMilestone(String name) {
        if (isNoMilestoneTitlePresent()) {
            throw new RuntimeException("Can't find milestone cards");
        }
        for (MilestoneCard milestoneCard : milestoneCards) {
            if (milestoneCard.getTitle().equals(name)) {
                log.info("Deleting milestone with name " + name);
                milestoneCard.delete();
                return;
            }
        }
    }

    public MilestoneCard getCertainMilestoneCard(String milestoneTitle) {
        if (isNoMilestoneTitlePresent()) {
            throw new RuntimeException("Can't find milestone cards");
        }

        return WaitUtil.waitElementAppearedInListByCondition(milestoneCards,
                card -> card.getTitle().equalsIgnoreCase(milestoneTitle),
                "Found milestone with name " + milestoneTitle,
                "Not found milestone with name " + milestoneTitle);
    }

    public void createMilestoneOnlyWithTitle(String title) {
        MilestoneModal milestoneModal = this.openAddMilestoneModal();
        milestoneModal.typeMilestoneName(title).submitModal();
    }

    public void createMilestoneWithTitleAndDescription(String title, String description) {
        MilestoneModal milestoneModal = this.openAddMilestoneModal();
        milestoneModal.typeMilestoneName(title)
                .typeDescription(description)
                .submitModal();
    }

    public void createMilestoneWithTitleAndDates(String title, LocalDate startDate, LocalDate endDate) {
        MilestoneModal milestoneModal = this.openAddMilestoneModal();
        milestoneModal.typeMilestoneName(title)
                .inputStartDate(startDate)
                .inputDueDate(endDate)
                .submitModal();
    }

    public boolean isNoMilestoneTitlePresent() {
        return noMilestonesTitle.isStateMatches(Condition.VISIBLE);
    }

    public boolean isOpenButtonPresent() {
        return openButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isFilteringSelectedByOpenMilestones() {
        return openButton.getAttributeValue("class").contains("_active");
    }

    public boolean isCompleteButtonPresent() {
        return completedButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isAddMilestoneButtonPresent() {
        return addMilestoneButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isBreadCrumbsMenuPresent() {
        return breadcrumbs.isBreadcrumbPresentOnPage();
    }

    public boolean isPresentedListIncludesOnlyOpenedMilestones() {
        return milestoneCards.stream().noneMatch(MilestoneCard::isCheckboxActive);
    }

    public boolean isPresentedMilestonesWithDueDateSortedByAsc() {
        WaitUtil.waitComponentList(milestoneCards);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date previousDueDate = null;

        for (MilestoneCard card : milestoneCards) {
            String dueDateInfo = card.getDueDateInfo();

            if (!Objects.equals(dueDateInfo, NO_DUE_DATE)) {
                Date currentDueDate = DateUtil.parseDate(dueDateInfo, dateFormat);

                if (previousDueDate != null && currentDueDate.before(previousDueDate)) {
                    return false;
                }

                previousDueDate = currentDueDate;
            }
        }

        return true;
    }

    public boolean isMilestoneCardsWithoutDatesSortedByCreationOrder(List<Milestone> createdMilestoneCards) {
        WaitUtil.waitComponentList(milestoneCards);

        if (createdMilestoneCards == null || milestoneCards == null) {
            throw new IllegalArgumentException("Invalid argument: Lists must be non-null");
        }

        boolean orderMatch = true;
        for (int i = 0; i < createdMilestoneCards.size() - 1; i++) {
            String currentMilestoneName = createdMilestoneCards.get(i).getName();
            String nextMilestoneName = createdMilestoneCards.get(i + 1).getName();

            boolean currentHasDueDate = !milestoneCards.get(i).getDueDateInfo().equals(NO_DUE_DATE);
            boolean nextHasDueDate = !milestoneCards.get(i + 1).getDueDateInfo().equals(NO_DUE_DATE);

            boolean currentHasStartDate = !milestoneCards.get(i).getStartDateInfo().equals(NO_START_DATE);
            boolean nextHasStartDate = !milestoneCards.get(i + 1).getStartDateInfo().equals(NO_START_DATE);

            if (!currentHasDueDate && !currentHasStartDate && !nextHasDueDate && !nextHasStartDate) {
                int currentMilestoneNameIndex = -1;
                int nextMilestoneNameIndex = -1;

                for (int b = 0; b < milestoneCards.size(); b++) {
                    if (milestoneCards.get(b).getTitle().equalsIgnoreCase(currentMilestoneName)) {
                        currentMilestoneNameIndex = b;
                    }

                    if (milestoneCards.get(b).getTitle().equalsIgnoreCase(nextMilestoneName)) {
                        nextMilestoneNameIndex = b;
                    }
                }

                if (!(currentMilestoneNameIndex < nextMilestoneNameIndex)) {
                    log.error("Milestone with name " + currentMilestoneName + " should be before milestone with name " + nextMilestoneName);
                    orderMatch = false;
                    break;
                } else {
                    log.info("Milestone with name " + currentMilestoneName + " is before milestone with name " + nextMilestoneName + " as expected when milestone has no due date or start date");
                }
            }
        }

        return orderMatch;
    }

    public int getIndexOfMilestoneOnPage(String milestoneName) {
        WaitUtil.waitComponentList(milestoneCards);

        int indexOfMilestone = -1;

        for (int i = 0; i < milestoneCards.size(); i++) {
            if (milestoneCards.get(i).getTitle().equalsIgnoreCase(milestoneName)) {
                indexOfMilestone = i;
            }
        }

        return indexOfMilestone;
    }

    public boolean isMilestonesWithoutDatesPlacedAfterMilestonesWithDueDate() {
        List<Integer> indexesOfMilestoneCardsWithDueDate = new ArrayList<>();
        List<Integer> indexesOfMilestoneCardsWithoutDueDate = new ArrayList<>();

        for (MilestoneCard card : milestoneCards) {
            if (card.getDueDateInfo().equalsIgnoreCase(NO_DUE_DATE)) {
                indexesOfMilestoneCardsWithoutDueDate.add(getIndexOfMilestoneOnPage(card.getTitle()));
            } else {
                indexesOfMilestoneCardsWithDueDate.add(getIndexOfMilestoneOnPage(card.getTitle()));
            }
        }

        boolean areMilestonesOrdered = true;
        int minSize = Math.min(indexesOfMilestoneCardsWithDueDate.size(), indexesOfMilestoneCardsWithoutDueDate.size());
        for (int i = 0; i < minSize; i++) {
            if (indexesOfMilestoneCardsWithDueDate.get(i) > indexesOfMilestoneCardsWithoutDueDate.get(i)) {
                areMilestonesOrdered = false;
                break;
            } else {
                log.info("Index of milestone with due date " + indexesOfMilestoneCardsWithDueDate.get(i) + " less than index of milestone without due date " + indexesOfMilestoneCardsWithoutDueDate.get(i));
            }
        }

        return areMilestonesOrdered;
    }

    public boolean isPresentedListIncludesOnlyCompletedMilestones() {
        return milestoneCards.stream().allMatch(MilestoneCard::isCheckboxActive);
    }

    public MilestoneModal openAddMilestoneModal() {
        addMilestoneButton.click();
        return new MilestoneModal(getDriver());
    }

    public List<MilestoneCard> getMilestoneCards() {
        pause(3);
        return milestoneCards;
    }

    @Deprecated(forRemoval = true) // should be removed after refactoring will be finished, for now we have new logic for adding milestone
    public class MilestoneProcessor {
        private final String milestoneName;
        private String startDate = "";
        private String startDay = "";
        private String endDate = "";
        private String endDay = "";
        private String description = "";

        public MilestoneProcessor(String milestoneName) {
            this.milestoneName = milestoneName;
        }

        @Deprecated(forRemoval = true) // should be removed after refactoring will be finished, for now we have new logic for adding milestone
        public MilestoneProcessor startDate(String startDate) {
            String[] start = startDate.split(" ");
            this.startDate = start[0] + " " + start[1];
            this.startDay = start[2];
            return this;
        }

        @Deprecated(forRemoval = true) // should be removed after refactoring will be finished, for now we have new logic for adding milestone
        public MilestoneProcessor dueDate(String endDate) {
            String[] start = endDate.split(" ");
            this.endDate = start[0] + " " + start[1];
            this.endDay = start[2];
            return this;
        }

        @Deprecated(forRemoval = true) // should be removed after refactoring will be finished, for now we have new logic for adding milestone
        public MilestoneProcessor description(String description) {
            this.description = description;
            return this;
        }

        @Deprecated(forRemoval = true) // should be removed after refactoring will be finished, for now we have new logic for adding milestone
        public MilestonePage create() {
            addMilestoneButton.click();
            MilestoneModal milestoneWindow = new MilestoneModal(getDriver());
            milestoneWindow.typeMilestoneName(milestoneName);
            if (!startDate.isEmpty() && !startDay.isEmpty()) {
                milestoneWindow.selectStartDate(startDate, startDay);
            }
            if (!endDate.isEmpty() && !endDay.isEmpty()) {
                milestoneWindow.selectEndDate(endDate, endDay);
            }
            if (!description.isEmpty()) {
                milestoneWindow.typeDescription(description);
            }
            milestoneWindow.submitModal();
            log.info("Created new milestone " + milestoneName);
            return MilestonePage.getInstance(getDriver());
        }

        @Deprecated(forRemoval = true) // should be removed after refactoring will be finished, for now we have new logic for adding milestone
        public MilestonePage edit(MilestoneCard card) {
            MilestoneModal milestoneWindow = card.edit();
            log.info("Editing milestone " + milestoneWindow.getTitle());
            if (!milestoneWindow.getTitle().equals(milestoneName)) {
                milestoneWindow.typeMilestoneName(milestoneName);
            }
            if (!startDate.isEmpty() && !startDay.isEmpty()) {
                milestoneWindow.selectStartDate(startDate, startDay);
            }
            if (!endDate.isEmpty() && !endDay.isEmpty()) {
                milestoneWindow.selectEndDate(endDate, endDay);
            }
            if (!description.isEmpty()) {
                milestoneWindow.typeDescription(description);
            }
            milestoneWindow.submitModal();
            return new MilestonePage(getDriver());
        }
    }
}
