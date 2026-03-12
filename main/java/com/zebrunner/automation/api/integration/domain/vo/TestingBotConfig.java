package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.automation.api.integration.domain.ToolConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestingBotConfig implements ToolConfig {

    private String hubUrl;
    private String key;
    private String secret;
    private boolean secretEncrypted;

    public static TestingBotConfig createValidConfig() {
        return TestingBotConfig.builder()
                .hubUrl("http://hub.testingbot.com/wd/hub")
                .secret("secret")
                .key("key")
                .build();
    }
}

