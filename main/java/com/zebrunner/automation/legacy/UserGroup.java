package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum UserGroup {

    SUPERADMIN("SuperAdmin"), ADMINS("Admins"), USERS("Users");

    private final String name;

}
