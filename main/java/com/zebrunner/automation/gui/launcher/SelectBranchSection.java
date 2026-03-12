package com.zebrunner.automation.gui.launcher;

import com.zebrunner.automation.gui.common.ZbrAutocomplete;
import com.zebrunner.automation.gui.common.ZbrAutocompleteInput;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.PageUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Optional;

@Getter
@Slf4j
public class SelectBranchSection extends AbstractUIObject {

    @FindBy(xpath = ".//div[text()='Branch']//parent::" + ZbrAutocompleteInput.ROOT_XPATH)
    private ZbrAutocompleteInput branchInput;

    public SelectBranchSection(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public SelectBranchSection findBranchAndChoose(String branchName) throws NoSuchElementException {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        branchInput.selectValue(branchName);
        return this;
    }

    public String selectAnyBranchAndClickOnIt() throws NoSuchElementException {
        String branchName = null;
        PageUtil.guaranteedToHideDropDownList(getDriver());
        branchInput.click();

        ZbrAutocomplete zbrAutocomplete = new ZbrAutocomplete(getDriver());
        Optional<Element> searchableBranch = zbrAutocomplete.getAny();

        if (searchableBranch.isPresent()) {
            branchName = searchableBranch.get().getText();
            log.info("Selected brunch is " + branchName);

            waitUntil(ExpectedConditions.elementToBeClickable(searchableBranch.get().getElement()), 2);
            searchableBranch.get().click();
        } else
            throw new NoSuchElementException(" No branch was selected");
        return branchName;
    }

    public String getBranchValue() {
        return branchInput.getValue();
    }

    public boolean isBranchFieldActive() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Checking branch field...");
        return branchInput.getRootExtendedElement().isClickable(3)
                && branchInput.getRootExtendedElement().isVisible(3);
    }

    public boolean isBranchFieldDisabled() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        log.info("Checking branch field...");
        return branchInput.isDisabled();
    }

    public SelectBranchSection clickBranch() {
        PageUtil.guaranteedToHideDropDownList(getDriver());
        branchInput.doubleClick();
        return this;
    }
}
