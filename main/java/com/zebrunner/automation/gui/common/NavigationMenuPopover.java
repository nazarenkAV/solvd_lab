package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.launcher.LauncherPage;
import com.zebrunner.automation.gui.reporting.dashboard.MainDashboardsPageR;
import com.zebrunner.automation.gui.integration.ProjectIntegrationsPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.tcm.sharedstep.SharedStepsPage;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

public class NavigationMenuPopover extends AbstractUIObject {
    @FindBy(xpath = ".//div[@class='sub-item__title']")
    private ExtendedWebElement menuTitle;

    @FindBy(xpath = ".//div[contains(@class,'sub-item__text')]")
    private List<ExtendedWebElement> items;

    public NavigationMenuPopover(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[contains(@class,'MuiPopover-paper')]"));
    }

    public String getMenuTitle() {
        return menuTitle.getTitle();
    }

    public boolean isItemPresent(PopoverItems itemName){
        return items.stream().anyMatch(item -> item.getText().equalsIgnoreCase(itemName.value));
    }

    public void selectItem(PopoverItems itemName) {
        items.stream().filter(item -> item.getText().equalsIgnoreCase(itemName.value))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("Unable to find " + itemName + " on menu " + getMenuTitle()))
                .click();
    }

    public enum PopoverItems {
        //---------------Test repository-------------//
        TEST_CASES("Test cases", TestCasesPage.class),
        SHARED_STEPS("Shared steps", SharedStepsPage.class),
        //---------------Test runs and plans-------------//
        TEST_RUNS("Test runs", TestCasesPage.class),
        TEST_PLANS("Test plans", null),
        //---------------Automation-------------//
        LAUNCHES("Launches", AutomationLaunchesPage.class),
        LAUNCHERS("Launchers", LauncherPage.class),
        DASHBOARDS("Dashboards", MainDashboardsPageR.class),
        //---------------Reports-------------//
        //---------------Milestones-------------//

        //---------------Settings-------------//
        GENERAL("General", AutomationLaunchesPage.class),
        TEST_CASE_FIELDS("Test case fields", LauncherPage.class),
        TESTING_CONFIGURATION("Testing configurations", MainDashboardsPageR.class),
        ENVS_AND_VARS("Environments and variables", AutomationLaunchesPage.class),
        MEMBERS("Members", MembersPageR.class),
        INTEGRATIONS("Integrations", ProjectIntegrationsPageR.class),
        ;

        private final String value;
        private final Class<?> pageClass;

        PopoverItems(String value, Class<?> pageClass) {
            this.value = value;
            this.pageClass = pageClass;
        }

        public Class<?> getPage() {
            return pageClass;
        }
    }
}
