package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Getter
@Slf4j
public class AttachmentsTab extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[@role='tabpanel' and contains(@id,'Attachments')]";

    @FindBy(xpath = AttachmentItem.ROOT)
    private List<AttachmentItem> attachments;

    ///only for Dedicated Test case page
    @FindBy(xpath = "//div[contains(@class,'attachment-card') and @role='button']//input")
    public ExtendedWebElement addAttachmentInput;

    @FindBy(xpath = "//div[contains(@class,'attachment-card__empty-placeholder')]")
    public ExtendedWebElement emptyPlaceholder;


    public AttachmentsTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public AttachmentItem getAttachment(String name) {
        WaitUtil.waitCheckListIsNotEmpty(attachments);
        return attachments.stream()
                .filter(a -> a.hoverAndGetAttachmentName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No attachments with name " + name));
    }

    public Optional<AttachmentItem> getOptionalAttachment(String name) {
        return attachments.stream()
                .filter(a -> a.hoverAndGetAttachmentName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean isEmptyPlaceholderPresent() {
        return emptyPlaceholder.isVisible(3);
    }

    public String getEmptyPlaceholderColor() {
        pause(2);
        return ColorUtil.getHexColorFromString(emptyPlaceholder.getCssValue("background-color"));
    }
}
