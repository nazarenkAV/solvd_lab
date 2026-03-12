package com.zebrunner.automation.api.launcher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaunchArguments {

    private Long gitRepositoryId;
    private Long launcherId;
    private Long presetId;

    private String branch;
    private Long milestoneId;

    private String dockerImage;
    private String launchCommand;

    private String executor;
    private Long executorId;
    private List<Config.Parameter> envVars = new ArrayList<>();
    private String agentRunContext;

    private String testingPlatform;
    private Long testingPlatformId;
    private List<Config.Parameter> providerCapabilities = new ArrayList<>();
    private List<Config.Parameter> customCapabilities = new ArrayList<>();

    private Config.NotificationChannels notificationChannels = new Config.NotificationChannels();


}

