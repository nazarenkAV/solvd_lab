package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationOption {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;

    public static List<ConfigurationOption> generateRandomOptions(int numOptions) {
        List<ConfigurationOption> options = new ArrayList<>();

        for (int i = 0; i < numOptions; i++) {
            String name = "options ".concat(RandomStringUtils.randomAlphabetic(5));
            ConfigurationOption option = ConfigurationOption.builder()
                    .name(name)
                    .build();
            options.add(option);
        }

        return options;
    }

    public static ConfigurationOption generateRandom() {
        return ConfigurationOption.builder()
                .name("options ".concat(RandomStringUtils.randomAlphabetic(5)))
                .build();
    }

}