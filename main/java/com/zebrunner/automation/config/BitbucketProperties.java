package com.zebrunner.automation.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.zebrunner.automation.gui.reporting.launch.SearchType;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BitbucketProperties {

    private final Boolean enabled;
    private final String url;
    private final String username;
    private final String accessToken;

}
