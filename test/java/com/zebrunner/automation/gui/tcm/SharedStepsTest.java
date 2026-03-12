package com.zebrunner.automation.gui.tcm;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.legacy.TooltipEnum;
import com.zebrunner.automation.gui.common.NavigationMenu;
import com.zebrunner.automation.gui.common.NavigationMenuPopover;
import com.zebrunner.automation.gui.tcm.sharedstep.DeleteSharedStepsModal;
import com.zebrunner.automation.gui.tcm.sharedstep.SharedStepsCard;
import com.zebrunner.automation.gui.tcm.sharedstep.SharedStepsModal;
import com.zebrunner.automation.gui.tcm.sharedstep.SharedStepsPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.tcm.domain.SharedStepsBunch;
import com.zebrunner.automation.api.tcm.domain.TestCase;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.util.LocalStorageManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class SharedStepsTest extends TcmLogInBase {
    private Project project;

    private String username;

    @BeforeClass
    public void getProject() {
        project = super.getCreatedProject();
    }

    @AfterMethod(onlyForGroups = "user-was-created", alwaysRun = true)
    public void deleteCreatedUser() {
        usersService.getUserId(username)
                .ifPresent(usersService::deleteUserById);
    }

    //============================== Tests =========================================

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-2801", "ZTP-2802", "ZTP-2804", "ZTP-2805", "ZTP-5233", "ZTP-2820"})
    public void _verifySharedStepsPage() {
        SoftAssert softAssert = new SoftAssert();

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        NavigationMenuPopover navigationMenuPopover = sharedStepsPage.getNavigationMenu().hover(NavigationMenu.NavigationMenuItem.TEST_REPOSITORY);

        softAssert.assertTrue(navigationMenuPopover.isItemPresent(NavigationMenuPopover.PopoverItems.SHARED_STEPS),
                "Shared steps button is not present!");//ZTP-2801

        navigationMenuPopover.selectItem(NavigationMenuPopover.PopoverItems.SHARED_STEPS);

        sharedStepsPage = new SharedStepsPage(getDriver());
        softAssert.assertTrue(sharedStepsPage.isPageOpened(), "Shared steps page isn't opened");//ZTP-2802
        softAssert.assertEquals(sharedStepsPage.getEmptyPlaceholder().getEmptyPagePlaceholder().getText(), SharedStepsPage.EMPTY_PAGE_MESSAGE,
                "There are shared steps, when should not be. Empty placeholder is not visible!");
        softAssert.assertTrue(sharedStepsPage.isAddSharedStepsButtonPresent(), "Shared steps button is not present!");//ZTP-2804
        softAssert.assertTrue(sharedStepsPage.isAddSharedStepsButtonClickable(), "Shared steps button is not clickable!");

        SharedStepsModal sharedStepsModal = sharedStepsPage.clickAddSharedSteps();
        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Modal title is not present!");
        softAssert.assertTrue(sharedStepsModal.isCreateButtonPresent(), "Add new step is not present or clickable!");

        String title = "SharedStepTitle ".concat(RandomStringUtils.randomAlphabetic(3));
        sharedStepsModal.typeTitle(title);
        sharedStepsModal.addSteps(TestCaseStep.generateRandomSteps(1), false);
        sharedStepsModal.submit();

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Card with " + title + " is not present");//ZTP-5233
        softAssert.assertTrue(sharedStepsPage.getSearch().isPresent(), "Shared steps search is not present!");//ZTP-2805

        softAssert.assertEquals(sharedStepsPage.getCertainSharedStepsCard(title).getNameTooltip(), title,
                "Tooltip for Name is not as expected!");//ZTP-2820
        softAssert.assertEquals(sharedStepsPage.getCertainSharedStepsCard(title).getEditButtonTooltip(), TooltipEnum.TOOLTIP_EDIT.getToolTipMessage(),
                "Tooltip for Edit button is not as expected!");
        softAssert.assertEquals(sharedStepsPage.getCertainSharedStepsCard(title).getDeleteButtonTooltip(), TooltipEnum.TOOLTIP_DELETE.getToolTipMessage(),
                "Tooltip for Delete button is not as expected!");

        sharedStepsPage.searchSharedStep("s");

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Card with " + title + " is not present, when searching single letter!");

        sharedStepsPage.searchSharedStep("SharedStepTitle");

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Card with " + title + " is not present, when searching word!");

        sharedStepsPage.searchSharedStep(title);

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Card with " + title + " is not present, when searching full name!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-2815")
    public void verifyExpandSharedStepsIsPresent() {
        SoftAssert softAssert = new SoftAssert();

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        String title = "SharedStepTitle ".concat(RandomStringUtils.randomAlphabetic(3));

        List<TestCaseStep> generatedSteps = TestCaseStep.generateRandomSteps(3);
        for (int i = 0; i < generatedSteps.size(); i++) {
            generatedSteps.get(i).setRelativePosition(i + 1);
        }
        sharedStepsPage.createSharedStep(title, generatedSteps);

        SharedStepsCard sharedStepsCard = sharedStepsPage.getCertainSharedStepsCard(title);
        sharedStepsCard.expandSharedStep();

        softAssert.assertTrue(sharedStepsCard.isExpanded(), "Shared step is not expanded!");

        List<TestCaseStep> actualSteps = sharedStepsCard.getSteps();

        softAssert.assertEquals(actualSteps, generatedSteps, "Shared Steps don't match!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5234")
    public void editSharedStepTest() {
        SoftAssert softAssert = new SoftAssert();

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        String title = "SharedStepTitle ".concat(RandomStringUtils.randomAlphabetic(3));
        List<TestCaseStep> firstGeneratedSteps = TestCaseStep.generateRandomSteps(1);
        for (int i = 0; i < firstGeneratedSteps.size(); i++) {
            firstGeneratedSteps.get(i).setRelativePosition(i + 1);
        }
        sharedStepsPage.createSharedStep(title, firstGeneratedSteps);

        softAssert.assertTrue(sharedStepsPage.getCertainSharedStepsCard(title).isEditButtonClickable(), "Edit button is not clickable!");

        SharedStepsModal sharedStepsModal = sharedStepsPage.getCertainSharedStepsCard(title).edit();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Edit modal is not present!");

        List<TestCaseStep> editedSteps = TestCaseStep.generateRandomSteps(1);
        for (int i = 0; i < editedSteps.size(); i++) {
            editedSteps.get(i).setRelativePosition(i + 1);
        }
        sharedStepsModal.addSteps(editedSteps, false);

        softAssert.assertTrue(sharedStepsModal.isSubmitButtonPresent(), "Save button is not present!");

        sharedStepsModal.submit();

        pause(2);
        SharedStepsCard sharedStepsCard2 = sharedStepsPage.getCertainSharedStepsCard(title);
        sharedStepsCard2.clickTitle();

        List<TestCaseStep> actualSteps = sharedStepsCard2.getSteps();

        softAssert.assertEquals(actualSteps, editedSteps, "Shared steps after editing should match!");
        softAssert.assertNotEquals(actualSteps, firstGeneratedSteps, "Shared steps has not been edited or is wrong!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5235")
    public void deleteSharedStepTest() {
        SoftAssert softAssert = new SoftAssert();

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        String title = "SharedStepTitle ".concat(RandomStringUtils.randomAlphabetic(3));
        sharedStepsPage.createSharedStep(title, TestCaseStep.generateRandomSteps(2));

        softAssert.assertTrue(sharedStepsPage.getCertainSharedStepsCard(title).isDeleteButtonClickable(), "Delete button is not clickable!");

        DeleteSharedStepsModal deleteSharedStepsModal = sharedStepsPage.getCertainSharedStepsCard(title).delete();

        softAssert.assertTrue(deleteSharedStepsModal.isModalOpened(), "Delete modal is not present!");
        softAssert.assertEquals(deleteSharedStepsModal.getModalBodyText(),
                String.format("You are about to delete the “%s“ shared steps.\n" +
                        "Are you sure you want to proceed with this action?", title));

        deleteSharedStepsModal.delete();

        pause(2);
        softAssert.assertFalse(sharedStepsPage.isSharedStepPresent(title), "Shared steps should not be present!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-2810", "ZTP-5237", "ZTP-5238"})
    public void cancelAddingEditingAndDeletingTest() {

        //Create - ZTP-2810

        SoftAssert softAssert = new SoftAssert();

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        softAssert.assertTrue(sharedStepsPage.isAddSharedStepsButtonClickable(), "Shared steps button is not clickable!");

        SharedStepsModal sharedStepsModal = sharedStepsPage.clickAddSharedSteps();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Create Shared Step modal is not opened (Cancel)!");

        String title = "SharedStepTitle ".concat(RandomStringUtils.randomAlphabetic(3));
        sharedStepsModal.typeTitle(title);
        List<TestCaseStep> generatedSteps = TestCaseStep.generateRandomSteps(1);
        for (int i = 0; i < generatedSteps.size(); i++) {
            generatedSteps.get(i).setRelativePosition(i + 1);
        }
        sharedStepsModal.addSteps(generatedSteps, false);

        softAssert.assertTrue(sharedStepsModal.getCancelButton().isPresent(), "Cancel button is not present");

        sharedStepsModal.clickCancel();

        softAssert.assertFalse(sharedStepsModal.isModalOpened(), "Modal should not be present after canceling creation!");
        softAssert.assertFalse(sharedStepsPage.isSharedStepPresent(title), "Shared steps should not be present after canceling creation!");

        sharedStepsPage.clickAddSharedSteps();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Create Shared Step modal is not opened (Close)!");

        sharedStepsModal.typeTitle(title);
        sharedStepsModal.addSteps(generatedSteps, false);
        sharedStepsModal.close();

        softAssert.assertFalse(sharedStepsModal.isModalOpened(), "Modal should not be present after closing creation!");
        softAssert.assertFalse(sharedStepsPage.isSharedStepPresent(title), "Shared steps should not be present after closing creation!");

        sharedStepsPage.clickAddSharedSteps();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Create Shared Step modal is not opened (ESC)!");

        sharedStepsModal.typeTitle(title);
        sharedStepsModal.addSteps(generatedSteps, false);

        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).build().perform();

        softAssert.assertFalse(sharedStepsModal.isModalOpened(), "Modal should not be present after escaping from creation!");
        softAssert.assertFalse(sharedStepsPage.isSharedStepPresent(title), "Shared steps should not be present after escaping from creation!");

        //Edit - ZTP-5237

        sharedStepsPage.createSharedStep(title, generatedSteps);

        sharedStepsPage.getCertainSharedStepsCard(title).edit();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Edit Shared Step modal is not opened (Cancel)!");

        List<TestCaseStep> editedSteps = TestCaseStep.generateRandomSteps(1);
        for (int i = 0; i < editedSteps.size(); i++) {
            editedSteps.get(i).setRelativePosition(i + 1);
        }
        pause(2);
        sharedStepsModal.addSteps(editedSteps, false);
        sharedStepsModal.clickCancel();

        softAssert.assertFalse(sharedStepsModal.isModalOpened(), "Modal should not be present after cancelling edition!");

        SharedStepsCard sharedStepsCard = sharedStepsPage.getCertainSharedStepsCard(title);
        sharedStepsCard.clickTitle();

        pause(2);
        List<TestCaseStep> actualSteps = sharedStepsCard.getSteps();

        softAssert.assertEquals(actualSteps, generatedSteps, "Shared steps should stay same after cancelling edition!");

        sharedStepsPage.getCertainSharedStepsCard(title).edit();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Edit Shared Step modal is not opened (Close)!");

        pause(2);
        sharedStepsModal.addSteps(editedSteps, false);
        sharedStepsModal.close();

        softAssert.assertFalse(sharedStepsModal.isModalOpened(), "Modal should not be present after closing edition!");

        sharedStepsCard = sharedStepsPage.getCertainSharedStepsCard(title);
        sharedStepsCard.clickTitle();

        pause(2);
        softAssert.assertEquals(actualSteps, generatedSteps, "Shared steps should stay same after closing edition!");

        sharedStepsPage.getCertainSharedStepsCard(title).edit();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Edit Shared Step modal is not opened (ESC)!");

        pause(2);
        sharedStepsModal.addSteps(editedSteps, false);

        actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).build().perform();

        softAssert.assertFalse(sharedStepsModal.isModalOpened(), "Modal should not be present after escaping from edition!");

        sharedStepsCard = sharedStepsPage.getCertainSharedStepsCard(title);
        sharedStepsCard.clickTitle();

        pause(2);
        softAssert.assertEquals(actualSteps, generatedSteps, "Shared steps should stay same after using ESC from edition!");

        //Delete - ZTP-5238

        DeleteSharedStepsModal deleteSharedStepsModal = sharedStepsPage.getCertainSharedStepsCard(title).delete();

        softAssert.assertTrue(deleteSharedStepsModal.isModalOpened(), "Delete modal is not opened (Cancel)!");

        deleteSharedStepsModal.clickCancel();

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Shared steps should be present after cancelling deletion!");

        deleteSharedStepsModal = sharedStepsPage.getCertainSharedStepsCard(title).delete();

        softAssert.assertTrue(deleteSharedStepsModal.isModalOpened(), "Delete modal is not opened (Close)!");

        deleteSharedStepsModal.clickCancel();

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Shared steps should be present after closing deletion!");

        deleteSharedStepsModal = sharedStepsPage.getCertainSharedStepsCard(title).delete();

        softAssert.assertTrue(deleteSharedStepsModal.isModalOpened(), "Delete modal is not opened (ESC)!");

        actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).build().perform();

        softAssert.assertTrue(sharedStepsPage.isSharedStepPresent(title), "Shared steps should be present after escaping from deletion!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-2812", "ZTP-2813"})
    public void verifyNumberOfAttachmentsAndStepsOnSharedStep() {
        SoftAssert softAssert = new SoftAssert();

        TestSuite testSuite = tcmService.createTestSuite(project.getId(), new TestSuite("Suite for automation"));
        SharedStepsBunch shared = tcmService.createSharedStep(project.getId(), SharedStepsBunch.generateRandom(2));
        TestCase.Step sharedStep = TestCase.Step.shared(shared);

        TestCase testCase = TestCase.builder()
                .title("Test case № ".concat(RandomStringUtils.randomNumeric(7)))
                .steps(List.of(sharedStep))
                .build();

        tcmService.createTestCase(project.getId(), testSuite.getId(), testCase);

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        SharedStepsCard sharedStepsCard = sharedStepsPage.getCertainSharedStepsCard(shared.getName());

        softAssert.assertEquals(sharedStepsCard.getStepsCount(), shared.getSteps().size(),
                "Shared step, step count is not as expected!");//ZTP-2812
        softAssert.assertEquals(sharedStepsCard.getAttachmentCount(), 1,
                "Shared step, attachment count is not as expected!");//ZTP-2813

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-2814")
    public void verifyCardsAreCollapsed() {
        SoftAssert softAssert = new SoftAssert();

        tcmService.createSharedStep(project.getId(), SharedStepsBunch.generateRandom(2));
        tcmService.createSharedStep(project.getId(), SharedStepsBunch.generateRandom(2));

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());
        sharedStepsPage.getAllSharedStepCards().forEach(sharedStepsCard -> {
            softAssert.assertFalse(sharedStepsCard.isExpanded(),
                    "Card is expanded for " + sharedStepsCard.cardTitle());
        });

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5247")
    public void verifyCreatingSharedStepWithSameName() {
        SoftAssert softAssert = new SoftAssert();

        SharedStepsBunch shared = tcmService.createSharedStep(project.getId(), SharedStepsBunch.generateRandom(2));

        String sharedStepName = shared.getName();

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());
        SharedStepsModal sharedStepsModal = sharedStepsPage.clickAddSharedSteps();

        softAssert.assertTrue(sharedStepsModal.isModalOpened(), "Modal is not opened!");

        sharedStepsModal.typeTitle(sharedStepName);
        sharedStepsModal.addSteps(TestCaseStep.generateRandomSteps(1), false);

        softAssert.assertTrue(sharedStepsModal.isCreateButtonPresent(), "Create button is not present");

        sharedStepsModal.submit();

        pause(2);
        List<SharedStepsCard> sharedStepCards = sharedStepsPage.getAllSharedStepCards();
        int count = 0;

        for (SharedStepsCard card : sharedStepCards) {
            if (card.getText().contains(sharedStepName)) {
                count++;
            }
        }

        softAssert.assertEquals(count, 2, "Number of shared step cards with title '" + sharedStepName + "' is not correct!");
        softAssert.assertAll();
    }

    @Test(groups = "user-was-created")
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-6011")
    public void verifyAddSharedStepButtonIsNotPresentForGuestUser() {
        User user = usersService.addRandomUserToTenant();
        username = user.getUsername();
        projectService.assignUserToProject(project.getId(), user.getId(), RoleEnum.GUEST.getName().toUpperCase());

        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());
        localStorageManager.clear();

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, iamService.login(user));

        SharedStepsPage sharedStepsPage = SharedStepsPage.openPageDirectly(getDriver(), project.getKey());

        Assert.assertFalse(sharedStepsPage.isAddSharedStepsButtonPresent(),
                "Add shared step button shouldn't be present !");
    }
}
