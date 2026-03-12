package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Point;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.reporting.domain.IssueReference;
import com.zebrunner.automation.api.reporting.domain.request.v1.FinishTestRequest;
import com.zebrunner.automation.api.reporting.service.ApiHelperService;
import com.zebrunner.automation.api.reporting.service.ApiHelperServiceImpl;
import com.zebrunner.automation.api.reporting.service.TestRunServiceAPIImplV1;
import com.zebrunner.automation.api.reporting.service.TestServiceV1Impl;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.reporting.dashboard.MainDashboardsPageR;
import com.zebrunner.automation.gui.reporting.widget.BaseWidget;
import com.zebrunner.automation.gui.reporting.widget.CanvasWidget;
import com.zebrunner.automation.gui.reporting.widget.CreateWidgetModal;
import com.zebrunner.automation.gui.reporting.widget.SendByEmailWindow;
import com.zebrunner.automation.gui.reporting.widget.TableWidget;
import com.zebrunner.automation.gui.reporting.widget.WidgetsPage;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.util.ComponentUtil;

@Maintainer("obabich")
@Slf4j
public class WidgetsPageTest extends LogInBase {

    private static final String widgetTemplateName = "PASS RATE (BAR)";

    private final ApiHelperService apiHelperService = new ApiHelperServiceImpl();
    private final TestRunServiceAPIImplV1 testRunServiceAPIImplV1 = new TestRunServiceAPIImplV1();
    private final TestServiceV1Impl testService = new TestServiceV1Impl();

    private String projectKey;
    private Project project;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
        projectKey = project.getKey();
    }

    @BeforeMethod
    public void closePopupOrDropDownListsBeforeTest() {
        ComponentUtil.closeAnyMenuOrModal(getDriver());
    }

    @AfterMethod
    public void closePopupOrDropDownListsAfterTest() {
        ComponentUtil.closeAnyMenuOrModal(getDriver());
    }

    @Test(groups = "min_acceptance")
    public void generalWidgetsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WIDGETS);
        String flakyTest = "Flaky test";
        FinishTestRequest finishTestRequest =
                FinishTestRequest.getRequestWithReason("Java.lang.AssertionError: The following asserts failed: Bla, bla, bla...");

        Long launchId = testRunServiceAPIImplV1.startTestRunWithName(projectKey.toUpperCase(), projectKey.toUpperCase());
        Long testId = testService.startTestWithMethodName(launchId, flakyTest);

        testService.finishTestAsResult(launchId, testId, finishTestRequest);
        testRunServiceAPIImplV1.finishTestRun(launchId);

        Long launchId1 = testRunServiceAPIImplV1.startTestRunWithName(projectKey.toUpperCase(), projectKey.toUpperCase());
        Long testId1 = testService.startTestWithMethodName(launchId1, flakyTest);
        testService.finishTestAsResult(launchId1, testId1, finishTestRequest);
        apiHelperService.linkIssueToTest(testId1, IssueReference.Type.JIRA, "ZEB-123");
        testRunServiceAPIImplV1.finishTestRun(launchId1);

        String expectedTitle = "General";
        List<String> expectedCanvasTitles = List.of("PASS RATE", "EXECUTION TIME (HOURS)", "NEW CASES (COVERAGE)",
                "PASS RATE BY OWNER (%)");

        int expectedNumberOfCanvasWidgets = 4;

        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        WidgetsPage generalWidgetsPage = mainDashboardsPage.toWidgetsPage(expectedTitle);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(generalWidgetsPage.isSendByEmailButtonPresent(), "Can't find send by email button");
        softAssert.assertEquals(generalWidgetsPage.getTitle(), expectedTitle);
        softAssert.assertFalse(generalWidgetsPage.isNewWidgetButtonPresent(), "New widget button should not be visible");
        softAssert.assertFalse(generalWidgetsPage.isEditButtonPresent(), "Edit button should not be visible");

        pause(2);
        for (String title : expectedCanvasTitles) {
            softAssert.assertTrue(generalWidgetsPage.isThereCertainCanvasWidget(title), "Can't find canvas widget with title " + title);
        }

        List<CanvasWidget> canvasWidgets = generalWidgetsPage.getCanvasWidgets();
        canvasWidgets.forEach(el -> softAssert.assertTrue(el.isWidgetVisible(), "Can't find body for widget " + el.getTitle()));
        softAssert.assertEquals(canvasWidgets.size(), expectedNumberOfCanvasWidgets,
                "Expected to find " + expectedNumberOfCanvasWidgets + " widgets");

        List<TableWidget> tableWidgets = generalWidgetsPage.getTableWidgets();
        softAssert.assertTrue(tableWidgets.size() == 3, "Should be only one table widget at general page");

        List<BaseWidget> baseWidgets = generalWidgetsPage.getAllWidgets();

        for (BaseWidget baseWidget : baseWidgets) {
            String expectedTitleText = generalWidgetsPage.getTitle() + " dashboard - " + baseWidget.getTitle() + " widget";
            String expectedDescription = "This is auto-generated email, please do not reply!";

            log.info("Verifying widget:  " + baseWidget.getTitle());

            SendByEmailWindow sendByEmailWindow = baseWidget.openSendByEmailWindow();
            softAssert.assertEquals(sendByEmailWindow.getModalTitle()
                                                     .getText(), SendByEmailWindow.MODAL_TITLE, "Modal title is not as expected!");
            softAssert.assertEquals(sendByEmailWindow.getNameInputValue(), expectedTitleText, "Name is not as expected!");
            softAssert.assertEquals(sendByEmailWindow.getDescription(), expectedDescription, "Description is not as expected!");
            softAssert.assertTrue(sendByEmailWindow.isAddEmailFieldVisible(), "Can't find email input field");
            softAssert.assertFalse(sendByEmailWindow.isSendButtonPresent(), "Send button should be inactive");
            sendByEmailWindow.clickCancel();
        }

        if (!tableWidgets.isEmpty()) {
            TableWidget tableWidget = generalWidgetsPage.getTabletWidgetByName("USERS TREND");
            List<String> headerValues = tableWidget.getHeader();

            List<String> expectedHeaderValues = List.of("NAME", "PASS", "FAIL", "DEFECT");
            expectedHeaderValues.forEach(expected -> softAssert.assertTrue(headerValues.stream()
                                                                                       .anyMatch(expected::equals),
                    "Can't find expected header title " + expected));
        }
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    public void widgetActionsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WIDGETS);

        SoftAssert softAssert = new SoftAssert();
        String titleOfDashboard = "Dashboard ".concat(RandomStringUtils.randomAlphabetic(6));
        String widgetName = "Widget ".concat(RandomStringUtils.randomAlphabetic(6));

        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        WidgetsPage widgetsPage = mainDashboardsPage
                .addDashboard(titleOfDashboard)
                .addNewWidgetFromTemplates(widgetName, widgetTemplateName);

        BaseWidget newWidget = widgetsPage.getWidgetByName(widgetName);
        Point startingPoint = newWidget.getLocation();

        widgetsPage.clickWidgetPlacement();
        /** commented out these checks because movement doesn't work on esg, only works on localhost **/
//        pause(WebConstant.TIME_TO_LOAD_HEAVY_ELEMENT);
//        newWidget.moveWidget(500, 0);
//        pause(WebConstant.TIME_TO_LOAD_HEAVY_ELEMENT);
        softAssert.assertTrue(widgetsPage.isApplyActive(), "Apply button should be active!");
        softAssert.assertTrue(widgetsPage.isCancelActive(), "Cancel button should be active!");
        widgetsPage.clickApply();

//        softAssert.assertTrue(widgetsPage.waitIsPopUpMessageAppear(MessageEnum.WIDGET_POSITIONS_WERE_UPDATED.getDescription()),
//                "Popup message is not as expected!");
//        Point pointAfterMovement = newWidget.getRootElement().getLocation();
//        softAssert.assertNotEquals(startingPoint, pointAfterMovement, "Location was not changed!");

        String newName = "NEW ".concat(widgetName);
        newWidget.editWidget(newName, "description");

        softAssert.assertEquals(widgetsPage.getPopUp(), MessageEnum.WIDGET_UPDATED.getDescription(),
                "Popup message is not as expected!");
        widgetsPage.assertPageOpened();
        softAssert.assertEquals(newWidget.getTitle(), newName.toUpperCase(Locale.ROOT),
                "Updated title is not as expected!");
        newWidget.removeWidget();
        softAssert.assertTrue(widgetsPage.waitIsPopUpMessageAppear(MessageEnum.WIDGET_DELETED.getDescription()),
                "Popup message is not as expected!");
        softAssert.assertFalse(widgetsPage.isThereCertainBaseWidget(widgetName), "Widget was found! ");
        softAssert.assertAll();
    }

    @Test(groups = "min_acceptance")
    public void createWidgetTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.WIDGETS);
        Label.attachToTest(TestLabelsConstant.WIDGETS, TestLabelsConstant.CREATION);

        SoftAssert softAssert = new SoftAssert();
        String titleOfDashboard = "Dashboard_".concat(RandomStringUtils.randomAlphabetic(6));
        String widgetName = "Widget_".concat(RandomStringUtils.randomAlphabetic(6));

        MainDashboardsPageR mainDashboardsPage = MainDashboardsPageR.openPageDirectly(getDriver(), project.getKey());
        WidgetsPage widgetsPage = mainDashboardsPage.addDashboard(titleOfDashboard);
        softAssert.assertTrue(widgetsPage.isNewWidgetButtonPresent(), "'New widget' button should be present on Widget page!");

        CreateWidgetModal createWidgetForm = widgetsPage.newWidgetClick();
        softAssert.assertEquals(createWidgetForm.getHeader()
                                                .getTitleText(), CreateWidgetModal.TITLE_PAGE_1, "Modal title is not as expected!");
        softAssert.assertFalse(createWidgetForm.isNextButtonActive(), "'Next' button should be inactive!");
        createWidgetForm.choseWidgetTemplate(widgetTemplateName);
        createWidgetForm.clickNext();
        softAssert.assertEquals(createWidgetForm.getHeader()
                                                .getTitleText(), CreateWidgetModal.TITLE_PAGE_2, "Modal title is not as expected!");
        softAssert.assertTrue(createWidgetForm.isNextButtonActive(), "'Next' button should be active!");
        softAssert.assertTrue(createWidgetForm.isBackButtonActive(), "'Back' button should be active!");
        createWidgetForm.clickNext();
        softAssert.assertEquals(createWidgetForm.getHeader()
                                                .getTitleText(), CreateWidgetModal.TITLE_PAGE_3, "Modal title is not as expected!");
        softAssert.assertTrue(createWidgetForm.isBackButtonActive(), "'Back' button should be active!");
        softAssert.assertFalse(createWidgetForm.getSubmitButton().isStateMatches(Condition.VISIBLE_AND_CLICKABLE),
                "'Add' button should be inactive!");
        createWidgetForm.typeName(widgetName);
        createWidgetForm.typeDescription(widgetName.concat("_description"));
        softAssert.assertTrue(createWidgetForm.getSubmitButton()
                                              .isStateMatches(Condition.VISIBLE_AND_CLICKABLE), "'Add' button should be active!");
        createWidgetForm.submitModal();
        softAssert.assertTrue(widgetsPage.waitIsPopUpMessageAppear(MessageEnum.WIDGET_CREATED.getDescription()),
                "Popup message is not as expected!");

        softAssert.assertTrue(widgetsPage.isCertainWidgetPresentOnDashboard(widgetName));
        softAssert.assertAll();
    }

}
