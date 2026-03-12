package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
public class MemberCard extends AbstractUIObject {
    private final String ROLE_INDEX = "[3]";
    private final String ADDED_INDEX = "[4]";
    private final String DELETE_INDEX = "[5]";

    @FindBy(xpath = ".//img")
    private ExtendedWebElement photo;

    @FindBy(xpath = ".//div[contains(@class,'member-name')]//span")
    private Element memberName;

    @FindBy(xpath = ".//td" + ROLE_INDEX)
    private Element role;

    @FindBy(xpath = ".//td" + ADDED_INDEX)
    private ExtendedWebElement addedDate;

    @FindBy(xpath = ".//div[contains(@class,'delete-member-button')]//button")
    private ExtendedWebElement deleteButton;

    @FindBy(xpath = "//*[@role='menu']//*[@class='member-role-dropdown__menu-item _title']")
    private List<Element> roles;

    @FindBy(xpath = ".//td" + ROLE_INDEX + "//button")
    private ExtendedWebElement selectRoleIcon;

    public MemberCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getMemberName() {
        return memberName.getText();
    }

    public UserInfoTooltip hoverMemberName() {
        memberName.hover();
        return new UserInfoTooltip(getDriver());
    }

    public void delete() {
        deleteButton.click();
        DeleteMemberModal deleteMemberModal = new DeleteMemberModal(getDriver());
        deleteMemberModal.delete();
    }

    public void changeRole(String roleName) {
        role.click();
        Element foundRole = WaitUtil.waitElementAppearedInListByCondition(roles,
                roleEl -> roleEl.getText().trim().equalsIgnoreCase(roleName),
                "Role " + roleName + "was found",
                "Role " + roleName + " was not found");

        foundRole.click();
        pause(1); // to update role on ui
    }

    public boolean isChangeRoleButtonPresent() {
        return role.isStateMatches(Condition.CLICKABLE);
    }

    public String getRole() {
        return role.getText();
    }


    @SneakyThrows
    public Date getAddedDate() {
        String pattern = "MMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(addedDate.getText());
    }

    public Boolean isClickableRole() {
        return role.isStateMatches(Condition.CLICKABLE);
    }

    public Boolean isDeleteButtonVisible() {
        return deleteButton.isVisible(2) && deleteButton.isClickable(2);
    }

    public Boolean isSelectRoleIconDisabled() {
        return selectRoleIcon.getAttribute("class").contains("_disabled");
    }
}
