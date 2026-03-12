package com.zebrunner.automation.gui.launcher.preset;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Slf4j
@Getter
public class DeletePresetModal extends AbstractModal<DeletePresetModal> {
    public static final String MODAL_NAME = "Delete preset";
    @FindBy(xpath = "//div[@class='modal-content__body _no-bottom-padding']")
    private Element modalContent;

    public DeletePresetModal(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public DeletePresetModal(WebDriver driver) {
        super(driver);
    }

    public static DeletePresetModal getModalInstance(WebDriver driver) {
        return new DeletePresetModal(driver);
    }

}
