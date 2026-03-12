package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

import com.zebrunner.automation.config.GithubProperties;
import com.zebrunner.automation.api.integration.domain.ToolConfig;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GithubConfig implements ToolConfig {

    private String url;
    private String username;
    private String accessToken;
    private boolean accessTokenEncrypted;

    public static GithubConfig of(GithubProperties properties) {
        return new GithubConfig().setUrl(properties.getUrl())
                                 .setUsername(properties.getUsername())
                                 .setAccessToken(properties.getAccessToken());
    }

}
