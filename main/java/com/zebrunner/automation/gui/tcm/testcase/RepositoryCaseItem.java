package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.util.KeyCombinations;
import com.zebrunner.automation.gui.tcm.repository.BaseRepositoryItem;
import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class RepositoryCaseItem extends BaseRepositoryItem {
    public static final String ROOT_XPATH = "div[contains(@class,'repository-virtuoso-item-case')]";
    public static final String BACKGROUND_HEX_COLOR_ON_HOVER = "#f5f5f5";

    @FindBy(xpath = ".//*[contains(@class, 'repository-case__title')]")
    private ExtendedWebElement caseTitle;

    @FindBy(xpath = ".//button[@aria-label='Edit']")
    private ExtendedWebElement editBtn;

    @FindBy(xpath = ".//button[@aria-label='Clone']")
    private ExtendedWebElement cloneBtn;

    @FindBy(xpath = ".//button[@aria-label='Delete']")
    private ExtendedWebElement deleteBtn;

    @FindBy(xpath = ".//*[contains(@class, 'repository-case__id')]")
    private ExtendedWebElement caseId;//key

    @FindBy(xpath = ".//*[contains(@aria-label,'Priority')]")
    private ExtendedWebElement priority;

    @FindBy(xpath = ".//*[contains(@aria-label,'Automation State')]")
    private ExtendedWebElement automationState;

    @FindBy(xpath = "./parent::div[contains(@style,'padding')]")
    private ExtendedWebElement style;

    @FindBy(xpath = ".//preceding::p[contains(@class,'repository-virtuoso-item-suite__title')]")
    private List<ExtendedWebElement> getParentSuiteNames;

    public RepositoryCaseItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isLast() {
        return this.getAttribute("class").contains("_last-case");
    }

    public String getTestCaseTitleValue() {
        return caseTitle.getText();
    }

    public Integer getLeftPadding() {
        String padding = style.getCssValue("padding-left");
        if (padding != null) {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(padding);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        }
        return 0;
    }

    public String getParentSuiteName() {
        int size = getParentSuiteNames.size();
        return getParentSuiteNames.get(size - 1).getText();
    }

    public DeleteTestCaseModal clickDelete() {
        this.hover();
        deleteBtn.click();
        return new DeleteTestCaseModal(getDriver());
    }

    public CloneTestCaseModal clickClone() {
        this.hover();
        cloneBtn.click();
        return new CloneTestCaseModal(getDriver());
    }

    public EditTestCaseModal clickEdite() {
        this.hover();
        editBtn.click();
        return new EditTestCaseModal(getDriver());
    }

    public String getBackgroundColor() {
        pause(1);
        String rgba = this.findElement(By.xpath(".//div[contains(@class,'repository-virtuoso-item-case')]"))
                .getCssValue("background-color");
        return ColorUtil.getHexColorFromString(rgba);
    }

    public String getCaseKeyValue() {
        return caseId.getText();
    }

    public DedicatedTestCasePage openCaseInNewTab() {

        String openInNewTab = "window.open(arguments[0].href, '_blank');";
        ((JavascriptExecutor) getDriver()).executeScript(openInNewTab, caseId.getElement());

        return new DedicatedTestCasePage(getDriver());
    }

    public <T extends AbstractTestCasePreview<?>> T clickTestCase() {
        this.scrollTo();
        this.click();

        AbstractTestCasePreview<?> preview = new TestCaseModalView(getDriver());
        if (preview.isPresent(7)) {
            return (T) preview;
        }

        AbstractTestCasePreview<?> sidebar = new TestCaseSideBarView(getDriver());
        if (sidebar.isPresent(7)) {
            return (T) sidebar;
        }

        throw new NoSuchElementException("Neither Test case modal preview nor side bar menu was opened!");
    }

    public void holdCmdAndClickTestCaseKey() {
        Actions action = new Actions(getDriver());
        Keys key = KeyCombinations.isMac(getDriver()) ? Keys.COMMAND : Keys.CONTROL;

        action
                .keyDown(key)
                .click(caseId.getElement())
                .keyUp(key)
                .perform();

    }
}
