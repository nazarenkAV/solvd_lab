package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.gui.tcm.TabWysiwygContainer;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractGeneralTab<T extends AbstractGeneralTab> extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[@role='tabpanel']";

    @FindBy(xpath = ".//div[text()='Description']" + TabWysiwygContainer.ROOT_XPATH)
    private TabWysiwygContainer descriptionInput;

    @FindBy(xpath = RepositoryPreviewStepContainer.ROOT_XPATH)
    protected List<RepositoryPreviewStepContainer> steps;

    @FindBy(xpath = ".//*[contains(@class,'tab-wysiwyg-title')" +
            " or contains(@class,'accordion__title') or contains(@class, 'title-container')" +
            "or contains(@class,'test-case-tab-content-requirements__wrapper')]")
    protected List<Element> fieldNames;//to verify hat 'General' tab contains those fields which are added in Test case field for 'General' tab


    public AbstractGeneralTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public T inputAndSaveDescription(String description) {
        descriptionInput
                .input(description)
                .clickSaveButton();
        return (T) this;
    }

    public List<String> getExistingFieldNames() {
        return fieldNames.stream().map(Element::getText).collect(Collectors.toList());
    }

    public String getDescriptionText() {
        return descriptionInput.getContentValue();
    }
}
