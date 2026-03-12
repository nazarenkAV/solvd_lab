package com.zebrunner.automation.api.launcher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWebhookResponse {
    private WebhookResults data;
    private Links _links;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class Links {
        private String htmlUrl;
    }
}
