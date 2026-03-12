package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Environment {
    private Long id;
    private String key;
    private String name;
    private String description;

    public static Environment createRandom() {
        return Environment.builder()
                .key("key " + RandomStringUtils.randomAlphabetic(5))
                .name(RandomStringUtils.randomAlphabetic(5))
                .build();
    }
}
