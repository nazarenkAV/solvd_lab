package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

import com.zebrunner.automation.config.SlackProperties;
import com.zebrunner.automation.api.integration.domain.ToolConfig;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SlackConfig implements ToolConfig {

    private String botName;
    private String botIconUrl;
    private String token;
    private boolean tokenEncrypted;

    public static SlackConfig of(SlackProperties properties) {
        return new SlackConfig().setBotName(properties.getBotName())
                                .setToken(properties.getToken());
    }

}
