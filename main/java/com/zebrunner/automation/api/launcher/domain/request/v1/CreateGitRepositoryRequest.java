package com.zebrunner.automation.api.launcher.domain.request.v1;

import lombok.Data;
import lombok.experimental.Accessors;

import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;

@Data
@Accessors(chain = true)
public class CreateGitRepositoryRequest {

    public static final String USERNAME_FIELD = "username";
    public static final String ACCESS_TOKEN_FIELD = "accessToken";

    private String provider;
    private String url;
    private String username;
    private String accessToken;

    public static CreateGitRepositoryRequest ofGithub(String url, String username, String accessToken) {
        return new CreateGitRepositoryRequest().setProvider(GitProvider.GITHUB.toString())
                                               .setUrl(url)
                                               .setUsername(username)
                                               .setAccessToken(accessToken);
    }

}
