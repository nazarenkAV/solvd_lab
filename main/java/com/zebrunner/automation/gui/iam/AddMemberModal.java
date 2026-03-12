package com.zebrunner.automation.gui.iam;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import lombok.Getter;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.zebrunner.automation.util.StreamUtils;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.iam.user.UserSuggestionCard;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

@Getter
public class AddMemberModal extends AbstractModal<AddMemberModal> {

    public static final String MODAL_NAME = "Add member";

    @FindBy(xpath = ".//*[@class='modal-header__inner']//h4")
    protected Element modalTitle;

    @FindBy(xpath = ".//button[text()='Add']")
    protected Element addButton;
    @FindBy(xpath = ".//button[contains(@class,'main-modal__close-icon')][2]")
    // used [2] since we have 2 completely identical elements, I don't know why
    protected Element close;
    @FindBy(xpath = ".//input[@placeholder='Start typing to search']")
    private Element usernameInput;

    @FindBy(xpath = ".//div[@id='descriptive-dropdown']")
    private Element chooseRole;

    @FindBy(xpath = "//li[@role ='option']")
    private List<UserSuggestionCard> suggestions;

    @FindBy(xpath = "//div[@class='MuiAutocomplete-noOptions css-t0bixx']")
    private Element suggestionsError;

    @FindBy(xpath = "//li[@data-value]")
    private List<Element> roles;

    @FindBy(xpath = "//*[@role='listbox']//p[contains(@class, 'descriptive-dropdown__menu-item _title')]")
    private List<Element> rolesNames;

    @FindBy(xpath = "//*[@role='listbox']//p[contains(@class, 'descriptive-dropdown__menu-item _description')]")
    private List<Element> rolesDescriptions;
    @FindBy(xpath = ".//*[@class = 'add-members-modal__message']")
    private ExtendedWebElement addMembersModalMessage;

    @FindBy(xpath = "//*[@class = 'chips-text'][text() = '%s']/span[@class = 'chips-text _secondary']")
    private ExtendedWebElement readOnlyUserAnnotation;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public AddMemberModal(WebDriver driver) {
        super(driver);
    }

    public static AddMemberModal getModalInstance(WebDriver driver) {
        AddMemberModal addMemberModal = new AddMemberModal(driver);
        addMemberModal.pause(3);

        return addMemberModal;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getAddMembersModalMessage() {
        return addMembersModalMessage.getText();
    }

    public List<String> getRoles() {
        chooseRole.click();

        WaitUtil.waitCheckListIsNotEmpty(roles);

        return StreamUtils.mapToStream(rolesNames, Element::getText)
                          .map(String::trim)
                          .collect(Collectors.toList());
    }

    public List<String> getRoleDescriptions() {
        chooseRole.click();

        WaitUtil.waitCheckListIsNotEmpty(rolesDescriptions);

        return StreamUtils.mapToList(rolesDescriptions, Element::getText);
    }

    public UserSuggestionCard getUserSuggestionCard(String username) {
        return StreamUtils.findFirst(suggestions, suggestion -> suggestion.getUsername().equalsIgnoreCase(username))
                          .orElse(null);
    }

    private Element getRoleByName(String roleName) {
        this.closePopup();

        chooseRole.click();

        // getElementExcludeChildNodes need to exclude description of role and give only name
        return WaitUtil.waitElementAppearedInListByCondition(
                roles,
                role -> role.getText().contains(roleName),
                "Role with name " + roleName + " was found",
                "There are no role with name: " + roleName
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isAddMembersModalMessagePresent() {
        return addMembersModalMessage.isElementPresent(3);
    }

    public boolean isUsernameFieldPresent() {
        return usernameInput.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    public boolean isUsernamePresentInSuggestions(String username) {
        return this.getUserSuggestionCard(username) != null;
    }

    public boolean isRoleFieldPresent() {
        return chooseRole.isStateMatches(Condition.VISIBLE_AND_CLICKABLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void typeUsernameOrEmail(String usernameOrEmail) {
        usernameInput.sendKeys(usernameOrEmail);
    }

    public AddMemberModal typeUsername(String username) {
        usernameInput.sendKeys(username);
        return this;
    }

    public AddMemberModal typeUsernameWithSuggestionChoosing(String username) {
        this.typeUsername(username);
        this.getUserSuggestionCard(username).click();
        this.closePopup();

        return this;
    }

    public AddMemberModal chooseRoleByName(String roleName) {
        this.getRoleByName(roleName.trim()).click();
        return this;
    }

    public void chooseSuggestionUsername(String username) {
        this.getUserSuggestionCard(username).click();
        this.closePopup();
    }

    public void clickRandomRole() {
        chooseRole.click();
        WaitUtil.waitCheckListIsNotEmpty(roles);
        int roleId = new Random().nextInt(4);
        roles.get(roleId).click();
    }

    public void clickAddButton() {
        addButton.click();
    }

    public void fillMemberAndSubmitR(String username, String role) {
        typeUsernameWithSuggestionChoosing(username).chooseRoleByName(role)
                                                    .getSubmitButton().click();
        MembersPageR.getPageInstance(super.getDriver());
    }

    public String hoverAndGetReadOnlyUserAnnotationToolTip(String username) {
        readOnlyUserAnnotation.format(username).hover();

        return tooltip.getTooltipText();
    }

    public void closePopup() {
        super.pressTab();
    }

}
