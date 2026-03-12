package com.zebrunner.automation.gui.landing;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import com.zebrunner.automation.gui.Element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class DocumentationPage extends AbstractPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String PAGE_NAME = "Documentation";

    @FindBy(xpath = "//header")
    private Header header;

    @FindBy(xpath = "//div[@data-md-component='sidebar']")
    private DocumentationSidebar documentationSidebar;

    @FindBy(id = "meet-zebrunner")
    private Element overviewTitle;

    @FindBy(id = "meet-zebrunner")
    private ExtendedWebElement uiLoadedMarker;

    public DocumentationPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static DocumentationPage openPage(WebDriver driver) {
        LOGGER.info("Attempt to go to the page '{}'", PAGE_NAME);
        DocumentationPage documentationPage = new DocumentationPage(driver);
        documentationPage.pause(2);
        return documentationPage;
    }

    public String getOverviewTitle() {
        return overviewTitle.getText();
    }

    public boolean isHeaderPresent() {
        return header.isUIObjectPresent();
    }

    public String getActiveSessionText() {
        return documentationSidebar.getActiveSection();
    }

    public boolean isSidebarPresent() {
        return documentationSidebar.isUIObjectPresent();
    }

}
