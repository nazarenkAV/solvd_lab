package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.util.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

//md-dialog[contains(@class,'milestone-modal')]
public class AssignToMilestoneModalR extends AbstractModal<AssignToMilestoneModalR> {

    public static final String MODAL_NAME = "Assign to milestone";

    @FindBy(xpath = ".//div[@class='assign-milestone-modal__search']//input")
    private Element searchInput;

    @FindBy(xpath = ".//button[text()='Assign']")
    protected Element assign;

    @FindBy(xpath = ".//div[@class='assign-milestone-modal__radio-label' and text()='None']")
    protected Element noneButton;

    @FindBy(xpath = ".//label[@class]")
    private List<ExtendedWebElement> milestones;

    @FindBy(xpath = ".//button[contains(@class, 'main-modal__close-icon')]")
    protected Element closeModalButton;

    public AssignToMilestoneModalR(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[@class='assign-milestone-modal']"));
    }

    public AssignToMilestoneModalR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isMilestoneChooseExists(String milestoneName) {
        pause(3);
        return milestones.stream()
                .anyMatch(milestone -> milestone.getText().trim().equalsIgnoreCase(milestoneName));
    }

    public void inputSearchTitle(String milestoneName) {
        searchInput.click();
        searchInput.sendKeys(milestoneName);
        pause(2);
    }

    public AssignToMilestoneModalR chooseMilestone(String milestoneName) throws NoSuchElementException {
        inputSearchTitle(milestoneName);
        WaitUtil.waitElementAppearedInListByCondition(milestones,
                        milestone -> milestone.getText().trim().equalsIgnoreCase(milestoneName),
                        "Milestone with name: " + milestoneName + " was founded",
                        "There are no milestone " + milestoneName + " on modal " + MODAL_NAME)
                .click(2);
        return this;
    }

    public AutomationLaunchesPage assign() {
        assign.click();
        return AutomationLaunchesPage.getPageInstance(getDriver());
    }

    public AutomationLaunchesPage unAssign() {
        noneButton.click();
        assign.click();
        return AutomationLaunchesPage.getPageInstance(getDriver());
    }

    public AutomationLaunchesPage chooseMilestoneAndAssign(String milestoneName) throws NoSuchElementException {
        return chooseMilestone(milestoneName)
                .assign();
    }

    public boolean isAssignToMilestoneModalOpened() {
        return this.isPresent(7);
    }

    public boolean isNoneButtonSelected() {
        return milestones.stream()
                .filter(milestone -> milestone.getText().trim().equalsIgnoreCase(noneButton.getText()))
                .map(milestone -> milestone.findElement(By.xpath(".//input")))
                .map(inputElement -> inputElement.getAttribute("checked") != null)
                .findFirst()
                .orElse(false);
    }

    public void closeModal() {
        closeModalButton.click();
    }
}
