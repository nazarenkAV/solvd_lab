package com.zebrunner.automation.gui.tcm.testsuite;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

// right part suite item
@Slf4j
public class BaseSuiteItem extends AbstractUIObject {
    public static final String ANCESTOR_XPATH = "div[@data-index]";

    @FindBy(xpath = ".//*[contains(@class,'repository-tree-node-arrow')]")
    private Element arrowIcon;

    @FindBy(xpath = ".//div[@class='repository-tree-node-delimiter']")
    private List<ExtendedWebElement> delimiters;

    @FindBy(xpath = ".//*[contains(@class,'repository-tree-node-title')]")
    private Element title;

    @FindBy(xpath = ".//*[contains(@class,'repository-tree-node-cases-count')]")
    private Element count;

    @FindBy(xpath = "./following-sibling::*")
    private List<SubSuiteItem> subSuiteItems;

    @FindBy(xpath = "./preceding-sibling::*")
    private List<SuiteItem> parentSuiteItems;

    public BaseSuiteItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getSuiteName() {
        return title.getText();
    }

    public String getSubSuitesCount() {
        return count.getText();
    }

    public int getDelimitersCount() {
        log.info(delimiters.size() + " for suite with name " + getSuiteName());
        return delimiters.size();
    }

    public List<SubSuiteItem> getSubSuites() {
        expandChildIfNeeded();
        subSuiteItems = subSuiteItems.stream()
                .takeWhile(subSuiteItem -> {
                            expandChildIfNeeded();
                            return subSuiteItem.getDelimitersCount() > this.getDelimitersCount();
                        }
                )
                .collect(Collectors.toList());
        return subSuiteItems;

    }

    public boolean isExpanded() {
        return arrowIcon.isPresent(1) && arrowIcon.getAttributeValue("class").contains("expand-icon");
    }

    public void expandChildIfNeeded() {
        if (arrowIcon.isPresent(1) && !arrowIcon.getAttributeValue("class").contains("expand-icon")) {
            arrowIcon.click();
            log.info("Expanded suite with name " + this.getSuiteName());
            pause(1);
        }
    }

    public SuiteItem getParentSuite() {
        List<SuiteItem> parentsSuites =
                parentSuiteItems.stream()
                        .filter(subSuiteItem -> subSuiteItem.getDelimitersCount() < this.getDelimitersCount())
                        .collect(Collectors.toList());// get all parents
        log.info(parentsSuites.size() + "");
        return parentsSuites.get(parentsSuites.size() - 1);//get last parent

        //return all items which level less than this parent suite, even from other suites.
        // Now we have no way to get exactly subSuites from one suite.
    }

    public boolean isParentSuiteExist() {
        List<SuiteItem> parentsSuites =
                parentSuiteItems.stream()
                        .filter(subSuiteItem -> subSuiteItem.getDelimitersCount() < this.getDelimitersCount())
                        .collect(Collectors.toList());// get all parents
        log.info(parentsSuites.size() + "");
        return parentsSuites.size() > 0;//get last parent

        //return all items which level less than this parent suite, even from other suites.
        // Now we have no way to get exactly subSuites from one suite.
    }

    public List<SubSuiteItem> getSubSuites1() {
        expandChildIfNeeded();
        return subSuiteItems.stream()
                .takeWhile(subSuiteItem -> {
                            expandChildIfNeeded();
                            return subSuiteItem.getDelimitersCount() > this.getDelimitersCount();
                        }
                )
                .collect(Collectors.toList());
    }
}
