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
public class TeamsWebhookConfig implements ToolConfig {

    private String channel;
    private String webhookUrl;

    public static TeamsWebhookConfig createValidConfig() {
        return TeamsWebhookConfig.builder()
                .channel("Automation")
                .webhookUrl("https://ddd.webhook.office.com")
                .build();
    }

}
