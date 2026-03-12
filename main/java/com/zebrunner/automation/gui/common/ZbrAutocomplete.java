package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class ZbrAutocomplete extends AbstractUIObject {

    public static final String PARENT_ROOT_XPATH = "//parent::div[contains(@class,'zbr-autocomplete   MuiBox-root')]";
    public static final String ROOT_XPATH = "//div[contains(@class,'zbr-autocomplete   MuiBox-root')]";

    @FindBy(xpath = ".//input")
    private Element input;

    @FindBy(xpath = ".//*[contains(@class,'zbr-autocomplete__no-options-text')]")
    private Element errorMessage;

    @FindBy(xpath = ".//li")
    private List<Element> autocompleteOptions;

    public ZbrAutocomplete(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[contains(@class,'zbr-autocomplete') and contains(@class, 'MuiPopper-root')]"));
    }

    public String getValue() {
        return input.getAttributeValue("value");
    }

    public String getErrorMessageText() {
        return errorMessage.getText();
    }

    public List<String> getOptionList() {
        WaitUtil.waitCheckListIsNotEmpty(autocompleteOptions);
        return autocompleteOptions.stream().map(Element::getText).collect(Collectors.toList());
    }

    public Optional<Element> getOption(String option) {
        WaitUtil.waitCheckListIsNotEmpty(autocompleteOptions);
        return autocompleteOptions.stream().filter(op -> op.getText().equalsIgnoreCase(option)).findFirst();
    }

    public Optional<Element> getAny() {
        WaitUtil.waitCheckListIsNotEmpty(autocompleteOptions);
        return autocompleteOptions.stream().findAny();
    }
}
