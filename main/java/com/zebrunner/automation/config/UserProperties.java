package com.zebrunner.automation.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserProperties {

    private final Admin admin;
    private final TestUser testUser;

    @Data
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Admin {

        private final String username;
        private final String password;

    }

    @Data
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class TestUser {

        private final String username;
        private final String password;

    }

}
