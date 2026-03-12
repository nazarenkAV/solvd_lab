package com.zebrunner.automation.gui.iam;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.gui.common.TenantBasePage;
import com.zebrunner.automation.gui.iam.user.UserCard;
import com.zebrunner.automation.gui.iam.user.UserProcessModal;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class UsersPageR extends TenantBasePage {

    public static final String PAGE_NAME = "Users";
    public static final String URL_MATCHER =
            "https://.+\\.zebrunner\\..+/settings/users(/|\\z)";
    public static final String PAGE_URL =
            ConfigHelper.getTenantUrl() + "/settings/users";

    @FindBy(xpath = "//h1[text() = 'Users']")
    private ExtendedWebElement pageHeader;

    @FindBy(xpath = "//div[@class='users-table__row']")
    private List<UserCard> userCards;

    @FindBy(xpath = "//small[@class='fixed-page-header__additional-text ng-binding ng-scope']")
    private ExtendedWebElement usersNumber;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='ID']")
    private ExtendedWebElement idTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='Username']")
    private ExtendedWebElement usernameTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='Email']")
    private ExtendedWebElement emailTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='Full name']")
    private ExtendedWebElement fullNameTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='Group']")
    private ExtendedWebElement groupTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='Source']")
    private ExtendedWebElement sourceTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//div[text()='Registration/ last activity']")
    private ExtendedWebElement lastActivityTitle;

    @FindBy(xpath = "//div[@class='users-table users-table__header']//span[@class='status-menu__button-text']")
    private ExtendedWebElement statusTitle;

    @FindBy(xpath = "//span[@class='status-icon active status-menu__dropdown-text']/ancestor::button")
    private ExtendedWebElement activeStatus;

    @FindBy(xpath = "//span[@class='status-icon inactive status-menu__dropdown-text']/ancestor::button")
    private ExtendedWebElement inactiveStatus;

    @FindBy(xpath = "//span[@class='status-icon  status-menu__dropdown-text']/ancestor::button")
    private ExtendedWebElement allStatus;

    @FindBy(xpath = "//input[@placeholder='Search users']")
    private Element searchUserField;

    @FindBy(xpath = "//md-icon[@class='input-close-icon material-icons']")
    private ExtendedWebElement flushSearchField;

    @FindBy(xpath = "//*[@aria-label  = 'Create user']")
    private Element createUserButton;

    @FindBy(xpath = "//div[@aria-label='Invite users']/button")
    private Element inviteUserButton;

    @FindBy(xpath = "//*[@class = 'users__seats-info']")
    private ExtendedWebElement userSeatsInfo;

    public UsersPageR(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(pageHeader);
    }

    public static UsersPageR getPageInstance(WebDriver driver) {
        return new UsersPageR(driver);
    }

    public static UsersPageR openPageDirectly(WebDriver driver) {
        UsersPageR usersPage = new UsersPageR(driver);
        usersPage.openURL(PAGE_URL);
        usersPage.assertPageOpened();
        return usersPage;
    }

    @Override
    public boolean isPageOpened() {
        boolean isUrlMatches = waitUntil(ExpectedConditions.urlMatches(URL_MATCHER), DEFAULT_EXPLICIT_TIMEOUT);
        return isUrlMatches && super.isPageOpened();
    }

    public UsersPageR openPageDirectly() {
        this.openURL(PAGE_URL);
        assertPageOpened();
        return this;
    }

    public boolean isSearchButtonClickable() {
        return searchUserField.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isInviteUserButtonClickable() {
        return inviteUserButton.isStateMatches(Condition.CLICKABLE);
    }

    public boolean isCreateUserButtonClickable() {
        return createUserButton.isStateMatches(Condition.CLICKABLE);
    }

    public void searchUser(String username) {
        searchUserField.type(username);
    }

    public UserCard getUserCard(String username) {
        UserCard searchableUserCard = null;
        for (UserCard user : userCards) {
            if (user.getUserName().equalsIgnoreCase(username)) {
                searchableUserCard = user;
                break;
            }
        }
        return searchableUserCard;
    }

    public UserCard getUserCardByEmail(String email) {
        UserCard searchableUserCard = null;
        for (UserCard user : userCards) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                searchableUserCard = user;
                break;
            }
        }
        return searchableUserCard;
    }

    public List<UserCard> getUserCards() {
        return userCards;
    }

    public List<String> getAllUseNamesExceptAnonymousAndAdmin() {
        WaitUtil.waitCheckListIsNotEmpty(userCards);
        List<String> usersNamesList = userCards.stream()
                .map(userCard -> userCard.getUserName())
                .filter(userName -> !userName.equalsIgnoreCase("admin"))
                .filter(userName -> !userName.equalsIgnoreCase("anonymous"))
                .collect(Collectors.toList());
        log.info("List of existing usernames: " + usersNamesList);
        return usersNamesList;
    }

    public UserProcessModal clickNewUserButton() {
        createUserButton.click();
        return new UserProcessModal(getDriver());
    }

    public boolean isUserPresentInListOfUsers(String username) {
        log.debug("Waiting for userCards list to load...");
        pause(3);
        for (UserCard userCard : userCards) {
            log.debug("UserCard with name " + userCard.getUserName());
            if (userCard.getUserName().equalsIgnoreCase(username)) {
                return true;
            }
        }
        log.info("User with username " + username + " was not find!");
        return false;
    }

    public InviteUserModal openInviteUserModal() {
        pause(3);
        inviteUserButton.click();
        return new InviteUserModal(getDriver());
    }

    public int getSeatsInUse() {
        String seatsInfoText = userSeatsInfo.getText();

        String numberRegex = "\\d+";
        Pattern pattern = Pattern.compile(numberRegex);
        Matcher matcher = pattern.matcher(seatsInfoText);

        if (matcher.find()) {
            String numberString = matcher.group();

            return Integer.parseInt(numberString);
        } else {
            return -1;
        }
    }

    public void createUser(String username, String password, String email, boolean readOnly) {
        UserProcessModal newUserModal = clickNewUserButton();
        newUserModal.fillNewUserForm(username, password,email);
        if (readOnly == true) {
            newUserModal.clickReadOnlyUserCheckBox();
        }
        newUserModal.submitModal();
    }
}
