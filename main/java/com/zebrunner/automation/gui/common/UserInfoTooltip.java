package com.zebrunner.automation.gui.common;

import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.legacy.StringUtil;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;


public class UserInfoTooltip extends AbstractUIObject {
    @FindBy(xpath = "//div[@class='zbr-on-hover-user-card__full-name']")
    private Element memberFullName;

    @FindBy(xpath = "//div[@class='zbr-on-hover-user-card__username']")
    private Element memberUsername;

    @FindBy(xpath = "//div[@class='zbr-on-hover-user-card__email']/a")
    private Element memberEmail;

    @FindBy(xpath = "//div[@class='zbr-on-hover-user-card__role']")
    private Element memberRole;

    @FindBy(xpath = "//span[@class='zbr-on-hover-user-card__status']")
    private Element deactivated;

    public UserInfoTooltip(WebDriver driver) {
        super(driver);
        setBy(By.xpath(Tooltip.ROOT_LOCATOR));
    }

    public boolean isFullNamePresent() {
        return memberFullName.isStateMatches(Condition.PRESENT);
    }

    public boolean isUsernamePresent() {
        return memberUsername.isStateMatches(Condition.PRESENT);
    }

    public boolean isEmailPresent() {
        return memberEmail.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public boolean isRolePresent() {
        return memberRole.isStateMatches(Condition.PRESENT);
    }

    public String getFullName() {
        return memberFullName.getText();
    }

    public String getUsername() {
        return memberUsername.getText();
    }

    public String getEmail() {
        return memberEmail.getText();
    }

    public String getRole() {
        return memberRole.getText();
    }

    public void verifyUserInfoTooltip(SoftAssert softAssert, User user, boolean isActive, RoleEnum expectedRole, String placeVerification) {

        UserInfoTooltip userInfoTooltip = new UserInfoTooltip(getDriver());
        softAssert.assertTrue(userInfoTooltip.isUIObjectPresent(), "User tooltip is not present! " + placeVerification + " !");
        softAssert.assertEquals(userInfoTooltip.getFullName(), StringUtil.getExpectedAuthor(user), "User full name is not as expected! " + placeVerification + " !");
        softAssert.assertEquals(userInfoTooltip.getUsername(), user.getUsername(), "Username is not as expected! " + placeVerification + " !");
        softAssert.assertEquals(userInfoTooltip.getEmail(), user.getEmail(), "User email is not as expected! " + placeVerification + " !");
        softAssert.assertEquals(userInfoTooltip.getRole(), expectedRole.getName().toLowerCase(), "User role is not as expected! " + placeVerification + " !");
        if (!isActive) {
            softAssert.assertTrue(deactivated.isElementPresent(5), "User deactivated indicator is not present!" + placeVerification + " !");
            softAssert.assertEquals(deactivated.getText(), "(deactivated)", "Deactivated text is not as expected! " + placeVerification);
        } else {
            softAssert.assertFalse(deactivated.isElementPresent(5), "User deactivated indicator is present, when it should not be! " + placeVerification + " !");
        }
    }

    public void verifyUserInfoTooltip(User user, boolean isActive, RoleEnum expectedRole, String placeVerification) {
        SoftAssert softAssert = new SoftAssert();

        verifyUserInfoTooltip(softAssert, user, isActive, expectedRole, placeVerification);

        softAssert.assertAll();
    }
}