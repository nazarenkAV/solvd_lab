package com.zebrunner.automation.gui.reporting.dashboard;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.reporting.widget.WidgetsPage;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class DashboardCardR extends AbstractUIObject {

    public final static String ROOT_XPATH = "//tr[contains(@class,'MuiTableRow-root') and not(contains(@class,'MuiTableRow-head'))]";

    @FindBy(xpath = ".//a[@class='dashboards-table__link']")
    private Element dashboardName;

    @FindBy(xpath = ".//td[2]")
    private Element dashboardCreatedDate;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.PENCIL + "']//ancestor::button")
    private Element dashboardEdit;

    @FindBy(xpath = ".//*[@d='" + SvgPaths.TRASH_BIN + "']//ancestor::button")
    private Element dashboardDelete;

    public DashboardCardR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getDashboardName() {
        return dashboardName.getText().trim();
    }

    public String getCreatedDate() {
        return dashboardCreatedDate.getText().trim();
    }

    public boolean isCreatedDatePresent() {
        return dashboardCreatedDate.isStateMatches(Condition.VISIBLE);
    }

    public Boolean isEditButtonPresent() {
        return dashboardEdit.isStateMatches(Condition.CLICKABLE);
    }

    public Boolean isDeleteButtonPresent() {
        return dashboardDelete.isStateMatches(Condition.CLICKABLE);
    }

    public void clickDeleteDashboardButton() {
        dashboardDelete.click();
    }

    public WidgetsPage toWidgetsPage() {
        dashboardName.click();

        WidgetsPage widgetsPage = WidgetsPage.getInstance(getDriver());
        widgetsPage.assertPageOpened();

        return widgetsPage;
    }

}
