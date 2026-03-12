package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationGroup {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private List<ConfigurationOption> options;


    public static ConfigurationGroup createRandom() {
        String name = "config_group ".concat(generateRandomName());
        return ConfigurationGroup.builder()
                .name(name)
                .build();
    }

    public static ConfigurationGroup createRandomWithOptions(List<ConfigurationOption> options) {
        String name = "config_group ".concat(generateRandomName());
        return ConfigurationGroup.builder()
                .name(name)
                .options(options)
                .build();
    }

    public static String generateRandomName() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public List<ConfigurationOption> getOptions() {
        if (options == null) {
            options = new ArrayList<>();//set default value
        }
        return options;
    }

}
