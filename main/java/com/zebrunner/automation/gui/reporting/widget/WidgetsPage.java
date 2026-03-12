package com.zebrunner.automation.gui.reporting.widget;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.automation.gui.reporting.dashboard.NewDashboardModal;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;

@Slf4j
public class WidgetsPage extends TenantProjectBasePage {

    public static final String PAGE_NAME = "Widgets";

    @Getter
    @FindBy(xpath = "//canvas//ancestor::" + BaseWidget.ROOT_ELEMENT)
    private List<CanvasWidget> canvasWidgets;

    @Getter
    @FindBy(xpath = "//table//ancestor::" + BaseWidget.ROOT_ELEMENT)
    private List<TableWidget> tableWidgets;

    @Getter
    @FindBy(xpath = "//" + BaseWidget.ROOT_ELEMENT)
    private List<BaseWidget> allWidgets;

    @FindBy(xpath = "//button[text()='Widget']")
    @CaseInsensitiveXPath
    private ExtendedWebElement newWidgetButton;

    @FindBy(xpath = "//div[@aria-label='Edit']//button")
    private Element editButton;

    @FindBy(xpath = "//button[text()='Cancel']")
    private Element cancelButton;

    @FindBy(xpath = "//button[text()='Apply']")
    private Element applyButton;

    @FindBy(xpath = "//div[@aria-label='Widget placement']//button")
    private Element widgetPlacementButton;

    @FindBy(xpath = "//div[@aria-label='Send by email']//button")
    private Element sendByEmailButton;

    @FindBy(xpath = "//li[@class='MuiBreadcrumbs-li']/a[text()='Dashboards']")
    private ExtendedWebElement uiLoadedMarker;

    public WidgetsPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static WidgetsPage getInstance(WebDriver driver) {
        return new WidgetsPage(driver);
    }

    public void editDashboard(String newDashboardName) {
        editButton.click();
        NewDashboardModal newDashboardModal = new NewDashboardModal(getDriver());
        newDashboardModal.getDashboardNameInput().sendKeys(newDashboardName);
        newDashboardModal.getSubmitButton().click();
    }

    public boolean isEditButtonPresent() {
        return editButton.isStateMatches(Condition.VISIBLE);
    }

    public boolean isNewWidgetButtonPresent() {
        return newWidgetButton.isVisible(2);
    }

    public boolean isSendByEmailButtonPresent() {
        return sendByEmailButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isThereCertainCanvasWidget(String name) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(
                canvasWidgets,
                canvasWidget -> canvasWidget.getTitle().equalsIgnoreCase(name)
        );
    }

    public boolean isThereCertainTableWidget(String name) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(
                tableWidgets,
                tableWidget -> tableWidget.getTitle().equalsIgnoreCase(name)
        );
    }

    public boolean isThereCertainBaseWidget(String name) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(
                allWidgets,
                baseWidget -> baseWidget.getTitle().equalsIgnoreCase(name)
        );
    }

    public WidgetsPage addNewWidgetFromTemplates(String name, String templateName) {
        newWidgetButton.click();
        CreateWidgetModal createWidgetModal = new CreateWidgetModal(getDriver());
        createWidgetModal.choseWidgetTemplate(templateName)
                         .clickNext()
                         .clickNext()
                         .typeName(name)
                         .submitModal();
        return new WidgetsPage(driver);
    }

    public BaseWidget getWidgetByName(String widgetName) {
        return WaitUtil.waitElementAppearedInListByCondition(
                allWidgets,
                widget -> widget.getTitle().equalsIgnoreCase(widgetName),
                "Widget with name " + widgetName + " was found",
                "There are no widget with name: " + widgetName
        );

    }

    public TableWidget getTabletWidgetByName(String widgetName) {
        return WaitUtil.waitElementAppearedInListByCondition(
                tableWidgets,
                widget -> widget.getTitle().equalsIgnoreCase(widgetName),
                "Widget with name " + widgetName + " was found",
                "There are no widget with name: " + widgetName
        );
    }

    public void clickWidgetPlacement() {
        widgetPlacementButton.click();
    }

    public Boolean isCancelActive() {
        return cancelButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public Boolean isApplyActive() {
        return applyButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public void clickApply() {
        applyButton.click();
    }

    public CreateWidgetModal newWidgetClick() {
        newWidgetButton.click();
        return new CreateWidgetModal(getDriver());
    }

    public boolean isCertainWidgetPresentOnDashboard(String widgetName) {
        super.pause(3);
        for (BaseWidget widget : allWidgets) {
            if (widget.getTitle().equalsIgnoreCase(widgetName)) {
                return true;
            }
        }
        return false;
    }

}
