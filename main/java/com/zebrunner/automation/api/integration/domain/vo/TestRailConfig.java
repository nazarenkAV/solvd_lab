package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

import com.zebrunner.automation.config.TestRailProperties;
import com.zebrunner.automation.api.integration.domain.ToolConfig;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestRailConfig implements ToolConfig {

    private String url;
    private String username;
    private String apiKey;
    private boolean apiKeyEncrypted;

    public static TestRailConfig of(TestRailProperties properties) {
        return new TestRailConfig().setUrl(properties.getUrl())
                                   .setUsername(properties.getUsername())
                                   .setApiKey(properties.getApiKey());
    }

}
