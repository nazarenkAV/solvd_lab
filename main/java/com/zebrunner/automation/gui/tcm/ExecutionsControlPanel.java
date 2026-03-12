package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.gui.common.ZbrTimeInput;
import com.zebrunner.automation.gui.reporting.launch.LinkIssueModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class ExecutionsControlPanel extends AbstractUIObject {

    private static final String ROOT_XPATH = "//div[contains(@class, 'test-cases-preview-modal__content')]";

    @FindBy(xpath = ZbrTimeInput.ROOT_XPATH)
    private ZbrTimeInput zbrTimeInput;

    @FindBy(xpath = ".//label[text()='Type']/following-sibling::div")
    private ExtendedWebElement typeStatusButton;

    @FindBy(xpath = ".//label[text()='Result']/following-sibling::div")
    private ExtendedWebElement resultsButton;

    @FindBy(xpath = "//div[contains(@class, 'modal-content__controls ')]/button")
    private ExtendedWebElement linkIssueButton;

    public ExecutionsControlPanel(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public LinkIssueModal clickLinkIssueButton() {
        linkIssueButton.click();
        return new LinkIssueModal(getDriver());
    }
}
