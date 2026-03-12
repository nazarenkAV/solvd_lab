package com.zebrunner.automation.api.reporting.service;

import com.zebrunner.automation.api.launcher.domain.LaunchArguments;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.launcher.domain.Preset;
import lombok.SneakyThrows;

@Deprecated
public interface LauncherService {
    void deleteGitRepoById(Long projectId, Long repoId);

    Long addGitRepo(Long projectId, String url, String username, String accessToken, String provider);

    Launcher addLauncher(Long projectId, Long repoId, String launcherName, Launcher launcher);

    Launcher addLauncher(Long projectId, Long repoId, Launcher launcher);

    Launcher createDefaultLauncherObject();

    void deleteLauncher(Long projectId, Long repoId, Long launcherId);

    Preset addPreset(Long projectId, Long repoId, Long launcherId, Preset preset);

    //    --------------------------------  Launch --------------------------------------
    @SneakyThrows
    LaunchArguments mapLauncherToLaunchArguments(Launcher launcher, Long repoId, Long launcherId);

}
