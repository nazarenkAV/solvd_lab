package com.zebrunner.automation.gui.smoke;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.project.method.v1.ProjectAssignment;
import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.FilterCardSettings;
import com.zebrunner.automation.gui.common.FilterSemiWindow;
import com.zebrunner.automation.gui.common.SelectWrapperMenu;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.launch.LaunchCard;
import com.zebrunner.automation.gui.reporting.launch.TestRunResultPageR;
import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.legacy.MessageEnum;
import com.zebrunner.automation.legacy.PlatformTypeR;
import com.zebrunner.automation.legacy.PreparationUtil;
import com.zebrunner.automation.legacy.TestClassLaunchDataStorage;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.util.VideoDelimiterUtil;

@Slf4j
public class AutomationLaunchesFiltersTest extends LogInBase {
    private static final String env = "NEW";
    private final List<Long> launchIds = new ArrayList<>();

    private final String LOCALE_LABEL_KEY = "com.zebrunner.app/sut.locale";

    private final String US_LOCALE = "en_US";

    private final String FR_LOCALE = "fr_FR";

    private Project project;

    @BeforeClass
    public void getProject() {
        project = LogInBase.project;
    }

    @AfterMethod(alwaysRun = true)
    public void removeTestRun() {
        launchIds.forEach(launchId -> testRunService.deleteLaunch(project.getId(), launchId));
    }

    @AfterMethod
    public void delimiter(ITestResult result) {
        VideoDelimiterUtil.delimit(getDriver(), result, 3, "GMT+3:00");
    }

    private TestRunStatusEnumR getStatusFromDropdown(Dropdown.DropdownItemsEnum status) {
        switch (status) {
            case STATUS_FAILED:
                return TestRunStatusEnumR.FAILED;
            case STATUS_PASSED:
                return TestRunStatusEnumR.PASSED;
            case STATUS_IN_PROGRESS:
                return TestRunStatusEnumR.IN_PROGRESS;
            case STATUS_SKIPPED:
                return TestRunStatusEnumR.SKIPPED;
            default:
                return TestRunStatusEnumR.UNKNOWN;
        }
    }
    //    ___________________Filters test_________________

    @Test
    @TestCaseKey("ZTP-1214")
    public void filterTestRunsByBrowserTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        String testRunName = "testRun_tr_filters ".concat(project.getKey()).concat("_")
                                                  .concat(RandomStringUtils.randomAlphabetic(3));
        String methodName = "test_tr_filters".concat(project.getKey()).concat("_")
                                             .concat(RandomStringUtils.randomAlphabetic(7));
        PlatformTypeR browser = PlatformTypeR.CHROME;
        PlatformTypeR browser1 = PlatformTypeR.FIREFOX;

        long testRunId = testRunService.startTestRunWithName(project.getKey(), browser + "-" + testRunName);
        launchIds.add(testRunId);
        long tesId = testService.startTestWithMethodName(testRunId, methodName);
        apiHelperService.startSession(testRunId, Collections.singletonList(tesId), "", browser.value());
        testService.finishTestAsResult(testRunId, tesId, "PASSED");
        testRunService.finishTestRun(testRunId);

        long testRunId1 = testRunService.startTestRunWithName(project.getKey(), browser1 + "-" + testRunName);
        launchIds.add(testRunId1);
        long tesId1 = testService.startTestWithMethodName(testRunId1, methodName);
        apiHelperService.startSession(testRunId1, Collections.singletonList(tesId1), "", browser1.value());
        testService.finishTestAsResult(testRunId1, tesId1, "PASSED");
        testRunService.finishTestRun(testRunId1);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectBrowser(browser.value());
        pause(3);

        SoftAssert softAssert = new SoftAssert();

        automationLaunchesPage.getAllTestRunCards().forEach(testRunCard1 -> {
            log.debug("Checking test run with name " + testRunCard1.getCardName());
            softAssert.assertEquals(testRunCard1.getBrowser().getType(), browser,
                    "Browser should be " + browser.value());
        });

        automationLaunchesPage.getFilters().clickResetFilter();

        softAssert.assertAll();
    }


    @Test
    @TestCaseKey("ZTP-1213")
    public void filterTestRunsByPlatformTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        String testRunName = "testRun_tr_filters ".concat(project.getKey()).concat("_")
                                                  .concat(RandomStringUtils.randomAlphabetic(3));
        String methodName = "test_tr_filters".concat(project.getKey()).concat("_")
                                             .concat(RandomStringUtils.randomAlphabetic(7));
        PlatformTypeR platform = PlatformTypeR.ANDROID;
        PlatformTypeR platform1 = PlatformTypeR.LINUX;

        long testRunId = testRunService.startTestRunWithName(project.getKey(), testRunName);
        launchIds.add(testRunId);
        long tesId = testService.startTestWithMethodName(testRunId, methodName);
        apiHelperService.startSession(testRunId, Collections.singletonList(tesId), platform.value(), "");
        testService.finishTestAsResult(testRunId, tesId, "PASSED");
        testRunService.finishTestRun(testRunId);

        long testRunId1 = testRunService.startTestRunWithName(project.getKey(), platform1 + testRunName);
        launchIds.add(testRunId1);
        long tesId1 = testService.startTestWithMethodName(testRunId1, methodName);
        apiHelperService.startSession(testRunId1, Collections.singletonList(tesId1), platform1.value(), "");
        testService.finishTestAsResult(testRunId1, tesId1, "PASSED");
        testRunService.finishTestRun(testRunId1);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectPlatform(platform.value());
        pause(3);

        SoftAssert softAssert = new SoftAssert();

        automationLaunchesPage.getAllTestRunCards().forEach(testRunCard1 -> {
            log.debug("Checking test run with name " + testRunCard1.getCardName());
            softAssert.assertEquals(
                    testRunCard1.getPlatform().getPlatformType(), platform,
                    "Platform should be only " + platform);
        });

        automationLaunchesPage.getFilters().clickResetFilter();

        softAssert.assertAll();
    }

    @Test(groups = {"min_acceptance"})
    @TestCaseKey({"ZTP-1803", "ZTP-1807", "ZTP-1811", "ZTP-1184", "ZTP-1191", "ZTP-1211", "ZTP-1188", "ZTP-1190"})
    public void testRunFilterVerificationsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long trId = apiHelperService.startTR(project.getKey());
        launchIds.add(trId);
        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        int amountOfTestRunCardsBeforeFiltering = automationLaunchesPage.getAllTestRunCards().size();
        automationLaunchesPage.getFilters().selectEnv(env);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(
                automationLaunchesPage.getFilters().isSaveButtonPresent(),
                "'Save' button should be appeared!");
        softAssert.assertTrue(
                automationLaunchesPage.getFilters().isResetButtonPresent(),
                "'Reset' button should be appeared!");

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName = "filter Name ".concat(RandomStringUtils.randomAlphabetic(6));//ZTP-1811 - The filter name can contain spaces between characters
        filterSemiWindow.saveFilter(filterName);//ZTP-1211 - User is able to save as filter the search input

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName),
                "Filter with name " + filterName + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters()
                              .toSavedFilters(); //ZTP-1188 User is able to see the list of all saved filters via 'Show all saved filters'

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterName),
                String.format("Filter with name '%s' should present in list", filterName)
        );//ZTP-1803 - Creating and saving filters
        softAssert.assertEquals(
                filterSemiWindow.getFilterCard(filterName).getFilterStatusTooltip(),
                "environment:\n" + env, "Tooltip is not as expected"
        );//ZTP-1190 - User is able to see the tooltip with filter parameters when hovering to 'i' icon
        softAssert.assertTrue(
                filterSemiWindow.isCloseButtonPresent(),
                "'X' - Close button is not present!"
        );

        filterSemiWindow.closeSemiWindow();

//        softAssert.assertEquals(
//                automationLaunchesPage.getFilters().getActiveFilterName(),
//                filterName, "Active filter name is not as expected!");

        automationLaunchesPage.getAllTestRunCards().forEach(testRunCard -> softAssert.assertEquals(
                testRunCard.getEnvironment().getText(),
                env, String.format("Env should be %s !", env)));

        int sizeAfterApplyingFiltering = automationLaunchesPage.getAllTestRunCards().size();

        automationLaunchesPage.getFilters().clickResetFilter(); //ZTP-1807 - Resetting the selected filters

        softAssert.assertNotEquals(
                amountOfTestRunCardsBeforeFiltering,
                sizeAfterApplyingFiltering, "The number of test runs should be different");

        softAssert.assertFalse(
                automationLaunchesPage.getFilters().isSaveButtonPresent(),
                "'Save' button should be appeared!");

        softAssert.assertFalse(
                automationLaunchesPage.getFilters().isResetButtonPresent(),
                "'Reset' button should be appeared!");

        softAssert.assertFalse(
                automationLaunchesPage.getFilters().isActiveFilterNameVisible(),
                "Active filter name should not be visible!");

        FilterCardSettings filterCardSettings = automationLaunchesPage //ZTP-1191 - User is able to search items in a window with saved filters
                                                                       .getFilters()
                                                                       .toSavedFilters()
                                                                       .searchFilter(filterName)
                                                                       .openSettings();

        filterCardSettings.delete();//ZTP-1184 - User is able to delete saved filter

        int sizeAfterCancelingFiltering = automationLaunchesPage.getAllTestRunCards().size();

        softAssert.assertNotEquals(
                sizeAfterCancelingFiltering,
                sizeAfterApplyingFiltering, "The number of test runs should change after cancelling filtering!");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1809", "ZTP-1810", "ZTP-1186"})
    public void filterNameVerificationsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long trId = apiHelperService.startTR(project.getKey());
        launchIds.add(trId);
        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectEnv(env);

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName = RandomStringUtils.randomAlphabetic(1);
        filterSemiWindow.saveFilter(filterName);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName),
                "Filter with name " + filterName + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterName),
                String.format("Filter with name '%s' should present in list", filterName));//ZTP-1809 - The length of the filter name contains 1-256 characters

        filterSemiWindow.getFilterCard(filterName).clickFilterName();
        automationLaunchesPage.getFilters()
                              .selectPlatform("API");//ZTP-1186 - User is able to change filter parameters of applied saved filter and save a new filter

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName2 = RandomStringUtils.randomAlphabetic(256);
        filterSemiWindow.saveFilter(filterName2);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName2),
                "Filter with name " + filterName2 + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterName2),
                String.format("Filter with name '%s' should present in list", filterName2));// ZTP-1809 - The length of the filter name contains 1-256 characters

        filterSemiWindow.closeSemiWindow();

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectEnv(env);

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName3 = RandomStringUtils.randomAlphabetic(1, 256);
        filterSemiWindow.saveFilter(filterName3);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName3),
                "Filter with name " + filterName3 + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterName3),
                String.format("Filter with name '%s' should present in list", filterName3)); //ZTP-1809 - The length of the filter name contains 1-256 characters

        filterSemiWindow.closeSemiWindow();

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectEnv(env);

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName4 = "[|]’~<!--@/$%^&#/()?>,.*/"; //ZTP-1810 - The filter name can contain special characters
        filterSemiWindow.saveFilter(filterName4);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName4),
                "Filter with name " + filterName4 + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterName4),
                String.format("Filter with name '%s' should present in list", filterName4));

        filterSemiWindow.closeSemiWindow();
        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1181", "ZTP-1182", "ZTP-1183"})
    public void negativeCasesForFilterNameTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long trId = apiHelperService.startTR(project.getKey());
        launchIds.add(trId);
        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectEnv(env);

        softAssert.assertTrue(automationLaunchesPage.getFilters()
                                                    .isResetButtonPresent(), "Reset button is not present!");
        softAssert.assertTrue(automationLaunchesPage.getFilters().isSaveButtonPresent(), "Save button is not present!");

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName = RandomStringUtils.randomAlphabetic(257); //ZTP-1182 - User can't save a filter with the name over 256 characters
        filterSemiWindow.saveFilter(filterName);
        String filterName256 = filterName.substring(0, 256);

        softAssert.assertNotEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName),
                "Filter with name " + filterName + " is present! The name should be shortened to 256 characters."
        );
        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName256),
                "Filter with name " + filterName256 + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterName256),
                String.format("Filter with name '%s' should present in list", filterName256));//ZTP-1183 - User is able to create saved filter with valid input
        softAssert.assertTrue(filterSemiWindow.isFavouritedFilterFirstInList(filterName256),
                String.format("%s filter is not first in the list", filterName256));

        filterSemiWindow.closeSemiWindow();
        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectEnv(env);

        softAssert.assertTrue(automationLaunchesPage.getFilters()
                                                    .isResetButtonPresent(), "Reset button is not present!");
        softAssert.assertTrue(automationLaunchesPage.getFilters().isSaveButtonPresent(), "Save button is not present!");

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String sameNameFilter = "TestEnv";
        filterSemiWindow.saveFilter(sameNameFilter);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(sameNameFilter),
                "Filter with name " + sameNameFilter + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(sameNameFilter),
                String.format("Filter with name '%s' should present in list", sameNameFilter));
        softAssert.assertTrue(filterSemiWindow.isFavouritedFilterFirstInList(sameNameFilter),
                String.format("%s filter is not first in the list", sameNameFilter));

        filterSemiWindow.getFilterCard(sameNameFilter).clickFilterName();

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        filterSemiWindow.saveFilter(sameNameFilter);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_ALREADY_EXISTS.getDescription(sameNameFilter),
                "Filter with name " + sameNameFilter + " already exists, but it was created again."
        );//ZTP-1181 - User can't save a filter with the same name
        softAssert.assertEquals(
                filterSemiWindow.getFilterCardNameCount(sameNameFilter), 1,
                "Expected 1 filter with name " + sameNameFilter + ", but found more!"
        );

        automationLaunchesPage.waitPopupDisappears();
        filterSemiWindow.closeSemiWindow();

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1804", "ZTP-1189"})
    public void favoriteFilterTests() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long trId = apiHelperService.startTR(project.getKey());
        launchIds.add(trId);
        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectEnv(env);

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String firstFilter = "firstFilter".concat(RandomStringUtils.randomAlphabetic(6));
        filterSemiWindow.saveFilter(firstFilter);

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(firstFilter),
                String.format("Filter with name '%s' should present in list", firstFilter));

        pause(2);
        filterSemiWindow.closeSemiWindow();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertEquals(
                filterSemiWindow.getFilterCard(firstFilter).getStarColor(),
                ColorEnum.USUAL_FILTER_STAR.getHexColor(), String.format("Color for %s is not as expected! Should be transparent color!", firstFilter));

        filterSemiWindow.getFilterCard(firstFilter).clickFavouriteButton();//ZTP-1804 - Favorite filter
        pause(2);
        filterSemiWindow.closeSemiWindow();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertEquals(
                filterSemiWindow.getFilterCard(firstFilter).getStarColor(),
                ColorEnum.FAVOURITE_FILTER_STAR.getHexColor(), String.format("Color for %s is not as expected! Should be favourite color!", firstFilter));

        filterSemiWindow.getFilterCard(firstFilter).clickFilterName();

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String secondFilter = "secondFilter".concat(RandomStringUtils.randomAlphabetic(6));
        filterSemiWindow.saveFilter(secondFilter);

        automationLaunchesPage.getFilters().toSavedFilters();
        filterSemiWindow.getFilterCard(secondFilter).clickFavouriteButton();

        softAssert.assertEquals(
                filterSemiWindow.getFilterCard(secondFilter).getStarColor(),
                ColorEnum.FAVOURITE_FILTER_STAR.getHexColor(), String.format("Color for %s is not as expected! Should be favourite color!", secondFilter));
        softAssert.assertTrue(filterSemiWindow.isFavouritedFilterFirstInList(secondFilter), "Favourite filter is not first in the list");

        pause(2);
        filterSemiWindow.getFilterCard(secondFilter)
                        .clickFavouriteButton();//ZTP-1189 - User is able to make a saved filter a favorite

        softAssert.assertEquals(
                filterSemiWindow.getFilterCard(secondFilter).getStarColor(),
                ColorEnum.USUAL_FILTER_STAR.getHexColor(), String.format("Color for %s is not as expected! Should be transparent color!", secondFilter));
        softAssert.assertFalse(filterSemiWindow.isFavouritedFilterFirstInList(secondFilter), "Favourite filter is first! when it should not be! ");
        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(secondFilter),
                String.format("Filter with name '%s' should present in list", secondFilter));

        filterSemiWindow.getFilterCard(firstFilter).clickFavouriteButton();
        filterSemiWindow.closeSemiWindow();

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-1812")
    public void filterNameUniquenessTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        String testRunName = "testRun_tr_filters ".concat(project.getKey()).concat("_")
                                                  .concat(RandomStringUtils.randomAlphabetic(3));
        String methodName = "test_tr_filters".concat(project.getKey()).concat("_")
                                             .concat(RandomStringUtils.randomAlphabetic(7));
        PlatformTypeR browser = PlatformTypeR.CHROME;

        long testRunId = testRunService.startTestRunWithName(project.getKey(), browser + "-" + testRunName);
        launchIds.add(testRunId);
        long tesId = testService.startTestWithMethodName(testRunId, methodName);
        apiHelperService.startSession(testRunId, Collections.singletonList(tesId), "", browser.value());
        testService.finishTestAsResult(testRunId, tesId, "PASSED");
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectBrowser(browser.value());

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterNameChrome = "Chrome";
        filterSemiWindow.saveFilter(filterNameChrome);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterNameChrome),
                "Filter with name " + filterNameChrome + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(filterNameChrome),
                String.format("Filter with name '%s' should present in list", filterNameChrome));

        filterSemiWindow.closeSemiWindow();
        automationLaunchesPage.getFilters().clickResetFilter();

        String[] filterNames = {"Chrome ", "CHROME", "chrome"};  //ZTP-1812 - Can't create filters with the same names and criteria

        for (String filterName : filterNames) {
            automationLaunchesPage.getFilters().selectBrowser(browser.value());

            filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
            filterSemiWindow.saveFilter(filterName);

            softAssert.assertEquals(
                    automationLaunchesPage.getPopUp(),
                    MessageEnum.FILTER_ALREADY_EXISTS.getDescription(filterName),
                    "Filter with name " + filterName + " exists, but it was created again."
            );

            automationLaunchesPage.waitPopupDisappears();
            filterSemiWindow.closeSemiWindow();
            automationLaunchesPage.getFilters().clickResetFilter();
        }
        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey({"ZTP-1247", "ZTP-1340", "ZTP-1192"})
    public void resetFilterParametersTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        com.zebrunner.automation.api.reporting.domain.Label label1 = com.zebrunner.automation.api.reporting.domain.Label.builder()
                                                                                                                        .key("Platform")
                                                                                                                        .value("Zebrunner")
                                                                                                                        .build();
        com.zebrunner.automation.api.reporting.domain.Label label2 = com.zebrunner.automation.api.reporting.domain.Label.builder()
                                                                                                                        .key("Framework")
                                                                                                                        .value("Carina")
                                                                                                                        .build();
        ArrayList<com.zebrunner.automation.api.reporting.domain.Label> labels = new ArrayList<>(Arrays.asList(label1, label2));

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());
        apiHelperService.addLabelsToTest(testClassLaunchDataStorage.getLaunch().getId(),
                testClassLaunchDataStorage.getPassedTests().get(0).getId(), labels);

        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        String passedMethodName = testClassLaunchDataStorage.getPassedTests().get(0).getName();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                testRunResultPage.isPageOpened(),
                "Test run results page was not opened!");
        softAssert.assertTrue(testRunResultPage.getActionsBlockR().isSearchFieldPresent(),
                "Search text field was not presented");

        testRunResultPage.getActionsBlockR().searchTest(passedMethodName);
        softAssert.assertTrue(testRunResultPage.isTestPresent(passedMethodName),
                "Searched test was not presented");

        testRunResultPage.getActionsBlockR()
                         .clickResetButton();//ZTP-1192 - User is able to Reset filter parameters on a test run grid
        softAssert.assertTrue(testRunResultPage.getActionsBlockR().isSearchTextFieldClean(),
                "The search text value should be reset");

        Dropdown.DropdownItemsEnum[] statusesName = Dropdown.DropdownItemsEnum.getStatues();
        for (Dropdown.DropdownItemsEnum statusName : statusesName) {
            testRunResultPage.getActionsBlockR().selectSingleStatusAndClose(statusName);

            softAssert.assertTrue(testRunResultPage.getActionsBlockR().isStatusSelected(statusName),
                    "Status '" + statusName + "' was not selected properly");

            // Add reset verification for each status
            testRunResultPage.getActionsBlockR().clickResetButton();
            softAssert.assertTrue(testRunResultPage.getActionsBlockR().
                                                   isStatusSelected(Dropdown.DropdownItemsEnum.DEFAULT_STATUS),
                    "The status value should be reset");
            softAssert.assertTrue(testRunResultPage.getActionsBlockR()
                                                   .isGroupSelected(SelectWrapperMenu.WrapperItemEnum.DEFAULT_GROUP),
                    "The group by value should be reset");
        }

        SelectWrapperMenu.WrapperItemEnum[] groupNames = SelectWrapperMenu.WrapperItemEnum.getGroups();
        for (SelectWrapperMenu.WrapperItemEnum groupName : groupNames) {
            testRunResultPage.getActionsBlockR().openAndSelectGroup(groupName);

            softAssert.assertTrue(testRunResultPage.getActionsBlockR().isGroupSelected(groupName),
                    "Group '" + groupName + "' was not selected properly");

            // Add reset verification for each group
            testRunResultPage.getActionsBlockR().clickResetButton();
            softAssert.assertTrue(testRunResultPage.getActionsBlockR().
                                                   isStatusSelected(Dropdown.DropdownItemsEnum.DEFAULT_STATUS),
                    "The status value should be reset");
            softAssert.assertTrue(testRunResultPage.getActionsBlockR()
                                                   .isGroupSelected(SelectWrapperMenu.WrapperItemEnum.DEFAULT_GROUP),
                    "The group by value should be reset");
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("Gmamaladze")
    @TestCaseKey("ZTP-1254")
    public void searchTestsOnPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);

        TestClassLaunchDataStorage testClassLaunchDataStorage = PreparationUtil.startAndFinishLaunchWithTests(1, 1, project.getKey());
        launchIds.add(testClassLaunchDataStorage.getLaunch().getId());

        String failedMethodName = testClassLaunchDataStorage.getFailedTests().get(0).getName();
        String passedMethodName = testClassLaunchDataStorage.getPassedTests().get(0).getName();
        TestRunResultPageR testRunResultPage = new TestRunResultPageR(getDriver())
                .openPageDirectly(project.getKey(), testClassLaunchDataStorage.getLaunch().getId());

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                testRunResultPage.isPageOpened(),
                "Test run results page was not opened!");
        softAssert.assertTrue(testRunResultPage.getActionsBlockR().isSearchFieldPresent(),
                "Search text field was not presented");

        testRunResultPage.getActionsBlockR().searchTest(failedMethodName);

        softAssert.assertTrue(testRunResultPage.isTestPresent(failedMethodName),
                "Searched test was not presented");
        softAssert.assertFalse(testRunResultPage.isTestPresent(passedMethodName),
                "Un searched test case should not be presented");


        testRunResultPage.getActionsBlockR().searchTest("[|]’~<!--@/$%^&#/()?>,.*/ " +
                RandomStringUtils.randomNumeric(3));

        softAssert.assertEquals(testRunResultPage.getEmptyPlaceholder().getEmptyPlaceHolderTitle(),
                MessageEnum.NO_RESULT_MESSAGE.getDescription(),
                "Message about the absence of tests is not as expected");
        softAssert.assertTrue(testRunResultPage.getEmptyPlaceholder().isEmptyPlaceholderImagePresent(),
                "No test matching logo should be displayed");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-3693")
    public void verifySearchInFiltersIsResetAfterReopeningTheList() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long trId = apiHelperService.startTR(project.getKey());
        launchIds.add(trId);
        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        List<String> filterNames = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            automationLaunchesPage.getFilters().selectEnv(env);

            FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
            String filterName = "Filter" + i;
            filterSemiWindow.saveFilter(filterName);
            filterNames.add(filterName);

            automationLaunchesPage.waitPopupDisappears();
            automationLaunchesPage.getFilters().toSavedFilters();

            softAssert.assertTrue(
                    filterSemiWindow.isFilterPresentInList(filterName),
                    String.format("Filter with name '%s' should present in the list", filterName)
            );

            automationLaunchesPage.waitPopupDisappears();
            filterSemiWindow.closeSemiWindow();
            automationLaunchesPage.getFilters().clickResetFilter();
        }

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().toSavedFilters();
        String filterName = filterNames.get(2);
        filterSemiWindow.searchFilter(filterName).clickFilterName();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(filterSemiWindow.isSearchFieldEmpty(), "Search field is not empty BEFORE resetting filter!");

        filterSemiWindow.closeSemiWindow();

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(filterSemiWindow.isSearchFieldEmpty(), "Search field is not empty AFTER resetting filter!");

        filterSemiWindow.closeSemiWindow();

        for (String filterNameAfter : filterNames) {
            automationLaunchesPage.getFilters().toSavedFilters();

            softAssert.assertTrue(
                    filterSemiWindow.isFilterPresentInList(filterNameAfter),
                    String.format("Filter with name '%s' should be present in the list AFTER resetting filter!", filterNameAfter)
            );

            filterSemiWindow.closeSemiWindow();
        }

        softAssert.assertAll();

    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1808", "ZTP-1193"})
    public void clearSelectedFiltersTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        Dropdown.DropdownItemsEnum[] statusesName = Dropdown.DropdownItemsEnum.getStatues();
        for (Dropdown.DropdownItemsEnum statusName : statusesName) {
            if (statusName == Dropdown.DropdownItemsEnum.STATUS_FAILED_NO_LINKED_ISSUE) { //We don’t have such filter "Failed (no linked issue)" for launches page
                continue;
            }

            automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(statusName);

            softAssert.assertTrue(automationLaunchesPage.getActionsBlockR().isStatusSelected(statusName),
                    "Status '" + statusName + "' was not selected properly");
            softAssert.assertTrue(
                    automationLaunchesPage.getFilters().isSaveButtonPresent(),
                    "'Save' button should be appeared!");
            softAssert.assertTrue(
                    automationLaunchesPage.getFilters().isResetButtonPresent(),
                    "'Reset' button should be appeared!");

            automationLaunchesPage.getActionsBlockR().openStatusSettings();
            automationLaunchesPage.getActionsBlockR()
                                  .clickClearSelectionButton();//ZTP-1808 - Clearing the selected filters
            automationLaunchesPage.getActionsBlockR().closeSelectBox();

            softAssert.assertFalse(automationLaunchesPage.getActionsBlockR().isStatusSelected(statusName),
                    "Status '" + statusName + "' should not be selected after clearing!");//ZTP-1193 - Verify user is able to clear filter parameters
            softAssert.assertFalse(
                    automationLaunchesPage.getFilters().isSaveButtonPresent(),
                    "'Save' button should not be appeared!");
            softAssert.assertFalse(
                    automationLaunchesPage.getFilters().isResetButtonPresent(),
                    "'Reset' button should not be appeared!");
        }
        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-1216")
    public void verifyTestRunFilterByStatusesTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        SoftAssert softAssert = new SoftAssert();

        TestClassLaunchDataStorage failedTR = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        launchIds.add(failedTR.getLaunch().getId());

        TestClassLaunchDataStorage passedTR = PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(passedTR.getLaunch().getId());

        Launch inProgressTR = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        launchIds.add(inProgressTR.getId());

        Launch skippedTR = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        launchIds.add(skippedTR.getId());
        testRunService.finishLaunch(skippedTR.getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        Dropdown.DropdownItemsEnum[] statuses = {Dropdown.DropdownItemsEnum.STATUS_FAILED,
                Dropdown.DropdownItemsEnum.STATUS_PASSED, Dropdown.DropdownItemsEnum.STATUS_IN_PROGRESS,
                Dropdown.DropdownItemsEnum.STATUS_SKIPPED};

        for (Dropdown.DropdownItemsEnum status : statuses) {
            automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(status);

            List<LaunchCard> testRunCards = automationLaunchesPage.getAllTestRunCards();

            for (int i = 0; i < testRunCards.size(); i++) {
                pause(2);
                testRunCards = automationLaunchesPage.getAllTestRunCards();
                LaunchCard card = testRunCards.get(i);
                TestRunStatusEnumR expectedStatus = getStatusFromDropdown(status);

                softAssert.assertEquals(
                        card.getStatus().getStatusColourFromCss(),
                        expectedStatus,
                        "Card status is wrong. Should be " + expectedStatus
                );
            }
            automationLaunchesPage.getFilters().clickResetFilter();
        }

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-1217")
    public void verifyTestRunFilterByEnvironmentsTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        List<String> environmentNames = Arrays.asList(env, "PROD", "TEST", "DEV");

        environmentNames.forEach(environmentName -> {
            long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), environmentName);
            launchIds.add(testRunId);
            testRunService.finishTestRun(testRunId);
        });

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        List<LaunchCard> testRunCards = automationLaunchesPage.getAllTestRunCards();

        for (String environmentName : environmentNames) {
            automationLaunchesPage.getFilters().selectEnv(environmentName);
            testRunCards = automationLaunchesPage.getAllTestRunCards();

            for (int i = 0; i < testRunCards.size(); i++) {
                pause(2);
                testRunCards = automationLaunchesPage.getAllTestRunCards();
                LaunchCard card = testRunCards.get(i);

                softAssert.assertEquals(
                        card.getEnvTextValue(),
                        environmentName,
                        "Card environment is wrong. Should be " + environmentName
                );
            }
            automationLaunchesPage.getFilters().clickResetFilter();
        }

        automationLaunchesPage.getFilters().selectEnv(environmentNames.get(0));
        automationLaunchesPage.getFilters().selectEnvItem(environmentNames.get(1));

        testRunCards.forEach(card -> {
            pause(2);

            softAssert.assertTrue(
                    (card.getEnvTextValue().equals(environmentNames.get(0)) || card.getEnvTextValue()
                                                                                   .equals(environmentNames.get(1))),
                    "Card environment is not one of the selected environments"
            );
        });

        automationLaunchesPage.getFilters().clickResetFilter();

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1187", "ZTP-1212"})
    public void verifyUserCanApplyOnlyOneSavedFilter() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.finishTestRun(testRunId);

        TestClassLaunchDataStorage passedTR = PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(passedTR.getLaunch().getId());

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectEnv(env);

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String firstFilter = env + RandomStringUtils.randomAlphabetic(6);
        filterSemiWindow.saveFilter(firstFilter);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(firstFilter),
                "Filter with name " + firstFilter + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        List<LaunchCard> testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            pause(2);

            softAssert.assertTrue(card.getEnvTextValue().equals(env),
                    "Card environment is wrong. Should be " + env
            );
        });

        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(firstFilter),
                String.format("Filter with name '%s' should present in the list", firstFilter));

        filterSemiWindow.closeSemiWindow();
        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);

        filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String secondFilter = TestRunStatusEnumR.PASSED + RandomStringUtils.randomAlphabetic(6);
        filterSemiWindow.saveFilter(secondFilter);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(secondFilter),
                "Filter with name " + secondFilter + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertTrue(
                filterSemiWindow.isFilterPresentInList(secondFilter),
                String.format("Filter with name '%s' should present in the list", secondFilter));

        filterSemiWindow.getFilterCard(secondFilter)
                        .clickFilterName();//ZTP-1212 - User is able to apply a saved filter to test runs

        softAssert.assertFalse(filterSemiWindow.isCloseButtonPresent(),
                "Filter semi window is still open, when it should not be!");

        testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            pause(2);

            softAssert.assertEquals(
                    card.getStatus().getStatusColourFromCss(),
                    TestRunStatusEnumR.PASSED,
                    "Card status is wrong. Should be " + TestRunStatusEnumR.PASSED
            );
            softAssert.assertFalse(card.getEnvTextValue().equals(env),
                    "Card environment should not be " + env
            );
        });

        automationLaunchesPage.getFilters().clickResetFilter();

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey({"ZTP-1194", "ZTP-1195", "ZTP-1190"})
    public void verifyFilterParametersInURLTest() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.finishTestRun(testRunId);

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_FAILED);
        automationLaunchesPage.getFilters().selectEnv(env);

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String filterName = RandomStringUtils.randomAlphabetic(6);
        filterSemiWindow.saveFilter(filterName);

        softAssert.assertEquals(
                automationLaunchesPage.getPopUp(),
                MessageEnum.FILTER_WAS_SAVED.getDescription(filterName),
                "Filter with name " + filterName + " is not present!"
        );

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();

        softAssert.assertEquals(
                filterSemiWindow.getFilterCard(filterName).getFilterStatusTooltip(),
                "environment:\n" + env + "\nstatus:\n" + TestRunStatusEnumR.FAILED,
                "Tooltip is not as expected"
        );//ZTP-1190 - User is able to see the tooltip with filter parameters when hovering to 'i' icon

        filterSemiWindow.closeSemiWindow();

        String filteredUrl = automationLaunchesPage.getCurrentUrl();
        String EXPECTED_FILTER_STATUS_URL = "&status=" + TestRunStatusEnumR.FAILED;
        String EXPECTED_FILTER_ENV_URL = "&environment=" + env;

        softAssert.assertTrue(filteredUrl.contains(EXPECTED_FILTER_STATUS_URL) //ZTP-1194 - Verify all selected filter parameters are written in URL in key-value format
                        && filteredUrl.contains(EXPECTED_FILTER_ENV_URL),
                "URL should contain '" + EXPECTED_FILTER_STATUS_URL + "' and '" + EXPECTED_FILTER_ENV_URL + "'");

        automationLaunchesPage.getActionsBlockR().openStatusSettings();
        automationLaunchesPage.getActionsBlockR().clickClearSelectionButton();

        automationLaunchesPage.getFilters().clickEnvironmentFilterButton();
        automationLaunchesPage.getActionsBlockR().clickClearSelectionButton();

        softAssert.assertFalse(automationLaunchesPage.getCurrentUrl().contains(EXPECTED_FILTER_STATUS_URL)
                        && automationLaunchesPage.getCurrentUrl().contains(EXPECTED_FILTER_ENV_URL),
                "URL should not contain '" + EXPECTED_FILTER_STATUS_URL + "' and '" + EXPECTED_FILTER_ENV_URL + "' after removing it!");

        automationLaunchesPage.getFilters().selectEnvItem(env);
        String invalidUrl = automationLaunchesPage.getCurrentUrl() + "W"; //ZTP-1195 - Verify that if changing filter parameters to invalid ones in URL - filter parameters are discarded
        getDriver().get(invalidUrl);
        pause(4);

        softAssert.assertFalse(
                getDriver().getCurrentUrl().contains(env),
                "URL contains the invalid parameter '" + env + "W', when it should not!"
        );
        softAssert.assertFalse(
                automationLaunchesPage.getFilters().isResetButtonPresent(),
                "'Reset' button should not be present!");

        getDriver().switchTo().newWindow(WindowType.TAB).get(filteredUrl);
        List<String> tabs = new ArrayList<>(getDriver().getWindowHandles());

        softAssert.assertTrue(
                getDriver().getCurrentUrl().contains(EXPECTED_FILTER_STATUS_URL)
                        && getDriver().getCurrentUrl().contains(EXPECTED_FILTER_ENV_URL),
                "URL in the new tab should contain '" + EXPECTED_FILTER_STATUS_URL + "' and '" + EXPECTED_FILTER_ENV_URL + "'"
        );

        List<LaunchCard> testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            pause(2);

            softAssert.assertTrue(
                    card.getStatus().equals(TestRunStatusEnumR.FAILED) || card.getEnvTextValue().equals(env),
                    "Card environment is not one of the selected environments"
            );
        });

        getDriver().close();
        getDriver().switchTo().window(tabs.get(0));

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-1215")
    public void verifyTestRunFilterByReviewed() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        TestClassLaunchDataStorage notReviewedTR = PreparationUtil.startAndFinishLaunchWithTests(1, 0, project.getKey());
        launchIds.add(notReviewedTR.getLaunch().getId());

        TestClassLaunchDataStorage reviewedTR = PreparationUtil.startAndFinishLaunchWithTests(0, 1, project.getKey());
        launchIds.add(reviewedTR.getLaunch().getId());

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(reviewedTR.getLaunch().getName(), true);
        testRunCard.markAsReviewed("New comment for project with key " + project.getKey());

        automationLaunchesPage.getFilters().selectReview(Dropdown.DropdownItemsEnum.YES.getItemValue());
        pause(2);
        List<LaunchCard> testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            softAssert.assertTrue(
                    card.isReviewedBadgePresent(),
                    "Reviewed badge is not visible for card '" + card.getName() + "'!"
            );
        });

        automationLaunchesPage.getFilters().selectReviewType(Dropdown.DropdownItemsEnum.NO.getItemValue());
        pause(2);
        testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            softAssert.assertFalse(
                    card.isReviewedBadgePresent(),
                    "Reviewed badge is visible for card '" + card.getName() + "', when it should not be!"
            );
        });

        automationLaunchesPage.getFilters().selectReviewType(Dropdown.DropdownItemsEnum.ANY.getItemValue());
        pause(2);

        softAssert.assertTrue(automationLaunchesPage.isCertainLaunchAppears(reviewedTR.getLaunch().getName()),
                "Reviewed test run is not displayed");
        softAssert.assertTrue(automationLaunchesPage.isCertainLaunchAppears(reviewedTR.getLaunch().getName()),
                "Not reviewed test run is not displayed");

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-1219")
    public void verifyTestRunFilterByLaunchesDates() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        //Only change day of the month
        List<LocalDate> dates = Arrays.asList(
                LocalDate.of(2022, 12, 10),
                LocalDate.of(2022, 12, 20),
                LocalDate.of(2022, 12, 30)
        );

        dates.forEach(date -> {
            Launch launch = testRunService.startTestRunWithSpecificDate(project.getKey(), date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            testRunService.finishLaunch(launch.getId());
            launchIds.add(launch.getId());
        });

        SoftAssert softAssert = new SoftAssert();

        //Verify launch time for 'ON_OR_AFTER', 'ON_OR_BEFORE' and 'BETWEEN' date types
        List<LocalDate> verifyDates = Arrays.asList(
                LocalDate.of(2022, 12, 19),
                LocalDate.of(2022, 12, 21)
        );

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());
        automationLaunchesPage.getFilters().selectLaunchesDate();
        automationLaunchesPage.getFilters().selectBetweenDateType(verifyDates.get(0), verifyDates.get(1));

        pause(3);
        List<LaunchCard> testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            Matcher matcher = Pattern.compile("^Launched on Dec (\\d{1,2}), (\\d{4}) by \\w+$")
                                     .matcher(card.getStateAndTimeFromStart().getText());

            if (matcher.matches()) {
                int day = Integer.parseInt(matcher.group(1));
                int year = Integer.parseInt(matcher.group(2));

                boolean isDateInRange = day >= verifyDates.get(0).getDayOfMonth() && day <= verifyDates.get(1)
                                                                                                       .getDayOfMonth() && year == verifyDates.get(1)
                                                                                                                                              .getYear();
                softAssert.assertTrue(isDateInRange, "Time of card '" + card.getName() + "' is not as expected for 'BETWEEN' date type.");
            } else {
                softAssert.fail("Card text format doesn't match the expected date format.");
            }
        });

        automationLaunchesPage.getFilters().selectAndTypeDate(Dropdown.DropdownItemsEnum.ON, dates.get(1));

        pause(2);
        testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            softAssert.assertTrue(card.getStateAndTimeFromStart().getText().matches(
                            String.format("^Launched on Dec (%d|0[1-9]|1[0-9]|2[0-9]|3[0-1]), %d by \\w+$", dates.get(1)
                                                                                                                 .getDayOfMonth(), dates.get(1)
                                                                                                                                        .getYear())),
                    "Time of '" + card.getName() + "' is not as expected for 'ON' date type.");
        });

        automationLaunchesPage.getFilters()
                              .selectAndTypeDate(Dropdown.DropdownItemsEnum.ON_OR_AFTER, verifyDates.get(1));

        pause(2);
        testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            String regex = "^Launched .*? by \\w+$|^Launched on Dec (" + verifyDates.get(1)
                                                                                    .getDayOfMonth() + "[1-9]|1[0-9]|2[0-9]|3[0-1]), " + verifyDates.get(1)
                                                                                                                                                    .getYear() + " by \\w+$|^Queued .*? by \\w+$";
            softAssert.assertTrue(card.getStateAndTimeFromStart().getText()
                                      .matches(regex), "Time of '" + card.getName() + "' is not as expected for 'ON_OR_AFTER' date type.");
        });

        automationLaunchesPage.getFilters()
                              .selectAndTypeDate(Dropdown.DropdownItemsEnum.ON_OR_BEFORE, verifyDates.get(0));

        pause(2);
        testRunCards = automationLaunchesPage.getAllTestRunCards();

        testRunCards.forEach(card -> {
            softAssert.assertTrue(card.getStateAndTimeFromStart().getText().matches(
                            String.format("^Launched on Dec (%d[1-9]|1[0-9]|2[0-9]|3[0-1]), %d by \\w+$", verifyDates.get(0)
                                                                                                                     .getDayOfMonth(), verifyDates.get(0)
                                                                                                                                                  .getYear())),
                    "Time of '" + card.getName() + "' is not as expected for 'ON_OR_BEFORE' date type.");
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-1220")
    public void verifyTestRunFilterByApplyingSeveralFiltersSameTime() {
        Long launchId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(launchId);
        testRunService.finishTestRun(launchId);

        launchId = testRunService.startTestRunWithName(project.getKey(), PlatformTypeR.CHROME + "-" + PlatformTypeR.API);
        launchIds.add(launchId);

        Long testId = testService.startTestWithMethodName(
                launchId,
                "test_tr_filters" + project.getKey() + "_" + RandomStringUtils.randomAlphabetic(7)
        );
        apiHelperService.startSession(launchId, Collections.singletonList(testId), PlatformTypeR.API.value(), PlatformTypeR.CHROME.value());
        apiHelperService.addLabelToTestRun(launchId, LOCALE_LABEL_KEY, US_LOCALE);
        testService.finishTestAsResult(launchId, testId, "PASSED");
        testRunService.finishTestRun(launchId);

        LocalDate launchStartedAt = LocalDate.of(2022, 12, 1);
        Launch launch = testRunService.startTestRunWithSpecificDate(project.getKey(), launchStartedAt.getYear(), launchStartedAt.getMonthValue(), launchStartedAt.getDayOfMonth());
        testService.startTest(TestExecution.getRandomTestExecution(), launch.getId());
        testRunService.finishLaunch(launch.getId());
        launchIds.add(launch.getId());

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        LaunchCard testRunCard = automationLaunchesPage.getCertainTestRunCard(launch.getName(), true);
        testRunCard.markAsReviewed("New comment for project with key " + project.getKey());
        automationLaunchesPage.getFilters().selectReview(Dropdown.DropdownItemsEnum.YES.getItemValue());
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertTrue(
                    launchCard.isReviewedBadgePresent(),
                    "Reviewed badge is not visible for card '" + launchCard.getName() + "'!"
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_FAILED);
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    launchCard.getStatus().getStatusColourFromCss(), TestRunStatusEnumR.FAILED,
                    "Card status should be " + TestRunStatusEnumR.FAILED
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectEnv(env);
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    launchCard.getEnvTextValue(), env,
                    "Card environment is not one of the selected environments"
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectPlatform("api");
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    launchCard.getPlatform().getPlatformType(), PlatformTypeR.API,
                    "Platform should be 'api'"
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectBrowser("chrome");
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    launchCard.getBrowser().getType(), PlatformTypeR.CHROME,
                    "Browser should be " + PlatformTypeR.CHROME.value()
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectLaunchesDate();
        automationLaunchesPage.getFilters().selectAndTypeDate(Dropdown.DropdownItemsEnum.ON, launchStartedAt);
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            String launchedOnText = String.format("^Launched on Dec %d, %d by \\w+$", launchStartedAt.getDayOfMonth(), launchStartedAt.getYear());

            Assert.assertTrue(
                    launchCard.getStateAndTimeFromStart().getText().matches(launchedOnText),
                    "Time of '" + launchCard.getName() + "' is not as expected for 'ON' date type."
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        automationLaunchesPage.getFilters().selectLocale();
        automationLaunchesPage.getFilters().selectLocaleType(Locale.US);
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    "Locale\n: " + Locale.US, launchCard.getLocale().getText(),
                    "Locale should be " + Locale.US + "!"
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        //Select 2 filters same time
        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_FAILED);
        automationLaunchesPage.getFilters().selectEnv(env);

        super.pause(2);
        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    TestRunStatusEnumR.FAILED, launchCard.getStatus().getStatusColourFromCss(),
                    "Card status should be " + TestRunStatusEnumR.FAILED + ", '2 filters are selected.'"
            );
            Assert.assertEquals(
                    launchCard.getEnvTextValue(), env,
                    "Card environment is not one of the selected environments, '2 filters are selected.'"
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        //Select 3 filters same time
        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_FAILED);
        automationLaunchesPage.getFilters().selectEnv(env);
        automationLaunchesPage.getFilters().selectReview(Dropdown.DropdownItemsEnum.NO.getItemValue());
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    TestRunStatusEnumR.FAILED, launchCard.getStatus().getStatusColourFromCss(),
                    "Card status should be " + TestRunStatusEnumR.FAILED + ", '3 filters are selected.'"
            );
            Assert.assertEquals(
                    launchCard.getEnvTextValue(), env,
                    "Card environment is not one of the selected environments, '3 filters are selected.'"
            );
            Assert.assertFalse(
                    launchCard.isReviewedBadgePresent(),
                    "Reviewed badge is visible for card '" + launchCard.getName() + "'! '3 filters are selected.'"
            );
        }

        automationLaunchesPage.getFilters().clickResetFilter();
        //Select 4 filters same time
        automationLaunchesPage.getActionsBlockR().selectSingleStatusAndClose(Dropdown.DropdownItemsEnum.STATUS_PASSED);
        automationLaunchesPage.getFilters().selectReview(Dropdown.DropdownItemsEnum.NO.getItemValue());
        automationLaunchesPage.getFilters().selectBrowser(PlatformTypeR.CHROME.value());
        automationLaunchesPage.getFilters().selectPlatform(PlatformTypeR.API.value());
        super.pause(2);

        for (LaunchCard launchCard : automationLaunchesPage.getAllTestRunCards()) {
            Assert.assertEquals(
                    TestRunStatusEnumR.PASSED, launchCard.getStatus().getStatusColourFromCss(),
                    "Card status should be " + TestRunStatusEnumR.PASSED + ", 'while 4 filters are selected!'"
            );
            Assert.assertFalse(
                    launchCard.isReviewedBadgePresent(),
                    "Reviewed badge is visible for card '" + launchCard.getName() + "'! '4 filters are selected.'"
            );
            Assert.assertEquals(
                    launchCard.getPlatform().getPlatformType(), PlatformTypeR.API,
                    "Platform should be " + PlatformTypeR.API.value() + "'! '4 filters are selected.'"
            );
            Assert.assertEquals(
                    launchCard.getBrowser().getType(), PlatformTypeR.CHROME,
                    "Browser should be " + PlatformTypeR.CHROME.value() + "'! '4 filters are selected.'"
            );
        }
        automationLaunchesPage.getFilters().clickResetFilter();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-1218")
    public void verifyTestRunFilterByLocale() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.LAUNCHES);
        Label.attachToTest(TestLabelsConstant.LAUNCHERS, TestLabelsConstant.FILTERS);

        Launch launchUS = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        apiHelperService.addLabelToTestRun(launchUS.getId(), LOCALE_LABEL_KEY, US_LOCALE);
        testRunService.finishLaunch(launchUS.getId());
        launchIds.add(launchUS.getId());

        Launch launchFR = testRunService.startTestRunWithName(project.getKey(), Launch.getRandomLaunch());
        apiHelperService.addLabelToTestRun(launchFR.getId(), LOCALE_LABEL_KEY, FR_LOCALE);
        testRunService.finishLaunch(launchFR.getId());
        launchIds.add(launchFR.getId());

        SoftAssert softAssert = new SoftAssert();

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectLocale();
        automationLaunchesPage.getFilters().selectLocaleType(Locale.US);

        pause(2);
        automationLaunchesPage.getAllTestRunCards().forEach(card -> {
            softAssert.assertTrue(
                    card.getLocale().getText().equals("Locale\n: " + Locale.US),
                    "Locale should be " + Locale.US + "!");
            softAssert.assertFalse(
                    card.getLocale().getText().equals("Locale\n: " + Locale.FRANCE),
                    "Locale is " + Locale.FRANCE + " for card, when it should not be!");
        });

        softAssert.assertAll();
    }

    @Test
    @Maintainer("bmakharadze")
    @TestCaseKey("ZTP-5764")
    public void verifyUserTooltipInfoAppearsWhenUserHoversUsernameInFilters() {
        User mainAdmin = userService.getUserByUsername(UsersEnum.MAIN_ADMIN.getUser().getUsername());
        ProjectAssignment assignedUser = projectV1Service.getProjectAssignmentForUser(Math.toIntExact(project.getId()), mainAdmin.getUsername());

        long testRunId = apiHelperService.startTRWithCertainConfig(project.getKey(), env);
        launchIds.add(testRunId);
        testRunService.setPlatform(testRunId, "API", "");
        testRunService.finishTestRun(testRunId);

        AutomationLaunchesPage automationLaunchesPage = AutomationLaunchesPage.openPageDirectly(getDriver(), project.getKey());

        automationLaunchesPage.getFilters().selectEnv(env);

        FilterSemiWindow filterSemiWindow = automationLaunchesPage.getFilters().clickSaveFilter();
        String firstFilter = "Filter ".concat(RandomStringUtils.randomAlphabetic(6));
        filterSemiWindow.saveFilter(firstFilter);

        automationLaunchesPage.waitPopupDisappears();
        automationLaunchesPage.getFilters().toSavedFilters();
        filterSemiWindow.getFilterCard(firstFilter)
                        .hoverFilterOwner()
                        .verifyUserInfoTooltip(mainAdmin, true, assignedUser.getRole(), "At filter semi window for card!");
    }
}
