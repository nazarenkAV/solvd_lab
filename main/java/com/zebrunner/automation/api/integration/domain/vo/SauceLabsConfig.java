package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

import com.zebrunner.automation.config.SauceLabsProperties;
import com.zebrunner.automation.api.integration.domain.ToolConfig;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SauceLabsConfig implements ToolConfig {

    private String hubUrl;
    private String username;
    private String accessKey;
    private boolean accessKeyEncrypted;

    public static SauceLabsConfig of(SauceLabsProperties properties) {
        return new SauceLabsConfig().setHubUrl(properties.getHubUrl())
                                    .setUsername(properties.getUsername())
                                    .setAccessKey(properties.getAccessKey());
    }

}
