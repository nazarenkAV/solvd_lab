package com.zebrunner.automation.gui.iam;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.List;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.common.ZbrSearch;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.TenantProjectBasePage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Getter
public class MembersPageR extends TenantProjectBasePage {

    public static final String PAGE_NAME = "Members";

    public static final String PAGE_URL = ConfigHelper.getTenantUrl() + "/projects/%s/members";

    @FindBy(xpath = "//tr[contains(@class,'MuiTableRow-root') and not(contains(@class,'MuiTableRow-head'))]")
    private List<MemberCard> memberCards;

    @FindBy(xpath = ZbrSearch.ROOT_XPATH)
    private ZbrSearch search;

    @FindBy(xpath = "//span[text()='member']//parent::button")
    private Element addMemberButton;

    @FindBy(xpath = ".//*[contains(@class,'table-header-cell _name')]")
    private ExtendedWebElement usernameTableHeader;

    @FindBy(xpath = ".//*[contains(@class,'table-header-cell _role')]")
    private ExtendedWebElement roleTableHeader;

    @FindBy(xpath = ".//*[contains(@class,'table-header-cell _added')]")
    private ExtendedWebElement addedTableHeader;

    @FindBy(xpath = "//thead[contains(@class,'MuiTableHead-root members-table__head')]")
    private ExtendedWebElement uiLoadedMarker;

    public MembersPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(uiLoadedMarker);
    }

    public static MembersPageR getPageInstance(WebDriver driver) {
        return new MembersPageR(driver);
    }

    public static MembersPageR openPageDirectly(WebDriver driver, String projectKey) {
        MembersPageR membersPageR = new MembersPageR(driver);
        membersPageR.openURL(String.format(PAGE_URL, projectKey));
        membersPageR.assertPageOpened();
        return membersPageR;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getUsernameTableTitle() {
        return usernameTableHeader.getText().trim();
    }

    public String getRoleTableTitle() {
        return roleTableHeader.getText().trim().replace("unfold_more", "");
    }

    public String getAddingDate() {
        return addedTableHeader.getText().trim().replace("unfold_more", "");
    }

    public MemberCard getMemberByName(String username) {
        super.pause(3);

        return WaitUtil.waitElementAppearedInListByCondition(
                memberCards,
                card -> card.getMemberName().equalsIgnoreCase(username),
                "Found member with name " + username,
                "Can't find user with username " + username
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isSearchFieldPresent() {
        return search.isVisible(2) && search.isClickable(2);
    }

    public boolean isAddMemberButtonActive() {
        return addMemberButton.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isMemberPresent(String username) {
        return WaitUtil.waitAndCheckElementAppearedInListByCondition(
                memberCards,
                card -> card.getMemberName().equalsIgnoreCase(username)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddMemberModal openAddMemberModal() {
        addMemberButton.click();
        return AddMemberModal.getModalInstance(getDriver());
    }

    public void typeInSearchField(String username) {
        search.getSearchField().sendKeys(username);
    }

    public void addMemberToProject(String username, RoleEnum roleEnum) {
        AddMemberModal addMemberModal = openAddMemberModal();
        addMemberModal.fillMemberAndSubmitR(username, roleEnum.getName());
    }

    public void sortByRole() {
        roleTableHeader.findElement(By.xpath(".//*[local-name()='svg']")).click();
        super.pause(3);
    }

    public void sortByAdded() {
        addedTableHeader.findElement(By.xpath(".//*[local-name()='svg']")).click();
        super.pause(3);
    }

}
