package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.common.ZbrCheckbox;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
@Getter
public class TrashBinTestCaseCard extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[contains(@class, 'trash-bin-test-case  MuiBox-root')]";

    @FindBy(xpath = ".//span[contains(@class, 'case__title')]")
    private ExtendedWebElement testCaseTitle;

    @FindBy(xpath = ".//a[contains(@class, 'trash-bin-test-case__id')]")
    private ExtendedWebElement testCaseKey;

    @FindBy(xpath = ".//*[@aria-label = 'Restore test case']")
    private ExtendedWebElement restoreTestCaseIcon;

    @FindBy(xpath = ".//*[@aria-label = 'Purge test case']")
    private ExtendedWebElement purgeTestCaseIcon;

    @FindBy(xpath = ".//*[contains(@aria-label, 'Priority')]")
    private ExtendedWebElement priority;

    @FindBy(xpath = ZbrCheckbox.ROOT_XPATH)
    private ZbrCheckbox checkbox;

    @FindBy(xpath = ".//*[contains(@class, 'zbr-on-hover-user-card__children')]")
    private ExtendedWebElement username;

    @FindBy(xpath = ".//*[@class = 'trash-bin-test-case__info']/span[1]")
    private ExtendedWebElement deletedTime;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public TrashBinTestCaseCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getTestCaseTitleText() {
        return testCaseTitle.getText();
    }

    public void hoverTestCase() {
        this.hover();
    }

    public TrashBinTestCasePreview clickTestCard() {
        this.click();
        return new TrashBinTestCasePreview(getDriver());
    }

    public boolean isRestoreTestCaseIconPresent() {
        return restoreTestCaseIcon.isElementPresent(2);
    }

    public boolean isPurgeTestCaseIconPresent() {
        return purgeTestCaseIcon.isElementPresent(2);
    }

    public RestoreTestCasesModals clickRestoreTestCaseIcon() {
        restoreTestCaseIcon.click();
        return new RestoreTestCasesModals(getDriver());
    }

    public PurgeTestCasesModals clickPurgeTestCaseIcon() {
        purgeTestCaseIcon.click();
        return new PurgeTestCasesModals(getDriver());
    }

    public String getPriority() {
        String ariaLabel = priority.getAttribute("aria-label");
        if (ariaLabel != null) {
            String[] parts = ariaLabel.split(": ");
            if (parts.length == 2) {
                return parts[1];
            }
        }
        return null;
    }

    public UserInfoTooltip hoverUsername() {
        username.hover();
        return new UserInfoTooltip(getDriver());
    }

    public String hoverAndGetTestCaseTitleToolTip() {
        testCaseTitle.hover();
        pause(2);
        return tooltip.getTooltipText();
    }

    public String hoverAndGetPurgeTestIconToolTip() {
        purgeTestCaseIcon.hover();
        pause(2);
        return tooltip.getTooltipText();
    }

    public String hoverAndGetRestoreTestIconToolTip() {
        restoreTestCaseIcon.hover();
        pause(2);
        return tooltip.getTooltipText();
    }

    public String hoverAndGetDeletedDateToolTip() {
        deletedTime.hover();
        pause(2);
        return tooltip.getTooltipText();
    }

    public RestoreTestCasesModals hoverAndClickRestore() {
        log.info("Attempting to restore test case...");

        hoverTestCase();
        pause(2);
        return clickRestoreTestCaseIcon();
    }

    public DedicatedTestCasePage openCaseInNewTab() {
        log.info("Opening test case in new tab...");

        String openInNewTab = "window.open(arguments[0].href, '_blank');";
        ((JavascriptExecutor) getDriver()).executeScript(openInNewTab, testCaseKey.getElement());

        return new DedicatedTestCasePage(getDriver());
    }
}