package com.zebrunner.automation.api.launcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repo {
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String provider;
    private String name;
    private String htmlUrl;
    private List<RepoLauncher> launchers;

    public Repo(String provider, String name, String htmlUrl, List<RepoLauncher> repoLaunchers) {
        this.provider = provider;
        this.name = name;
        this.htmlUrl = htmlUrl;
        this.launchers = repoLaunchers;
    }
}
