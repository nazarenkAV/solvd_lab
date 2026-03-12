package com.zebrunner.automation.gui.tcm.testsuite;

import com.zebrunner.automation.gui.tcm.repository.BaseRepositoryItem;
import com.zebrunner.carina.utils.mobile.IMobileUtils;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

// left part of test case repository
@Slf4j
public class TestSuitesTree extends AbstractUIObject implements IMobileUtils {
    public static final String ROOT_XPATH = "//div[contains(@class,'test-cases-suite-tree')]/parent::div";

    @FindBy(xpath = ".//*[text()='%s']/ancestor::" + BaseSuiteItem.ANCESTOR_XPATH)
    private BaseSuiteItem suiteItem;

    @FindBy(xpath = ".//" + BaseRepositoryItem.ANCESTOR_XPATH)
    private List<BaseRepositoryItem> suiteItemList;

    public TestSuitesTree(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public BaseSuiteItem getSuite(String name) {
        return format(suiteItem, name);
    }

    public TestSuitesTree selectSuite(String suiteName) {
        BaseSuiteItem suite = findTestSuite(suiteName);

        if (suite.isPresent(3)) {

            log.info("Suite {} was found!", suiteName);
            suite.click();
        } else
            throw new RuntimeException(String.format("Suite %s was not found!", suiteName));
        return this;
    }

    public List<BaseRepositoryItem> getSuiteItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//" + BaseSuiteItem.ANCESTOR_XPATH), 0), 7);
        return suiteItemList;
    }

    public BaseSuiteItem findTestSuite(String suiteName) {

        if (getSuiteItems().isEmpty()) {
            throw new NoSuchElementException("Test suites list is empty!!!");
        }

        int previousFirstIndex = -1;
        int previousLastIndex = -1;

        boolean isEndOfListAchieved = false;
        boolean isHeadOfListAchieved = false;

        int attempts = 0;

        int attemptsToSwipeUp = 1;
        int attemptsToSwipeDown = 1;

        BaseSuiteItem searchedTestSuite = getSuite(suiteName);

        while ((!searchedTestSuite.isPresent(3)) && !(isEndOfListAchieved && isHeadOfListAchieved) && attempts < 3) {

            searchedTestSuite = getSuite(suiteName);

            if (searchedTestSuite.isPresent(3)) {
                return searchedTestSuite;
            }

            // Swipe up until we find the desired test case, or until the last index stops changing, or until the number of attempts exceeds 20
            while (!searchedTestSuite.isPresent(3) || previousLastIndex != this.getLastItemIndex() || attemptsToSwipeUp < 21) {
                log.info("Swiping UP....");
                log.info("Attempt " + attemptsToSwipeUp);

                if (swipe(searchedTestSuite, this.getRootExtendedElement(), IMobileUtils.Direction.UP, 3)) {
                    searchedTestSuite = getSuite(suiteName);
                    return searchedTestSuite;
                }
                if (previousLastIndex == this.getLastItemIndex()) {

                    isEndOfListAchieved = true;
                    log.info("End of list with cases is achieved! ");
                    break;
                }

                previousLastIndex = this.getLastItemIndex();
                log.debug("Previous last item index is" + previousLastIndex);

                attemptsToSwipeUp++;
            }

            // Swipe down until we find the desired test case, or until the last index stops changing, or until the number of attempts exceeds 20
            while (!searchedTestSuite.isPresent(3) || previousFirstIndex != this.getFirstItemIndex() || attemptsToSwipeDown < 21) {
                log.info("Swiping Down ...");
                log.info("Attempt " + attemptsToSwipeDown);

                if (swipe(searchedTestSuite, this.getRootExtendedElement(), IMobileUtils.Direction.DOWN, 3)) {

                    searchedTestSuite = getSuite(suiteName);
                    return searchedTestSuite;
                }
                if (previousFirstIndex == this.getFirstItemIndex()) {

                    isHeadOfListAchieved = true;

                    log.info("Head of list with cases is achieved! ");
                    break;
                }
                previousFirstIndex = this.getFirstItemIndex();

                log.debug("Previous first item index is " + previousFirstIndex);

                attemptsToSwipeDown++;
            }
            attempts++;
        }

        return searchedTestSuite;
    }

    public int getLastItemIndex() {
        List<BaseRepositoryItem> items = getSuiteItems();
        if (!items.isEmpty()) {
            int lastItemIndex = items.get(items.size() - 1).getDataIndex();

            log.info("Last existing item index in list " + lastItemIndex);
            return lastItemIndex;
        } else {
            log.warn("Repository list is empty!");
            return 0;
        }
    }

    public int getFirstItemIndex() {
        List<BaseRepositoryItem> items = getSuiteItems();

        if (!items.isEmpty()) {
            int firstItemIndex = items.get(0).getDataIndex();

            log.info("Fist existing item index in list " + firstItemIndex);
            return firstItemIndex;
        } else {
            log.warn("Repository list is empty!");
            return 0;
        }
    }
}
