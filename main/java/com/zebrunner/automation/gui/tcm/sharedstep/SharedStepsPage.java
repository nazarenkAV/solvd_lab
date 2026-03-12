package com.zebrunner.automation.gui.tcm.sharedstep;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.api.tcm.domain.TestCaseStep;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SharedStepsPage extends TenantProjectBasePage implements IMobileUtils {
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/shared-steps";
    public static final String EMPTY_PAGE_MESSAGE = "There are no shared steps yet";

    @FindBy(xpath = "//button/span[contains(text(), 'Step')]")
    private ExtendedWebElement addSharedStepsButton;

    @FindBy(xpath = SharedStepsCard.ROOT_XPATH)
    private List<SharedStepsCard> sharedStepsCards;

    @FindBy(xpath = "//h2[text() = 'Shared steps']")
    private ExtendedWebElement uiLoadedMarker;

    public SharedStepsPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static SharedStepsPage openPageDirectly(WebDriver driver, String projectKey) {
        SharedStepsPage sharedStepsPage = new SharedStepsPage(driver);
        sharedStepsPage.openURL(String.format(PAGE_URL, projectKey));
        sharedStepsPage.assertPageOpened();
        return sharedStepsPage;
    }

    public void searchSharedStep(String stepTitle) {
        search.search(stepTitle);
    }

    public boolean isAddSharedStepsButtonPresent() {
        return addSharedStepsButton.isElementPresent(3);
    }

    public boolean isAddSharedStepsButtonClickable() {
        return addSharedStepsButton.isClickable();
    }

    public boolean isSharedStepPresent(String title) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(sharedStepsCards,
                card -> card.cardTitle().trim().equalsIgnoreCase(title));
    }

    public SharedStepsModal clickAddSharedSteps() {
        addSharedStepsButton.click();
        return new SharedStepsModal(getDriver());
    }

    public SharedStepsCard getCertainSharedStepsCard(String sharedStepsTitle) {
        return WaitUtil.waitElementAppearedInListByCondition(sharedStepsCards,
                card -> card.cardTitle().trim().equalsIgnoreCase(sharedStepsTitle),
                "Found shared steps card with name " + sharedStepsTitle,
                "Not found shared steps card with name " + sharedStepsTitle);
    }

    public List<SharedStepsCard> getAllSharedStepCards() {
        WaitUtil.waitCheckListIsNotEmpty(sharedStepsCards);
        return sharedStepsCards;
    }

    public void createSharedStep(String title, List<TestCaseStep> steps) {
        SharedStepsModal sharedStepsModal = clickAddSharedSteps();

        sharedStepsModal.typeTitle(title);
        sharedStepsModal.addSteps(steps, false);
        sharedStepsModal.submit();
    }
}
