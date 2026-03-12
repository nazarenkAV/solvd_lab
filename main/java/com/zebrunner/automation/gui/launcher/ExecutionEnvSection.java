package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Optional;

@Getter
@Slf4j
public class ExecutionEnvSection extends AbstractUIObject {
    private final String DOCKER_IMAGE_DROPDOWN_LOCATOR = "//*[@id='dockerImage-listbox']/li";
    @FindBy(xpath = "//section[contains(@class, '_execution-env')]//div[@class='dropdown-with-icon']")
    private Element executionEnv;

    @FindBy(xpath = ".//div[contains(@class,'selected-system-label')]")
    private Element executionEnvButton;

    @FindBy(xpath = "//div[@aria-labelledby='select-instanceTypeId']")
    private Element executionInstanceType;

    @FindBy(xpath = "//li[contains(@class, 'css-19vrvbx')]")
    private List<Element> executionEnvList;

    @FindBy(xpath = ".//button[contains(@class,'css-1pblrdc')]")
    private Element execEnvSelectBtn;

    @FindBy(id = "dockerImage")
    private Element dockerImageInput;

    @FindBy(xpath = DOCKER_IMAGE_DROPDOWN_LOCATOR)
    private List<Element> dockerImageList;

    @FindBy(id = "launchCommand")
    private Element launchCommandInput;

    @FindBy(xpath = ".//button[contains(@class,'selected-launcher__button _expand')]")
    private Element expandBtn;

    @FindBy(xpath = ".//input[@id='var']")
    private Element envVarsInput;

    @FindBy(xpath = ".//button[contains(@class,'custom-vars-view__input-icon')]")
    private Element viewEnvVarsButton;

    public ExecutionEnvSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getLaunchCommand() {
        if (!launchCommandInput.isStateMatches(Condition.VISIBLE)) {
            expandBtn.click();
        }
        waitUntil(ExpectedConditions.attributeToBeNotEmpty(launchCommandInput.getElement(), "value"), 3);
        return launchCommandInput.getAttributeValue("value");
    }

    public boolean isLaunchCommandClickable() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Checking launch command field...");
        if (!launchCommandInput.isStateMatches(Condition.VISIBLE)) {
            expandBtn.click();
        }
        return launchCommandInput.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isLaunchCommandVisible() {
        launchCommandInput.waitUntil(Condition.VISIBLE);
        return launchCommandInput.isStateMatches(Condition.VISIBLE);
    }

    public String getInstanceType() {
        if (!executionInstanceType.isStateMatches(Condition.VISIBLE)) {
            expandBtn.click();
        }
        return executionInstanceType.getText();
    }

    public String getDockerImage() {
        if (!dockerImageInput.isStateMatches(Condition.VISIBLE)) {
            expandBtn.click();
        }
        return dockerImageInput.getAttributeValue("value");
    }

    public boolean isDockerImageClickable() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Checking docker image field...");
        if (!dockerImageInput.isStateMatches(Condition.VISIBLE)) {
            expandBtn.click();
        }
        return dockerImageInput.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isDockerImageVisible() {
        dockerImageInput.waitUntil(Condition.VISIBLE);
        return dockerImageInput.isStateMatches(Condition.VISIBLE);
    }

    public boolean isExecutionEnvDisabled() {
        return executionEnvButton.getRootExtendedElement().getAttribute("class")
                .contains("_disabled");
    }

    public String getEnvVariables() {
        log.info("Getting all env variables...");
        envVarsInput.waitUntil(Condition.VISIBLE);
        String envVars = envVarsInput.getAttributeValue("value");
        log.info(envVars);
        return envVars;
    }

    public EnvironmentVariablesModal openEnvironmentVariableModal() {
        viewEnvVarsButton.click();
        pause(3);
        return new EnvironmentVariablesModal(getDriver());
    }

    public ExecutionEnvSection findAndChooseDockerImage(String imageName) throws NoSuchElementException {
        PageUtil.guaranteedToHideDropDownList(getDriver());

        if (!dockerImageInput.getAttributeValue("value").equalsIgnoreCase(imageName)) {
            dockerImageInput.click();
            waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(DOCKER_IMAGE_DROPDOWN_LOCATOR), 1), 5);

            Optional<Element> searchableImage = dockerImageList.stream()
                    .filter(image -> image.getText().trim().equalsIgnoreCase(imageName))
                    .findFirst();
            if (searchableImage.isPresent()) {
                waitUntil(ExpectedConditions.elementToBeClickable(searchableImage.get().getElement()), 2);
                searchableImage.get().click();
            } else
                throw new NoSuchElementException(String.format("Docker image '%s' was not found", imageName));
            return this;
        }
        return this;
    }

    public ExecutionEnvSection findAndChooseExecutionInstanceType(String instanceTypeName) throws NoSuchElementException {
        executionInstanceType.click();
        Optional<Element> searchableInstanceType = executionEnvList.stream()
                .filter(instanceType -> instanceType.getText().trim().equalsIgnoreCase(instanceTypeName))
                .findFirst();

        if (searchableInstanceType.isPresent()) {
            searchableInstanceType.get().click();
        } else {
            throw new NoSuchElementException(String.format("Instance type '%s' was not found", instanceTypeName));
        }
        return this;
    }


    public ExecutionEnvSection typeLaunchCommand(String launchCommand) {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        launchCommandInput.getElement().clear();
        launchCommandInput.sendKeys(launchCommand);
        return this;
    }

    public ExecutionEnvSection findAndChooseExecutionEnv(String execEnvName) throws NoSuchElementException {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        executionEnv.click();
        Element foundExEnv = WaitUtil.waitElementAppearedInListByCondition(executionEnvList,
                env -> {
                    log.info(env.getText());
                    return env.getText().equalsIgnoreCase(execEnvName);
                },
                "Execution environment with name " + execEnvName + " was found",
                "Execution environment name " + execEnvName + " was not found");
        foundExEnv.click();
        return this;
    }

    public ExecutionEnvSection clickExpandButton() {
        expandBtn.click();
        return this;
    }

    public String getExecutionEnvValue() {
        return executionEnv.getText();
    }
}
