package com.zebrunner.automation.config;


import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SlackProperties {

    private final Boolean enabled;
    private final String botName;
    private final String token;

}
