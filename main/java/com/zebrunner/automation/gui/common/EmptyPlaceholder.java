package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class EmptyPlaceholder extends AbstractUIObject {

    public static final String ROOT_XPATH = "//*[contains(@class, 'placeholder__wrapper')]";

    @FindBy(xpath = ".//*[contains(@class, 'placeholder__title')]")
    private ExtendedWebElement emptyPagePlaceholder;

    @FindBy(xpath = ".//*[contains(@class, 'placeholder__description')]")
    private ExtendedWebElement emptyPlaceholderDescription;

    @FindBy(xpath = ".//*[contains(@class,'placeholder__description')]//a[text()=' configure reporting agent']")
    private ExtendedWebElement configureReportingAgent;

    @FindBy(xpath = ".//*[contains(@class,'placeholder__description')]//a[contains(text(),'set up launchers')]")
    private ExtendedWebElement setUpLaunchers;

    @FindBy(xpath = ".//*[contains(@class, 'placeholder__image')]")
    private ExtendedWebElement emptyPlaceholderImage;

    @FindBy(xpath = ".//*[text()='View docs']//parent::button")
    private Element goToDocsButton;

    @FindBy(xpath = ".//button[text() = 'Show me how']")
    private Element showMeHowButton;

    //-----------Test runs button-----------//
    @FindBy(xpath = "//div[@class='placeholder__buttons']")
    private ExtendedWebElement createFirstTestRunButton;

    public EmptyPlaceholder(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

//    public Boolean isEmptyPlaceholderImagePresent() {
//        return (Boolean) ((JavascriptExecutor) getDriver()).executeScript(
//                "return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" "
//                        + "&& arguments[0].naturalWidth > 0", emptyPlaceholderImage.getElement());
//    }

    public boolean isEmptyPlaceholderImagePresent() {
        return emptyPlaceholderImage.isElementPresent(3);
    }

    public String getEmptyPlaceHolderTitle() {
        return emptyPagePlaceholder.getText();
    }

    public String getEmptyPlaceHolderDescription() {
        return emptyPlaceholderDescription.getText();
    }

    public boolean isCreateFirstTestRunButtonPresent() {
        return createFirstTestRunButton.isElementPresent(3);
    }

    public boolean isEmptyPlaceholderTitlePresent() {
        return emptyPagePlaceholder.isPresent(3);
    }

    public boolean isEmptyPlaceholderDescriptionPresent() {
        return emptyPlaceholderDescription.isElementPresent(3);
    }

    public GettingStartedPage openGettingStartedPage() {
        showMeHowButton.click();
        return new GettingStartedPage(getDriver());
    }
}
