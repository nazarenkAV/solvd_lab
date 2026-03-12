package com.zebrunner.automation.gui.iam;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.automation.gui.common.TenantBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class GroupsAndPermissionPage extends TenantBasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//b[@name='groupName' and contains(text(),'Admins')]")
    private ExtendedWebElement adminGroupTitle;

    @FindBy(css = ".span.md-button__text")
    private ExtendedWebElement newGroupButton;

    @FindBy(xpath = "//div[@class='user-groups__content']/div")
    private List<UserGroupCard> usersGroupsList;

    @FindBy(xpath = "//b[@name='groupName' and contains(text(),'Admins')]")
    private ExtendedWebElement uiLoadedMarker;

    public GroupsAndPermissionPage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
        pause(2);
    }

    public UserGroupCard getUserGroupByName(String groupName) {
        for (UserGroupCard userGroupCard : usersGroupsList) {
            if (userGroupCard.getGroupName().equalsIgnoreCase(groupName)) {
                LOGGER.info(("User group with name " + userGroupCard.getGroupName() + " was found!"));
                return userGroupCard;
            }
        }
        throw new RuntimeException("Groups with name " + groupName + " was not found!");
    }

}
