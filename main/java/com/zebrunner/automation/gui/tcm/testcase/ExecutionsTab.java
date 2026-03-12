package com.zebrunner.automation.gui.tcm.testcase;

import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
@Slf4j
public class ExecutionsTab extends AbstractUIObject {

    public static final String ROOT_XPATH = ".//div[@role='tabpanel' and contains(@id,'Executions')]";

    @FindBy(xpath = ExecutionItem.ROOT)
    private List<ExecutionItem> executions;


    public ExecutionsTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath(ROOT_XPATH));
    }

    public ExecutionItem getLastExecution() {
        return getExecutions().get(0);
    }

    public List<ExecutionItem> getExecutions() {
        WaitUtil.waitCheckListIsNotEmpty(executions);
        return executions;
    }
}
