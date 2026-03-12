package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import groovy.util.logging.Log;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Log
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long version;
    private String branch;
    private String dockerImage;
    private String launchCommand;
    private String executor;
    private Long executorId;
    private List<Parameter> envVars = new ArrayList<>();
    private String testingPlatform;
    private Long testingPlatformId;
    private List<Parameter> providerCapabilities = new ArrayList<>();
    private List<Parameter> customCapabilities = new ArrayList<>();
    private NotificationChannels notificationChannels = new NotificationChannels();

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Parameter {

        private String name;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String defaultValue;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Type type = Type.STRING;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> values;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String value;

        public enum Type {
            STRING,
            BOOLEAN,
            INTEGER,
            CHOICE

        }

    }

    @Data
    @NoArgsConstructor
    public static class NotificationChannels {

        private String teamsChannels;
        private String slackChannels;
        private String emails;

    }

}


