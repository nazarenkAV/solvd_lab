package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

import com.zebrunner.automation.config.JiraProperties;
import com.zebrunner.automation.api.integration.domain.ToolConfig;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraConfig implements ToolConfig {

    private Type type;
    private String url;
    private String username;
    private String token;
    private boolean tokenEncrypted;

    public enum Type {

        SERVER_DC,
        CLOUD

    }

    public static JiraConfig of(JiraProperties properties) {
        return new JiraConfig().setUrl(properties.getUrl())
                               .setUsername(properties.getUsername())
                               .setToken(properties.getToken())
                               .setType(Type.CLOUD);
    }

}
