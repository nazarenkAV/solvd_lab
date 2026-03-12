package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.PaginationR;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
@Slf4j
public class TrashBinPage extends TenantProjectBasePage {
    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/test-cases/trash-bin";
    public static final String PAGE_TITLE = "Trash bin";

    @FindBy(xpath = TrashBinControlPanel.ROOT_XPATH)
    private TrashBinControlPanel trashBinControlPanel;

    @FindBy(xpath = TrashBinTestCaseCard.ROOT_XPATH)
    private List<TrashBinTestCaseCard> trashBinTestCaseCardList;

    @FindBy(xpath = TrashBinListAction.ROOT_XPATH)
    private TrashBinListAction trashBinListAction;

    @FindBy(xpath = "//div[@class = 'trash-bin-list__header']" + PaginationR.ROOT_XPATH)
    private PaginationR topPagination;

    public TrashBinPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(trashBinControlPanel);
    }

    public static TrashBinPage openPageDirectly(WebDriver driver, String projectKey) {
        log.info("Opening " + PAGE_TITLE + " page...");
        TrashBinPage TrashBinPage = new TrashBinPage(driver);
        TrashBinPage.openURL(String.format(PAGE_URL, projectKey));

        TrashBinPage.assertPageOpened();
        log.info(PAGE_TITLE + " page is opened!");

        return TrashBinPage;
    }

    public boolean isTestCasePresent(String testCaseTitle) {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), TrashBinTestCaseCard.ROOT_XPATH);
        return trashBinTestCaseCardList.stream()
                .map(TrashBinTestCaseCard::getTestCaseTitleText)
                .anyMatch(title -> title.trim().equals(testCaseTitle.trim()));
    }

    public TrashBinTestCaseCard findTestCase(String testCaseTitle) {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), TrashBinTestCaseCard.ROOT_XPATH);
        return getTrashBinTestCaseCardList().stream()
                .filter(card -> card.getTestCaseTitleText().trim().equals(testCaseTitle))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Test case with title '" + testCaseTitle + "' not found"));
    }

    public List<TrashBinTestCaseCard> getTrashBinTestCaseCardList() {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), TrashBinTestCaseCard.ROOT_XPATH);
        return trashBinTestCaseCardList;
    }
}
