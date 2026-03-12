package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum ColorEnum {

    WAITING_TO_START("#777777"),
    FAILED("#eb4454"),
    PASSED("#44c480"),
    ABORTED("#aeb8be"),
    IN_PROGRESS("#4eade9"),
    FAILED_TEST_CARD("#df4150"),
    PASSED_TEST_CARD("#44c480"),

    // ------- Filters ------- //
    FAVOURITE_FILTER_STAR("#fea521"),
    USUAL_FILTER_STAR("#6c757c"),

    // ------- Webhooks ------- //
    HOVER_ON_WEBHOOK_BUTTON("#dfe3e5"),
    HOVER_ON_CREATE_BUTTON("#26a69a"),

    // ------- Link issue modal ------- //
    ACTIVE_LINK_ISSUE_BUTTON("#9bcdc9"),
    DISABLED_LINK_ISSUE_BUTTON("#dfe3e5"),

    // ------- Link issue card ------- //
    ISSUE_LINK_BUTTON("#6c757c"),
    ISSUE_UNLINK_BUTTON("#26a69a"),
    ISSUE_LINK_BUTTON_HOVER("#000000"),

    // ------ Failure tag  ----- //
    TAGGED("#e3eeff"),

    // ------ Starred ------ //
    STARRED("#fea521"),
    NO_STARRED("#dfe3e5"),

    // ------- Common ------- //
    HOVER_ON_GREY("#f5f5f5");

    private final String hexColor;

}


