package com.zebrunner.automation.api.integration.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.automation.api.integration.domain.ToolConfig;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZebrunnerEngineConfig implements ToolConfig {

    private String hubUrl;
    private String username;
    private String accessKey;
    private boolean accessKeyEncrypted;

}
