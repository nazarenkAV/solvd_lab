package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preset {

    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
    private Config config;
    private List<Schedule> schedules = new ArrayList<>();

    public Preset(String presetName, Config config) {
        this.name = presetName;
        this.config = config;
    }
}
