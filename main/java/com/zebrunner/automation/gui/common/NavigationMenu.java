package com.zebrunner.automation.gui.common;

import org.apache.commons.beanutils.ConstructorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.reporting.dashboard.MainDashboardsPageR;
import com.zebrunner.automation.gui.reporting.launch.AutomationLaunchesPage;
import com.zebrunner.automation.gui.reporting.milestone.MilestonePage;
import com.zebrunner.automation.gui.tcm.testcase.TestCasesPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Getter
public class NavigationMenu extends AbstractUIObject {

    public static final String NAVIGATION_MENU_XPATH = "//div[contains(@class, 'page-sidebar')]";

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//span[contains(text(), '%s')]//ancestor::a")
    private Element navigationItem;

    @FindBy(xpath = ".//div[contains(@class, 'shortname')]")
    private Element projectKey;

    @FindBy(xpath = ".//div[@class='collapse-button__icon']")
    private Element collapseSideBarButton;

    @FindBy(xpath = ".//div[@class='sidebar-project__image']//img")
    private ExtendedWebElement sidebarProjectImage;

    private NavigationMenu(WebDriver driver) {
        super(driver);
        setBy(By.xpath(NAVIGATION_MENU_XPATH));
    }

    public static NavigationMenu getInstance(WebDriver driver) {
        NavigationMenu navigationMenu = new NavigationMenu(driver);
        navigationMenu.pause(1);
        return navigationMenu;
    }

    public MainDashboardsPageR toMainDashboardPage() {
        return (MainDashboardsPageR) this.to(NavigationMenuItem.AUTOMATION, NavigationMenuPopover.PopoverItems.DASHBOARDS);
    }

    public AutomationLaunchesPage toTestRunsPage() {
        return (AutomationLaunchesPage) to(NavigationMenuItem.AUTOMATION, NavigationMenuPopover.PopoverItems.LAUNCHES);
    }

    public MembersPageR toMembersPageR() {
        return (MembersPageR) to(NavigationMenuItem.SETTINGS, NavigationMenuPopover.PopoverItems.MEMBERS);
    }

    public TestCasesPage toTestCasesPage() {
        return (TestCasesPage) to(NavigationMenuItem.TEST_REPOSITORY, NavigationMenuPopover.PopoverItems.TEST_CASES);
    }

    public MilestonePage toMilestonePage() {
        this.click(NavigationMenuItem.MILESTONES);

        return MilestonePage.getInstance(super.getDriver());
    }

    public String getProjectKey() {
        return projectKey.getText();
    }

    public boolean isProjectPhotoPresent() {
        return sidebarProjectImage.isVisible();
    }

    public boolean waitUntilProjectKeyToBE(String key) {
        return waitUntil(ExpectedConditions.textToBePresentInElement(projectKey.getElement(), key), 4);
    }

    public String getSidebarProjectImgLink() {
        return sidebarProjectImage.getAttribute("src");
    }

    public NavigationMenuPopover hover(String item) {
        Element navigationItem = super.format(this.navigationItem, item);
        navigationItem.hover();

        NavigationMenuPopover popover = new NavigationMenuPopover(super.getDriver());
        popover.assertElementPresent(10);

        return popover;
    }

    public NavigationMenuPopover click(String item) {
        Element navigationItem = super.format(this.navigationItem, item);
        navigationItem.click();

        return new NavigationMenuPopover(super.getDriver());
    }

    @SneakyThrows
    public TenantProjectBasePage to(String navigationItem, NavigationMenuPopover.PopoverItems popoverItem) {
        this.hover(navigationItem).selectItem(popoverItem);

        Class<?> pageClass = popoverItem.getPage();
        if (pageClass == null) {
            throw new IllegalArgumentException("No page class found for the specified popover item: " + popoverItem);
        }

        Constructor<?> constructor = ConstructorUtils.getAccessibleConstructor(pageClass, WebDriver.class);
        if (constructor == null) {
            throw new RuntimeException("Unable to find accessible constructor");
        }

        return (TenantProjectBasePage) constructor.newInstance(super.getDriver());
    }

    public boolean isOpened() {
        return this.getRootExtendedElement().getAttribute("class").contains("_opened");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NavigationMenuItem {

        public static final String TEST_REPOSITORY = "Test repository";
        public static final String TESTING_ACTIVITIES = "Testing activities";
        public static final String AUTOMATION = "Automation";
        public static final String MILESTONES = "Milestones";
        public static final String SETTINGS = "Settings";

    }

}
