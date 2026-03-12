package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.legacy.UserGroup;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
@Getter
public class InviteUserModal extends AbstractModal<InviteUserModal> {

    public static final String MODAL_NAME = "Invite users";

    @FindBy(xpath = ".//*[@id='modal-header']")
    private Element modalHeader;

    @FindBy(xpath = ".//*[text() = 'Enter emails']/parent::div//input")
    private Element emailInput;

    @FindBy(xpath = "//label[text()='Choose group']/parent::div")
    private Element chooseGroup;

    @FindBy(xpath = "//li[contains(@class,'MuiMenuItem-gutters')]")
    private List<ExtendedWebElement> groupsList;

    @FindBy(xpath = "//button[text()='Invite']")
    private Element inviteButton;

    @FindBy(xpath = "//*[@class = 'checkbox-label'][contains(text(), 'Read-only')]/parent::div//input")
    private Element readOnlyUserCheckBox;

    public InviteUserModal(WebDriver driver) {
        super(driver);
    }

    public static InviteUserModal getInstance(WebDriver driver) {
        return new InviteUserModal(driver);
    }

    public InviteUserModal inputEmail(String email) {
        emailInput.sendKeys(email, true, true);
        modalHeader.click();
        return this;
    }

    public InviteUserModal chooseGroup(String groupName) {
        chooseGroup.click();
        log.info("Waiting for group list to load...");
        WaitUtil.waitElementAppearedInListByCondition(groupsList,
                        el -> el.getText().equalsIgnoreCase(groupName),
                        "Group with name" + groupName + " was selected!",
                        "Can't find group " + groupName)
                .click();
        return InviteUserModal.getInstance(getDriver());
    }

    public boolean isChooseGroupPresent() {
        return chooseGroup.isStateMatches(Condition.PRESENT);
    }

    public boolean isEmailInputPresent() {
        return emailInput.isStateMatches(Condition.PRESENT);
    }

    public boolean isInviteButtonActive() {
        return inviteButton.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public UsersPageR fillInviteUserForm(User user) {
        log.info("Filling 'Invite user' popup...");
        return this.chooseGroup(user.getZebrunnerRole())
                .inputEmail(user.getEmail())
                .clickInvite();
    }

    public UsersPageR fillInviteUserFormForReadOnlyUser(User user) {
        chooseGroup(user.getZebrunnerRole())
                .inputEmail(user.getEmail());
        clickReadOnlyUserCheckBox();
        return clickInvite();
    }

    public UsersPageR sendInvitation(String email, UserGroup group) {
        chooseGroup(group.getName());
        inputEmail(email);
        return clickInvite();
    }

    public UsersPageR clickInvite() {
        inviteButton.click();
        log.info("'Invite user' popup was filled!");
        UsersPageR invitationsPageR = new UsersPageR(getDriver());
        invitationsPageR.assertPageOpened();
        return invitationsPageR;
    }

    public void clickReadOnlyUserCheckBox() {
        readOnlyUserCheckBox.click();
    }
}
