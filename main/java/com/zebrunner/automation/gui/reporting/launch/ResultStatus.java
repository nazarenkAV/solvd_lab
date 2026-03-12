package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.automation.gui.Element;
import lombok.Getter;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

@Getter
public class ResultStatus extends AbstractUIObject {

    @FindBy(xpath = "//div[contains(@class, 'wrapper _collapsed')]")
    private Element card;


    public ResultStatus(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public TestRunStatusEnumR getStatusColourFromCss() {
        String colour = card.getElement().getCssValue("border-left-color");
        return TestRunStatusEnumR.colourStatus(Color.fromString(colour).asHex());
    }

}
