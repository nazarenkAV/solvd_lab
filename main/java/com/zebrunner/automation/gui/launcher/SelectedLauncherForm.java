package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
@Slf4j
public class SelectedLauncherForm extends AbstractUIObject {
    @FindBy(xpath = ".//h2[@class='selected-launcher__title']")
    private Element selectedLauncherTitle;

    @FindBy(xpath = "//div[@class='section-title__header']")
    private Element selectedPresetTitle;

    @FindBy(xpath = "//div[@class='section-title']//button[contains(@class,'section-title__arrow')]")
    private Element arrowButton;

    @FindBy(xpath = ".//button[text()='change defaults']")
    private Element changeDefaultsBtn;

    @FindBy(xpath = ".//section[@class='selected-launcher__section']")
    private SelectBranchSection selectedBranchSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _schedules')]")
    private SchedulesSection schedulesSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _execution-env')]")
    private ExecutionEnvSection executionEnvSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _env-variables')]")
    private EnvVariablesSection envVariablesSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _testing-platform')]")
    private TestingPlatformSection testingPlatformSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _custom-capabilities')]")
    private CustomCapabilitiesSection customCapabilitiesSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _notification-channels')]")
    private NotificationChannelsSection notificationChannelsSection;

    @FindBy(xpath = ".//section[contains(@class,'selected-launcher__section _footer')]")
    private FooterSection footerSection;

    @FindBy(xpath = "//div[@class='custom-vars-add__row']")
    private List<CustomVariableAddingForm> variablesList;

    public SelectedLauncherForm(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getSelectedLauncherName() {
        return selectedLauncherTitle.getText();
    }

    public void navigateBackViaArrowButton() {
        arrowButton.waitUntil(Condition.VISIBLE);
        arrowButton.click();
    }

    public void waitSelectedLauncherNameAppears() {
        selectedLauncherTitle.waitUntil(Condition.VISIBLE);
    }
}
