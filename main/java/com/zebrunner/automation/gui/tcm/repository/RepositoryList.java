package com.zebrunner.automation.gui.tcm.repository;

import com.zebrunner.automation.gui.tcm.testcase.RepositoryCaseItem;
import com.zebrunner.automation.util.WaitUtil;
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

// right part of test case repository
@Slf4j
public class RepositoryList extends AbstractUIObject implements IMobileUtils {
    public static final String ROOT_XPATH = "//div[contains(@class,'repository-suite-list ')]";

    @FindBy(xpath = ".//*[text()='%s']//ancestor::" + BaseRepositoryItem.ANCESTOR_XPATH)
    private BaseRepositoryItem repositoryItem;

    @FindBy(xpath = ".//*[text()='%s']//ancestor::" + BaseRepositoryItem.ANCESTOR_XPATH)
    private RepositoryCaseItem testCaeItem;

    @FindBy(xpath = ".//" + RepositoryCaseItem.ANCESTOR_XPATH)
    private List<BaseRepositoryItem> repositoryItemList;

    @FindBy(xpath = ".//" + RepositoryCaseItem.ROOT_XPATH)
    private List<RepositoryCaseItem> repositoryTestCases;


    public RepositoryList(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public BaseRepositoryItem getSuite(String name) {
        return format(repositoryItem, name);
    }

    public RepositoryCaseItem getTestCase(String name) {
        return format(testCaeItem, name);
    }

    public int getLastItemIndex() {
        List<BaseRepositoryItem> items = getRepositoryItems();
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
        List<BaseRepositoryItem> items = getRepositoryItems();

        if (!items.isEmpty()) {
            int firstItemIndex = items.get(0).getDataIndex();

            log.info("Fist existing item index in list " + firstItemIndex);
            return firstItemIndex;
        } else {
            log.warn("Repository list is empty!");
            return 0;
        }
    }

    public List<BaseRepositoryItem> getRepositoryItems() {
        waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//" + RepositoryCaseItem.ANCESTOR_XPATH), 0), 7);
        return repositoryItemList;
    }

    public RepositoryCaseItem findTestCase(String caseNameOrKey) {

        if (this.getRepositoryItems().isEmpty()) {
            throw new NoSuchElementException("Repository list is empty!!!");
        }

        int previousFirstIndex = -1;
        int previousLastIndex = -1;

        boolean isEndOfListAchieved = false;
        boolean isHeadOfListAchieved = false;

        int attempts = 0;

        int attemptsToSwipeUp = 1;
        int attemptsToSwipeDown = 1;

        RepositoryCaseItem searchedTestCase = this.getTestCase(caseNameOrKey);

        while ((!searchedTestCase.isPresent(3)) && !(isEndOfListAchieved && isHeadOfListAchieved) && attempts < 3) {

            searchedTestCase = this.getTestCase(caseNameOrKey);

            if (searchedTestCase.isPresent(3)) {
                return searchedTestCase;
            }

            // Swipe up until we find the desired test case, or until the last index stops changing, or until the number of attempts exceeds 30
            while (!searchedTestCase.isPresent(3) || previousLastIndex != this.getLastItemIndex() || attemptsToSwipeUp < 31) {
                log.info("Swiping UP....");
                log.info("Attempt " + attemptsToSwipeUp);

                if (swipe(searchedTestCase, this.getRootExtendedElement(), IMobileUtils.Direction.UP, 3)) {
                    searchedTestCase = this.getTestCase(caseNameOrKey);
                    return searchedTestCase;
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

            // Swipe down until we find the desired test case, or until the last index stops changing, or until the number of attempts exceeds 30
            while (!searchedTestCase.isPresent(3) || previousFirstIndex != this.getFirstItemIndex() || attemptsToSwipeDown < 31) {
                log.info("Swiping Down ...");
                log.info("Attempt " + attemptsToSwipeDown);

                if (swipe(searchedTestCase, this.getRootExtendedElement(), IMobileUtils.Direction.DOWN, 3)) {

                    searchedTestCase = this.getTestCase(caseNameOrKey);
                    return searchedTestCase;
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

        return searchedTestCase;
    }

    public List<RepositoryCaseItem> getAllTestCases() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), ".//" + RepositoryCaseItem.ROOT_XPATH);
        return repositoryTestCases;
    }
}
