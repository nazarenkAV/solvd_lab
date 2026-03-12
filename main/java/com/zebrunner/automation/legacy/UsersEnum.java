package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.api.iam.domain.User;

@Getter
@Deprecated
@RequiredArgsConstructor
public enum UsersEnum {

    MAIN_ADMIN(
            User.builder()
                .username(ConfigHelper.getUserProperties().getAdmin().getUsername())
                .password(ConfigHelper.getUserProperties().getAdmin().getPassword())
                .build()
    );

    private final User user;

}
