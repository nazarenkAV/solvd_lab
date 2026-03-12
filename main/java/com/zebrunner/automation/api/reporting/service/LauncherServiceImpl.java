package com.zebrunner.automation.api.reporting.service;

import lombok.SneakyThrows;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

import com.zebrunner.automation.api.launcher.domain.Config;
import com.zebrunner.automation.api.launcher.domain.GitRepository;
import com.zebrunner.automation.api.launcher.domain.LaunchArguments;
import com.zebrunner.automation.api.launcher.domain.Launcher;
import com.zebrunner.automation.api.launcher.domain.Preset;
import com.zebrunner.automation.api.launcher.domain.request.v1.CreateGitRepositoryRequest;
import com.zebrunner.automation.api.launcher.method.v1.CreateGitRepository;
import com.zebrunner.automation.api.launcher.method.v1.DeleteGithubRepoByIdMethod;
import com.zebrunner.automation.api.launcher.method.v1.DeleteLauncherMethod;
import com.zebrunner.automation.api.launcher.method.v1.PostCreateLauncherMethod;
import com.zebrunner.automation.api.launcher.method.v1.PostLaunchLauncherMethod;
import com.zebrunner.automation.api.launcher.method.v1.PostPresetMethod;
import com.zebrunner.automation.util.AuthenticationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class LauncherServiceImpl implements LauncherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final File configFile = new File("src/test/resources/api/launcher_service/config.json");

    //    --------------------------------  Git repo --------------------------------------

    @Override
    public void deleteGitRepoById(Long projectId, Long repoId) {
        DeleteGithubRepoByIdMethod deleteGithubRepoByIdMethod = new DeleteGithubRepoByIdMethod(projectId, repoId);
        deleteGithubRepoByIdMethod.callAPI();
        LOGGER.info("Git repo with id {} was deleted!", repoId);
    }

    @Override
    public Long addGitRepo(Long projectId, String url, String username, String accessToken, String provider) {
        String authToken = AuthenticationContext.getTenantAdminAuthToken();
        CreateGitRepositoryRequest request = new CreateGitRepositoryRequest().setUrl(url)
                                                                             .setProvider(provider)
                                                                             .setUsername(username)
                                                                             .setAccessToken(accessToken);
        GitRepository gitRepository = CreateGitRepository.invoke(projectId, authToken, request);

        return gitRepository.getId();
    }

    //    --------------------------------  Launcher --------------------------------------

    @Override
    public Launcher addLauncher(Long projectId, Long repoId, String launcherName, Launcher launcher) {
        PostCreateLauncherMethod postCreateLauncherMethod = new PostCreateLauncherMethod(projectId, repoId, launcherName, launcher);
        postCreateLauncherMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = postCreateLauncherMethod.callAPI().asString();
        return JsonPath.from(rs).getObject("data", Launcher.class);
    }

    @Override
    public Launcher addLauncher(Long projectId, Long repoId, Launcher launcher) {
        String launcherName = RandomStringUtils.randomAlphabetic(10);
        return addLauncher(projectId, repoId, launcherName, launcher);
    }

    @Override
    public Launcher createDefaultLauncherObject() {
        String launcherName = "Launcher ".concat(RandomStringUtils.randomAlphabetic(10));
        Config config = JsonPath.from(configFile).getObject("", Config.class);
        return new Launcher(launcherName, config);
    }

    @Override
    public void deleteLauncher(Long projectId, Long repoId, Long launcherId) {
        DeleteLauncherMethod deleteLauncherMethod = new DeleteLauncherMethod(projectId, repoId, launcherId);
        deleteLauncherMethod.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        deleteLauncherMethod.callAPI();
    }
    //    --------------------------------  Presets --------------------------------------

    @Override
    public Preset addPreset(Long projectId, Long repoId, Long launcherId, Preset preset) {
        PostPresetMethod postPresetMethod = new PostPresetMethod(projectId, repoId, launcherId, preset);
        String rs = postPresetMethod.callAPI().asString();
        Preset createdPreset = JsonPath.from(rs).getObject("data", Preset.class);
        return createdPreset;
    }

    //    --------------------------------  Launch --------------------------------------
    @SneakyThrows
    @Override
    public LaunchArguments mapLauncherToLaunchArguments(Launcher launcher, Long repoId, Long launcherId) {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LaunchArguments launchArguments = mapper.readValue(mapper.writeValueAsString(launcher.getConfig()), LaunchArguments.class);

        launchArguments.setGitRepositoryId(repoId);
        launchArguments.setLauncherId(launcherId);
        launchArguments.setLaunchCommand(launcher.getConfig().getLaunchCommand());

        Config.Parameter runName = new Config.Parameter();
        runName.setName("REPORTING_RUN_DISPLAY_NAME");
        runName.setDefaultValue(launcher.getName());

        List<Config.Parameter> parameters = launcher.getConfig().getProviderCapabilities().stream().map(parameter -> {
            Config.Parameter remap = new Config.Parameter();
            remap.setName(parameter.getName());
            remap.setValue(parameter.getDefaultValue());
            return remap;
        }).collect(Collectors.toList());
        launchArguments.setProviderCapabilities(parameters);

        launchArguments.getEnvVars().add(runName);
        launchArguments.getEnvVars().forEach(envVar -> envVar.setValue(envVar.getDefaultValue()));
        return launchArguments;
    }

    public void launchLauncher(Long projectId, Long repoId, Launcher launcher) {
        LaunchArguments launchArguments = mapLauncherToLaunchArguments(launcher, repoId, launcher.getId());
        PostLaunchLauncherMethod postLaunchLauncherMethod = new PostLaunchLauncherMethod(projectId, launchArguments);
        postLaunchLauncherMethod.getRequest().expect().statusCode(HttpStatus.SC_OK);
        postLaunchLauncherMethod.callAPI();
    }

    public Launcher addDefaultUiTestsLauncher(Long projectId, Long repoId, String launcherName, String branch, String suite) {
        Launcher webLauncherInstance = this.createDefaultLauncherObject();
        webLauncherInstance.setName(launcherName);
        webLauncherInstance.getConfig().setBranch(branch);
        webLauncherInstance.getConfig()
                           .setLaunchCommand(String.format("mvn clean test --no-transfer-progress -Dsuite=%s", suite));

        Config.Parameter platform = new Config.Parameter();
        platform.setName("platformName");
        platform.setDefaultValue("linux");
        Config.Parameter browserName = new Config.Parameter();
        browserName.setName("browserName");
        browserName.setDefaultValue("chrome");
        Config.Parameter browserVersion = new Config.Parameter();
        browserVersion.setName("browserVersion");
        browserVersion.setDefaultValue("latest");

        webLauncherInstance.getConfig().setProviderCapabilities(List.of(platform, browserName, browserVersion));

        return this.addLauncher(projectId, repoId, webLauncherInstance);
    }

    public Launcher addDefaultApiTestsLauncher(Long projectId, Long repoId, String launcherName, String suite) {
        Launcher apiLauncherInstance = this.createDefaultLauncherObject();
        apiLauncherInstance.setName(launcherName);
        apiLauncherInstance.getConfig().setLaunchCommand(String.format("mvn clean test -Dsuite=%s", suite));

        return this.addLauncher(projectId, repoId, apiLauncherInstance);
    }

}
