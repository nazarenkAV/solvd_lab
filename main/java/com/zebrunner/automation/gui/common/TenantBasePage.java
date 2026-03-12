package com.zebrunner.automation.gui.common;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.PopUp;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;

@Slf4j
@Getter
public abstract class TenantBasePage extends AbstractPage {

    @FindBy(xpath = "//header")
    private TenantHeader header;

    @FindBy(xpath = "//button[contains(@class, 'mobile-sidebar')]")
    private ExtendedWebElement mobileSidebarBtn;

    @FindBy(xpath = PaginationR.ROOT_XPATH)
    private PaginationR pagination;

    @FindBy(xpath = EmptyPlaceholder.ROOT_XPATH)
    private EmptyPlaceholder emptyPlaceholder;

    @FindBy(xpath = "//h1[contains(@h1,'')]")
    private Element pageTitle;

    @FindBy(xpath = PopUp.POPUP_XPATH)
    private PopUp popUp;

    @FindBy(xpath = "//button[contains(text(),'CANCEL')]")
    private Element closePopup;

    @FindBy(xpath = "//*[@class='CloseIcon']")
    private Element closeChat;

    @FindBy(xpath = "//div[contains(@class,'loader-container')]")
    private Element loaderContainer;

    public TenantBasePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getTitle() {
        return pageTitle.getText();
    }

    /**
     * @see TenantBasePage waitIsPopUpMessageAppear waitIsPopUpMessageAppearByRegex
     */
    public String getPopUp() {
        PopUp popUp = this.popUp.waitUntilAppear();
        if (popUp == null) {
            return "No popup find! ";
        }

        super.waitUntil(ExpectedConditions.invisibilityOfElementWithText(popUp.getPopupMessage().getBy(), ""), 3);

        return popUp.getPopupMessage().getText();
    }

    public boolean waitIsPopUpMessageAppear(String expectingPopUpMessage) {
        return WaitUtil.waitCheckElementMatchByCondition(
                popUp,
                popUp -> popUp.getPopupMessage().getText().equals(expectingPopUpMessage),
                Duration.ofSeconds(10),
                Duration.ofMillis(250)
        );
    }

    public boolean waitPopupDisappears() {
        log.info("Wait for the popup to disappear...");

        return super.waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.xpath(PopUp.POPUP_XPATH)), 7);
    }

    public void closeChat() {
        WebDriver webDriver = super.getDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        try {
            WebElement frameElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.className("Papercups-chatWindowContainer"))
            );
            webDriver.switchTo().frame(frameElement);

            if (closeChat.isStateMatches(Condition.VISIBLE)) {
                log.info("Closing chat...");
                closeChat.click();
            }
            webDriver.switchTo().parentFrame();
        } catch (TimeoutException e) {
            log.warn("Chat frame was not found!", e);
        }
    }

    public void waitInvisibilityOfLoader() {
        super.waitUntil(ExpectedConditions.invisibilityOf(loaderContainer), Duration.ofSeconds(15));
    }

    public NavigationMenu openMobileNavigationMenu() {
        NavigationMenu navigationMenu = NavigationMenu.getInstance(super.getDriver());

        if (!navigationMenu.isOpened()) {
            mobileSidebarBtn.click();
        }

        return navigationMenu;
    }

    public void closeMobileNavigationMenu() {
        NavigationMenu navigationMenu = NavigationMenu.getInstance(super.getDriver());

        if (navigationMenu.isOpened()) {
            mobileSidebarBtn.click();
        }
    }

}
