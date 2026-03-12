package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.launcher.AddOrEditLauncherPage;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class MenuContent extends AbstractUIObject {
    @FindBy(xpath = ".//span[text()='Open in new tab']")
    public Element openInNewTab;

    @FindBy(xpath = ".//span[text()='Copy link']")
    private Element copyLink;

    @FindBy(xpath = ".//span[text()='Mark as reviewed']")
    private Element markAsReviewed;

    @FindBy(xpath = ".//span[text()='Assign to milestone']")
    private Element assignToMilestone;

    @FindBy(xpath = ".//span[text()='Send as email']")
    private Element sendAsEmail;

    @FindBy(xpath = ".//span[text()='Export to HTML']")
    private Element exportToHtml;

    @FindBy(xpath = ".//span[text()='Build now']")
    private Element buildNow;

    @FindBy(xpath = ".//span[text()='Rerun']")
    private Element rerun;

    @FindBy(xpath = ".//span[text()='Abort']")
    private Element abort;

    @FindBy(xpath = ".//span[text()='Go to launcher']")
    private Element goToLauncher;

    @FindBy(xpath = ".//span[text()='Delete']")
    private Element delete;

    public MenuContent(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//ul[@role='menu']"));
    }

    public DeleteTestRunModal openDeleteModal(){
        delete.click();
        return new DeleteTestRunModal(getDriver());
    }

    public AddOrEditLauncherPage goToLauncher(){
        goToLauncher.click();
        return new AddOrEditLauncherPage(getDriver());
    }

    public AssignToMilestoneModalR openAssignToMilestoneModal() {
        assignToMilestone.click();
        return new AssignToMilestoneModalR(getDriver());
    }

    public MarkAsReviewedModal openMarkAsReviewModal() {
        markAsReviewed.click();
        return new MarkAsReviewedModal(getDriver());
    }
}
