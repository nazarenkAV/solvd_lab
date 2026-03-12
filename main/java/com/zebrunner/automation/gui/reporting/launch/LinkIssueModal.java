package com.zebrunner.automation.gui.reporting.launch;

import com.zebrunner.automation.gui.common.Menu;
import com.zebrunner.automation.gui.Condition;
import com.zebrunner.automation.gui.Element;
import com.zebrunner.automation.gui.common.AbstractModal;
import com.zebrunner.automation.legacy.ColorUtil;
import com.zebrunner.automation.util.WaitUtil;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Getter
public class LinkIssueModal extends AbstractModal {

    public static final String MODAL_TITLE = "Link issue";

    private static final String SUGGESTION_XPATH = "//li[@role='option']";

    @FindBy(xpath = "//*[text() = 'Create a new issue']/ancestor::button")
    private Element createNewIssueButton;

    @FindBy(xpath = "//*[@class = 'external-systems-control']")
    private ExtendedWebElement bugTracker;

    @FindBy(xpath = ".//input[@placeholder='Search']")
    private Element idOrSummaryField;

    @FindBy(xpath = SUGGESTION_XPATH)
    private List<ExtendedWebElement> suggestions;

    @FindBy(xpath = ".//button[text()='Link']")
    private Element linkButton;

    @FindBy(xpath = ".//*[@class = 'selected-issue__info-id-number']")
    private ExtendedWebElement selectedLinkIssueId;

    @FindBy(xpath = ".//*[@class = 'selected-issue__info-text']")
    private ExtendedWebElement selectedLinkIssueTitle;

    @FindBy(xpath = LinkedIssueCard.ROOT_XPATH)
    private List<LinkedIssueCard> linkedIssueCards;

    @FindBy(xpath = Menu.ROOT_LOCATOR)
    private Menu menu;

    public LinkIssueModal(WebDriver driver) {
        super(driver);
    }

    public void linkIssue(String issue) {
        idOrSummaryField.waitUntil(Condition.VISIBLE);
        idOrSummaryField.sendKeys(issue);
        WaitUtil.waitComponentInListByCondition(suggestions, element -> element.getText().contains(issue))
                .ifPresent(ExtendedWebElement::click);
        linkButton.waitUntil(Condition.CLICKABLE).click();
        close.click();
    }

    public void typeAndSelectIdOrSummary(String idOrSummary) {
        idOrSummaryField.sendKeys(idOrSummary);
        WaitUtil.waitComponentInListByCondition(suggestions, element -> element.getText().contains(idOrSummary))
                .ifPresent(ExtendedWebElement::click);
    }

    public void typeIdOrSummary(String idOrSummary) {
        idOrSummaryField.sendKeys(idOrSummary);
    }

    public void selectSuggestionByIdOrSummary(String idOrSummary) {
        WaitUtil.waitComponentInListByCondition(suggestions, element -> element.getText().contains(idOrSummary))
                .ifPresent(ExtendedWebElement::click);
    }

    public boolean isCreateNewIssueButtonPresentAndClickable() {
        return createNewIssueButton.isStateMatches(Condition.PRESENT_AND_CLICKABLE);
    }

    public void clickCreateNewIssueButton() {
        createNewIssueButton.click();
    }

    public String getBugTrackerText() {
        return bugTracker.getText();
    }

    public String getColorOfLinkIssueButton() {
        String color = linkButton.getElement().getCssValue("background-color");
        return ColorUtil.getHexColorFromString(color);
    }

    public void clickLinkIssueButton() {
        linkButton.click();
    }

    public LinkedIssueCard findIssueCard(String searchTerm, SearchType searchType) {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), LinkedIssueCard.ROOT_XPATH);

        return linkedIssueCards.stream()
                               .filter(linkedIssueCard -> {
                                   if (searchType == SearchType.BY_ID) {
                                       return linkedIssueCard.getIssueTicketIdText().equals(searchTerm);
                                   } else if (searchType == SearchType.BY_TITLE) {
                                       return linkedIssueCard.getIssueTicketTitleText().equals(searchTerm);
                                   }
                                   return false;
                               })
                               .findFirst()
                               .orElseThrow(() -> {
                                   if (searchType == SearchType.BY_ID) {
                                       return new NoSuchElementException(String.format("linked issue card item with this '%s' id was not found!", searchTerm));
                                   } else if (searchType == SearchType.BY_TITLE) {
                                       return new NoSuchElementException(String.format("linked issue card item with this '%s' title was not found!", searchTerm));
                                   }
                                   return new IllegalArgumentException("Invalid search type provided");
                               });
    }

    public boolean isIssueCardIdPresentOnHistorySection(String id) {
        WaitUtil.waitNotEmptyListOfElements(getDriver(), LinkedIssueCard.ROOT_XPATH);
        return linkedIssueCards.stream()
                               .anyMatch(linkedIssueCard -> linkedIssueCard.getIssueTicketIdText().equals(id));
    }

    public String getSelectedLinkIssueIdText() {
        return selectedLinkIssueId.getText();
    }

    public String getSelectedLinkIssueTitleText() {
        return selectedLinkIssueTitle.getText();
    }

    public boolean isTicketPresentInSuggestionList(String idOrSummary) {
        boolean condition = false;
        WaitUtil.waitNotEmptyListOfElements(getDriver(), SUGGESTION_XPATH);
        for (ExtendedWebElement suggestion : suggestions) {
            if (suggestion.getText().contains(idOrSummary)) {
                condition = true;
                break;
            }
        }
        return condition;
    }

    private Menu openBugTrackerMenu() {
        bugTracker.click();
        return menu;
    }

    public LinkIssueModal openAndSelectBugTracker(Menu.MenuItemEnum menuItemEnum) {
        if (!getBugTrackerText().equalsIgnoreCase(menuItemEnum.getItemValue())) {
            log.info("Selecting bug tracker " + menuItemEnum.getItemValue());
            openBugTrackerMenu()
                    .findItem(menuItemEnum.getItemValue())
                    .click();
        }

        log.info("Bug tracker " + menuItemEnum.getItemValue() + " already selected!");
        return this;
    }
}