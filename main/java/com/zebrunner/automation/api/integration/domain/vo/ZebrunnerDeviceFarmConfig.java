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
public class ZebrunnerDeviceFarmConfig implements ToolConfig {

    private String hubUrl;
    private String username;
    private String accessKey;
    private boolean accessKeyEncrypted;

    public static ZebrunnerDeviceFarmConfig createValidConfig() {
        return ZebrunnerDeviceFarmConfig.builder()
                .hubUrl("https://demo.zebrunner.farm/mcloud/wd/hub")
                .username("my_user")
                .accessKey("my_key")
                .build();
    }
}
