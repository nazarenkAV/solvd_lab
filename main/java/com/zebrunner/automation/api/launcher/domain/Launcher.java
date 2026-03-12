package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Launcher {

    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
    private Config config;

    public Launcher(String launcherName, Config config) {
        this.name = launcherName;
        this.config = config;
    }

}
