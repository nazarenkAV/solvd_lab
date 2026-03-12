package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoLauncher {
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
    private List<RepoPreset> presets;

    public RepoLauncher(String name, List<RepoPreset> repoPresets) {
        this.name = name;
        this.presets = repoPresets;
    }
}
