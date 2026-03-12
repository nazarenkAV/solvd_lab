package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class MarkAsReviewedModal extends AbstractModal {

    @FindBy(xpath = ".//textarea[@id='outlined-multiline-static']")
    private Element messageForReview;

    // TODO check this element work
    @FindBy(xpath = ".//input[@type='checkbox']")
    private Element checkBokForNotifications;

    @FindBy(xpath = ".//div[@class='modal-subfooter']//span[contains(text(), 'notification')]")
    private Element checkBoxDescriptionElement;

    public MarkAsReviewedModal(WebDriver driver) {
        super(driver);
    }

    public MarkAsReviewedModal(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public MarkAsReviewedModal inputReviewMessage(String reviewMessage) {
        messageForReview.sendKeys(reviewMessage);
        return this;
    }
}
