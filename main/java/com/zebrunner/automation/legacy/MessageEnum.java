package com.zebrunner.automation.legacy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Deprecated
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MessageEnum {

    PROJECT_NOT_FOUND("Project with key \"%s\" not found"),
    PROJECT_DELETE("Project '%s' was successfully deleted"),
    SUCCESSFUL_PROJECT_CHANGE("%s was successfully changed"),
    PROJECT_WAS_CREATED("Project “%s” was successfully created"),
    WIDGET_POSITIONS_WERE_UPDATED("Widget positions were updated"),
    WIDGET_DELETED("Widget was deleted"),
    WIDGET_UPDATED("Widget was updated"),
    WIDGET_CREATED("Widget was created"),
    WIDGET_HAS_BEEN_SENT("Widget has been sent"),
    USER_CREATED("User created"),
    USER_ALREADY_EXISTS_("User with the provided username or email already exists."),
    INVITATION_WAS_SENT("Invitation was sent"),
    FILTER_WAS_SAVED("\"%s\" filter was successfully saved"),
    FILTER_ALREADY_EXISTS("Filter with given name already exists"),
    MILESTONE_CREATED("Milestone \"%s\" was successfully created"),
    MILESTONE_DELETED("Milestone \"%s\" was successfully deleted"),
    MILESTONE_UPDATED("Milestone \"%s\" was successfully updated"),
    MILESTONE_ERROR_MESSAGE_IN_TITLE_NAME_ALREADY_IN_USE("Name already in use"),
    MILESTONE_ERROR_MESSAGE_INVALID_CHARACTERS_IN_TITLE("Can only contain letters, digits, dashes, underscores, dots and whitespaces"),
    DASHBOARD_CREATED("Dashboard was successfully created"),
    DASHBOARD_DELETED("Dashboard has been deleted"),
    DASHBOARD_UPDATED("Dashboard has been updated"),
    EMAIL_WAS_SUCCESSFULLY_SENT("Dashboard has been sent"),
    ISSUE_LINKED("Issue was linked successfully"),
    ISSUE_UNLINKED("Issue was unlinked successfully"),
    TITLE_WAS_COPIED("The title was copied to the clipboard"),
    LOG_LINK_WAS_COPIED("Log link was copied to the clipboard"),
    PERMALINK_WAS_COPIED("Permalink was copied to the clipboard"),
    TEST_WAS_MARKED_AS_PASSED("Test was marked as PASSED"),
    TEST_WAS_MARKED_AS_FAILED("Test was marked as FAILED"),
    FAILURE_TAG_WAS_SUCCESSFULLY_ASSIGNED("Failure tag was successfully assigned"),
    WE_NOT_RECOGNIZE_CREDS("We do not recognize that username or password.\n"
            + "Please check your spelling and try again or use another login option."),
    USER_WAS_DEACTIVATED("We were not able to log you in: your account was deactivated by workspace administrator and access to the system is suspended."),
    INVALID_USER_CREDENTIALS("Invalid user credentials."),
    SUPPLIED_PAYLOAD_FAILS("Supplied payload fails to meet validation constraints"),
    PROVIDED_VALUE_IS_NOT_VALID("Provided value is not valid."),
    THIS_FIELD_IS_REQUIRED("This field is required"),
    GIT_REPO_WAS_NOT_FOUND("The git repo was not found on the provider side. Probably, you don't have direct access to the repo"),
    GITHUB_INVALID_CREDS("The provided Github credentials are invalid"),
    NO_RESULT_MESSAGE("No results were found matching your search"),
    NO_REPO_MESSAGE("No launchers. Add repo to start launch."),
    INVALID_CARD_NUMBER("This card number is not valid."),
    INVALID_EXPIRATION_DATE("This expiration date is not valid."),
    INVALID_CVV("This security code is not valid."),
    PAYMENT_METHOD_WAS_SUCCESSFULLY_ADDED("Your payment method has been successfully added"),
    PAYMENT_METHOD_WAS_CHANGED("Your payment method was successfully changed"),
    SOMETHING_WRONG_WITH_ADDING_PAYMENT_METHOD("Something went wrong, try again later"),
    I_AGREE_WITH_PRIVACY_POLICY("I agree with Terms of Service & Privacy Policy"),
    NAME_DUPLICATE_TITLE_FORBIDDEN("Duplicate title forbidden."),
    ONLY_LETTERS_AND_DIGITS_WARNING_MESSAGE("Only letters, numbers, ., -, !, ?, &, and brackets are allowed"),
    KEY_EDIT_WARNING("Please note! After changing the Project Key all current project links will be lost."),
    KEY_EDIT_ERROR("Only latin letters and numbers. Must contain at least one letter"),
    KEY_SYMBOLS_WARNING("3-6 characters required"),
    DUPLICATE_KEY_WARNING("Duplicate key forbidden."),
    PROJECT_WITH_GIVEN_KEY_EXISTS("Project with given key already exists"),
    PROJECT_WITH_GIVEN_NAME_EXISTS("Project with given name already exists"),
    PROJECT_WITH_SUCH_NAME_EXISTS("Project with such name already exists"),
    PROJECT_WITH_SUCH_KEY_EXISTS("Project with such key already exists"),
    PROJECT_MEMBER_WAS_SUCCESSFULLY_UPDATED("Project member was successfully updated"),
    PROJECT_MEMBER_WAS_SUCCESSFULLY_DELETED("Project member was successfully deleted"),
    THERE_IS_NOT_VALID_PROJECT_ASSIGNMENTS_TO_SAVE("There is no valid Project Assignments to save"),
    POPUP_WRONG_FILE_SIZE_OR_EXTENSION("Maximum image size 5mb"),
    AUT_LAUNCHES_EMPTY_PAGE_PLACEHOLDER_TEXT("There are no reported launches yet"),
    LAUNCHES_HAVE_BEEN_SUCCESSFULLY_DELETED("Launches have been successfully deleted"),
    LAUNCH_HAS_BEEN_DELETED("Launch has been deleted"),
    LAUNCH_IS_ABORTED("Launch is aborted"),
    LAUNCH_IS_QUEUED("Launch is queued"),
    LAUNCH_HAS_BEEN_MARKED_AS_REVIEWED("Launch has been marked as reviewed"),
    LAUNCH_HAS_BEEN_ASSIGNED_TO_MILESTONE("Launch has been assigned to milestone"),
    LAUNCH_HAS_BEEN_UNASSIGNED_TO_MILESTONE("Launch has been unassigned from milestone"),
    LAUNCHES_HAVE_BEEN_ASSIGNED_TO_MILESTONE("Launches have been assigned to milestone"),
    LAUNCHES_HAVE_BEEN_UNASSIGNED_FROM_MILESTONE("Launches have been unassigned from milestone"),
    MAKE_SURE_INPUT_CONTAINS_VALID_EMAIL_ADDRESSES_ONLY("Please make sure that input contains valid email addresses only"),
    LINK_COPIED_TO_CLIPBOARD("Link copied to clipboard"),
    LAUNCH_REPORTS_WERE_SUCCESSFULLY_SENT("Launch reports were successfully sent"),
    CREATE_PRESET_MODAL_ALERT_TEXT(
            "Since presets are immutable, milestone will not be set automatically for launches that were triggered via preset. It is still possible to link those launches manually if needed."),
    REPOSITORY_WAS_SUCCESSFULLY_UPDATED("Repository was successfully updated"),
    // -------------------- Presets -------------------//
    DELETE_PRESET_POPUP("Preset was successfully deleted"),
    CREATE_PRESET_POPUP("Preset was successfully created"),
    UPDATE_PRESET_POPUP("Preset was successfully updated"),
    CREATE_PRESET_WITH_SCHEDULES_POPUP("Preset was successfully created with schedules"),
    ENTER_VALID_EXPRESSION("Please enter valid expression"),
    // -------------------- Schedules -------------------//
    SCHEDULES_WAS_SUCCESSFULLY_PAUSED("Schedules was successfully paused"),
    SCHEDULES_WAS_SUCCESSFULLY_RESUMED("Schedules was successfully resumed"),
    SCHEDULE_WAS_SUCCESSFULLY_PAUSED("Schedule was successfully paused"),
    SCHEDULE_WAS_SUCCESSFULLY_RESUMED("Schedule was successfully resumed"),
    INVITATION_WAS_SENT_SUCCESSFULLY("Invitation was sent successfully"),
    INVITATION_WAS_TAKEN_OFF_SUCCESSFULLY("Invitation was taken off successfully"),
    // -------------------- Webhooks -------------------//
    WEBHOOK_CREATED("Webhook was successfully created"),
    WEBHOOK_DELETED("Webhook was successfully deleted"),
    WEBHOOK_UPDATED("Webhook was successfully updated"),
    // -------------------- Test runs -------------------//
    TEST_RUN_CREATED("Test run was successfully created"),
    TEST_RUN_CLOSED("Test run has been successfully closed"),
    TEST_RUN_UPDATED("Test run was successfully updated"),
    TEST_RUN_DELETED("Test run has been successfully deleted"),
    TEST_RUN_ID_COPIED("Test run ID was copied to clipboard"),
    // -------------------- Test cases -------------------//
    TEST_CASE_EDITED("Case \"%s\" was successfully edited"),
    TEST_CASE_DELETED("Test case was deleted"),
    TEST_CASE_SUCCESSFULLY_DELETED("Case \"%s\" was successfully deleted"),
    TEST_CASES_WERE_RESTORED("Test cases were restored"),
    TEST_CASES_WERE_DELETED("Test cases were deleted"),
    TEST_CASE_WAS_RESTORED("Test case was restored"),
    // -------------------- Repo ---------------------//
    REPO_IS_CONNECTED("Repo is connected."),
    REPO_IS_UNABLE_TO_CONNECT("Unable to connect: please check credentials and access permissions."),
    // -------------------- GitLab --------------------//
    GITLAB_RESOURCE_WAS_NOT_FOUND("Resource was not found in GitLab. Error message: '404 Project Not Found'"),
    GITLAB_REJECTED_ACTION("GitLab rejected the action due to unauthenticated access. Error message: '401 Unauthorized'"),
    // -------------------- BitBucket ---------------//
    BITBUCKET_RESOURCE_WAS_NOT_FOUND("Resource was not found in Bitbucket. Error message: 'You may not have access to this repository or it no longer exists in this workspace. If you think this repository exists and you have access, make sure you are authenticated.'"),
    BITBUCKET_REJECTED_ACTION("Bitbucket rejected the action due to unauthenticated access. Error message: ''"),
    REQUEST_BODY_FAILS_VALIDATION_CONSTRAINTS("Supplied request body fails to meet validation constraints"),
    HOMEPAGE_WAS_SUCCESSFULLY_UPDATED("Homepage was successfully updated"),
    // ----------------------- Users ----------------//
    USER_SUCCESSFULLY_CREATED("User %s was successfully created"),
    USER_SUCCESSFULLY_UPDATED("User %s was successfully updated"),
    USER_SUCCESSFULLY_DEACTIVATED("User '%s' was deactivated"),
    USER_PASSWORD_WAS_UPDATED("Password of user '%s' was successfully updated"),
    USER_ALREADY_EXISTS("User with the provided username or email already exists"),
    USER_IS_NOT_ACTIVE("User is not active.");

    private final String description;

    public String getDescription(String... value) {
        return String.format(description, value);
    }

}
