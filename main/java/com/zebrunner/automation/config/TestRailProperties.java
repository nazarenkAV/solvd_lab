package com.zebrunner.automation.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TestRailProperties {

    private final Boolean enabled;
    private final String url;
    private final String username;
    private final String apiKey;

}
