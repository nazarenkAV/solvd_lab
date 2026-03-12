package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitRepository {

    private Long id;

}
