package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum TooltipEnum {

    // ------------------------ Stacktrace --------------------------- //
    TOOLTIP_COPY_STACK_TRACE_BUTTON_HOVER("Copy stacktrace"),

    // ------------------------ Webhook ---------------------------- //
    TOOLTIP_COPY_NAME_BUTTON_NOT_CLICKED("Copy name"),
    TOOLTIP_COPY_URL_BUTTON_NOT_CLICKED("Copy URL"),
    TOOLTIP_EDIT_BUTTON("Edit webhook"),
    TOOLTIP_DELETE_BUTTON("Delete webhook"),

    // -------------------- Common ---------------------------- //
    TOOLTIP_COPY_CLICKED("Copied!"),

    // ------------------- Starred --------------------------- //

    TOOLTIP_ADD_TO_STARRED("Add to Starred"),
    TOOLTIP_REMOVE_FROM_STARRED("Remove from Starred"),

    // ------------------ Shared Steps ------------------------ //
    TOOLTIP_EDIT("Edit"),
    TOOLTIP_DELETE("Delete");

    private final String toolTipMessage;

}