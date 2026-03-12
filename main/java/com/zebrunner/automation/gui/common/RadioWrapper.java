package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class RadioWrapper extends AbstractUIObject {

    public static final String ROOT_XPATH = "//ancestor::div[contains(@class,'radio-wrapper')]";

    @FindBy(xpath = ".//input")
    private Element input;

    @FindBy(xpath = ".//span[contains(@class,'MuiRadio-root')]")
    private Element radioChecker;

    public RadioWrapper(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void click() {
        input.click();
    }

    public boolean isChecked() {
        return radioChecker.getAttributeValue("class").contains("Mui-checked");
    }
}
