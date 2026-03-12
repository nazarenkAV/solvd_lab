package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum RepoTypeEnum {

    GITHUB("GITHUB", "https://github.com/"),
    GITLAB("GITLAB", "https://gitlab.com/"),
    BITBUCKET("BITBUCKET", "https://bitbucket.org/");

    private final String repoType;
    private final String url;

    public String getType() {
        return repoType;
    }

}
