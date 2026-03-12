package com.zebrunner.automation.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestLabels {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Name {

        public static final String GROUP = "group";
        public static final String GLOBAL_SETTINGS = "global_settings";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Value {

        public static final String LAUNCHES = "launches";
        public static final String INVITATIONS = "invitations";
        public static final String GLOBAL_SETTINGS = "global_settings";

    }

}
