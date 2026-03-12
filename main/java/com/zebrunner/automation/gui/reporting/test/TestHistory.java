package com.zebrunner.automation.gui.reporting.test;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

import lombok.Getter;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;

@Getter
public class TestHistory extends AbstractUIObject {

    public final String HISTORY_ITEM_COLOR_XPATH = ".//*[local-name()='svg']//*[local-name()='path' and @d='m0 4 4-2-4-2 1 2-1 2ZM4 4h155l-.003-4H4v4ZM159 4l1-2-1-2v4Z']";

    @FindBy(xpath = ".//div[contains(@class, 'swiper-slide ')]")
    private List<Element> historyItems;

    public TestHistory(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public Optional<Element> pollHistoryItemByTestId(Long testId) {
        return WaitUtil.waitComponentInListByCondition(
                historyItems,
                historyItem -> historyItem.getRootExtendedElement()
                                          .getAttribute("id")
                                          .trim()
                                          .equalsIgnoreCase(String.valueOf(testId))
        );
    }

    public String getHistoryItemColorByTestId(Long testId) {
        Element historyItem = this.pollHistoryItemByTestId(testId)
                                  .orElseThrow(() -> new RuntimeException("Could not find history item by attribute 'id' equals to '" + testId + "'"));

        String fillCssValue = historyItem.getRootExtendedElement()
                                         .findExtendedWebElement(By.xpath(HISTORY_ITEM_COLOR_XPATH))
                                         .getElement()
                                         .getCssValue("fill");

        return Color.fromString(fillCssValue).asHex();
    }

}
