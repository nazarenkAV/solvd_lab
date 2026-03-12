package com.zebrunner.automation.gui.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.decorator.annotations.CaseInsensitiveXPath;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@Getter
public class ZbrTimeInput extends AbstractUIObject {
    public static final String ROOT_XPATH = ".//*[contains(@class,'ZbrTimeInput')]";

    @FindBy(xpath = ".//input[@name='seconds']")
    private ExtendedWebElement secondsInput;

    @FindBy(xpath = ".//input[@name='minutes']")
    private ExtendedWebElement minutesInput;

    @FindBy(xpath = ".//input[@name='hours']")
    private ExtendedWebElement hoursInput;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Start timer']")
    private ExtendedWebElement startButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Reset']")
    private ExtendedWebElement resetTimeButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Stop']")
    private ExtendedWebElement stopButton;

    @CaseInsensitiveXPath
    @FindBy(xpath = ".//button[text()='Resume']")
    private ExtendedWebElement resumeButton;

    public ZbrTimeInput(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public String getCurrentSecondsValue() {
        return secondsInput.getAttribute("value");
    }

    public String getCurrentMinutesValue() {
        return minutesInput.getAttribute("value");
    }

    public String getCurrentHoursValue() {
        return hoursInput.getAttribute("value");
    }

    public String getCurrentTime() {
        return getCurrentHoursValue() + ":" + getCurrentMinutesValue() + ":" + getCurrentSecondsValue();
    }

    public void setSecondsValue(Integer countSeconds) {
        secondsInput.type(countSeconds.toString());
    }

    public void setMinutesValue(Integer countMinutes) {
        minutesInput.type(countMinutes.toString());
    }

    public void setHoursValue(Integer countHours) {
        hoursInput.type(countHours.toString());
    }

    public void setTimeValue(Integer countHours, Integer countMinutes, Integer countSeconds) {
        setHoursValue(countHours);
        setMinutesValue(countMinutes);
        setSecondsValue(countSeconds);
    }

    public void clickResetButton() {
        resetTimeButton.click();
    }

    public void clickHoursInput() {
        hoursInput.click();
    }

    public void clickMinutesInput() {
        minutesInput.click();
    }

    public void clickSecondsInput() {
        secondsInput.click();
    }

    public void clickStartButton() {
        startButton.click();
    }

    public void clickStopButton() {
        stopButton.click();
    }

    public void clickResumeButton() {
        resumeButton.click();
    }

    public boolean isStartButtonPresent() {
        return startButton.isPresent(1);
    }

    public boolean isStopButtonPresent() {
        return stopButton.isPresent(1);
    }

    public boolean isResetTimeButtonPresent() {
        return resetTimeButton.isPresent(1);
    }

    public boolean isResumeButtonPresent() {
        return resumeButton.isPresent(1);
    }

    public boolean isElapsedTimeFieldEditable() {
        return !this.getAttribute("class")
                .contains("_disabled");
    }
}
