package com.zebrunner.automation.gui.tcm.repository;

import com.zebrunner.automation.gui.common.ZbrCheckbox;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.tcm.testcase.RepositoryCaseItem;
import com.zebrunner.automation.gui.tcm.testsuite.CreateOrEditSuiteModal;
import com.zebrunner.automation.gui.tcm.testsuite.SuiteItemActions;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
public class BaseRepositoryItem extends AbstractUIObject {

    public static final String ANCESTOR_XPATH = "div[@data-index]";

    @FindBy(xpath = ZbrCheckbox.ROOT_XPATH)
    private ZbrCheckbox checkbox;

    @FindBy(xpath = ".//div[contains(@class, '_hover-icon')]")
    private ExtendedWebElement hoverIcon;

    @FindBy(xpath = ".//*[contains(@class,'repository-virtuoso-item-suite__icon')]")
    private Element arrowIcon;//to expand/collapse suite

    @FindBy(xpath = ".//*[contains(@class,'repository-virtuoso-item-suite__title')]")
    private Element suiteTitle;

    @FindBy(xpath = ".//div[contains(@style,'padding')]")
    private ExtendedWebElement style;

    @FindBy(xpath = ".//*[contains(@class,'repository-virtuoso-item-suite__count')]")
    private Element suitesCount;

    @FindBy(xpath = SuiteItemActions.ROOT_XPATH)
    private SuiteItemActions suiteActions;

    @FindBy(xpath = "./following::" + RepositoryCaseItem.ROOT_XPATH)
    private List<RepositoryCaseItem> caseItems;

    @FindBy(xpath = "./following-sibling::" + "div//div[contains(@class,'test-cases-add-new-case')]/button")
    private List<ExtendedWebElement> createCaseBtns;// we can see 'Create test case' buttons from suites located bellow

    @FindBy(xpath = "./following-sibling::" + "div//div[contains(@class,'test-cases-add-new-case')]//input")
    private ExtendedWebElement inputCaseName;

    public BaseRepositoryItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isExpanded() {
        return arrowIcon.isPresent(Duration.ofSeconds(1)) && arrowIcon.getAttributeValue("class").contains("expand-icon");
    }

    public List<RepositoryCaseItem> getDirectSuiteTestCases(boolean expandBeforeObtaining) {
        if (expandBeforeObtaining) {
            expandCasesIfNeeded();
        }

        List<RepositoryCaseItem> result = new ArrayList<>();

        for (RepositoryCaseItem caseItem : caseItems) {

            String suiteLeftPadding = this.getSuiteName();
            String caseLeftPadding = caseItem.getParentSuiteName();

            log.info("Suite name " + suiteLeftPadding);
            log.info("Case name " + caseLeftPadding);

            if (!caseItem.getParentSuiteName().equalsIgnoreCase(this.getSuiteName())) {
                break;
            } else {

                log.info("Added case " + caseItem.getCaseTitle().getText());
                result.add(caseItem);

                if (caseItem.isLast()) {
                    break;
                }
            }
        }
        return result;
    }

    public List<RepositoryCaseItem> getDirectSuiteTestCases() {
        return getDirectSuiteTestCases(true);
    }

    public String getSuiteName() {
        return suiteTitle.getText();
    }

    public void expandCasesIfNeeded() {
        if (!isExpanded()) {
            arrowIcon.click();
            log.info("Expanded cases for suite " + this.getSuiteName());
            pause(1);
        }
    }

    public void collapseCasesIfNeeded() {
        if (isExpanded()) {
            arrowIcon.click();
            log.info("Collapsed cases for suite " + this.getSuiteName());
            pause(1);
        }
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


    public void clickCreateQuickTestCase(String title) {
        expandCasesIfNeeded();

        createCaseBtns.get(0).click();
        inputCaseName.type(title);
        inputCaseName.sendKeys(Keys.ENTER);

        inputCaseName.waitUntilElementDisappear(3);
    }

    public int getDataIndex() {
        return Integer.valueOf(getRootExtendedElement().getAttribute("data-index"));
    }

    public void copyId() {
        log.info("Attempting to copy ID...");

        suiteTitle.hover();
        suiteActions.clickCopyId();
    }

    public String copyLink() {
        log.info("Attempting to copy link...");

        suiteTitle.hover();
        suiteActions.clickCopyLink();

        return getClipboardText();
    }

    public CreateOrEditSuiteModal edit() {
        log.info("Attempting to edit test suite...");

        suiteTitle.hover();
        suiteActions.clickEdit();

        return new CreateOrEditSuiteModal(getDriver());
    }
}
