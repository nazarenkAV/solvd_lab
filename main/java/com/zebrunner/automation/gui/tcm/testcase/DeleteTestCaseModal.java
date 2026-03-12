package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class DeleteTestCaseModal extends AbstractModal<DeleteTestCaseModal> {
    public static final String MODAL_TITLE = "Delete test case?";
    public static final String expectedModalContent = "You are about to delete the “%s“ test case. The deleted test case will be placed in a bin and won't be accessible from repository until restored.\n" +
            "Are you sure you want to proceed with this action?";

    @FindBy(xpath = ".//div[contains(@class,'modal-text-container')]")
    public Element modalContent;

    public DeleteTestCaseModal(WebDriver driver) {
        super(driver);
    }

    public String getModalContentText() {
        return modalContent.getText();
    }

    public String getExpectedModalContentText(String testCaseTitle) {
        return String.format(expectedModalContent, testCaseTitle);
    }

}