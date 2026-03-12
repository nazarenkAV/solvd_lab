package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoPreset {
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
    private Boolean hasSchedules;

    public RepoPreset(String name, Boolean hasSchedules) {
        this.name = name;
        this.hasSchedules = hasSchedules;
    }
}
