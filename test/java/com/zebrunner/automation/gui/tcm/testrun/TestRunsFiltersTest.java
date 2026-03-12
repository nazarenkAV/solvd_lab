package com.zebrunner.automation.gui.tcm.testrun;

import com.zebrunner.agent.core.annotation.Maintainer;
import com.zebrunner.agent.core.annotation.TestCaseKey;
import com.zebrunner.agent.core.registrar.Label;
import com.zebrunner.automation.legacy.TestLabelsConstant;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.automation.gui.tcm.TcmLogInBase;
import com.zebrunner.automation.api.tcm.domain.Environment;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.reporting.domain.Milestone;
import com.zebrunner.automation.api.tcm.domain.ConfigurationGroup;
import com.zebrunner.automation.api.tcm.domain.ConfigurationOption;
import com.zebrunner.automation.api.tcm.domain.TestRun;
import com.zebrunner.automation.api.tcm.domain.TestRunConfiguration;
import com.zebrunner.automation.util.ComponentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Collections;
import java.util.List;

@Slf4j
@Maintainer("akhivyk")
public class TestRunsFiltersTest extends TcmLogInBase {

    private Project project;
    private Project emptyProject;
    private User mainAdmin;
    private Environment environment;
    private Milestone milestone;
    private TestRun testRun_1;
    private TestRun testRun_2;
    private ConfigurationGroup configGroup_1;
    private ConfigurationOption configOption_1;
    private ConfigurationGroup configGroup_2;
    private ConfigurationOption configOption_2;
    private TestRunConfiguration testRunConfiguration_1;

    @BeforeClass
    public void preparation() {
        String milestoneName = "Milestone_" + RandomStringUtils.randomAlphabetic(5);
        project = super.getCreatedProject();
        emptyProject = super.getEmptyProject();
        mainAdmin = usersService.getUserByUsername(MAIN_ADMIN.getUsername());

        environment = environmentService.createEnvironment(project.getId(), Environment.createRandom());
        milestone = apiHelperService.createMilestone(project.getId(), Milestone.createMilestoneWithTitle(milestoneName));

        configGroup_1 = apiHelperService.createConfigurationGroup(project.getId(), ConfigurationGroup.createRandom());
        configOption_1 = apiHelperService.createConfigurationOption(project.getId(), configGroup_1.getId(), ConfigurationOption.generateRandom());

        configGroup_2 = apiHelperService.createConfigurationGroup(project.getId(), ConfigurationGroup.createRandom());
        configOption_2 = apiHelperService.createConfigurationOption(project.getId(), configGroup_2.getId(), ConfigurationOption.generateRandom());

        testRun_1 = TestRun.createWithRandomName();
        testRun_1.setMilestoneId(milestone.getId());
        testRun_1.setEnvironment(environment);
        testRunConfiguration_1 = new TestRunConfiguration();
        testRunConfiguration_1.setGroupId(configGroup_1.getId());
        testRunConfiguration_1.setOptionId(configOption_1.getId());
        testRun_1.setConfigurations(Collections.singletonList(testRunConfiguration_1));

        testRun_2 = TestRun.createWithRandomName();
        testRun_2.setMilestoneId(milestone.getId());
        testRun_2.setClosed(true);
        testRun_2.setEnvironment(environment);
        testRun_2.setConfigurations(Collections.singletonList(testRunConfiguration_1));

        testRun_1 = tcmService.createTestRun(project.getId(), testRun_1);
        testRun_2 = tcmService.createTestRun(project.getId(), testRun_2);
        tcmService.closeTestRun(project.getId(), testRun_2.getId());
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedData() {
        environmentService.deleteEnvironment(project.getId(), environment.getId());
        apiHelperService.deleteMilestone(project.getId(), milestone.getId());
        apiHelperService.deleteConfigurationGroup(project.getId(), configGroup_1);
        apiHelperService.deleteConfigurationGroup(project.getId(), configGroup_2);
    }

    @Test
    @TestCaseKey({"ZTP-4699", "ZTP-4700"})
    public void verifyUserIsAbleToSearchTestRunsByNameAndFilterByMilestone() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        String newRunName = "New " + RandomStringUtils.randomNumeric(3) + " " + testRun_1.getTitle();
        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);

        testRunsGridPage.clickCreateTestRunButton()
                .inputTitle(newRunName)
                .clickCreateButton()
                .backToTestRunsGrid();

        // ZTP-4699 able to search test runs by name
        List<TestRunItem> foundedResults = testRunsGridPage.search(testRun_1.getTitle());
        foundedResults.forEach(result -> {
            softAssert.assertTrue(result.getTestRunName().contains(testRun_1.getTitle()),
                    "Test run with name " + result.getTestRunName() + " isn't contains founded keyword");
        });

        testRunsGridPage.getFilters().clickResetFilter();

        testRunsGridPage.getFilters().selectMilestoneFilter(milestone.getName());
        testRunsGridPage.clickOpenedTestRuns();

        // ZTP-4700 able to filter test runs by Milestones
        foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by Milestone " +
                "is empty on 'Open' tab");
        foundedResults.forEach(filteredResult -> {
            softAssert.assertTrue(filteredResult.getMilestoneName().equalsIgnoreCase(milestone.getName()),
                    "Test run " + filteredResult.getTestRunName() + " isn't have corresponding " +
                            "milestone on 'Open' tab");
        });

        testRunsGridPage.clickClosedTestRuns();
        foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by Milestone " +
                "is empty on 'Closed' tab");
        foundedResults.forEach(filteredResult -> {
            softAssert.assertTrue(filteredResult.getMilestoneName().equalsIgnoreCase(milestone.getName()),
                    "Test run " + filteredResult.getTestRunName() + " isn't have corresponding " +
                            "milestone on 'Closed' tab");
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4701")
    public void verifyUserIsAbleToFilterTestRunsByEnvironments() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);

        testRunsGridPage.getFilters().selectFilterItem(TestRunsFilterBlock.TestRunsFiltersEnum.ENVIRONMENT,
                environment.getKey());
        testRunsGridPage.clickOpenedTestRuns();

        // ZTP-4701 able to filter test runs by Environment
        List<TestRunItem> foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by Environment " +
                "is empty on 'Open' tab");
        foundedResults.forEach(result -> {
            softAssert.assertTrue(result.getEnvironmentLabel().equalsIgnoreCase(environment.getKey()),
                    "Test run " + result.getTestRunName() + " isn't have corresponding " +
                            "environment on 'Open' tab");
        });

        testRunsGridPage.clickClosedTestRuns();
        foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by Environment " +
                "is empty on 'Closed' tab");
        foundedResults.forEach(filteredResult -> {
            softAssert.assertTrue(filteredResult.getEnvironmentLabel().equalsIgnoreCase(environment.getKey()),
                    "Test run " + filteredResult.getTestRunName() + " isn't have corresponding " +
                            "environment on 'Closed' tab");
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-4746")
    public void verifyUserIsAbleToFilterTestRunsByConfigurationGroups() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);

        testRunsGridPage.getFilters().selectConfigurationFilter(configGroup_1.getName(), configOption_1.getName());
        testRunsGridPage.clickOpenedTestRuns();

        List<TestRunItem> foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by Configuration " +
                "is empty on 'Open' tab");
        foundedResults.forEach(result -> {
            result.getConfigurations().forEach(configurationLabel -> {
                softAssert.assertTrue(configurationLabel.getConfigurationGroupName().equalsIgnoreCase(configGroup_1.getName()),
                        "Test run " + result.getTestRunName() + " isn't have corresponding " +
                                "configuration group on 'Open' tab");

                softAssert.assertTrue(configurationLabel.getConfigurationOptionName().equalsIgnoreCase(configOption_1.getName()),
                        "Test run " + result.getTestRunName() + " isn't have corresponding " +
                                "configuration option on 'Open' tab");
            });
        });

        testRunsGridPage.getFilters().clickResetFilter();
        testRunsGridPage.clickClosedTestRuns();

        testRunsGridPage.getFilters().selectConfigurationFilter(configGroup_1.getName(), configOption_1.getName());
        foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by Configuration " +
                "is empty on 'Closed' tab");
        foundedResults.forEach(result -> {
            result.getConfigurations().forEach(configurationLabel -> {
                softAssert.assertTrue(configurationLabel.getConfigurationGroupName().equalsIgnoreCase(configGroup_1.getName()),
                        "Test run " + result.getTestRunName() + " isn't have corresponding " +
                                "configuration group on 'Closed' tab");

                softAssert.assertTrue(configurationLabel.getConfigurationOptionName().equalsIgnoreCase(configOption_1.getName()),
                        "Test run " + result.getTestRunName() + " isn't have corresponding " +
                                "configuration option on 'Closed' tab");
            });
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4747", "ZTP-4748"})
    public void verifyUserIsAbleToFilterTestRunsByDifferentConfigurationsAtTheSameTime() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();

        TestRunConfiguration testRunConfiguration_2 = new TestRunConfiguration();
        testRunConfiguration_2.setGroupId(configGroup_2.getId());
        testRunConfiguration_2.setOptionId(configOption_2.getId());

        testRun_1.setConfigurations(List.of(testRunConfiguration_1, testRunConfiguration_2));
        testRun_2.setConfigurations(List.of(testRunConfiguration_1, testRunConfiguration_2));
        testRun_1 = tcmService.updateTestRun(project.getId(), testRun_1);

        // ZTP-4747 Verify that configurations in the filter are displayed from the moment of creation
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);
        softAssert.assertTrue(testRunsGridPage.getFilters().isConfigurationAppearInList(configGroup_2.getName()),
                "Configuration isn't present in filter list after using in test run");
        ComponentUtil.pressEscape(getDriver());

        softAssert.assertTrue(testRunsGridPage.getFilters().isConfigurationAppearInList(configGroup_1.getName()),
                "Configuration isn't present in filter list after using in test run");
        ComponentUtil.pressEscape(getDriver());

        testRunsGridPage.getFilters().selectConfigurationFilter(configGroup_1.getName(), configOption_1.getName());

        softAssert.assertTrue(testRunsGridPage.getFilters()
                        .isFilterButtonPresent(TestRunsFilterBlock.TestRunsFiltersEnum.CONFIGURATION),
                "Configuration filter button isn't present after selecting 1 config filter");

        testRunsGridPage.getFilters().selectConfigurationFilter(configGroup_2.getName(), configOption_2.getName());

        // ZTP-4748 can filter by different configurations at the same time
        List<TestRunItem> foundedResults = testRunsGridPage.getTestRunItems();
        softAssert.assertFalse(foundedResults.isEmpty(), "Founded results after filtering by 2 configurations " +
                "is empty");
        foundedResults.forEach(testRunItem -> {
            testRunItem.getConfigurations().forEach(configurationLabel -> {
                boolean isMatchGroup = configurationLabel.getConfigurationGroupName().equalsIgnoreCase(configGroup_1.getName())
                        || configurationLabel.getConfigurationGroupName().equalsIgnoreCase(configGroup_2.getName());

                softAssert.assertTrue(isMatchGroup, "Group name in test run " + testRunItem.getTestRunName()
                        + " isn't equals to expected");

                boolean isMatchOption = configurationLabel.getConfigurationOptionName().equalsIgnoreCase(configOption_1.getName())
                        || configurationLabel.getConfigurationOptionName().equalsIgnoreCase(configOption_2.getName());

                softAssert.assertTrue(isMatchOption, "Option name in test run " + testRunItem.getTestRunName()
                        + " isn't equals to expected");
            });
        });

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey({"ZTP-4703", "ZTP-4706"})
    public void verifyFiltersBlockIsNotAppearOnEmptyGridPage() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), emptyProject);

        // Filter section unavailable
        softAssert.assertFalse(testRunsGridPage.getFilters().isFilterButtonPresent(TestRunsFilterBlock.TestRunsFiltersEnum.MILESTONE),
                "Milestone filters block is present on empty test runs grid page");
        softAssert.assertFalse(testRunsGridPage.getFilters().isFilterButtonPresent(TestRunsFilterBlock.TestRunsFiltersEnum.ENVIRONMENT),
                "Environment filters block is present on empty test runs grid page");
        softAssert.assertFalse(testRunsGridPage.getFilters().isFilterButtonPresent(TestRunsFilterBlock.TestRunsFiltersEnum.CONFIGURATION),
                "Configuration filters block is present on empty test runs grid page");
        softAssert.assertTrue(testRunsGridPage.isSearchFieldDisabled(),
                "Search field is enabled on empty test run page");

        softAssert.assertAll();
    }

    @Test
    @TestCaseKey("ZTP-5759")
    public void verifyUserCardIsAppearsWhenHoverUsername() {
        Label.attachToTest(TestLabelsConstant.GROUP, TestLabelsConstant.TEST_RUNS);

        SoftAssert softAssert = new SoftAssert();
        TestRunsGridPage testRunsGridPage = TestRunsGridPage.openPageByUrl(getDriver(), project);

        TestRunItem testRunItem = testRunsGridPage.getTestRunItem(testRun_1.getTitle());
        testRunItem.hoverUsername();

        UserInfoTooltip userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering username");

        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test run grid page");

        TestRunPage testRunPage = testRunItem.clickTestRunItem();
        testRunPage.getExpandedHeader().hoverUsername();

        userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering username on test run page with expanded header");

        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test run page with expanded header");

        CollapsedTestRunHeader collapsedTestRunHeader = testRunPage.getCollapsedHeader();
        softAssert.assertFalse(userInfoTooltip.isPresent(2),
                "User info tooltip still present after collapsing header");

        collapsedTestRunHeader.hoverUsername();
        userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering username on test run page with collapsed header");

        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test run page with collapsed header");

        testRunPage.backToTestRunsGrid().clickClosedTestRuns();
        testRunItem = testRunsGridPage.getTestRunItem(testRun_2.getTitle());
        testRunItem.hoverUsername();

        userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isPresent(2),
                "User info tooltip isn't present after hovering username");

        userInfoTooltip.verifyUserInfoTooltip(mainAdmin, true, RoleEnum.ADMINISTRATOR,
                "On test run grid page");

        softAssert.assertAll();
    }
}
