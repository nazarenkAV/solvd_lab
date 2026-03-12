package com.zebrunner.automation.gui.iam;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ApiAccessTab extends AbstractUIObject {

    @FindBy(xpath = ".//*[text()='TOKEN']//parent::button")
    private ExtendedWebElement addTokenButton;

    public ApiAccessTab(WebDriver driver) {
        super(driver);
        setBy(By.xpath("//div[@class='user-profile__content']"));
    }

    public boolean isAddTokenButtonPresent() {
        return addTokenButton.isElementPresent();
    }

    public AddTokenModal openAddTokenModal() {
        addTokenButton.click();
        return new AddTokenModal(driver);
    }
}
