package com.zebrunner.automation.gui.reporting.launch;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

import com.zebrunner.automation.legacy.TestRunStatusEnumR;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

@Deprecated
public class StatusR extends AbstractUIObject {

    public StatusR(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public TestRunStatusEnumR getStatusColourFromCss() {
        String colour = this.getRootExtendedElement().getElement().getCssValue("border-left-color");
        return TestRunStatusEnumR.colourStatus(Color.fromString(colour).asHex());
    }

}

