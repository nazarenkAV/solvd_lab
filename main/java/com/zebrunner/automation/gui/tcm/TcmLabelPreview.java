package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.common.SvgPaths;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.external.TestRailLogInPage;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

@Getter
public class TcmLabelPreview extends AbstractUIObject {
    public static final String ROOT_XPATH = "//*[contains(@class, 'tcm-label-preview-wrapper')]";
    private final String attributesXpath = "//*[@class ='tcm-label-preview__attribute-name']";

    @FindBy(xpath = ROOT_XPATH)
    private ExtendedWebElement tcmPreviewRootElement;

    @FindBy(xpath = "//div[@class='tcm-label-preview__content']")
    private ExtendedWebElement tcmPreviewContent;

    @FindBy(xpath = ".//*[@class='tcm-label-preview__title-link']")
    private Element caseTitle;

    @FindBy(xpath = ".//*[@class='tcm-label-preview__title-link']/a")
    private ExtendedWebElement caseTitleLink;

    @FindBy(xpath = ".//*[@class='tcm-label-preview__steps']/div")
    private List<CaseStep> caseSteps;

    @FindBy(xpath = ".//*[@d = '" + SvgPaths.CLOSE_X_ICON +
            "']//ancestor::button")
    private ExtendedWebElement closeButton;

    @FindBy(xpath = "//*[contains(@class, 'tcm-label-preview__content-wrapper scroll-shadow')]")
    private Element scrollableElement;

    @FindBy(xpath = attributesXpath)
    private List<ExtendedWebElement> attributes;

    @FindBy(xpath = "//*[@class ='tcm-label-preview__error-text']")
    private ExtendedWebElement errorText;

    @FindBy(xpath = "//*[contains(@class, 'testrail-notification')]")
    private ExtendedWebElement testCaseStatusNotification;

    public TcmLabelPreview(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public void close() {
        closeButton.click();
    }

    public String getCaseTitleText() {
        caseTitle.waitUntil(Condition.VISIBLE);
        return caseTitle.getText();
    }

    public Optional<CaseStep> findWithAction(String action) {
        return caseSteps.stream()
                .filter(caseStep ->
                        caseStep.getActionText()
                                .equalsIgnoreCase(action))
                .findFirst();
    }

    public TestRailLogInPage clickCaseTitleLink() {
        caseTitleLink.click();
        return new TestRailLogInPage(getDriver());
    }

    private ExtendedWebElement findAttributeByName(String name) {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), attributesXpath);
        for (ExtendedWebElement attribute : attributes) {
            if (attribute.getText().equals(name)) {
                return attribute;
            }
        }
        throw new RuntimeException("Attribute with name " + name + " not found");
    }

    public void scrollToAttribute(String name) {
        findAttributeByName(name).scrollTo();
    }

    public String getErrorText() {
        return errorText.getText();
    }

    public String getTestCaseStatusNotification() {
        return testCaseStatusNotification.getText();
    }

    public boolean isStatusNotificationPresent() {
        return testCaseStatusNotification.isElementPresent(3);
    }

    public boolean isTcmLabelPreviewOpened() {
        return tcmPreviewRootElement.isElementPresent(3);
    }
}
