package com.zebrunner.automation.api.launcher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

import com.zebrunner.automation.api.tcm.domain.Suite;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LauncherWeb {

    private String launchName;
    private String runName;
    private Suite suite;
    private String launchCommand;
    private String osType;
    private String os;
    private String branch;
    private String browser;
    private String device;
    private String deviceVersion;
    private String dockerImage;
    private String browserVersion;

    private String executionEnvironment;
    private List<CustomVariable> envVariables;
    private List<CustomVariable> customVariables;

    @Override
    public LauncherWeb clone() throws CloneNotSupportedException {
        LauncherWeb launcher;
        try {
            launcher = (LauncherWeb) super.clone();
        } catch (CloneNotSupportedException e) {
            launcher = new LauncherWeb();
            launcher.setLaunchName(launchName);
            launcher.setSuite(suite);
            launcher.setRunName(runName);
            launcher.setExecutionEnvironment(executionEnvironment);
            launcher.setLaunchCommand(launchCommand);
            launcher.setOsType(osType);
            launcher.setOs(os);
            launcher.setBranch(branch);
            launcher.setBrowser(browser);
            launcher.setDevice(device);
            launcher.setDeviceVersion(deviceVersion);
            launcher.setDockerImage(dockerImage);
            launcher.setBrowserVersion(browserVersion);
            launcher.setEnvVariables(envVariables);
            launcher.setCustomVariables(customVariables);
        }
        return launcher;
    }

    public String getBrowserAndVersion() {
        return this.browser.concat(" ").concat(browserVersion);
    }

}
