package com.zebrunner.automation.gui.reporting.dashboard;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.reporting.widget.WidgetsPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
public class MainDashboardsPageR extends TenantProjectBasePage {

    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/automation-dashboards";
    public static final String PAGE_NAME = "Dashboards";

    @FindBy(xpath = DashboardCardR.ROOT_XPATH)
    private List<DashboardCardR> dashboardCards;

    @CaseInsensitiveXPath
    @FindBy(xpath = "//span[text()='Dashboard']/ancestor::button")
    private Element addDashboardButton;

    @FindBy(xpath = "//input[@placeholder='Search dashboards']")
    private Element search;

    @FindBy(xpath = "//th[contains(@class,'table-header-cell _title')]//span")
    private ExtendedWebElement dashboardsTableColName;

    @FindBy(xpath = "//th[contains(@class,'table-header-cell _date')]//span")
    private ExtendedWebElement dashboardsTableColCreationDate;

    public MainDashboardsPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(dashboardsTableColCreationDate);
    }

    public static MainDashboardsPageR openPageDirectly(WebDriver driver, String projectKey) {
        MainDashboardsPageR dashboardsPage = new MainDashboardsPageR(driver);
        dashboardsPage.openURL(String.format(PAGE_URL, projectKey));
        dashboardsPage.assertPageOpened();
        return dashboardsPage;
    }

    public static MainDashboardsPageR getInstance(WebDriver driver) {
        return new MainDashboardsPageR(driver);
    }

    public WidgetsPage addDashboard(String dashboardName) {
        log.info("Trying to create dashboard " + dashboardName + "");
        addDashboardButton.click();
        NewDashboardModal newDashboardModal = new NewDashboardModal(getDriver());
        newDashboardModal.getDashboardNameInput().sendKeys(dashboardName);
        newDashboardModal.getSubmitButton().click();
        log.info("Dashboard  " + dashboardName + " was created");
        WidgetsPage widgetsPage = WidgetsPage.getInstance(getDriver());
        widgetsPage.assertPageOpened();
        return widgetsPage;
    }

    public List<DashboardCardR> searchDashboard(String dashboardName) {
        search.sendKeys(dashboardName);
        log.info("Dashboard with name " + dashboardName + " was found!");
        pause(4);
        return dashboardCards;
    }

    public void deleteDashboard(String dashboardName) {
        log.debug("Waiting for dashboardCards list to load...");

        DashboardCardR dashboardCard = WaitUtil.waitElementAppearedInListByCondition(dashboardCards,
                card -> card.getDashboardName().equalsIgnoreCase(dashboardName),
                "Dashboard with name " + dashboardName + " was found",
                "Dashboard with name " + dashboardName + " was not found");

        dashboardCard.clickDeleteDashboardButton();
        DeleteDashboardModal deleteDashboardModal = new DeleteDashboardModal(getDriver());
        deleteDashboardModal
                .getDeleteButton()
                .waitUntil(Condition.CLICKABLE)
                .click();
        log.info("Dashboard with name " + dashboardName + " was deleted!");
        return;

    }

    public DashboardCardR getDashboardByName(String dashboardName) {
        log.debug("Waiting for dashboardCards list to load...");
        return WaitUtil.waitElementAppearedInListByCondition(dashboardCards,
                dashboardCard -> dashboardCard.getDashboardName().equalsIgnoreCase(dashboardName),
                "Found dashboard with name " + dashboardName + " !",
                "Can't find dashboard with name " + dashboardName + " !"
        );
    }

    public List<DashboardCardR> getDashboardCards() {
        log.debug("Waiting for dashboardCards list to load...");
        WaitUtil.waitCheckListIsNotEmpty(dashboardCards);
        return dashboardCards;
    }

    public Boolean isDashboardPresentOnMainPage(String dashboardName) {
        for (DashboardCardR dashboardCard : dashboardCards) {
            if (dashboardCard.getDashboardName().equalsIgnoreCase(dashboardName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSearchPresentAndClickable() {
        return search.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isAddDashboardButtonPresentAndClickable() {
        return addDashboardButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public String getColumnTitleForDashboardName() {
        pause(4);
        String dashboardNameColon = dashboardsTableColName.getText().trim();
        log.info("Dashboard name column name is  " + dashboardNameColon);
        return dashboardNameColon;
    }

    public String getColumnTitleForCreationDate() {
        pause(4);
        String creationDate = dashboardsTableColCreationDate.getText().trim();
        log.info("Creation date column name is  " + creationDate);
        return creationDate;
    }

    public WidgetsPage toWidgetsPage(String dashboardName) {
        return getDashboardByName(dashboardName).toWidgetsPage();
    }
}
