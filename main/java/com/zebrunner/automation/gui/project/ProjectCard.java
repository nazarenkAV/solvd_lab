package com.zebrunner.automation.gui.project;

import com.zebrunner.automation.legacy.ColorEnum;
import com.zebrunner.automation.gui.common.Dropdown;
import com.zebrunner.automation.gui.common.Tooltip;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.reporting.TestRunsPageR;
import com.zebrunner.automation.gui.iam.MembersPageR;
import com.zebrunner.automation.gui.common.UserInfoTooltip;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class ProjectCard extends AbstractUIObject {
    public final static String CARD_ROOT_XPATH = "//tr[contains(@class,'MuiTableRow-root') and not(contains(@class,'MuiTableRow-head'))]";
    private final String KEY_INDEX = "[3]";
    private final String CATEGORY_INDEX = "[5]";
    private final String CREATED_INDEX = "[6]";
    private final String SETTINGS_INDEX = "[7]";

    @FindBy(xpath = ".//div[@class='add-photo__image _noImage']")
    private ExtendedWebElement defaultLogo;

    @FindBy(xpath = ".//*[contains(@class, 'project-star')]")
    private Element projectStar;

    @FindBy(xpath = ".//img")
    private ExtendedWebElement customLogo;

    @FindBy(xpath = ".//div[@class='project-logo-name']//a")
    private Element projectName;

    @FindBy(xpath = ".//td" + CATEGORY_INDEX)
    private Element category;

    @FindBy(xpath = ".//td" + KEY_INDEX)
    private Element key;

    @FindBy(xpath = ".//div[@class='project-lead']")
    private Element lead;

    @FindBy(xpath = ".//td" + CREATED_INDEX)
    private Element createdDate;

    @FindBy(xpath = ".//div[@class='projects-table__col _members']/a")
    private Element membersRef;

    @FindBy(xpath = ".//td" + SETTINGS_INDEX + "//button")
    private Element threeDots;

    @FindBy(xpath = Tooltip.ROOT_LOCATOR)
    private Tooltip tooltip;

    public ProjectCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getKey() {
        return key.getText().trim();
    }

    public String getProjectName() {
        return projectName.getText().trim();
    }

    public TestRunsPageR toTestRunsPage() {
        projectName.click();
        TestRunsPageR testRunsPage = TestRunsPageR.getPageInstance(getDriver());
        testRunsPage.closeOnboardingModalIfExists();
        return testRunsPage;
    }

    public ProcessProjectModal editCard() {
        threeDots.click();
        Dropdown dropdown = new Dropdown(getDriver());
        dropdown.findItem("Edit").click();

        return new ProcessProjectModal(getDriver());
    }

    public DeleteProjectModal clickDeleteCard() {
        threeDots.click();
        Dropdown dropdown = new Dropdown(getDriver());
        dropdown.findItem("Delete").click();
        return new DeleteProjectModal(getDriver());
    }

    public boolean isProjectPublic() {
        super.pause(2);
        return category.getText().trim().equalsIgnoreCase("Public");
    }

    public MembersPageR toMembersPageR() {
        threeDots.click();
        Dropdown dropdown = new Dropdown(getDriver());
        dropdown.findItem("Members").click();
        return MembersPageR.getPageInstance(getDriver());
    }

    @Deprecated(forRemoval = true)
    public boolean isCardEditable() {
        return false;
    }

    public String getLead() {
        pause(1);
        return lead.getText().trim();
    }

    public UserInfoTooltip hoverLead() {
        lead.hover();
        return new UserInfoTooltip(getDriver());
    }

    /**
     * Default logo selector differs from custom logo.
     *
     * @return <b>true</b> if logo is default, otherwise return <b>false</b>
     */
    public boolean isLogoDefault() {
        return defaultLogo.isPresent(3);
    }

    /**
     * @return Unique link to logo avatar.
     */
    public String getLogoLink() {
        return customLogo.getAttribute("src");
    }

    @SneakyThrows
    public Date getCreatedDate() {
        String pattern = "dd MMM, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(createdDate.getText());
    }

    public boolean isProjectStarPresent() {
        return projectStar.isElementPresent(3);
    }

    public void clickProjectStar() {
        projectStar.click();
    }

    public String getStarToolTip() {
        projectStar.hover();
        return tooltip.getTooltipText();
    }

    public boolean isProjectStarClickable() {
        return projectStar.isClickable(3);
    }

    public String getColorFromStar() {
        String color = projectStar.getElement().findElement(By.tagName("svg")).getCssValue("fill");
        return Color.fromString(color).asHex();
    }

    public boolean isProjectStarred() {
        return getColorFromStar().equals(ColorEnum.STARRED.getHexColor());
    }
}
